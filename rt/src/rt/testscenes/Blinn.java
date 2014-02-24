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
 * Simple scene using a Blinn material.
 */
public class Blinn extends Scene {

	public Blinn()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/Blinn");
		
		// Image width and height in pixels
		width = 512;
		height = 512;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 3.f);
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

		// Ground plane
		CSGPlane groundPlane = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), 1.f);
		
		// Sphere with Blinn material
		Sphere sphere = new Sphere();
		sphere.material = new rt.materials.Blinn(new Spectrum(.8f, 0.f, 0.f), new Spectrum(.4f, .4f, .4f), 50.f);
		
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(groundPlane);
		intersectableList.add(sphere);
		
		root = intersectableList;
		
		// Light sources
		LightGeometry pl1 = new PointLight(new Vector3f(.5f, .5f, 2.f), new Spectrum(1.f, 1.f, 1.f));
		LightGeometry pl2 = new PointLight(new Vector3f(-.75f, .75f, 2.f), new Spectrum(1.f, 1.f, 1.f));
		lightList = new LightList();
		lightList.add(pl1);
		lightList.add(pl2);
	}
}
