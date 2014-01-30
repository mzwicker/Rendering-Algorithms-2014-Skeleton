package rt;

import javax.vecmath.*;

/**
 * Stores information about a ray-surface intersection. This information 
 * is typically used for shading.
 */
public class HitRecord  {

	/**
	 * Hit position.
	 */
	public Vector3f position;
	
	/**
	 * Normal at hit point.
	 */
	public Vector3f normal;
	
	/**
	 * Tangent vectors at hit point.
	 */
	public Vector3f t1, t2;
	
	/**
	 * Texture coordinates at hit point.
	 */
	public float u, v;			
	
	/**
	 * Direction towards origin of ray that hit surface. By convention it points away from 
	 * the surface, that is, in the direction opposite to the incident ray.
	 */
	public Vector3f w;
	
	/**
	 * t parameter of the ray at the hit point.
	 */
	public float t;				
	
	/**
	 * The {@link Intersectable} that was hit.
	 */
	public Intersectable intersectable;	
	
	/** 
	 * The material at the hit point.
	 */
	public Material material;
	
	public HitRecord(float t, Vector3f position, Vector3f normal, Vector3f w, Intersectable intersectable, Material material, float u, float v)
	{
		this.t = t;
		this.position = position;
		this.normal = normal;
		this.w = w;
		this.intersectable = intersectable;
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
