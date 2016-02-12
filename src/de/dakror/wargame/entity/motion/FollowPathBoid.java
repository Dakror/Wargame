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
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;

/**
 * @author Maximilian Stark | Dakror
 */
public class FollowPathBoid<T extends Vector<T>> extends Arrive<T> {
	Array<T> path;
	int currentNode;
	
	float radius;
	
	public FollowPathBoid(Steerable<T> owner, Array<T> path) {
		super(owner);
		if (path.size < 2) throw new IllegalArgumentException("Path must contain >= 2 nodes");
		this.path = path;
		currentNode = 0;
	}
	
	public FollowPathBoid<T> setRadius(float radius) {
		this.radius = radius;
		return this;
	}
	
	@Override
	protected SteeringAcceleration<T> calculateRealSteering(SteeringAcceleration<T> steering) {
		if (currentNode < path.size - 1) {
			T toTarget = steering.linear.set(path.get(currentNode)).sub(owner.getPosition());
			if (toTarget.len() <= radius) currentNode++;
			steering.linear.nor().scl(getActualLimiter().getMaxLinearAcceleration()); // seek target at max speed
			
			return steering;
		} else {
			return arrive(steering, path.peek());
		}
	}
}
