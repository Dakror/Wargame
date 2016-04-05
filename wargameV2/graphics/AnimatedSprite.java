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

import de.dakror.wargameV2.graphics.TextureAtlas.TextureRegion;
import de.dakror.wargameV2.graphics.TextureAtlas.Tile;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class AnimatedSprite extends Sprite {
	protected Tile tile;
	protected int index;
	protected float vAnim, lastAnim;
	protected boolean loopAnim;
	
	public AnimatedSprite() {}
	
	public AnimatedSprite(float x, float y, float z, float width, float height, Tile tile, int color) {
		super(x, y, z, width, height);
		
		this.tile = tile;
		paletteIndex = color;
		
		updateTexture();
	}
	
	protected void updateTexture() {
		textureId = tile.regions.get(index).texture.textureId;
		textureX = tile.regions.get(index).x;
		textureY = tile.regions.get(index).y;
		textureWidth = tile.regions.get(index).width;
		textureHeight = tile.regions.get(index).height;
	}
	
	public void update(float timePassed) {
		if (vAnim > 0) {
			lastAnim -= timePassed;
			if (lastAnim <= 0) {
				index++;
				lastAnim = vAnim;
				
				index = loopAnim ? index % tile.regions.size() : (index >= tile.regions.size() ? tile.regions.size() - 1 : index);
				
				updateTexture();
			}
		}
	}
	
	public TextureRegion getTextureRegion() {
		return tile.regions.get(index);
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public float getAnimationSpeed() {
		return vAnim;
	}
	
	public void setAnimationSpeed(long vAnim) {
		this.vAnim = vAnim;
	}
	
	public boolean isLoopAnimation() {
		return loopAnim;
	}
	
	public void setLoopAnimation(boolean loopAnim) {
		this.loopAnim = loopAnim;
	}
}
