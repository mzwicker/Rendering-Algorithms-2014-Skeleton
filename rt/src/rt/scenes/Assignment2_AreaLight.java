package rt.scenes;

import java.io.IOException;

import rt.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class Assignment2_AreaLight {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	public Assignment2_AreaLight()
	{	
		outputFileName = new String("Assignment2_AreaLight.png");
		
		// Specify integrator to be used. Note that these scene looks a bit different
		// using the photon map integrator. This seems to be because of the geometry
		// of the teapot, where the photon map radiance estimate is not very precise
		// because of the fine geometry. Also, the path tracer interpolates normals
		// across triangle faces, whereas photon mapping implicitly doesn't!
//		integratorFactory = new DirectIlluminationIntegratorFactory();
		integratorFactory = new PathTracingIntegratorFactory();
//		integratorFactory = new PhotonMapIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new JitteredSamplerFactory();
		samplerFactory.setNumberOfSamples(64);
		
		// Make camera and film
		Vector3f eye = new Vector3f(0.f,0.f,2.f);
		Vector3f lookAt = new Vector3f(0.f,0.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 256;
		int height = 256;
		float aspect = (float)width/(float)height;
		camera = new Camera(eye, lookAt, up, fov, aspect, width, height);
		film = new Film(width, height);						
		
		// List of objects
		objects = new IntersectableList();	
				
		// Box
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
		
		// Add objects
		Mesh mesh;
		BSPAccelerator accelerator;
		try
		{
			
			mesh = ObjReader.read("..\\obj\\teapot.obj", 1.f);
			mesh.material = new BlinnMaterial(new Spectrum(.5f, .5f, .5f));
			accelerator = new BSPAccelerator(mesh);
		} catch(IOException e) 
		{
			System.out.printf("Could not read .obj file\n");
			return;
		}
		Matrix4f t = new Matrix4f();
		t.setIdentity();
		
		// Instance one
		t.setScale(0.5f);
		t.setTranslation(new Vector3f(0.f, -0.25f, 0.f));
		Instance instance = new Instance(accelerator, t);
		objects.add(instance);	
		
		// Instance two
		t.setScale(0.5f);
		t.setTranslation(new Vector3f(0.f, 0.25f, 0.f));
		instance = new Instance(accelerator, t);
		objects.add(instance);
						
		// List of lights
		lights = new LightList();
		
		Rectangle rectangle = new Rectangle(new Vector3f(-0.25f, 0.9f, 0.25f), new Vector3f(0.f, 0.f, -.5f), new Vector3f(.5f, 0.f, 0.f));
		RectangleLight light = new RectangleLight(rectangle, new Spectrum(21.f, 21.f, 21.f));
		lights.add(light);
		objects.add(light);
		
		rectangle = new Rectangle(new Vector3f(-0.9f, -0.1f, -0.1f), new Vector3f(0.f, 0.2f, 0.0f), new Vector3f(.0f, 0.0f, 0.2f));
		light = new RectangleLight(rectangle, new Spectrum(5.f, 5.f, 5.f));
		lights.add(light);	
		objects.add(light);
	}
}
