package rt;

import java.util.Iterator;
import javax.vecmath.*;
import rt.LightSource.LightGeometry;

/**
 * Integrator for Whitted style ray tracing. This is a basic version that needs to be extended!
 */
public class WhittedIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	
	public WhittedIntegrator(Scene scene)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
	}

	/**
	 * Basic integrator that simply iterates over the light sources and accumulates
	 * their contributions. No shadow testing, reflection, refraction, or 
	 * area light sources, etc. supported.
	 */
	public Spectrum integrate(Ray r) {

		HitRecord hitRecord = root.intersect(r);
		if(hitRecord != null)
		{
			Spectrum outgoing = new Spectrum(0.f, 0.f, 0.f);
			Spectrum brdfValue;
			
			// Iterate over all light sources
			Iterator<LightSource> it = lightList.iterator();
			while(it.hasNext())
			{
				// Make direction from hit point to light source position
				LightGeometry lightGeo = it.next().getGeometry(null);
				Vector3f lightPos = lightGeo.position;
				Vector3f lightDir = new Vector3f(lightPos);
				lightDir.sub(hitRecord.position);
				float d = lightDir.length();
				lightDir.normalize();
				
				// Evaluate the BRDF
				brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);
				
				// Multiply with cosine of surface normal and incident direction
				float ndotl = hitRecord.normal.dot(lightDir);
				ndotl = Math.max(ndotl, 0.f);
				brdfValue.scale(ndotl);
				
				// Multiply with 1/(squared distance), only correct like this 
				// for point lights (not area lights)!
				brdfValue.scale(1.f/(d*d));
				
				// Accumulate
				outgoing.add(brdfValue);
			}
			
			return outgoing;
		} else 
			return new Spectrum(0.f,0.f,0.f);
		
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
