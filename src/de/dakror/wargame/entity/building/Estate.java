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

package de.dakror.wargame.entity.building;

import com.badlogic.gdx.utils.Queue;

import de.dakror.wargame.Player;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.entity.Unit;
import de.dakror.wargame.entity.Unit.UnitType;
import de.dakror.wargame.render.Sprite;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.ui.Button;
import de.dakror.wargame.ui.ContextMenu;
import de.dakror.wargame.ui.Panel;
import de.dakror.wargame.ui.ProgressBar;
import de.dakror.wargame.ui.UI;
import de.dakror.wargame.util.Colors;
import de.dakror.wargame.util.Listeners.ButtonListener;

/**
 * @author Maximilian Stark | Dakror
 */
public class Estate extends Building {
	public static class ProductionTask {
		UnitType unitType;
		float timeLeft, allTime;
		Button src;
		boolean paid;
		
		public ProductionTask(UnitType unitType, Button src) {
			this.unitType = unitType;
			timeLeft = unitType.produceDuration;
			allTime = unitType.produceDuration;
			this.src = src;
		}
	}
	
	public class EstateContextMenu extends ContextMenu {
		ProgressBar pb;
		Panel secondary;
		Button selected;
		
		public EstateContextMenu(Entity entity) {
			super(500, 300, entity);
			secondary = new Panel(-Wargame.width / 2 + 500, -Wargame.height / 2, 500, 300, UI.BEIGE);
			pb = new ProgressBar(-Wargame.width / 2 + 30, -Wargame.height / 2 + 30, 440, 0, UI.BAR_BLUE);
			buttons.add(new Button(1, 0, new Sprite(owner.getColor(), Wargame.standing.getTile("palette99_" + UnitType.values()[0].name() + "_Large_face0").regions.get(0)), new ButtonListener() {
				@Override
				public void onDown(Button b) {
					boolean t = b.isToggled();
					for (Button bt : buttons)
						bt.setToggled(false);
					b.setToggled(t);
				}
				
				@Override
				public void onUp(Button b) {
					if (b.isToggled()) selected = b;
					else selected = null;
				}
			}, UnitType.values()[0], true));
			
			buttons.add(new Button(1, 1, new Sprite(Wargame.ui.getTile("iconPlus_green").regions.get(0)), new ButtonListener() {
				@Override
				public void onDown(Button b) {}
				
				@Override
				public void onUp(Button b) {
					if (queue.size >= 9) return; // TODO show error
					
					for (Button bt : buttons) {
						if (bt.isToggled()) {
							queue.addLast(new ProductionTask((UnitType) bt.getPayload(), bt));
							break;
						}
					}
				}
				
			}, null, false).setPadding(30, 5).setColor(UI.BLUE));
		}
		
		@Override
		public void render(SpriteRenderer r, TextRenderer t) {
			super.render(r, t);
			
			if (selected != null) {
				secondary.render(r, t);
				UnitType type = ((UnitType) selected.getPayload());
				t.renderText(secondary.getX() + 20, secondary.getY() + secondary.getHeight() - 60, 0, 0.8f, Colors.MEDIUM_BLUE, type.name(), r);
				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 100, 0, 0.5f, Colors.DARK_RED, "Costs: $" + type.costs, r);
				//				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 140, 0, 0.5f, runCosts > 0 ? Colors.KHAKI : Colors.MINT, (runCosts > 0 ? "Run costs: " : "Profits: ") + Math.abs(runCosts) + "$/min", r);
				//				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 180, 0, 0.5f, Color.ROYAL, function, r);
				//				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 230, 0, 0.5f, Color.WHITE, detail1, r);
				//				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 260, 0, 0.5f, Color.WHITE, detail2, r);
			}
			
			if (queue.size > 0) {
				ProductionTask first = queue.first();
				pb.setValue(1 - (first.timeLeft / first.allTime));
				pb.render(r, t);
				Sprite s = first.src.getForeground();
				s.resizeSoft(150, 150);
				s.setX(-Wargame.width / 2 + 30);
				s.setY(-Wargame.height / 2 + height - 30 - s.getHeight());
				r.render(s);
				
				int i = 0;
				int size = 70;
				int pad = 5;
				for (ProductionTask pt : queue) {
					if (pt.equals(first)) continue;
					s = pt.src.getForeground();
					s.resizeSoft(size, size);
					s.setX(-Wargame.width / 2 + 180 + (i > 3 ? i - 4 : i) * (size + pad));
					s.setY(-Wargame.height / 2 + height - 30 - (s.getHeight() + pad) * (i > 3 ? 2 : 1));
					r.render(s);
					
					i++;
				}
				//t.renderText(s.getX() + 150, -Wargame.height / 2 + height - 60, 0, 0.8f, Colors.MEDIUM_BLUE, first.unitType.name(), r);
			}
		}
	}
	
	Queue<ProductionTask> queue;
	
	public Estate(int x, int z, Player owner) {
		super(x, z, owner, BuildingType.Estate);
		hp = 550;
		def = 25;
		buildCosts = 1575;
		runCosts = 12;
		function = "Trains Infantry";
		detail1 = "These soldiers do the";
		detail2 = "dirty work for you.";
		queue = new Queue<ProductionTask>();
	}
	
	@Override
	protected ContextMenu getContextMenu() {
		contextMenu = new EstateContextMenu(this);
		return contextMenu;
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		
		if (queue.size > 0) {
			if (queue.first().paid) {
				queue.first().timeLeft -= timePassed;
				if (queue.first().timeLeft <= 0) {
					world.addEntity(new Unit(x + 1 + (float) Math.random(), z + (float) Math.random(), owner, queue.removeFirst().unitType));
				}
			} else if (owner.money >= queue.first().unitType.costs) { // TODO: show when funds insufficient
				owner.money -= queue.first().unitType.costs;
				queue.first().paid = true;
			}
		}
	}
}
