package rt.samplers;

import rt.Sampler;
import rt.SamplerFactory;

/**
 * Makes a {@link OneSampler}.
 */
public class OneSamplerFactory implements SamplerFactory {
		
	public Sampler make()
	{
		return new OneSampler();
	}
}
