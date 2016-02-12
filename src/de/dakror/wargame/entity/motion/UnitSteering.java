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

package de.dakror.wargame.entity.motion;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.math.Vector;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class UnitSteering<T extends Vector<T>> extends PrioritySteering<T> {
	public UnitSteering(Steerable<T> owner) {
		super(owner);
	}
	
	public UnitSteering<T> setGlobal(SteeringBehavior<T> global) {
		global.setOwner(owner);
		if (behaviors.size == 0) behaviors.add(global);
		else behaviors.insert(0, global);
		
		return this;
	}
	
	@Override
	public UnitSteering<T> add(SteeringBehavior<T> behavior) {
		if (behaviors.size == 0) {
			System.err.println("no global state in UnitSteering!");
			return this;
		}
		super.add(behavior);
		return this;
	}
	
	public int getStateSize() {
		return behaviors.size - 1;
	}
	
	public void flushState() {
		behaviors.truncate(1);
	}
}
