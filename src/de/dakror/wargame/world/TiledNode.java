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

package de.dakror.wargame.world;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

/**
 * @author Maximilian Stark | Dakror
 */
public class TiledNode implements IndexedNode<TiledNode> {
	int x, z;
	World world;
	TileType type;
	boolean buildingOnTop;
	Array<Connection<TiledNode>> connections;
	
	public TiledNode(int x, int z, World world, TileType type) {
		this.x = x;
		this.z = z;
		this.type = type;
		this.world = world;
		connections = new Array<Connection<TiledNode>>(8);
	}
	
	@Override
	public int getIndex() {
		return z * world.getWidth() + x;
	}
	
	@Override
	public Array<Connection<TiledNode>> getConnections() {
		return connections;
	}
}
