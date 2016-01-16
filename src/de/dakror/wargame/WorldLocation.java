package de.dakror.wargame;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector3;

public class WorldLocation implements Location<Vector3> {
	Vector3 position;
	float orientation;
	
	public WorldLocation() {
		position = new Vector3();
		orientation = 0;
	}
	
	public WorldLocation(Vector3 position, float orientation) {
		this.position = position;
		this.orientation = orientation;
	}
	
	@Override
	public Vector3 getPosition() {
		return position;
	}
	
	@Override
	public float getOrientation() {
		return orientation;
	}
	
	@Override
	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}
	
	@Override
	public Location<Vector3> newLocation() {
		return new WorldLocation();
	}
	
	@Override
	public float vectorToAngle(Vector3 vector) {
		return VectorToAngle(vector);
	}
	
	@Override
	public Vector3 angleToVector(Vector3 outVector, float angle) {
		return AngleToVector(outVector, angle);
	}
	
	public static Vector3 AngleToVector(Vector3 outVector, float angle) {
		outVector.x = (float) Math.sin(angle);
		outVector.y = 0;
		outVector.z = (float) Math.cos(angle);
		return outVector;
	}
	
	public static float VectorToAngle(Vector3 vector) {
		return (float) Math.atan2(vector.x, vector.z);
	}
}
