package rt.scenes;

import java.io.IOException;
import javax.vecmath.Vector3f;
import rt.*;

public class BoxScene {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	public BoxScene()
	{	
		outputFileName = new String("BoxScene.png");
		
		// Specify integrator to be used
		integratorFactory = new PathTracingIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new JitteredSamplerFactory();
		samplerFactory.setNumberOfSamples(512);
		
		// Make camera and film
		Vector3f eye = new Vector3f(-3.f,0.f,4.f);
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
		
		Sphere sphere = new Sphere(new Vector3f(-.5f,-.2f,1.f), .5f);
		sphere.material = new MirrorMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
//		sphere.material = new RefractiveMaterial(1.5f);
		sphere.material = new BlinnMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(sphere);
		
//		sphere = new Sphere(new Vector3f(.8f,-.5f,-.3f), .5f);
//		sphere.material = new DiffuseMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
//		objects.add(sphere);
		
		Rectangle rectangle = new Rectangle(new Vector3f(2.f, -.75f, 2.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new BlinnMaterial(new Spectrum(1.f, 0.f, 0.f));
		objects.add(rectangle);
	
		rectangle = new Rectangle(new Vector3f(-2.f, -.75f, 2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		objects.add(rectangle);

		rectangle = new Rectangle(new Vector3f(-2.f, 3.25f, 2.f), new Vector3f(0.f, 0.f, -4.f), new Vector3f(4.f, 0.f, 0.f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Vector3f(-2.f, -.75f, -2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 4.f, 0.f));
//		rectangle.material = new MirrorMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		// Add objects
/*		Timer timer = new Timer();
		Mesh mesh;
		BSPAccelerator accelerator;
		try
		{
			
			mesh = ObjReader.read("obj\\cow.obj", 1.f);
			timer.reset();
			accelerator = new BSPAccelerator(mesh);
			System.out.printf("Accelerator computed in %d ms.\n", timer.timeElapsed());
		} catch(IOException e) 
		{
			System.out.printf("Could not read .obj file\n");
			return;
		}
		objects.add(accelerator); */	
	
		rectangle = new Rectangle(new Vector3f(-0.25f, 3.f, 0.25f), new Vector3f(0.f, 0.f, -.5f), new Vector3f(.5f, 0.f, 0.f));
		RectangleLight light = new RectangleLight(rectangle, new Spectrum(40.f, 40.f, 40.f));
		objects.add(light);
		
		PointLight light2 = new PointLight(new Vector3f(-4.f,0.f,1.f), new Spectrum(5.f, 5.f, 5.f));
		
		// List of lights
		lights = new LightList();
		lights.add(light);
	//	lights.add(light2);		
	}
}
