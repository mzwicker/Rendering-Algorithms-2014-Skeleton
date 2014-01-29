package rt;

import javax.vecmath.*;

public class CSGCube implements Intersectable {

	CSGNode root;
	
	public CSGCube()
	{
		CSGPlane p1 = new CSGPlane(new Vector3f(1.f,0.f,0.f),1.f);
		CSGPlane p2 = new CSGPlane(new Vector3f(-1.f,0.f,0.f),1.f);
		CSGPlane p3 = new CSGPlane(new Vector3f(0.f,1.f,0.f),1.f);
		CSGPlane p4 = new CSGPlane(new Vector3f(0.f,-1.f,0.f),1.f);
		CSGPlane p5 = new CSGPlane(new Vector3f(0.f,0.f,1.f),1.f);
		CSGPlane p6 = new CSGPlane(new Vector3f(0.f,0.f,-1.f),1.f);
		CSGNode n1 = new CSGNode(p1, p2, CSGSolid.OperationType.INTERSECT);
		CSGNode n2 = new CSGNode(p3, p4, CSGSolid.OperationType.INTERSECT);
		CSGNode n3 = new CSGNode(p5, p6, CSGSolid.OperationType.INTERSECT);
		CSGNode n4 = new CSGNode(n1, n2, CSGSolid.OperationType.INTERSECT);
		root = new CSGNode(n3, n4, CSGSolid.OperationType.INTERSECT);
	}

	public HitRecord intersect(Ray r) {
		return root.intersect(r);
	}

	public float surfaceArea() {
		return root.surfaceArea();
	}

	public AxisAlignedBox boundingBox() {
		return root.boundingBox();
	}
	
	
}
