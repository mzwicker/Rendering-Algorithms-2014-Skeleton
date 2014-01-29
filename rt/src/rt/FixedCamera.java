package rt;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class FixedCamera implements Camera {

	Vector3f eye;
	Matrix4f m;
	
	public FixedCamera(int width, int height)
	{
		// Fixed eye position in world coordinates
		eye = new Vector3f(0.f, 0.f, -2.f);
		
		// Fixed camera to world transform
		Matrix4f c = new Matrix4f();
		c.setIdentity();
		c.m03 = eye.x;
		c.m13 = eye.y;
		c.m23 = eye.z;
		
		// Fixed projection matrix
		Matrix4f p = new Matrix4f();
		p.setIdentity();
		p.m22 = -1.f;
		p.m23 = -2.f;
		p.m32 = -1.f;
		p.m33 = 0.f;		
		p.invert();
		
		// Make viewport matrix
		Matrix4f v = new Matrix4f();
		v.setIdentity();		
		v.m00 = (float)width/2.f;
		v.m03 = (float)width/2.f;
		v.m11 = (float)height/2.f;
		v.m13 = (float)height/2.f;
		v.m22 = 1.f;
		v.m23 = 0.f;
		v.invert();
		
		// Make the matrix that transforms a viewport pixel coordinate
		// to a world space point
		p.mul(v);
		m = v;
	}
	
	public Ray makeWorldSpaceRay(int i, int j, int k, float[][] samples) {
		// Make point on image plane in viewport coordinates, that is range [0,width-1] x [0,height-1]
		Vector4f d = new Vector4f((float)i+samples[k][0],(float)j+samples[k][1],-1.f,1.f);
		
		// Transform it to world coordinates
		m.transform(d);
		
		// Make ray consisting of origin and direction
		Vector3f dir = new Vector3f();
		dir.sub(new Vector3f(d.x, d.y, d.z), eye);
		Ray r = new Ray(new Vector3f(eye), dir);
		return r;
	}

}
