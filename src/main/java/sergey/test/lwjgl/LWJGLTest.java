package sergey.test.lwjgl;

import java.io.File;
import java.net.URISyntaxException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import sergey.lib.api.lwjgl.PerspectiveCamera;
import sergey.lib.api.lwjgl.ShaderProgram;
import sergey.lib.api.lwjgl.Texture;
import sergey.lib.api.lwjgl.gl.util.GLUtil;
import sergey.lib.api.lwjgl.mesh.Mesh;
import sergey.lib.api.lwjgl.mesh.MeshFactory;
import sergey.lib.api.math.Matrix4f;
import sergey.lib.api.math.Vector3f;

public class LWJGLTest extends WindowApplication {

	private ShaderProgram shaderProgram;
	private Mesh cube;
	private Mesh light;
	private PerspectiveCamera camera;
	private Texture texture;

	@Override
	public void create() {
		GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

		camera = new PerspectiveCamera(67, 0.1f, 100f, getWidth(), getHeight());
		camera.translate(new Vector3f(0, 0, -1.5f));

		shaderProgram = new ShaderProgram("shader", "u_modelTransform", "u_projectionView");

		try {
			texture = new Texture(new File(getClass().getResource("/image.png").toURI()).getAbsolutePath(), Texture.Type.RGB, false, true);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		cube = MeshFactory.cubeUV(shaderProgram, 1);
		cube.setTexture(texture);

		light = MeshFactory.cubeUV(shaderProgram, 1);
		light.applyTransform(Matrix4f.translate(1.5f, 1.5f, -1.5f).multiply(Matrix4f.scale(0.5f, 0.5f, 0.5f)).multiply(Matrix4f.rotate((float) (Math.PI/4), 1, 1, 0)));
		light.setTexture(texture);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	@Override
	public void render() {

		//camera.translateLocal(0.1f, 0.1f, 0.005f);
		camera.lookAt(new Vector3f());

		camera.update(getWidth(), getHeight());

		cube.getShader().bind(); //Bind the shader
		GLUtil.setUniform(cube.getShader().getUniform(cube.getShader().getCameraTransform()), false, camera.getCombined());
		cube.draw(); //Draw the mesh
		light.draw();
		Texture.unbind();

		if (isKeyDown(GLFW.GLFW_KEY_W)) {
			camera.rotateAround(new Vector3f(), camera.getPerpendicular(), -0.01f); 
		}
		if (isKeyDown(GLFW.GLFW_KEY_S)) {
			camera.rotateAround(new Vector3f(), camera.getPerpendicular(), 0.01f); 
		}
		if (isKeyDown(GLFW.GLFW_KEY_D)) {
			camera.rotateAround(new Vector3f(), camera.getUp(), 0.01f); 
		}
		if (isKeyDown(GLFW.GLFW_KEY_A)) {
			camera.rotateAround(new Vector3f(), camera.getUp(), -0.01f); 
		}
		if (isKeyDown(GLFW.GLFW_KEY_Q)) {
			camera.rotateAround(new Vector3f(), camera.getFacing(), 0.01f); 
		}
		if (isKeyDown(GLFW.GLFW_KEY_E)) {
			camera.rotateAround(new Vector3f(), camera.getFacing(), -0.01f); 
		}
	}

	@Override
	public void dispose() {
		cube.dispose();
		light.dispose();
		texture.dispose();
		shaderProgram.dispose();
	}
}
