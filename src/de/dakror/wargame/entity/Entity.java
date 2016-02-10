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
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

import de.dakror.wargame.Player;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.World;
import de.dakror.wargame.render.AnimatedSprite;
import de.dakror.wargame.render.TextureAtlas.Tile;
import de.dakror.wargame.util.WorldLocation;

/**
 * @author Maximilian Stark | Dakror
 */
public abstract class Entity extends AnimatedSprite implements EntityLifeCycle, Comparable<Entity>, Steerable<Vector2> {
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	protected boolean dead;
	/**
	 * 0 = X+<br>
	 * 1 = Z-<br>
	 * 2 = X-<br>
	 * 3 = Z+<br>
	 */
	protected int face;
	protected Tile[] faces;
	protected boolean huge;
	protected World world;
	protected Player owner;
	
	protected boolean tagged;
	protected boolean independentFacing = false;
	protected float maxLinearSpeed;
	protected float maxLinearAcceleration;
	protected float maxAngularSpeed;
	protected float maxAngularAcceleration;
	protected float angularVelocity;
	protected float boundingRadius;
	protected float orientation;
	protected Vector2 linearVelocity = new Vector2();
	protected Vector2 pos = new Vector2();
	protected SteeringBehavior<Vector2> steering;
	protected float newX, newZ;
	public int id;
	
	public Entity(float x, float z, int face, Player owner, boolean huge, String name) {
		this.x = x;
		y = 1;
		this.z = z;
		newX = x;
		newZ = z;
		this.huge = huge;
		this.face = face;
		paletteIndex = owner.getColor();
		this.owner = owner;
		vAnim = 0.125f;
		loopAnim = true;
		faces = Wargame.standing.getFaces("palette99_" + name + "_" + (huge ? "Huge" : "Large"));
		
		updateTexture();
	}
	
	public int getFace() {
		return face;
	}
	
	public float getRealX() {
		return x;
	}
	
	public float getRealY() {
		return y;
	}
	
	public float getRealZ() {
		return z;
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		
		if (steering != null) {
			steering.calculateSteering(steeringOutput);
			applySteering(steeringOutput, timePassed);
			
			newX = pos.x - boundingRadius;
			newZ = pos.y - boundingRadius;
			
			face = ((((int) Math.round(Math.toDegrees(orientation) + 360)) % 360) / 90 + 3) % 4;
			updateTexture();
		}
	}
	
	public void updatePosition() {
		if (newX != x || newZ != z) {
			world.getEntities().update(newX, newZ, this);
			x = newX;
			z = newZ;
		}
	}
	
	protected void applySteering(SteeringAcceleration<Vector2> steering, float timePassed) {
		pos.mulAdd(linearVelocity, timePassed);
		if (steering.linear.isZero(0.5f)) linearVelocity.setZero();
		else linearVelocity.mulAdd(steering.linear, timePassed).limit(getMaxLinearSpeed());
		
		if (independentFacing) {
			orientation += angularVelocity * timePassed;
			angularVelocity += steering.angular * timePassed;
		} else if (!linearVelocity.isZero(getZeroLinearSpeedThreshold())) {
			float newOrientation = vectorToAngle(linearVelocity);
			angularVelocity = (newOrientation - getOrientation()) * timePassed; // this is superfluous if independentFacing is always true
			orientation = newOrientation;
		}
	}
	
	@Override
	public float getX() {
		return (x + (huge ? 1 : 0)) * (World.WIDTH) / 2 + z * (World.WIDTH) / 2 + world.getPos().x + ((World.WIDTH) - width) / 4;
	}
	
	@Override
	public float getY() {
		return y * World.HEIGHT - (x + (huge ? 1 : 0)) * (World.DEPTH) / 2 + z * (World.DEPTH) / 2 + world.getPos().y + World.HEIGHT - 1;
	}
	
	@Override
	public float getZ() {
		return (y * World.HEIGHT + (x + (huge ? 1 : 0)) * (World.DEPTH) / 2 + world.getPos().z + World.HEIGHT) / 1024f;
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
		return 1f;
	}
	
	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		throw new UnsupportedOperationException();
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
	
	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void setFace(int face) {
		this.face = face;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	@Override
	public int compareTo(Entity another) {
		return Float.compare(getZ(), another.getZ());
	}
	
	@Override
	protected void updateTexture() {
		tile = faces[face];
		super.updateTexture();
		width = textureWidth * tile.regions.get(index).texture.width;
		height = textureHeight * tile.regions.get(index).texture.height;
	}
}
