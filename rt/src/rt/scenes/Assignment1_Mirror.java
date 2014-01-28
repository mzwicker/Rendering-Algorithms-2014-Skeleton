package rt.scenes;

import rt.*;
import javax.vecmath.Vector3f;

public class Assignment1_Mirror {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	/**
	 * Timing: 0.9 sec on 8 core Xeon 2.5GHz
	 */
	public Assignment1_Mirror()
	{	
		outputFileName = new String("Assignment1_Mirror.png");
		
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
		
		Sphere sphere = new Sphere(new Vector3f(0.f,0.f,0.f), .2f);
		// BlinnMaterial(kd, ks, shininess)
		sphere.material = new BlinnMaterial(new Spectrum(0.8f, 0.f, 0.f), new Spectrum(.2f, .2f, .2f), 30.f);
		objects.add(sphere);
		
		sphere = new Sphere(new Vector3f(0.4f,0.2f,-0.3f), .3f);
		sphere.material = new MirrorMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(sphere);
		
		Plane plane = new Plane(new Vector3f(0.f, 1.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(0.f, 0.8f, 0.8f));
		objects.add(plane);		
		
		plane = new Plane(new Vector3f(0.f, 0.f, 1.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(0.3f, 0.8f, 0.8f));
		objects.add(plane);
		
		plane = new Plane(new Vector3f(-1.f, 0.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(1.f, 0.8f, 0.8f));
		objects.add(plane);
		
		plane = new Plane(new Vector3f(1.f, 0.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(0.f, 0.8f, 0.0f));
		objects.add(plane);
		
		plane = new Plane(new Vector3f(0.f, -1.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(plane);
				
		// List of lights
		lights = new LightList();
		
		PointLight light = new PointLight(new Vector3f(0.f,0.8f,0.8f), new Spectrum(2.f, 2.f, 2.f));
		lights.add(light);
		
		light = new PointLight(new Vector3f(-0.8f,0.2f,0.0f), new Spectrum(1.5f, 1.5f, 1.5f));
		lights.add(light);		
	}
}
