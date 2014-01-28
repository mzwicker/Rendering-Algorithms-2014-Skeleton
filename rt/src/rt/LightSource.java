package rt;

import javax.vecmath.*;

public interface LightSource extends Intersectable {
	
	public class LightGeometry
	{
		Vector3f position;
		Vector3f normal;
	}
	
	public Spectrum sampleSpectrum(float[] s);
	public LightGeometry sampleGeometry(float[] s);
	public HitRecord samplePhoton(float[] s);
}
