package de.dakror.wargame;

import android.opengl.GLES20;

/**
 * @author Maximilian Stark | Dakror
 */
public class Texture {
	public static enum TextureFilter {
		Nearest(GLES20.GL_NEAREST), Linear(GLES20.GL_LINEAR);
		public final int glEnum;
		
		TextureFilter(int glEnum) {
			this.glEnum = glEnum;
		}
	}
	
	public String name;
	public int minFilter, magFilter, width, height;
	
	public int textureId;
}
