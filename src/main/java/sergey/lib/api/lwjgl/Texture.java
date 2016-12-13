package sergey.lib.api.lwjgl;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

public class Texture {
	private int textureID;
	private int width;
	private int height;
	private int components;

	public Texture(String path, Type type, boolean flipV, boolean genMipMap) throws URISyntaxException {
		textureID = GL11.glGenTextures();

		int[] width = new int[1];
		int[] height = new int[1];
		int[] components = new int[1];

		STBImage.stbi_set_flip_vertically_on_load(flipV);
		ByteBuffer pixels = STBImage.stbi_load(path, width, height, components, 3);

		this.width = width[0];
		this.height = height[0];
		this.components = components[0];

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, type.format, width[0], height[0], 0, type.format, GL11.GL_UNSIGNED_BYTE, pixels);

		if (genMipMap) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		STBImage.stbi_image_free(pixels);
	}

	public void bind() {
		if (GL11.glGetInteger(GL11.GL_TEXTURE_2D) != textureID) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		}
	}

	public static void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getComponents() {
		return components;
	}
	
	public void dispose() {
		GL11.glDeleteTextures(textureID);
		textureID = 0;
		width = 0;
		height = 0;
		components = 0;
	}

	public enum Type {
		RGB(GL11.GL_RGB, 3),
		RGBA(GL11.GL_RGBA, 4);

		public final int format;
		public final int components;

		private Type(int format, int comp) {
			this.format = format;
			components = comp;
		}
	}
}
