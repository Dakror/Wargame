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

import com.badlogic.gdx.graphics.Color;

import de.dakror.wargame.Player;
import de.dakror.wargame.World;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.ui.Panel;
import de.dakror.wargame.util.Colors;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Building extends Entity {
	public static enum Type {
		//		Airport(750, 160), // for planes
		//		Castle(2500, 325), // for defense
		City(1000, 850) {
			@Override
			public void onDeath(Building b) {
				if (b.equals(b.owner.getMainCity())) System.out.println(b.owner.getName() + " lost.");
			}
			
			@Override
			public void update(Building b, float timePassed) {
				b.owner.money -= 6 / 60f * timePassed; // maintenance cost per minute
			}
			
			@Override
			public void renderDetails(Panel p, SpriteRenderer r, TextRenderer t) {
				super.renderDetails(p, r, t);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 140, 0, 0.5f, Colors.KHAKI, "Run cost: 6$/min", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 180, 0, 0.5f, Color.ROYAL, "Function: 404", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 230, 0, 0.5f, Color.WHITE, "If your main city is", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 260, 0, 0.5f, Color.WHITE, "destroyed you lose!", r);
			}
		}, // main building
		//		Dock(650, 90), // for ships
		Estate(350, 1575) {
			@Override
			public void update(Building b, float timePassed) {
				b.owner.money -= 12 / 60f * timePassed; // maintenance cost or whatever lmao
			}
			
			@Override
			public void renderDetails(Panel p, SpriteRenderer r, TextRenderer t) {
				super.renderDetails(p, r, t);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 140, 0, 0.5f, Colors.KHAKI, "Run cost: 12$/min", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 180, 0, 0.5f, Color.ROYAL, "Trains Infantry", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 230, 0, 0.5f, Color.WHITE, "These soldiers do the", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 260, 0, 0.5f, Color.WHITE, "dirty work for you.", r);
			}
		}, // for troops
		Factory(550, 450) {// for resources
			@Override
			public void update(Building b, float timePassed) {
				b.owner.money += 3 * timePassed;
			}
			
			@Override
			public void renderDetails(Panel p, SpriteRenderer r, TextRenderer t) {
				super.renderDetails(p, r, t);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 140, 0, 0.5f, Colors.MINT, "Profits: 240$/min", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 180, 0, 0.5f, Color.ROYAL, "Produces cash", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 230, 0, 0.5f, Color.WHITE, "Printing money so you", r);
				t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 260, 0, 0.5f, Color.WHITE, "have more (not really)", r);
			}
		},
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
		
		public void renderDetails(Panel p, SpriteRenderer r, TextRenderer t) {
			t.renderText(p.getX() + 20, p.getY() + p.getHeight() - 60, 0, 0.8f, Colors.MEDIUM_BLUE, name(), r);
			t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 100, 0, 0.5f, Colors.DARK_RED, "Costs: $" + costs, r);
		}
	}
	
	protected Type type;
	
	public Building(int x, int z, int face, Player owner, boolean huge, Type type) {
		super(x, z, face, owner, huge, type.name());
		this.type = type;
		onCreate();
	}
	
	public Building(int x, int z, Player owner, Type type) {
		this(x, z, 0, owner, type);
	}
	
	public Building(int x, int z, int face, Player owner, Type type) {
		this(x, z, face, owner, false, type);
	}
	
	public Building(int x, int z, Player owner, boolean huge, Type type) {
		this(x, z, 0, owner, huge, type);
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
		return (world.getDepth() - z * 2 + x * 2) / 1024f + (color.a < 1.0f ? 10 : 0) /*Wargame#placeBuilding*/;
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
