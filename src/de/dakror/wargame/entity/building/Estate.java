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
import de.dakror.wargame.entity.Unit;
import de.dakror.wargame.entity.Unit.UnitType;
import de.dakror.wargame.render.Sprite;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.ui.Button;
import de.dakror.wargame.ui.ContextMenu;
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
		
		public ProductionTask(UnitType unitType, Button src) {
			this.unitType = unitType;
			timeLeft = unitType.produceDuration;
			allTime = unitType.produceDuration;
			this.src = src;
		}
	}
	
	Queue<ProductionTask> queue;
	
	public Estate(int x, int z, Player owner) {
		super(x, z, owner, BuildingType.Estate);
		hp = 350;
		buildCosts = 1575;
		runCosts = 12;
		function = "Trains Infantry";
		detail1 = "These soldiers do the";
		detail2 = "dirty work for you.";
		queue = new Queue<ProductionTask>();
	}
	
	@Override
	protected ContextMenu getContextMenu() {
		contextMenu = new ContextMenu(500, 300, this) {
			ProgressBar pb = new ProgressBar(-Wargame.width / 2 + 30, -Wargame.height / 2 + 30, 440, 0, UI.BAR_BLUE);
			
			@Override
			public void render(SpriteRenderer r, TextRenderer t) {
				super.render(r, t);
				
				if (queue.size > 0) {
					pb.setValue(1 - (queue.first().timeLeft / queue.first().allTime));
					pb.render(r, t);
					Sprite s = queue.first().src.getForeground();
					s.resizeSoft(150, 150);
					s.setX(-Wargame.width / 2 + 30);
					s.setY(-Wargame.height / 2 + height - 30 - s.getHeight());
					r.render(s);
				}
			}
		};
		contextMenu.addButton(new Button(1, new Sprite(owner.getColor(), Wargame.standing.getTile("palette99_" + UnitType.values()[0].name() + "_Large_face0").regions.get(0)), new ButtonListener() {
			@Override
			public void onDown(Button b) {}
			
			@Override
			public void onUp(Button b) {
				if (getOwner().money >= ((UnitType) b.getPayload()).costs) {
					getOwner().money -= ((UnitType) b.getPayload()).costs;
					queue.addLast(new ProductionTask((UnitType) b.getPayload(), b));
				}
			}
		}, UnitType.values()[0], false));
		return contextMenu;
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		
		if (queue.size > 0) {
			queue.first().timeLeft -= timePassed;
			if (queue.first().timeLeft <= 0) {
				world.addEntity(new Unit(x + 1 + (float) Math.random(), z + (float) Math.random(), owner, queue.removeFirst().unitType));
			}
		}
	}
}
