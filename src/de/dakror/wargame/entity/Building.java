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

package de.dakror.wargame.entity;

import de.dakror.wargame.World;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Building extends Entity {
	public static enum Type {
		//		Airport(750, 160), // for planes
		//		Castle(2500, 325), // for defense
		City(1000, 125) {
			@Override
			public void onDeath(Building b) {
				System.out.println("Team " + b.getPaletteIndex() + " lost.");
			}
		}, // main building
		//		Dock(650, 90), // for ships
		Estate(350, 35), // for troops
		Factory(550, 75), // for resources
		//		Laboratory(450, 1000), // for science!!!!
		;
		
		public final int hp, costs;
		
		private Type(int hp, int costs) {
			this.hp = hp;
			this.costs = costs;
		}
		
		public void onCreate(Building b) {}
		
		public void onSpawn(Building b) {}
		
		public void update(Building b, float timePassed) {}
		
		public void onDeath(Building b) {}
		
		public void onRemoval(Building b) {}
		
		public void onSelect(Building b) {}
		
		public void onDeselect(Building b) {}
	}
	
	protected Type type;
	
	public Building(int x, int z, int face, int color, boolean huge, Type type) {
		super(x, z, face, color, huge, type.name());
		this.type = type;
		onCreate();
	}
	
	public Building(int x, int z, int color, Type type) {
		this(x, z, 0, color, type);
	}
	
	public Building(int x, int z, int face, int color, Type type) {
		this(x, z, face, color, false, type);
	}
	
	public Building(int x, int z, int color, boolean huge, Type type) {
		this(x, z, 0, color, huge, type);
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		type.update(this, timePassed);
	}
	
	@Override
	protected void updateTexture() {
		tile = faces[face];
		super.updateTexture();
		
		float texWidth = textureWidth * tile.regions.get(index).texture.width;
		
		width = (float) (Math.ceil(texWidth / World.WIDTH) * (World.WIDTH / 2));
		height = (textureHeight * tile.regions.get(index).texture.height) * (width / texWidth);
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public float getX() {
		return (x + (huge ? 1 : 0)) * (World.WIDTH / 2) / 2 + z * (World.WIDTH / 2) / 2 + world.getPos().x + ((World.WIDTH / 2) - width) / 2;
	}
	
	@Override
	public float getY() {
		return y * World.HEIGHT - (x + (huge ? 1 : 0)) * (World.DEPTH / 2) / 2 + z * (World.DEPTH / 2) / 2 + world.getPos().y + World.HEIGHT - 1;
	}
	
	@Override
	public float getZ() {
		return (world.getDepth() - z * 2 + x * 2) / 1024f + (color[3] < 1.0f ? 10 : 0) /*Wargame#placeBuilding*/;
	}
	
	@Override
	public void onCreate() {
		type.onCreate(this);
	}
	
	@Override
	public void onSpawn() {
		type.onSpawn(this);
	}
	
	@Override
	public void onDeath() {
		type.onDeath(this);
	}
	
	@Override
	public void onRemoval() {
		type.onDeath(this);
	}
	
	@Override
	public void onSelect() {
		type.onSelect(this);
	}
	
	@Override
	public void onDeselect() {
		type.onDeselect(this);
	}
}
