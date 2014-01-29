package rt;

public class RandomSamplerFactory implements SamplerFactory {

	public Sampler make() {
		return new RandomSampler();
	}

}
