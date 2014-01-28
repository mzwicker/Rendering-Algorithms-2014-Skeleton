package rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import javax.vecmath.Vector3f;

public class KdTree_old<T extends KdItem> 
{
	public KdNode root;
	
	public class Neighbor implements Comparable<Neighbor>
	{
		float d;
		T item;

		public Neighbor(T item, float d)
		{
			this.item = item;
			this.d = d;
		}
		
		// Note: we want the largest element to be at the top 
		// of the priority queue
		public final int compareTo(Neighbor n)
		{
			if(d > n.d)
				return -1;
			else if(d == n.d)
				return 0;
			else
				return 1;
		}		
	}
		
	private class KdNode
	{
		public KdNode(KdNode left, KdNode right, T item)
		{
			this.left = left;
			this.right = right;
			this.item = item;
		}
		
		KdNode left, right;
		T item;
	}
	
	public KdTree_old(ArrayList<T> list)
	{
		root = buildTree(list, 0);
	}
	
	private KdNode buildTree(ArrayList<T> list, int axis)
	{	
		KdNode left, right;
		T item;
		
		left = right = null;
		int size2 = list.size()/2;
		int size = list.size();
		
		if(size>1)
		{					
			// Sort according to position along axis
			Collections.sort(list, new KdItemComparator(axis));			
			item = list.get(size2);
			
			// Collect items in right subtree, i.e., larger than current 
			if(size2+1 < size)
			{
				ArrayList<T> rightList = new ArrayList<T>();
				for(int i=size2+1; i<size; i++)
					rightList.add(list.get(i));
				// Recurse into right subtree
				right = buildTree(rightList, (axis+1)%3);
			}
			
			// Collect items in left subtree, i.e., smaller than current
			ArrayList<T> leftList = new ArrayList<T>();
			for(int i=0; i<size2; i++)
				leftList.add(list.get(i));
			// Recurse into left subtree
			left = buildTree(leftList, (axis+1)%3);
		} else if(size==1)
		{
			item = list.get(0);
		} else
		{
			item = null;
		}
		
		// Return new node with left and right subtrees
		return new KdNode(left, right, item);
	}
	
	/**
	 * Get the nearest neighbors.
	 * 
	 * @param query the query position
	 * @param k the maximum number of neighbors to return
	 * @param maxD the maximum radius around the query to search
	 * @param minD the minimum radius around the query to search
	 * @return a priority queue containing the found neighbors
	 */
	public PriorityQueue<Neighbor> getNeighbors(Vector3f query, int k, float maxD, float minD)
	{
		float queryArray[] = new float[3];
		query.get(queryArray);
		
		PriorityQueue<Neighbor> neighborQueue = new PriorityQueue<Neighbor>(k);
		descendToLeaf(queryArray, root, 0, k, neighborQueue, maxD, minD);
		
		return neighborQueue;
	}
	
	private final void descendToLeaf(float[] query, KdNode current, int axis, int k, PriorityQueue<Neighbor> neighborQueue, float maxD, float minD)
	{
		// Return if reached leaf
		if(current == null)
			return;
		
		// Remember if we descended in left or right subtree
		boolean tookLeft;

		// Descend to leaf
		float currentPosition[] = current.item.getPosition();		
		if(query[axis] < currentPosition[axis])
		{
			descendToLeaf(query, current.left, (axis+1)%3, k, neighborQueue, maxD, minD);
			tookLeft = true;
		} else
		{
			descendToLeaf(query, current.right, (axis+1)%3, k, neighborQueue, maxD, minD);
			tookLeft = false;
		}
		
		// Distance of current item to query		
		float dx = query[0] - currentPosition[0];
		float dy = query[1] - currentPosition[1];
		float dz = query[2] - currentPosition[2];
		float d = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
				
		if(neighborQueue.size() < k || d < minD)
		{
			// Add current
			neighborQueue.add(new Neighbor(current.item, d));
			
		} else if(d<neighborQueue.peek().d)
		{
			// Replace previous farthest neighbor and find new one
			neighborQueue.poll();
			neighborQueue.add(new Neighbor(current.item, d));			
		}

		// Check which subtrees to search
		boolean descendLeft = false;
		boolean descendRight = false;

		float currentMaxD = neighborQueue.peek().d;
		int currentSize = neighborQueue.size();
		if(tookLeft && (currentPosition[axis] - query[axis] <= currentMaxD || currentSize < k || currentMaxD < minD && 
				currentPosition[axis] - query[axis] <= maxD))
			descendRight = true;
		if(!tookLeft && (query[axis] - currentPosition[axis] < currentMaxD || currentSize < k || currentMaxD < minD &&
				query[axis] - currentPosition[axis] < maxD))
			descendLeft = true;
	
		// Descend into unexplored subtree if necessary
		if(descendLeft && current.left != null)
			searchSubtree(query, current.left, (axis+1)%3, k, neighborQueue, maxD, minD);
		if(descendRight && current.right != null)
			searchSubtree(query, current.right, (axis+1)%3, k, neighborQueue, maxD, minD);
	}	
	
	private final void searchSubtree(float query[], KdNode current, int axis, int k, PriorityQueue<Neighbor> neighborQueue, float maxD, float minD)
	{
		// Distance of current item to query
		float currentPosition[] = current.item.getPosition();
		
		float dx = query[0] - currentPosition[0];
		float dy = query[1] - currentPosition[1];
		float dz = query[2] - currentPosition[2];
		float d = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
			
		// Check if we should add current item to the list of neighbors 
		if(neighborQueue.size() < k || d < minD)
		{
			// Add current
			neighborQueue.add(new Neighbor(current.item, d));
			
		} else if(d<neighborQueue.peek().d)
		{
			// Replace previous farthest neighbor and find new one
			neighborQueue.poll();
			neighborQueue.add(new Neighbor(current.item, d));			
		}
		
		// Check if we need to explore subtrees
		boolean descendLeft = true;
		boolean descendRight = true;
		
		int currentSize = neighborQueue.size();
		float currentMaxD = neighborQueue.peek().d;
		if(currentSize >= k && currentPosition[axis] - query[axis] > currentMaxD && currentMaxD > minD ||
				currentPosition[axis] - query[axis] > maxD)
			descendRight = false;
		if(currentSize >= k && query[axis] - currentPosition[axis] > currentMaxD && currentMaxD > minD ||
				query[axis] - currentPosition[axis] > maxD)
			descendLeft = false;
				
		// Search unexplored subtrees if necessary
		if(descendLeft && current.left != null)
			searchSubtree(query, current.left, (axis+1)%3, k, neighborQueue, maxD, minD);
		if(descendRight && current.right != null)
			searchSubtree(query, current.right, (axis+1)%3, k, neighborQueue, maxD, minD);
	}
}
