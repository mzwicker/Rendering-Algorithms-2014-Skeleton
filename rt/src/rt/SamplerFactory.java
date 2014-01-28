package rt;

public interface SamplerFactory {

	public Sampler make(int d);
	public void setNumberOfSamples(int nDesired);
	public int getNumberOfSamples();
}
