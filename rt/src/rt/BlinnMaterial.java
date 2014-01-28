package rt;

import javax.vecmath.Vector3f;

public class BlinnMaterial implements Material {

	private Spectrum kd;
	private Spectrum ks;
	private float shininess;
	public Spectrum ke;
	public Texture kdTexture;
	
	private float reflectContribution;
	private float diffuseContribution;
	
	/**
	 * @param kd specified in terms of diffuse reflectivity in the range [0,1]
	 * @param ks
	 * @param shininess
	 */
	public BlinnMaterial(Spectrum kd, Spectrum ks, float shininess)
	{
		this.kd = new Spectrum(kd);
		// Normalize diffuse reflectivity to diffuse BRDF constant
		this.kd.r /= (float)Math.PI;
		this.kd.g /= (float)Math.PI;
		this.kd.b /= (float)Math.PI;
		
		this.ks = new Spectrum(ks);
		this.shininess = shininess;
		
		diffuseContribution = 0.5f;
		reflectContribution = 0.5f;
		
		kdTexture = null;
	}

	public BlinnMaterial(Spectrum kd)
	{
		this.kd = new Spectrum(kd);
		// Normalize diffuse reflectivity to diffuse BRDF constant
		this.kd.r /= (float)Math.PI;
		this.kd.g /= (float)Math.PI;
		this.kd.b /= (float)Math.PI;
		
		this.ks = new Spectrum();
		this.shininess = 0.f;
		
		diffuseContribution = 1.f;
		reflectContribution = 0.f;
		
		kdTexture = null;
	}
	
	public BlinnMaterial()
	{
		kd = new Spectrum(.5f/(float)Math.PI, .5f/(float)Math.PI, .5f/(float)Math.PI);
		ks = new Spectrum();
		shininess = 0.f;
		
		diffuseContribution = 1.f;
		reflectContribution = 0.f;
		
		kdTexture = null;
	}
	
	public Spectrum evaluateDiffuseBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		if(kdTexture == null)
		{
			return new Spectrum(kd.r, kd.g, kd.b);
		} else
		{
			return kdTexture.bilinearLookup(hitRecord.u, hitRecord.v);
		}
	}
	
	public Spectrum evaluateSpecularBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		// Incident and outgoing directions
		Vector3f h = new Vector3f(wOut);
		
		// Half-way vector
		h.add(wIn);
		h.normalize();
		
		// Compute specular reflection factor
		float s  = (float)Math.pow(h.dot(hitRecord.normal), shininess);
		
		// Normalize with cosine factor; necessary because we are
		// evaluating the BRDF here
		s = s/hitRecord.normal.dot(wIn);
		
		Spectrum r = new Spectrum();
		r.r = ks.r * s;
		r.g = ks.g * s;
		r.b = ks.b * s;
		
		return r;
	}
	
	public Spectrum evaluateRefractiveBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		return new Spectrum();
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut)
	{
		return ke;
	}

	public float getDiffuseContribution()
	{
		return diffuseContribution;
	}
	
	public float getReflectContribution()
	{
		return reflectContribution;
	}
	
	public float getRefractContribution()
	{
		return 0.f;
	}

	public Vector3f sampleDiffuse(HitRecord hitRecord, float[] sample)
	{
		// Generate sample with cosine distribution
		float phi = (float)(2*Math.PI*sample[0]);
		Vector3f d = new Vector3f(0,0,0);
		// cos(phi)*sqrt(sample[1]); note r = sqrt(sample[1]) 
		d.scaleAdd((float)(Math.cos(phi)*Math.sqrt(sample[1])), hitRecord.t1, d);
		// sin(phi)*sqrt(sample[1]); note r = sqrt(sample[1])
		d.scaleAdd((float)(Math.sin(phi)*Math.sqrt(sample[1])), hitRecord.t2, d);
		// sqrt(1-sample[1]); note r^2 = sample[1]
		d.scaleAdd((float)Math.sqrt(1-sample[1]), hitRecord.normal, d);
		d.normalize();
		
		// Length of returned direction is set to density, note normalization
		// by 1/pi
		d.scale(hitRecord.normal.dot(d)/(float)Math.PI);
		
		return d;
	}
	
	public Vector3f sampleReflect(HitRecord hitRecord, float[] sample)
	{
		return null;
	}

	public Vector3f sampleRefract(HitRecord hitRecord, float[] sample)
	{
		return null;
	}
	
	public boolean mirrorReflect()
	{
		return false;
	}
	
	public boolean mirrorRefract()
	{
		return false;
	}
	
}
