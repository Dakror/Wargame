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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class TextureAtlas {
	/**
	 * @author Maximilian Stark | Dakror
	 */
	public static class Tile {
		public String name;
		public List<TextureRegion> regions = new ArrayList<TextureRegion>();
	}
	
	/**
	 * @author Maximilian Stark | Dakror
	 */
	public static class TextureRegion {
		public Texture texture;
		/**
		 * relative to the full size of the texture
		 */
		public float x, y, width, height, offsetX, offsetY;
		/**
		 * all absolute values
		 */
		public int origWidth, origHeight, index;
	}
	
	List<Texture> textures = new ArrayList<Texture>();
	HashMap<String, Tile> tiles = new HashMap<String, Tile>();
	int regions = 0;
	final String split = "(: )|,( ?)";
	
	public TextureAtlas(String atlasFile) {
		parse(atlasFile);
	}
	
	public Tile getTile(String name) {
		return tiles.get(name);
	}
	
	public HashMap<String, Tile> getTiles() {
		return tiles;
	}
	
	public Tile[] getFaces(String tile) {
		Tile[] tiles = new Tile[4];
		for (int i = 0; i < 4; i++)
			tiles[i] = this.tiles.get(tile + "_face" + i);
		return tiles;
	}
	
	public List<Texture> getTextures() {
		return textures;
	}
	
	void parse(String atlasFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Wargame.instance.getAssets().open(atlasFile)));
			
			Texture currentTexture = null;
			String line = "";
			String[] l;
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					if (currentTexture != null) textures.add(currentTexture);
					
					currentTexture = new Texture();
					currentTexture.name = br.readLine();
					
					// size
					l = br.readLine().split(split);
					currentTexture.width = Integer.parseInt(l[1]);
					currentTexture.height = Integer.parseInt(l[2]);
					
					// format
					br.readLine();
					
					// filter
					l = br.readLine().split(split);
					currentTexture.minFilter = Texture.TextureFilter.valueOf(l[1]).glEnum;
					currentTexture.magFilter = Texture.TextureFilter.valueOf(l[2]).glEnum;
					
					System.out.println("(TextureAtlas) Loading Texture: " + currentTexture.name);
					currentTexture.textureId = Wargame.instance.loadTexture(atlasFile.substring(0, atlasFile.lastIndexOf("/")) + "/" + currentTexture.name, currentTexture.minFilter, currentTexture.magFilter);
					
					// repeat
					br.readLine();
				} else if (!line.startsWith("  ")) {
					TextureRegion tr = new TextureRegion();
					tr.texture = currentTexture;
					
					// rotate
					br.readLine();
					
					// xy
					l = br.readLine().split(split);
					tr.x = Integer.parseInt(l[1]) / (float) currentTexture.width;
					tr.y = Integer.parseInt(l[2]) / (float) currentTexture.height;
					
					// size
					l = br.readLine().split(split);
					tr.width = Integer.parseInt(l[1]) / (float) currentTexture.width;
					tr.height = Integer.parseInt(l[2]) / (float) currentTexture.height;
					
					// orig
					l = br.readLine().split(split);
					tr.origWidth = Integer.parseInt(l[1]);
					tr.origHeight = Integer.parseInt(l[2]);
					
					// offset
					l = br.readLine().split(split);
					tr.offsetX = Integer.parseInt(l[1]) / (float) currentTexture.width;
					tr.offsetY = Integer.parseInt(l[2]) / (float) currentTexture.height;
					
					// index
					l = br.readLine().split(split);
					tr.index = Integer.parseInt(l[1]);
					
					Tile t = tiles.get(line);
					if (t == null) {
						t = new Tile();
						t.name = line;
						tiles.put(line, t);
					}
					
					t.regions.add(tr);
					regions++;
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
