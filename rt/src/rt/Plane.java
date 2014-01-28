package rt;

import javax.vecmath.*;

public class Plane implements Intersectable {

	Vector3f normal;
	float d;
	public Material material;
	
	public Plane(Vector3f normal, float d)
	{
		material = new BlinnMaterial();
		
		this.normal = new Vector3f(normal);
		this.normal.normalize();
		this.d = d;
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
		
			if(t>0) return new HitRecord(t, position, retNormal, wIn, this, null, material, 0.f, 0.f); else return null;
		} else
		{
			return null;
		}
	}

	public float surfaceArea() {
		return 0.f;
	}
}
