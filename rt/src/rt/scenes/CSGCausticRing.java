package rt.scenes;

import rt.*;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.IOException;

public class CSGCausticRing {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;
	public IntegratorFactory integratorFactory;
	public SamplerFactory samplerFactory;
	public String outputFileName;

	public CSGCausticRing()
	{	
		outputFileName = new String("CSGCausticRing.png");
		
		// Specify integrator to be used
//		integratorFactory = new WhittedIntegratorFactory();
		integratorFactory = new PhotonMapIntegratorFactory(true, true, true, 2000000, 49, .3f, .1f);
//		integratorFactory = new DirectIlluminationIntegratorFactory();
		
		// Specify pixel sampler to be used
		samplerFactory = new JitteredSamplerFactory();
		samplerFactory.setNumberOfSamples(36);
		
		// Make camera and film
		Vector3f eye = new Vector3f(0.f,3.f,4.f);
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
						
		// A ring using CSG
		CSGPlane plane1 = new CSGPlane(new Vector3f(0.f, 0.f, -1.f), 0.f);
		CSGPlane plane2 = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), -.6f);
		CSGCylinder cylinder1 = new CSGCylinder(1.5f);
		cylinder1.material = new MirrorMaterial(new Spectrum(1.f, 1.f, 1.f));
		CSGCylinder cylinder2 = new CSGCylinder(1.3f);
		cylinder2.material = new MirrorMaterial(new Spectrum(1.f, 1.f, 1.f));
		
		CSGNode csgnode = new CSGNode(plane1,plane2, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, cylinder1, CSGSolid.OperationType.INTERSECT);
		csgnode = new CSGNode(csgnode, cylinder2, CSGSolid.OperationType.SUBTRACT);
		
		// Transform it to lie on the ground plane
		Matrix4f trafo = new Matrix4f();
		trafo.setIdentity();
		trafo.rotX(-(float)Math.PI/2.f);
		Instance ring = new Instance(csgnode, trafo);
		objects.add(ring);
		
		// Textured ground plane
		try {
			Rectangle plane = new Rectangle(new Vector3f(-4.f, 0.f, 4.f), new Vector3f(8.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -8.f));
			BlinnMaterial wood = new BlinnMaterial();
			wood.kdTexture = new Texture("..\\textures\\light hardwood vinyl 512.jpg");
			plane.material = wood;
			objects.add(plane);
		} catch(IOException e) {}
		
		// List of lights
		lights = new LightList();
		
		Rectangle rectangle = new Rectangle(new Vector3f(-.5f, 1.5f, .5f), new Vector3f(0.f, 0.f, -.25f), new Vector3f(.25f, 0.f, 0.f));
		RectangleLight light = new RectangleLight(rectangle, new Spectrum(15.f, 15.f, 15.f));
		objects.add(light);
		lights.add(light);
	}
}
