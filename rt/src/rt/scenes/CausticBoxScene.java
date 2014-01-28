package rt.scenes;

import java.io.IOException;
import rt.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class CausticBoxScene {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	/**
	 * Timing: 22 sec on 8 core Xeon 2.5GHz
	 */
	public CausticBoxScene()
	{	
		outputFileName = new String("CausticBoxScene.png");
		
		// Specify integrator to be used
//		integratorFactory = new DirectIlluminationIntegratorFactory();
		integratorFactory = new PhotonMapIntegratorFactory(true, true, true, 200000, 25, .1f, .01f);
//		integratorFactory = new PathTracingIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new JitteredSamplerFactory();
		samplerFactory.setNumberOfSamples(256);
		
		// Make camera and film
		Vector3f eye = new Vector3f(0.f,0.f,2.f);
		Vector3f lookAt = new Vector3f(0.f,0.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 128;
		int height = 128;
		float aspect = (float)width/(float)height;
		camera = new Camera(eye, lookAt, up, fov, aspect, width, height);
		film = new Film(width, height);						
		
		// List of objects
		objects = new IntersectableList();	
				
		// Box
		Plane plane = new Plane(new Vector3f(0.f, 1.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(.5f, 0.5f, 0.5f));
		objects.add(plane);		
		
		plane = new Plane(new Vector3f(0.f, 0.f, 1.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(.5f, .5f, .5f));
		objects.add(plane);
		
		plane = new Plane(new Vector3f(-1.f, 0.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(.0f, .5f, .0f));
		objects.add(plane);
		
		plane = new Plane(new Vector3f(1.f, 0.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(.5f, .0f, .0f));
		objects.add(plane);
		
		plane = new Plane(new Vector3f(0.f, -1.f, 0.f), 1.f);
		plane.material = new BlinnMaterial(new Spectrum(.5f, .5f, .5f));
		objects.add(plane);
				
		// Sphere
		Sphere sphere = new Sphere(new Vector3f(.4f, -.7f, 0.f), .3f);
		sphere.material = new RefractiveMaterial(1.5f);
		objects.add(sphere);
		
		// List of lights
		lights = new LightList();
		
		Rectangle rectangle = new Rectangle(new Vector3f(-0.125f, 0.9f, 0.125f), new Vector3f(0.f, 0.f, -.25f), new Vector3f(.25f, 0.f, 0.f));
		RectangleLight light = new RectangleLight(rectangle, new Spectrum(21.f, 21.f, 21.f));
		lights.add(light);
		objects.add(light);		
	}
}
