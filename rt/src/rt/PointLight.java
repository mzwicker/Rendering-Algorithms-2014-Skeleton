package rt;

import javax.vecmath.*;

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
	
	public Spectrum getEmission(float[] s) {
		return emission;
	}

	/**
	 * Get light source position and normal for location on the
	 * light source given by a sample. Always returns null for 
	 * the light source normal.
	 */
	public LightGeometry getGeometry(float[] s) {
		return new LightGeometry(position, null);
	}

}
