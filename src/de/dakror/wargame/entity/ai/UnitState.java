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

package de.dakror.wargame.entity.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.behaviors.ReachOrientation;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.dakror.wargame.entity.Unit;
import de.dakror.wargame.entity.building.Building;
import de.dakror.wargame.util.ERTreeProximity;
import de.dakror.wargame.util.UnitSteering;

/**
 * @author Maximilian Stark | Dakror
 */
public enum UnitState implements State<Unit> {
	GLOBAL_STATE {
		@Override
		public void enter(Unit entity) {
			Proximity<Vector2> proximity = new ERTreeProximity(entity, entity.getWorld().getEntities(), entity.getBoundingRadius()).setFilterType(Unit.class);
			Proximity<Vector2> proximity2 = new ERTreeProximity(entity, entity.getWorld().getEntities(), 1).setFilterType(Building.class).setNearest(1);
			((UnitSteering) entity.getSteeringBehavior()).setGlobal(new BlendedSteering<Vector2>(entity)//
			.add(new Separation<Vector2>(entity, proximity).setDecayCoefficient(1), 1)//
			.add(new Separation<Vector2>(entity, proximity2).setDecayCoefficient(1), 100), 1);
		}
	},
	BUILD_FORMATION {
		@Override
		public void enter(Unit entity) {
			Proximity<Vector2> proximity2 = new ERTreeProximity(entity, entity.getWorld().getEntities(), 2).setFilterType(Building.class).setNearest(1);
			
			((UnitSteering) entity.getSteeringBehavior()).add(new BlendedSteering<Vector2>(entity) //
			.add(new Arrive<Vector2>(entity, entity.getTargetLocation()) //
			.setTimeToTarget(0.1f) //
			.setArrivalTolerance(0.01f) //
			.setDecelerationRadius(1f), 1f) //
			.add(new ReachOrientation<Vector2>(entity, entity.getTargetLocation()) //
			.setTimeToTarget(0.1f) //
			.setAlignTolerance(0.01f) //
			.setDecelerationRadius(MathUtils.PI), 1f) //
			.add(new LookWhereYouAreGoing<Vector2>(entity) //
			.setTimeToTarget(0.1f) //
			.setAlignTolerance(0.01f) //
			.setDecelerationRadius(MathUtils.PI), 1f)//
			.add(new CollisionAvoidance<Vector2>(entity, proximity2), 2500), 1)//
			;
		}
	};
	
	@Override
	public void enter(Unit entity) {}
	
	@Override
	public void update(Unit entity) {}
	
	@Override
	public void exit(Unit entity) {
		((UnitSteering) entity.getSteeringBehavior()).flushState();
	}
	
	@Override
	public boolean onMessage(Unit entity, Telegram telegram) {
		return false;
	}
}
