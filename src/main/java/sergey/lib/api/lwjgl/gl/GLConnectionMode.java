package sergey.lib.api.lwjgl.gl;

import org.lwjgl.opengl.GL11;

public enum GLConnectionMode {
	TRIANGLES(GL11.GL_TRIANGLES),
	TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
	TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN);

	public final int glMode;

	private GLConnectionMode(int mode) {
		glMode = mode;
	}
}
