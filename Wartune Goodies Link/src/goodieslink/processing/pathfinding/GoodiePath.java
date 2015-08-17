package goodieslink.processing.pathfinding;

import java.awt.Point;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class GoodiePath implements Iterable<Point> {
	private class GoodiePathIterator implements Iterator<Point> {
		private Point startPoint;
		private Point endPoint;
		private Point corner1;
		private Point corner2;

		/**
		 * Which part of the path the iterator is on. 0 = between start and
		 * corner1, 1 = between corner1 and corner 2, 2 = between corner2 and
		 * end, 3 = done
		 */
		private int segment;
		/**
		 * How boxes have been returned in current segment
		 */
		private int progress;

		public GoodiePathIterator(Point pStart, Point corner1, Point corner2, Point pEnd) {
			this.startPoint = pStart;
			this.endPoint = pEnd;
			this.corner1 = corner1;
			this.corner2 = corner2;
			segment = 0;
			progress = 0;
		}

		private Point advance(Point p1, Point p2, int progress) {
			if (p1.x == p2.x) {
				int direction = p1.y > p2.y ? -1 : 1;
				return new Point(p1.x, p1.y + direction * progress);
			} else {
				int direction = p1.x > p2.x ? -1 : 1;
				return new Point(p1.x + direction * progress, p1.y);
			}
		}

		@Override
		public boolean hasNext() {
			return segment != 3;
		}

		@Override
		public Point next() {
			Point result = new Point();
			switch (segment) {
			case 0:
				if (corner1 == null) {
					if (corner2 == null) {
						// straight line with no corners
						result = advance(startPoint, endPoint, progress);
						if (result.equals(endPoint)) {
							// this point is at the end, finished
							segment = 3;
						} else {
							progress++;
						}
					} else {
						// 1 corner at corner2
						result = advance(startPoint, corner2, progress);
						if (result.equals(corner2)) {
							segment = 2;
							// set to 1 because if it were 0
							// then the corner would be returned again
							progress = 1;
						} else {
							progress++;
						}
					}
				} else {
					// corner1 != null, move to it
					result = advance(startPoint, corner1, progress);
					if (result.equals(corner1)) {
						segment = 1;
						progress = 1;
					} else {
						progress++;
					}
				}
				break;
			case 1:
				if (corner2 == null) {
					// move from corner1 to end
					result = advance(corner1, endPoint, progress);
					if (result.equals(endPoint)) {
						segment = 3;
					} else {
						progress++;
					}
				} else {
					// move from corner1 to corner2
					result = advance(corner1, corner2, progress);
					if (result.equals(corner2)) {
						segment = 2;
						progress = 1;
					} else {
						progress++;
					}
				}
				break;
			case 2:
				result = advance(corner2, endPoint, progress);
				if (result.equals(endPoint)) {
					segment = 3;
				} else {
					progress++;
				}
				break;
			default:
				// at end, there are no more
				throw new NoSuchElementException();
			}
			return result;
		}
	}

	private Point startPoint;
	private Point endPoint;

	private Point corner1;
	private Point corner2;

	public GoodiePath() {
		this(new Point(), new Point());
	}

	public GoodiePath(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public Point getStartPoint() {
		return startPoint;
	}

	@Override
	public Iterator<Point> iterator() {
		return new GoodiePathIterator(startPoint, corner1, corner2, endPoint);
	}

	public void setCorner1(Point corner) {
		if (!startPoint.equals(corner)) {
			corner1 = corner;
		} else {
			corner1 = null;
		}
	}

	public void setCorner2(Point corner) {
		if (!endPoint.equals(corner)) {
			corner2 = corner;
		} else {
			corner2 = null;
		}
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	@Override
	public String toString() {
		String result = "GoodiePath [startPoint=" + startPoint;
		if (corner1 != null) {
			result += ", corner1=" + corner1;
		}
		if (corner2 != null) {
			result += ", corner2=" + corner2;
		}
		result += ", endPoint=" + endPoint + "]";
		return result;
	}

}
