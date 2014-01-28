package rt;

import javax.vecmath.Vector3f;

public class PointLight implements LightSource {
	
	Spectrum spectrum;
	Vector3f position;
	
	public PointLight(Vector3f position, Spectrum spectrum)
	{
		this.position = new Vector3f(position);
		this.spectrum = new Spectrum(spectrum);
	}
	
	public LightGeometry sampleGeometry(float[] s) {
		LightGeometry g = new LightGeometry();
		g.position = new Vector3f(position);
		g.normal = null; 
		return g;
	}

	public Spectrum sampleSpectrum(float[] s) {
		return new Spectrum(spectrum);
	}
	
	public HitRecord intersect(Ray r)
	{
		return null;
	}
	
	public float surfaceArea()
	{
		return 0.f;
	}

	public AxisAlignedBox boundingBox()
	{
		return null;
	}
	
	public Material getMaterial()
	{
		return null;
	}
	
	public HitRecord samplePhoton(float[] s)
	{
		return null;
	}
}
