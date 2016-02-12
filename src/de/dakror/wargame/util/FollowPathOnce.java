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

import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.Path;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.math.Vector;

/**
 * @author Maximilian Stark | Dakror
 */
public class FollowPathOnce<T extends Vector<T>> extends FollowPath<T, LinePathParam> {
	public FollowPathOnce(Steerable<T> owner, Path<T, LinePathParam> path, float pathOffset, float predictionTime) {
		super(owner, path, pathOffset, predictionTime);
	}
	
	@Override
	protected SteeringAcceleration<T> arrive(SteeringAcceleration<T> steering, T targetPosition) {
		// Get the direction and distance to the target
		T toTarget = steering.linear.set(targetPosition).sub(owner.getPosition());
		float distance = toTarget.len();
		
		// Check if we are there, return no steering
		if (distance <= arrivalTolerance) {
			enabled = false; // thats all I changed
			return steering.setZero();
		}
		
		Limiter actualLimiter = getActualLimiter();
		// Go max speed
		float targetSpeed = actualLimiter.getMaxLinearSpeed();
		
		// If we are inside the slow down radius calculate a scaled speed
		if (distance <= decelerationRadius) targetSpeed *= distance / decelerationRadius;
		
		// Target velocity combines speed and direction
		T targetVelocity = toTarget.scl(targetSpeed / distance); // Optimized code for: toTarget.nor().scl(targetSpeed)
		
		// Acceleration tries to get to the target velocity without exceeding max acceleration
		// Notice that steering.linear and targetVelocity are the same vector
		targetVelocity.sub(owner.getLinearVelocity()).scl(1f / timeToTarget).limit(actualLimiter.getMaxLinearAcceleration());
		
		// No angular acceleration
		steering.angular = 0f;
		
		// Output the steering
		return steering;
	}
	
}
