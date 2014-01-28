package rt;

import javax.vecmath.*;

public class Instance implements Intersectable {

	Intersectable i;
	Matrix4f T;
	Matrix4f invT;
	AxisAlignedBox boundingBox;
	
	public Instance(Intersectable i, Matrix4f T)
	{
		this.i = i;
		this.T = new Matrix4f(T);
		this.invT = new Matrix4f(T);
		invT.invert();
		boundingBox = i.boundingBox().transform(T);
	}
		
	public AxisAlignedBox boundingBox() {
		return boundingBox;
	}

	public HitRecord intersect(Ray r) {

		// Make transformed ray
		Vector4f origin = new Vector4f(r.origin.x, r.origin.y, r.origin.z, 1.f);
		Vector3f direction = new Vector3f(r.direction);
		invT.transform(origin);
		invT.transform(direction);
		Ray tr = new Ray(new Vector3f(origin.x, origin.y, origin.z), direction);
		
		// Get transformed hit record
		HitRecord tHitRecord = i.intersect(tr);
		
		if(tHitRecord != null)
		{
			// Transform hit record back
			Vector4f position = new Vector4f(tHitRecord.position.x, tHitRecord.position.y, tHitRecord.position.z, 1.f);
			T.transform(position);
			Vector3f normal = new Vector3f(tHitRecord.normal);
			T.transform(normal);
			Vector3f wIn = new Vector3f(tHitRecord.wIn);
			T.transform(wIn);
			
			return new HitRecord(tHitRecord.t, new Vector3f(position.x, position.y, position.z), normal, wIn, tHitRecord.intersectable, tHitRecord.spectrum, tHitRecord.material, tHitRecord.u, tHitRecord.v);
		} else
		{
			return null;
		}
	}

	public float surfaceArea() {
		return i.surfaceArea();
	}	
}
