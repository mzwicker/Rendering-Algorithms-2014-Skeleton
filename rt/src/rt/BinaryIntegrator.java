package rt;

public class BinaryIntegrator implements Integrator {

	Intersectable scene;
	
	public BinaryIntegrator(Intersectable scene)
	{
		this.scene = scene;
	}
	
	public Spectrum integrate(Ray r) 
	{		
		HitRecord hitRecord = scene.intersect(r);
		
		if(hitRecord!=null)
		{
			return new Spectrum(1.f, 1.f, 1.f);
		} else
		{
			return new Spectrum();
		}
	}
	
	public void prepareSamples(int n)
	{
		// Does nothing
	}
}

