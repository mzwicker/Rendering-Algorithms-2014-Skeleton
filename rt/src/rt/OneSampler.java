package rt;

import java.util.Iterator;


public class OneSampler implements Sampler {

	int d;
	
	public OneSampler(int d)
	{
		this.d = d;
	}
	
	public void makeSamples()
	{
	}
	
	public class OneSamplerIterator implements Iterator<float[]>
	{	
		boolean consumed;
		
		public OneSamplerIterator()
		{
			consumed = false;
		}
		
		public boolean hasNext()
		{
			return !consumed;
		}
		
		public float[] next()
		{
			consumed = true;
			
			float[] s = new float[d];
			for(int j=0; j<d; j++)
			{
				s[j] = 0.5f;
			}
			return s;
		}
		
		public void remove()
		{
		}
		
	}

	public Iterator<float[]> getIterator() {
		return new OneSamplerIterator();
	}

}
