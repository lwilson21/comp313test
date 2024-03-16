package edu.luc.etl.cs313.android.shapes.model;
/**
 * A shape visitor for calculating the bounding box, that is, the smallest
 * rectangle containing the shape. The resulting bounding box is returned as a
 * rectangle at a specific location.
 */
public class BoundingBox implements Visitor<Location> {

	@Override
	public Location onCircle(final Circle c) {
		final int radius = c.getRadius();
		return new Location(-radius, -radius, new Rectangle(2 * radius, 2 * radius));
	}

	@Override
	public Location onFill(final Fill f) {
		return f.getShape().accept(this);
	}

	@Override
	public Location onGroup(final Group g) {
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;

		for (Shape s : g.getShapes()) {
			Location locationFind = s.accept(this);
			Rectangle rectShape = (Rectangle) locationFind.getShape();
			int height = rectShape.getHeight();
			int width = rectShape.getWidth();
			int x = locationFind.getX();
			int y = locationFind.getY();

			if (y + height > maxY) {
				maxY = y + height;
			}
			if (x + width > maxX) {
				maxX = x + width;
			}
			if (x < minX) {
				minX = x;
			}
			if (y < minY) {
				minY = y;
			}
		}
		int height = maxX - minX;
		int width = maxY - minY;
		return new Location(minX, minY, new Rectangle(height, width));
	}

	@Override
	public Location onLocation(final Location l) {
		Location locationFind = l.getShape().accept(this);
		int firstX = l.getX();
		int secX = locationFind.getX();
		int firstY = l.getY();
		int secY = locationFind.getY();

		return new Location(firstX+secX,firstY+secY, locationFind.getShape());
	}

	@Override
	public Location onRectangle(final Rectangle r) {
		return new Location(0, 0, r);
	}

	@Override
	public Location onStrokeColor(final StrokeColor c) {
		return c.getShape().accept(this);
	}

	@Override
	public Location onOutline(final Outline o) {
		return o.getShape().accept(this);
	}

	@Override
	public Location onPolygon(final Polygon s) {
		return onGroup(s);
	}
}
