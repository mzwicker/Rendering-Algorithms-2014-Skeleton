package rt.materials;

import java.util.Random;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

/**
 * This material should be used with {@link rt.lightsources.PointLight}.
 */
public class PointLightMaterial implements Material {

	Spectrum emission;
	Random rand;
	
	public PointLightMaterial(Spectrum emission)
	{
		this.emission = new Spectrum(emission);
		this.rand = new Random();
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		
		return new Spectrum(emission);
	}

	/**
	 * Return a random direction over the full sphere of directions.
	 */
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		// To be implemented
		return null;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public boolean castsShadows() {
		return false;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum(0.f, 0.f, 0.f);
	}
	
	/** 
	 * Shouldn't be called on a point light
	 */
	public boolean hasSpecularReflection() {
		return false;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		return null;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public boolean hasSpecularRefraction() {
		return false;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		return null;
	}


}
