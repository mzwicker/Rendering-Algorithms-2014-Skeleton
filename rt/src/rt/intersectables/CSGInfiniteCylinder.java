package rt.intersectables;

import java.util.ArrayList;
import javax.vecmath.*;
import rt.*;
import rt.materials.*;

/**
 * An infinite, open cylinder.
 */
public class CSGInfiniteCylinder extends CSGSolid {

	float radius;
	public Material material;
	
	public CSGInfiniteCylinder()
	{
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
		
		this.radius = 1.f;
	}
	
	public CSGInfiniteCylinder(float radius)
	{
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
		
		this.radius = radius;
	}
	
	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		ArrayList<IntervalBoundary> boundaries = new ArrayList<IntervalBoundary>();
		
		// Ray-cylinder intersection
		// http://www.cl.cam.ac.uk/teaching/1999/AGraphHCI/SMAG/node2.html
		float a, b, c;
		a = r.direction.x*r.direction.x + r.direction.y*r.direction.y;
		b = 2 * (r.origin.x*r.direction.x + r.origin.y*r.direction.y);
		c = r.origin.x*r.origin.x + r.origin.y*r.origin.y - radius*radius;
				
		float discr = b*b - 4*a*c;
		if(discr<0)
		{
			return boundaries;
		}
		
		float q;
		if(b<0)
		{
			q = (-b+(float)Math.sqrt((double)(b*b-4*a*c)))/2.f;
		} else
		{
			q = (-b-(float)Math.sqrt((double)(b*b-4*a*c)))/2.f;
		}
		
		if(a==0 || q==0)
		{
			return boundaries;
		}
		
		float t0 = q/a;
		float t1 = c/q;
		
		// Swap intersection points such that t0 is smaller
		float t;
		if(t1<t0)
		{
			t = t0;
			t0 = t1;
			t1 = t;
		}

		// First intersection 
		Vector3f position = new Vector3f(r.direction);
		position.scale(t0);
		position.add(r.origin);
		// wIn is incident direction; convention is that it points away from surface
		Vector3f wIn = new Vector3f(r.direction);
		wIn.negate();
		wIn.normalize();
		Vector3f normal = new Vector3f(position);
		normal.z = 0.f;
		normal.normalize();
		
		HitRecord hitRecord = new HitRecord(t0, position, normal, wIn, null, material, 0.f, 0.f);

		// Add interval boundary to list
		IntervalBoundary b1, b2;
		b1 = new IntervalBoundary();		
		b1.hitRecord = hitRecord;
		b1.t = hitRecord.t;
		b1.type = BoundaryType.START;
		
		// Second intersection
		position = new Vector3f(r.direction);
		position.scale(t1);
		position.add(r.origin);
		// wIn is incident direction; convention is that it points away from surface
		wIn = new Vector3f(r.direction);
		wIn.negate();
		wIn.normalize();
		normal = new Vector3f(position);
		normal.z = 0.f;
		normal.normalize();

		hitRecord = new HitRecord(t1, position, normal, wIn, null, material, 0.f, 0.f);

		// Add interval boundary to list
		b2 = new IntervalBoundary();
		b2.hitRecord = hitRecord;
		b2.type = BoundaryType.END;
		b2.t = t1;
		
		boundaries.add(b1);
		boundaries.add(b2);
		
		return boundaries;
	}
}
