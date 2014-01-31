package rt;

/**
 * Makes a {@link OneSampler}.
 */
public class OneSamplerFactory implements SamplerFactory {
		
	public Sampler make()
	{
		return new OneSampler();
	}
}
