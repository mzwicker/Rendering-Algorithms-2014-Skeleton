package rt;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class CSGSphere extends CSGSolid {
	
	Vector3f center;
	float radius;
	public Material material;
	
	public CSGSphere(Vector3f center, float radius)
	{
		material = new BlinnMaterial();
		
		this.center = new Vector3f(center);
		this.radius = radius;
	}
	
	public ArrayList<IntervalBoundary> computeIntervals(Ray r)
	{
		ArrayList<IntervalBoundary> boundaries = new ArrayList<IntervalBoundary>();
		
		// Ray-sphere intersection
		float a, b, c;
		Vector3f tmp = new Vector3f();
		a = r.direction.dot(r.direction);
		tmp.sub(r.origin, center);
		b = 2*tmp.dot(r.direction);
		c = tmp.dot(tmp) - radius*radius;
		
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
		Vector3f normal = new Vector3f();
		normal.sub(position, center);
		normal.normalize();
		
		HitRecord hitRecord = new HitRecord(t0, position, normal, wIn, null, null, material, 0.f, 0.f);

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
		// since ray is leaving wIn is same direction as ray
		wIn = new Vector3f(r.direction);
		wIn.normalize();
		normal = new Vector3f();
		normal.sub(position, center);
		normal.normalize();

		hitRecord = new HitRecord(t1, position, normal, wIn, null, null, material, 0.f, 0.f);

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
