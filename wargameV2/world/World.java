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

package de.dakror.wargameV2.world;

import de.dakror.wargameV2.graphics.Renderable;
import de.dakror.wargameV2.graphics.SpriteRenderer;
import de.dakror.wargameV2.graphics.TextRenderer;
import de.dakror.wargameV2.util.Vector3;

/**
 * @author Maximilian Stark | Dakror
 */
public class World implements Renderable {
	public static float WIDTH = 129f;
	public static float HEIGHT = 18f;
	public static float DEPTH = 64f;
	
	protected int[] world;
	protected Vector3 pos, newPos;
	
	public boolean dirty = true;
	protected int width, depth;
	public int rEntities;
	float add;
	
	//	protected ERTree entities = new ERTree();
	//	protected ArrayList<Entity> pendingSpawns = new ArrayList<Entity>();
	
	int[] fbo = new int[1];
	int[] rbo = new int[1];
	int[] tex = new int[1];
	int texWidth, texHeight;
	float[] matrix = new float[16];
	
	@Override
	public void render(SpriteRenderer r, TextRenderer t) {}
	
}
