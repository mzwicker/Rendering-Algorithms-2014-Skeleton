package rt;

public interface Integrator {

	public Spectrum integrate(Ray r);
	public void prepareSamples(int nRays);
}
