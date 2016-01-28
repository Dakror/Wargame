package de.dakror.wargame.util;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

public class WorldLocation implements Location<Vector2> {
	Vector2 position;
	float orientation;
	
	public WorldLocation() {
		position = new Vector2();
		orientation = 0;
	}
	
	public WorldLocation(Vector2 position, float orientation) {
		this.position = position;
		this.orientation = orientation;
	}
	
	@Override
	public Vector2 getPosition() {
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
	public Location<Vector2> newLocation() {
		return new WorldLocation();
	}
	
	@Override
	public float vectorToAngle(Vector2 vector) {
		return VectorToAngle(vector);
	}
	
	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return AngleToVector(outVector, angle);
	}
	
	public static Vector2 AngleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float) Math.sin(angle);
		outVector.y = (float) Math.cos(angle);
		return outVector;
	}
	
	public static float VectorToAngle(Vector2 vector) {
		return (float) Math.atan2(vector.x, vector.y);
	}
}
