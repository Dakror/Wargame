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

import de.dakror.wargame.Player;
import de.dakror.wargame.Wargame;
import de.dakror.wargame.entity.Unit.Units;
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
	public Estate(int x, int z, Player owner) {
		super(x, z, owner, Buildings.Estate);
		hp = 350;
		buildCosts = 1575;
		runCosts = 12;
		function = "Trains Infantry";
		detail1 = "These soldiers do the";
		detail2 = "dirty work for you.";
		contextMenu = new ContextMenu(500, 300, this) {
			ProgressBar pb = new ProgressBar(-Wargame.width / 2 + 30, -Wargame.height / 2 + 30, 440, 0, UI.BAR_BLUE);
			
			@Override
			public void render(SpriteRenderer r, TextRenderer t) {
				super.render(r, t);
				pb.render(r, t);
			}
		};
		contextMenu.addButton(new Button(1, new Sprite(owner.getColor(), Wargame.standing.getTile("palette99_" + Units.values()[0].name() + "_Large_face0").regions.get(0)), new ButtonListener() {
			@Override
			public void onDown(Button b) {}
			
			@Override
			public void onUp(Button b) {}
		}, Units.values()[0], false));
	}
}
