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

package de.dakror.wargame.graphics;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Color {
	public static class Colors {
		// Palette URL: http://paletton.com/#uid=33D0u0khxjJ9btcdTnVlJgaqGby
		public static final Color MEDIUM_BLUE = new Color(51 / 255f, 74 / 255f, 103 / 255f, 1);
		public static final Color KHAKI = new Color(157 / 255f, 139 / 255f, 71 / 255f, 1);
		public static final Color MINT = new Color(36 / 255f, 109 / 255f, 95 / 255f, 1);
		public static final Color DARK_RED = new Color(92 / 255f, 30 / 255f, 15 / 255f, 1);
		
		public static final Color WHITE = new Color(1, 1, 1, 1);
		public static final Color RED = new Color(1, 0, 0, 1);
		public static final Color BLACK = new Color(0, 0, 0, 1);
		
		public static final Color ROYAL = new Color(0x4169e1ff);
		public static final Color SLATE = new Color(0x708090ff);
		
		public static final Color YELLOW = new Color(0xffff00ff);
		public static final Color GOLD = new Color(0xffd700ff);
	}
	
	public float r, g, b, a;
	
	public Color(int rgba) {
		r = ((rgba & 0xff000000) >>> 24) / 255f;
		g = ((rgba & 0x00ff0000) >>> 16) / 255f;
		b = ((rgba & 0x0000ff00) >>> 8) / 255f;
		a = ((rgba & 0x000000ff)) / 255f;
	}
	
	public Color(float r, float g, float b) {
		this(r, g, b, 1);
	}
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color(Color c) {
		r = c.r;
		g = c.g;
		b = c.b;
		a = c.a;
	}
	
	public Color set(Color c) {
		r = c.r;
		g = c.g;
		b = c.b;
		a = c.a;
		
		return this;
	}
}
