package rt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import javax.vecmath.*;

public class BSPAccelerator implements Intersectable {

	private float area;
	private AxisAlignedBox boundingBox;
	public BSPNode root;
	
	private int minObjects;
	private int maxDepth;
	private float costInterior;
	private float costLeaf;
	private float costIntersect;
	
	public BSPAccelerator(Aggregate a)
	{
		boundingBox = a.boundingBox();
		area = a.surfaceArea();
		
		minObjects = 3;
		maxDepth = 50;
		costInterior = 1.f;
		costLeaf = 1.f;
		costIntersect = 8.f;
		
		ArrayList<Intersectable> objList = new ArrayList<Intersectable>();
		Iterator<Intersectable> itr = a.iterator();
		while(itr.hasNext())
		{
			objList.add(itr.next());
		}
		root = splitOptimized(objList, boundingBox, 1);
	}
	
	/**
	 * Constructs kd-tree by splitting along longest axis.
	 * 
	 * @param objects list of objects
	 * @param bbox bounding box of objects
	 * @param depth current depth
	 * @return kd-tree root
	 */
	private BSPNode split(ArrayList<Intersectable> objects, AxisAlignedBox bbox, int depth)
	{
		if(objects.size()<=minObjects || depth>maxDepth)
			return makeLeaf(objects);
		
		BSPNodeType type;
		AxisAlignedBox bboxLeft, bboxRight;
		float splitPos;
		
		// Determine split axis, compute split bounding boxes
		if(bbox.bounds[0][1]-bbox.bounds[0][0]>bbox.bounds[1][1]-bbox.bounds[1][0] && bbox.bounds[0][1]-bbox.bounds[0][0]>bbox.bounds[2][1]-bbox.bounds[2][0])
		{
			type = BSPNodeType.X;
			splitPos = (bbox.bounds[0][1]+bbox.bounds[0][0])/2.f;
 			bboxLeft = new AxisAlignedBox(bbox.bounds[0][0], splitPos, bbox.bounds[1][0], bbox.bounds[1][1], bbox.bounds[2][0], bbox.bounds[2][1]);
			bboxRight = new AxisAlignedBox(splitPos, bbox.bounds[0][1], bbox.bounds[1][0], bbox.bounds[1][1], bbox.bounds[2][0], bbox.bounds[2][1]);
		} else if (bbox.bounds[1][1]-bbox.bounds[1][0]>bbox.bounds[2][1]-bbox.bounds[2][0])
		{
			type = BSPNodeType.Y;
			splitPos = (bbox.bounds[1][1]+bbox.bounds[1][0])/2.f;
			bboxLeft = new AxisAlignedBox(bbox.bounds[0][0], bbox.bounds[0][1], bbox.bounds[1][0], splitPos, bbox.bounds[2][0], bbox.bounds[2][1]);
			bboxRight = new AxisAlignedBox(bbox.bounds[0][0], bbox.bounds[0][1], splitPos, bbox.bounds[1][1], bbox.bounds[2][0], bbox.bounds[2][1]);
		} else
		{
			type = BSPNodeType.Z;
			splitPos = (bbox.bounds[2][1]+bbox.bounds[2][0])/2.f;
			bboxLeft = new AxisAlignedBox(bbox.bounds[0][0], bbox.bounds[0][1], bbox.bounds[1][0], bbox.bounds[1][1], bbox.bounds[2][0], splitPos);
			bboxRight = new AxisAlignedBox(bbox.bounds[0][0], bbox.bounds[0][1], bbox.bounds[1][0], bbox.bounds[1][1], splitPos, bbox.bounds[2][1]);
		}
		
		// Insert objects below and above split plane into separate lists
		ArrayList<Intersectable> objsLeft = new ArrayList<Intersectable>();
		ArrayList<Intersectable> objsRight = new ArrayList<Intersectable>();
		
		Iterator<Intersectable> itr = objects.iterator();
		while(itr.hasNext())
		{
			Intersectable cur = itr.next();
			AxisAlignedBox curBox = cur.boundingBox();
			boolean intersectsLeft = bboxLeft.intersects(curBox); 
			if(intersectsLeft)
			{
				objsLeft.add(cur);
			}
			boolean intersectsRight = bboxRight.intersects(curBox); 
			if(intersectsRight)
			{
				objsRight.add(cur);
			}
			if(!intersectsLeft && !intersectsRight)
			{
				// Should never happen
				System.out.printf("BSP tree split problem!\n");
			}
		}
		
		// Determine if split is worth it
		float cost = (objsLeft.size()*bboxLeft.surfaceArea()+objsRight.size()*bboxRight.surfaceArea())*costIntersect/bbox.surfaceArea();
		float costNoSplit = costLeaf + objects.size()*costIntersect;
		float costSplit = costInterior + 2*costLeaf + cost;
		
		if(costSplit>costNoSplit)
			return makeLeaf(objects);
		else
			return new BSPNode(type, splitPos, split(objsLeft, bboxLeft, depth+1), split(objsRight, bboxRight, depth+1));
	}

	/**
	 * Constructs kd-tree by finding optimal split using the surface area heuristics (see e.g. PBRT book).
	 * It doesn't really seem to lead to trees that accelerate rendering compared to splitting along the
	 * longest axis. Not sure why.
	 * 
	 * @param objects list of objects
	 * @param bbox bounding box of objects
	 * @param depth current depth
	 * @return kd-tree root
	 */
	private BSPNode splitOptimized(ArrayList<Intersectable> objects, AxisAlignedBox bbox, int depth)
	{	
		if(objects.size()<=minObjects || depth>maxDepth)
			return makeLeaf(objects);

		// List for bounding box edges along all three axes
		ArrayList<Edge> edges[] = new ArrayList[3];
		for(int i=0; i<3; i++)
			edges[i] = new ArrayList<Edge>();
		
		// Get bounding box edges for all objects in array
		Iterator<Intersectable> it = objects.iterator();
		while(it.hasNext()) 
		{
			Intersectable o = it.next();
			AxisAlignedBox b = o.boundingBox();
	
			// Insert edges into lists for each axis
			for(int j=0; j<3; j++)
			{
				Edge eLow = new Edge();
				eLow.t = b.bounds[j][0];
				eLow.type = EdgeType.LOWER;
				eLow.obj = o;
				edges[j].add(eLow);
	
				Edge eHigh = new Edge();
				eHigh.t = b.bounds[j][1];
				eHigh.type = EdgeType.HIGHER;
				eHigh.obj = o;
				edges[j].add(eHigh);
			}
		}
		
		// Area for parent bounding box
		float area = bbox.surfaceArea();
		float bboxRange[] = new float[3];
		bboxRange[0] = bbox.bounds[0][1]-bbox.bounds[0][0];
		bboxRange[1] = bbox.bounds[1][1]-bbox.bounds[1][0];
		bboxRange[2] = bbox.bounds[2][1]-bbox.bounds[2][0];
		float tmp[] = new float[3];
			
		// Minimum costs and cut positions for all three axes
		float minCost[] = new float[3];
		float cut[] = new float[3];
		int nBelowOpt[] = new int[3];
		int nAboveOpt[] = new int[3];
		
		// Find minimum costs for all three axes
		for(int j=0; j<3; j++)
		{
			minCost[j] = Float.MAX_VALUE;
			cut[j] = bbox.bounds[j][0];
			
			Collections.sort(edges[j]);
			int nBelow, nAbove;			
			nBelow = 0;
			nAbove = objects.size();
			
			for(int i=0; i<edges[j].size(); i++)
			{
				Edge e = edges[j].get(i);
				
				if(e.type == EdgeType.HIGHER)
					nAbove--;
				
				if(e.t > bbox.bounds[j][0] && e.t < bbox.bounds[j][1])
				{
					// Compute bounding box surface areas of below and above box
					tmp[0] = bboxRange[0];
					tmp[1] = bboxRange[1];
					tmp[2] = bboxRange[2];
					tmp[j] = e.t - bbox.bounds[j][0];
					float areaBelow = 2*tmp[0]*tmp[1]+2*tmp[0]*tmp[2]+2*tmp[1]*tmp[2];
					tmp[j] = bbox.bounds[j][1] - e.t;
					float areaAbove = 2*tmp[0]*tmp[1]+2*tmp[0]*tmp[2]+2*tmp[1]*tmp[2];
					
					// Estimate cost
					float cost = costIntersect*nBelow*areaBelow/area + costIntersect*nAbove*areaAbove/area;
					if(minCost[j] > cost)
					{
						minCost[j] = cost;
						cut[j] = e.t;
						nBelowOpt[j] = nBelow;
						nAboveOpt[j] = nAbove;
					}
				}
				
				if(e.type == EdgeType.LOWER)
					nBelow++;
			}			
		}
		
		// Determine axis with minimum cost
		int minAxis;
		if(minCost[0] < minCost[1]) minAxis = 0; else minAxis = 1;
		if(minCost[2] < minCost[minAxis]) minAxis = 2;
		
		// Determine if split is worth it
		float costNoSplit = costLeaf + objects.size()*costIntersect;
		float costSplit = costInterior + 2*costLeaf + minCost[minAxis];
		
		if(costSplit>costNoSplit)
			return makeLeaf(objects);
					
		// Assemble object arrays for above and below nodes
		float[][] bounds = bbox.bounds; 
		AxisAlignedBox bboxBelow = new AxisAlignedBox(bounds[0][0], bounds[0][1], bounds[1][0], bounds[1][1], bounds[2][0], bounds[2][1]);
		AxisAlignedBox bboxAbove = new AxisAlignedBox(bounds[0][0], bounds[0][1], bounds[1][0], bounds[1][1], bounds[2][0], bounds[2][1]);
		bboxBelow.bounds[minAxis][1] = cut[minAxis];
		bboxAbove.bounds[minAxis][0] = cut[minAxis];
		
		ArrayList<Intersectable> objsBelow = new ArrayList<Intersectable>();
		ArrayList<Intersectable> objsAbove = new ArrayList<Intersectable>();		
		Iterator<Intersectable> itr = objects.iterator();
		while(itr.hasNext())
		{
			Intersectable cur = itr.next();
			AxisAlignedBox curBox = cur.boundingBox();
			boolean intersectsBelow = bboxBelow.intersects(curBox); 
			if(intersectsBelow)
			{
				objsBelow.add(cur);
			}
			boolean intersectsAbove = bboxAbove.intersects(curBox); 
			if(intersectsAbove)
			{
				objsAbove.add(cur);
			}
			if(!intersectsBelow && !intersectsAbove)
			{
				// Should never happen
				System.out.printf("BSP tree split problem!\n");
			}
		}
		
		BSPNodeType type = BSPNodeType.X;
		switch(minAxis) {
			case 0: type = BSPNodeType.X; break;
			case 1: type = BSPNodeType.Y; break;
			case 2: type = BSPNodeType.Z; break;
		}

		return new BSPNode(type, cut[minAxis], splitOptimized(objsBelow, bboxBelow, depth+1), splitOptimized(objsAbove, bboxAbove, depth+1));
	}

	private BSPNode makeLeaf(ArrayList<Intersectable> objects)
	{
		// Make a leaf node
		IntersectableList list = new IntersectableList();
		Iterator<Intersectable> itr = objects.iterator();
		while(itr.hasNext())
		{
			Intersectable t = itr.next();
			list.add(t);
		}
		return new BSPNode(BSPNodeType.L, 0.f, list, null);
	}
	
	/**
	 * Intersect ray with kd-tree. See Rendering Algorithms class slides for details.
	 */
	public HitRecord intersect(Ray r) {
	
		BSPNode node = root;
		float isect = Float.MAX_VALUE;

		LinkedList<BSPStackItem> stack = new LinkedList<BSPStackItem>();
		
		// tMin, tMax = intersect root bounding box
		Tuple2f tmp = boundingBox.intersections(r);
		if(tmp==null) return null;
		float tMin = tmp.x;
		float tMax = tmp.y;
		
		HitRecord hitRecord = null;
		
		while(node!=null) 
		{
			if(isect<tMin) break;
			if(node.type!=BSPNodeType.L) 
			{
				float rSplitAxis = 0.f;
				float rSplitDir = 0.f;
				if(node.type==BSPNodeType.X) 
				{
					rSplitAxis = r.origin.x;
					rSplitDir = r.direction.x;
				} else if(node.type==BSPNodeType.Y) 
				{
					rSplitAxis = r.origin.y;
					rSplitDir = r.direction.y;
				} else if(node.type==BSPNodeType.Z) 
				{
					rSplitAxis = r.origin.z;
					rSplitDir = r.direction.z;
				}
								
				// compute ray-split plane intersection
				float tSplit;
				if(rSplitDir==0.f) tSplit = Float.MAX_VALUE;
				else tSplit = (node.splitPos-rSplitAxis)/rSplitDir;
				
				BSPNode first, second;
				if(rSplitAxis<=node.splitPos) {
					first = (BSPNode)node.left;
					second = (BSPNode)node.right;
				} else 
				{
					first = (BSPNode)node.right;
					second = (BSPNode)node.left;
				}
				
				if(tSplit>tMax || tSplit<0.f || (tSplit==0.f && rSplitDir<0.f)) 
				{
					node = first;
				}
				else if(tSplit<tMin || (tSplit==0.f && rSplitDir>0.f)) 
				{
					node = second;
				}
				else
				{
					node = first;
					stack.addFirst(new BSPStackItem(second, tSplit, tMax));
					tMax = tSplit;
				}
			
			} else 
			{
				// Check for intersection inside leaf
				HitRecord h = ((IntersectableList)(node.left)).intersect(r);
				if(h!=null)
				{
					hitRecord = h;
					isect = h.t;
				}

				// Pop stack item
				if(!stack.isEmpty())
				{
					BSPStackItem i = stack.removeFirst();
					node = i.node;
					tMin = i.tMin;
					tMax = i.tMax;
				}
				else
				{
					node = null;
				}
			}
		}
		return hitRecord;
	}

	/**
	 * For debugging purposes. Naive traversal of kd-tree.
	 */
	public HitRecord intersectAll(BSPNode n, Ray r) {
	
		HitRecord h1, h2;
		float t1, t2;
		t1 = t2 = Float.MAX_VALUE;
		
		if(n.type!=BSPNodeType.L)
		{
			h1 = intersectAll((BSPNode)n.left, r); 
			h2 = intersectAll((BSPNode)n.right, r);
			if(h1!=null) 
				t1 = h1.t;
			if(h2!=null) 
				t2 = h2.t;
			if(t1<t2) 
				return h1; else
			return h2;
		}
		else
		{
			return ((IntersectableList)(n.left)).intersect(r);
		}
	}
	
	public float surfaceArea()
	{
		return area;
	}
	
	public AxisAlignedBox boundingBox()
	{
		return boundingBox;
	}
	
	public Material getMaterial()
	{
		return null;
	}
	
	private enum BSPNodeType {X, Y, Z, L};
	
	private class BSPNode
	{
		public BSPNodeType type;
		public float splitPos;
		public Object left, right;
		
		BSPNode(BSPNodeType type, float splitPos, Object left, Object right)
		{
			this.type = type;
			this.splitPos = splitPos;
			this.left = left;
			this.right = right;
		}
	}
	
	private class BSPStackItem
	{
		public BSPNode node;
		public float tMin, tMax;
		
		BSPStackItem(BSPNode node, float tMin, float tMax)
		{
			this.node = node;
			this.tMin = tMin;
			this.tMax = tMax;
		}
	}
	
	public enum EdgeType {LOWER, HIGHER};
	
	private class Edge implements Comparable<Edge>
	{	
		public float t;
		public Intersectable obj;
		public EdgeType type;
		
		public int compareTo(Edge e)
		{
			if(this.t < e.t)
				return -1;
			else if(this.t > e.t)
				return 1;
			else if(this.type == EdgeType.LOWER)
				return -1;
			else 
				return 1;
		}
	}
}