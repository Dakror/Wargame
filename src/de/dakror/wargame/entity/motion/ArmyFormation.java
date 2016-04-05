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

import android.location.Location;

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
			int width = Math.min(numberOfSlots, (numberOfSlots / 3) + 3);
			int numOfThisRow = Math.min(width, numberOfSlots - ((int) Math.floor(slotNumber / (float) width) * width));
			System.out.println(slotNumber + ", " + width + ", " + numberOfSlots + ", " + numOfThisRow);
			outLocation.getPosition().set((slotNumber % numOfThisRow) * memberSize - ((numOfThisRow - 1) * memberSize * 0.5f), -slotNumber / width * memberSize);
			System.out.println(outLocation.getPosition().x + ", " + ((numOfThisRow - 1) * memberSize));
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
