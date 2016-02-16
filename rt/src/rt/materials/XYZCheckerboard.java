package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Spectrum;

/**
 * A procedural checkerboard along the XYZ axes.
 */
public class XYZCheckerboard extends Diffuse {

	private Spectrum color1;
	private Spectrum color2;
	private float scale;
	
	public XYZCheckerboard(Spectrum color1, Spectrum color2, float scale)
	{
		super(new Spectrum(1.f, 1.f, 1.f));
		this.color1 = color1;
		this.color2 = color2;
		this.scale = scale;
	}

	public XYZCheckerboard()
	{
		super(new Spectrum(1.f, 1.f, 1.f));
		this.color1 = new Spectrum(1.f, 1.f, 1.f);
		this.color2 = new Spectrum(0.f, 0.f, 0.f);
		this.scale = 1.f;
	}
		
	private Spectrum getColor(HitRecord hitRecord) {
		
		if((Math.round(Math.abs(hitRecord.position.x)/scale) % 2 +
			Math.round(Math.abs(hitRecord.position.y)/scale) % 2 + 
			Math.round(Math.abs(hitRecord.position.z)/scale) % 2) % 2 == 0)
		{
			return color1;
		} else
		{
			return color2;
		}
	}
	
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		Spectrum s = super.evaluateBRDF(hitRecord, wOut, wIn);
		if(s!=null)
			s.mult(getColor(hitRecord));
		return s;
	}
	
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample)
	{
		ShadingSample s = super.getShadingSample(hitRecord, sample);
		if(s!=null)
			s.brdf.mult(getColor(hitRecord));
		return s;
	}

}
