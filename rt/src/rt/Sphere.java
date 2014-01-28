package rt;

import javax.vecmath.*;

public class Sphere implements Intersectable {

	private Vector3f center;
	private float radius;
	private float surfaceArea;
	private AxisAlignedBox boundingBox;
	public Material material;
	
	public Sphere(Vector3f center, float radius)
	{
		material = new BlinnMaterial();
		
		this.center = center;
		this.radius = radius;
		boundingBox = new AxisAlignedBox(center.x-radius, center.x+radius, center.y-radius, center.y+radius, center.z-radius, center.z+radius);
		surfaceArea = 4.f*(float)Math.PI*radius*radius;
	}
	
	public float surfaceArea()
	{
		return surfaceArea;
	}
	
	public HitRecord intersect(Ray r) {

		float a, b, c;
		Vector3f tmp = new Vector3f();
		a = r.direction.dot(r.direction);
		tmp.sub(r.origin, center);
		b = 2*tmp.dot(r.direction);
		c = tmp.dot(tmp) - radius*radius;
		
		float discr = b*b - 4*a*c;
		if(discr<0)
		{
			return null;
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
		
		float t;
		if(t0<t1)
		{
			if(t0>0)
				t = t0;
			else if(t1>0)
				t = t1;
			else
				return null;
		} else 
		{
			if(t1>0)
				t = t1;
			else if(t0>0)
				t = t0;
			else
				return null;
		}
		
		// Hit position
		Vector3f position = new Vector3f(r.direction);
		position.scale(t);
		position.add(r.origin);
		
		// Hit normal
		Vector3f normal = new Vector3f();
		normal.sub(position, center);
		normal.normalize();

		// Incident direction
		Vector3f wIn = new Vector3f(r.direction);
		// Convention is that incident direction points away from surface
		wIn.negate();
		wIn.normalize();
		
		return new HitRecord(t, position, normal, wIn, this, null, material, 0.f, 0.f);
	}
	
	public AxisAlignedBox boundingBox()
	{
		return boundingBox;
	}
}
