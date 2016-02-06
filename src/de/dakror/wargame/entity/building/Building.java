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
import de.dakror.wargame.Wargame;
import de.dakror.wargame.World;
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;
import de.dakror.wargame.ui.ContextMenu;
import de.dakror.wargame.ui.Panel;
import de.dakror.wargame.util.Colors;
import de.dakror.wargame.util.Listeners.TouchListener;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public abstract class Building extends Entity implements TouchListener {
	public static enum BuildingType {
		//		Airport(750, 160), // for planes
		//		Castle(2500, 325), // for defense
		City,
		Estate,
		Factory,
		//		Laboratory(450, 1000), // for science!!!!
	}
	
	public static Building create(int x, int y, Player player, BuildingType type) {
		try {
			return (Building) Class.forName(Building.class.getPackage().getName() + "." + type.name()).getConstructor(int.class, int.class, Player.class).newInstance(x, y, player);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected int hp, currentHp, atk, def, buildCosts, runCosts;
	protected String function, detail1, detail2;
	protected BuildingType type;
	protected ContextMenu contextMenu;
	
	boolean selected = false;
	float time = 0;
	
	private Building(int x, int z, int face, Player owner, boolean huge, BuildingType type) {
		super(x, z, face, owner, huge, type.name());
		this.type = type;
		onCreate();
	}
	
	public Building(int x, int z, Player owner, BuildingType type) {
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
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 135, 0, 0.5f, runCosts > 0 ? Colors.KHAKI : Colors.MINT, (runCosts > 0 ? "Run costs: " : "Profits: ") + Math.abs(runCosts) + "$/min", r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 170, 0, 0.5f, Color.ROYAL, function, r);
		
		final float icoHeight = 35;
		TextureRegion heartIcon = Wargame.ui.getTile("hud_heartFull").regions.get(0);
		float hW = heartIcon.origWidth * (icoHeight / heartIcon.origHeight);
		r.render(p.getX() + 30, p.getY() + p.getHeight() - 213, 0, hW, icoHeight, heartIcon.x, heartIcon.y, heartIcon.width, heartIcon.height, heartIcon.texture.textureId);
		t.renderText(p.getX() + 30 + hW, p.getY() + p.getHeight() - 205, 0, 0.5f, Color.WHITE, hp + "", r);
		
		TextureRegion swordIcon = Wargame.ui.getTile("cursorSword_gold").regions.get(0);
		float sW = swordIcon.origWidth * (icoHeight / swordIcon.origHeight);
		r.render(p.getX() + p.getWidth() / 3 + 10, p.getY() + p.getHeight() - 213, 0, sW, icoHeight, swordIcon.x, swordIcon.y, swordIcon.width, swordIcon.height, swordIcon.texture.textureId);
		t.renderText(p.getX() + p.getWidth() / 3 + 15 + sW, p.getY() + p.getHeight() - 205, 0, 0.5f, Color.WHITE, atk + "", r);
		
		
		TextureRegion shieldIcon = Wargame.ui.getTile("shieldSilver").regions.get(0);
		float dW = shieldIcon.origWidth * (icoHeight / shieldIcon.origHeight);
		r.render(p.getX() + p.getWidth() / 3 * 2 + 10, p.getY() + p.getHeight() - 213, 0, dW, icoHeight, shieldIcon.x, shieldIcon.y, shieldIcon.width, shieldIcon.height, shieldIcon.texture.textureId);
		t.renderText(p.getX() + p.getWidth() / 3 * 2 + 15 + dW, p.getY() + p.getHeight() - 205, 0, 0.5f, Color.WHITE, def + "", r);
		
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 245, 0, 0.5f, Color.WHITE, detail1, r);
		t.renderText(p.getX() + 30, p.getY() + p.getHeight() - 275, 0, 0.5f, Color.WHITE, detail2, r);
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
	
	protected ContextMenu getContextMenu() {
		return new ContextMenu(this);
	}
	
	public int getRunCosts() {
		return runCosts;
	}
	
	public BuildingType getType() {
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
		if (contextMenu == null) contextMenu = getContextMenu();
		selected = true;
	}
	
	@Override
	public void onDeselect() {
		selected = false;
		additive.set(Color.BLACK);
	}
}
