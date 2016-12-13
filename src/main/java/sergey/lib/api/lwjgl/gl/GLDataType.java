package sergey.lib.api.lwjgl.gl;

import org.lwjgl.opengl.GL15;

public enum GLDataType {
	STATIC(GL15.GL_STATIC_DRAW),
	DYNAMIC(GL15.GL_DYNAMIC_DRAW),
	STREAM(GL15.GL_STREAM_DRAW);

	public final int val;

	private GLDataType(int val) {
		this.val = val;
	}
}
