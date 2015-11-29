package de.dakror.wargame;

import de.dakror.wargame.TextureAtlas.TextureRegion;
import de.dakror.wargame.TextureAtlas.Tile;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class AnimatedSprite extends Sprite {
	protected int color;
	protected Tile tile;
	protected int index;
	protected float vAnim, lastAnim;
	protected boolean loopAnim;
	
	int loops;
	
	public AnimatedSprite() {}
	
	public AnimatedSprite(float x, float y, float z, float width, float height, Tile tile, int color) {
		super(x, y, z, width, height);
		
		this.tile = tile;
		this.color = color;
		
		updateTexture();
	}
	
	protected void updateTexture() {
		textureId = tile.regions.get(index).textureId;
		textureX = tile.regions.get(index).x;
		textureY = tile.regions.get(index).y;
		textureWidth = tile.regions.get(index).width;
		textureHeight = tile.regions.get(index).height;
	}
	
	public void update(float timePassed) {
		// entity stuff
		/*if (frozen || dead) return;
		
		if (lifeTime > 0) {
			life += timePassed;
			if (life >= lifeTime) dead = true;
		}*/
		if (vAnim > 0) {
			lastAnim -= timePassed;
			if (lastAnim <= 0) {
				index++;
				lastAnim = vAnim;
				
				index = loopAnim ? index % tile.regions.size() : (index >= tile.regions.size() ? tile.regions.size() - 1 : index);
				if (index == 0) loops++;
				
				if (loops % 20 == 0) {
					color = (color + 1) % 8;
					loops = 1;
				}
				updateTexture();
			}
		}
	}
	
	@Override
	public float getPaletteIndex() {
		return color;
	}
	
	@Override
	public void setPaletteIndex(int i) {
		color = i;
	}
	
	public TextureRegion getTextureRegion() {
		return tile.regions.get(index);
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public float getAnimationSpeed() {
		return vAnim;
	}
	
	public void setAnimationSpeed(long vAnim) {
		this.vAnim = vAnim;
	}
	
	public boolean isLoopAnimation() {
		return loopAnim;
	}
	
	public void setLoopAnimation(boolean loopAnim) {
		this.loopAnim = loopAnim;
	}
}
