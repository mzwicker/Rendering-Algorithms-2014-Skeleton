package rt;

import java.util.Iterator;

/**
 * A group of {@link Intersectable} objects.
 */
public abstract class Aggregate implements Intersectable {

	public HitRecord intersect(Ray r) {

		HitRecord hitRecord = null;
		float t = Float.MAX_VALUE;
		
		// Intersect all objects in group, return closest hit
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
	
	public abstract Iterator<Intersectable> iterator();

}
