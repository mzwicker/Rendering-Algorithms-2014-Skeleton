package rt;

/**
 * Makes random samples, which are used for Monte Carlo rendering. The 
 * samples always need to lie in the range [0,1]. Various versions such 
 * as purely random, jittered, or low discrepancy samplers could be 
 * implemented.   
 */
public interface Sampler {

	/**
	 * Make an array of samples. The samples need to lie in the range [0,1]^d,
	 * where d is the dimensionality of the samples. The number of returned 
	 * samples may differ from the number of desired samples n.
	 * 
	 * @param n desired number of samples
	 * @param d dimensionality of samples
	 * @return the array of d-dimensional samples samples
	 */
	public float[][] makeSamples(int n, int d);
}
