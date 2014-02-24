package rt.lightsources;

import java.util.Random;
import javax.vecmath.Vector3f;
import rt.*;
import rt.materials.PointLightMaterial;

/**
 * Implements a point light using a {@link rt.materials.PointLightMaterial}.
 */
public class PointLight implements LightGeometry {

	Vector3f position;
	PointLightMaterial pointLightMaterial;
	Random rand;
	
	public PointLight(Vector3f position, Spectrum emission)
	{
		this.position = new Vector3f(position);
		this.rand = new Random();
		pointLightMaterial = new PointLightMaterial(emission);
	}
	
	/**
	 * A ray never hit a point.
	 */
	public HitRecord intersect(Ray r) {
		return null;
	}

	/**
	 * Sample a point on the light geometry. On a point light,
	 * always return light position with probability one. 
	 * Set normal to null.
	 */
	public HitRecord sample(float[] s) {
		HitRecord hitRecord = new HitRecord();
		hitRecord.position = new Vector3f(position);
		hitRecord.material = pointLightMaterial;
		hitRecord.normal = null;
		hitRecord.p = 1.f;
		return hitRecord;
	}

}
