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

import com.badlogic.gdx.ai.fma.FormationMember;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

import de.dakror.wargame.Player;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.entity.ai.Messages;
import de.dakror.wargame.entity.ai.UnitState;
import de.dakror.wargame.entity.motion.UnitSteering;
import de.dakror.wargame.entity.motion.WorldLocation;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;
import de.dakror.wargame.world.World;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Unit extends Entity implements FormationMember<Vector2> {
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
		N$A;
		
		public String getName() {
			return name().replace("_", " ").replace("$", "/");
		}
	}
	
	public static enum UnitType {
		Infantry(20, 6, 2, 35, .50f, false, AttackKind.Machine_Gun, AttackKind.N$A, 1, 0, "Infantry"),
		Bazooka(10, 15, 0, 65, 11.0f, false, AttackKind.Handgun, AttackKind.Rocket, 1, 3, "Infantry_P"),
		Medic(28, 0, 2, 110, 15.0f, false, AttackKind.Medkit, AttackKind.N$A, 0, 0, "Volunteer_T"),
		
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
	
	UnitType type;
	WorldLocation target;
	float scale = 0.5f;
	
	StateMachine<Unit, UnitState> stateMachine;
	
	public Unit(float x, float z, int face, Player owner, boolean huge, UnitType type) {
		super(x + (float) (Math.random() / 10), z + (float) (Math.random() / 10), face, owner, huge, type.alias);
		this.type = type;
		maxLinearSpeed = 1;
		maxLinearAcceleration = 20;
		maxAngularSpeed = 25;
		maxAngularAcceleration = 50;
		boundingRadius = 0.15f;
		pos.set(this.x + boundingRadius, this.z + boundingRadius);
		target = new WorldLocation();
		stateMachine = new DefaultStateMachine<Unit, UnitState>(this, UnitState.BUILD_FORMATION, UnitState.GLOBAL_STATE);
		MessageManager.getInstance().addListener(stateMachine, Messages.FORMATION_UPDATED);
		steering = new UnitSteering(this);
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
		stateMachine.update();
		
		type.update(this, deltaTime);
	}
	
	@Override
	public float getX() {
		return (x + (huge ? 1 : 0)) * (World.WIDTH / 2) + z * (World.WIDTH / 2) + world.getPos().x - xOffset;
	}
	
	@Override
	public float getY() {
		return World.HEIGHT - (x + (huge ? 1 : 0)) * (World.DEPTH / 2) + z * (World.DEPTH / 2) + world.getPos().y + yOffset * 2;
	}
	
	@Override
	public float getZ() {
		return (world.getDepth() - z * 2 + x * 2) / 10f;
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
		Wargame.requestSlotUpdate = true;
		stateMachine.getGlobalState().enter(this);
		stateMachine.getCurrentState().enter(this);
	}
	
	@Override
	public Location<Vector2> getTargetLocation() {
		return target;
	}
}
