package rt;

import javax.vecmath.*;

/**
 * A camera whose rays have x- and y- coordinates in the direction vector that are in the range [0,1] over the image plane. 
 */
public class DummyCamera implements Camera {

	private int width;
	private int height;
	
	public DummyCamera(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Make a ray whose x- and y- coordinates in the direction vector are in the range [0,1] over the image plane.
	 */
	public Ray makeWorldSpaceRay(int i, int j, int k, float samples[][]) {
		float x = ((float)i+samples[k][0]) / (float)width;
		float y = ((float)j+samples[k][1]) / (float)height;
		return new Ray(new Vector3f(0.f,0.f,0.f), new Vector3f(x, y, 1.f));
	}

}
