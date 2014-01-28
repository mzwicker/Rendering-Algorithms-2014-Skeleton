package rt;

import java.util.Iterator;
import javax.vecmath.Vector3f;

public class WhittedIntegrator implements Integrator {

	private Intersectable scene;
	private LightList lights;
	private int maxBounces;
	
	public WhittedIntegrator(Intersectable scene, LightList lights)
	{
		this.scene = scene;
		this.lights = lights;
		maxBounces = 4;
	}
	
	public Spectrum integrate(Ray r) 
	{		
		HitRecord hitRecord = scene.intersect(r);
		return shadeRecursive(hitRecord, 0);
	}

	private Spectrum shadeRecursive(HitRecord hitRecord, int depth)
	{
		Spectrum l = new Spectrum();
		
		if(depth > maxBounces || hitRecord == null) return l;
		hitRecord.wIn.normalize();
		
		Material material = hitRecord.material;
		
		Iterator<LightSource> lightItr = lights.iterator();
		while(lightItr.hasNext())
		{
			// Pick light
			LightSource light = lightItr.next();
			
			// Add contribution of light source by shooting shadow ray
			
			// Avoid shooting shadow ray towards light itself
			if(hitRecord.intersectable != (Intersectable)light) 
			{				
				// Whitted ray tracing. We do not distribute samples.
				float[] sample = new float[2];
				sample[0] = sample[1] = 0.5f;
				LightSource.LightGeometry lightGeometry = light.sampleGeometry(sample);
			
				// Make shadow ray
				Vector3f d = new Vector3f(lightGeometry.position);
				d.sub(hitRecord.position);
				Vector3f o = new Vector3f(hitRecord.position);
				o.scaleAdd(0.0001f, d, o);
				Ray shadowRay = new Ray(o, d);
				d.normalize();
				
				// Cosine factor
				float cosine = hitRecord.normal.dot(d);

				// Shoot shadow ray
				HitRecord shadowHit = null;
				if(cosine > 0)
					shadowHit = scene.intersect(shadowRay);
				
				// If shadow ray is front facing and not blocked, shade
				if(cosine>0 && (shadowHit==null || shadowHit.t>0.999f))
				{					
					// Diffuse shading
					Spectrum kd = material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
					// Specular shading
					Spectrum ks;
					if(!material.mirrorReflect()) 
						ks = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
					else
						ks = new Spectrum();
										
					Spectrum c = light.sampleSpectrum(sample);
				
					// Compute geometry term
					Vector3f tmp = new Vector3f();
					tmp.sub(lightGeometry.position,hitRecord.position);
					float t = tmp.length();
					shadowRay.direction.normalize();
					
					if(lightGeometry.normal != null)
					{
						// Area light source
						
						tmp = new Vector3f(lightGeometry.normal);
						shadowRay.direction.normalize();
						if(tmp.dot(shadowRay.direction)>0)
							t = 0;
						else
							t = Math.abs(tmp.dot(shadowRay.direction)) / (t*t);
						
						// Divide geometry term by pdf. Note: pdf = 1/(area of light), i.e. 1/pdf = (area of light)
						t = t*light.surfaceArea();
					} else
					{
						// Point light source
						t = 1 / (t*t);
					}
	
					// Apply shading coefficients, cosine factor, and attenuate light with geometry term
					l.r += c.r * (kd.r+ks.r) * cosine * t;
					l.g += c.g * (kd.g+ks.g) * cosine * t;
					l.b += c.b * (kd.b+ks.b) * cosine * t;
				}
			}
		}		

		// Mirror reflection
		if(material.mirrorReflect())
		{
			// Make reflected ray
			Vector3f r = material.sampleReflect(hitRecord, null);
			Vector3f o = new Vector3f(hitRecord.position);
			o.scaleAdd(0.0001f, r, o);
			Ray reflectedRay = new Ray(o, r);
			r.normalize();
		
			// Evaluate BRDF
			Spectrum kr = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, r);
			
			// Cosine factor
			float cosine = hitRecord.normal.dot(r);
			
			// Shoot reflected ray
			HitRecord reflectedHit = scene.intersect(reflectedRay);
			Spectrum reflected = shadeRecursive(reflectedHit, depth+1);
			l.r += reflected.r * kr.r * cosine;
			l.g += reflected.g * kr.g * cosine;
			l.b += reflected.b * kr.b * cosine;
			
		}
		
		// Mirror refraction
		if(material.mirrorRefract())
		{
			// Get refracted direction
			Vector3f r = material.sampleRefract(hitRecord, null);
			
			// In case of total internal reflection no refraction occurs
			if(r != null)
			{
				// Make refracted ray
				Vector3f o = new Vector3f(hitRecord.position);
				o.scaleAdd(0.0001f, r, o);
				Ray refractedRay = new Ray(o, r);
				r.normalize();

				// Shade
				Spectrum kt = material.evaluateRefractiveBRDF(hitRecord, hitRecord.wIn, r);

				// Cosine factor
				float cosine = Math.abs(hitRecord.normal.dot(r));

				// Shoot reflected ray
				HitRecord refractedHit = scene.intersect(refractedRay);
				Spectrum refracted = shadeRecursive(refractedHit, depth+1);
				l.r += refracted.r * kt.r * cosine;
				l.g += refracted.g * kt.g * cosine;
				l.b += refracted.b * kt.b * cosine;
			}			
		}
		
		return l;
	}

	public void prepareSamples(int n) 
	{
	}
	
}

