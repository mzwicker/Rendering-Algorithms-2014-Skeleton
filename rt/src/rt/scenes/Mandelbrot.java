package rt.scenes;

import rt.*;

/**
 * Renders a Mandelbrot set. An educational example to show how to use the {@link rt} framework.
 */
public final class Mandelbrot extends Scene {

	public Mandelbrot()
	{
		// Output file name
		outputFilename = new String("Mandelbrot");
		
		// Width and height of output image in pixels
		width = 1024;
		height = 1024;
		
		// Number of samples per pixel
		SPP = 16;
		
		// Specify the camera, film, and tonemapper to use
		camera = new DummyCamera(width, height);
		film = new Film(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify the integrator and sampler to use
		integratorFactory = new MandelbrotIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();
	}
	
}
