/*******************************************************************************
 * Copyright 2016 Maximilian Stark | Dakror <mail@dakror.de>
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

package de.dakror.wargame.entity;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.proximities.InfiniteProximity;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.dakror.wargame.Player;
import de.dakror.wargame.World;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;
import de.dakror.wargame.util.WorldLocation;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Unit extends Entity implements Steerable<Vector2> {
	public static class UnitTypeProximity extends InfiniteProximity<Vector2> {
		public UnitTypeProximity(Unit owner, Array<Unit> agents) {
			super(owner, agents);
		}
	}
	
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
	
	public static enum UnitType {
		Infantry(20, 6, 2, 35, 5.0f, false, AttackKind.Machine_Gun, null, 1, 0, null),
		
		;
		
		public final String alias;
		public final int hp, atk, def, costs;
		public final float produceDuration; // in seconds
		public final int receiveStrength0, receiveStrength1;
		public final boolean superAvailable;
		public final AttackKind weapon0, weapon1;
		
		private UnitType(int hp, int atk, int def, int costs, float produceDuration, boolean superAvailable, AttackKind weapon0, AttackKind weapon1, int receiveStrength0, int receiveStrength1, String alias) {
			this.hp = hp;
			this.atk = atk;
			this.def = def;
			this.costs = costs;
			this.produceDuration = produceDuration;
			this.superAvailable = superAvailable;
			this.weapon0 = weapon0;
			this.weapon1 = weapon1;
			this.receiveStrength0 = receiveStrength0;
			this.receiveStrength1 = receiveStrength1;
			this.alias = alias;
		}
		
		public void onCreate(Unit u) {}
		
		public void onDeath(Unit u) {}
		
		public void onDeselect(Unit u) {}
		
		public void onRemoval(Unit u) {}
		
		public void onSelect(Unit u) {}
		
		public void onSpawn(Unit u) {}
		
		public void update(Unit u, float timePassed) {}
	}
	
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	boolean tagged;
	boolean independentFacing = false;
	float maxLinearSpeed = 2;
	float maxLinearAcceleration = 10;
	float maxAngularSpeed = 5;
	float maxAngularAcceleration = 10;
	float angularVelocity;
	float boundingRadius;
	float orientation;
	Vector2 linearVelocity = new Vector2();
	Vector2 pos = new Vector2();
	UnitType type;
	SteeringBehavior<Vector2> steeringBehavior;
	float scale = 0.5f;
	
	public Unit(float x, float z, int face, Player owner, boolean huge, UnitType type) {
		super(x, z, face, owner, huge, type.name());
		this.type = type;
		pos.set(x, z);
		onCreate();
	}
	
	public Unit(float x, float z, Player owner, UnitType type) {
		this(x, z, 0, owner, type);
	}
	
	public Unit(float x, float z, int face, Player owner, UnitType type) {
		this(x, z, face, owner, false, type);
	}
	
	public Unit(float x, float z, Player owner, boolean huge, UnitType type) {
		this(x, z, 0, owner, huge, type);
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
		
		type.update(this, deltaTime);
	}
	
	private void applySteering(SteeringAcceleration<Vector2> steering, float deltaTime) {
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
		return (pos.x + (huge ? 1 : 0)) * (World.WIDTH / 2) + pos.y * (World.WIDTH / 2) + world.getPos().x - xOffset + ((World.WIDTH) - width) / 4;
	}
	
	@Override
	public float getY() {
		return 2 * World.HEIGHT - (pos.x + (huge ? 1 : 0)) * (World.DEPTH / 2) + pos.y * (World.DEPTH / 2) + world.getPos().y + yOffset * 2;
	}
	
	@Override
	public float getZ() {
		return (world.getDepth() - pos.y * 2 + pos.x * 2) / 1024f;
	}
	
	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return WorldLocation.AngleToVector(outVector, angle);
	}
	
	@Override
	public float vectorToAngle(Vector2 vector) {
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
	public Vector2 getLinearVelocity() {
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
	public Vector2 getPosition() {
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
	
	public SteeringBehavior<Vector2> getSteeringBehavior() {
		return steeringBehavior;
	}
	
	public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
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
	public Location<Vector2> newLocation() {
		return new WorldLocation();
	}
	
	@Override
	public void onCreate() {
		type.onCreate(this);
	}
	
	@Override
	public void onDeath() {
		type.onDeath(this);
	}
	
	@Override
	public void onDeselect() {
		type.onDeselect(this);
	}
	
	@Override
	public void onRemoval() {
		type.onRemoval(this);
	}
	
	@Override
	public void onSelect() {
		type.onSelect(this);
	}
	
	@Override
	public void onSpawn() {
		type.onSpawn(this);
	}
}
