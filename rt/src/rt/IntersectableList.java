package rt;

import java.util.LinkedList;
import java.util.Iterator;

public class IntersectableList extends Aggregate {

	public LinkedList<Intersectable> list;
	private AxisAlignedBox bbox;
	private float surfaceArea;
	
	public IntersectableList()
	{
		list = new LinkedList<Intersectable>();
		bbox = new AxisAlignedBox(Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE);
		surfaceArea = 0.f;
	}
	
	public void add(Intersectable i)
	{
		AxisAlignedBox b = i.boundingBox();
		for(int j=0; j<3; j++)
		{
			if(b.bounds[j][0]<bbox.bounds[j][0]) bbox.bounds[j][0] = b.bounds[j][0];
			if(b.bounds[j][1]>bbox.bounds[j][1]) bbox.bounds[j][1] = b.bounds[j][1];
		}
		surfaceArea += i.surfaceArea();
		list.add(i);
	}
	
	public Iterator<Intersectable> iterator() {
		return list.iterator();
	}

	public AxisAlignedBox boundingBox() {
		return bbox;
	}
	
	public float surfaceArea()
	{
		return surfaceArea;
	}
	
	public Material getMaterial()
	{
		return null;
	}
}
