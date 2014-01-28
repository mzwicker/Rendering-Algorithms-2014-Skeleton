package rt;

import javax.vecmath.*;

public class HitRecord implements KdItem {

	public Vector3f position;
	public Vector3f normal;
	public Vector3f t1, t2;
	public float u, v;
	
	/**
	 * Incident direction, but points away from surface!
	 */
	public Vector3f wIn;
	
	public float t;
	public Intersectable intersectable;
	public Spectrum spectrum;
	public Material material;
	
	public HitRecord(float t, Vector3f position, Vector3f normal, Vector3f wIn, Intersectable intersectable, Spectrum s, Material material, float u, float v)
	{
		this.t = t;
		this.position = position;
		this.normal = normal;
		this.wIn = wIn;
		this.intersectable = intersectable;
		this.spectrum = s;
		this.material = material;
		this.u = u;
		this.v = v;
		
		// Make tangent frame: t1, t2, normal is a right handed frame
		t1 = new Vector3f(1,0,0);
		t1.cross(t1, normal);
		if(t1.length()==0)
		{
			t1 = new Vector3f(0,1,0);
			t1.cross(t1, normal);
		}
		t1.normalize();
		t2 = new Vector3f();
		t2.cross(normal, t1);
	}
	
	public float[] getPosition()
	{
		float tmp[] = new float[3];
		position.get(tmp);
		return tmp;
	}
	
	public Vector3f getPositionVector()
	{
		return position;
	}
}
