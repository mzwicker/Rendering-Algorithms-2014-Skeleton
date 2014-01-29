package rt;

public class RandomSampler implements Sampler {

	public float[][] makeSamples(int n, int d)
	{
		float samples[][] = new float[n][d];
		
		for(int i=0; i<n; i++)
		{
			for(int j=0; j<d; j++)
			{
				samples[i][j] = (float)Math.random();
			}
		}
		return samples;
	}
	
}
