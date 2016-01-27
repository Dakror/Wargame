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

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class SpriteRenderer {
	public static final int VERTEX_SIZE = 10 * 4;
	
	final int kAboSize = 2048;
	final int kEboSize = kAboSize + (kAboSize / 2);
	int vertexIndex = 0, elementIndex = 0;
	int lastTexture;
	int program;
	int[] vbo, ibo;
	
	int[] palettes;
	int[] uPalettes;
	int aPos, aTex, aCol, aPal;
	int uTex, uMat;
	
	FloatBuffer vertices;
	ShortBuffer indices;
	
	float[] matrix;
	
	final float[] WHITE = new float[] { 1, 1, 1, 1 };
	final float[] BLACK = new float[] { 0, 0, 0, 1 };
	
	public SpriteRenderer() {
		ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX_SIZE * kAboSize);
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asFloatBuffer();
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
		program = Wargame.instance.createProgram("shader/sprite.vs", "shader/sprite.fs");
		
		vbo = new int[1];
		glGenBuffers(1, vbo, 0);
		ibo = new int[1];
		glGenBuffers(1, ibo, 0);
		
		palettes = new int[9];
		uPalettes = new int[9];
		for (int i = 0; i < 8; i++)
			palettes[i] = Wargame.instance.loadTexture("palettes/color" + i + ".png");
		palettes[8] = Wargame.instance.loadTexture("palettes/terrain.png");
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, int texture) {
		render(x, y, z, w, h, tx, ty, tw, th, 0, 0, w, h, WHITE, -1, texture);
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, float p, int texture) {
		render(x, y, z, w, h, tx, ty, tw, th, 0, 0, w, h, WHITE, p, texture);
	}
	
	public void render(Sprite sprite) {
		if (sprite.getXOffset() != 0 && sprite.getYOffset() != 0 && sprite.getInnerWidth() != 0 && sprite.getInnerHeight() != 0) render(sprite.getX(), sprite.getY(), sprite.getZ() - 0.001f, sprite.getWidth(), sprite.getHeight(), 0, 0, 1, 1, 0, 0, sprite.getWidth(), sprite.getHeight(), BLACK, 255, sprite.getTextureId());
		render(sprite.getX(), sprite.getY(), sprite.getZ(), sprite.getWidth(), sprite.getHeight(), sprite.getTextureX(), sprite.getTextureY(), sprite.getTextureWidth(), sprite.getTextureHeight(), sprite.getXOffset(), sprite.getYOffset(), sprite.getInnerWidth(), sprite.getInnerHeight(), sprite.getColor(), sprite.getPaletteIndex(), sprite.getTextureId());
	}
	
	public void render(float x, float y, float z, float w, float h, float tx, float ty, float tw, float th, float xOff, float yOff, float innerW, float innerH, float[] c, float p, int texture) {
		if (texture != lastTexture) {
			if (lastTexture != 0) flush();
			lastTexture = texture;
		}
		
		elementIndex += 6;
		
		boolean useOffset = xOff != 0 && yOff != 0 && innerW != 0 && innerH != 0;
		if (useOffset) {
			vertices.put(new float[] { x + xOff, y + yOff + innerH, z, tx, ty, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
			vertices.put(new float[] { x + xOff, y + yOff, z, tx, ty + th, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
			vertices.put(new float[] { x + xOff + innerW, y + yOff, z, tx + tw, ty + th, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
			vertices.put(new float[] { x + xOff + innerW, y + yOff + innerH, z, tx + tw, ty, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
		} else {
			vertices.put(new float[] { x, y + h, z, tx, ty, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
			vertices.put(new float[] { x, y, z, tx, ty + th, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
			vertices.put(new float[] { x + w, y, z, tx + tw, ty + th, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
			vertices.put(new float[] { x + w, y + h, z, tx + tw, ty, c[0], c[1], c[2], c[3], p });
			vertexIndex++;
		}
		
		if (vertexIndex >= kAboSize) flush();
	}
	
	public void begin(float[] matrix) {
		this.matrix = matrix;
		glUseProgram(program);
		aPos = glGetAttribLocation(program, "aPos");
		aTex = glGetAttribLocation(program, "aTex");
		aCol = glGetAttribLocation(program, "aCol");
		aPal = glGetAttribLocation(program, "aPal");
		uMat = glGetUniformLocation(program, "uMat");
		uTex = glGetUniformLocation(program, "uTex");
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, kEboSize * 2, indices, GL_STREAM_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		for (int i = 0; i < 8; i++)
			uPalettes[i] = glGetUniformLocation(program, "color" + i);
		uPalettes[8] = glGetUniformLocation(program, "terrain");
		
		glUniform1i(uTex, 0);
		for (int i = 0; i < 9; i++)
			glUniform1i(uPalettes[i], i + 1);
		glUniformMatrix4fv(uMat, 1, false, matrix, 0);
	}
	
	public void end() {
		if (vertexIndex > 0) flush();
		glUseProgram(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void flush() {
		glEnable(GL_TEXTURE_2D);
		glActiveTexture(GL_TEXTURE0 + 0);
		glBindTexture(GL_TEXTURE_2D, lastTexture);
		
		for (int i = 0; i < palettes.length; i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			glBindTexture(GL_TEXTURE_2D, palettes[i]);
		}
		
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
		
		glDrawElements(GL_TRIANGLES, elementIndex, GL_UNSIGNED_SHORT, 0);
		//		indices.clear();
		elementIndex = 0;
		vertices.clear();
		vertexIndex = 0;
		
		glDisableVertexAttribArray(aPos);
		glDisableVertexAttribArray(aTex);
		glDisableVertexAttribArray(aCol);
		glDisableVertexAttribArray(aPal);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
