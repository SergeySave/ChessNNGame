package sergey.lib.api.lwjgl.gl;

import sergey.lib.api.lwjgl.ShaderProgram;

public class VertexDataDef {
	private String name;
	private int components;
	private int type;
	private int totalByteLength;
	private boolean isNormalized;
	
	public VertexDataDef(String attributeName, GLAttributeType type) {
		this(attributeName, type, false);
	}
	
	public VertexDataDef(String attributeName, GLAttributeType type, boolean isNormalized) {
		this.name = attributeName;
		this.components = type.components;
		this.type = type.glTypeNum;
		this.totalByteLength = type.bytePerComponent * this.components;
		this.isNormalized = isNormalized;
	}

	public int getAttributeIndex(ShaderProgram program) {
		return program.getAttribute(name);
	}
	
	public int getComponents() {
		return components;
	}
	
	public int getType() {
		return type;
	}
	
	public int getTotalByteLength() {
		return totalByteLength;
	}
	
	public boolean isNormalized() {
		return isNormalized;
	}
}
