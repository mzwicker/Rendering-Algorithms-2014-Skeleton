package rt;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Vector3f;

public class DirectIlluminationIntegrator implements Integrator {

	private Intersectable scene;
	private ArrayList<LightSource> lights;
	private Iterator<float[]> sampleItr;
	
	public DirectIlluminationIntegrator(Intersectable scene, ArrayList<LightSource> lights)
	{
		this.scene = scene;
		this.lights = lights;
	}
	
	public Spectrum integrate(Ray r) 
	{		
		HitRecord hitRecord = scene.intersect(r);
		Spectrum s = new Spectrum(); 
		Spectrum alpha = new Spectrum(1.f, 1.f, 1.f);
		
		float[] sample = sampleItr.next();
		
		while(hitRecord!=null)
		{
			Material material = hitRecord.material;
						
			// Get surface emission, if any
			if(hitRecord.spectrum != null)
			{
				s.r += alpha.r * hitRecord.spectrum.r;
				s.g += alpha.g * hitRecord.spectrum.g;
				s.b += alpha.b * hitRecord.spectrum.b;
			}

			// Shadow ray, no need to do this if surface is a mirror
			if(!material.mirrorReflect())
			{
				Iterator<LightSource> lightItr = lights.iterator();
				while(lightItr.hasNext())
				{
					// Pick light
					LightSource light = lightItr.next();
	
					// Avoid shooting shadow ray towards light itself
					if(hitRecord.intersectable != (Intersectable)light) 
					{
						LightSource.LightGeometry lightGeometry = light.sampleGeometry(sample);
						
						// Make shadow ray
						Vector3f d = new Vector3f(lightGeometry.position);
						d.sub(hitRecord.position);
						Vector3f o = new Vector3f(hitRecord.position);
						o.scaleAdd(0.0001f, d, o);
						Ray shadowRay = new Ray(o, d);
						d.normalize();
						
						// Cosine factor
						float c = d.dot(hitRecord.normal);
	
						// Shoot shadow ray, if light is in front of surface
						HitRecord shadowHit = null;
						if(c>0)
							shadowHit = scene.intersect(shadowRay);
						
						// If shadow ray is front facing and not blocked, shade
						if(c>0 && (shadowHit==null || shadowHit.t>0.999f))
						{
							hitRecord.wIn.normalize();
							
							// Diffuse shading
							Spectrum kd = material.evaluateDiffuseBRDF(hitRecord, hitRecord.wIn, d);
							// Specular shading
							Spectrum ks;
							if(!material.mirrorReflect()) 
								ks = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
							else
								ks = new Spectrum();
													
							Spectrum l = light.sampleSpectrum(sample);
						
							// Compute geometry term
							Vector3f tmp = new Vector3f();
							tmp.sub(lightGeometry.position,hitRecord.position);
							float t = tmp.length();
							
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
			
							// Final shading: alpha * light * shading coefficients * cosine factor * (geometry term / pdf)
							s.r += alpha.r * l.r * (kd.r+ks.r) * c * t;
							s.g += alpha.g * l.g * (kd.g+ks.g) * c * t;
							s.b += alpha.b * l.b * (kd.b+ks.b) * c * t;
						} // Shadow ray not blocked
					} // Loop over light samples
				} // Loop over lights
			} // Material is not a mirror
			
			// Follow reflection or refraction, if necessary
			float pReflect, pRefract;
			if(material.mirrorReflect() && !material.mirrorRefract())
			{
				pReflect = 1;
				pRefract = 0;
			} else if(material.mirrorReflect() && material.mirrorRefract())
			{
				pReflect = .5f;
				pRefract = .5f;
			} else
			{
				pReflect = 0.f;
				pRefract = 0.f;
			}
			
			Vector3f d;
			Spectrum f = null;
			float p = 0.f;
			float t = (float)Math.random();
			
			if(t<pReflect)
			{
				// Follow reflection
				d = material.sampleReflect(hitRecord, sample);
				f = material.evaluateSpecularBRDF(hitRecord, hitRecord.wIn, d);
				p = pReflect;
			} else if(t<pReflect+pRefract)
			{
				// Follow refraction
				d = material.sampleRefract(hitRecord, sample);
				if(d!=null)
					f = material.evaluateRefractiveBRDF(hitRecord, hitRecord.wIn, d);
				p = pRefract;
			} else
			{
				d = null;
			}
			
			if(d!=null)
			{
				// The length of d needs to encode the sample density!
				float density = d.length();
				d.normalize();
								
				// Cosine term
				float cosine = Math.abs(hitRecord.normal.dot(d));
				
				// Update alpha
				alpha.r = alpha.r * f.r * cosine / (density * p);
				alpha.g = alpha.g * f.g * cosine / (density * p);
				alpha.b = alpha.b * f.b * cosine / (density * p);
				
				// Shoot next ray segment
				r.origin.scaleAdd(0.0001f, d, hitRecord.position);
				r.direction = d;
				hitRecord = scene.intersect(r);
			} else
			{
				hitRecord = null;
			}			
		} // while(hitRecord!=null)
		
		return s;
	}
	
	public void prepareSamples(int n)
	{
		Sampler sampler = new JitteredSampler(n, 2);
		sampler.makeSamples();
		sampleItr = sampler.getIterator();
	}
}
