package rt;

public class OneSamplerFactory implements SamplerFactory {
		
	public Sampler make()
	{
		return new OneSampler();
	}
}
