package rt.testscenes;

import rt.*;
import rt.cameras.*;
import rt.films.*;
import rt.integrators.*;
import rt.intersectables.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.samplers.*;
import rt.tonemappers.*;

import javax.vecmath.*;

/**
 * Test scene for recursive integrator with mirror spheres.
 */
public class MirrorSpheres extends Scene {
		
	public MirrorSpheres()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/MirrorSpheres");
		
		// Image width and height in pixels
		width = 1024;
		height = 512;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(5.f, 2.5f, 10.f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 20.f;
		float aspect = 2.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new WhittedIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
		
		// Three spheres
		Matrix4f trafo = new Matrix4f();
		trafo.setIdentity();
		Sphere sphere1 = new Sphere();
		// The BlinnPlusMirror material is a sum of Blinn and a mirror
		// The parameters are kd, ks, shininess (for Blinn) and mirror reflectivity (for the mirror)
		sphere1.material = new BlinnPlusMirror(new Spectrum(0.f,0.f,0.75f), new Spectrum(0.01f,0.01f,0.01f), 20.f, new Spectrum(0.5f,0.5f,0.5f));
		trafo.setTranslation(new Vector3f(2.f,0.f,0.f));
		
		Sphere sphere2 = new Sphere();
		sphere2.material = new BlinnPlusMirror(new Spectrum(0.f,0.75f,0.f), new Spectrum(0.01f,0.01f,0.01f), 20.f, new Spectrum(0.5f,0.5f,0.5f));
		Instance sphere2Instance = new Instance(sphere2, trafo);
		
		trafo.setTranslation(new Vector3f(-2.f,0.f,0.f));
		Sphere sphere3 = new Sphere();
		sphere3.material = new BlinnPlusMirror(new Spectrum(0.75f,0.f,0.f), new Spectrum(0.01f,0.01f,0.01f), 20.f, new Spectrum(0.5f,0.5f,0.5f));
		Instance sphere3Instance = new Instance(sphere3, trafo);
		
		// Ground plane
		Plane plane = new Plane(new Vector3f(0.f, 1.f, 0.f), 1.f);
		plane.material = new XYZCheckerboard();
		
		// Add objects to scene
		IntersectableList sceneObjects = new IntersectableList();
		sceneObjects.add(sphere1);
		sceneObjects.add(sphere2Instance);
		sceneObjects.add(sphere3Instance);
		sceneObjects.add(plane);
		root = sceneObjects;
			
		// Light source, relatively far away but strong
		LightGeometry pointLight = new PointLight(new Vector3f(0.f, 20.f, 20.f), new Spectrum(2500.f, 2500.f, 2500.f));
		lightList = new LightList();
		lightList.add(pointLight);
	}
}
