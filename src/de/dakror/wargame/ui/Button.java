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

import java.util.ArrayList;

import android.view.MotionEvent;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.render.Sprite;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;
import de.dakror.wargame.util.Listeners.ButtonListener;
import de.dakror.wargame.util.Listeners.TouchListener;

/**
 * @author Maximilian Stark | Dakror
 */
public class Button extends Component implements TouchListener {
	TextureRegion background, backgroundPressed, backgroundDisabled;
	TextureRegion backgroundToggle, backgroundTogglePressed, backgroundToggleDisabled;
	Sprite foreground;
	
	boolean pressed, toggled, disabled;
	int pressedHeight;
	
	ArrayList<ButtonListener> listeners;
	
	Object payload;
	String color, type;
	float padding = 15, paddingBot = 5;
	
	/**
	 * Initialize typical buyButton (square)
	 */
	public Button(int index, int y, Sprite foreground, ButtonListener listener, Object payload, boolean toggle) {
		type = UI.BTN_SQUARE;
		x = Wargame.width / 2 - UI.BTN_SQUARE_WIDTH * index - 15;
		this.y = -Wargame.height / 2 + 15 + y * (UI.BTN_HEIGHT + 5);
		setColor(UI.BROWN);
		listeners = new ArrayList<ButtonListener>();
		listeners.add(listener);
		this.foreground = foreground;
		this.payload = payload;
		width = (int) (UI.DEFAULT_SCALE * background.width * background.texture.width);
		if (toggle) setToggle(UI.BEIGE);
	}
	
	public Button(int x, int y, String color, String type) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.type = type;
		background = Wargame.ui.getTile("button" + type + color).regions.get(0);
		backgroundPressed = Wargame.ui.getTile("button" + type + color + "_pressed").regions.get(0);
		backgroundDisabled = Wargame.ui.getTile("button" + type + "_grey").regions.get(0);
		listeners = new ArrayList<ButtonListener>();
		width = (int) (UI.DEFAULT_SCALE * background.width * background.texture.width);
	}
	
	public Button setToggle(String toggleColor) {
		backgroundToggle = Wargame.ui.getTile("button" + type + toggleColor).regions.get(0);
		backgroundTogglePressed = Wargame.ui.getTile("button" + type + toggleColor + "_pressed").regions.get(0);
		backgroundToggleDisabled = Wargame.ui.getTile("button" + type + "_grey").regions.get(0);
		
		return this;
	}
	
	public Button setPadding(float p, float b) {
		padding = p;
		paddingBot = b;
		return this;
	}
	
	public Button setColor(String color) {
		this.color = color;
		background = Wargame.ui.getTile("button" + type + color).regions.get(0);
		backgroundPressed = Wargame.ui.getTile("button" + type + color + "_pressed").regions.get(0);
		backgroundDisabled = Wargame.ui.getTile("button" + type + "_grey").regions.get(0);
		return this;
	}
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {
		TextureRegion tr = toggled ? (disabled ? backgroundToggleDisabled : (pressed ? backgroundTogglePressed : backgroundToggle)) : (disabled ? backgroundDisabled : (pressed ? backgroundPressed : background));
		r.render(x, y, 0, width, getHeight(), tr.x, tr.y, tr.width, tr.height, tr.texture.textureId);
		
		if (foreground != null) {
			foreground.setZ(0);
			foreground.resizeSoft(width - padding * 2, getHeight() - padding * 2);
			foreground.setX(x + (width - foreground.getWidth()) / 2);
			foreground.setY(y + (pressed ? UI.BTN_PRESSED_HEIGHT - UI.BTN_HEIGHT : 0) + (padding + paddingBot));
			r.render(foreground);
		}
	}
	
	@Override
	public boolean onUp(MotionEvent e) {
		if (!disabled) {
			if (contains(e) && pressed) {
				if (backgroundToggle != null) toggled = !toggled;
				for (ButtonListener l : listeners)
					l.onUp(this);
			}
			pressed = false;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		if (contains(e) && !disabled) {
			pressed = true;
			for (ButtonListener l : listeners)
				l.onDown(this);
			return true;
		}
		
		return false;
	}
	
	public boolean contains(MotionEvent e) {
		return e.getX() - Wargame.width / 2 >= x && e.getX() - Wargame.width / 2 <= x + width && Wargame.height - e.getY() - Wargame.height / 2 >= y && Wargame.height - e.getY() - Wargame.height / 2 <= y + UI.BTN_HEIGHT;
	}
	
	public void addListener(ButtonListener listener) {
		listeners.add(listener);
	}
	
	public Object getPayload() {
		return payload;
	}
	
	public void setPayload(Object payload) {
		this.payload = payload;
	}
	
	public ArrayList<ButtonListener> getListener() {
		return listeners;
	}
	
	public Sprite getForeground() {
		return foreground;
	}
	
	public void setForeground(Sprite foreground) {
		this.foreground = foreground;
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
	
	public boolean isToggled() {
		return toggled;
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
	
	@Override
	public int getHeight() {
		return pressed ? UI.BTN_PRESSED_HEIGHT : UI.BTN_HEIGHT;
	}
}
