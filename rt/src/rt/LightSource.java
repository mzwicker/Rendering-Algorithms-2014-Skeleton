package rt;

import javax.vecmath.*;

/**
 * Interface required for light sources.
 */
public interface LightSource {

	public class LightGeometry
	{
		Vector3f position;
		Vector3f normal;
		
		public LightGeometry(Vector3f position, Vector3f normal)
		{
			this.position = position;
			this.normal = normal;
		}
		
	}
	
	/**
	 * Return the emission spectrum for a light source. 
	 * 
	 * @param s random sample to determine the location on the light source
	 * @return the emission spectrum
	 */
	public Spectrum getEmission(float[] s);
	
	/**
	 * Return the position and normal on a light source, given a random sample location.
	 * 
	 * @param s random sample to determine the location on the light source
	 * @return the position and normal on the light source in world coordinates
	 */
	public LightGeometry getGeometry(float[] s);
}
