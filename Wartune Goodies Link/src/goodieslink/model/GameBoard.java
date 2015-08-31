package goodieslink.model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import goodieslink.processing.Square;
import goodieslink.processing.matching.DifferenceSquaredMeasure;
import goodieslink.processing.matching.RegionMatcher;
import goodieslink.processing.matching.SimilarityResult;

/**
 * Converts square pixel location to locations on a rectangular grid, and stores
 * the icon ID values for this grid.
 * 
 * @author Jonathan Schram
 *
 */
public class GameBoard {
	// private BufferedImage sourceImage;
	/**
	 * A list of squares to be converted to a grid
	 */
	private List<Square> gridLocations;
	/**
	 * 2D array storing icon IDs to determine which grid locations contain
	 * matching icons
	 */
	private int[][] iconIds;
	/**
	 * 2D array storing the actual Squares detected in the source image
	 */
	private Square[][] squareLocations;
	/**
	 * RegionMatcher that will be used to determine which squares are similar
	 */
	private RegionMatcher matcher;
	/**
	 * Similarity score required to identify two squares as similar
	 */
	private double similarityThreshold;

	/**
	 * Whether this object has processed an image to find matching squares
	 */
	private boolean initialized;
	/**
	 * Whether this object has been assigned a source image
	 */
	private boolean hasImage;
	/**
	 * Whether this object has been assigned detected squares
	 */
	private boolean hasSquares;
	/**
	 * Location of square (in pixel coordinates) in the upper left corner of the
	 * game board.<br>
	 * There is not necessarily a square in this location, but is the location
	 * that a square would be if there were one.
	 */
	private Point2D gridUpperLeft;
	/**
	 * Location of square (in pixel coordinates) in the lower right corner of
	 * the game board.<br>
	 * There is not necessarily a square in this location, but is the location
	 * that a square would be if there were one.
	 */
	private Point2D gridLowerRight;
	/**
	 * Number of squares horizontally
	 */
	private int gridWidth;
	/**
	 * Number of squares vertically
	 */
	private int gridHeight;
	/**
	 * average space (in pixels) between squares in adjacent columns
	 */
	private double averageColumnSpacing;
	/**
	 * average space (in pixels) between squares in adjacent rows
	 */
	private double averageRowSpacing;

	/**
	 * Maximum difference between square centers for squares to no longer be
	 * considered in the same row/column any more
	 */
	private int tolerance;

	/**
	 * Creates a new game board with specified parameters
	 * 
	 * @param sourceImage
	 *            Image of game grid
	 * @param locationTolerance
	 *            Maximum difference between squares before they are no longer
	 *            considered in the same row/column
	 * @param searchMargin
	 *            Maximum number of pixels to shift search regions to find a
	 *            matching icon
	 * @param similarityThreshold
	 *            Similarity score required for two icons to be considered
	 *            identical. Must be positive. Closer to 0 is a closer match.
	 */
	public GameBoard(BufferedImage sourceImage, int locationTolerance, int searchMargin, double similarityThreshold) {
		// this.sourceImage = sourceImage;
		this.tolerance = locationTolerance;
		this.matcher = new RegionMatcher(sourceImage, new DifferenceSquaredMeasure(), searchMargin);
		this.similarityThreshold = similarityThreshold;
		initialized = false;
		hasImage = true;
		hasSquares = false;
	}

	/**
	 * Creates a new game board with no initial image
	 * 
	 * @param locationTolerance
	 *            Maximum difference between squares before they are no longer
	 *            considered in the same row/column
	 * @param searchMargin
	 *            Maximum number of pixels to shift search regions to find a
	 *            matching icon
	 * @param similarityThreshold
	 *            Similarity score required for two icons to be considered
	 *            identical. Must be positive. Closer to 0 is a closer match.
	 */
	public GameBoard(int locationTolerance, int searchMargin, double similarityThreshold) {
		this.tolerance = locationTolerance;
		this.matcher = new RegionMatcher(new DifferenceSquaredMeasure(), searchMargin);
		this.similarityThreshold = similarityThreshold;
		initialized = false;
		hasImage = false;
		hasSquares = false;
	}

	/**
	 * Uses source image and square location mapping to determine which icons
	 * are similar
	 */
	private void computeSquareIds() {
		if (hasSquares && hasImage) {
			iconIds = new int[gridHeight][gridWidth];
			int nextID = 1;
			// HashSet<Integer> nonMatchingIcons = new HashSet<>();
			Set<Integer> nonMatchingIcons = Collections.synchronizedSet(new HashSet<Integer>());

			int numOfCores = Runtime.getRuntime().availableProcessors();
			ExecutorService pool = Executors.newFixedThreadPool(numOfCores);
			// ExecutorService pool = Executors.newCachedThreadPool();
			List<Future<SimilarityResult>> futures = new LinkedList<>();
			// for each square
			for (int row = 0; row < iconIds.length; row++) {
				for (int col = 0; col < iconIds[row].length; col++) {
					if (squareLocations[row][col] != null) {
						nonMatchingIcons.clear();
						// is a valid square
						// compare square to all in rows above current
						for (int i = 0; i < row && iconIds[row][col] == 0; i++) {
							for (int j = 0; j < iconIds[i].length && iconIds[row][col] == 0; j++) {
								// don't bother checking similarity if that icon
								// has been checked
								if (squareLocations[i][j] != null && !nonMatchingIcons.contains(iconIds[i][j])) {
									Future<SimilarityResult> future = pool.submit(new GameBoardWorker(new Point(i, j),
											new Point(row, col), iconIds[i][j], squareLocations[row][col],
											squareLocations[i][j], matcher, nonMatchingIcons));
									futures.add(future);
									// double similarity =
									// matcher.similarity(squareLocations[row][col],
									// squareLocations[i][j]);
									// if (similarity < similarityThreshold) {
									// iconIds[row][col] = iconIds[i][j];
									// } else {
									// nonMatchingIcons.add(iconIds[i][j]);
									// }
								}
							}
						}
						// compare square to all those to the left in same row
						for (int j = 0; j < col && iconIds[row][col] == 0; j++) {
							if (squareLocations[row][j] != null && !nonMatchingIcons.contains(iconIds[row][j])) {
								Future<SimilarityResult> future = pool.submit(new GameBoardWorker(new Point(row, j),
										new Point(row, col), iconIds[row][j], squareLocations[row][col],
										squareLocations[row][j], matcher, nonMatchingIcons));
								futures.add(future);
								// double similarity =
								// matcher.similarity(squareLocations[row][col],
								// squareLocations[row][j]);
								// if (similarity < similarityThreshold) {
								// iconIds[row][col] = iconIds[row][j];
								// } else {
								// nonMatchingIcons.add(iconIds[row][j]);
								// }
							}
						}
						boolean foundSimilar = false;
						for (Future<SimilarityResult> f : futures) {
							if (!foundSimilar) {
								SimilarityResult sr;
								try {
									sr = f.get();
									if (sr.getSimilarity() < similarityThreshold) {
										foundSimilar = true;
										Point dest = sr.getDest();
										iconIds[dest.x][dest.y] = sr.getSourceValue();
									} else {
										nonMatchingIcons.add(sr.getSourceValue());
									}
								} catch (InterruptedException | ExecutionException e) {
									e.printStackTrace();
								}
							} else {
								f.cancel(false);
							}
							// futureIterator.remove();
						}
						futures.clear();
						if (iconIds[row][col] == 0) {
							// no match has been found and it is end of search
							iconIds[row][col] = nextID++;
						}
						// end comparisons
					}
					// end matching a valid square

				}
			}
			pool.shutdownNow();
		} else {
			throw new NullPointerException("Can't compute square IDs, no image or no squares");
		}
	}

	/**
	 * Takes the list of squares and determines which grid location it would be
	 * in
	 */
	private void convertToSquareArray() {
		if (hasSquares) {

			ArrayList<RowGroup> rows = new ArrayList<>();
			ArrayList<ColumnGroup> cols = new ArrayList<>();
			getRowColumnList(rows, cols);

			for (RowGroup row : rows) {
				row.sort();
			}
			for (ColumnGroup col : cols) {
				col.sort();
			}

			// minimum space between squares in adjacent columns
			// double minColumnSpacing = findMinSpacing(rows);
			averageColumnSpacing = findAverageSpacing(rows);
			// minimum space between squares in adjacent rows
			// double minRowSpacing = findMinSpacing(cols);
			averageRowSpacing = findAverageSpacing(cols);

			if (averageColumnSpacing != -1 && averageRowSpacing != -1) {

				// only have to search rows for min because both rows and cols
				// contain
				// all squares
				gridUpperLeft = findMin(rows);
				gridLowerRight = findMax(rows);
				gridWidth = 1 + (int) Math.round((gridLowerRight.getX() - gridUpperLeft.getX()) / averageColumnSpacing);
				gridHeight = 1 + (int) Math.round((gridLowerRight.getY() - gridUpperLeft.getY()) / averageRowSpacing);

				iconIds = new int[gridHeight][gridWidth];
				squareLocations = new Square[gridHeight][gridWidth];

				for (Square s : gridLocations) {
					int rowNum = (int) Math.round((s.getCenterY() - gridUpperLeft.getY()) / averageRowSpacing);
					int colNum = (int) Math.round((s.getCenterX() - gridUpperLeft.getX()) / averageColumnSpacing);
					squareLocations[rowNum][colNum] = s;
				}
			} else {
				// not enough squares to establish a grid, no average spacing
				// found
				throw new NoSuchElementException("Not enough squares, conversion to grid failed.");
			}
		} else {
			throw new NullPointerException("No squares, can't convert to grid");
		}

	}

	/**
	 * Finds average spacing between squares in the SquareGrouping
	 * 
	 * @param squareGroups
	 *            List of groups in which to search for an average spacing
	 * @return
	 */
	private double findAverageSpacing(ArrayList<? extends SquareGrouping> squareGroups) {
		Iterator<? extends SquareGrouping> groupIterator = squareGroups.iterator();
		if (groupIterator.hasNext()) {
			int count = 0;
			double spaceSum = 0;
			while (groupIterator.hasNext()) {
				SquareGrouping group = groupIterator.next();
				double averageSpace = group.averageMinSpacing();
				if (averageSpace != -1) {
					if (count != 0) {
						if (Math.round(averageSpace * count / spaceSum) == 1) {
							// gaps are similar enough that this is the true
							// minimum spacing
							count++;
							spaceSum += averageSpace;
						} else if (Math.round(spaceSum / (count * averageSpace)) > 1) {
							// running average divided by current average is
							// big, current average is true one
							count = 1;
							spaceSum = averageSpace;
						}
						// discards spacings that are too different
					} else {
						count = 1;
						spaceSum = averageSpace;
					}
				}
			}
			if (count != 0) {
				return spaceSum / count;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * Finds the maximum square location in the list of square groups
	 * 
	 * @param squareGroups
	 *            List to search
	 * @return
	 */
	private Point2D.Double findMax(ArrayList<? extends SquareGrouping> squareGroups) {
		Iterator<? extends SquareGrouping> groupIterator = squareGroups.iterator();
		SquareGrouping group = groupIterator.next();
		double maxX = group.get(group.size() - 1).getCenterX();
		double maxY = group.get(group.size() - 1).getCenterY();
		while (groupIterator.hasNext()) {
			group = groupIterator.next();
			double tempX = group.get(group.size() - 1).getCenterX();
			double tempY = group.get(group.size() - 1).getCenterY();
			if (tempX > maxX) {
				maxX = tempX;
			}
			if (tempY > maxY) {
				maxY = tempY;
			}
		}
		return new Point2D.Double(maxX, maxY);
	}

	/**
	 * Finds the minimum square location in the list
	 * 
	 * @param squareGroups
	 *            List to search
	 * @return
	 */
	private Point2D.Double findMin(ArrayList<? extends SquareGrouping> squareGroups) {
		Iterator<? extends SquareGrouping> groupIterator = squareGroups.iterator();
		SquareGrouping group = groupIterator.next();
		double minX = group.get(0).getCenterX();
		double minY = group.get(0).getCenterY();
		while (groupIterator.hasNext()) {
			group = groupIterator.next();
			double tempX = group.get(0).getCenterX();
			double tempY = group.get(0).getCenterY();
			if (tempX < minX) {
				minX = tempX;
			}
			if (tempY < minY) {
				minY = tempY;
			}
		}
		return new Point2D.Double(minX, minY);
	}

	/**
	 * Find minimum spacing between square groups.<br>
	 * Deprecated because this can cause serious problems in grid detection,
	 * since a few pixel difference in the minimum will have a big effect on
	 * grid locations at extreme ends of the game board
	 * 
	 * @param squareGroups
	 *            List of groups to search
	 * @return
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private double findMinSpacing(ArrayList<? extends SquareGrouping> squareGroups) {
		Iterator<? extends SquareGrouping> groupIterator = squareGroups.iterator();
		if (groupIterator.hasNext()) {
			double minSpacing = groupIterator.next().minSpacing();
			while (groupIterator.hasNext()) {
				SquareGrouping group = groupIterator.next();
				double temp = group.minSpacing();
				if (temp < minSpacing) {
					minSpacing = temp;
				}
			}
			return minSpacing;
		} else {
			return -1;
		}
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	/**
	 * Converts the list of the game board's squares into a list of rows and
	 * columns, using lists <code>rows</code> and <code>cols</code> as output
	 * variables
	 * 
	 * @param rows
	 *            Output variable for list of row groups
	 * @param cols
	 *            Output variable for list of column groups
	 */
	private void getRowColumnList(ArrayList<RowGroup> rows, ArrayList<ColumnGroup> cols) {
		Iterator<Square> squareIterator = gridLocations.iterator();
		while (squareIterator.hasNext()) {
			Square s = squareIterator.next();
			// put square in proper row
			boolean belongs = false;
			Iterator<RowGroup> rowIterator = rows.iterator();
			while (rowIterator.hasNext() && !belongs) {
				RowGroup temp = rowIterator.next();
				if (temp.belongs(s)) {
					temp.add(s);
					belongs = true;
				}
			}
			if (!belongs) {
				// didn't belong to any row
				RowGroup newRow = new RowGroup(tolerance);
				newRow.add(s);
				rows.add(newRow);
			}

			// put in proper column
			belongs = false;
			Iterator<ColumnGroup> columnIterator = cols.iterator();
			while (columnIterator.hasNext() && !belongs) {
				ColumnGroup temp = columnIterator.next();
				if (temp.belongs(s)) {
					temp.add(s);
					belongs = true;
				}
			}
			if (!belongs) {
				// didn't belong to any column
				ColumnGroup newCol = new ColumnGroup(tolerance);
				newCol.add(s);
				cols.add(newCol);
			}
		}
	}

	/**
	 * Gets size of game board
	 * 
	 * @return A Dimension representing game board size
	 */
	public Dimension getSize() {
		int width;
		int height;
		if (initialized) {
			width = gridWidth;
			height = gridHeight;
		} else {
			width = 0;
			height = 0;
		}
		return new Dimension(width, height);
	}

	/**
	 * Returns the icon ID for the specified square
	 * 
	 * @param row
	 *            Row number
	 * @param col
	 *            Column number
	 * @return Icon ID of square located at row and column
	 */
	public int getSquareId(int row, int col) {
		if (iconIds != null) {
			if (row >= 0 && row < iconIds.length) {
				if (col >= 0 && col < iconIds[row].length) {
					return iconIds[row][col];
				}
			}
		}
		return -1;
	}

	public Square[][] getSquareGrid() {
		return squareLocations;
	}

	/**
	 * Computes the location of the pixel at the center of the location on the
	 * grid.
	 * 
	 * @param gridLocation
	 * @return
	 */
	public Point gridToPixel(Point gridLocation) {
		int pixelX = 0;
		int pixelY = 0;
		if (initialized) {
			pixelX = (int) (gridUpperLeft.getX() + gridLocation.getX() * averageColumnSpacing);
			pixelY = (int) (gridUpperLeft.getY() + gridLocation.getY() * averageRowSpacing);
		}
		Point result = new Point(pixelX, pixelY);
		return result;
	}

	/**
	 * Determines whether the game board has been cleared of squares
	 * 
	 * @return True if there is at least one square, false otherwise
	 */
	public boolean isEmpty() {
		boolean hasSquare = false;
		if (initialized) {
			for (int i = 0; i < gridHeight && !hasSquare; i++) {
				for (int j = 0; j < gridWidth && !hasSquare; j++) {
					if (iconIds[i][j] != 0) {
						hasSquare = true;
					}
				}
			}
		}
		return !hasSquare;
	}

	/**
	 * Returns whether this GameBoard has analyzed a set of squares and detected
	 * a grid with icons
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Computes the closest grid location from the coordinates of a pixel in the
	 * interior of a square.
	 * 
	 * @param pixelLocation
	 * @return
	 */
	public Point pixelToGrid(Point pixelLocation) {
		int rowNum = 0;
		int colNum = 0;
		if (initialized) {
			rowNum = (int) Math.round((pixelLocation.getY() - gridUpperLeft.getY()) / averageRowSpacing);
			colNum = (int) Math.round((pixelLocation.getX() - gridUpperLeft.getX()) / averageColumnSpacing);
		}
		Point result = new Point(rowNum, colNum);
		return result;
	}

	/**
	 * Removes a square from the game board's grid
	 * 
	 * @param row
	 *            Row number
	 * @param col
	 *            Column number
	 */
	public void removeSpace(int row, int col) {
		if (iconIds != null) {
			if (row >= 0 && row < iconIds.length) {
				if (col >= 0 && col < iconIds[row].length) {
					// clear by setting icon to 0 and
					// setting that square to null
					iconIds[row][col] = 0;
					squareLocations[row][col] = null;
				}
			}
		}
	}

	/**
	 * Removes a square from the game board grid at specified point
	 * 
	 * @param space
	 *            Location of grid space to remove
	 */
	public void removeSpace(Point space) {
		removeSpace(space.y, space.x);
	}

	/**
	 * Assigns a new set of squares to the game board and processes image using
	 * these squares
	 * 
	 * @param gridLocations
	 *            List of squares where icons are located
	 */
	public void setGridLocations(List<Square> gridLocations) {
		this.gridLocations = gridLocations;
		if (gridLocations.size() > 0) {
			// guard against no squares detected
			hasSquares = true;
			convertToSquareArray();
			computeSquareIds();
			initialized = true;
		}
	}

	/**
	 * Assigns a new image and triggers re-assigning icon IDs
	 * 
	 * @param newImage
	 *            New source image
	 */
	public void setImage(BufferedImage newImage) {
		// sourceImage = newImage;
		matcher.setImage(newImage);
		hasImage = true;
		computeSquareIds();
	}

	/**
	 * Sets image of matcher without trigging a computation of square IDs,
	 * required when setting up the game board for the first time
	 * 
	 * @param newImage
	 */
	public void setImageQuiet(BufferedImage newImage) {
		matcher.setImage(newImage);
		hasImage = true;
	}

	public void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	/**
	 * Sets icon ID of the specified grid location to the specified ID
	 * 
	 * @param row
	 *            Row number of icon
	 * @param col
	 *            Column number of icon
	 * @param id
	 *            New icon ID for this grid location
	 */
	public void setSquareId(int row, int col, int id) {
		if (iconIds != null) {
			if (row >= 0 && row < iconIds.length) {
				if (col >= 0 && col < iconIds[row].length) {
					iconIds[row][col] = id;
				}
			}
		}
	}

	public void setTolerance(int tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Counts how many squares are remaining on the grid
	 * 
	 * @return
	 */
	public int getCountRemaining() {
		int count = 0;
		if (initialized) {
			for (int i = 0; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					if (iconIds[i][j] != 0) {
						count++;
					}
				}
			}
		}
		return count;
	}
}