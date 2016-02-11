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

package de.dakror.wargame.util;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class UnitSteering extends BlendedSteering<Vector2> {
	public UnitSteering(Steerable<Vector2> owner) {
		super(owner);
	}
	
	public BlendedSteering<Vector2> setGlobal(SteeringBehavior<Vector2> global, float weight) {
		global.setOwner(owner);
		if (list.size == 0) list.add(new BehaviorAndWeight<Vector2>(global, weight));
		else list.insert(0, new BehaviorAndWeight<Vector2>(global, weight));
		
		return this;
	}
	
	@Override
	public BlendedSteering<Vector2> add(SteeringBehavior<Vector2> behavior, float weight) {
		if (list.size == 0) {
			System.err.println("no global state in UnitSteering!");
			return this;
		}
		
		return super.add(behavior, weight);
	}
	
	public int getStateSize() {
		return list.size - 1;
	}
	
	public void flushState() {
		list.truncate(1);
	}
}
