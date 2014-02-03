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
		/**
		 * The position on the light source that was sampled.
		 */
		public Vector3f position;
		
		/**
		 * The normal on the light source that was sampled.
		 */
		public Vector3f normal;
		
		/**
		 * The emission on the light source that was sampled.
		 */
		public Spectrum emission;
		
		/**
		 * The probability density of the sample.
		 */
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
	 * Get a {@link LightSample} from this light source.
	 * 
	 * @param s random sample to determine the location on the light source
	 * @return the light samples
	 */
	public LightSample getLightSample(float[] s);
}
