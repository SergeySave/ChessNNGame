package sergey.lib.api.lwjgl.mesh;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import sergey.lib.api.lwjgl.ShaderProgram;
import sergey.lib.api.lwjgl.Texture;
import sergey.lib.api.lwjgl.gl.GLDataType;
import sergey.lib.api.lwjgl.gl.VertexDataDef;
import sergey.lib.api.lwjgl.gl.util.GLUtil;
import sergey.lib.api.math.Matrix4f;

public class Mesh {
	private int vaoID;
	private int vboID;
	private int eboID;
	private boolean useIBOs;
	private int verticiesLength;
	private int indiciesLength;
	private int drawingMode;
	
	private Matrix4f localTransform;
	private int transformID;
	
	private ShaderProgram shader;
	private VertexDataDef[] vertexDatas;
	private Texture texture;

	public Mesh() {
		vaoID = GL30.glGenVertexArrays();
		vboID = GL15.glGenBuffers(); //Gen buffers
		eboID = GL15.glGenBuffers();
		
		//Set the local transform
		localTransform = new Matrix4f();
	}

	public void setVertexBuffer(float[] points, GLDataType type, VertexDataDef... vertexDatas) {
		GL30.glBindVertexArray(vaoID);{ //Begin working on the vertex array
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); //Bind the vertex buffer
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, points, type.val); //Set the vertex buffer

			int totalComponents = 0;
			for (VertexDataDef vd : vertexDatas) {
				totalComponents += vd.getComponents();
			}
			verticiesLength = points.length/totalComponents;
			this.vertexDatas = vertexDatas;
			if (shader != null) {
				updateVertexAttribPointers();
			}
		}GL30.glBindVertexArray(0); //Stop working on the vertex array
	}
	
	private void updateVertexAttribPointers() {
		GL30.glBindVertexArray(vaoID);{ //Begin working on the vertex array
			int totalSize = 0;
			for (VertexDataDef vd : vertexDatas) {
				totalSize += vd.getTotalByteLength();
			}
			int offset = 0;
			for (VertexDataDef vd : vertexDatas) {
				GL20.glVertexAttribPointer(vd.getAttributeIndex(shader), vd.getComponents(), vd.getType(), vd.isNormalized(), totalSize, offset);
				GL20.glEnableVertexAttribArray(vd.getAttributeIndex(shader));
				offset += vd.getTotalByteLength();
			}
		}GL30.glBindVertexArray(0); //Stop working on the vertex array
	}

	public void setElementBuffer(int[] indicies, GLDataType type) {
		if (indicies == null) {
			useIBOs = false;
			return;
		}
		useIBOs = true;
		indiciesLength = indicies.length;
		GL30.glBindVertexArray(vaoID);{ //Begin working on the vertex array
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID); //Bind the vertex buffer
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicies, type.val); //Set the vertex buffer
		}GL30.glBindVertexArray(0); //Stop working on the vertex array
	}

	public void dispose() {
		GL15.glDeleteBuffers(eboID);
		GL15.glDeleteBuffers(vboID);
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public void applyTransform(Matrix4f mat) {
		localTransform = localTransform.multiply(mat);
	}
	
	public Matrix4f getLocalTransform() {
		return localTransform;
	}
	
	public void setLocalTransform(Matrix4f localTransform) {
		this.localTransform = localTransform;
	}

	public void setDrawingMode(int drawingMode) {
		this.drawingMode = drawingMode;
	}
	
	public ShaderProgram getShader() {
		return shader;
	}
	
	public void setShader(ShaderProgram shader) {
		this.shader = shader;
		transformID = shader.getTransformationName() == null ? -1 : shader.getUniform(shader.getTransformationName());
		if (vertexDatas != null) {
			updateVertexAttribPointers();
		}
	}

	public void draw() {
		shader.bind();
		
		if (transformID != -1) GLUtil.setUniform(transformID, false, localTransform);

		if (texture != null) texture.bind();
		
		if (useIBOs) {
			GL30.glBindVertexArray(vaoID);
			GL11.glDrawElements(drawingMode, indiciesLength, GL11.GL_UNSIGNED_INT, 0);
			GL30.glBindVertexArray(0);
		} else {
			GL30.glBindVertexArray(vaoID);
			GL11.glDrawArrays(drawingMode, 0, verticiesLength);
			GL30.glBindVertexArray(0);
		}
	}
	
	public int getVAOID() {
		return vaoID;
	}

	public static VertexData attribute(int attrIndex, int attrByteSize, int attrType, boolean normalize, int attrByteLength, int components) {
		VertexData vd = new VertexData();
		vd.attributeIndex = attrIndex;
		vd.attributeSize = attrByteSize;
		vd.attributeType = attrType;
		vd.isNormalized = normalize;
		vd.attributeLength = attrByteLength;
		vd.components = components;
		return vd;
	}

	public static VertexData attrFloat(int components, int attrIndex, boolean normalize) {
		return attribute(attrIndex, components, GL11.GL_FLOAT, normalize, 4*components, components);
	}

	public static VertexData attrFloat(int components, int attrIndex) {
		return attribute(attrIndex, components, GL11.GL_FLOAT, false, 4*components, components);
	}

	/**
	 * A shader must be bound before using this method
	 * 
	 * @throws IllegalStateException when the shader has not been set using the setShader(ShaderProgram) method before this
	 * 
	 * @param components the number of components
	 * @param attrName the name of the attribute
	 * @return a VertexData object
	 */
	public VertexData attrFloat(int components, String attrName) {
		if (shader == null) throw new IllegalStateException("Shader not set. Cannot find attribute index.");
		return attribute(shader.getAttribute(attrName), components, GL11.GL_FLOAT, false, 4*components, components);
	}

	public static class VertexData {
		public int attributeIndex;
		public int attributeSize;
		public int attributeType;
		public int attributeLength;
		public int components;
		public boolean isNormalized;
	}
}
