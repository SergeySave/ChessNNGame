package sergey.lib.api.lwjgl;

import sergey.lib.api.lwjgl.gl.util.GLUtil;
import sergey.lib.api.lwjgl.mesh.Mesh;

public class RenderContext {
	private PerspectiveCamera camera;

	public void render(Mesh mesh) {
		mesh.getShader().bind();
		String trans = mesh.getShader().getCameraTransform();
		if (trans != null) {
			GLUtil.setUniform(mesh.getShader().getUniform(trans), false, camera.getCombined());
		}
	}
}
