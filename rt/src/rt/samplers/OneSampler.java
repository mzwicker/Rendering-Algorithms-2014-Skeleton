package rt.samplers;

import rt.Sampler;

/**
 * Returns always one sample at 0.5 in all dimensions.
 */
public class OneSampler implements Sampler {

	int d;
	
	public OneSampler()
	{
	}
	
	public float[][] makeSamples(int n, int d)
	{
		float[][] samples = new float[1][d];
		for(int i=0; i<d; i++)
			samples[0][i] = 0.5f;
		
		return samples;
	}
}
