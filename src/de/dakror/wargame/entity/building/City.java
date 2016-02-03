/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
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

package de.dakror.wargame.entity.building;

import de.dakror.wargame.Player;
import de.dakror.wargame.World.Terrain;

/**
 * @author Maximilian Stark | Dakror
 */
public class City extends Building {
	int radius;
	
	public City(int x, int z, Player owner) {
		super(x, z, owner, Buildings.City);
		hp = 1000;
		buildCosts = 850;
		runCosts = 6;
		function = "Function: Claim land";
		detail1 = "Enables you to place";
		detail2 = "buildings around.";
		radius = 4;
	}
	
	@Override
	public void onSpawn() {
		if (owner.isHuman()) {
			for (int i = -radius; i <= radius; i++)
				for (int j = -radius; j <= radius; j++)
					if (Math.sqrt(i * i + j * j) < radius) world.replace((int) x + i, (int) z + j, Terrain.Forest, Terrain.Mountains);
		}
	}
	
	@Override
	public void onSelect() {
		System.out.println("fee");
	}
	
	public int getRadius() {
		return radius;
	}
	
	@Override
	public void onDeath() {
		if (equals(owner.getMainCity())) System.out.println(owner.getName() + " lost.");
	}
}
