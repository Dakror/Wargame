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

import java.sql.Connection;
import java.util.Iterator;

/**
 * @author Maximilian Stark | Dakror
 */
public class TiledNode implements IndexedNode<TiledNode> {
	public int x, z;
	World world;
	TileType type;
	boolean blocked;
	Array<Connection<TiledNode>> connections;
	
	public TiledNode(int x, int z, World world, TileType type) {
		this.x = x;
		this.z = z;
		this.type = type;
		this.world = world;
		connections = new Array<Connection<TiledNode>>(4);
	}
	
	@Override
	public int getIndex() {
		return z * world.getWidth() + x;
	}
	
	public void setBlocked(boolean blocked) {
		if (blocked == this.blocked) return;
		for (int i = 0; i < 4; i++) {
			int x1 = (i - 1) % 2, z1 = (i - 2) % 2;
			TiledNode tn = world.get(x + x1, z + z1);
			if (blocked) {
				for (Iterator<Connection<TiledNode>> iter = tn.getConnections().iterator(); iter.hasNext();)
					if (iter.next().getToNode().equals(this)) iter.remove();
			} else tn.getConnections().add(new TiledConnection(tn, this));
		}
		this.blocked = blocked;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	@Override
	public Array<Connection<TiledNode>> getConnections() {
		if (connections.size == 0) {
			for (int i = 0; i < 4; i++) {
				int x1 = (i - 1) % 2, z1 = (i - 2) % 2;
				if (world.isInBounds(x + x1, z + z1) && world.get(x + x1, z + z1).type.solid) connections.add(new TiledConnection(this, world.get(x + x1, z + z1)));
			}
		}
		return connections;
	}
}
