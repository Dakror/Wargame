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

import de.dakror.wargame.TextureAtlas.Tile;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Entity extends AnimatedSprite {
	World world;
	Tile[] faces;
	int face;
	boolean dead;
	
	public Entity(int x, int y, int z, String name, int face) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.face = face;
		vAnim = 0.05f;
		loopAnim = true;
		faces = MainActivity.standing.getFaces(name);
		
		updateTexture();
	}
	
	public Entity(int x, int y, int z, String name) {
		this(x, y, z, name, 0);
	}
	
	@Override
	protected void updateTexture() {
		tile = faces[face];
		super.updateTexture();
		width = textureWidth * 2048;//(float) Math.ceil((textureWidth * 2048) / World.WIDTH) * World.WIDTH;
		height = textureHeight * 2048;//(width / (textureWidth * 2048)) * 2048;
	}
	
	public int getFace() {
		return face;
	}
	
	public void setFace(int face) {
		this.face = face;
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
	
	@Override
	public float getX() {
		return super.getX() * World.WIDTH / 2 + super.getZ() * World.WIDTH / 2 + world.getPos().x + (World.WIDTH - width) / 2;//- world.width * World.WIDTH / 2;
	}
	
	@Override
	public float getY() {
		return super.getY() * World.HEIGHT - super.getX() * World.DEPTH / 2 + super.getZ() * World.DEPTH / 2 + world.getPos().y;
	}
	
	@Override
	public float getZ() {
		return super.getY() * World.HEIGHT + super.getX() * World.DEPTH / 2 + world.getPos().z + World.HEIGHT;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
}
