package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

/**
 * A basic diffuse material.
 */
public class Diffuse implements Material {

	Spectrum kd;
	
	public Diffuse(Spectrum kd)
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
