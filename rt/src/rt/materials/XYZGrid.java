package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Spectrum;
import rt.Material.ShadingSample;

/**
 * A procedural grid along the XYZ axes.
 */
public class XYZGrid extends Diffuse {

	private float thickness;
	private Spectrum lineColor;
	private Spectrum tileColor;
	private Vector3f shift;
	private float scale;
	
	public XYZGrid(Spectrum lineColor, Spectrum tileColor, float thickness)
	{
		super(new Spectrum(1.f, 1.f, 1.f));
		this.lineColor = lineColor;
		this.tileColor = tileColor;
		this.thickness = thickness;
		this.shift = new Vector3f(0.f, 0.f, 0.f);
		this.scale = 1.f;
	}
	
	public XYZGrid(Spectrum lineColor, Spectrum tileColor, float thickness, Vector3f shift)
	{
		this(lineColor, tileColor, thickness);
		this.shift = new Vector3f(shift);
		this.scale = 1.f;
	}
	
	public XYZGrid(Spectrum lineColor, Spectrum tileColor, float thickness, Vector3f shift, float scale)
	{
		this(lineColor, tileColor, thickness, shift);
		this.scale = scale;
	}
	
	private Spectrum getColor(HitRecord hitRecord) {
		
		if(Math.abs((shift.x + hitRecord.position.x)/scale - Math.round(hitRecord.position.x/scale))<thickness/scale || 
			Math.abs((shift.y + hitRecord.position.y)/scale - Math.round(hitRecord.position.y/scale))<thickness/scale ||
			Math.abs((shift.z + hitRecord.position.z)/scale - Math.round(hitRecord.position.z/scale))<thickness/scale )
		{
			return lineColor;
		} else
		{
			return tileColor;
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
