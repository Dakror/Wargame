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

package de.dakror.wargame.world;

/**
 * @author Maximilian Stark | Dakror
 */
public class CanBuildResult {
	public boolean result;
	/**
	 * 0 nothing
	 * 1 no space
	 * 2 building there
	 * 3 too far from city
	 */
	public int reason;
	
	public CanBuildResult() {
		result = true;
	}
	
	public CanBuildResult(int reason) {
		if (reason == 0) result = true;
		else {
			result = false;
			this.reason = reason;
		}
	}
}
