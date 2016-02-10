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

import java.util.List;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;

import de.dakror.wargame.entity.Entity;

/**
 * @author Maximilian Stark | Dakror
 */
public class EntityRTreeProximity1 implements Proximity<Vector2> {
	Entity owner;
	float radius;
	final float[] size;
	Class<?> filterType;
	EntityRTree1 entities;
	
	public EntityRTreeProximity1(Entity owner, EntityRTree1 entities, float radius) {
		this.owner = owner;
		this.entities = entities;
		this.radius = radius;
		size = new float[2];
		//		size = new float[] { radius * 5, radius * 5 };
	}
	
	public EntityRTreeProximity1 setFilterType(Class<?> c) {
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
	
	@Override
	public int findNeighbors(ProximityCallback<Vector2> callback) {
		List<Entity> list = entities.search(new float[] { owner.getRealX(), owner.getRealZ() }, size); // TODO generalize this for many calls and a wider subarea
		int num = 0;
		for (Entity e : list) {
			if (e == owner) continue;
			if (filterType != null && !filterType.isInstance(e)) continue;
			if (Vector2.dst(owner.getRealX(), owner.getRealZ(), e.getRealX(), e.getRealZ()) > radius) continue;
			
			callback.reportNeighbor(e);
			num++;
		}
		
		return num;
	}
	
}
