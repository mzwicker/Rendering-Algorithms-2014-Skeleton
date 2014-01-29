package rt;

public interface Camera {

	/**
	 * Make a ray in world space according to the camera specifications.
	 * 
	 * @param i pixel column index
	 * @param j pixel row index
	 * @param k sample to be used
	 * @param samples array of n-dimensional samples. Use the first two dimensions to sample the image plane. The samples need to be in the range [0,1].   
	 * @return
	 */
	public Ray makeWorldSpaceRay(int i, int j, int k, float samples[][]);
}
