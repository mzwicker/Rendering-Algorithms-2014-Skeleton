package rt;

import java.util.ArrayList;

public abstract class CSGSolid {

	enum BoundaryType { START, END };
	public enum OperationType { INTERSECT, ADD, SUBTRACT };
	public enum BelongsTo { LEFT, RIGHT };
	
	public CSGSolid left, right;
	public OperationType operation;
	
	class IntervalBoundary implements Comparable<IntervalBoundary>
	{
		float t;
		BoundaryType type;
		HitRecord hitRecord;
		BelongsTo belongsTo;
		
		public int compareTo(IntervalBoundary b)
		{
			if(this.t < b.t) 
				return -1; 
			else if(this.t == b.t)
				return 0;
			else
				return 1;
		}
	}
	
	abstract ArrayList<IntervalBoundary> computeIntervals(Ray r);
}
