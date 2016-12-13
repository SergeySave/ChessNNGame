package sergey.lib.api.lwjgl.mesh;

import org.lwjgl.opengl.GL11;

import sergey.lib.api.lwjgl.ShaderProgram;
import sergey.lib.api.lwjgl.gl.GLAttributeType;
import sergey.lib.api.lwjgl.gl.GLDataType;
import sergey.lib.api.lwjgl.gl.VertexDataDef;
import sergey.lib.api.math.Matrix4f;

public class MeshFactory {
	private final static float[] verticiesCubeUV = new float[] {
			//Position				//UV
			-0.5f,  0.5f, 0.5f,  0.0f, 0.0f,
			-0.5f, -0.5f, 0.5f,  0.0f, 1.0f, //+Z Face
			0.5f, -0.5f, 0.5f,  1.0f, 1.0f,
			0.5f,  0.5f, 0.5f,  1.0f, 0.0f,

			0.5f,  0.5f, -0.5f,  0.0f, 0.0f,
			0.5f, -0.5f, -0.5f,  0.0f, 1.0f, //-Z Face
			-0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
			-0.5f,  0.5f, -0.5f,  1.0f, 0.0f,
			
			0.5f,  0.5f, 0.5f,  0.0f, 0.0f,
			0.5f, -0.5f, 0.5f,  0.0f, 1.0f, //+X Face
			0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
			0.5f,  0.5f, -0.5f,  1.0f, 0.0f,
			
			-0.5f,  0.5f, -0.5f,  0.0f, 0.0f,
			-0.5f, -0.5f, -0.5f,  0.0f, 1.0f, //-X Face
			-0.5f, -0.5f, 0.5f,  1.0f, 1.0f,
			-0.5f,  0.5f, 0.5f,  1.0f, 0.0f,
			
			-0.5f,  0.5f, -0.5f,  0.0f, 0.0f,
			-0.5f, 0.5f, 0.5f,  0.0f, 1.0f, //+Y Face
			0.5f, 0.5f, 0.5f,  1.0f, 1.0f,
			0.5f,  0.5f, -0.5f,  1.0f, 0.0f,
			
			-0.5f,  -0.5f, 0.5f,  0.0f, 0.0f,
			-0.5f, -0.5f, -0.5f,  0.0f, 1.0f, //-Y Face
			0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
			0.5f,  -0.5f, 0.5f,  1.0f, 0.0f
	};
	
	private final static int[] indiciesCubeUV = new int[] {
			0,1,2, //+Z Quad
			0,2,3,

			4,5,6, //-Z Quad
			4,6,7,

			8,9,10, //+X Quad
			8,10,11,

			12,13,14, //-X Quad
			12,14,15,

			16,17,18, //+Y Quad
			16,18,19,

			20,21,22, //-Y Quad
			20,22,23
	};

	public static Mesh cubeUV(ShaderProgram shader, float sideLength) {
		return boxUV(shader, sideLength, sideLength, sideLength, "a_position", "a_uv");
	}
	
	public static Mesh cubeUV(ShaderProgram shader, float sideLength, String posName, String uvName) {
		return boxUV(shader, sideLength, sideLength, sideLength, posName, uvName);
	}
	
	public static Mesh boxUV(ShaderProgram shader, float xLength, float yLength, float zLength) {
		return boxUV(shader, xLength, yLength, zLength, "a_position", "a_uv");
	}
	
	public static Mesh boxUV(ShaderProgram shader, float xLength, float yLength, float zLength, String posName, String uvName) {
		Mesh mesh = new Mesh();
		mesh.setShader(shader);
		mesh.setDrawingMode(GL11.GL_TRIANGLES);
		mesh.setVertexBuffer(verticiesCubeUV, GLDataType.STATIC, new VertexDataDef(posName, GLAttributeType.VEC3), new VertexDataDef(uvName, GLAttributeType.VEC2));
		mesh.setElementBuffer(indiciesCubeUV, GLDataType.STATIC);
		mesh.applyTransform(Matrix4f.scale(xLength, yLength, zLength));
		
		return mesh;
	}
}
