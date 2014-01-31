package rt;

import javax.vecmath.Vector3f;

/**
 * A basic diffuse material.
 */
public class DiffuseMaterial implements Material {

	Spectrum kd;
	
	public DiffuseMaterial(Spectrum kd)
	{
		this.kd = new Spectrum(kd);
	}

	/**
	 * Returns diffuse BRDF value, that is, a constant.
	 * 
	 *  @param wOut outgoing direction, by convention towards camera
	 *  @param wIn incident direction, by convention towards light
	 *  @param hitRecord hit record to be used
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		return kd;
	}

}
