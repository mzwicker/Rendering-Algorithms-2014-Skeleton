package rt.testscenes;

import javax.vecmath.Vector3f;

import rt.*;
import rt.cameras.*;
import rt.films.*;
import rt.integrators.*;
import rt.intersectables.*;
import rt.lightsources.*;
import rt.samplers.*;
import rt.tonemappers.*;

/**
 * Test scene for pinhole camera specifications.
 */
public class CameraTestScene extends Scene {

	public CameraTestScene()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/Camera");
		
		// Image width and height in pixels
		width = 1280;
		height = 720;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.5f, 0.5f, 3.f);
		Vector3f lookAt = new Vector3f(0.5f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.2f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = 16.f/9.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new PointLightIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
		
		// Define the root object (an intersectable) of the scene
		// A box
		CSGPlane p1 = new CSGPlane(new Vector3f(1.f, 0.f, 0.f), 1.f);
		CSGPlane p2 = new CSGPlane(new Vector3f(-1.f, 0.f, 0.f), 1.f);
		CSGPlane p3 = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), 1.f);
		CSGPlane p4 = new CSGPlane(new Vector3f(0.f, -1.f, 0.f), 1.f);
		CSGPlane p5 = new CSGPlane(new Vector3f(0.f, 0.f, 1.f), 1.f);
		
		CSGNode n1 = new CSGNode(p1, p2, CSGNode.OperationType.ADD);
		CSGNode n2 = new CSGNode(p3, p4, CSGNode.OperationType.ADD);
		CSGNode n3 = new CSGNode(n2, p5, CSGNode.OperationType.ADD);
		root = new CSGNode(n1, n3, CSGNode.OperationType.ADD); 
		
		// Light sources
		LightGeometry pointLight = new PointLight(new Vector3f(0.f, 0.f, 3.f), new Spectrum(10.f, 10.f, 10.f));
		lightList = new LightList();
		lightList.add(pointLight);
	}
}
