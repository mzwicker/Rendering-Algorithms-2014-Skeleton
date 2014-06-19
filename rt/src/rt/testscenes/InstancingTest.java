package rt.testscenes;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import rt.*;
import rt.films.*;
import rt.integrators.*;
import rt.intersectables.*;
import rt.samplers.*;
import rt.tonemappers.*;
import rt.cameras.*;

/**
 * Test for instancing functionality. Note: the spheres look strange (elliptical) and
 * it looks like they intersect a bit because of perspective projection!
 */
public class InstancingTest extends Scene {

	public InstancingTest()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/InstancingTest");
		
		// Image width and height in pixels
		width = 1280;
		height = 720;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 3.f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new DebugIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
			
		// Make sphere and instances; the default sphere is a unit sphere
		// placed at the origin
		Sphere sphere = new Sphere();
		
		Matrix4f translation = new Matrix4f();
		translation.setIdentity();
		translation.setTranslation(new Vector3f(2.0f, 0.f, 0.f));
		Instance sphere2 = new Instance(sphere, translation);
		
		translation.setTranslation(new Vector3f(-2.0f, 0.f, 0.f));
		Instance sphere3 = new Instance(sphere, translation);
				
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(sphere);
		intersectableList.add(sphere2);
		intersectableList.add(sphere3);
		
		root = intersectableList;
	}
}
