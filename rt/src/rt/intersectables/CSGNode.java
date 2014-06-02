package rt.intersectables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import rt.Ray;

/**
 * A CSG node combines two CSG solids using a set operation, such as
 * intersection, addition, or subtraction.
 */
public class CSGNode extends CSGSolid {

	public enum OperationType {
		INTERSECT, ADD, SUBTRACT
	};

	protected CSGSolid left, right;
	protected OperationType operation;

	public CSGNode(CSGSolid left, CSGSolid right, OperationType operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	/**
	 * Get boundaries of intersection intervals. The main idea is to first get
	 * the boundaries of the two CSG solids to be combined. Then, the boundaries
	 * are merged according to the set operation specified by the node.
	 */
	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {

		// Get interval boundaries of left and right children
		ArrayList<IntervalBoundary> leftIntervals = left.getIntervalBoundaries(r);
		ArrayList<IntervalBoundary> rightIntervals = right.getIntervalBoundaries(r);

		ArrayList<IntervalBoundary> combined = new ArrayList<IntervalBoundary>(leftIntervals.size() + rightIntervals.size());

		// Tag interval boundaries with left or right node and combine into one list
		tagAndCombine(combined, leftIntervals, BelongsTo.LEFT);
		tagAndCombine(combined, rightIntervals, BelongsTo.RIGHT);

		// sort
		Collections.sort(combined);

		// Traverse interval boundaries and set inside/outside
		// according to Boolean set operation to combine the two child solids
		boolean inLeft, inRight;
		inLeft = false;
		inRight = false;
		for (IntervalBoundary b: combined) {
			//decide if we're in left or right part 
			switch (b.belongsTo) {
				case LEFT:
					inLeft = b.type == BoundaryType.START;
					break;
				case RIGHT:
					inRight = b.type == BoundaryType.START;
					break; 
			}

			// apply operation
			switch (operation) {
				case INTERSECT: 
					if (inLeft && inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;
					break;
				case SUBTRACT: 
					if (inLeft && !inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;

					// In a subtract operation, the subtracted solid is turned
					// inside out,
					// or it "switches sign", so we need to flip its normal
					// direction
					if (b.belongsTo == BelongsTo.RIGHT && b.hitRecord != null) {
						b.hitRecord.normal.negate();
					}
					break;
				case ADD: 
					if (inLeft || inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;
					break;
			}
		}

		// Clean up
		Iterator<IntervalBoundary> it = combined.iterator();
		IntervalBoundary prev = new IntervalBoundary();
		prev.type = BoundaryType.END;
		while (it.hasNext()) {
			IntervalBoundary b = it.next();
			if (b.type == prev.type)
				it.remove();
			prev.type = b.type;
		}

		return combined;
	}

	private void tagAndCombine(Collection<IntervalBoundary> combined,
			Collection<IntervalBoundary> intervals, BelongsTo tag) {
		for (IntervalBoundary b: intervals) {
			b.belongsTo = tag;
			combined.add(b);
		}
	}

}
