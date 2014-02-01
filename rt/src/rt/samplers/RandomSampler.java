package rt.samplers;

import java.util.Random;

import rt.Sampler;

/**
 * Makes uniform random samples in the range [0,1].
 */
public class RandomSampler implements Sampler {

	Random random;
	
	public RandomSampler()
	{
		random = new Random();
	}
	
	/**
	 * Makes @param n uniform random samples in @param d
	 * dimensions. The samples are in the range [0,1] in 
	 * all dimensions.
	 */
	public float[][] makeSamples(int n, int d)
	{
		float samples[][] = new float[n][d];
		
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<d; j++)
			{
				samples[i][j] = random.nextFloat();
			}
		}
		return samples;
	}
	
}
