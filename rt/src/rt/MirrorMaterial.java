package rt;

import javax.vecmath.Vector3f;

public class MirrorMaterial implements Material {

	Spectrum k;
	
	public MirrorMaterial(Spectrum k)
	{
		this.k = new Spectrum(k);
	}
	
	public float getDiffuseContribution() 
	{
		return 0;
	}

	public float getReflectContribution() 
	{
		return 1;
	}

	public float getRefractContribution() 
	{
		return 0;
	}

	public Spectrum evaluateDiffuseBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		return new Spectrum();
	}

	public Spectrum evaluateSpecularBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		// Need to divide by cosine, since we are evaluating the mirror BRDF!
		float c = hitRecord.normal.dot(wIn);
		Spectrum s = new Spectrum(k);
		s.r /= c;
		s.g /= c;
		s.b /= c;
		return s;
	}

	public Spectrum evaluateRefractiveBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		return new Spectrum();
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut)
	{
		return new Spectrum();
	}
	
	public Vector3f sampleDiffuse(HitRecord hitRecord, float[] sample)
	{	
		return null;
	}
	
	public Vector3f sampleReflect(HitRecord hitRecord, float[] sample)
	{
		// Compute mirror reflection direction
		// Note: incident direction wIn points away from surface
		Vector3f d = new Vector3f(hitRecord.wIn);
		float tmp = d.dot(hitRecord.normal);
		d.negate();
		d.scaleAdd(2*tmp, hitRecord.normal, d);
		d.normalize();

		return d;
	}

	public Vector3f sampleRefract(HitRecord hitRecord, float[] sample)
	{
		return null;
	}
	
	public boolean mirrorReflect()
	{
		return true;
	}

	public boolean mirrorRefract()
	{
		return false;
	}
}
