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
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;

/**
 * @author Maximilian Stark | Dakror
 */
public class Panel extends Component {
	String color;
	
	TextureRegion bg;
	
	public Panel(int x, int y, int width, int height, String color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		
		bg = Wargame.ui.getTile("panel" + color).regions.get(0);
	}
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {
		//TODO: add FBOs for scalability
		
		float s = UI.DEFAULT_SCALE;
		
		r.render(x, y + height - 25 * s, 0, 20 * s, 25 * s, bg.x, bg.y, bg.width / 5, bg.height / 4, bg.texture.textureId); // upper left corner
		
		r.render(x + 20 * s, y + height - 25 * s, 0, 20 * s, 25 * s, bg.x + bg.width / 5, bg.y, bg.width / 5, bg.height / 4, bg.texture.textureId); // top side
		r.render(x + 40 * s, y + height - 25 * s, 0, 20 * s, 25 * s, bg.x + bg.width / 5 * 2, bg.y, bg.width / 5, bg.height / 4, bg.texture.textureId); // top side
		r.render(x + 60 * s, y + height - 25 * s, 0, width - 80 * s, 25 * s, bg.x + bg.width / 5 * 3, bg.y, bg.width / 5, bg.height / 4, bg.texture.textureId); // top
		
		r.render(x + width - 20 * s, y + height - 25 * s, 0, 20 * s, 25 * s, bg.x + bg.width / 5 * 4, bg.y, bg.width / 5, bg.height / 4, bg.texture.textureId); // upper right corner
		
		r.render(x, y + 25 * s, 0, 20 * s, 25 * s, bg.x, bg.y + bg.height / 2, bg.width / 5, bg.height / 4, bg.texture.textureId); // left side
		r.render(x, y + 50 * s, 0, 20 * s, height - 75 * s, bg.x, bg.y + bg.height / 4, bg.width / 5, bg.height / 4, bg.texture.textureId); // left filler
		
		r.render(x, y, 0, 20 * s, 25 * s, bg.x, bg.y + bg.height / 4 * 3, bg.width / 5, bg.height / 4, bg.texture.textureId); // lower left corner
		
		r.render(x + 20 * s, y, 0, width - 40 * s, 25 * s, bg.x + bg.width / 5, bg.y + bg.height / 4 * 3, bg.width / 5 * 3, bg.height / 4, bg.texture.textureId); //bottom
		r.render(x + width - 20 * s, y, 0, 20 * s, 25 * s, bg.x + bg.width / 5 * 4, bg.y + bg.height / 4 * 3, bg.width / 5, bg.height / 4, bg.texture.textureId); //lower right corner
		
		r.render(x + 20 * s, y + 25 * s, 0, width - 40 * s, height - 50 * s, bg.x + bg.width / 5, bg.y + bg.height / 4, bg.width / 5, bg.height / 4, bg.texture.textureId); //middle
		
		r.render(x + width - 20 * s, y + 25 * s, 0, 20 * s, height - 50 * s, bg.x + bg.width / 5 * 4, bg.y + bg.height / 4, bg.width / 5, bg.height / 2, bg.texture.textureId); //right
	}
}
