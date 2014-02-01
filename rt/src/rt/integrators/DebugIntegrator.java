package rt.integrators;

import rt.HitRecord;
import rt.Integrator;
import rt.Ray;
import rt.Sampler;
import rt.Scene;
import rt.Spectrum;

/**
 * Use this integrator for debugging purposes. For example, you can simply return a white spectrum
 * if the ray hits something, and black otherwise. Any other visualization of data associated
 * with a hit record may be useful. 
 */
public class DebugIntegrator implements Integrator {

	Scene scene;
	
	public DebugIntegrator(Scene scene)
	{
		this.scene = scene;
	}

	/**
	 * Return some value useful for debugging. 
	 */
	public Spectrum integrate(Ray r) {
		HitRecord hitRecord = scene.getIntersectable().intersect(r);
		
		if(hitRecord != null)
			
			if(hitRecord.t > 0.f)
				// Ok, hit point was "in front" of ray origin
				return new Spectrum(0.f,1.f,0.f);
			else
				// Weird, a hit point "behind" the ray origin was returned, 
				// this shouldn't happen in general!			
				return new Spectrum(1.f,0.f,0.f);
		else 
			return new Spectrum(0.f,0.f,0.f);
		
		// Other potential debugging visualizations
		// return new Spectrum(r.direction.x/2.f+0.5f, r.direction.y/2.f+0.5f, 0.f);
	}

	/**
	 * Make sample budget for a pixel. Since this integrator only samples the 2D 
	 * pixel area itself, the samples are 2D.
	 * 
	 * @param sampler the sampler to be usef for generating the samples
	 * @param n the desired number of samples
	 */
	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
