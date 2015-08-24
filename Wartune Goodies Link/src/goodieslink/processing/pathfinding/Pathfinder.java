package goodieslink.processing.pathfinding;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import goodieslink.model.GameBoard;

/**
 * 
 * Finds paths through the goodies link grid following the rule that two
 * identical icons must be matched and the path between them can't cross grid
 * spaces with an icon
 * 
 * @author Jonathan Schram
 *
 */
public class Pathfinder {
	/**
	 * Game board storing icon information
	 */
	private GameBoard board;

	/**
	 * Constructs Pathfinder to find paths on the given board.
	 * 
	 * @param board
	 *            Game board holding icon data
	 */
	public Pathfinder(GameBoard board) {
		this.board = board;
	}

	/**
	 * Finds and returns a valid path following the rules of the Goodies Link
	 * game, or null if there are no paths.
	 * 
	 * @return A valid path, null if no paths exist
	 */
	public GoodiePath findPath() {
		GoodiePath path = new GoodiePath();

		Dimension boardSize = board.getSize();

		// whether two matching spaces have been found;
		// they may not be clickable
		boolean pairFound = false;
		Set<Integer> failedIds = new HashSet<>();

		int row = 0;
		while (row < boardSize.height && !pairFound) {
			int col = 0;
			while (col < boardSize.width && !pairFound) {
				int squareId = board.getSquareId(row, col);
				if (squareId != 0 && !failedIds.contains(squareId)) {
					Point[] matches = getAllMatches(squareId);
					int compare1 = 0;
					while (compare1 < matches.length - 1 && !pairFound) {
						Point startPoint = matches[compare1];
						int compare2 = compare1 + 1;
						while (compare2 < matches.length && !pairFound) {
							Point endPoint = matches[compare2];
							path = getPath(startPoint, endPoint);
							pairFound = path != null;
							compare2++;
						}
						compare1++;
					}
					if (pairFound == false) {
						failedIds.add(squareId);
					}
				}
				col++;
			}
			row++;
		}

		return path;
	}

	/**
	 * Gets all spaces in the game board with the same icon ID as the specified
	 * ID
	 * 
	 * @param squareId
	 *            Icon ID to match
	 * @return All grid locations with this icon ID
	 */
	private Point[] getAllMatches(int squareId) {
		ArrayList<Point> matches = new ArrayList<>();
		Dimension boardSize = board.getSize();
		for (int i = 0; i < boardSize.height; i++) {
			for (int j = 0; j < boardSize.width; j++) {
				if (board.getSquareId(i, j) == squareId) {
					matches.add(new Point(j, i));
				}
			}
		}
		return matches.toArray(new Point[matches.size()]);
	}

	/**
	 * Attempts to get a path following Goodies Link rules connecting specified
	 * grid locations
	 * 
	 * @param startPoint
	 *            First grid location
	 * @param endPoint
	 *            Second grid location
	 * @return A {@link GoodiePath} connecting the two grid locations, or null
	 *         if no valid path exists for this pair of points
	 */
	private GoodiePath getPath(Point startPoint, Point endPoint) {
		boolean pathValid = false;
		GoodiePath testPath = new GoodiePath(startPoint, endPoint);
		if (startPoint.x == endPoint.x || startPoint.y == endPoint.y) {
			pathValid = isPathValid(testPath);
			if (pathValid) {
				return testPath;
			}
		}

		Dimension size = board.getSize();
		for (int bendY = 0; bendY < size.height; bendY++) {
			if (bendY == 0) {
				// set y location 1 above the grid
				testPath.setCorner1(new Point(startPoint.x, -1));
				testPath.setCorner2(new Point(endPoint.x, -1));
			} else if (bendY == size.height - 1) {
				// set y location 1 below the grid
				testPath.setCorner1(new Point(startPoint.x, size.height));
				testPath.setCorner2(new Point(endPoint.x, size.height));
			} else {
				testPath.setCorner1(new Point(startPoint.x, bendY));
				testPath.setCorner2(new Point(endPoint.x, bendY));
			}

			pathValid = isPathValid(testPath);
			if (pathValid) {
				return testPath;
			}
		}

		for (int bendX = 0; bendX < size.width; bendX++) {
			if (bendX == 0) {
				// set y location 1 to the left of the grid
				testPath.setCorner1(new Point(-1, startPoint.y));
				testPath.setCorner2(new Point(-1, endPoint.y));
			} else if (bendX == size.width - 1) {
				// set y location 1 to the right of the grid
				testPath.setCorner1(new Point(size.width, startPoint.y));
				testPath.setCorner2(new Point(size.width, endPoint.y));
			} else {
				testPath.setCorner1(new Point(bendX, startPoint.y));
				testPath.setCorner2(new Point(bendX, endPoint.y));
			}

			pathValid = isPathValid(testPath);
			if (pathValid) {
				return testPath;
			}
		}

		return null;

	}

	/**
	 * Determines whether this GoodiePath follows game rules (by not crossing
	 * any other icon)
	 * 
	 * @param path
	 *            Path to test
	 * @return True if the path is valid, false otherwise
	 */
	private boolean isPathValid(GoodiePath path) {
		boolean hasCollision = false;
		Dimension size = board.getSize();

		Iterator<Point> pathPoints = path.iterator();
		// skip start point
		pathPoints.next();

		while (pathPoints.hasNext() && !hasCollision) {
			Point p = pathPoints.next();
			// ignore points off the board
			if (p.x >= 0 && p.x <= size.width && p.y >= 0 && p.y <= size.height) {
				if (board.getSquareId(p.y, p.x) != 0) {
					if (pathPoints.hasNext()) {
						// runs into a square that isn't the final one
						hasCollision = true;
					}
				}
			}
		}
		return !hasCollision;
	}

}
