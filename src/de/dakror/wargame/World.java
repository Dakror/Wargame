/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
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

package de.dakror.wargame;

import static android.opengl.GLES20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import de.dakror.wargame.entity.Building;
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.entity.Unit;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextureAtlas.TextureRegion;
import de.dakror.wargame.render.TextureAtlas.Tile;

/**
 * @author Maximilian Stark | Dakror
 */
public class World {
	public static enum Type {
		Air,
		Basement,
		Custom0,
		Custom1,
		Custom2,
		Desert,
		Forest,
		Hills,
		Jungle,
		Mountains,
		Plains,
		River,
		Road,
		Ruins,
		Sea,
		Tundra // 16
	}
	
	// lol super dumb method, but idc 
	// (X|Z)
	protected byte[][] map;
	
	protected Vector3 pos, newPos;
	
	public static final float WIDTH = 129f;
	public static final float HEIGHT = 18f;
	public static final float DEPTH = 64f;
	
	public boolean dirty = true;
	protected int width, depth;
	public int rEntities;
	protected Array<Unit> units;
	protected Array<Building> buildings;
	
	int[] fbo = new int[1];
	int[] rbo = new int[1];
	int[] tex = new int[1];
	int texWidth, texHeight;
	Bitmap fboBitmap;
	float[] matrix = new float[16];
	
	public World(String worldFile) {
		super();
		parse(worldFile);
		init();
	}
	
	public World(int width, int depth) {
		super();
		this.width = width;
		this.depth = depth;
		map = new byte[width][depth];
		
		generate();
		init();
	}
	
	void parse(String worldFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Wargame.instance.getAssets().open(worldFile)));
			
			String line = "";
			int z = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("+")) {
					String[] s = line.substring(1).split(",");
					width = Integer.valueOf(s[0]);
					depth = Integer.valueOf(s[1]);
					z = depth - 1;
					map = new byte[width][depth];
				} else if (map != null) {
					if (line.startsWith("-")) {
						z = depth - 1;
					} else if (line.length() == width) {
						for (int i = 0; i < width; i++) {
							String s = line.substring(i, i + 1);
							if (s.equals(" ")) s = "0";
							set(i, z, Type.values()[Integer.valueOf(s, 16)]);
						}
						z--;
					}
				} else throw new IOException("Illegal world file structure. No size definition at beginning");
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void generate() {
		Type[] t = { Type.Desert, Type.Forest, Type.Mountains, Type.River, Type.Tundra };
		for (int i = 0; i < width; i++)
			for (int j = 0; j < depth; j++)
				set(i, j, t[(int) (Math.random() * t.length)]);
	}
	
	void init() {
		units = new Array<Unit>();
		buildings = new Array<Building>();
		
		pos = new Vector3(-width / 2 * WIDTH, 0, 0);
		newPos = new Vector3(pos);
		
		glGenFramebuffers(1, fbo, 0);
		glGenRenderbuffers(1, rbo, 0);
		glGenTextures(1, tex, 0);
		
		glBindTexture(GL_TEXTURE_2D, tex[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		texWidth = (int) (depth * WIDTH / 2 + width * WIDTH / 2);
		texHeight = (int) (depth * DEPTH / 2 + width * DEPTH / 2 + HEIGHT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		fboBitmap = Bitmap.createBitmap(texWidth, texHeight, Config.ARGB_8888);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboBitmap, 0);
		glBindRenderbuffer(GL_RENDERBUFFER, rbo[0]);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, texWidth, texHeight);
		
		float[] projMatrix = new float[16], viewMatrix = new float[16];
		Matrix.orthoM(projMatrix, 0, -texWidth / 2, texWidth / 2, -texHeight / 2, texHeight / 2, -100, 1000);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
		Matrix.multiplyMM(matrix, 0, projMatrix, 0, viewMatrix, 0);
	}
	
	public void center(Entity e) {
		center(e.getRealX() + (e.getWidth() / WIDTH) * 2, e.getRealZ());
	}
	
	public void center(float x, float z) {
		newPos.x = -(x * WIDTH / 2 + z * WIDTH / 2) / 2;
		newPos.y = -(-x * DEPTH / 2 + z * DEPTH / 2);
		
		clampNewPosition();
	}
	
	public void clampNewPosition() {
		newPos.x = Math.min(-Wargame.width / 2 / Wargame.scale, Math.max(-(width * WIDTH / 2 + depth * WIDTH / 2) + Wargame.width / 2 / Wargame.scale, newPos.x));
		newPos.y = Math.min((width - 1) * DEPTH / 2 - Wargame.height / 2 / Wargame.scale, Math.max(-(depth + 1) * DEPTH / 2 - HEIGHT + Wargame.height / 2 / Wargame.scale, newPos.y));
	}
	
	public boolean isInBounds(int x, int z) {
		return x >= 0 && x < width && z >= 0 && z < depth;
	}
	
	public boolean set(int x, int z, Type type) {
		if (!isInBounds(x, z)) return false;
		byte oldVal = map[x][z];
		map[x][z] = (byte) type.ordinal();
		return oldVal != map[x][z];
	}
	
	//	public Entity getEntityAt(float x, float y, boolean global) {
	//		if (!global) {
	//			x += pos.x;
	//			y += pos.y;
	//		}
	//		
	//		for (Entity e : entities) {
	//			if (e.getX() * Wargame.scale <= x && x <= (e.getX() + e.getWidth()) * Wargame.scale && e.getY() * Wargame.scale <= y && y <= (e.getY() + e.getHeight()) * Wargame.scale) {
	//				return e;
	//			}
	//		}
	//		
	//		return null;
	//	}
	
	public Type get(int x, int z) {
		if (!isInBounds(x, z)) return Type.Air;
		return Type.values()[map[x][z]];
	}
	
	public String getFile(int x, int z) {
		if (!isInBounds(x, z)) return null;
		String t = get(x, z).name();
		
		return t;
	}
	
	public void update(float timePassed) {
		update(buildings, timePassed);
		update(units, timePassed);
	}
	
	public <E extends Entity> void update(Array<E> arr, float timePassed) {
		for (Iterator<E> iter = arr.iterator(); iter.hasNext();) {
			Entity e = iter.next();
			if (e.isDead()) {
				e.onRemoval();
				iter.remove();
			} else e.update(timePassed);
		}
	}
	
	public void render(SpriteRenderer r) {
		int rEntities = 0;
		
		if (dirty) {
			System.out.println("Rendering terrain");
			r.end();
			r.begin(matrix);
			glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex[0], 0);
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo[0]);
			glViewport(0, 0, texWidth, texHeight);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
			
			for (int x = 0; x < width; x++) {
				for (int z = depth - 1; z >= 0; z--) {
					Tile t = Wargame.terrain.getTile(getFile(x, z));
					if (t == null) continue;
					TextureRegion tr = t.regions.get(0);
					float x1 = x * WIDTH / 2 + z * WIDTH / 2;
					float y1 = -(x + 1) * DEPTH / 2 + z * DEPTH / 2;
					
					r.render(x1 - texWidth / 2, y1 - HEIGHT / 2, (depth - z + x * 1f / depth), tr.width * 2048, tr.height * 2048, tr.x, tr.y, tr.width, tr.height, 8, tr.texture.textureId);
				}
			}
			
			r.end();
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			dirty = false;
			r.begin(Wargame.instance.viewProjMatrix);
			glViewport(0, 0, Wargame.width, Wargame.height);
		}
		
		r.render(pos.x, pos.y - texHeight / 2 + HEIGHT / 2 + DEPTH / 2, 0, texWidth, texHeight, 0, 1, 1, -1, tex[0]);
		
		rEntities += render(buildings, r);
		//		long t = System.nanoTime();
		units.sort();
		//		System.out.println((System.nanoTime() - t) / 1000000f);
		rEntities += render(units, r);
		
		this.rEntities = rEntities;
		
		pos.set(newPos);
	}
	
	public <E extends Entity> int render(Array<E> arr, SpriteRenderer r) {
		int rEntities = 0;
		for (Entity e : arr) {
			if ((e.getX() + e.getWidth()) * Wargame.scale >= -Wargame.width / 2 && e.getX() * Wargame.scale <= Wargame.width / 2 && e.getY() * Wargame.scale <= Wargame.height / 2 && (e.getY() + e.getHeight()) * Wargame.scale >= -Wargame.height / 2) {
				r.render(e);
				rEntities++;
			}
		}
		
		return rEntities;
	}
	
	public void addEntity(Entity e) {
		e.setWorld(this);
		if (e instanceof Building) {
			buildings.add((Building) e);
			buildings.sort();
		} else if (e instanceof Unit) {
			units.add((Unit) e);
		} else System.out.println("Can't handle Entity: " + e);
		e.onSpawn();
	}
	
	public void move(float x, float y) {
		newPos.set(pos).add(x, -y, 0);
		clampNewPosition();
	}
	
	public Vector3 getPos() {
		return pos;
	}
	
	public Vector3 getNewPos() {
		return newPos;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getDepth() {
		return depth;
	}
}
