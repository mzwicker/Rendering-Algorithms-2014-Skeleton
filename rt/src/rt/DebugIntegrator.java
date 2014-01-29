package rt;

public class DebugIntegrator implements Integrator {

	Scene scene;
	
	public DebugIntegrator(Scene scene)
	{
		this.scene = scene;
	}
	
	public Spectrum integrate(Ray r) {
		HitRecord hitRecord = scene.getIntersectable().intersect(r);
		if(hitRecord != null)
			return new Spectrum(1.f,1.f,1.f);
		else 
			return new Spectrum(0.f,0.f,0.f);
		
		// return new Spectrum(r.direction.x/2.f+0.5f, r.direction.y/2.f+0.5f, 0.f);
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
