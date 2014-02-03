package rt;

import javax.vecmath.*;

/**
 * Materials implement functionality for shading surfaces using their BRDFs.
 */
public interface Material {
	
	/**
	 * Stores information about a shading sample.
	 */
	public class ShadingSample {
		/**
		 * The BRDF value.
		 */
		public Spectrum brdf;
		
		/**
		 * The sampled direction.
		 */
		public Vector3f w;
		
		/**
		 * Tells the integrator whether this is a specular sample. In this case,
		 * a cosine factor in the specular BRDF should be omitted in the returned 
		 * BRDF value, and the integrator should act accordingly.
		 */
		public boolean isSpecular;
		
		/**
		 * The probability density of the sample
		 */
		public float p;
	}
	
	/**
	 * Evaluate BRDF for pair of incoming and outgoing directions. This method
	 * is typically called by an integrator when the integrator sampled the incident 
	 * direction on a light source.
	 * 
	 * @param hitRecord Information about hit point
	 * @param wOut Outgoing direction, normalized and pointing away from the surface
	 * @param wIn Incoming direction, normalized and pointing away from the surface
	 * @return BRDF value
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn);

	/**
	 * Return whether material has perfect specular reflection. 
	 */
	public boolean hasSpecularReflection();
	
	/**
	 * Evaluate specular reflection. This method is typically called by a recursive
	 * ray tracer to follow the path of specular reflection.
	 */
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord);
	
	/**
	 * Return whether the material has perfect specular refraction. 
	 */
	public boolean hasSpecularRefraction();

	/**
	 * Evaluate specular refraction. This method is typically called by a recursive
	 * ray tracer to follow the path of specular refraction.
	 */
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord);	
	
	/**
	 * Calculate a shading sample, given a uniform random sample as input. This 
	 * method is typically called in a path tracer to sample and evaluate the
	 * next path segment. The methods decides which component of the material to 
	 * sample (diffuse, specular reflection or refraction, etc.), computes an 
	 * incident direction, and returns the BRDF value, the direction, and the 
	 * probability density (stored in a {@link ShadingSample}). 
	 */
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample);		
}
