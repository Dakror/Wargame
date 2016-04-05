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

import android.opengl.GLES20;

/**
 * @author Maximilian Stark | Dakror
 */
public class Texture {
	public static enum TextureFilter {
		Nearest(GLES20.GL_NEAREST),
		Linear(GLES20.GL_LINEAR);
		public final int glEnum;
		
		TextureFilter(int glEnum) {
			this.glEnum = glEnum;
		}
	}
	
	public String name;
	public int minFilter, magFilter, width, height;
	
	public int textureId;
}
