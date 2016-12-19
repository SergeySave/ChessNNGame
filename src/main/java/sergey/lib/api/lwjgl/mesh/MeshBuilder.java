package sergey.lib.api.lwjgl.mesh;

import java.security.InvalidParameterException;
import java.util.LinkedList;

import sergey.lib.api.lwjgl.ShaderProgram;
import sergey.lib.api.lwjgl.gl.GLConnectionMode;
import sergey.lib.api.lwjgl.gl.GLDataType;
import sergey.lib.api.lwjgl.gl.VertexDataDef;
import sergey.lib.api.lwjgl.util.ArrayUtil;

/**
 * A class for creating static meshes
 * 
 * @author sergeys
 *
 */
public class MeshBuilder {

	private GLConnectionMode mode;
	private VertexDataDef[] vertexDef;
	private int vertexLength;
	
	private float[] vertexData;
	
	private int[] indexData;
	
	private int totalComponents;
	private LinkedList<float[]> verticies;
	private LinkedList<Integer> indicies;

	public MeshBuilder(GLConnectionMode connectionMode, VertexDataDef... vertexDef) {
		verticies = new LinkedList<>();
		indicies = new LinkedList<>();
		mode = connectionMode;
		this.vertexDef = vertexDef;
		for (VertexDataDef def : vertexDef) {
			vertexLength += def.getComponents();
		}
	}

	public int addVertex(float... data) {
		if (data.length != vertexLength) throw new InvalidParameterException("Expected " + vertexLength + " components. Recieved " + data.length + ".");
		verticies.addLast(data);
		vertexData = null;
		totalComponents += data.length;
		return verticies.size()-1; // The index where this vertex is now at
	}

	public void addIndex(int index) {
		indicies.addLast(index);
		indexData = null;
	}

	/**
	 * Does not guarantee that all three indicies are added ever single time or that they are added in the same order they were passed in as
	 * This method will attempt to use as little space in the index buffer as possible while preventing unwanted triangles from being drawn
	 * This means that it will use extra space to prevent unwanted triangles
	 * 
	 * When this is drawing as a triangle fan index1 is the hub of the triangle fan
	 * @throws InvalidParameterException when index1 is not the hub of the triangle fan
	 * 
	 * @param index1 the first index of the triangle
	 * @param index2 the second index of the triangle
	 * @param index3 the third index of the triangle
	 */
	public void addTriangle(int index1, int index2, int index3) {
		switch (mode) {
			case TRIANGLES:
				addIndex(index1);
				addIndex(index2);
				addIndex(index3);
				break;
			case TRIANGLE_STRIP:
				if (indicies.size() >= 2) { //If we have more than two indicies
					//If the previous two indicies align with index 1 and 2 then we just add the third index 
					if ((indicies.get(indicies.size()-2) == index1 && indicies.getLast() == index2) || (indicies.get(indicies.size()-2) == index2 && indicies.getLast() == index1)) {
						addIndex(index3);
					} else { //If the indicies don't align
						addIndex(indicies.getLast());
						addIndex(index1);
						addIndex(index1);
						addIndex(index2);
						addIndex(index3);
					}
				} else if (indicies.size() == 1) {//If we have one index
					if (indicies.getLast() == index1) {//If the one index is the correct index
						//Add the next two indicies
						addIndex(index2);
						addIndex(index3);
					} else if (indicies.get(0) == index2) {//If the one index is the opposite index
						//Add the next other two indicies
						addIndex(index1);
						addIndex(index3);
					} else {
						addIndex(indicies.getLast());
						addIndex(index1);
						addIndex(index1);
						addIndex(index2);
						addIndex(index3);
					}
				} else if (indicies.size() == 0) {
					addIndex(index1);
					addIndex(index2);
					addIndex(index3);
				}
				break;
			case TRIANGLE_FAN:
				if (indicies.size() == 0) {
					addIndex(index1);
				} else if (indicies.getFirst() != index1) {
					throw new InvalidParameterException("The first index must be the hub of the triangle fan");
				}
				addIndex(index2);
				addIndex(index3);
				break;
		}
	}

	public Mesh create(ShaderProgram shader) {
		Mesh mesh = new Mesh();
		mesh.setDrawingMode(mode.glMode);
		
		if (vertexData == null) {
			vertexData = ArrayUtil.stringCollectionData(verticies, totalComponents);
		}
		mesh.setVertexBuffer(vertexData, GLDataType.STATIC, vertexDef);
		
		if (indexData == null) {
			vertexData = ArrayUtil.stringCollectionData(verticies, totalComponents);
		}
		mesh.setElementBuffer(indexData, GLDataType.STATIC);
		
		mesh.setShader(shader);
	
		return mesh;
	}
}
