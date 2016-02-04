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

package de.dakror.wargame.render;

import com.badlogic.gdx.graphics.Color;

import de.dakror.wargame.render.TextureAtlas.TextureRegion;

/**
 * @author Maximilian Stark | Dakror
 */
public class Sprite {
	protected float x, y, z;
	protected float width, height;
	protected float textureX, textureY, textureWidth, textureHeight;
	protected float xOffset, yOffset, innerWidth, innerHeight;
	protected float srcWidth, srcHeight;
	protected Color color = new Color(Color.WHITE), additive = new Color(Color.BLACK);
	protected int textureId;
	protected int paletteIndex;
	
	public Sprite() {}
	
	public Sprite(float x, float y, float z, float width, float height) {
		this(x, y, z, width, height, 0, 0, 1, 1);
	}
	
	public Sprite(float x, float y, float z, float width, float height, float textureX, float textureY, float textureWidth, float textureHeight) {
		this(x, y, z, width, height, textureX, textureY, textureWidth, textureHeight, 0, 0, width, height);
	}
	
	public Sprite(float x, float y, float z, float width, float height, TextureRegion tr) {
		this(x, y, z, width, height, -1, tr);
	}
	
	public Sprite(int color, TextureRegion tr) {
		this(0, 0, 0, 0, 0, color, tr);
		srcWidth = tr.width * tr.texture.width;
		srcHeight = tr.height * tr.texture.height;
	}
	
	public Sprite(float x, float y, float z, float width, float height, int color, TextureRegion tr) {
		this(x, y, z, width, height, tr.x, tr.y, tr.width, tr.height);
		textureId = tr.texture.textureId;
		paletteIndex = color;
	}
	
	public Sprite(float x, float y, float z, float width, float height, float textureX, float textureY, float textureWidth, float textureHeight, float xOffset, float yOffset, float innerWidth, float innerHeight) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.textureX = textureX;
		this.textureY = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.innerWidth = innerWidth;
		this.innerHeight = innerHeight;
		paletteIndex = -1;
	}
	
	public float getSourceWidth() {
		return srcWidth;
	}
	
	public float getSourceHeight() {
		return srcHeight;
	}
	
	public int getPaletteIndex() {
		return paletteIndex;
	}
	
	public void setPaletteIndex(int paletteIndex) {
		this.paletteIndex = paletteIndex;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setAdditive(Color additive) {
		this.additive = additive;
	}
	
	public Color getAdditive() {
		return additive;
	}
	
	public int getTextureId() {
		return textureId;
	}
	
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getTextureX() {
		return textureX;
	}
	
	public void setTextureX(float textureX) {
		this.textureX = textureX;
	}
	
	public float getTextureY() {
		return textureY;
	}
	
	public void setTextureY(float textureY) {
		this.textureY = textureY;
	}
	
	public float getTextureWidth() {
		return textureWidth;
	}
	
	public void setTextureWidth(float textureWidth) {
		this.textureWidth = textureWidth;
	}
	
	public float getTextureHeight() {
		return textureHeight;
	}
	
	public float getXOffset() {
		return xOffset;
	}
	
	public void setXOffset(float xOffset) {
		this.xOffset = xOffset;
	}
	
	public float getYOffset() {
		return yOffset;
	}
	
	public void setYOffset(float yOffset) {
		this.yOffset = yOffset;
	}
	
	public float getInnerWidth() {
		return innerWidth;
	}
	
	public void setInnerWidth(float innerWidth) {
		this.innerWidth = innerWidth;
	}
	
	public float getInnerHeight() {
		return innerHeight;
	}
	
	public void setInnerHeight(float innerHeight) {
		this.innerHeight = innerHeight;
	}
	
	public void setTextureHeight(float textureHeight) {
		this.textureHeight = textureHeight;
	}
}
