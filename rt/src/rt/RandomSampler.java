package rt;

import java.util.Random;

public class RandomSampler implements Sampler {

	Random random;
	
	public RandomSampler()
	{
		random = new Random();
	}
	
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
