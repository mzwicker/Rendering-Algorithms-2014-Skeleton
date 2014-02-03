package rt.lightsources;

import javax.vecmath.*;

import rt.LightSource;
import rt.Spectrum;

/**
 * A point light source.
 */
public class PointLight implements LightSource {

	Vector3f position;
	Spectrum emission;
	
	public PointLight(Vector3f position, Spectrum emission)
	{
		this.position = new Vector3f(position);
		this.emission = new Spectrum(emission);
	}
	
	/**
	 * Get a {@link rt.LightSource.LightSample} from this point light. Since this
	 * is a point light, always return null for the light source normal,
	 * and 1 for the probability density.
	 */
	public LightSample getLightSample(float[] s) {
		return new LightSample(position, null, emission, 1.f);
	}

}
