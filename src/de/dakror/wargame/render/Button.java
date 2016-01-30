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

import android.view.MotionEvent;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Button implements Renderable, TouchListener {
	public static final String BEIGE = "_beige";
	public static final String BROWN = "_brown";
	public static final String BLUE = "_blue";
	
	public static final String LONG = "Long";
	public static final String ROUND = "Round";
	public static final String SQUARE = "Square";
	
	public static final int DEFAULT_SCALE = 3;
	
	public static final int LONG_WIDTH = 190 * DEFAULT_SCALE;
	public static final int ROUND_WIDTH = 35 * DEFAULT_SCALE;
	public static final int SQUARE_WIDTH = 45 * DEFAULT_SCALE;
	
	public static final int HEIGHT = 49 * DEFAULT_SCALE;
	public static final int PRESSED_HEIGHT = 45 * DEFAULT_SCALE;
	
	TextureRegion background, backgroundPressed, backgroundDisabled;
	TextureRegion foreground, foregroundPressed, foregroundDisabled;
	
	boolean pressed, disabled;
	int x, y;
	int width, pressedHeight;
	
	TouchListener listener;
	
	public Button(int x, int y, String color, String type) {
		this.x = x;
		this.y = y;
		background = Wargame.ui.getTile("button" + type + color).regions.get(0);
		backgroundPressed = Wargame.ui.getTile("button" + type + color + "_pressed").regions.get(0);
		backgroundDisabled = Wargame.ui.getTile("button" + type + "_grey").regions.get(0);
		
		width = (int) (DEFAULT_SCALE * background.width * background.texture.width);
	}
	
	@Override
	public void render(SpriteRenderer r) {
		renderTextureRegion(x, y, disabled ? backgroundDisabled : (pressed ? backgroundPressed : background), r);
	}
	
	private void renderTextureRegion(int x, int y, TextureRegion tr, SpriteRenderer r) {
		r.render(x, y, 0, width, getHeight(), tr.x, tr.y, tr.width, tr.height, tr.texture.textureId);
	}
	
	@Override
	public boolean onUp(MotionEvent e) {
		if (!disabled) {
			if (contains(e) && pressed && listener != null) listener.onUp(e);
			pressed = false;
		}
		return false;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		if (contains(e) && !disabled) {
			pressed = true;
			if (listener != null) listener.onDown(e);
			return true;
		}
		
		return false;
	}
	
	public boolean contains(MotionEvent e) {
		return e.getX() - Wargame.width / 2 >= x && e.getX() - Wargame.width / 2 <= x + width && Wargame.height - e.getY() - Wargame.height / 2 >= y && Wargame.height - e.getY() - Wargame.height / 2 <= y + HEIGHT;
	}
	
	public void setListener(TouchListener listener) {
		this.listener = listener;
	}
	
	public TouchListener getListener() {
		return listener;
	}
	
	public TextureRegion getForeground() {
		return foreground;
	}
	
	public void setForeground(TextureRegion foreground) {
		this.foreground = foreground;
	}
	
	public TextureRegion getForegroundPressed() {
		return foregroundPressed;
	}
	
	public void setForegroundPressed(TextureRegion foregroundPressed) {
		this.foregroundPressed = foregroundPressed;
	}
	
	public TextureRegion getForegroundDisabled() {
		return foregroundDisabled;
	}
	
	public void setForegroundDisabled(TextureRegion foregroundDisabled) {
		this.foregroundDisabled = foregroundDisabled;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return pressed ? PRESSED_HEIGHT : HEIGHT;
	}
}
