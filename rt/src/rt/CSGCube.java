package rt;

import javax.vecmath.*;

/**
 * A cube implemented using planes and CSG. The cube occupies the volume [-1,1] x [-1,1] x [-1,1]. 
 */
public class CSGCube implements Intersectable {

	CSGNode root;
	
	public CSGCube()
	{
		CSGPlane p1 = new CSGPlane(new Vector3f(1.f,0.f,0.f),-1.f);
		CSGPlane p2 = new CSGPlane(new Vector3f(-1.f,0.f,0.f),-1.f);
		CSGPlane p3 = new CSGPlane(new Vector3f(0.f,1.f,0.f),-1.f);
		CSGPlane p4 = new CSGPlane(new Vector3f(0.f,-1.f,0.f),-1.f);
		CSGPlane p5 = new CSGPlane(new Vector3f(0.f,0.f,1.f),-1.f);
		CSGPlane p6 = new CSGPlane(new Vector3f(0.f,0.f,-1.f),-1.f);
		
		p1.material = new DiffuseMaterial(new Spectrum(1.f, 1.f, 1.f));
		p2.material = new DiffuseMaterial(new Spectrum(1.f, 0.f, 0.f));
		p3.material = new DiffuseMaterial(new Spectrum(0.f, 1.f, 0.f));
		p4.material = new DiffuseMaterial(new Spectrum(0.f, 0.f, 1.f));
		p5.material = new DiffuseMaterial(new Spectrum(1.f, 1.f, 0.f));
		p6.material = new DiffuseMaterial(new Spectrum(0.f, 1.f, 1.f));
		
		CSGNode n1 = new CSGNode(p1, p2, CSGNode.OperationType.INTERSECT);
		CSGNode n2 = new CSGNode(p3, p4, CSGNode.OperationType.INTERSECT);
		CSGNode n3 = new CSGNode(p5, p6, CSGNode.OperationType.INTERSECT);
		CSGNode n4 = new CSGNode(n1, n2, CSGNode.OperationType.INTERSECT);
		root = new CSGNode(n3, n4, CSGNode.OperationType.INTERSECT);
	}

	public HitRecord intersect(Ray r) {
		return root.intersect(r);
	}

}
