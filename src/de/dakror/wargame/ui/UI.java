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
 *
 */
public class UI {
	public static final String BEIGE = "_beige";
	public static final String BEIGE_LIGHT = "_beigeLight";
	public static final String BROWN = "_brown";
	public static final String BLUE = "_blue";
	public static final String GREY = "_grey";
	
	public static final String BAR_BLUE = "Blue";
	public static final String BAR_GREEN = "Green";
	public static final String BAR_RED = "Red";
	public static final String BAR_YELLOW = "Yellow";
	
	public static final String BTN_LONG = "Long";
	public static final String BTN_ROUND = "Round";
	public static final String BTN_SQUARE = "Square";
	
	public static final int DEFAULT_SCALE = 3;
	
	public static final int BTN_LONG_WIDTH = 190 * DEFAULT_SCALE;
	public static final int BTN_ROUND_WIDTH = 35 * DEFAULT_SCALE;
	public static final int BTN_SQUARE_WIDTH = 45 * DEFAULT_SCALE;
	
	public static final int BTN_HEIGHT = 49 * DEFAULT_SCALE;
	public static final int BTN_PRESSED_HEIGHT = 45 * DEFAULT_SCALE;
	
	public static void renderStats(float x, float y, float width, int hp, int atk, int def, SpriteRenderer r, TextRenderer t) {
		final float icoHeight = 35;
		TextureRegion heartIcon = Wargame.ui.getTile("hud_heartFull").regions.get(0);
		float hW = heartIcon.origWidth * (icoHeight / heartIcon.origHeight);
		r.render(x, y - 8, 0, hW, icoHeight, heartIcon.x, heartIcon.y, heartIcon.width, heartIcon.height, heartIcon.texture.textureId);
		t.renderText(x + hW, y - 1, 0, 0.5f, Color.SLATE, hp + "", r);
		
		TextureRegion swordIcon = Wargame.ui.getTile("cursorSword_gold").regions.get(0);
		float sW = swordIcon.origWidth * (icoHeight / swordIcon.origHeight);
		r.render(x + width / 3 - 20, y - 8, 0, sW, icoHeight, swordIcon.x, swordIcon.y, swordIcon.width, swordIcon.height, swordIcon.texture.textureId);
		t.renderText(x + width / 3 - 15 + sW, y - 1, 0, 0.5f, Color.SLATE, atk + "", r);
		
		TextureRegion shieldIcon = Wargame.ui.getTile("shieldSilver2").regions.get(0);
		float dW = shieldIcon.origWidth * (icoHeight / shieldIcon.origHeight);
		r.render(x + width / 3 * 2 - 20, y - 8, 0, dW, icoHeight, shieldIcon.x, shieldIcon.y, shieldIcon.width, shieldIcon.height, shieldIcon.texture.textureId);
		t.renderText(x + width / 3 * 2 - 15 + dW, y - 1, 0, 0.5f, Color.SLATE, def + "", r);
	}
}
