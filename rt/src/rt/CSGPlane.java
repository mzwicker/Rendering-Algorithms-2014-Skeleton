package rt;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class CSGPlane extends CSGSolid {
	
	Vector3f normal;
	float d;
	public Material material;
	
	public CSGPlane(Vector3f normal, float d)
	{
		material = new BlinnMaterial();
		
		this.normal = new Vector3f(normal);
		this.normal.normalize();
		this.d = d;
	}
	
	public ArrayList<IntervalBoundary> computeIntervals(Ray r)
	{
		ArrayList<IntervalBoundary> boundaries = new ArrayList<IntervalBoundary>();
		
		IntervalBoundary b1, b2;
		b1 = new IntervalBoundary();
		b2 = new IntervalBoundary();
		
		HitRecord hitRecord = intersect(r);
		if(hitRecord != null)
		{
			b1.hitRecord = hitRecord;
			b1.t = hitRecord.t;
			b2.hitRecord = null;
			
			if(normal.dot(r.direction) < 0)
			{
				b1.type = BoundaryType.START;
				b2.type = BoundaryType.END;
				b2.t = Float.POSITIVE_INFINITY;			
			} else
			{
				b1.type = BoundaryType.END;
				b2.type = BoundaryType.START;
				b2.t = Float.NEGATIVE_INFINITY;			
			}
			
			boundaries.add(b1);
			boundaries.add(b2);
		}
		
		return boundaries;
	}
		
	public AxisAlignedBox boundingBox() {
		return new AxisAlignedBox(0.f, 0.f, 0.f, 0.f, 0.f, 0.f);
	}

	public HitRecord intersect(Ray r) {

		float tmp = normal.dot(r.direction);
		
		if(tmp!=0)
		{
			float t = -(normal.dot(r.origin) + d) / tmp;
		
			Vector3f position = new Vector3f(r.direction);
			position.scaleAdd(t, r.origin);
			Vector3f retNormal = new Vector3f(normal);
			// wIn is incident direction; convention is that it points away from surface
			Vector3f wIn = new Vector3f(r.direction);
			wIn.negate();
		
			return new HitRecord(t, position, retNormal, wIn, null, null, material, 0.f, 0.f);
		} else
		{
			return null;
		}
	}
}
