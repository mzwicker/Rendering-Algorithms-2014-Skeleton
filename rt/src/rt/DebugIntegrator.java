package rt;

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

	public Spectrum integrate(Ray r) {
		HitRecord hitRecord = scene.getIntersectable().intersect(r);
		if(hitRecord != null)
			if(hitRecord.t > 0.f)
				return new Spectrum(0.f,1.f,0.f);
			else
				return new Spectrum(1.f,0.f,0.f);
		else 
			return new Spectrum(0.f,0.f,0.f);
		
		// return new Spectrum(r.direction.x/2.f+0.5f, r.direction.y/2.f+0.5f, 0.f);
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
