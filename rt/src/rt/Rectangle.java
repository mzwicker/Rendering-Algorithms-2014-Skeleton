package rt;

import javax.vecmath.*;

public class Rectangle implements Intersectable {

	Vector3f bottomLeft;
	Vector3f top;
	Vector3f right;
	Vector3f normal;
	public Material material;
	
	public Rectangle(Vector3f bottomLeft, Vector3f right, Vector3f top)
	{
		material = new BlinnMaterial();
		
		this.bottomLeft = new Vector3f(bottomLeft);
		this.right = new Vector3f(right);
		this.top = new Vector3f(top);
		normal = new Vector3f();
		normal.cross(right, top);
		normal.normalize();
	}
	
	public Rectangle(Rectangle r)
	{
		material = new BlinnMaterial();
		
		this.bottomLeft = new Vector3f(r.bottomLeft);
		this.right = new Vector3f(r.right);
		this.top = new Vector3f(r.top);
		this.normal = new Vector3f(r.normal);
	}
	
	public AxisAlignedBox boundingBox() {
		AxisAlignedBox box = new AxisAlignedBox(Math.min(Math.min(bottomLeft.x, bottomLeft.x+top.x), bottomLeft.x+right.x),
												Math.max(Math.max(bottomLeft.x, bottomLeft.x+top.x), bottomLeft.x+right.x),
												Math.min(Math.min(bottomLeft.y, bottomLeft.y+top.y), bottomLeft.y+right.y),
												Math.max(Math.max(bottomLeft.y, bottomLeft.y+top.y), bottomLeft.y+right.y),
												Math.min(Math.min(bottomLeft.z, bottomLeft.z+top.z), bottomLeft.z+right.z),
												Math.max(Math.max(bottomLeft.z, bottomLeft.z+top.z), bottomLeft.z+right.z));
		return box;
	}

	public HitRecord intersect(Ray r) {
		
		Matrix3f m = new Matrix3f(r.direction.x, -right.x, -top.x,
								  r.direction.y, -right.y, -top.y,
								  r.direction.z, -right.z, -top.z);
		
		try
		{
			m.invert();
		} catch(SingularMatrixException e)
		{
			return null;
		}
		
		Vector3f t = new Vector3f(-r.origin.x+bottomLeft.x, -r.origin.y+bottomLeft.y, -r.origin.z+bottomLeft.z);
		m.transform(t);
		
		if(t.y>=0.f && t.y<=1.f && t.z>=0.f && t.z<=1.f)
		{
			Vector3f position = new Vector3f(r.direction);
			position.scale(t.x);
			position.add(r.origin);
			Vector3f wIn = new Vector3f(r.direction);
			wIn.negate();
			wIn.normalize();
			
			if(t.x>0) return new HitRecord(t.x, position, normal, wIn, this, null, material, t.y, t.z); else return null;
		} else
		{
			return null;
		}
	}

	public float surfaceArea() {
		return top.length()*right.length();
	}
}
