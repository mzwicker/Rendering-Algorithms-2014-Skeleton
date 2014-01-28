package rt;

import javax.vecmath.Vector3f;

public class RefractiveMaterial implements Material {

	float refractiveIndex;
	
	public RefractiveMaterial(float refractiveIndex)
	{
		this.refractiveIndex = refractiveIndex;
	}
	
	public float getDiffuseContribution() {
		return 0.f;
	}

	public float getReflectContribution() {
		return 0.5f;
	}

	public float getRefractContribution() {
		return 0.5f;
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut)
	{
		return new Spectrum();
	}

	public boolean mirrorReflect() {
		return true;
	}

	public boolean mirrorRefract() {
		return true;
	}

	public Vector3f sampleDiffuse(HitRecord hitRecord, float[] sample) {
		return null;
	}

	public Vector3f sampleReflect(HitRecord hitRecord, float[] sample) {
		
		// Compute mirror reflection direction
		Vector3f d = new Vector3f(hitRecord.wIn);
		float tmp = d.dot(hitRecord.normal);
		d.negate();
		d.scaleAdd(2*tmp, hitRecord.normal, d);
		d.normalize();

		return d;
	}

	public Vector3f sampleRefract(HitRecord hitRecord, float[] sample) {
		
		// Incident direction, points away from surface
		Vector3f v = new Vector3f(hitRecord.wIn);
		v.normalize();
		// We want it to point towards surface
		v.negate();
		
		float theta1, costheta2;
		float r;
		
		// Check if we enter or leave the material
		Vector3f n = new Vector3f(hitRecord.normal);
		float costheta1 = v.dot(n);
		if(costheta1<0)
		{
			// Entering material
			
			costheta1 = -costheta1;
			// r = (ref. index outside = 1) / (ref. index inside)
			r = 1/refractiveIndex;
		} else
		{
			// Leaving material
			
			// Flip normal so it points inside
			n.scale(-1.f);
			// r = (ref. index inside) / (ref. index outside = 1)
			r = refractiveIndex;

		}

		theta1 = (float)Math.acos(costheta1);
		// Check for total internal reflection
		if(Math.sin(theta1)*r <= 1.f)
		{	
			// Snell's law
			costheta2 = (float)Math.cos( Math.asin( Math.sin(theta1)*r ) );
			
			// Refracted vector
			Vector3f refracted = new Vector3f(v);
			refracted.normalize();
			refracted.scale(r);
			n.scale(r*costheta1-costheta2);
			refracted.add(n);
			refracted.normalize();
			
			return refracted;
		} else
		{
			// Total internal reflection				
			return null;
		}
	}

	public Spectrum evaluateDiffuseBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		return new Spectrum();
	}

	public Spectrum evaluateSpecularBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{	
		// Schlick approximation
		float cosOut = (wOut.dot(hitRecord.normal));
		
		// Check if we did hit inside of an object. In this case, need to flip normal, i.e.,
		// change sign of cosine of outgoing angle.
		if(cosOut<0)
		{
			cosOut = -cosOut;
		}
		
		float  kr = (refractiveIndex-1.f)/(refractiveIndex+1.f);
		kr*=kr;
		kr = kr + (1.f-kr) * (float)Math.pow(1.f-cosOut,5);
	
		// Need to divide by cosine of incident angle, since we evaluate BRDF!
		float cosIn = Math.abs(hitRecord.normal.dot(wIn));
		kr = kr/cosIn;
		
		return new Spectrum(kr, kr, kr);
	}

	public Spectrum evaluateRefractiveBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) 
	{
		Spectrum kt = evaluateSpecularBRDF(hitRecord, wOut, wIn);
		kt.r = Math.max(0.f, 1-kt.r);
		kt.g = Math.max(0.f, 1-kt.g);
		kt.b = Math.max(0.f, 1-kt.b);

		return kt;
	}

}
