package rt.scenes;

import rt.*;
import javax.vecmath.Vector3f;

public class CSGCubeSphere {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	public CSGCubeSphere()
	{	
		outputFileName = new String("CSGCubeSphereScene.png");
		
		// Specify integrator to be used
		integratorFactory = new PathTracingIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new JitteredSamplerFactory();
		samplerFactory.setNumberOfSamples(128);
		
		// Make camera and film
		Vector3f eye = new Vector3f(2.f,2.f,4.f);
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
				
		Vector3f normal = new Vector3f(0.f, 1.f, 0.f);
		float d = 1.f;
		Plane plane = new Plane(normal, d);
		plane.material = new BlinnMaterial(new Spectrum(0.f, 0.8f, 0.8f));
		objects.add(plane);		
		
		// A CSG object
		CSGPlane plane1 = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), -1.f);
		plane1.material = new BlinnMaterial(new Spectrum(1.0f, 0.f, 0.f));
		CSGPlane plane2 = new CSGPlane(new Vector3f(1.f, 0.f, 0.f), -1.f);
		CSGPlane plane3 = new CSGPlane(new Vector3f(-1.f, 0.f, 0.f), -1.f);
		CSGPlane plane4 = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), -1.f);
		CSGPlane plane5 = new CSGPlane(new Vector3f(0.f, -1.f, 0.f), -1.f);
		CSGPlane plane6 = new CSGPlane(new Vector3f(0.f, 0.f, -1.f), -1.f);
		CSGSphere csgsphere = new CSGSphere(new Vector3f(0.f, 0.f, 0.f), 1.2f);
		csgsphere.material = new BlinnMaterial(new Spectrum(1.0f, 0.f, 0.f));
		CSGSphere csgsphere2 = new CSGSphere(new Vector3f(0.f, 0.f, 0.f), 1.3f);
		csgsphere.material = new BlinnMaterial(new Spectrum(0.0f, 1.f, 0.f));
		
		CSGNode csgnode = new CSGNode(plane1,plane2, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane3, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane4, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane5, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, plane6, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, csgsphere, CSGSolid.OperationType.SUBTRACT);
		csgnode = new CSGNode(csgnode, csgsphere2, CSGSolid.OperationType.INTERSECT);
		objects.add(csgnode);
		
		// List of lights
		lights = new LightList();
		
		Rectangle rectangle = new Rectangle(new Vector3f(0.f, 4.f, -0.5f), new Vector3f(1.f, 0.0f, 0.0f), new Vector3f(.0f, 0.0f, 1.f));
		RectangleLight rectangleLight = new RectangleLight(rectangle, new Spectrum(15.f, 15.f, 15.f));
		lights.add(rectangleLight);
//		objects.add(rectangleLight);
	}
}
