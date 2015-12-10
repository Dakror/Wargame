/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.dakror.wargame;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector3;

import de.dakror.wargame.TextureAtlas.TextureRegion;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Unit extends Entity implements Steerable<Vector3> {
	public static enum AttackKind {
		Arc_Missile,
		Bomb,
		Cannon,
		Handgun,
		Long_Cannon,
		Machine_Gun,
		Rocket,
		Torpedo
	}
	
	public static enum Type implements EntityLifeCycle {
		Infantry(20, 35, false, AttackKind.Machine_Gun, null, 1, 0, null),
		
		;
		
		public final String alias;
		public final int hp, costs;
		public final int receiveStrength0, receiveStrength1;
		public final boolean superAvailable;
		public final AttackKind weapon0, weapon1;
		
		private Type(int hp, int costs, boolean superAvailable, AttackKind weapon0, AttackKind weapon1, int receiveStrength0, int receiveStrength1, String alias) {
			this.hp = hp;
			this.costs = costs;
			this.superAvailable = superAvailable;
			this.weapon0 = weapon0;
			this.weapon1 = weapon1;
			this.receiveStrength0 = receiveStrength0;
			this.receiveStrength1 = receiveStrength1;
			this.alias = alias;
		}
		
		@Override
		public void onCreate() {}
		
		@Override
		public void onDeath() {}
		
		@Override
		public void onDeselect() {}
		
		@Override
		public void onRemoval() {}
		
		@Override
		public void onSelect() {}
		
		@Override
		public void onSpawn() {}
		
		@Override
		public void update(float timePassed) {}
	}
	
	private static final SteeringAcceleration<Vector3> steeringOutput = new SteeringAcceleration<Vector3>(new Vector3());
	
	boolean tagged;
	boolean independentFacing = true;
	float maxLinearSpeed = 2;
	float maxLinearAcceleration = 10;
	float maxAngularSpeed = 5;
	float maxAngularAcceleration = 10;
	float angularVelocity;
	float boundingRadius;
	float orientation;
	Vector3 linearVelocity = new Vector3();
	Vector3 pos = new Vector3();
	Type type;
	SteeringBehavior<Vector3> steeringBehavior;
	float scale = 0.5f;
	
	public Unit(float x, float y, float z, int face, int color, boolean huge, Type type) {
		super(x, y, z, face, color, huge, type.name());
		this.type = type;
		pos.set(x, y, z);
		onCreate();
	}
	
	public Unit(float x, float y, float z, int color, Type type) {
		this(x, y, z, 0, color, type);
	}
	
	public Unit(float x, float y, float z, int face, int color, Type type) {
		this(x, y, z, face, color, false, type);
	}
	
	public Unit(float x, float y, float z, int color, boolean huge, Type type) {
		this(x, y, z, 0, color, huge, type);
	}
	
	@Override
	protected void updateTexture() {
		super.updateTexture();
		
		TextureRegion tr = tile.regions.get(index);
		
		innerWidth = scale * width;
		innerHeight = scale * height;
		width = scale * tr.origWidth;
		height = scale * tr.origHeight;
		xOffset = scale * tr.offsetX * tr.texture.width;
		yOffset = scale * tr.offsetY * tr.texture.height;
		//		innerWidth = width;
		//		innerHeight = height;
		//		width = tr.origWidth;
		//		height = tr.origHeight;
		//		xOffset = tr.offsetX * tr.texture.width;
		//		yOffset = tr.offsetY * tr.texture.height;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (steeringBehavior != null) {
			steeringBehavior.calculateSteering(steeringOutput);
			applySteering(steeringOutput, deltaTime);
			
			face = ((((int) Math.round(Math.toDegrees(orientation) + 360)) % 360) / 90 + 3) % 4;
			updateTexture();
		}
	}
	
	private void applySteering(SteeringAcceleration<Vector3> steering, float deltaTime) {
		pos.mulAdd(linearVelocity, deltaTime);
		linearVelocity.mulAdd(steering.linear, deltaTime).limit(getMaxLinearSpeed());
		
		if (independentFacing) {
			orientation += angularVelocity * deltaTime;
			angularVelocity += steering.angular * deltaTime;
		} else if (!linearVelocity.isZero(getZeroLinearSpeedThreshold())) {
			float newOrientation = vectorToAngle(linearVelocity);
			angularVelocity = (newOrientation - getOrientation()) * deltaTime; // this is superfluous if independentFacing is always true
			orientation = newOrientation;
		}
	}
	
	@Override
	public float getX() {
		return (pos.x + (huge ? 1 : 0)) * (World.WIDTH * World.SCALE) / 2 + pos.z * (World.WIDTH * World.SCALE) / 2 + world.getPos().x;// + ((World.WIDTH) - width) / 4;
	}
	
	@Override
	public float getY() {
		return pos.y * World.HEIGHT - (pos.x + (huge ? 1 : 0)) * (World.DEPTH * World.SCALE) / 2 + pos.z * (World.DEPTH * World.SCALE) / 2 + world.getPos().y + World.HEIGHT;
	}
	
	@Override
	public float getZ() {
		return (pos.y * World.HEIGHT + (pos.x + (huge ? 1 : 0)) * (World.DEPTH * World.SCALE) / 2 + world.getPos().z + World.HEIGHT) / 1024f;
	}
	
	@Override
	public Vector3 angleToVector(Vector3 outVector, float angle) {
		return WorldLocation.AngleToVector(outVector, angle);
	}
	
	@Override
	public float vectorToAngle(Vector3 vector) {
		return WorldLocation.VectorToAngle(vector);
	}
	
	@Override
	public float getAngularVelocity() {
		return angularVelocity;
	}
	
	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}
	
	public boolean isIndependentFacing() {
		return independentFacing;
	}
	
	public void setIndependentFacing(boolean independentFacing) {
		this.independentFacing = independentFacing;
	}
	
	@Override
	public Vector3 getLinearVelocity() {
		return linearVelocity;
	}
	
	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}
	
	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}
	
	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}
	
	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}
	
	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}
	
	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}
	
	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}
	
	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}
	
	@Override
	public float getOrientation() {
		return orientation; //(float) (face * Math.PI);
	}
	
	@Override
	public void setOrientation(float orientation) {
		face = (int) Math.round(orientation / Math.PI);
	}
	
	@Override
	public Vector3 getPosition() {
		return pos;
	}
	
	@Override
	public float getZeroLinearSpeedThreshold() {
		return 0.001f;
	}
	
	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		throw new UnsupportedOperationException();
	}
	
	public SteeringBehavior<Vector3> getSteeringBehavior() {
		return steeringBehavior;
	}
	
	public void setSteeringBehavior(SteeringBehavior<Vector3> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
	}
	
	@Override
	public boolean isTagged() {
		return tagged;
	}
	
	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
	
	@Override
	public Location<Vector3> newLocation() {
		return new WorldLocation();
	}
	
	@Override
	public void onCreate() {
		type.onCreate();
	}
	
	@Override
	public void onDeath() {
		type.onDeath();
	}
	
	@Override
	public void onDeselect() {
		type.onDeselect();
	}
	
	@Override
	public void onRemoval() {
		type.onRemoval();
	}
	
	@Override
	public void onSelect() {
		type.onSelect();
	}
	
	@Override
	public void onSpawn() {
		type.onSpawn();
	}
}
