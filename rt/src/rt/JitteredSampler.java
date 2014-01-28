package rt;

import java.util.Iterator;

public class JitteredSampler implements Sampler {

	float[] samples;// array of samples
	int b[];		// used to compute jittered sample location
	int c[];		// used to compute jittered sample location
	int n, d;		// number of samples, number of dimensions
	int nPerD[];	// number of samples per dimension
	int perm[];		// permutation table to randomize order of samples
	
	public JitteredSampler(int nDesired, int d)
	{
		this.d = d;
		nPerD = new int[d];
		for(int i=0; i<d; i++)
		{
			nPerD[i] = (int)Math.ceil(Math.pow((double)nDesired, 1/(double)d));
		}
		
		d = nPerD.length;
		n = 1;
		b = new int[d];
		for(int i=0; i<d; i++)
		{
			b[i] = n;
			n = n*nPerD[i];
		}
		
		samples = new float[n*d];
		c = new int[d];
		
		// Build permutation table
		perm = new int[n];
		for(int i=0; i<n; i++) perm[i] = i;
		for(int i=0; i<n; i++)
		{
			int ii = (int)(Math.random()*(double)n);
			int t = perm[i];
			perm[i] = perm[ii];
			perm[ii] = t;
		}
	}
	
	public JitteredSampler(int[] nPerD)
	{
		this.nPerD = new int[nPerD.length];
		d = nPerD.length;
		n = 1;
		int b[] = new int[d];
		for(int i=0; i<d; i++)
		{
			b[i] = n;
			n = n*nPerD[i];
			this.nPerD[i] = nPerD[i];
		}
		
		samples = new float[n*d];
		c = new int[d];
		
		// Build permutation table
		perm = new int[n];
		for(int i=0; i<n; i++) perm[i] = i;
		for(int i=0; i<n; i++)
		{
			int ii = (int)(Math.random()*(double)n);
			int t = perm[i];
			perm[i] = perm[ii];
			perm[ii] = t;
		}
	}
	
	public void makeSamples()
	{
		for(int i=0; i<n; i++)
		{
			// Look up index of sample in permutation table
			int ii = perm[i];
			// Compute jittering center c[] based on index
			for(int j=d-1; j>=0; j--)
			{
				c[j] = ii/b[j];
				ii = ii%b[j];
			}

			for(int j=0; j<d; j++)
			{
				samples[i*d+j] = ((float)c[j]+(float)Math.random())/(float)nPerD[j];
			}
		}
	}
	
	public class JitteredSamplerIterator implements Iterator<float[]>
	{
		private int i;
		
		public JitteredSamplerIterator()
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
		return new JitteredSamplerIterator();
	}

}
