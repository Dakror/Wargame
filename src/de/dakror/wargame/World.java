package de.dakror.wargame;

import static android.opengl.GLES20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import de.dakror.wargame.TextureAtlas.TextureRegion;
import de.dakror.wargame.TextureAtlas.Tile;

/**
 * @author Maximilian Stark | Dakror
 */
public class World {
	public static enum Types {
		Air, Basement, Custom0, Custom1, Custom2, Desert, Forest, Hills, Jungle, Mountains, Plains, River, Road, Ruins, Sea, Tundra // 16
	}
	
	public static enum Directions {
		/**
		 * X+
		 */
		SE,
		/**
		 * Z-
		 */
		SW,
		/**
		 * X-
		 */
		NW,
		/**
		 * Z+
		 */
		NE
	}
	
	// lol super dumb method, but idc 
	// (Y|X|Z)
	protected byte[][][] map;
	
	protected Vector pos, newPos;
	
	public static final float WIDTH = 129f;
	public static final float HEIGHT = 19f;
	public static final float DEPTH = 64f;
	
	public boolean dirty = true;
	protected int width, height, depth;
	public int rEntities;
	protected ArrayList<Entity> entities;
	
	public int[] fbo = new int[1];
	int[] tex = new int[1];
	public int texWidth, texHeight;
	
	public World(String worldFile) {
		parse(worldFile);
		init();
	}
	
	public World(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		map = new byte[height][width][depth];
		
		generate();
		init();
	}
	
	void parse(String worldFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(MainActivity.instance.getAssets().open(worldFile)));
			
			String line = "";
			int y = 0;
			int z = 0;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("+")) {
					String[] s = line.substring(1).split(",");
					width = Integer.valueOf(s[0]);
					depth = Integer.valueOf(s[1]);
					height = Integer.valueOf(s[2]);
					z = depth - 1;
					map = new byte[height][width][depth];
				} else if (map != null) {
					if (line.startsWith("-")) {
						y++;
						z = depth - 1;
					} else if (line.length() == width) {
						for (int i = 0; i < width; i++) {
							String s = line.substring(i, i + 1);
							if (s.equals(" ")) s = "0";
							set(i, y, z, Types.values()[Integer.valueOf(s, 16)]);
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
		Types[] t = { Types.Desert, Types.Forest, Types.Mountains, Types.River, Types.Tundra };
		for (int i = 0; i < width; i++)
			for (int j = 0; j < depth; j++)
				set(i, 0, j, t[(int) (Math.random() * t.length)]);//, i == width - 1, j == 0, i == 0, j == depth - 1);
		//		set(0, 0, 0, Types.Custom0); // red
		//		set(2, 0, 0, Types.Custom1); // pink 
		//		set(0, 0, 2, Types.Forest); // green
		//		set(2, 0, 2, Types.Tundra); // white
	}
	
	void init() {
		entities = new ArrayList<Entity>();
		
		pos = new Vector(-width / 2 * WIDTH, 0, 0);
		newPos = new Vector().set(pos);
		//		updateDirections();
		
		glGenFramebuffers(1, fbo, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
		glGenTextures(1, tex, 0);
		glBindTexture(GL_TEXTURE_2D, tex[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		texWidth = 1920;//(int) (width * WIDTH / 2 + depth * WIDTH / 2);//(int) ((width + depth) * WIDTH);
		texHeight = 1080;//(int) (height * HEIGHT + (depth - 1) * DEPTH / 2 + (width - 1) * DEPTH / 2);//(int) ((width + depth) * DEPTH + height * HEIGHT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		//		fboBitmap = Bitmap.createBitmap((int) (width * WIDTH / 2 + depth * WIDTH / 2), (int) (height * HEIGHT - width * DEPTH / 2 + depth * DEPTH / 2), Config.ARGB_8888);
		//		GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboBitmap, 0);
		int[] rbo = new int[1];
		glGenRenderbuffers(1, rbo, 0);
		glBindRenderbuffer(GL_RENDERBUFFER, rbo[0]);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, texWidth, texHeight);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex[0], 0);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo[0]);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Framebuffer not complete");
			MainActivity.instance.finish();
		}
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public boolean isInBounds(int x, int y, int z) {
		return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth;
	}
	
	public boolean set(int x, int y, int z, Types type) {
		if (!isInBounds(x, y, z)) return false;
		byte oldVal = map[y][x][z];
		map[y][x][z] = (byte) (map[y][x][z] >> 4 << 4 | type.ordinal());
		return oldVal != map[y][x][z];
	}
	
	public boolean set(int x, int y, int z, Directions d, boolean set) {
		if (!isInBounds(x, y, z)) return false;
		byte oldVal = map[y][x][z];
		if (set) map[y][x][z] |= 1 << (7 - d.ordinal());
		else map[y][x][z] &= ~(1 << (7 - d.ordinal()));
		return oldVal != map[y][x][z];
	}
	
	public boolean set(int x, int y, int z, Types type, boolean se, boolean sw, boolean nw, boolean ne) {
		if (!isInBounds(x, y, z)) return false;
		byte oldVal = map[y][x][z];
		map[y][x][z] = (byte) (type.ordinal() | (se ? 1 : 0) << 7 | (sw ? 1 : 0) << 6 | (nw ? 1 : 0) << 5 | (ne ? 1 : 0) << 4);
		return oldVal != map[y][x][z];
	}
	
	public void updateDirections() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				for (int k = 0; k < depth; k++) {
					Types t = get(j, i, k);
					boolean solid = t != Types.River || t != Types.Sea || t != Types.Air;
					boolean sky = get(j, i + 1, k) == Types.Air;
					
					set(j, i, k, Directions.SE, (j == width - 1 || get(j + 1, i, k) == Types.Air) && solid && sky);
					set(j, i, k, Directions.SW, (k == 0 || get(j, i, k - 1) == Types.Air) && solid && sky);
					set(j, i, k, Directions.NW, (j == 0 || get(j - 1, i, k) == Types.Air) && solid && sky);
					set(j, i, k, Directions.NE, (k == depth - 1 || get(j, i, k + 1) == Types.Air) && solid && sky);
				}
			}
		}
	}
	
	public Types get(int x, int y, int z) {
		if (!isInBounds(x, y, z)) return Types.Air;
		return Types.values()[map[y][x][z] & 0xf];
	}
	
	public boolean is(int x, int y, int z, Directions d) {
		if (!isInBounds(x, y, z)) return false;
		return (map[y][x][z] >> (7 - d.ordinal()) & 0x1) == 1;
	}
	
	public String getFile(int x, int y, int z) {
		if (!isInBounds(x, y, z)) return null;
		String t = get(x, y, z).name();
		if (is(x, y, z, Directions.SE)) t += "_SE";
		if (is(x, y, z, Directions.SW)) t += "_SW";
		if (is(x, y, z, Directions.NW)) t += "_NW";
		if (is(x, y, z, Directions.NE)) t += "_NE";
		
		return t;
	}
	
	public void update(float timePassed) {
		for (Iterator<Entity> iter = entities.iterator(); iter.hasNext();) {
			Entity e = iter.next();
			if (e.isDead()) iter.remove();
			else e.update(timePassed);
		}
	}
	
	public void render(SpriteRenderer r) {
		float ratio = MainActivity.instance.renderer.ratio;
		float scale = 1 / 1024f * MainActivity.instance.renderer.scale;
		int rEntities = 0;
		
		if (dirty) {
			r.flush();
			glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
			glClearColor(1, 0, 0, 1);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glEnable(GL_DEPTH_TEST);
			
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < depth; z++) {
					for (int y = 0; y < height; y++) {
						Tile t = MainActivity.terrain.getTile(getFile(x, y, z));
						if (t == null) continue;
						TextureRegion tr = t.regions.get(0);
						float x1 = x * WIDTH / 2 + z * WIDTH / 2;
						float y1 = y * HEIGHT - x * DEPTH / 2 + z * DEPTH / 2;
						
						r.render(x1, y1, y * HEIGHT + x * DEPTH / 2, tr.width * 2048, tr.height * 2048, tr.x, tr.y, tr.width, tr.height, 8, tr.textureId);
					}
				}
			}
			r.flush();
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glClearColor(130 / 255f, 236 / 255f, 255 / 255f, 1);
			//			glViewport(0, 0, MainActivity.width, MainActivity.height);
			dirty = false;
		} else {
			float fac = (float) 1.25;
			int texWidth = (int) (this.texWidth * fac);
			int texHeight = (int) (this.texHeight * fac);
			r.render(pos.x - texWidth / 2, pos.y - texHeight / 2, 0, texWidth, texHeight, 0, 1, 1, -1, tex[0]);
		}
		for (Entity e : entities) {
			if ((e.getX() + e.getWidth()) * scale >= -ratio && e.getX() * scale <= ratio && e.getY() * scale <= 1 && (e.getY() + e.getHeight()) * scale >= -1) {
				r.render(e);
				rEntities++;
			}
		}
		this.rEntities = rEntities;
		
		pos.set(newPos);
	}
	
	public void addEntity(Entity e) {
		e.setWorld(this);
		entities.add(e);
	}
	
	public void move(float x, float y) {
		newPos.set(pos).add(x, -y, 0);
	}
	
	public Vector getPos() {
		return pos;
	}
	
	public Vector getNewPos() {
		return newPos;
	}
}
