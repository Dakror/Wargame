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
public abstract class Entity extends AnimatedSprite {
	World world;
	Tile[] faces;
	boolean huge;
	int face;
	boolean dead;
	
	public Entity(int x, int y, int z, int face, boolean huge, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.huge = huge;
		this.face = face;
		vAnim = 0.05f;
		loopAnim = true;
		faces = MainActivity.standing.getFaces("palette99_" + name + "_" + (huge ? "Huge" : "Large"));
		
		updateTexture();
	}
	
	public Entity(int x, int y, int z, int face, String name) {
		this(x, y, z, face, false, name);
	}
	
	public Entity(int x, int y, int z, boolean huge, String name) {
		this(x, y, z, 0, huge, name);
	}
	
	public Entity(int x, int y, int z, String name) {
		this(x, y, z, 0, false, name);
	}
	
	@Override
	protected void updateTexture() {
		tile = faces[face];
		super.updateTexture();
		width = textureWidth * tile.regions.get(index).texture.width;
		height = textureHeight * tile.regions.get(index).texture.height;
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
		return super.getX() + world.getPos().x;
	}
	
	@Override
	public float getY() {
		return super.getY() + world.getPos().y + World.HEIGHT;
	}
	
	@Override
	public float getZ() {
		return super.getZ() + world.getPos().z;
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
