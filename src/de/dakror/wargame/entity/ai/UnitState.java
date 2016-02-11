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
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.dakror.wargame.entity.Unit;
import de.dakror.wargame.entity.building.Building;
import de.dakror.wargame.util.ERTreeProximity;
import de.dakror.wargame.util.UnitSteering;
import de.dakror.wargame.world.TiledSmoothableGraphPath;

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
			.add(new Separation<Vector2>(entity, proximity2).setDecayCoefficient(1), 1), 1);
		}
	},
	BUILD_FORMATION {
		@Override
		public boolean onMessage(Unit entity, Telegram telegram) {
			if (telegram.message == Messages.FORMATION_UPDATED) {
				TiledSmoothableGraphPath p = new TiledSmoothableGraphPath();
				entity.getWorld().pathFinder.searchNodePath(entity.getWorld().get(entity.getPosition()), entity.getWorld().get(entity.getTargetLocation().getPosition()), entity.getWorld().heuristic, p);
				//entity.getWorld().pathSmoother.smoothPath(p);
				
				Array<Vector2> nodes = new Array<Vector2>(p.getCount());
				nodes.add(entity.getPosition());
				
				float dx = entity.getPosition().x % 1;
				float dz = entity.getPosition().y % 1;
				
				for (int i = 1; i < p.getCount(); i++) {
					nodes.add(p.getNodePosition(i).cpy().add(dx, dz));
				}
				
				LinePath<Vector2> path = new LinePath<Vector2>(nodes, true);
				((UnitSteering) entity.getSteeringBehavior()).flushState();
				((UnitSteering) entity.getSteeringBehavior()).add(//
				new FollowPath<Vector2, LinePathParam>(entity, path, 0.01f)//
				.setTimeToTarget(0.01f)//
				.setPredictionTime(0.1f)//
				.setArrivalTolerance(0.05f)//
				.setDecelerationRadius(0.2f), 1);
				
				return true;
			}
			return false;
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
