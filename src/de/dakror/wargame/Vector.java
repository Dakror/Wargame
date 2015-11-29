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

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Vector {
	public float x, y, z;
	
	public Vector() {
		this(0, 0, 0);
	}
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vector add(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}
	
	public Vector set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector set(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}
	
	public Vector normalize() {
		float l = length();
		x /= l;
		y /= l;
		z /= l;
		return this;
	}
	
	@Override
	public String toString() {
		return x + ", " + y + ", " + z;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector) return ((Vector) o).x == x && ((Vector) o).y == y && ((Vector) o).z == z;
		return false;
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
}
