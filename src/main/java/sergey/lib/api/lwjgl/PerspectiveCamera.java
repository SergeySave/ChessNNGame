package sergey.lib.api.lwjgl;

import sergey.lib.api.math.Matrix4f;
import sergey.lib.api.math.Vector3f;
import sergey.lib.api.math.Vector4f;

public class PerspectiveCamera {

	private Vector3f position;
	private Vector3f direction;
	private Vector3f up;

	private Matrix4f combined;

	private float fov;
	private float nearPlane;
	private float farPlane;

	public PerspectiveCamera(float fov, float nearPlane, float farPlane, float width, float height) {
		this.fov = fov;
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
		direction = new Vector3f(0, 0, 1);
		position = new Vector3f();
		up = new Vector3f(0, 1, 0);
		//view = new Matrix4f();
		update(width, height);
	}

	public void lookAt(float x, float y, float z) {
		lookAt(new Vector3f(x, y, z));
	}

	public void lookAt(Vector3f pos) {
		Vector3f deltaPos = pos.subtract(position).normalize();

		if (deltaPos.subtract(pos).lengthSquared() != 0) {
			float dot = deltaPos.dot(up); //Up and direction are always orthonormal
			if (Math.abs(dot - 1) < 0.000000001f) { //Epsilon
				//Collinear
				up = direction.scale(-1);
			} else if (Math.abs(dot + 1) < 0.000000001f) {
				//Collinear opposite
				up = direction.copy();
			}
			direction = deltaPos;
			Vector3f cross = direction.cross(up).normalize();
			up = cross.cross(direction).normalize();
		}
	}

	public void translate(float x, float y, float z) {
		translate(new Vector3f(x, y, z));
	}

	public void translate(Vector3f vec) {
		position = position.add(vec);
	}
	
	public void translateLocal(float right, float up, float forward) {
		position = position.add(this.up.scale(-up)).add(this.direction.scale(forward)).add(getPerpendicular().scale(-right));
	}
	
	public void rotate(Vector3f axis, float radians) {
		Matrix4f rotation = Matrix4f.rotate(radians, axis.x, axis.y, axis.z);
		direction = rotation.multiply(direction);
		up = rotation.multiply(up);
	}
	
	public void rotateAround(Vector3f point, Vector3f axis, float radians) {
		Vector3f delta = point.subtract(position);
		translate(delta);
		
		Matrix4f rotation = Matrix4f.rotate(radians, axis.x, axis.y, axis.z);
		direction = rotation.multiply(direction);
		up = rotation.multiply(up);
		delta = rotation.multiply(delta);
		
		translate(delta.negate());
	}
	
	public Vector3f getFacing() {
		return direction;
	}
	
	public Vector3f getUp() {
		return up;
	}
	
	public Vector3f getPerpendicular() {
		return up.cross(direction);
	}

	public void update(float width, float height) {
		//Update the combined matrix
		Vector3f eye = position.copy();
		Vector3f target = eye.add(direction);

		Vector3f zAx = target.subtract(eye).normalize();
		Vector3f xAx = up.cross(zAx).normalize();
		Vector3f yAx = zAx.cross(xAx);

		combined = Matrix4f.perspective(fov, width/height, nearPlane, farPlane)
				.multiply(new Matrix4f(
						new Vector4f(xAx.x, yAx.x, zAx.x, 0),
						new Vector4f(xAx.y, yAx.y, zAx.y, 0),
						new Vector4f(xAx.z, yAx.z, zAx.z, 0),
						new Vector4f(xAx.dot(eye), yAx.dot(eye), zAx.dot(eye), 1))
							/*.multiply(Matrix4f.translate(eye.x, eye.y, eye.z))*/);
	}

	public Matrix4f getCombined() {
		return combined;
	}
}
