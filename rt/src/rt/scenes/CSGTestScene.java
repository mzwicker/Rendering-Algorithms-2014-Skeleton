package rt.scenes;

import rt.*;
import javax.vecmath.Vector3f;

public class CSGTestScene {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	public CSGTestScene()
	{	
		outputFileName = new String("CSGTestScene.png");
		
		// Specify integrator to be used
		integratorFactory = new WhittedIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new OneSamplerFactory();
		
		// Make camera and film
		Vector3f eye = new Vector3f(2.f,2.f,4.f);
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
						
		// A CSG object
		CSGPlane plane1 = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), -1.f);
		plane1.material = new BlinnMaterial(new Spectrum(1.0f, 0.f, 0.f));
		CSGPlane plane2 = new CSGPlane(new Vector3f(1.f, 0.f, 0.f), -1.f);
		CSGPlane plane3 = new CSGPlane(new Vector3f(-1.f, 0.f, 0.f), -1.f);
		CSGPlane plane4 = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), -1.f);
		CSGPlane plane5 = new CSGPlane(new Vector3f(0.f, -1.f, 0.f), -1.f);
		CSGPlane plane6 = new CSGPlane(new Vector3f(0.f, 0.f, -1.f), -1.f);
		
		CSGNode csgnode = new CSGNode(plane1,plane2, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane3, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane4, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane5, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane6, CSGSolid.OperationType.INTERSECT);
		objects.add(csgnode);
		
		// List of lights
		lights = new LightList();
		
		PointLight light = new PointLight(new Vector3f(eye), new Spectrum(10.f, 10.f, 10.f));
		lights.add(light);
//		objects.add(rectangleLight);
	}
}
