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

/**
 * @author Maximilian Stark | Dakror
 */
public class City extends Building {
	public City(int x, int z, Player owner) {
		super(x, z, owner, Type.City);
		hp = 1000;
		buildCosts = 850;
		runCosts = 6;
		function = "Function: 404";
		detail1 = "If your main city is";
		detail2 = "destroyed you lose!";
	}
	
	@Override
	public void onDeath() {
		if (equals(owner.getMainCity())) System.out.println(owner.getName() + " lost.");
	}
}
