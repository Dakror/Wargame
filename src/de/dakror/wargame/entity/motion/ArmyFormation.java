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

import com.badlogic.gdx.ai.fma.FormationPattern;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class ArmyFormation implements FormationPattern<Vector2> {
	int numberOfSlots;
	float memberSize;
	
	public ArmyFormation(float memberSize) {
		this.memberSize = memberSize;
	}
	
	@Override
	public void setNumberOfSlots(int numberOfSlots) {
		this.numberOfSlots = numberOfSlots;
	}
	
	@Override
	public Location<Vector2> calculateSlotLocation(Location<Vector2> outLocation, int slotNumber) {
		if (numberOfSlots > 1) {
			int width = (numberOfSlots / 5) + 5;
			
			outLocation.getPosition().set((slotNumber % width) * memberSize, -slotNumber / width * memberSize); // not yet there
		} else {
			outLocation.getPosition().setZero();
		}
		
		outLocation.setOrientation(0);
		
		return outLocation;
	}
	
	@Override
	public boolean supportsSlots(int slotCount) {
		return true;
	}
	
}
