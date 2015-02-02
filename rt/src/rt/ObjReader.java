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
	 * @param fileName
	 *            the file to read.
	 * @param scale
	 *            scales the object to fit into a cube of the given size
	 * @return a mesh
	 * @throws IOException
	 */
	public static Mesh read(String fileName, float scale) throws IOException {
		BufferedReader reader;
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> texCoords = new ArrayList<float[]>();
		ArrayList<float[]> normals = new ArrayList<float[]>();
		ArrayList<int[][]> faces = new ArrayList<int[][]>();

		boolean hasNormals, hasTexCoords;
		hasNormals = true;
		hasTexCoords = true;

		// Extents for normalization
		float[] max = new float[] { Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY };
		float[] min = new float[] { Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY };

		reader = new BufferedReader(new FileReader(fileName));

		String line = null;
		while ((line = reader.readLine()) != null) {
			// Read line
			String[] s = line.split("\\s+"); // split on whitespace

			// Parse
			switch (s[0]) {
			case "v":
				// Position
				float[] v = new float[3];
				v[0] = Float.valueOf(s[1]).floatValue();
				v[1] = Float.valueOf(s[2]).floatValue();
				v[2] = Float.valueOf(s[3]).floatValue();
				vertices.add(v);

				// Update extent
				for (int i = 0; i < 3; i++) {
					min[i] = Math.min(min[i], v[i]);
					max[i] = Math.max(max[i], v[i]);
				}
				break;
			case "vn":
				// Normal
				float[] n = new float[3];
				n[0] = Float.valueOf(s[1]).floatValue();
				n[1] = Float.valueOf(s[2]).floatValue();
				n[2] = Float.valueOf(s[3]).floatValue();
				normals.add(n);
				break;
			case "vt":
				// Texture
				float[] t = new float[2];
				t[0] = Float.valueOf(s[1]).floatValue();
				t[1] = Float.valueOf(s[2]).floatValue();
				texCoords.add(t);
				break;
			case "f":
				// Indices
				int[][] indices = new int[s.length - 1][3];

				// For all vertices
				for (int i = 1; i < s.length; i++) {
					// Get indices for vertex position, tex. coords., and
					// normals, format is v/tc/n -> int[]{ v, tc, n }
					// if only two are given, it is v/tc -> int[]{v, tc}
					String[] ss = s[i].split("/");

					for (int k = 0; k < ss.length; k++) {
						if (ss[k].length() > 0)
							indices[i - 1][k] = Integer.parseInt(ss[k]);
						else {
							indices[i - 1][k] = -1;
							if (k == 1)
								hasTexCoords = false;
							if (k == 2)
								hasNormals = false;
						}
					}
					if (ss.length < 2) {
						hasTexCoords = false;
					}
					if (ss.length < 3) {
						hasNormals = false;
					}
				}
				// Convert arbitrary polygons to triangles pivoted around the first vertex.
				// Expects that polygon lies in plane.
				for (int i = 0; i < indices.length - 2; i++) {
					int triangleIndices[][] = new int[][]{ indices[0], indices[i + 1], indices[i + 2] };
					faces.add(triangleIndices);
				}
				break;
			case "#":
				// This is a comment.
				System.out.println(line);
				break;
			default:
				System.out.println("Unknown token on line: " + line);
				break;
			}
		}
		reader.close();

		// Normalization
		float[] trans = new float[3];
		float[] scales = new float[3];
		for (int i = 0; i < 3; i++) {
			trans[i] = -(max[i] + min[i]) / 2.f;
			scales[i] = 2.f / (max[i] - min[i]);
		}
		float s = Math.min(scales[0], Math.min(scales[1], scales[2]));
		scale = s * scale;

		// Brute force approach to generate single index per vertex
		// Expand arrays
		int nFaces = faces.size();
		float[] verticesFinal = new float[nFaces * 9];
		float[] normalsFinal = new float[nFaces * 9];
		float[] texCoordsFinal = new float[nFaces * 6];
		int[] indices = new int[nFaces * 3];

		// For all faces
		int vertexNr = 0;
		for (int i = 0; i < nFaces; i++) {
			// For all vertices
			for (int j = 0; j < 3; j++) {
				// Copy positions, tex. coords., and normals to expanded arrays
				// We subtract one from the index because indexing in the obj
				// file is 1-based, whereas our arrays are 0-based

				int vertexIdx = faces.get(i)[j][0] - 1;
				// For every coordinate, scale and translate.
				for (int c = 0; c < 3; c++) {
					float v = vertices.get(vertexIdx)[c];
					verticesFinal[vertexNr * 3 + c] = scale * (v + trans[c]);
				}

				if (hasNormals) {
					int normalIdx = faces.get(i)[j][2] - 1;
					normalsFinal[vertexNr * 3] = normals.get(normalIdx)[0];
					normalsFinal[vertexNr * 3 + 1] = normals.get(normalIdx)[1];
					normalsFinal[vertexNr * 3 + 2] = normals.get(normalIdx)[2];
				}

				if (hasTexCoords) {
					int textCoordIdx = faces.get(i)[j][1] - 1;
					texCoordsFinal[vertexNr * 2] = texCoords.get(textCoordIdx)[0];
					texCoordsFinal[vertexNr * 2 + 1] = texCoords
							.get(textCoordIdx)[1];
				}

				indices[vertexNr] = vertexNr;
				vertexNr++;
			}
			if (!hasNormals) {
				Vector3f edge0 = new Vector3f(verticesFinal[(vertexNr - 1) * 3]
						- verticesFinal[(vertexNr - 3) * 3],
						verticesFinal[(vertexNr - 1) * 3 + 1]
								- verticesFinal[(vertexNr - 3) * 3 + 1],
						verticesFinal[(vertexNr - 1) * 3 + 2]
								- verticesFinal[(vertexNr - 3) * 3 + 2]);
				Vector3f edge1 = new Vector3f(verticesFinal[(vertexNr - 2) * 3]
						- verticesFinal[(vertexNr - 3) * 3],
						verticesFinal[(vertexNr - 2) * 3 + 1]
								- verticesFinal[(vertexNr - 3) * 3 + 1],
						verticesFinal[(vertexNr - 2) * 3 + 2]
								- verticesFinal[(vertexNr - 3) * 3 + 2]);
				Vector3f n = new Vector3f();
				n.cross(edge1, edge0);
				n.normalize();
				for (int j = 0; j < 3; j++) {
					int normalIdx = (vertexNr - (j + 1)) * 3;
					normalsFinal[normalIdx + 0] = n.x;
					normalsFinal[normalIdx + 1] = n.y;
					normalsFinal[normalIdx + 2] = n.z;
				}
			}
		}
		return new Mesh(verticesFinal, normalsFinal, texCoordsFinal, indices);
	}
}
