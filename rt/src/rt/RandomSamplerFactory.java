package rt;

/**
 * Makes a {@link RandomSampler}.
 */
public class RandomSamplerFactory implements SamplerFactory {

	public Sampler make() {
		return new RandomSampler();
	}

}
