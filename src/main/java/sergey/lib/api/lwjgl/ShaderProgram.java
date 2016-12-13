package sergey.lib.api.lwjgl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {

	private int programID;
	private String modelTransform;
	private String cameraTransform;
	private HashMap<String, Integer> attributes, uniforms;

	public ShaderProgram(String name, String transformName, String cameraTransform) {
		this(name + ".vert.glsl", name + ".frag.glsl", transformName, cameraTransform);
	}
	
	public void setTransformationName(String name) {
		this.modelTransform = name;
	}
	
	public void setCameraTransform(String cameraTransform) {
		this.cameraTransform = cameraTransform;
	}

	public ShaderProgram(String vertexLocation, String fragmentLocation, String transformName, String cameraTransform) {
		attributes = new HashMap<>();
		uniforms = new HashMap<>();
		programID = GL20.glCreateProgram();
		{ //Vertex Shader
			int shaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
			GL20.glShaderSource(shaderID, readAssetToArray(vertexLocation));
			GL20.glCompileShader(shaderID);
			int status = GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS);
			if (status == 0) {
				System.err.println("Vertex Shader Failed:");
				System.err.println(GL20.glGetShaderInfoLog(shaderID));
			}
			GL20.glAttachShader(programID, shaderID);
			GL20.glDeleteShader(shaderID);
		}

		{ //Fragment Shader
			int shaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
			GL20.glShaderSource(shaderID, readAssetToArray(fragmentLocation));
			GL20.glCompileShader(shaderID);
			int status = GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS);
			if (status == 0) {
				System.err.println("Fragment Shader Failed:");
				System.err.println(GL20.glGetShaderInfoLog(shaderID));
			}
			GL20.glAttachShader(programID, shaderID);
			GL20.glDeleteShader(shaderID);
		}

		GL20.glLinkProgram(programID);
		int status = GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS);
		if (status == 0) {
			System.err.println("Shader Program Linking Failed:");
			System.err.println(GL20.glGetProgramInfoLog(programID));
		}
		
		setTransformationName(transformName);
		setCameraTransform(cameraTransform);
	}

	public void dispose() {
		if (GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != programID) {
			GL20.glUseProgram(0);
		}
		GL20.glDeleteProgram(programID);
	}

	public void bind() {
		if (GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != programID) {
			GL20.glUseProgram(programID);
		}
	}

	public int getProgramID() {
		return programID;
	}

	public int getAttribute(String str) {
		Integer v = attributes.get(str);
		if (v == null) {
			attributes.put(str, GL20.glGetAttribLocation(programID, str));
			return attributes.get(str);
		}
		return v;
	}

	public int getUniform(String str) {
		Integer v = uniforms.get(str);
		if (v == null) {
			uniforms.put(str, GL20.glGetUniformLocation(programID, str));
			return uniforms.get(str);
		}
		return v;
	}
	
	public String getTransformationName() {
		return modelTransform;
	}
	
	public String getCameraTransform() {
		return cameraTransform;
	}

	private String[] readAssetToArray(String assetName) {
		try (Scanner scan = new Scanner(new File(getClass().getResource("/" + assetName).toURI()))) {
			List<String> lines = new LinkedList<String>();
			while (scan.hasNextLine()) {
				lines.add(scan.nextLine() + " ");
			}

			scan.close();
			return lines.toArray(new String[0]);
		} catch (FileNotFoundException | URISyntaxException e) {

		}
		return new String[]{};
	}
}
