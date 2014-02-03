package rt;

import javax.vecmath.*;

/**
 * Light sources provide illumination for rendered scenes.
 */
public interface LightSource {

	/**
	 * Stores all information related to a light sample.
	 */
	public class LightSample
	{
		public Vector3f position;
		public Vector3f normal;
		public Spectrum emission;
		public float p;
		
		public LightSample(Vector3f position, Vector3f normal, Spectrum emission, float p)
		{
			this.position = position;
			this.normal = normal;
			this.emission = emission;
			this.p = p;
		}
		
	}
		
	/**
	 * Return the position and normal on a light source, given a random sample location.
	 * 
	 * @param s random sample to determine the location on the light source
	 * @return the position and normal on the light source in world coordinates
	 */
	public LightSample getLightSample(float[] s);
}
