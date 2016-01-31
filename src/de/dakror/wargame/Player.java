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

package de.dakror.wargame;

import de.dakror.wargame.entity.Building;

/**
 * @author Maximilian Stark | Dakror
 */
public class Player {
	public float money;
	int color;
	
	String name;
	
	Building mainCity;
	
	public Player(String name, int color) {
		this.name = name;
		this.color = color;
		money = 2000;
	}
	
	public int getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public void setMainCity(Building mainCity) {
		this.mainCity = mainCity;
	}
	
	public Building getMainCity() {
		return mainCity;
	}
}
