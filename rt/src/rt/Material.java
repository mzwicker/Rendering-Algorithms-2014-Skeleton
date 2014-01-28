package rt;

import javax.vecmath.*;

public interface Material {
	
	/**
	 * Returns relative contribution of different reflection types. This is used by the 
	 * integrators to determine which type to sample. 
	 */
	public float getDiffuseContribution();
	public float getReflectContribution();
	public float getRefractContribution();

	/**
	 * Evaluate BRDF for each reflection type.
	 * 
	 * @param hitRecord Information about hit point
	 * @param wOut Outgoing direction, normalized and pointing away from the surface
	 * @param wIn Incoming direction, normalized and pointing away from the surface
	 * @return BRDF value
	 */
	public Spectrum evaluateDiffuseBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn);
	public Spectrum evaluateSpecularBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn);
	public Spectrum evaluateRefractiveBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn);
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut);
	
	/**
	 * Sample an incident direction for each reflection type. The length
	 * of the returned vector is the sample probability density (pdf).
	 */
	public Vector3f sampleDiffuse(HitRecord hitRecord, float[] sample);	
	public Vector3f sampleReflect(HitRecord hitRecord, float[] sample);	
	public Vector3f sampleRefract(HitRecord hitRecord, float[] sample);
	
	/**
	 * Return whether material has perfect mirror reflection and refraction.
	 */
	public boolean mirrorReflect();
	public boolean mirrorRefract();
}
