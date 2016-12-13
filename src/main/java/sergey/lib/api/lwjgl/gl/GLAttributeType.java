package sergey.lib.api.lwjgl.gl;

import org.lwjgl.opengl.GL11;

public enum GLAttributeType {
	FLOAT(GL11.GL_FLOAT, 1, 4),
	VEC2(GL11.GL_FLOAT, 2, 4),
	VEC3(GL11.GL_FLOAT, 3, 4),
	VEC4(GL11.GL_FLOAT, 4, 4);
	
	public final int glTypeNum;
	public final int components;
	public final int bytePerComponent;
	
	/**
	 * @param glTypeNum
	 * @param components
	 * @param bytePerComponent
	 */
	private GLAttributeType(int glTypeNum, int components, int bytePerComponent) {
		this.glTypeNum = glTypeNum;
		this.components = components;
		this.bytePerComponent = bytePerComponent;
	}
}
