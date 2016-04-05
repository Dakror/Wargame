package de.dakror.wargame.world;

/**
 * @author Maximilian Stark | Dakror
 */
public class TiledRaycastCollisionDetector implements RaycastCollisionDetector<Vector2> {
	World world;
	
	public TiledRaycastCollisionDetector(World world) {
		this.world = world;
	}
	
	// See http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
	@Override
	public boolean collides(Ray<Vector2> ray) {
		int x0 = (int) ray.start.x;
		int y0 = (int) ray.start.y;
		int x1 = (int) ray.end.x;
		int y1 = (int) ray.end.y;
		
		int tmp;
		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
		if (steep) {
			// Swap x0 and y0
			tmp = x0;
			x0 = y0;
			y0 = tmp;
			// Swap x1 and y1
			tmp = x1;
			x1 = y1;
			y1 = tmp;
		}
		if (x0 > x1) {
			// Swap x0 and x1
			tmp = x0;
			x0 = x1;
			x1 = tmp;
			// Swap y0 and y1
			tmp = y0;
			y0 = y1;
			y1 = tmp;
		}
		
		int deltax = x1 - x0;
		int deltay = Math.abs(y1 - y0);
		int error = 0;
		int y = y0;
		int ystep = (y0 < y1 ? 1 : -1);
		for (int x = x0; x <= x1; x++) {
			TiledNode tile = steep ? world.get(y, x) : world.get(x, y);
			if (tile.isBlocked() || !tile.type.solid) return true; // We've hit a building or water
			error += deltay;
			if (error + error >= deltax) {
				y += ystep;
				error -= deltax;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean findCollision(Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
		throw new UnsupportedOperationException();
	}
}
