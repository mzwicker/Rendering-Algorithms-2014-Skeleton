package rt;

import java.util.Iterator;

public class Mesh extends Aggregate {

	public float[] vertices;
	public float[] normals;
	public int[] indices;
	private Triangle[] triangles;
	private AxisAlignedBox boundingBox;
	public Material material;
	
	/**
	 * Make a mesh from arrays with vertices, normals, and indices.
	 */
	public Mesh(float[] vertices, float[] normals, int[] indices)
	{
		material = new BlinnMaterial();
		
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		triangles = new Triangle[indices.length/3];
		
		boundingBox = new AxisAlignedBox(Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE);

		float p[][] = new float[3][3];
		
		for(int i=0; i<indices.length/3; i++)
		{
			triangles[i] = new Triangle(this, i);
			int v0, v1, v2;
			v0 = indices[i*3];
			v1 = indices[i*3+1];
			v2 = indices[i*3+2];

			// p[vertex 0,1,2][coordinate x,y,z]
			p[0][0] = vertices[v0*3];
			p[0][1] = vertices[v0*3+1];
			p[0][2] = vertices[v0*3+2];
			p[1][0] = vertices[v1*3];
			p[1][1] = vertices[v1*3+1];
			p[1][2] = vertices[v1*3+2];
			p[2][0] = vertices[v2*3];
			p[2][1] = vertices[v2*3+1];
			p[2][2] = vertices[v2*3+2];
			
			for(int k=0; k<3; k++)
			{
				for(int j=0; j<3; j++)
				{
					if(p[k][j]<boundingBox.bounds[j][0]) boundingBox.bounds[j][0] = p[k][j];
					if(p[k][j]>boundingBox.bounds[j][1]) boundingBox.bounds[j][1] = p[k][j];
				}
			}
		}
	}
	
	public Iterator<Intersectable> iterator() {
		return new MeshIterator(triangles);
	}
	
	private class MeshIterator implements Iterator<Intersectable>
	{
		private int i;
		private Triangle[] triangles;
		
		public MeshIterator(Triangle[] triangles)
		{
			this.triangles = triangles;
			i = 0;
		}
		
		public boolean hasNext()
		{
			return i<triangles.length;
		}
		
		public Triangle next()
		{
			int j = i;
			i++;
			return triangles[j];
		}
		
		public void remove()
		{
		}
	}

	public AxisAlignedBox boundingBox()
	{
		return boundingBox;
	}
		
}
