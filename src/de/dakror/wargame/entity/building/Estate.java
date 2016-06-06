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

import java.util.LinkedList;

import de.dakror.wargame.Player;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.entity.Unit;
import de.dakror.wargame.entity.Unit.UnitType;
import de.dakror.wargame.graphics.Color.Colors;
import de.dakror.wargame.graphics.Sprite;
import de.dakror.wargame.graphics.SpriteRenderer;
import de.dakror.wargame.graphics.TextRenderer;
import de.dakror.wargame.ui.Button;
import de.dakror.wargame.ui.ContextMenu;
import de.dakror.wargame.ui.Panel;
import de.dakror.wargame.ui.ProgressBar;
import de.dakror.wargame.ui.UI;
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
		
		ButtonListener bl = new ButtonListener() {
			@Override
			public void onDown(Button b) {
				boolean t = b.isToggled();
				for (Button bt : buttons)
					if (bt.getPayload() != null) bt.setToggled(false);
				b.setToggled(t);
			}
			
			@Override
			public void onUp(Button b) {
				if (b.isToggled()) selected = b;
				else selected = null;
			}
		};
		
		public EstateContextMenu(Entity entity) {
			super(500, 300, entity);
			secondary = new Panel(-Wargame.width / 2 + 500, -Wargame.height / 2, 500, 240, UI.BEIGE_LIGHT);
			pb = new ProgressBar(-Wargame.width / 2 + 30, -Wargame.height / 2 + 30, 440, 0, UI.BAR_BLUE);
			
			addUnitType(UnitType.Infantry);
			addUnitType(UnitType.Bazooka);
			addUnitType(UnitType.Medic);
			
			buttons.add(new Button(1, 1, new Sprite(Wargame.ui.getTile("iconPlus_green").regions.get(0)), new ButtonListener() {
				@Override
				public void onDown(Button b) {}
				
				@Override
				public void onUp(Button b) {
					if (queue.size() >= 9) return; // TODO show error
					
					for (Button bt : buttons) {
						if (bt.isToggled()) {
							queue.addLast(new ProductionTask((UnitType) bt.getPayload(), bt));
							break;
						}
					}
				}
				
			}, null, false).setPadding(30, 5).setColor(UI.BLUE));
			
			//			buttons.add(new Button(2, 1, new Sprite(Wargame.ui.getTile("iconLoop_beigeLight").regions.get(0)), new ButtonListener() {
			//				@Override
			//				public void onDown(Button b) {}
			//				
			//				@Override
			//				public void onUp(Button b) {}
			//				
			//			}, null, true).setPadding(30, 5).setColor(UI.BLUE));
		}
		
		int index = 1;
		
		void addUnitType(UnitType type) {
			buttons.add(new Button(index, 0, new Sprite(owner.getColor(), Wargame.standing.getTile("palette99_" + type.alias + "_Large_face0").regions.get(0)), bl, type, true));
			index++;
		}
		
		@Override
		public void render(SpriteRenderer r, TextRenderer t) {
			super.render(r, t);
			
			if (selected != null) {
				secondary.render(r, t);
				UnitType type = ((UnitType) selected.getPayload());
				t.renderText(secondary.getX() + 20, secondary.getY() + secondary.getHeight() - 60, 0, 0.8f, Colors.MEDIUM_BLUE, type.name(), r);
				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 100, 0, 0.5f, Colors.DARK_RED, "Costs: $" + type.costs, r);
				t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 135, 0, 0.5f, Colors.KHAKI, "Train time: " + type.produceDuration + "s", r);
				float w = t.renderText(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 170, 0, 0.5f, Colors.ROYAL, type.weapon0.getName(), r);
				t.renderText(secondary.getX() + 40 + w, secondary.getY() + secondary.getHeight() - 170, 0, 0.5f, Colors.MINT, "(" + type.weapon1.getName() + ")", r);
				UI.renderStats(secondary.getX() + 30, secondary.getY() + secondary.getHeight() - 205, secondary.getWidth(), type.hp, type.atk, type.def, r, t);
			}
			
			if (queue.size() > 0) {
				ProductionTask first = queue.getFirst();
				pb.setValue(1 - (first.timeLeft / first.allTime));
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
			} else pb.setValue(0);
			pb.render(r, t);
			if (queue.size() > 0) {
				t.renderText(pb.getX() + pb.getWidth() / 2 - 30, pb.getY() + 10, 0, 0.7f, Colors.BLACK, (int) Math.ceil(queue.getFirst().timeLeft) + "s", r);
			}
		}
	}
	
	LinkedList<ProductionTask> queue;
	
	public Estate(int x, int z, Player owner) {
		super(x, z, owner, BuildingType.Estate);
		hp = 550;
		def = 25;
		buildCosts = 1575;
		runCosts = 45;
		function = "Trains Infantry";
		detail1 = "These soldiers do the";
		detail2 = "dirty work for you.";
		queue = new LinkedList<ProductionTask>();
	}
	
	@Override
	protected ContextMenu getContextMenu() {
		contextMenu = new EstateContextMenu(this);
		return contextMenu;
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		
		if (queue.size() > 0) {
			if (queue.getFirst().paid) {
				queue.getFirst().timeLeft -= timePassed;
				if (queue.getFirst().timeLeft <= 0) {
					Unit u = new Unit(x + 1 + (float) Math.random() * 0.5f - 0.25f, z + (float) Math.random() * 0.5f - 0.25f, owner, queue.removeFirst().unitType);
					world.addEntity(u);
				}
			} else if (owner.money >= queue.getFirst().unitType.costs) { // TODO: show when funds insufficient
				owner.money -= queue.getFirst().unitType.costs;
				queue.getFirst().paid = true;
			}
		}
	}
}
