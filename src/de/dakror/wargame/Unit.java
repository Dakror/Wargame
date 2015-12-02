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
public class Unit extends Entity {
	public static enum AttackKind {
		Machine_Gun,
		Handgun,
		Rocket,
		Arc_Missile,
		Cannon,
		Long_Cannon,
		Bomb,
		Torpedo
	}
	
	public static enum Type implements EntityLifeCycle {
		Infantry(20, 35, false, AttackKind.Machine_Gun, null, 1, 0, null),
		
		;
		
		public final int hp, costs;
		public final boolean superAvailable;
		public final AttackKind weapon0, weapon1;
		public final int receiveStrength0, receiveStrength1;
		public final String alias;
		
		private Type(int hp, int costs, boolean superAvailable, AttackKind weapon0, AttackKind weapon1, int receiveStrength0, int receiveStrength1, String alias) {
			this.hp = hp;
			this.costs = costs;
			this.superAvailable = superAvailable;
			this.weapon0 = weapon0;
			this.weapon1 = weapon1;
			this.receiveStrength0 = receiveStrength0;
			this.receiveStrength1 = receiveStrength1;
			this.alias = alias;
		}
		
		@Override
		public void onCreate() {}
		
		@Override
		public void onSpawn() {}
		
		@Override
		public void update(float timePassed) {}
		
		@Override
		public void onDeath() {}
		
		@Override
		public void onRemoval() {}
		
	}
	
	public Unit(int x, int y, int z, int face, int color, String name) {
		super(x, y, z, face, color, false, name);
	}
	
	@Override
	public void onCreate() {}
	
	@Override
	public void onSpawn() {}
	
	@Override
	public void onDeath() {}
	
	@Override
	public void onRemoval() {}
	
}
