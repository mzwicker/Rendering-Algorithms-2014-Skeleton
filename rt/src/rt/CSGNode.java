package rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class CSGNode extends CSGSolid implements Intersectable {
	
	public CSGNode(CSGSolid left, CSGSolid right, OperationType operation)
	{
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
	
	public HitRecord intersect(Ray r) {

		ArrayList<IntervalBoundary> intervals = computeIntervals(r);
		
		if(intervals.size() > 0)
		{
			HitRecord firstHit = intervals.get(0).hitRecord;
		
			if(firstHit!=null &&firstHit.t>0.f)
			{		
				firstHit.intersectable = this;
				return firstHit;
			} else
				return null;
		} else
			return null;
	}
	
	public ArrayList<IntervalBoundary> computeIntervals(Ray r)
	{
		ArrayList<IntervalBoundary> combined = new ArrayList<IntervalBoundary>();
		ArrayList<IntervalBoundary> leftIntervals = left.computeIntervals(r);
		ArrayList<IntervalBoundary> rightIntervals = right.computeIntervals(r);
		
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
				b.hitRecord.wIn.negate();
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
				b.hitRecord.wIn.negate();
			}
			b.belongsTo = BelongsTo.RIGHT;			
		}

		// Combine interval boundaries and sort
		combined.addAll(leftIntervals);
		combined.addAll(rightIntervals);
		Collections.sort(combined);

		// Traverse interval boundaries and set inside/outside 
		// according to Boolean operation
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

	public float surfaceArea() {
		return 0.f;
	}
	
	public AxisAlignedBox boundingBox() {
		return new AxisAlignedBox(0.f, 0.f, 0.f, 0.f, 0.f, 0.f);
	}
}
