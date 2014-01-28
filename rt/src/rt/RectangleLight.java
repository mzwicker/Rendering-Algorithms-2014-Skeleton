package rt;

import javax.vecmath.Vector3f;

public class RectangleLight implements LightSource {

	Rectangle rectangle;
	Spectrum ke;
	
	/**
	 * 
	 * @param rectangle Rectangular geometry of light source
	 * @param spectrum Light source power
	 */
	public RectangleLight(Rectangle rectangle, Spectrum spectrum)
	{
		ke = new Spectrum(spectrum);
		this.rectangle = new Rectangle(rectangle);
		this.rectangle.material = new BlinnMaterial(new Spectrum());
	}
	
	public LightGeometry sampleGeometry(float[] s) {
		LightGeometry g = new LightGeometry();
		g.position = new Vector3f(rectangle.bottomLeft);
		g.position.scaleAdd(s[0], rectangle.right, g.position);
		g.position.scaleAdd(s[1], rectangle.top, g.position);
		g.normal = new Vector3f(rectangle.normal); 
		return g;
	}

	public Spectrum sampleSpectrum(float[] s) {
		Spectrum l = new Spectrum(ke);
		l.r = l.r/(rectangle.surfaceArea()*(float)Math.PI);
		l.g = l.g/(rectangle.surfaceArea()*(float)Math.PI);
		l.b = l.b/(rectangle.surfaceArea()*(float)Math.PI);
		return new Spectrum(l);
	}
	
	public HitRecord intersect(Ray r)
	{
		HitRecord hitRecord = rectangle.intersect(r);
		if(hitRecord!=null)
		{
			hitRecord.intersectable = this;
			hitRecord.spectrum = new Spectrum(ke);
		}
		return hitRecord;
	}
	
	public float surfaceArea()
	{
		return rectangle.surfaceArea();
	}

	public AxisAlignedBox boundingBox()
	{
		return rectangle.boundingBox();
	}
	
	public HitRecord samplePhoton(float sample[])
	{
		// Generate photon direction with cosine distribution
		float phi = (float)(2*Math.PI*sample[0]);
		Vector3f d = new Vector3f(0,0,0);
		Vector3f tmp = new Vector3f(rectangle.right);
		tmp.normalize();
		// cos(phi)*sqrt(sample[1]); note r = sqrt(sample[1]) 
		d.scaleAdd((float)(Math.cos(phi)*Math.sqrt(sample[1])), tmp, d);
		tmp = new Vector3f(rectangle.top);
		tmp.normalize();
		// sin(phi)*sqrt(sample[1]); note r = sqrt(sample[1])
		d.scaleAdd((float)(Math.sin(phi)*Math.sqrt(sample[1])), tmp, d);
		// sqrt(1-sample[1]); note r^2 = sample[1]
		d.scaleAdd((float)Math.sqrt(1-sample[1]), rectangle.normal, d);
		d.normalize();
		
		// Length of returned direction is set to density
		d.scale(rectangle.normal.dot(d));

		// Generate photon position
		Vector3f position = new Vector3f(rectangle.bottomLeft);
		position.scaleAdd(sample[2], rectangle.right, position);
		position.scaleAdd(sample[3], rectangle.top, position);
		
		// Note: initial weight of photon corresponds to light source power,
		// because of cosine weighted sampling of directions, etc.
		return new HitRecord(0.f, position, rectangle.normal, d, null, new Spectrum(ke), null, 0.f, 0.f);
	}
}
