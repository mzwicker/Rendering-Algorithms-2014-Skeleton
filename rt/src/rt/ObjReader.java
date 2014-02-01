package rt;

import java.io.*;
import java.util.ArrayList;

import javax.vecmath.*;

import rt.intersectables.Mesh;


/**
 * Reads an .obj file including normals and stores it in a {@link Mesh}.
 */
public class ObjReader {

	/**
	 * Read an .obj file and return a {@link Mesh}.
	 * 
	 * @param fileName the file to read.
	 * @param scale scales the object to fit into a cube of the given size
	 * @return a mesh
	 * @throws IOException
	 */
	public static Mesh read(String fileName, float scale) throws IOException
	{
		BufferedReader reader;
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> texCoords = new ArrayList<float[]>();
		ArrayList<float[]> normals = new ArrayList<float[]>();
		ArrayList<int[][]> faces = new ArrayList<int[][]>();
		
		boolean hasNormals, hasTexCoords;
		hasNormals = true;
		hasTexCoords = true;
		
		// Extents for normalization
		float xMin, xMax, yMin, yMax, zMin, zMax;
		xMin = Float.MAX_VALUE;
		xMax = Float.MIN_VALUE;
		yMin = Float.MAX_VALUE;
		yMax = Float.MIN_VALUE;
		zMin = Float.MAX_VALUE;
		zMax = Float.MIN_VALUE;
		
		reader = new BufferedReader(new FileReader(fileName));

		String line = null;
		while((line = reader.readLine()) != null)
		{	
			// Read line
			String[] s = line.split("\\s+");
			
			// Parse
			if(s[0].compareTo("v")==0)
			{
				// Position
				float[] v = new float[3];
				v[0] = Float.valueOf(s[1]).floatValue();
				v[1] = Float.valueOf(s[2]).floatValue();
				v[2] = Float.valueOf(s[3]).floatValue();
				vertices.add(v);
				
				// Update extent
				if(v[0] < xMin) xMin = v[0];
				if(v[0] > xMax) xMax = v[0];
				if(v[1] < yMin) yMin = v[1];
				if(v[1] > yMax) yMax = v[1];
				if(v[2] < zMin) zMin = v[2];
				if(v[2] > zMax) zMax = v[2];
			} 
			else if(s[0].compareTo("vn")==0)
			{
				// Normal
				float[] n = new float[3];
				n[0] = Float.valueOf(s[1]).floatValue();
				n[1] = Float.valueOf(s[2]).floatValue();
				n[2] = Float.valueOf(s[3]).floatValue();
				normals.add(n);
			}
			else if(s[0].compareTo("vt")==0)
			{
				// Texture
				float[] t = new float[2];
				t[0] = Float.valueOf(s[1]).floatValue();
				t[1] = Float.valueOf(s[2]).floatValue();
				texCoords.add(t);
			}
			else if(s[0].compareTo("f")==0)
			{
				// Indices
				int[][] indices = new int[3][3];
				
				// For all vertices
				int i=1;
				while(i < s.length)
				{	
					// Get indices for vertex position, tex. coords., and normals
					String[] ss = s[i].split("/");
					int k=0;
					while(k < ss.length)
					{
						if(ss[k].length()>0)
							indices[i-1][k] = Integer.valueOf(ss[k]).intValue();
						else
						{
							indices[i-1][k] = -1;
							if(k == 1) hasTexCoords = false;
							if(k == 2) hasNormals = false;
						}
						k++;
					}
					if(ss.length == 1)
					{
						hasTexCoords = false;
						hasNormals = false;
					}
					i++;
				}
				faces.add(indices);
			}
			else if(s[0].length()>0 && s[0].charAt(0)!='#')
			{
				System.out.print("Unknown token '".concat(line).concat("'\n"));
			}
		}

		// Normalization
		float xTrans = -(xMax+xMin)/2;
		float yTrans = -(yMax+yMin)/2;
		float zTrans = -(zMax+zMin)/2;
		float xScale = 2/(xMax-xMin);
		float yScale = 2/(yMax-yMin);
		float zScale = 2/(zMax-zMin);
		float s = yScale;
		if(xScale < yScale) s = xScale;
		if(zScale < s) s = zScale;
		scale = s*scale;
		
		// Brute force approach to generate single index per vertex
		// Expand arrays
		int nFaces = faces.size();
		float[] verticesFinal = new float[nFaces*9];
		float[] normalsFinal = new float[nFaces*9];
		float[] texCoordsFinal = new float[nFaces*6];
		int[] indices = new int[nFaces*3];
		
		// For all faces
		int vertexNr = 0;
		for(int i=0; i<nFaces; i++)
		{
			// For all vertices
			for(int j=0; j<3; j++)
			{
				// Copy positions, tex. coords., and normals to expanded arrays
				// Note: we subtract one from the index because indexing in the obj
				// file is 1-based, whereas our arrays are 0-based
				verticesFinal[vertexNr*3] = vertices.get(faces.get(i)[j][0]-1)[0];
				verticesFinal[vertexNr*3+1] = vertices.get(faces.get(i)[j][0]-1)[1];
				verticesFinal[vertexNr*3+2] = vertices.get(faces.get(i)[j][0]-1)[2];
				
				verticesFinal[vertexNr*3] = scale*(verticesFinal[vertexNr*3]+xTrans);
				verticesFinal[vertexNr*3+1] = scale*(verticesFinal[vertexNr*3+1]+yTrans);
				verticesFinal[vertexNr*3+2] = scale*(verticesFinal[vertexNr*3+2]+zTrans);
				
				if(hasNormals)
				{
					normalsFinal[vertexNr*3] = normals.get(faces.get(i)[j][2]-1)[0];
					normalsFinal[vertexNr*3+1] = normals.get(faces.get(i)[j][2]-1)[1];
					normalsFinal[vertexNr*3+2] = normals.get(faces.get(i)[j][2]-1)[2];
				} 
				
				if(hasTexCoords)
				{
					texCoordsFinal[vertexNr*2] = texCoords.get(faces.get(i)[j][1]-1)[0];
					texCoordsFinal[vertexNr*2+1] = texCoords.get(faces.get(i)[j][1]-1)[1];
				}
				
				indices[vertexNr] = vertexNr;
				vertexNr++;
			}
			
			if(!hasNormals)
			{
				Vector3f d0 = new Vector3f(verticesFinal[(vertexNr-1)*3]-verticesFinal[(vertexNr-3)*3],
						                   verticesFinal[(vertexNr-1)*3+1]-verticesFinal[(vertexNr-3)*3+1],
						                   verticesFinal[(vertexNr-1)*3+2]-verticesFinal[(vertexNr-3)*3+2]);
				Vector3f d1 = new Vector3f(verticesFinal[(vertexNr-2)*3]-verticesFinal[(vertexNr-3)*3],
		                                   verticesFinal[(vertexNr-2)*3+1]-verticesFinal[(vertexNr-3)*3+1],
		                                   verticesFinal[(vertexNr-2)*3+2]-verticesFinal[(vertexNr-3)*3+2]);
				Vector3f n = new Vector3f();
				n.cross(d1,d0);
				n.normalize();
				for(int j=0; j<3; j++)
				{
					normalsFinal[(vertexNr-(j+1))*3] = n.x;
					normalsFinal[(vertexNr-(j+1))*3+1] = n.y;
					normalsFinal[(vertexNr-(j+1))*3+2] = n.z;
				}
			}
		}

		reader.close();
		return new Mesh(verticesFinal, normalsFinal, indices);
	}
}
 