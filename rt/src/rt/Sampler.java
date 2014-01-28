package rt;

import java.util.Iterator;

public interface Sampler {

	public void makeSamples();
	public Iterator<float[]> getIterator();
	
}
