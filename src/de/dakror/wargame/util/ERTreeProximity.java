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

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;

import de.dakror.wargame.entity.Entity;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class ERTreeProximity implements Proximity<Vector2> {
	int nearest = 10;
	Entity owner;
	float radius;
	Class<?> filterType;
	ERTree entities;
	
	public ERTreeProximity(Entity owner, ERTree entities, float radius) {
		this.owner = owner;
		this.entities = entities;
		this.radius = radius;
	}
	
	public ERTreeProximity setFilterType(Class<?> c) {
		filterType = c;
		return this;
	}
	
	@Override
	public Steerable<Vector2> getOwner() {
		return owner;
	}
	
	@Override
	public void setOwner(Steerable<Vector2> owner) {
		this.owner = (Entity) owner;
	}
	
	public ERTreeProximity setNearest(int nearest) {
		this.nearest = nearest;
		return this;
	}
	
	@Override
	public int findNeighbors(final ProximityCallback<Vector2> callback) {
		ResultProcedure<Integer> p = new ResultProcedure<Integer>() {
			{
				result = 0;
			}
			
			@Override
			public boolean execute(int id) {
				Entity e = entities.get(id);
				if (e == owner) return true;
				if (filterType != null && !filterType.isInstance(e)) return true;
				if (Vector2.dst(owner.getRealX(), owner.getRealZ(), e.getRealX(), e.getRealZ()) > radius) return true;
				
				result++;
				callback.reportNeighbor(e);
				return true;
			}
		};
		
		entities.nearestN(owner.getRealX(), owner.getRealZ(), nearest, p, radius);
		return p.getResult();
	}
}
