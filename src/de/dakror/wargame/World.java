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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.dakror.wargame.TextureAtlas.TextureRegion;
import de.dakror.wargame.TextureAtlas.Tile;

/**
 * @author Maximilian Stark | Dakror
 */
public class World /*extends PooledEngine*/ {
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
	public int rendered, all, rEntities;
	protected Array<Entity> entities;
	
	public static final float SCALE = 0.5f;
	
	//	public int[] fbo = new int[1];
	//	int[] tex = new int[1];
	//	public int texWidth, texHeight;
	//	ImmutableArray<Entity> renderables;
	
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
				set(i, j, t[(int) (Math.random() * t.length)]);//, i == width - 1, j == 0, i == 0, j == depth - 1);
		//		set(0, 0, 0, Types.Custom0); // red
		//		set(2, 0, 0, Types.Custom1); // pink 
		//		set(0, 0, 2, Types.Forest); // green
		//		set(2, 0, 2, Types.Tundra); // white
	}
	
	void init() {
		entities = new Array<Entity>();
		//		renderables = getEntitiesFor(Family.all(CAnimatedSprite.class, CPosition.class, CFace.class).get());
		pos = new Vector3(-width / 2 * WIDTH, 0, 0);
		newPos = new Vector3(pos);
		//		updateDirections();
		
		//		glGenFramebuffers(1, fbo, 0);
		//		glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
		//		glGenTextures(1, tex, 0);
		//		glBindTexture(GL_TEXTURE_2D, tex[0]);
		//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		//		
		//		texWidth = (int) (Math.sqrt(width * depth) * (WIDTH + 10 /*idk why */)) / 2;//(int) ((width + depth) * WIDTH);
		//		texHeight = (int) (height * HEIGHT + depth * DEPTH / 2 + width * DEPTH / 2) / 2;//(int) ((width + depth) * DEPTH + height * HEIGHT);
		//		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		//		fboBitmap = Bitmap.createBitmap((int) (width * WIDTH / 2 + depth * WIDTH / 2), (int) (height * HEIGHT - width * DEPTH / 2 + depth * DEPTH / 2), Config.ARGB_8888);
		//		GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboBitmap, 0);
		//		int[] rbo = new int[1];
		//		glGenRenderbuffers(1, rbo, 0);
		//		glBindRenderbuffer(GL_RENDERBUFFER, rbo[0]);
		//		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, texWidth, texHeight);
		//		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex[0], 0);
		//		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo[0]);
		//		
		//		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
		//			System.err.println("Framebuffer not complete");
		//			MainActivity.instance.finish();
		//		}
		//		
		//		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void center(Entity e) {
		center(e.getRealX() + (e.getWidth() / WIDTH) * 2, e.getRealZ());
	}
	
	public void center(float x, float z) {
		newPos.x = -(x * WIDTH / 2 + z * WIDTH / 2) / 2;
		newPos.y = -(-x * DEPTH / 2 + z * DEPTH / 2);
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
	
	public Entity getEntityAt(float x, float y, boolean global) {
		if (!global) {
			x += pos.x;
			y += pos.y;
		}
		float scale = Wargame.instance.scale;
		
		for (Entity e : entities) {
			if (e.getX() * scale <= x && x <= (e.getX() + e.getWidth()) * scale && e.getY() * scale <= y && y <= (e.getY() + e.getHeight()) * scale) {
				return e;
			}
		}
		
		return null;
	}
	
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
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();) {
			Entity e = iter.next();
			if (e.isDead()) {
				e.onRemoval();
				iter.remove();
			} else e.update(timePassed);
		}
	}
	
	public void render(SpriteRenderer r) {
		int rendered = 0, all = 0, rEntities = 0;
		
		float scale = Wargame.instance.scale;
		
		//		if (dirty) {
		//			float[] m = r.matrix;
		//			r.end();
		//			Matrix.setIdentityM(MainActivity.instance.renderer.mvMatrix, 0);
		//			Matrix.scaleM(MainActivity.instance.renderer.mvMatrix, 0, 1f / 1024 / (texWidth / 1920f), 1f / 1024 / (texWidth / 1920f), 1f / 1024);
		//			Matrix.multiplyMM(MainActivity.instance.renderer.matrix, 0, MainActivity.instance.renderer.projMatrix, 0, MainActivity.instance.renderer.mvMatrix, 0);
		//			
		//			r.begin(MainActivity.instance.renderer.matrix);
		//			glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
		//			glClearColor(1, 0, 0, 1);
		//			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		//			glEnable(GL_DEPTH_TEST);
		//			glViewport(0, 0, texWidth, texHeight);
		//			
		//			float hX = 0, hY = 0;
		
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < depth; z++) {
				Tile t = Wargame.terrain.getTile(getFile(x, z));
				if (t == null) continue;
				TextureRegion tr = t.regions.get(0);
				float x1 = pos.x + x * WIDTH / 2 + z * WIDTH / 2;
				float y1 = pos.y - x * DEPTH / 2 + z * DEPTH / 2;
				
				if ((x1 + tr.width * 2048) * scale >= -Wargame.width / 2 && x1 * scale <= Wargame.width / 2 && y1 * scale <= Wargame.height / 2 && (y1 + tr.height * 2048) * scale >= -Wargame.height / 2) {
					r.render(x1, y1, (pos.z + x * DEPTH / 2) / 1024f, tr.width * 2048, tr.height * 2048, tr.x, tr.y, tr.width, tr.height, 8, tr.texture.textureId);
					rendered++;
				}
				all++;
			}
		}
		
		//			r.end();
		//			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		//			r.begin(m);
		//			glClearColor(130 / 255f, 236 / 255f, 255 / 255f, 1);
		//			glViewport(0, 0, MainActivity.width, MainActivity.height);
		//			dirty = false;
		
		//		if (dirty) {
		//			float[] m = r.matrix;
		//			r.end();
		//			Matrix.setIdentityM(MainActivity.instance.renderer.mvMatrix, 0);
		//			Matrix.scaleM(MainActivity.instance.renderer.mvMatrix, 0, 1f / 1024 / (texWidth / 1920f), 1f / 1024 / (texWidth / 1920f), 1f / 1024);
		//			Matrix.multiplyMM(MainActivity.instance.renderer.matrix, 0, MainActivity.instance.renderer.projMatrix, 0, MainActivity.instance.renderer.mvMatrix, 0);
		//			
		//			r.begin(MainActivity.instance.renderer.matrix);
		//			glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
		//			glClearColor(1, 0, 0, 1);
		//			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		//			glEnable(GL_DEPTH_TEST);
		//			glViewport(0, 0, texWidth, texHeight);
		//			
		//			float hX = 0, hY = 0;
		//			
		//			for (int x = 0; x < width; x++) {
		//				for (int z = 0; z < depth; z++) {
		//					for (int y = 0; y < height; y++) {
		//						Tile t = MainActivity.terrain.getTile(getFile(x, y, z));
		//						if (t == null) continue;
		//						TextureRegion tr = t.regions.get(0);
		//						float x1 = x * WIDTH / 2 + z * WIDTH / 2 - texWidth + 10;
		//						float y1 = y * HEIGHT - x * DEPTH / 2 + z * DEPTH / 2 - (texHeight - width * DEPTH / 2);
		//						
		//						if (x1 > hX) hX = x1;
		//						if (y1 > hY) hY = y1;
		//						
		//						r.render(x1, y1, y * HEIGHT + x * DEPTH / 2, tr.width * 2048, tr.height * 2048, tr.x, tr.y, tr.width, tr.height, 8, tr.textureId);
		//					}
		//				}
		//			}
		//			
		//			r.end();
		//			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		//			r.begin(m);
		//			glClearColor(130 / 255f, 236 / 255f, 255 / 255f, 1);
		//			glViewport(0, 0, MainActivity.width, MainActivity.height);
		//			dirty = false;
		//		} else {
		//			float fac = (float) 1.25;
		//			int texWidth = (int) (this.texWidth * fac);
		//			int texHeight = (int) (this.texHeight * fac);
		//			r.render(pos.x - texWidth / 2, pos.y - texHeight / 2, 0, texWidth, texHeight, 0, 1, 1, -1, tex[0]);
		//		}
		
		for (Entity e : entities) {
			if ((e.getX() + e.getWidth()) * scale >= -Wargame.width / 2 && e.getX() * scale <= Wargame.width / 2 && e.getY() * scale <= Wargame.height / 2 && (e.getY() + e.getHeight()) * scale >= -Wargame.height / 2) {
				r.render(e);
				rEntities++;
			}
		}
		
		this.rEntities = rEntities;
		this.rendered = rendered;
		this.all = all;
		
		pos.set(newPos);
	}
	
	public void addEntity(Entity e) {
		e.setWorld(this);
		entities.add(e);
		e.onSpawn();
	}
	
	public void move(float x, float y) {
		newPos.set(pos).add(x, -y, 0);
	}
	
	public Vector3 getPos() {
		return pos;
	}
	
	public Vector3 getNewPos() {
		return newPos;
	}
}
