package rt;

import javax.vecmath.*;

/**
 * Materials implement functionality for shading surfaces using their BRDFs.
 */
public interface Material {
	
	/**
	 * Evaluate BRDF for pair of incoming and outgoing directions.
	 * 
	 * @param hitRecord Information about hit point
	 * @param wOut Outgoing direction, normalized and pointing away from the surface
	 * @param wIn Incoming direction, normalized and pointing away from the surface
	 * @return BRDF value
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn);
}
