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
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.graphics.Renderable;
import de.dakror.wargame.graphics.SpriteRenderer;
import de.dakror.wargame.graphics.TextRenderer;
import de.dakror.wargame.util.Listeners.TouchListener;

/**
 * @author Maximilian Stark | Dakror
 */
public class ContextMenu implements Renderable, TouchListener {
	protected Panel panel;
	protected ArrayList<Button> buttons = new ArrayList<Button>();
	protected int width, height;
	protected Entity entity;
	
	public ContextMenu(Entity entity) {
		this.entity = entity;
	}
	
	public ContextMenu(int width, int height, Entity entity) {
		this.width = width;
		this.height = height;
		this.entity = entity;
		panel = new Panel(-Wargame.width / 2, -Wargame.height / 2, width, height, UI.BEIGE);
	}
	
	public void addButton(Button b) {
		buttons.add(b);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		boolean u = false;
		if (entity.getOwner().isHuman()) {
			for (Button b : buttons)
				if (b.onDown(e)) u = true;
		}
		return u;
	}
	
	@Override
	public boolean onUp(MotionEvent e) {
		boolean u = false;
		if (entity.getOwner().isHuman()) {
			for (Button b : buttons)
				if (b.onUp(e)) u = true;
		}
		return u;
	}
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {
		if (panel != null) panel.render(r, t);
		if (entity.getOwner().isHuman()) {
			for (Button b : buttons)
				b.render(r, t);
		}
	}
}
