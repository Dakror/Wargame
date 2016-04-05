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

package de.dakror.wargameV2.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import de.dakror.wargameV2.Wargame;

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
	
	/**
	 * @author Maximilian Stark | Dakror
	 */
	public static class Font {
		HashMap<Character, Glyph> glyphs;
		int textureId;
		float textureWidth, textureHeight;
		int lineHeight, base;
		int[] padding = new int[4];
		int[] spacing = new int[2];
		
		Font(String fontFile) {
			try {
				glyphs = new HashMap<Character, Glyph>();
				BufferedReader br = new BufferedReader(new InputStreamReader(Wargame.instance.getAssets().open(fontFile)));
				
				String line = "";
				while ((line = br.readLine()) != null) {
					if (line.startsWith("info ")) {
						String[] p = line.split(" +");
						String[] padding = p[p.length - 2].substring(8).split(",");
						for (int i = 0; i < 4; i++)
							this.padding[i] = Integer.parseInt(padding[i]);
						String[] spacing = p[p.length - 1].substring(8).split(",");
						for (int i = 0; i < 2; i++)
							this.spacing[i] = Integer.parseInt(spacing[i]);
					} else if (line.startsWith("page ")) {
						String[] p = line.split(" +");
						String texture = p[2].substring(6);
						texture = texture.substring(0, texture.indexOf("\""));
						
						System.out.println("(TextRenderer) Loading Texture: " + texture);
						if (textureId != 0) System.err.println("(TextRenderer) Conflict! Multiple Pages detected");
						textureId = GLUtil.loadTexture(fontFile.substring(0, fontFile.lastIndexOf("/")) + "/" + texture);
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
						base = Integer.parseInt(p[2].substring(5));
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
	
	LinkedList<Font> fonts = new LinkedList<Font>();
	int font = 0;
	
	public TextRenderer(String... fonts) {
		for (String f : fonts)
			this.fonts.add(new Font(f));
	}
	
	public void setFont(int font) {
		this.font = font;
	}
	
	public float renderText(float x, float y, float z, float size, String text, SpriteRenderer r) {
		return renderText(x, y, z, size, Color.WHITE, text, r);
	}
	
	public float renderText(float x, float y, float z, float size, Color color, String text, SpriteRenderer r) {
		float w = 0;
		Font font = fonts.get(this.font);
		for (int i = 0; i < text.length(); i++) {
			Glyph g = font.glyphs.get(text.charAt(i));
			if (g == null) System.err.println("No gylph found for '" + text.charAt(i) + "'");
			r.render(x + w + (g.advanceX - g.width) / 2 * size, y + (font.base - g.height - g.offsetY) * size, z, g.width * size, g.height * size, g.x / font.textureWidth, g.y / font.textureHeight, g.width / font.textureWidth, g.height / font.textureHeight, color, font.textureId);
			w += g.advanceX * size;
		}
		
		return w;
	}
	
	public void renderTextCentered(float x, float y, float z, float size, String text, SpriteRenderer r) {
		renderTextCentered(x, y, z, size, Color.WHITE, text, r);
	}
	
	public void renderTextCentered(float x, float y, float z, float size, Color color, String text, SpriteRenderer r) {
		float w = 0;
		Font font = fonts.get(this.font);
		for (int i = 0; i < text.length(); i++) {
			Glyph g = font.glyphs.get(text.charAt(i));
			w += g.advanceX * size;
		}
		renderText(x - w / 2, y, z, size, color, text, r);
	}
}
