package rt;

import java.util.Comparator;

public class KdItemComparator implements Comparator<KdItem> {

	private int axis;
	
	public KdItemComparator(int axis)
	{
		this.axis = axis;
	}
	
	public int compare(KdItem a, KdItem b)
	{
		float[] posA = a.getPosition();
		float[] posB = b.getPosition();
				
		if(posA[axis] < posB[axis])
			return -1;
		else if(posA[axis] == posB[axis])
			return 0;
		else return 1;
	}
}
