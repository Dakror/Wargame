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

package de.dakror.wargame.ui;

import de.dakror.wargame.Wargame;
import de.dakror.wargame.graphics.SpriteRenderer;
import de.dakror.wargame.graphics.TextRenderer;
import de.dakror.wargame.graphics.TextureAtlas.TextureRegion;

/**
 * @author Maximilian Stark | Dakror
 */
public class ProgressBar extends Component {
	TextureRegion backLeft, backMid, backRight;
	TextureRegion barLeft, barMid, barRight;
	String color;
	float value;
	
	public ProgressBar(int x, int y, int width, float value, String color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.value = value;
		this.color = color;
		backLeft = Wargame.ui.getTile("barBack_horizontalLeft").regions.get(0);
		backMid = Wargame.ui.getTile("barBack_horizontalMid").regions.get(0);
		backRight = Wargame.ui.getTile("barBack_horizontalRight").regions.get(0);
		
		barLeft = Wargame.ui.getTile("bar" + color + "_horizontalLeft").regions.get(0);
		barMid = Wargame.ui.getTile("bar" + color + "_horizontalMid").regions.get(0);
		barRight = Wargame.ui.getTile("bar" + color + "_horizontalRight").regions.get(0);
	}
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {
		r.render(x, y, 0, backLeft.origWidth * UI.DEFAULT_SCALE, getHeight(), backLeft.x, backLeft.y, backLeft.width, backLeft.height, backLeft.texture.textureId);
		r.render(x + backLeft.origWidth * UI.DEFAULT_SCALE, y, 0, width - (backLeft.origWidth + backRight.origWidth) * UI.DEFAULT_SCALE, getHeight(), backMid.x, backMid.y, backMid.width, backMid.height, backMid.texture.textureId);
		r.render(x + width - backRight.origWidth * UI.DEFAULT_SCALE, y, 0, backRight.origWidth * UI.DEFAULT_SCALE, getHeight(), backRight.x, backRight.y, backRight.width, backRight.height, backRight.texture.textureId);
		
		float w = barLeft.origWidth * UI.DEFAULT_SCALE;
		float u = width * value;
		float v = Math.min(w, u);
		float s = Math.max(0, u - (width - w));
		
		r.render(x, y, 0, v, getHeight(), barLeft.x, barLeft.y, barLeft.width * v / w, barLeft.height, barLeft.texture.textureId);
		r.render(x + w, y, 0, Math.min(width - 2 * w, Math.max(0, u - w)), getHeight(), barMid.x, barMid.y, barMid.width, barMid.height, barMid.texture.textureId);
		r.render(x + width - w, y, 0, s, getHeight(), barRight.x, barRight.y, barRight.width * s / w, barRight.height, barRight.texture.textureId);
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public int getHeight() {
		return backMid.origHeight * UI.DEFAULT_SCALE;
	}
}















