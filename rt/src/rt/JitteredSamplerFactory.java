package rt;

public class JitteredSamplerFactory implements SamplerFactory {

	int nDesired;
	
	public JitteredSamplerFactory()
	{
		nDesired = 1;
	}
	
	public Sampler make(int d)
	{
		return new JitteredSampler(nDesired, d);
	}
	
	public void setNumberOfSamples(int nDesired)
	{
		this.nDesired = nDesired;
	}
	
	public int getNumberOfSamples()
	{
		return nDesired;
	}
}
