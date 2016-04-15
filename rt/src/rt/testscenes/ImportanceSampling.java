package rt.testscenes;

import javax.vecmath.*;

import rt.*;
import rt.intersectables.*;
import rt.tonemappers.*;
import rt.integrators.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.samplers.*;
import rt.cameras.*;
import rt.films.*;

/**
 * Demonstrates an AreaLightIntegrator with multiple importance sampling 
 * using a simple scene with glossy materials of varying roughness  
 */
public class ImportanceSampling extends Scene {
	
	public ImportanceSampling()
	{	
		outputFilename = new String("../output/testscenes/ImportanceSampling");
				
		// Specify pixel sampler to be used
		samplerFactory = new RandomSamplerFactory();
		
		// Samples per pixel
		SPP = 128;
		outputFilename = outputFilename + " " + String.format("%d", SPP) + "SPP";
		
		// Make camera and film
		Vector3f eye =  new Vector3f(-5f,1f,0.f);
		Vector3f lookAt =  new Vector3f(0.f,0.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		width = 256;
		height = 256;
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);						
		tonemapper = new ClampTonemapper();
		
		// Specify integrator to be used
		// Area light integrator for direct illumination from area lights
		// Supports three sampling techniques: AreaLightIntegrator.SamplingTechnique.MIS, 
		// AreaLightIntegrator.SamplingTechnique.BRDF, AreaLightIntegrator.SamplingTechnique.Light
		AreaLightIntegrator.SamplingTechnique technique = AreaLightIntegrator.SamplingTechnique.BRDF;
		integratorFactory = new AreaLightIntegratorFactory(technique);
		outputFilename = outputFilename + " " + width + "x" + height + " " + technique;
		
		// List of objects
		IntersectableList objects = new IntersectableList();	
					
		Rectangle rectangle = new Rectangle(new Vector3f(2.f, -.75f, 4.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -8.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.2f, 0.2f));
		objects.add(rectangle);
	
		// Bottom
		rectangle = new Rectangle(new Vector3f(-4.f, -.75f, 4.f), new Vector3f(6.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -8.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);

		// GLOSSY RECTANGLES
		rectangle = new Rectangle(new Vector3f(-2.f, -.6f, -1.95f), new Vector3f(-0.5f, -0.01f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		// The Glossy material is a Torrance Sparrow BRDF with Blinn microfacet distribution
		// Parameters: roughness, index of refraction, absorption (both for Fresnel terms for conductors as in PBRT book, Eqn. 8.1, 8.2)  
		rectangle.material = new Glossy(300.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(10.f, 10.f, 10.f));
		objects.add(rectangle);

		rectangle = new Rectangle(new Vector3f(-1.f, -.5f, -1.95f), new Vector3f(-0.5f, -0.07f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(1000.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(1.f, 1.f, 1.f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Vector3f(0.f, -.3f, -1.95f), new Vector3f(-0.5f, -0.14f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(3300.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(1.f, 1.f, 1.f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Vector3f(1.f, -.0f, -1.95f), new Vector3f(-0.5f, -0.24f, 0.f), new Vector3f(0.f, 0.f, 3.9f));
		rectangle.material = new Glossy(10000.f, new Spectrum(1.f, 1.f, 1.f), new Spectrum(1.f, 1.f, 1.f));
		objects.add(rectangle);

		
		lightList = new LightList();
		
		// Add area lights
		Vector3f bottomLeft = new Vector3f(1.95f, 2.f, -2.2f);
		Vector3f right = new Vector3f(0.f, .1f, 0.f);
		Vector3f top = new Vector3f(0.f, 0.f, -.1f);
		RectangleLight rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(0.015625f, 0.0015625f, 0.0015625f));
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(1.f, 0.1f, 0.1f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		
		bottomLeft = new Vector3f(1.95f, 2.f, -0.6f);
		right = new Vector3f(0.f, .2f, 0.f);
		top = new Vector3f(0.f, 0.f, -.2f);
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(0.01225f, 0.01625f, 0.1225f));
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(0.1f, 0.1f, 1.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		
		bottomLeft = new Vector3f(1.95f, 2.f, 1.1f);
		right = new Vector3f(0.f, .4f, 0.f);
		top = new Vector3f(0.f, 0.f, -.4f);
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(0.05f, .5f, .05f));
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(0.1f, 1.f, 0.1f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		
		bottomLeft = new Vector3f(1.95f, 2.f, 3.f);
		right = new Vector3f(0.f, 0.8f, 0.f);
		top = new Vector3f(0.f, 0.f, -0.8f);
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(2.f, 2.f, 0.2f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		
		bottomLeft = new Vector3f(-0.5f, 3.f, 0.75f);
		right = new Vector3f(0.f, 0.f, -0.5f);
		top = new Vector3f(0.5f, 0.f, 0.f);
		rectangleLight = new RectangleLight(bottomLeft, right, top, new Spectrum(40.f, 40.f, 40.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		// Connect objects to root
		root = objects;
	}
}
