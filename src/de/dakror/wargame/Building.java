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

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Building extends Entity {
	public static enum Type {
		Airport,
		Castle,
		City,
		Dock,
		Estate,
		Factory,
		Laboratory;
		
		private Type() {}
	}
	
	protected Type type;
	
	public Building(int x, int y, int z, int face, boolean huge, Type type) {
		super(x, y, z, face, huge, type.name());
		this.type = type;
	}
	
	public Building(int x, int y, int z, Type type) {
		this(x, y, z, 0, type);
	}
	
	public Building(int x, int y, int z, int face, Type type) {
		this(x, y, z, face, false, type);
	}
	
	public Building(int x, int y, int z, boolean huge, Type type) {
		this(x, y, z, 0, huge, type);
	}
	
	@Override
	protected void updateTexture() {
		tile = faces[face];
		super.updateTexture();
		
		float texWidth = textureWidth * tile.regions.get(index).texture.width;
		
		width = (float) (Math.ceil(texWidth / World.WIDTH) * World.WIDTH);
		height = (textureHeight * tile.regions.get(index).texture.height) * (width / texWidth);
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public float getX() {
		return (x + (huge ? 1 : 0)) * World.WIDTH / 2 + z * World.WIDTH / 2 + world.getPos().x + (World.WIDTH - width) / 2;
	}
	
	@Override
	public float getY() {
		return y * World.HEIGHT - (x + (huge ? 1 : 0)) * World.DEPTH / 2 + z * World.DEPTH / 2 + world.getPos().y + World.HEIGHT;
	}
	
	@Override
	public float getZ() {
		return y * World.HEIGHT + (x + (huge ? 1 : 0)) * World.DEPTH / 2 + world.getPos().z + World.HEIGHT + 1337;
	}
}
