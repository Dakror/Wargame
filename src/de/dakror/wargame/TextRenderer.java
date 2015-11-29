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
import java.util.HashMap;

/**
 * Only supports one page long fonts for simplicity and lazyness :P
 * @author Maximilian Stark | Dakror
 *
 */
public class TextRenderer {
	/**
	 * @author Maximilian Stark | Dakror
	 */
	public static class Glyph {
		int x, y, width, height, offsetX, offsetY, advanceX;
		char c;
	}
	
	HashMap<Character, Glyph> glyphs;
	int textureId;
	float textureWidth, textureHeight;
	int lineHeight;
	
	final float SCALE = 1.0f;
	
	public TextRenderer(String fontFile) {
		parse(fontFile);
	}
	
	public void renderText(float x, float y, float z, float size, String text, SpriteRenderer r) {
		float w = 0;
		for (int i = 0; i < text.length(); i++) {
			Glyph g = glyphs.get(text.charAt(i));
			r.render(x + w + g.offsetX / SCALE * size, y, z, g.width / SCALE * size, g.height / SCALE * size, g.x / textureWidth, g.y / textureHeight, g.width / textureWidth, g.height / textureHeight, textureId);
			w += g.advanceX / SCALE * size;
		}
	}
	
	void parse(String fontFile) {
		try {
			glyphs = new HashMap<Character, Glyph>();
			BufferedReader br = new BufferedReader(new InputStreamReader(MainActivity.instance.getAssets().open(fontFile)));
			
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.startsWith("page ")) {
					String[] p = line.split(" +");
					String texture = p[2].substring(6);
					texture = texture.substring(0, texture.indexOf("\""));
					
					System.out.println("(TextRenderer) Loading Texture: " + texture);
					if (textureId != 0) System.err.println("(TextRenderer) Conflict! Multiple Pages detected");
					textureId = MainActivity.instance.loadTexture(fontFile.substring(0, fontFile.lastIndexOf("/")) + "/" + texture);
				} else if (line.startsWith("char ")) {
					String[] p = line.split(" +");
					Glyph g = new Glyph();
					g.c = (char) Integer.parseInt(p[1].substring(3));
					g.x = Integer.parseInt(p[2].substring(2));
					g.y = Integer.parseInt(p[3].substring(2));
					g.width = Integer.parseInt(p[4].substring(6));
					g.height = Integer.parseInt(p[5].substring(7));
					g.offsetX = Integer.parseInt(p[6].substring(8));
					g.offsetY = Integer.parseInt(p[7].substring(8));
					g.advanceX = Integer.parseInt(p[8].substring(9));
					
					glyphs.put(g.c, g);
				} else if (line.startsWith("common ")) {
					String[] p = line.split(" +");
					lineHeight = Integer.parseInt(p[1].substring(11));
					textureWidth = Integer.parseInt(p[3].substring(7));
					textureHeight = Integer.parseInt(p[4].substring(7));
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
