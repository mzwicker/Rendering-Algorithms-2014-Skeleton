package rt.cameras;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import rt.Camera;
import rt.Ray;

/**
 * A simple camera with fixed position and view frustum.
 */
public class FixedCamera implements Camera {

	Vector3f eye;
	Matrix4f m;
	
	/**
	 * Makes a camera with fixed position and view frustum. The position is at [0,0,3] in world space. 
	 * The camera looks down the negative z-axis towards the origin. The view frustum of the camera
	 * goes through the points [-1,-1,-1], [1,-1,-1], [-1,1,-1],[1,1,-1] in camera coordinates.
	 * 
	 * @param width width of the image in pixels
	 * @param height height of the image in pixels
	 */
	public FixedCamera(int width, int height)
	{
		// Fixed eye position in world coordinates
		eye = new Vector3f(0.f, 0.f, 3.0f);
		
		// Fixed camera to world transform, just a translation
		Matrix4f c = new Matrix4f();
		c.setIdentity();
		c.m03 = eye.x;
		c.m13 = eye.y;
		c.m23 = eye.z;
		
		// Fixed projection matrix, the viewing frustum defined here goes through
		// the points [-1,-1,-1], [1,-1,-1], [-1,1,-1],[1,1,-1] in camera coordinates. 
		Matrix4f p = new Matrix4f();
		p.setIdentity();
		float near = 1.f;
		float far = 10.f;
		p.m22 = -(far+near)/(far-near);
		p.m23 = -(2*far*near)/(far-near);
		p.m32 = -1.f;
		p.m33 = 0.f;		
		p.invert();
		
		// Make viewport matrix given image resolution
		Matrix4f v = new Matrix4f();
		v.setIdentity();		
		v.m00 = (float)width/2.f;
		v.m03 = (float)width/2.f;
		v.m11 = (float)height/2.f;
		v.m13 = (float)height/2.f;
		v.m22 = 1.f;
		v.m23 = 0.f;
		v.invert();
		
		// Make the matrix c*p*v that transforms a viewport pixel coordinate
		// to a world space point
		p.mul(v);
		c.mul(p);
		m = c;
	}

	/**
	 * Make a world space ray. The method receives a sample that 
	 * the camera uses to generate the ray. It uses the first two
	 * sample dimensions to sample a location in the current 
	 * pixel. The samples are assumed to be in the range [0,1].
	 * 
	 * @param i column index of pixel through which ray goes (0 = left boundary)
	 * @param j row index of pixel through which ray goes (0 = bottom boundary)
	 * @param sample random sample that the camera can use to generate a ray   
	 */
	public Ray makeWorldSpaceRay(int i, int j, float[] sample) {
		// Make point on image plane in viewport coordinates, that is range [0,width-1] x [0,height-1]
		// The assumption is that pixel [i,j] is the square [i,i+1] x [j,j+1] in viewport coordinates
		Vector4f d = new Vector4f((float)i+sample[0],(float)j+sample[1],-1.f,1.f);
		
		// Transform it back to world coordinates
		m.transform(d);
		
		// Make ray consisting of origin and direction in world coordinates
		Vector3f dir = new Vector3f();
		dir.sub(new Vector3f(d.x, d.y, d.z), eye);
		Ray r = new Ray(new Vector3f(eye), dir);
		return r;
	}

}
