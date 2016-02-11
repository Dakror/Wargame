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

import com.badlogic.gdx.ai.pfa.DefaultConnection;

/**
 * @author Maximilian Stark | Dakror
 */
public class TiledConnection extends DefaultConnection<TiledNode> {
	public TiledConnection(TiledNode fromNode, TiledNode toNode) {
		super(fromNode, toNode);
	}
	
	@Override
	public float getCost() {
		return 1;
	}
}
