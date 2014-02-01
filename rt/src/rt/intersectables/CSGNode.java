package rt.intersectables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import rt.Ray;

/**
 * A CSG node combines two CSG solids using a set operation, such as intersection,
 * addition, or subtraction.
 */
public class CSGNode extends CSGSolid {
	
	public enum OperationType { INTERSECT, ADD, SUBTRACT };
	
	protected CSGSolid left, right;
	protected OperationType operation;
	
	public CSGNode(CSGSolid left, CSGSolid right, OperationType operation)
	{
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	/**
	 * Get boundaries of intersection intervals. The main idea is to first get the boundaries
	 * of the two CSG solids to be combined. Then, the boundaries are merged according
	 * to the set operation specified by the node.
	 */
	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		ArrayList<IntervalBoundary> combined = new ArrayList<IntervalBoundary>();
		
		// Get interval boundaries of left and right children
		ArrayList<IntervalBoundary> leftIntervals = left.getIntervalBoundaries(r);
		ArrayList<IntervalBoundary> rightIntervals = right.getIntervalBoundaries(r);
		
		// Tag interval boundaries with left or right node
		Iterator<IntervalBoundary> it = leftIntervals.iterator();
		while(it.hasNext())
		{
			IntervalBoundary b = it.next();
			// In case an interval end needs to be shaded, we need the 
			// negated normal and incident direction (to shade the "inside" surface)!
			if(b.type == BoundaryType.END && b.hitRecord!=null) 
			{
				b.hitRecord.normal.negate();
				b.hitRecord.w.negate();
			}
			b.belongsTo = BelongsTo.LEFT;
		}
		it = rightIntervals.iterator();
		while(it.hasNext())
		{
			IntervalBoundary b = it.next();
			// In case an interval end needs to be shaded, we need the 
			// negated normal and incident direction (to shade the "inside" surface)!
			if(b.type == BoundaryType.END && b.hitRecord!=null)
			{
				b.hitRecord.normal.negate();
				b.hitRecord.w.negate();
			}
			b.belongsTo = BelongsTo.RIGHT;			
		}

		// Combine interval boundaries and sort
		combined.addAll(leftIntervals);
		combined.addAll(rightIntervals);
		Collections.sort(combined);

		// Traverse interval boundaries and set inside/outside 
		// according to Boolean set operation to combine the two child solids
		boolean inLeft, inRight;
		inLeft = false;
		inRight = false;
		
		it = combined.iterator();
		while(it.hasNext())
		{
			IntervalBoundary b = it.next();
			
			if(b.belongsTo == BelongsTo.LEFT)
			{
				if(b.type == BoundaryType.START)
					inLeft = true;
				else
					inLeft = false;
			}
			if(b.belongsTo == BelongsTo.RIGHT)
			{
				if(b.type == BoundaryType.START)
					inRight= true;
				else
					inRight = false;
			}

			switch(operation) 
			{
				case INTERSECT:
				{		
					if(inLeft && inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;
					break;
				}
				case SUBTRACT:
				{
					if(inLeft && !inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;
					break;
				}
				case ADD:
				{
					if(inLeft || inRight)
						b.type = BoundaryType.START; 
					else
						b.type = BoundaryType.END;
					break;
				}
			}
		}
		
		// Clean up
		it = combined.iterator();		
		IntervalBoundary prev = new IntervalBoundary();
		prev.type = BoundaryType.END;				
		IntervalBoundary b;	
		while(it.hasNext())
		{
			b = it.next();
			if(b.type == prev.type)
				it.remove();
			prev.type = b.type;						
		}

		return combined;
	}

}
