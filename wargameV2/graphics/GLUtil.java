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

package de.dakror.wargameV2.graphics;

import static android.opengl.GLES20.*;

import java.io.IOException;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.os.Build;
import android.util.Log;
import de.dakror.wargameV2.Wargame;
import de.dakror.wargameV2.util.MiscUtil;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class GLUtil {
	public static int createProgram(String vertexShaderFile, String fragmentShaderFile) {
		try {
			int vs = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vs, MiscUtil.read(Wargame.instance.getAssets().open(vertexShaderFile)));
			glCompileShader(vs);
			String vsError = glGetShaderInfoLog(vs);
			if (vsError != null && vsError.length() > 0) Log.e("createProgram", vertexShaderFile + ": " + vsError);
			
			int fs = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fs, MiscUtil.read(Wargame.instance.getAssets().open(fragmentShaderFile)));
			glCompileShader(fs);
			String fsError = glGetShaderInfoLog(fs);
			if (fsError != null && fsError.length() > 0) Log.e("createProgram", fragmentShaderFile + ": " + fsError);
			
			int program = glCreateProgram();
			glAttachShader(program, vs);
			glAttachShader(program, fs);
			glLinkProgram(program);
			
			String pError = glGetProgramInfoLog(program);
			if (pError != null && pError.length() > 0) {
				Log.e("createProgram", "LNK: " + pError);
				System.exit(-1);
			}
			
			return program;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static int loadTexture(String textureFile, int filterMin, int filterMag) {
		int[] id = new int[1];
		try {
			glGenTextures(1, id, 0);
			Bitmap bitmap = BitmapFactory.decodeStream(Wargame.instance.getAssets().open(textureFile));
			glBindTexture(GL_TEXTURE_2D, id[0]);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMin);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMag);
			
			GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0);
			
			bitmap.recycle();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return id[0];
	}
	
	public static int loadTexture(String textureFile) {
		return GLUtil.loadTexture(textureFile, GL_NEAREST, GL_NEAREST);
	}
	
}
