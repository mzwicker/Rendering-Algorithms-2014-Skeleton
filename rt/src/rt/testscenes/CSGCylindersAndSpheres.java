package rt.testscenes;

import rt.*;
import rt.cameras.FixedCamera;
import rt.cameras.PinholeCamera;
import rt.films.BoxFilterFilm;
import rt.integrators.PointLightIntegratorFactory;
import rt.intersectables.*;
import rt.lightsources.*;
import rt.samplers.OneSamplerFactory;
import rt.tonemappers.ClampTonemapper;

import javax.vecmath.*;

/**
 * Test scene for CSG cylinders and spheres.
 */
public class CSGCylindersAndSpheres extends Scene {
		
	public CSGCylindersAndSpheres()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/CSGCylindersAndSpheres");
		
		// Image width and height in pixels
		width = 512;
		height = 512;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(-4.f, 2.5f, 3.f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = 1.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new PointLightIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
		
		// Construct a cross out of CSG cylinders and spheres
		Matrix4f trafo = new Matrix4f();
		trafo.setIdentity();
		trafo.setScale(2.f);
		// Note: CSGSphere makes a unit sphere centered at the origin 
		CSGInstance sphere = new CSGInstance(new CSGSphere(), trafo);
		trafo.setScale(0.5f);
		// Note: CSGInfiniteCylinder makes an infinite cylinder with unit radius,
		// and the axis of the cylinder is the x-axis
		CSGInstance cylinder = new CSGInstance(new CSGInfiniteCylinder(), trafo);
				
		CSGNode cylinderSphere1 = new CSGNode(sphere, cylinder, CSGNode.OperationType.INTERSECT);
		
		trafo.setScale(1.f);
		trafo.rotX((float)Math.PI/2.f);
		CSGInstance cylinderSphere2 = new CSGInstance(cylinderSphere1, trafo);
		
		root = new CSGNode(cylinderSphere1, cylinderSphere2, CSGNode.OperationType.ADD);
		
		
		// Light sources
		LightGeometry pointLight = new PointLight(eye, new Spectrum(25.f, 25.f, 25.f));
		lightList = new LightList();
		lightList.add(pointLight);
	}
}
