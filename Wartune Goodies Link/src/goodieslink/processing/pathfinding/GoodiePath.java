package goodieslink.processing.pathfinding;

import java.awt.Point;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a path on the Goodies Link board connecting two grid locations.
 * This class enforces the limitation that the path may have up to and including
 * two corners.
 * 
 * @author Jonathan Schram
 */
public class GoodiePath implements Iterable<Point> {
	/**
	 * Iterator to return successive points on the game board including the
	 * start and end points and every space in between
	 * 
	 * @author Jonathan Schram
	 */
	private class GoodiePathIterator implements Iterator<Point> {
		/**
		 * Start point of path
		 */
		private Point startPoint;
		/**
		 * End point of path
		 */
		private Point endPoint;
		/**
		 * First turning point on path, if it exists
		 */
		private Point corner1;
		/**
		 * Second turning point on path, if it exists
		 */
		private Point corner2;

		/**
		 * Which part of the path the iterator is on. 0 = between start and
		 * corner1, 1 = between corner1 and corner 2, 2 = between corner2 and
		 * end, 3 = done
		 */
		private int segment;
		/**
		 * How many boxes have been returned in current segment
		 */
		private int progress;

		/**
		 * Creates a GoodiePathIterator with the parameters of this GoodiePath
		 * 
		 * @param pStart
		 *            Start point
		 * @param corner1
		 *            Corner 1
		 * @param corner2
		 *            Corner 2
		 * @param pEnd
		 *            End point
		 */
		public GoodiePathIterator(Point pStart, Point corner1, Point corner2, Point pEnd) {
			this.startPoint = pStart;
			this.endPoint = pEnd;
			this.corner1 = corner1;
			this.corner2 = corner2;
			segment = 0;
			progress = 0;
		}

		/**
		 * Move 1 grid space along the current segment of the path
		 * 
		 * @param p1
		 *            Start point of current path segment
		 * @param p2
		 *            End point of current path segment
		 * @param progress
		 *            Current path progress
		 * @return Next point on the path segment
		 */
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
			switch (segment) {
			case 0:
				if (corner1 == null) {
					if (corner2 == null) {
						// straight line with no corners
						if (startPoint.x != endPoint.x && startPoint.y != endPoint.y) {
							return false;
						}
					} else {
						// 1 corner at corner2
						if (startPoint.x != corner2.x && startPoint.y != corner2.y) {
							return false;
						}
					}
				} else {
					// 1 corner at corner 1
					if (startPoint.x != corner1.x && startPoint.y != corner1.y) {
						return false;
					}
				}
				break;
			case 1:
				if (corner2 == null) {
					// move from corner1 to end
					if (corner1.x != endPoint.x && corner1.y != endPoint.y) {
						return false;
					}
				} else {
					// move from corner1 to corner2
					if (corner1.x != corner2.x && corner1.y != corner2.y) {
						return false;
					}
				}
				break;
			case 2:
				// move from corner2 to end
				if (corner2.x != endPoint.x && corner2.y != endPoint.y) {
					return false;
				}
				break;
			}
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

	/**
	 * Start point of path
	 */
	private Point startPoint;
	/**
	 * End point of path
	 */
	private Point endPoint;

	/**
	 * First turning point of path, if it exists
	 */
	private Point corner1;
	/**
	 * Second turning point of path, if it exists
	 */
	private Point corner2;

	/**
	 * Constructs a default GoodiePath with start and end at (0,0) and with no
	 * corners
	 */
	public GoodiePath() {
		this(new Point(), new Point());
	}

	/**
	 * Constructs a GoodiePath with given start and end point, with no corners
	 * 
	 * @param startPoint
	 *            Start point of path
	 * @param endPoint
	 *            End point of path
	 */
	public GoodiePath(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	/**
	 * Gets the end point of the path
	 * 
	 * @return Path end point
	 */
	public Point getEndPoint() {
		return endPoint;
	}

	/**
	 * Gets the start point of the path
	 * 
	 * @return Path start point
	 */
	public Point getStartPoint() {
		return startPoint;
	}

	@Override
	public Iterator<Point> iterator() {
		return new GoodiePathIterator(startPoint, corner1, corner2, endPoint);
	}

	/**
	 * Sets corner 1. Will not be set if the corner equals the start point or
	 * corner 2
	 * 
	 * @param corner
	 *            Point which will be a corner
	 */
	public void setCorner1(Point corner) {
		if (!startPoint.equals(corner) && !(corner2 != null && corner2.equals(corner))) {
			corner1 = corner;
		} else {
			corner1 = null;
		}
	}

	/**
	 * Returns corner1
	 * 
	 * @return
	 */
	public Point getCorner1() {
		return corner1;
	}

	/**
	 * Returns corner2
	 * 
	 * @return
	 */
	public Point getCorner2() {
		return corner2;
	}

	/**
	 * Sets corner 2. Will not be set if the corner equals the end point or
	 * corner1
	 * 
	 * @param corner
	 *            Path which will be the second corner.
	 */
	public void setCorner2(Point corner) {
		if (!endPoint.equals(corner) && !(corner1 != null && corner1.equals(corner))) {
			corner2 = corner;
		} else {
			corner2 = null;
		}
	}

	/**
	 * Sets the end point
	 * 
	 * @param endPoint
	 *            New path end point
	 */
	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Sets the start point
	 * 
	 * @param startPoint
	 *            New path start point
	 */
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
