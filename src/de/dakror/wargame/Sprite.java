package de.dakror.wargame;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class Sprite {
	protected float x, y, z;
	protected float width, height;
	protected float textureX, textureY, textureWidth, textureHeight;
	protected float[] color;
	protected int textureId;
	
	// entity stuff
	/*protected float vx, vy, vz;
	protected boolean frozen, dead;
	protected long life, lifeTime;*/
	
	public Sprite() {
		color = new float[] { 1, 1, 1, 1 };
	}
	
	public Sprite(float x, float y, float z, float width, float height) {
		this(x, y, z, width, height, 0, 0, 1, 1);
	}
	
	public Sprite(float x, float y, float z, float width, float height, float textureX, float textureY, float textureWidth, float textureHeight) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.textureX = textureX;
		this.textureY = textureY;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		color = new float[] { 1, 1, 1, 1 };
	}
	
	public float getPaletteIndex() {
		return -1;
	}
	
	public void setPaletteIndex(int i) {}
	
	public void setColor(float[] color) {
		this.color = color;
	}
	
	public float[] getColor() {
		return color;
	}
	
	public int getTextureId() {
		return textureId;
	}
	
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getTextureX() {
		return textureX;
	}
	
	public void setTextureX(float textureX) {
		this.textureX = textureX;
	}
	
	public float getTextureY() {
		return textureY;
	}
	
	public void setTextureY(float textureY) {
		this.textureY = textureY;
	}
	
	public float getTextureWidth() {
		return textureWidth;
	}
	
	public void setTextureWidth(float textureWidth) {
		this.textureWidth = textureWidth;
	}
	
	public float getTextureHeight() {
		return textureHeight;
	}
	
	public void setTextureHeight(float textureHeight) {
		this.textureHeight = textureHeight;
	}
	
	// entity stuff
	/*
		public float getSpeedX() {
		return vx;
	}
	
	public void setSpeedX(float vx) {
		this.vx = vx;
	}
	
	public float getSpeedY() {
		return vy;
	}
	
	public void setSpeedY(float vy) {
		this.vy = vy;
	}
	
	public float getSpeedZ() {
		return vz;
	}
	
	public void setSpeedZ(float vz) {
		this.vz = vz;
	}
	
	public boolean isFrozen() {
		return frozen;
	}
	
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public long getLife() {
		return life;
	}
	
	public void setLife(long life) {
		this.life = life;
	}
	
	public long getLifeTime() {
		return lifeTime;
	}
	
	public void setLifeTime(long lifeTime) {
		this.lifeTime = lifeTime;
	}
	*/
}
