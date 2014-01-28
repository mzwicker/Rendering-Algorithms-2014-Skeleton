package rt;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Vector3f;

public class PathTracingIntegrator implements Integrator {

	private Intersectable scene;
	private LightList lights;
	private EnvironmentMap envMap;
	private ArrayList<Iterator<float[]>> sampleItr;
	private int maxBounces;
	
	enum ScatteringType
	{
		DIFFUSE, REFLECT, REFRACT
	}
	
	public PathTracingIntegrator(Intersectable scene, LightList lights, EnvironmentMap envMap)
	{
		this.scene = scene;
		this.lights = lights;
		this.envMap = envMap;
		maxBounces = 4;
	}
	
	public Spectrum integrate(Ray r) 
	{
		Spectrum l = new Spectrum();
		Spectrum c = new Spectrum();
		Spectrum alpha = new Spectrum(1,1,1);
		Vector3f d;
		Material material;
		
		HitRecord hitRecord = scene.intersect(r);			
		int i = 0;
		while(i<maxBounces && hitRecord!=null)
		{
			material = hitRecord.material;
			hitRecord.wIn.normalize();

			// Determine scattering type to be evaluated
			float p = (float)Math.random();		
			float diffusePdf = material.getDiffuseContribution();
			float reflectPdf = material.getReflectContribution();
			float refractPdf = material.getRefractContribution();
			ScatteringType type;
			float typePdf;

			if(p<=diffusePdf)
			{
				type = ScatteringType.DIFFUSE;
				typePdf = diffusePdf;
			} else if(p<=diffusePdf+reflectPdf)
			{
				type = ScatteringType.REFLECT;
				typePdf = reflectPdf;
			} else
			{
				type = ScatteringType.REFRACT;
				typePdf = refractPdf;
			}
				
			float[] sample = sampleItr.get(i).next();
						
			// Add contribution of light source by shooting shadow ray
			l.r = l.g = l.b = 0;
				
			if(type == ScatteringType.DIFFUSE)
			{
				if(lights.size()>0)
				{
					// Sample a point on a light source
				
					// Pick light randomly
					LightSource light = lights.get((int)Math.max(0, Math.round( Math.random()*lights.size()-0.5000001f )));
					float lightPdf = 1.f/lights.size();
	
					// Avoid shooting shadow ray towards light itself
					if(hitRecord.intersectable != (Intersectable)light) 
					{
						// Sample light source
						LightSource.LightGeometry lightGeometry = light.sampleGeometry(sample);
				
						// Make shadow ray
						d = new Vector3f(lightGeometry.position);
						d.sub(hitRecord.position);
						Vector3f o = new Vector3f(hitRecord.position);
						o.scaleAdd(0.0001f, d, o);
						Ray shadowRay = new Ray(o, d);
						d.normalize();
	
						// Cosine of shadow ray and surface normal
						float cosine = hitRecord.normal.dot(d);
	
						// Shoot shadow ray only if cosine is positive
						HitRecord shadowHit = null;
						if(cosine > 0)
							shadowHit = scene.intersect(shadowRay);
					
						// If shadow ray is not back facing or blocked, shade
						if(cosine>0 && (shadowHit==null || shadowHit.t>0.999f))
						{						
							// Evaluate BRDF
							Spectrum f = material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
													
							// Get light
							l = light.sampleSpectrum(sample);
						
							// Compute geometry term
							Vector3f tmp = new Vector3f();
							tmp.sub(lightGeometry.position,hitRecord.position);
							float g = tmp.length();
							
							if(lightGeometry.normal != null)
							{
								// Area light source with a normal
								tmp = new Vector3f(lightGeometry.normal);
								shadowRay.direction.normalize();
								if(tmp.dot(shadowRay.direction)>0)
									g = 0;
								else
									g = Math.abs(tmp.dot(shadowRay.direction)) / (g*g);
								
								// Divide geometry term by pdf. Note: pdf = 1/(area of light), i.e. 1/pdf = (area of light)
								g = g*light.surfaceArea();
							} else
							{
								// Point light source
								g = 1/(g*g);
							}
													
							// Final contribution: light * shading coefficients * cosine * geometry term / (pdf for light sample * pdf for scattering type)
							l.r = l.r * f.r * cosine * g / (typePdf * lightPdf);
							l.g = l.g * f.g * cosine * g / (typePdf * lightPdf);
							l.b = l.b * f.b * cosine * g / (typePdf * lightPdf);						
						}
					}
				}
			} else 
			{
				// Sample a direction for the shadow ray
				
				if(type == ScatteringType.REFLECT)
					d = material.sampleReflect(hitRecord, sample);
				else
					d = material.sampleRefract(hitRecord, sample);
				
				// If total internal reflection occurs we terminate the path
				if(d == null) break;

				// Length of returned direction d is density
				float density = d.length();
				
				// Make shadow ray
				Vector3f o = new Vector3f(hitRecord.position);
				o.scaleAdd(0.0001f, d, o);
				Ray shadowRay = new Ray(o, d);
				d.normalize();
				
				// Shoot shadow ray
				HitRecord shadowHit = scene.intersect(shadowRay);
			
				// If we hit something that emits light
				if(shadowHit!=null && shadowHit.spectrum != null)
				{
					// The emitted light at the hit point
					l = shadowHit.spectrum;
					
					// Shading
					Spectrum f;
					if(type == ScatteringType.REFLECT)
						f = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
					else
						f = material.evaluateRefractiveBRDF(hitRecord, hitRecord.wIn, d);
											
					// Cosine term
					float cosine = hitRecord.normal.dot(d);
					
					// Final contribution: emitted light * shading coefficient * cosine / (pdf of ray * pdf of scattering type)
					l.r = l.r * f.r * cosine / (density * typePdf);
					l.g = l.g * f.g * cosine / (density * typePdf);
					l.b = l.b * f.b * cosine / (density * typePdf);					
				}

			}
						 
			// Only indirect illumination
//			if(i>=1)
			{
				c.r = c.r + alpha.r * l.r;
				c.g = c.g + alpha.g * l.g;
				c.b = c.b + alpha.b * l.b;
			}
			
			// Russian roulette
			float q;
			if(i>1)
			{
				q = (float)Math.random();
				if(q<0.5) break;
				q = 0.5f;
			} else
			{
				q = 0.f;
			}
				
			// Sample new direction		
			if(type == ScatteringType.DIFFUSE)
				d = material.sampleDiffuse(hitRecord, sample);
			else if(type == ScatteringType.REFLECT)
				d = material.sampleReflect(hitRecord, sample);
			else
				d = material.sampleRefract(hitRecord, sample);

			// If total internal reflection occurs we terminate the path
			if(d == null) break;
			
			// The length of d needs to encode the sample density!
			float density = d.length();
			d.normalize();
							
			// Update alpha
			Spectrum f;
			if(type == ScatteringType.DIFFUSE)
				f = material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
			else if(type == ScatteringType.REFLECT)
				f = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
			else
				f = material.evaluateRefractiveBRDF(hitRecord, hitRecord.wIn, d);
			
			// Cosine term
			float cosine = Math.abs(hitRecord.normal.dot(d));
			
			alpha.r = alpha.r * f.r * cosine / ((1-q) * density * typePdf);
			alpha.g = alpha.g * f.g * cosine / ((1-q) * density * typePdf);
			alpha.b = alpha.b * f.b * cosine / ((1-q) * density * typePdf);
			
			// Shoot next ray segment
			r.origin.scaleAdd(0.0001f, d, hitRecord.position);
			r.direction = d;
			hitRecord = scene.intersect(r);

			i++;
		} 
		
		// Add environment illumination
		if(hitRecord==null && envMap!=null)
		{
			Spectrum env = envMap.lookUp(r.direction);
			c.r = c.r + alpha.r * env.r;
			c.g = c.g + alpha.g * env.g;
			c.b = c.b + alpha.b * env.b;
		}

		return c;
	}

	public void prepareSamples(int n) 
	{
		sampleItr = new ArrayList<Iterator<float[]>>(maxBounces);
		for(int i=0; i<maxBounces; i++)
		{
			Sampler sampler = new JitteredSampler(n, 2);
			sampler.makeSamples();
			sampleItr.add(i, sampler.getIterator());
		}
	}
}
