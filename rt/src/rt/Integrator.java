package rt;

/**
 * An integrator takes a ray starting at the camera and evaluates
 * its contribution to the image. 
 * 
 * The name "integrator" refers to the fact that solving the 
 * rendering equation requires integrating over the space of all 
 * light paths connecting the camera and a light source.
 * Various implementations of this interface may make different 
 * approximations and simplifications regarding the solution
 * of the rendering equation.
 */
public interface Integrator {

	/**
	 * Compute contribution of a ray to the image.
	 * 
	 * @param r the ray
	 * @return the contribution of the ray to the image
	 */
	public Spectrum integrate(Ray r);
	
	/**
	 * Generate samples required by the integrator to evaluate 
	 * light paths. 
	 * 
	 * @param sampler the type of sampler to be used to generate the samples
	 * @param n the desired number of samples
	 * @return the array of samples
	 */
	public float[][] makePixelSamples(Sampler sampler, int n);
}
