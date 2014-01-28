package rt;

import java.util.Iterator;

public class RandomSampler implements Sampler {

	float[] samples;
	int n, d;
	public RandomSampler(int n, int d)
	{
		this.n = n;
		this.d = d;
		samples = new float[n*d];
	}

	public void makeSamples()
	{
		for(int i=0; i<n*d; i++)
		{
			samples[i] = (float)Math.random();
		}
	}
	
	public class RandomSamplerIterator implements Iterator<float[]>
	{
		private int i;
		
		public RandomSamplerIterator()
		{
			i = 0;
		}
		
		public boolean hasNext()
		{
			return i*d<samples.length;
		}
		
		public float[] next()
		{
			float[] s = new float[d];
			for(int j=0; j<d; j++)
			{
				s[j] = samples[i*d+j];
			}
			i++;
			return s;
		}
		
		public void remove()
		{
		}
		
	}

	public Iterator<float[]> getIterator() {
		return new RandomSamplerIterator();
	}

}
