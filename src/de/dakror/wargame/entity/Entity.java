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

import de.dakror.wargame.Player;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.graphics.AnimatedSprite;
import de.dakror.wargame.graphics.Renderable;
import de.dakror.wargame.graphics.SpriteRenderer;
import de.dakror.wargame.graphics.TextRenderer;
import de.dakror.wargame.graphics.TextureAtlas.Tile;
import de.dakror.wargame.world.World;

/**
 * @author Maximilian Stark | Dakror
 */
public abstract class Entity extends AnimatedSprite implements EntityLifeCycle, Comparable<Entity>, Renderable {
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
	
	protected float boundWidth, boundDepth;
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
	
	public void updatePosition() {
		if (newX != x || newZ != z) {
			world.getEntities().update(newX, newZ, this);
			x = newX;
			z = newZ;
		}
	}
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {
		r.render(this);
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
	
	public float getBoundingWidth() {
		return boundWidth;
	}
	
	public float getBoundingDepth() {
		return boundDepth;
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
		width = textureWidth * tile.regions.get(index).texture.width * (huge ? 2 : 1);
		height = textureHeight * tile.regions.get(index).texture.height * (huge ? 2 : 1);
	}
}
