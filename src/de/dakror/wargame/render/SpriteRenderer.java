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

package de.dakror.wargame.render;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class SpriteRenderer {
	public static final int VERTEX_SIZE = 14 * 4;
	
	final int kAboSize = VERTEX_SIZE * 1024;
	final int kEboSize = kAboSize + (kAboSize / 2);
	int vertexIndex = 0;
	int lastTexture;
	int program;
	int[] vbo, ibo;
	
	//	float[][] palettes;
	int[] palettes;
	int[] uPalettes;
	int aPos, aTex, aCol, aPal, aAdd;
	int uTex, uMat;
	
	FloatBuffer vertices;
	ShortBuffer indices;
	
	boolean drawBuffer1 = true;
	
	float[] matrix;
	
	public SpriteRenderer() {
		vertices = FloatBuffer.allocate(kAboSize);
		ByteBuffer blb = ByteBuffer.allocateDirect(kEboSize * 2);
		blb.order(ByteOrder.nativeOrder());
		indices = blb.asShortBuffer();
		for (int i = 0; i < kAboSize; i += 4) {
			indices.put((short) i);
			indices.put((short) (i + 1));
			indices.put((short) (i + 2));
			indices.put((short) i);
			indices.put((short) (i + 2));
			indices.put((short) (i + 3));
		}
		
		indices.position(0);
		program = GLUtil.createProgram("shader/sprite.vs", "shader/sprite.fs");
		
		vbo = new int[1];
		glGenBuffers(1, vbo, 0);
		ibo = new int[1];
		glGenBuffers(1, ibo, 0);
		
		palettes = new int[9];
		uPalettes = new int[9];
		for (int i = 0; i < 9; i++)
			palettes[i] = GLUtil.loadTexture("palettes/" + (i < 8 ? ("color" + i) : "terrain") + ".png");
			
		glUseProgram(program);
		aPos = glGetAttribLocation(program, "aPos");
		aTex = glGetAttribLocation(program, "aTex");
		aCol = glGetAttribLocation(program, "aCol");
		aPal = glGetAttribLocation(program, "aPal");
		aAdd = glGetAttribLocation(program, "aAdd");
		uMat = glGetUniformLocation(program, "uMat");
		uTex = glGetUniformLocation(program, "uTex");
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, kEboSize * 2, indices, GL_STREAM_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		for (int i = 0; i < 8; i++)
			uPalettes[i] = glGetUniformLocation(program, "color" + i);
		uPalettes[8] = glGetUniformLocation(program, "terrain");
		glUseProgram(0);
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, int texture) {
		render(x, y, z, w, h, tx, ty, tw, th, 0, 0, w, h, Color.WHITE, Color.BLACK, -1, texture);
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, Color color, int texture) {
		render(x, y, z, w, h, tx, ty, tw, th, 0, 0, w, h, color, Color.BLACK, -1, texture);
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, float p, int texture) {
		render(x, y, z, w, h, tx, ty, tw, th, 0, 0, w, h, Color.WHITE, Color.BLACK, p, texture);
	}
	
	public void render(Sprite sprite) {
		render(sprite.getX(), sprite.getY(), sprite.getZ(), sprite.getWidth(), sprite.getHeight(), sprite.getTextureX(), sprite.getTextureY(), sprite.getTextureWidth(), sprite.getTextureHeight(), sprite.getXOffset(), sprite.getYOffset(), sprite.getInnerWidth(), sprite.getInnerHeight(), sprite.getColor(), sprite.getAdditive(), sprite.getPaletteIndex(), sprite.getTextureId());
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, float xOff, float yOff, float innerW, float innerH, Color c, Color a, float p, int texture) {
		if (texture != lastTexture) {
			if (lastTexture != 0) flush();
			lastTexture = texture;
		}
		
		if (vertexIndex + 4 >= (kAboSize / (VERTEX_SIZE / 4))) flush();
		boolean useOffset = xOff != 0 && yOff != 0 && innerW != 0 && innerH != 0;
		if (useOffset) {
			put(x + xOff, y + yOff + innerH, z, tx, ty, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a, //
			x + xOff, y + yOff, z, tx, ty + th, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a, //
			x + xOff + innerW, y + yOff, z, tx + tw, ty + th, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a, //
			x + xOff + innerW, y + yOff + innerH, z, tx + tw, ty, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a);
		} else {
			put(x, y + h, z, tx, ty, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a, //
			x, y, z, tx, ty + th, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a, //
			x + w, y, z, tx + tw, ty + th, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a, //
			x + w, y + h, z, tx + tw, ty, c.r, c.g, c.b, c.a, p, a.r, a.g, a.b, a.a);
		}
	}
	
	void put(float... f) {
		System.arraycopy(f, 0, vertices.array(), vertexIndex * (VERTEX_SIZE / 4), f.length);
		vertexIndex += 4;
	}
	
	public void begin(float[] matrix) {
		this.matrix = matrix;
		
		glUseProgram(program);
		glUniform1i(uTex, 0);
		for (int i = 0; i < 9; i++)
			glUniform1i(uPalettes[i], i + 1);
		//			glUniform3fv(uPalettes[i], 256, palettes[i], 0);
		glUniformMatrix4fv(uMat, 1, false, matrix, 0);
		
		glEnable(GL_TEXTURE_2D);
		for (int i = 0; i < palettes.length; i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			glBindTexture(GL_TEXTURE_2D, palettes[i]);
		}
	}
	
	public void end() {
		if (vertexIndex > 0) flush();
		glUseProgram(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void flush() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, lastTexture);
		
		vertices.position(0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		glBufferData(GL_ARRAY_BUFFER, vertexIndex * VERTEX_SIZE, vertices, GL_STREAM_DRAW);
		
		glEnableVertexAttribArray(aPos);
		glVertexAttribPointer(aPos, 3, GL_FLOAT, false, VERTEX_SIZE, 0);
		
		glEnableVertexAttribArray(aTex);
		glVertexAttribPointer(aTex, 2, GL_FLOAT, false, VERTEX_SIZE, 3 * 4);
		
		glEnableVertexAttribArray(aCol);
		glVertexAttribPointer(aCol, 4, GL_FLOAT, false, VERTEX_SIZE, 5 * 4);
		
		glEnableVertexAttribArray(aPal);
		glVertexAttribPointer(aPal, 1, GL_FLOAT, false, VERTEX_SIZE, 9 * 4);
		
		glEnableVertexAttribArray(aAdd);
		glVertexAttribPointer(aAdd, 4, GL_FLOAT, false, VERTEX_SIZE, 10 * 4);
		
		glDrawElements(GL_TRIANGLES, vertexIndex * 3 / 2, GL_UNSIGNED_SHORT, 0);
		vertexIndex = 0;
		
		glDisableVertexAttribArray(aPos);
		glDisableVertexAttribArray(aTex);
		glDisableVertexAttribArray(aCol);
		glDisableVertexAttribArray(aPal);
		glDisableVertexAttribArray(aAdd);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
