package rt.samplers;

import rt.Sampler;
import rt.SamplerFactory;

/**
 * Makes a {@link RandomSampler}.
 */
public class RandomSamplerFactory implements SamplerFactory {

	public Sampler make() {
		return new RandomSampler();
	}

}
