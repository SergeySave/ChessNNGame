package sergey.lib.api.lwjgl.gl.util;

import org.lwjgl.opengl.GL20;

import sergey.lib.api.math.Matrix2f;
import sergey.lib.api.math.Matrix3f;
import sergey.lib.api.math.Matrix4f;
import sergey.lib.api.math.Vector2f;
import sergey.lib.api.math.Vector3f;
import sergey.lib.api.math.Vector4f;

public class GLUtil {
	/**
	 * Set the value of a mat2 uniform
	 * 
	 * @param location the location of the uniform
	 * @param transpose should the matrix be transposed
	 * @param mat the matrix
	 */
	public static void setUniform(int location, boolean transpose, Matrix2f mat) {
		GL20.glUniformMatrix2fv(location, transpose, mat.getBuffer());
	}
	
	/**
	 * Set the value of a mat3 uniform
	 * 
	 * @param location the location of the uniform
	 * @param transpose should the matrix be transposed
	 * @param mat the matrix
	 */
	public static void setUniform(int location, boolean transpose, Matrix3f mat) {
		GL20.glUniformMatrix3fv(location, transpose, mat.getBuffer());
	}
	
	/**
	 * Set the value of a mat4 uniform
	 * 
	 * @param location the location of the uniform
	 * @param transpose should the matrix be transposed
	 * @param mat the matrix
	 */
	public static void setUniform(int location, boolean transpose, Matrix4f mat) {
		GL20.glUniformMatrix4fv(location, transpose, mat.getBuffer());
	}
	
	/**
	 * Set the value of a vec2 uniform
	 * 
	 * @param location the location of the uniform
	 * @param vec the vector
	 */
	public static void setUniform(int location, Vector2f vec) {
		GL20.glUniform2fv(location, vec.getBuffer());
	}
	
	/**
	 * Set the value of a vec3 uniform
	 * 
	 * @param location the location of the uniform
	 * @param vec the vector
	 */
	public static void setUniform(int location, Vector3f vec) {
		GL20.glUniform3fv(location, vec.getBuffer());
	}
	
	/**
	 * Set the value of a vec4 uniform
	 * 
	 * @param location the location of the uniform
	 * @param vec the vector
	 */
	public static void setUniform(int location, Vector4f vec) {
		GL20.glUniform4fv(location, vec.getBuffer());
	}
}
