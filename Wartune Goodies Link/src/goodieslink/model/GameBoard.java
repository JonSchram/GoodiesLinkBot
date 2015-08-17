package goodieslink.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import goodieslink.processing.Square;
import goodieslink.processing.matching.DifferenceSquaredMeasure;
import goodieslink.processing.matching.RegionMatcher;
import goodieslink.processing.matching.SimilarityResult;

public class GameBoard {
	// private BufferedImage sourceImage;
	private List<Square> gridLocations;
	private int[][] iconIds;
	private Square[][] squareLocations;
	private RegionMatcher matcher;
	double similarityThreshold;

	/**
	 * Maxmimum difference between square centers for squares to no longer be
	 * considered in the same row/column any more
	 */
	private int tolerance;

	public GameBoard(BufferedImage sourceImage, int locationTolerance, int searchMargin, double similarityThreshold) {
		// this.sourceImage = sourceImage;
		this.tolerance = locationTolerance;
		this.matcher = new RegionMatcher(sourceImage, new DifferenceSquaredMeasure(), searchMargin);
		this.similarityThreshold = similarityThreshold;
	}

	public void setGridLocations(List<Square> gridLocations) {
		this.gridLocations = gridLocations;
		convertToSquareArray();
		setSquareIds();
	}

	public void updateBoardState(BufferedImage newImage) {
		// sourceImage = newImage;
		matcher.setImage(newImage);
		setSquareIds();
	}

	private void setSquareIds() {
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
							// don't bother checking similarity if that icon has
							// been checked
							if (!nonMatchingIcons.contains(iconIds[i][j])) {
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
						if (!nonMatchingIcons.contains(iconIds[row][j])) {
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
	}

	private void convertToSquareArray() {
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
		double averageColumnSpacing = findAverageSpacing(rows);
		// minimum space between squares in adjacent rows
		// double minRowSpacing = findMinSpacing(cols);
		double averageRowSpacing = findAverageSpacing(cols);

		// only have to search rows for min because both rows and cols contain
		// all squares
		Point2D gridUpperLeft = findMin(rows);
		Point2D gridLowerRight = findMax(rows);
		int gridWidth = 1 + (int) Math.round((gridLowerRight.getX() - gridUpperLeft.getX()) / averageColumnSpacing);
		int gridHeight = 1 + (int) Math.round((gridLowerRight.getY() - gridUpperLeft.getY()) / averageRowSpacing);

		iconIds = new int[gridHeight][gridWidth];
		squareLocations = new Square[gridHeight][gridWidth];

		for (Square s : gridLocations) {
			int rowNum = (int) Math.round((s.getCenterY() - gridUpperLeft.getY()) / averageRowSpacing);
			int colNum = (int) Math.round((s.getCenterX() - gridUpperLeft.getX()) / averageColumnSpacing);
			squareLocations[rowNum][colNum] = s;
		}

	}

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

	private double findAverageSpacing(ArrayList<? extends SquareGrouping> squareGroups) {
		Iterator<? extends SquareGrouping> groupIterator = squareGroups.iterator();
		if (groupIterator.hasNext()) {
			int count = 0;
			double spaceSum = 0;
			while (groupIterator.hasNext()) {
				SquareGrouping group = groupIterator.next();
				double averageSpace = group.averageMinSpacing();
				if (averageSpace != -1) {
					count++;
					spaceSum += averageSpace;
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

}
