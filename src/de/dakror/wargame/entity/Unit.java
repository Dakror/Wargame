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
import de.dakror.wargame.graphics.Color.Colors;
import de.dakror.wargame.graphics.SpriteRenderer;
import de.dakror.wargame.graphics.TextRenderer;
import de.dakror.wargame.graphics.TextureAtlas.TextureRegion;
import de.dakror.wargame.world.World;
import gnu.trove.TIntProcedure;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Unit extends Entity {
	public static enum AttackKind {
		Arc_Missile,
		Bomb,
		Cannon,
		Handgun,
		Long_Cannon,
		Machine_Gun,
		Rocket,
		Torpedo,
		Medkit,
		NA;
		
		public String getName() {
			return name().replace("_", " ").replace("$", "/");
		}
	}
	
	public static enum UnitType {
		Infantry(20, 6, 2, 35, .50f, false, AttackKind.Machine_Gun, AttackKind.NA, 1, 0, "Infantry"),
		Bazooka(10, 15, 0, 65, 11.0f, false, AttackKind.Handgun, AttackKind.Rocket, 1, 3, "Infantry_P"),
		Medic(28, 0, 2, 110, 15.0f, false, AttackKind.Medkit, AttackKind.NA, 0, 0, "Volunteer_T"),
		
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
	
	public static final int ARMY_SIZE = 25;
	
	public static TextureRegion halo;
	
	UnitType type;
	float scale = 0.5f;
	Unit general;
	int armySize = 0;
	float avgOffsetX, avgOffsetY;
	
	public Unit(float x, float z, int face, Player owner, boolean huge, UnitType type) {
		super(x /*+ (float) (Math.random() / 10)*/, z /*+ (float) (Math.random() / 10)*/, face, owner, huge, type.alias);
		this.type = type;
		boundWidth = 0.25f;
		boundDepth = 0.25f;
		for (int i = 0; i < 4; i++) {
			TextureRegion tr = tile.regions.get(index);
			avgOffsetX += scale * tr.offsetX * tr.texture.width;
			avgOffsetY += scale * tr.offsetY * tr.texture.height;
		}
		avgOffsetX /= 4;
		avgOffsetY /= 4;
		
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
		
		world.getEntities().intersects(x, z, boundWidth, boundDepth, new TIntProcedure() {
			
			@Override
			public boolean execute(int id) {
				if (id != Unit.this.id) System.out.println(world.getEntities().get(id));
				return true;
			}
		});
		
		type.update(this, deltaTime);
	}
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {
		if (general == this) {
			if (halo == null) halo = Wargame.standing.getTile("palette99_Infantry_Large_halo").regions.get(0);
			r.render(getX(), getY(), getZ() - 1, scale * halo.origWidth, scale * halo.origHeight, halo.x, halo.y, halo.width, halo.height, scale * halo.offsetX * halo.texture.width, scale * halo.offsetY * halo.texture.height, scale * halo.width * halo.texture.width, scale * halo.height * halo.texture.height, Colors.GOLD, additive, -1, halo.texture.textureId);
		}
		
		super.render(r, t);
	}
	
	@Override
	public float getX() {
		return ((x + 0.33f) + (huge ? 1 : 0)) * (World.WIDTH / 2) + (z + 0.33f) * (World.WIDTH / 2) + world.getPos().x;
	}
	
	@Override
	public float getY() {
		return World.HEIGHT - (x + (huge ? 1 : 0)) * (World.DEPTH / 2) + z * (World.DEPTH / 2) + world.getPos().y + World.DEPTH / 4 + 2;
	}
	
	@Override
	public float getZ() {
		return (world.getDepth() - z * 2 + x * 2) / 10f;
	}
	
	public boolean isGeneral() {
		return general == this;
	}
	
	public Unit getGeneral() {
		return general;
	}
	
	public void setGeneral(Unit general) {
		this.general = general;
	}
	
	public int getArmySize() {
		return armySize;
	}
	
	public void changeArmySize(int delta) {
		armySize += delta;
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
		if (owner.getGenerals().size() == 0) {
			general = this;
			armySize = 1;
			owner.getGenerals().add(this);
		} else {
			for (Unit u : owner.getGenerals()) {
				if (u.isGeneral() && u.getArmySize() < ARMY_SIZE) {
					general = u;
					u.changeArmySize(1);
					break;
				}
			}
			
			if (general == null) {
				general = this;
				armySize = 1;
				owner.getGenerals().add(this);
			}
		}
	}
}
