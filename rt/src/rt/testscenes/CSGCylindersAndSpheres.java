package rt.testscenes;

import rt.*;
import rt.cameras.FixedCamera;
import rt.cameras.*;
import rt.films.BoxFilterFilm;
import rt.integrators.*;
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
		width = 1024;
		height = 512;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(5.f, 2.5f, 10.f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 30.f;
		float aspect = 2.f;
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
		// and the axis of the cylinder is the z-axis
		CSGInstance cylinder = new CSGInstance(new CSGInfiniteCylinder(), trafo);
				
		// A cylinder intersected with a sphere
		CSGNode cylinderSphere1 = new CSGNode(sphere, cylinder, CSGNode.OperationType.INTERSECT);
		
		// A second instance of the cylinder intersected with a sphere, rotated around the x-axis by 90 degrees
		trafo.setScale(1.f);
		trafo.rotX((float)Math.PI/2.f);
		CSGInstance cylinderSphere2 = new CSGInstance(cylinderSphere1, trafo);
		
		// Subtraction of the two cylinders
		trafo.setIdentity();
		trafo.rotX((float)Math.PI/2.f);
		CSGInstance crossSubtract = new CSGInstance(new CSGNode(cylinderSphere1, cylinderSphere2, CSGNode.OperationType.SUBTRACT), trafo);
		
		// Intersection of the two cylinders, place it on the right along the x-axis
		trafo.setIdentity();
		trafo.rotX((float)Math.PI/2.f);
		trafo.setTranslation(new Vector3f(3.f, 0.f, 0.f));
		CSGInstance crossIntersect = new CSGInstance(new CSGNode(cylinderSphere1, cylinderSphere2, CSGNode.OperationType.INTERSECT), trafo);

		// Addition/union of the two cylinders, place it on the left along the x-axis
		trafo.setIdentity();
		trafo.rotY((float)Math.PI/2.f);
		trafo.setTranslation(new Vector3f(-4.f, 0.f, 0.f));
		CSGInstance crossAdd = new CSGInstance(new CSGNode(cylinderSphere1, cylinderSphere2, CSGNode.OperationType.ADD), trafo);
		
		// Ground plane
		Plane plane = new Plane(new Vector3f(0.f, 1.f, 0.f), 2.2f);
		
		// Add objects to scene
		IntersectableList sceneObjects = new IntersectableList();
		sceneObjects.add(crossSubtract);
		sceneObjects.add(crossIntersect);
		sceneObjects.add(crossAdd);
		sceneObjects.add(plane);
		root = sceneObjects;
			
		// Light source, relatively far away but strong
		LightGeometry pointLight = new PointLight(new Vector3f(0.f, 20.f, 20.f), new Spectrum(2000.f, 2000.f, 2000.f));
		lightList = new LightList();
		lightList.add(pointLight);
	}
}
