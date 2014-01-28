package rt.scenes;

import javax.vecmath.Vector3f;

import rt.Camera;
import rt.BlinnMaterial;
import rt.Film;
import rt.IntersectableList;
import rt.LightList;
import rt.PointLight;
import rt.Rectangle;
import rt.RefractiveMaterial;
import rt.Spectrum;
import rt.Sphere;

public class RefractionTestScene {

	public Camera camera;
	public Film film;
	public IntersectableList objects;
	public LightList lights;

	public RefractionTestScene()
	{
		// Make camera and film
		Vector3f eye = new Vector3f(0.f,0.f,4.f);
		Vector3f lookAt = new Vector3f(0.f,0.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 128;
		int height = 128;
		float aspect = (float)width/(float)height;
		camera = new Camera(eye, lookAt, up, fov, aspect, width, height);
		film = new Film(width, height);						
		
		// List of objects
		objects = new IntersectableList();	
		
		Sphere sphere = new Sphere(new Vector3f(0.0f,0.f,1.f), 1.f);
		sphere.material = new RefractiveMaterial(1.5f);
		objects.add(sphere);
				
		Rectangle rectangle = new Rectangle(new Vector3f(-1.f, -1.f, -.5f), new Vector3f(1.f, 0.f, 0.f), new Vector3f(0.f, 1.f, 0.f));
		rectangle.material = new BlinnMaterial(new Spectrum(1.f, 0.f, 0.f));
		objects.add(rectangle);
		
		Rectangle rectangle2 = new Rectangle(new Vector3f(0.f, 0.f, -.5f), new Vector3f(1.f, 0.f, 0.f), new Vector3f(0.f, 1.f, 0.f));
		rectangle2.material = new BlinnMaterial(new Spectrum(0.f, 1.f, 0.f));
		objects.add(rectangle2);
		
		Rectangle rectangle3 = new Rectangle(new Vector3f(-1.f, 0.f, -.5f), new Vector3f(1.f, 0.f, 0.f), new Vector3f(0.f, 1.f, 0.f));
		rectangle3.material = new BlinnMaterial(new Spectrum(0.f, 0.f, 1.f));
		objects.add(rectangle3);
	
		
		// List of lights
		lights = new LightList();
		PointLight light = new PointLight(new Vector3f(0.f,2.f,0.5f), new Spectrum(15.f, 15.f, 15.f));
		lights.add(light);
	}
}
