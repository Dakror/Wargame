package de.dakror.wargame.world;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * @author Maximilian Stark | Dakror
 */
public class TiledManhattanDistance implements Heuristic<TiledNode> {
	@Override
	public float estimate(TiledNode node, TiledNode endNode) {
		return Math.abs(endNode.x - node.x) + Math.abs(endNode.z - node.z);
	}
}
