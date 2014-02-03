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
	 * Get light source position and normal for location on the
	 * light source given by a sample. Always returns null for 
	 * the light source normal.
	 */
	public LightSample getLightSample(float[] s) {
		return new LightSample(position, null, emission, 1.f);
	}

}
