package rt;

import java.util.ArrayList;
import java.util.Collections;
import javax.vecmath.Vector3f;

public class KdTree<T extends KdItem> 
{
	public KdNode root;
			
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
	
	public KdTree(ArrayList<T> list)
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
	public void getNeighbors(Vector3f query, int k, float maxD, float minD, int nFound[], T neighbors[], float distances[])
	{
		float queryArray[] = new float[3];
		query.get(queryArray);
		nFound[0] = 0;
		
		descendToLeaf(queryArray, root, 0, k, maxD, minD, nFound, neighbors, distances);
	}
	
	private final void descendToLeaf(float[] query, KdNode current, int axis, int k, float maxD, float minD, int nFound[], T neighbors[], float distances[])
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
			descendToLeaf(query, current.left, (axis+1)%3, k, maxD, minD, nFound, neighbors, distances);
			tookLeft = true;
		} else
		{
			descendToLeaf(query, current.right, (axis+1)%3, k, maxD, minD, nFound, neighbors, distances);
			tookLeft = false;
		}
		
		// Distance of current item to query		
		float dx = query[0] - currentPosition[0];
		float dy = query[1] - currentPosition[1];
		float dz = query[2] - currentPosition[2];
		float d = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
				
		if(nFound[0] < k || d < minD)
		{
			// Add current
			neighbors[nFound[0]] = current.item;
			distances[nFound[0]] = d;
						
			// Sift up
			int i=nFound[0];
			int p = (i+1)/2-1;
			while(i>0 && p>=0 && distances[p]<distances[i])
			{
				// Swap indices p and i
				float tmpD = distances[p];
				T tmpItem = neighbors[p];
				distances[p] = distances[i];
				neighbors[p] = neighbors[i];
				distances[i] = tmpD;
				neighbors[i] = tmpItem;
				// Move one up
				i = p;
				p = (i+1)/2-1;
			}				
			nFound[0]++;
		} else if(d<distances[0])
		{
			// Replace previous farthest neighbor and find new one			
			distances[0] = d;
			neighbors[0] = current.item;
			
			// Sift down
			int i=0;
			int p = 2*i+1;
			if(p+1<nFound[0] && distances[p+1]>distances[p]) p++;
			while(p<nFound[0] && distances[p]>distances[i])
			{
				// Swap indices p and i
				float tmpD = distances[p];
				T tmpItem = neighbors[p];
				distances[p] = distances[i];
				neighbors[p] = neighbors[i];
				distances[i] = tmpD;
				neighbors[i] = tmpItem;
				// Move one down			
				i = p;
				p = 2*i+1;
				if(p+1<nFound[0] && distances[p+1]>distances[p]) p++;
			}
		}

		// Check which subtrees to search
		boolean descendLeft = false;
		boolean descendRight = false;

		float currentMaxD = distances[0];
		int currentSize = nFound[0];
		if(tookLeft && (currentPosition[axis] - query[axis] <= currentMaxD || currentSize < k || currentMaxD < minD && 
				currentPosition[axis] - query[axis] <= maxD))
			descendRight = true;
		if(!tookLeft && (query[axis] - currentPosition[axis] < currentMaxD || currentSize < k || currentMaxD < minD &&
				query[axis] - currentPosition[axis] < maxD))
			descendLeft = true;
	
		// Descend into unexplored subtree if necessary
		if(descendLeft && current.left != null)
			searchSubtree(query, current.left, (axis+1)%3, k, maxD, minD, nFound, neighbors, distances);
		if(descendRight && current.right != null)
			searchSubtree(query, current.right, (axis+1)%3, k, maxD, minD, nFound, neighbors, distances);
	}	
	
	private final void searchSubtree(float query[], KdNode current, int axis, int k, float maxD, float minD, int nFound[], T neighbors[], float distances[])
	{		
		// Distance of current item to query
		float currentPosition[] = current.item.getPosition();
		
		float dx = query[0] - currentPosition[0];
		float dy = query[1] - currentPosition[1];
		float dz = query[2] - currentPosition[2];
		float d = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
			
		// Check if we should add current item to the list of neighbors 
		if(nFound[0] < k || d < minD)
		{
			// Add current
			neighbors[nFound[0]] = current.item;
			distances[nFound[0]] = d;
						
			// Sift up
			int i=nFound[0];
			int p = (i+1)/2-1;
			while(i>0 && p>=0 && distances[p]<distances[i])
			{
				// Swap indices p and i
				float tmpD = distances[p];
				T tmpItem = neighbors[p];
				distances[p] = distances[i];
				neighbors[p] = neighbors[i];
				distances[i] = tmpD;
				neighbors[i] = tmpItem;
				// Move one up
				i = p;
				p = (i+1)/2-1;
			}				
			nFound[0]++;
		} else if(d<distances[0])
		{
			// Replace previous farthest neighbor and find new one			
			distances[0] = d;
			neighbors[0] = current.item;
			
			// Sift down
			int i=0;
			int p = 1;
			if(p+1<nFound[0] && distances[p+1]>distances[p]) p++;
			while(p<nFound[0] && distances[p]>distances[i])
			{
				// Swap indices p and i
				float tmpD = distances[p];
				T tmpItem = neighbors[p];
				distances[p] = distances[i];
				neighbors[p] = neighbors[i];
				distances[i] = tmpD;
				neighbors[i] = tmpItem;
				// Move one down			
				i = p;
				p = 2*i+1;
				if(p+1<nFound[0] && distances[p+1]>distances[p]) p++;
			}
		}
		
		// Check if we need to explore subtrees
		boolean descendLeft = true;
		boolean descendRight = true;
		
		int currentSize = nFound[0];
		float currentMaxD = distances[0];
		if(currentSize >= k && currentPosition[axis] - query[axis] > currentMaxD && currentMaxD > minD ||
				currentPosition[axis] - query[axis] > maxD)
			descendRight = false;
		if(currentSize >= k && query[axis] - currentPosition[axis] > currentMaxD && currentMaxD > minD ||
				query[axis] - currentPosition[axis] > maxD)
			descendLeft = false;
				
		// Search unexplored subtrees if necessary
		if(descendLeft && current.left != null)
			searchSubtree(query, current.left, (axis+1)%3, k, maxD, minD, nFound, neighbors, distances);
		if(descendRight && current.right != null)
			searchSubtree(query, current.right, (axis+1)%3, k, maxD, minD, nFound, neighbors, distances);
	}
}
