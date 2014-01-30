package rt;

public interface Sampler {

	/**
	 * Make an array of samples. The samples need to lie in the range [0,1]^d,
	 * where d is the dimensionality of the samples. The number of returned 
	 * samples may differ from the number of desired samples n.
	 * 
	 * @param n desired number of samples
	 * @param d dimensionality of samples
	 * @return
	 */
	public float[][] makeSamples(int n, int d);
}
