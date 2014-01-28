package rt.scenes;

import rt.*;
import javax.vecmath.Vector3f;

public class Assignment1_Basic {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	/**
	 * Timing:  0.6 sec on 8 core Xeon 2.5GHz
	 */
	public Assignment1_Basic()
	{	
		outputFileName = new String("Assignment1_Basic.png");
		
		// Specify integrator to be used
		integratorFactory = new WhittedIntegratorFactory();
		
		// Specify pixel sampler to be used
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
		
		// List of objects
		objects = new IntersectableList();	
		
		Vector3f center = new Vector3f(0.f,0.f,0.f);
		float radius = 0.2f;
		Sphere sphere = new Sphere(center, radius);
		Spectrum kd = new Spectrum(0.8f, 0.8f, 0.8f);
		sphere.material = new BlinnMaterial(kd);
		objects.add(sphere);
		
		Vector3f normal = new Vector3f(0.f, 1.f, 0.f);
		float d = 1.f;
		Plane plane = new Plane(normal, d);
		kd = new Spectrum(0.f, 0.8f, 0.8f);
		plane.material = new BlinnMaterial(kd);
		objects.add(plane);		
		
		normal = new Vector3f(0.f, 0.f, 1.f);
		d = 1.f;
		plane = new Plane(normal, d);
		kd = new Spectrum(0.3f, 0.8f, 0.8f);
		plane.material = new BlinnMaterial(kd);
		objects.add(plane);
		
		normal = new Vector3f(-1.f, 0.f, 0.f);
		d = 1.f;
		plane = new Plane(normal, d);
		kd = new Spectrum(1.f, 0.8f, 0.8f);
		plane.material = new BlinnMaterial(kd);
		objects.add(plane);
		
		normal = new Vector3f(1.f, 0.f, 0.f);
		d = 1.f;
		plane = new Plane(normal, d);
		kd = new Spectrum(0.f, 0.8f, 0.0f);
		plane.material = new BlinnMaterial(kd);
		objects.add(plane);
		
		normal = new Vector3f(0.f, -1.f, 0.f);
		d = 1.f;
		plane = new Plane(normal, d);
		kd = new Spectrum(0.8f, 0.8f, 0.8f);
		plane.material = new BlinnMaterial(kd);
		objects.add(plane);
		
		Vector3f position = new Vector3f(0.f,0.8f,0.8f);
		Spectrum strength = new Spectrum(1.f, 1.f, 1.f);
		PointLight light = new PointLight(position, strength);
		
		// List of lights
		lights = new LightList();
		lights.add(light);		
	}
}
