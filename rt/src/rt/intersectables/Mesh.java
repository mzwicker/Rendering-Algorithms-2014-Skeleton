package rt.intersectables;

import java.util.Iterator;

import rt.Intersectable;
import rt.Material;
import rt.Spectrum;
import rt.materials.Diffuse;

/**
 * A triangle mesh. The mesh internally stores the triangles using vertex
 * and index arrays. The mesh also instantiates a {@link MeshTriangle} for each triangle,
 * and the mesh provides an iterator to iterate through the triangles.
 */
public class Mesh extends Aggregate {

	/**
	 * Array of triangle vertices. Stores x,y,z coordinates for each vertex consecutively.
	 */
	public float[] vertices;
	
	/**
	 * Array of triangle normals (one per vertex). Stores x,y,z coordinates for each normal consecutively.
	 */
	public float[] normals;
	
	/**
	 * Array of texture coordinates (one per vertex). Stores x,y coordinates for each texture coordinate consecutively.
	 */
	public float[] texCoords;
	
	/**
	 * Index array. Each triangle is defined by three consecutive
	 * indices in this array. The indices refer to the {@link Mesh#vertices} 
	 * and {@link Mesh#normals} arrays that store vertex and normal coordinates.
	 */
	public int[] indices;
	
	/**
	 * Array of triangles stored in the mesh.
	 */
	private MeshTriangle[] triangles;
	
	/**
	 * A material.
	 */
	public Material material;
	
	/**
	 * Make a mesh from arrays with vertices, normals, and indices.
	 */
	public Mesh(float[] vertices, float[] normals, float[] texCoordsFinal, int[] indices)
	{
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
		
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		triangles = new MeshTriangle[indices.length/3];		
		
		// A triangle simply stores a triangle index and refers back to the mesh
		// to look up the vertex data
		for (int i = 0; i < indices.length / 3; i++) {
			triangles[i] = new MeshTriangle(this, i);
		}
	}

	public Iterator<Intersectable> iterator() {
		return new MeshIterator(triangles);
	}
	
	private class MeshIterator implements Iterator<Intersectable>
	{
		private int i;
		private MeshTriangle[] triangles;
		
		public MeshIterator(MeshTriangle[] triangles)
		{
			this.triangles = triangles;
			i = 0;
		}
		
		public boolean hasNext()
		{
			return i<triangles.length;
		}
		
		public MeshTriangle next()
		{
			int j = i;
			i++;
			return triangles[j];
		}
		
		public void remove()
		{
		}
	}
		
}
