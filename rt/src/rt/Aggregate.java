package rt;

import java.util.Iterator;

public abstract class Aggregate implements Intersectable {

	public HitRecord intersect(Ray r) {

		HitRecord hitRecord = null;
		float t = Float.MAX_VALUE;
		
		Iterator<Intersectable> it = iterator();
		while(it.hasNext())
		{
			Intersectable o = it.next();
			HitRecord tmp = o.intersect(r);
			if(tmp!=null && tmp.t<t)
			{
				t = tmp.t;
				hitRecord = tmp;
			}
		}
		return hitRecord;
	}
	
	public float surfaceArea()
	{
		Iterator<Intersectable> it = iterator();
		float a=0;
		while(it.hasNext())
		{
			a += it.next().surfaceArea();
		}
		return a;
	}
	
	public abstract Iterator<Intersectable> iterator();

}
