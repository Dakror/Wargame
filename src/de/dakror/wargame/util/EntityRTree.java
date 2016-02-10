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

import com.newbrightidea.util.RTree;

import de.dakror.wargame.entity.Entity;

/**
 * @author Maximilian Stark | Dakror
 */
public class EntityRTree extends RTree<Entity> {
	final float[] Zero = { 0, 0 };
	
	public void insert(Entity entity) {
		super.insert(new float[] { entity.getRealX(), entity.getRealZ() }, new float[] { entity.getBoundingRadius() * 2, entity.getBoundingRadius() * 2 }, entity);
	}
	
	public boolean delete(Entity entity) {
		return super.delete(new float[] { entity.getRealX(), entity.getRealZ() }, new float[] { entity.getBoundingRadius(), entity.getBoundingRadius() }, entity);
	}
	
	public List<Entity> getAll(float[] worldDims) {
		return search(Zero, worldDims);
	}
}
