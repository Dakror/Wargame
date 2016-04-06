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

/**
 * @author Maximilian Stark | Dakror
 */
public class Vector {
	public float x, y, z;
	
	public Vector() {}
	
	public Vector(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public Vector(float x, float y) {
		this(x, y, 0);
	}
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		
		return this;
	}
	
	public Vector add(Vector v) {
		return add(v.x, v.y, v.z);
	}
	
	public Vector scl(float scl) {
		x *= scl;
		y *= scl;
		z *= scl;
		
		return this;
	}
	
	public Vector mul(Vector v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		
		return this;
	}
	
	public Vector set(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
		
		return this;
	}
	
	public Vector neg() {
		return scl(-1);
	}
	
	// 2D
	public static float dst(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
