package rt;

public interface Integrator {

	public Spectrum integrate(Ray r);
	public float[][] makePixelSamples(Sampler sampler, int n);
}
