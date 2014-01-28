package rt.scenes;

import java.io.IOException;

import javax.vecmath.*;
import rt.*;

public class DragonEnvMap {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public String outputFileName;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public EnvironmentMap envMap;
	
	public DragonEnvMap()
	{
		outputFileName = new String("..\\images\\DragonEnvMap.png");
		
		// Specify integrator to be used
		integratorFactory = new PathTracingIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new JitteredSamplerFactory();
		samplerFactory.setNumberOfSamples(512);
		
		// Make camera and film
		Vector3f eye = new Vector3f(-1.f,0.f,3.f);
		Vector3f lookAt = new Vector3f(0.f,0.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 512;
		int height = 512;
		float aspect = (float)width/(float)height;
		camera = new Camera(eye, lookAt, up, fov, aspect, width, height);
		film = new Film(width, height);						
	
		// Environment map
		envMap = new EnvironmentMap("..\\images\\rnl_probe.pfm");
		
		// List of objects
		objects = new IntersectableList();
		
		// Add objects
		Timer timer = new Timer();
		Mesh mesh;
		BSPAccelerator accelerator;
		try
		{
			
			mesh = ObjReader.read("..\\obj\\cow.obj", 1.f);
			timer.reset();
			accelerator = new BSPAccelerator(mesh);
			System.out.printf("Accelerator computed in %d ms.\n", timer.timeElapsed());
		} catch(IOException e) 
		{
			System.out.printf("Could not read .obj file\n");
			return;
		}
		Matrix4f t = new Matrix4f();
		t.setIdentity();
//		t.setRotation(new AxisAngle4f(1.f, 0.f, 0.f, -(float)Math.PI/2.f));
		Instance instance = new Instance(accelerator, t);
		objects.add(instance);	
				
/*		Rectangle rectangle = new Rectangle(new Vector3f(2.f, -.75f, 2.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new BlinnMaterial(new Spectrum(1.f, 0.f, 0.f));
		objects.add(rectangle);
	
		rectangle = new Rectangle(new Vector3f(-2.f, -.75f, 2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		objects.add(rectangle);
*/				
		// Light source
		RectangleLight light = new RectangleLight(new Rectangle(new Vector3f(-0.25f, 2.f, 0.25f), new Vector3f(0.f, 0.f, -.5f), new Vector3f(.5f, 0.f, 0.f)), new Spectrum(80.f, 80.f, 80.f));
//		objects.add(light);
	
		// List of lights
		lights = new LightList();
//		lights.add(light);
	}
}
