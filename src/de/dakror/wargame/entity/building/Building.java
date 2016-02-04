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

import com.badlogic.gdx.graphics.Color;

import android.view.MotionEvent;
import de.dakror.wargame.Player;
import de.dakror.wargame.World;
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.ui.ContextMenu;
import de.dakror.wargame.ui.Panel;
import de.dakror.wargame.util.Colors;
import de.dakror.wargame.util.Listeners.TouchListener;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public abstract class Building extends Entity implements TouchListener {
	public static enum Buildings {
		//		Airport(750, 160), // for planes
		//		Castle(2500, 325), // for defense
		City,
		Estate,
		Factory,
		//		Laboratory(450, 1000), // for science!!!!
	}
	
	public static Building create(int x, int y, Player player, Buildings type) {
		try {
			return (Building) Class.forName(Building.class.getPackage().getName() + "." + type.name()).getConstructor(int.class, int.class, Player.class).newInstance(x, y, player);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected int hp, buildCosts, runCosts;
	protected String function, detail1, detail2;
	protected Buildings type;
	protected ContextMenu contextMenu;
	
	boolean selected = false;
	float time = 0;
	
	private Building(int x, int z, int face, Player owner, boolean huge, Buildings type) {
		super(x, z, face, owner, huge, type.name());
		this.type = type;
		onCreate();
	}
	
	public Building(int x, int z, Player owner, Buildings type) {
		this(x, z, 0, owner, false, type);
	}
	
	@Override
	protected void updateTexture() {
		tile = faces[face];
		super.updateTexture();
		
		float texWidth = textureWidth * tile.regions.get(index).texture.width;
		
		width = (float) (Math.ceil(texWidth / World.WIDTH) * World.WIDTH);
		height = (textureHeight * tile.regions.get(index).texture.height) * (width / texWidth);
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		if (selected) {
			additive.r = (float) (Math.sin(time += timePassed * 4) * 0.125f + 0.125f);
			additive.g = additive.r;
			additive.b = additive.r;
		}
		owner.money -= runCosts / 60f * timePassed;
	}
	
	public void renderContextMenu(SpriteRenderer r, TextRenderer t) {
		if (contextMenu != null) contextMenu.render(r, t);
	}
	
	public void renderDetails(Panel p, SpriteRenderer r, TextRenderer t) {
		t.renderText(p.getX() + 20, p.getY() + p.getHeight() - 60, 0, 0.8f, Colors.MEDIUM_BLUE, type.name(), r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 100, 0, 0.5f, Colors.DARK_RED, "Costs: $" + buildCosts, r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 140, 0, 0.5f, runCosts > 0 ? Colors.KHAKI : Colors.MINT, (runCosts > 0 ? "Run costs: " : "Profits: ") + Math.abs(runCosts) + "$/min", r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 180, 0, 0.5f, Color.ROYAL, function, r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 230, 0, 0.5f, Color.WHITE, detail1, r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 260, 0, 0.5f, Color.WHITE, detail2, r);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		if (contextMenu != null) return contextMenu.onDown(e);
		return false;
	}
	
	@Override
	public boolean onUp(MotionEvent e) {
		if (contextMenu != null) return contextMenu.onUp(e);
		return false;
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getBuildCosts() {
		return buildCosts;
	}
	
	public ContextMenu getContextMenu() {
		return contextMenu;
	}
	
	public int getRunCosts() {
		return runCosts;
	}
	
	public Buildings getType() {
		return type;
	}
	
	@Override
	public float getY() {
		return y * World.HEIGHT - (x + (huge ? 1 : 0)) * (World.DEPTH / 2) + z * (World.DEPTH / 2) + world.getPos().y;
	}
	
	@Override
	public float getZ() {
		return (world.getDepth() - z * 2 + x * 2) / 1024f + (color.a < 1.0f ? 10 : 0) /*Wargame#placeBuilding*/;
	}
	
	@Override
	public void onCreate() {}
	
	@Override
	public void onSpawn() {}
	
	@Override
	public void onDeath() {}
	
	@Override
	public void onRemoval() {}
	
	@Override
	public void onSelect() {
		selected = true;
	}
	
	@Override
	public void onDeselect() {
		selected = false;
		additive.set(Color.BLACK);
	}
}
