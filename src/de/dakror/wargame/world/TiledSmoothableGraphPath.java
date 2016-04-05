package de.dakror.wargame.world;

/**
 * @author Maximilian Stark | Dakror
 */
public class TiledSmoothableGraphPath extends DefaultGraphPath<TiledNode> implements SmoothableGraphPath<TiledNode, Vector2> {
	private Vector2 tmpPosition = new Vector2();
	
	@Override
	public Vector2 getNodePosition(int index) {
		TiledNode node = nodes.get(index);
		return tmpPosition.set(node.x, node.z);
	}
	
	@Override
	public void swapNodes(int index1, int index2) {
		nodes.set(index1, nodes.get(index2));
	}
	
	@Override
	public void truncatePath(int newLength) {
		nodes.truncate(newLength);
	}
}
