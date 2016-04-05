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

package de.dakror.wargameV2.util;

/**
 * @author Maximilian Stark | Dakror
 */
public class Vector3 {
	public float x, y, z;
	
	public Vector3() {}
	
	public Vector3(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3 add(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		
		return this;
	}
	
	public Vector3 scl(float scl) {
		x *= scl;
		y *= scl;
		z *= scl;
		
		return this;
	}
	
	public Vector3 mul(Vector3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		
		return this;
	}
	
	public Vector3 neg() {
		return scl(-1);
	}
}
