package rt;

import javax.vecmath.*;

public class Triangle implements Intersectable {

	private Mesh mesh;
	private int index;
	private float area;
	
	public Triangle(Mesh mesh, int index)
	{
		this.mesh = mesh;
		this.index = index;
		
		float vertices[] = mesh.vertices;
		int v0 = mesh.indices[index*3];
		int v1 = mesh.indices[index*3+1];
		int v2 = mesh.indices[index*3+2];
		
		float x0 = vertices[v0*3];
		float x1 = vertices[v1*3];
		float x2 = vertices[v2*3];
		float y0 = vertices[v0*3+1];
		float y1 = vertices[v1*3+1];
		float y2 = vertices[v2*3+1];
		float z0 = vertices[v0*3+2];
		float z1 = vertices[v1*3+2];
		float z2 = vertices[v2*3+2];

		Vector3f a = new Vector3f(x1-x0,y1-y0,z1-z0);
		Vector3f b = new Vector3f(x2-x0,y2-y0,z2-z0);
		Vector3f tmp = new Vector3f();
		tmp.cross(a,b);
		area = a.length()/2.f;
	}
	
	public HitRecord intersect(Ray r)
	{
		float vertices[] = mesh.vertices;
		float normals[] = mesh.normals;
		int v0 = mesh.indices[index*3];
		int v1 = mesh.indices[index*3+1];
		int v2 = mesh.indices[index*3+2];
		
		float x0 = vertices[v0*3];
		float x1 = vertices[v1*3];
		float x2 = vertices[v2*3];
		float y0 = vertices[v0*3+1];
		float y1 = vertices[v1*3+1];
		float y2 = vertices[v2*3+1];
		float z0 = vertices[v0*3+2];
		float z1 = vertices[v1*3+2];
		float z2 = vertices[v2*3+2];
		
		Matrix3f m = new Matrix3f(x0-x1, x0-x2, r.direction.x,
							 	  y0-y1, y0-y2, r.direction.y,
							 	  z0-z1, z0-z2, r.direction.z);
		try
		{
			m.invert();
		} catch(RuntimeException e)
		{
			return null;
		}
		Vector3f t = new Vector3f(x0-r.origin.x, y0-r.origin.y, z0-r.origin.z);
		m.transform(t);
		
		if(t.x>=0 && t.x<=1 && t.y>=0 && t.y<=1 && t.x+t.y<=1)
		{
			Vector3f position = new Vector3f(r.direction);
			position.scale(t.z);
			position.add(r.origin);
			// wIn is incident direction; convention is that it points away from surface
			Vector3f wIn = new Vector3f(r.direction);
			wIn.negate();
			wIn.normalize();

			Vector3f n0 = new Vector3f(normals[v0*3], normals[v0*3+1], normals[v0*3+2]);
			Vector3f n1 = new Vector3f(normals[v1*3], normals[v1*3+1], normals[v1*3+2]);
			Vector3f n2 = new Vector3f(normals[v2*3], normals[v2*3+1], normals[v2*3+2]);
			n0.scale(1.f-t.x-t.y);
			n1.scale(t.x);
			n2.scale(t.y);
			Vector3f normal = new Vector3f();
			normal.add(n0, n1);
			normal.add(n2);
			normal.normalize();
			
			if(t.z>0) return new HitRecord(t.z, position, normal, wIn, this, null, mesh.material, 0.f, 0.f); else return null;
		} else
		{
			return null;
		}
	}
	
	public float surfaceArea()
	{
		return area;
	}
	
	public AxisAlignedBox boundingBox()
	{
		float vertices[] = mesh.vertices;
		int v0 = mesh.indices[index*3];
		int v1 = mesh.indices[index*3+1];
		int v2 = mesh.indices[index*3+2];
		
		float x0 = vertices[v0*3];
		float x1 = vertices[v1*3];
		float x2 = vertices[v2*3];
		float y0 = vertices[v0*3+1];
		float y1 = vertices[v1*3+1];
		float y2 = vertices[v2*3+1];
		float z0 = vertices[v0*3+2];
		float z1 = vertices[v1*3+2];
		float z2 = vertices[v2*3+2];

		float xMin = Math.min(x0, Math.min(x1,x2));
		float xMax = Math.max(x0, Math.max(x1,x2));
		float yMin = Math.min(y0, Math.min(y1,y2));
		float yMax = Math.max(y0, Math.max(y1,y2));
		float zMin = Math.min(z0, Math.min(z1,z2));
		float zMax = Math.max(z0, Math.max(z1,z2));
		
		// Avoid infinitely flat bounding boxes. This leads to problems
		// in the BSP tree implementation.
		if(xMin == xMax) xMax += 0.0001f;
		if(yMin == yMax) yMax += 0.0001f;
		if(zMin == zMax) zMax += 0.0001f;
		
		AxisAlignedBox boundingBox = new AxisAlignedBox(xMin, xMax, yMin, yMax, zMin, zMax);
		
		return boundingBox;
	}
}
