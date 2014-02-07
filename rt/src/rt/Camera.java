package rt;

/**
 * Given the specification of a ray in image space, a camera constructs 
 * a corresponding ray in world space. 
 */
public interface Camera {

	/**
	 * Given a ray in image space, make a ray in world space according 
	 * to the camera specifications. The method receives a sample that 
	 * the camera can use to generate the ray. Typically the first two
	 * sample dimensions are used to sample a location in the current 
	 * pixel. The samples are assumed to be in the range [0,1].
	 * 
	 * @param i pixel column index
	 * @param j pixel row index
	 * @param sample random sample that the camera can use to generate a ray   
	 * @return the ray in world coordinates
	 */
	public Ray makeWorldSpaceRay(int i, int j, float sample[]);
}
