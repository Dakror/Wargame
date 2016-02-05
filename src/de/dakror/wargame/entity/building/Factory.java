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

package de.dakror.wargame.entity.building;

import de.dakror.wargame.Player;

/**
 * @author Maximilian Stark | Dakror
 */
public class Factory extends Building {
	public Factory(int x, int z, Player owner) {
		super(x, z, owner, BuildingType.Factory);
		hp = 550;
		buildCosts = 450;
		runCosts = -240;
		function = "Produces cash";
		detail1 = "Prints money so you";
		detail2 = "have more (not really)";
	}
}
