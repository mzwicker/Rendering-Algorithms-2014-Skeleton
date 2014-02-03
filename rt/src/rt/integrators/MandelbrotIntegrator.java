package rt.integrators;

import rt.Integrator;
import rt.Ray;
import rt.Sampler;
import rt.Spectrum;

/**
 * An educational integrator to demonstrate the {@link rt} framework. 
 * Computes a Mandelbrot set.
 */
public class MandelbrotIntegrator implements Integrator {

	/**
	 * Compute Mandelbrot set. See <a href="http://en.wikipedia.org/wiki/Mandelbrot_set">.  
	 */
	public Spectrum integrate(Ray r)
	{
		// Assumes ray x and y direction is in range [0,1] across the image.
		float x0 = r.direction.x*3.5f-2.5f;
		float y0 = r.direction.y*2.f-1.f;

		float x = 0.f;
		float y = 0.f;
		int iteration = 0;
		int max_iteration = 100;
		
		while ( x*x + y*y < 2*2  &&  iteration < max_iteration )
		{
			float xtemp = x*x - y*y + x0;
			y = 2*x*y + y0;
			x = xtemp;
			iteration = iteration + 1;
		}	
		
		return new Spectrum((float)iteration/(float)max_iteration, (float)iteration/(float)max_iteration, (float)iteration/(float)max_iteration);
	}
	
	public float[][] makePixelSamples(Sampler sampler, int n)
	{
		return sampler.makeSamples(n, 2);
	}
}
