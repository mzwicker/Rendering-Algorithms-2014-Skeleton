package rt;

public class OneSamplerFactory implements SamplerFactory {
		
	public Sampler make(int d)
	{
		return new OneSampler(d);
	}
	
	public void setNumberOfSamples(int nDesired)
	{
	}
	
	public int getNumberOfSamples()
	{
		return 1;
	}
}
