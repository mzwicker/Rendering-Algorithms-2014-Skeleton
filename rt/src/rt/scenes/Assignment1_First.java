package rt.scenes;

import rt.*;
import javax.vecmath.Vector3f;

public class Assignment1_First {

	// Variables accessed by the renderer
	public Camera camera;
	public Film film;
	public Intersectable objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	public Assignment1_First()
	{	
		outputFileName = new String("Assignment1_First.png");
		
		// Specify integrator and sampler to be used
		integratorFactory = new BinaryIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
		
		// Make camera and film
		Vector3f eye = new Vector3f(0.f,0.f,2.f);
		Vector3f lookAt = new Vector3f(0.f,0.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 512;
		int height = 512;
		float aspect = (float)width/(float)height;
		camera = new Camera(eye, lookAt, up, fov, aspect, width, height);
		film = new Film(width, height);						

		// A plane
		Vector3f normal = new Vector3f(0.f, 1.f, 0.f);
		float d = 1.f;
		Plane plane = new Plane(normal, d);
		Spectrum kd = new Spectrum(0.f, 0.8f, 0.8f);
		plane.material = new BlinnMaterial(kd);
		objects = plane;
				
		// List of lights
		lights = new LightList();
		
		Vector3f position = new Vector3f(0.f,0.8f,0.8f);
		Spectrum strength = new Spectrum(10.f, 10.f, 10.f);
		PointLight light = new PointLight(position, strength);

		lights.add(light);		
	}
}
