package rt.testscenes;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import rt.*;
import rt.cameras.*;
import rt.films.*;
import rt.integrators.*;
import rt.intersectables.*;
import rt.samplers.*;
import rt.tonemappers.*;

public class TriangleTest extends Scene {

	public TriangleTest()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/TriangleTest");
		
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
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new DebugIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
			
		// Make a triangle. Note: convention is that vertex order is counter
		// clockwise when triangle is seen from outside (outside is by convention
		// the direction the normal points into).
		float[] vertices = {0.f, 0.f, 0.f, 1.f, 0.f, 0.f, 0.f, 1.f, 0.f};
		float[] normals = {0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f};
		int[] indices = {0, 1, 2};
		
		Mesh mesh = new Mesh(vertices, normals, indices);
		
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(mesh);
		
		root = intersectableList;
	}

}
