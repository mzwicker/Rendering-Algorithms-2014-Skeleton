package rt.scenes;

import rt.*;

/**
 * Ray traces a cube. An educational example to show how to use the {@link rt} framework.
 */
public class Scene0 extends Scene {
		
	public Scene0()
	{
		// Output file name
		outputFilename = new String("Scene0");
		
		// Image width and height in pixels
		width = 512;
		height = 512;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		camera = new FixedCamera(width, height);
		film = new Film(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new DebugIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();
		
		// Define the root object (an intersectable) of the scene
		root = new CSGCube();
	}
	
}
