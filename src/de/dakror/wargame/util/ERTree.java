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

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

import de.dakror.wargame.entity.Entity;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntProcedure;

/**
 * @author Maximilian Stark | Dakror
 */
public class ERTree extends RTree {
	final Rectangle r_cache = new Rectangle();
	final Point p_cache = new Point(0, 0);
	final TIntObjectHashMap<Entity> map = new TIntObjectHashMap<Entity>(1024);
	int idCount = 0;
	
	public ERTree() {
		init(null);
	}
	
	public void add(Entity e) {
		e.id = idCount++;
		map.put(e.id, e);
		add(setCache(e), e.id);
	}
	
	Rectangle setCache(float x, float y, float width, float height) {
		r_cache.set(x, y, x + width, y + height);
		return r_cache;
	}
	
	Rectangle setCache(Entity e) {
		return setCache(e.getRealX(), e.getRealZ(), e.getBoundingWidth(), e.getBoundingDepth());
	}
	
	public Entity get(int id) {
		return map.get(id);
	}
	
	public boolean delete(Entity e, boolean update) {
		if (delete(setCache(e), e.id)) {
			if (!update) map.remove(e.id);
			return true;
		}
		return false;
	}
	
	public boolean delete(Entity e) {
		return delete(e, false);
	}
	
	public void update(float newX, float newZ, Entity e) {
		if (delete(e, true)) add(setCache(newX, newZ, e.getBoundingWidth(), e.getBoundingDepth()), e.id);
	}
	
	public void intersects(float x, float z, float width, float depth, TIntProcedure callback) {
		intersects(setCache(x, z, width, depth), callback);
	}
	
	public void contains(float x, float z, float width, float depth, TIntProcedure callback) {
		contains(setCache(x, z, width, depth), callback);
	}
	
	public void nearestN(float x, float z, int n, TIntProcedure callback, float furthest) {
		p_cache.x = x;
		p_cache.y = z;
		nearestN(p_cache, callback, n, furthest);
	}
	
	public void nearest(float x, float z, TIntProcedure callback, float furthest) {
		p_cache.x = x;
		p_cache.y = z;
		nearest(p_cache, callback, furthest);
	}
	
	public Object[] getAll() {
		return map.getValues();
	}
}
