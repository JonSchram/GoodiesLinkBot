package goodieslink.processing;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Implementation of the {@link Shape} interface that represents a Square with 4
 * sides of equal length.
 * 
 * @author Jonathan Schram
 *
 */
public class Square implements Shape {
	/**
	 * Implementation of {@link PathIterator} that returns successive vertices
	 * of the representative {@link Shape} object.
	 * 
	 * @author Jonathan Schram
	 *
	 */
	private class SquarePathIterator implements PathIterator {
		/**
		 * Which point should be returned on the next call to currentSegment()
		 */
		private int pointNum;
		private int x;
		private int y;
		private int s;
		private AffineTransform at;

		/**
		 * Initializes a {@link SquarePathIterator} with given dimensions
		 * 
		 * @param x
		 *            X coordinate of square
		 * @param y
		 *            Y coordinate of square
		 * @param s
		 *            Side length
		 * @param at
		 *            {@link AffineTransform} applied to the square
		 */
		public SquarePathIterator(int x, int y, int s, AffineTransform at) {
			pointNum = 0;
			this.x = x;
			this.y = y;
			this.s = s;
			this.at = at;
		}

		@Override
		public int currentSegment(double[] coords) {
			int segType;
			if (pointNum == 0) {
				segType = SEG_MOVETO;
			} else if (pointNum == 4) {
				segType = SEG_CLOSE;
			} else {
				segType = SEG_LINETO;
			}

			if (pointNum == 0 || pointNum == 3) {
				coords[0] = x;
			} else if (pointNum == 1 || pointNum == 2) {
				coords[0] = x + s;
			}
			if (pointNum == 0 || pointNum == 1) {
				coords[1] = y;
			} else if (pointNum == 2 || pointNum == 3) {
				coords[1] = y + s;
			}

			if (at != null) {
				at.transform(coords, 0, coords, 0, 1);
			}

			return segType;
		}

		@Override
		public int currentSegment(float[] coords) {
			int segType;
			if (pointNum == 0) {
				segType = SEG_MOVETO;
			} else if (pointNum == 4) {
				segType = SEG_CLOSE;
			} else {
				segType = SEG_LINETO;
			}

			if (pointNum == 0 || pointNum == 3) {
				coords[0] = x;
			} else if (pointNum == 1 || pointNum == 2) {
				coords[0] = x + s;
			}
			if (pointNum == 0 || pointNum == 1) {
				coords[1] = y;
			} else if (pointNum == 2 || pointNum == 3) {
				coords[1] = y + s;
			}

			if (at != null) {
				at.transform(coords, 0, coords, 0, 1);
			}
			return segType;
		}

		@Override
		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		@Override
		public boolean isDone() {
			return pointNum == 5;
		}

		@Override
		public void next() {
			if (pointNum < 5) {
				pointNum++;
			}
		}
	}

	/**
	 * Computes a square whose location and side length is the average of the
	 * locations and side lengths of the squares in the list.
	 * 
	 * @param squares
	 *            List of squares to average.
	 * @return A square representing the average of all squares in the list.
	 */
	public static Square average(List<Square> squares) {
		double sumX = 0;
		double sumY = 0;
		double sumSide = 0;
		for (Square s : squares) {
			sumX += s.x;
			sumY += s.y;
			sumSide = s.sideLength;
		}
		double scalar = 1.0 / squares.size();
		return new Square((int) (sumX * scalar), (int) (sumY * scalar), (int) (sumSide * scalar));
	}

	/**
	 * Computes the "distance" between the two squares, as if the square were a
	 * representation of a 3D coordinate space, with sideLength as a third
	 * spatial dimension
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static double difference(Square s1, Square s2) {
		return Math
				.sqrt(Math.pow(s1.x - s2.x, 2) + Math.pow(s1.y - s2.y, 2) + Math.pow(s1.sideLength - s2.sideLength, 2));
	}

	private int x, y, sideLength;

	/**
	 * Creates a {@link Square} with default location at (0, 0) and side length
	 * of 0
	 */
	public Square() {
		this(0, 0, 0);
	}

	/**
	 * Creates a square at the given location with given side length
	 * 
	 * @param x
	 * @param y
	 * @param sideLength
	 */
	public Square(int x, int y, int sideLength) {
		this.x = x;
		this.y = y;
		this.sideLength = sideLength;
	}

	@Override
	public boolean contains(double x, double y) {
		if (x >= this.x && x <= this.x + sideLength) {
			if (y >= this.y && y <= this.y + sideLength) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		if (x >= this.x && x + w <= this.x + sideLength) {
			if (y >= this.y && y + h <= this.y + sideLength) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Square)) {
			Square s2 = (Square) obj;
			if (this.x == s2.x && this.y == s2.y && this.sideLength == s2.sideLength) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, sideLength, sideLength);
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(x, y, sideLength, sideLength);
	}

	/**
	 * Calculates the location of the center of the Square
	 * 
	 * @return The location of the center of the square
	 */
	public Point2D.Double getCenter() {
		return new Point2D.Double(x + sideLength / 2.0, y + sideLength / 2.0);
	}

	/**
	 * Calculates the x coordinate of the center of the square
	 * 
	 * @return
	 */
	public double getCenterX() {
		return x + sideLength / 2.0;
	}

	/**
	 * Calculates the y coordinate of the center of the square
	 * 
	 * @return
	 */
	public double getCenterY() {
		return y + sideLength / 2.0;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new SquarePathIterator(x, y, sideLength, at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPathIterator(at);
	}

	/**
	 * Gets the side length of the square
	 * 
	 * @return square side length
	 */
	public int getSideLength() {
		return sideLength;
	}

	/**
	 * Gets the x coordinate of the square
	 * 
	 * @return the x coordinate of the square
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y coordinate of the square
	 * 
	 * @return the y coordinate of the square
	 */
	public int getY() {
		return y;
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		if (x < this.x && x + w > this.x || x < this.x + sideLength && x + w < this.x + sideLength) {
			if (y < this.y && y + h > this.y || y < this.y + sideLength && y + h < this.y + sideLength) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public String toString() {
		return "Square. Upper left: (" + x + ", " + y + "), side length: " + sideLength;
	}

}
