package goodieslink.model;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import goodieslink.processing.Square;
import goodieslink.processing.matching.DifferenceSquaredMeasure;
import goodieslink.processing.matching.RegionMatcher;

public class GameBoard {
	private BufferedImage sourceImage;
	private List<Square> gridLocations;
	private int[][] iconIds;
	private Square[][] squareLocations;
	private RegionMatcher matcher;

	/**
	 * Maxmimum difference between square centers for squares to no longer be
	 * considered in the same row/column any more
	 */
	private int tolerance;

	public GameBoard(BufferedImage sourceImage, int tolerance, int searchMargin) {
		this.sourceImage = sourceImage;
		this.tolerance = tolerance;
		this.matcher = new RegionMatcher(sourceImage, new DifferenceSquaredMeasure(), searchMargin);
	}

	public void setGridLocations(List<Square> gridLocations) {
		this.gridLocations = gridLocations;
		// TODO: use squares to match regions on source image
	}

	public void updateBoardState(BufferedImage newImage) {
		sourceImage = newImage;
		matcher.setImage(newImage);
		// TODO: decide which squares contain matching images
	}

	private void setSquareIds() {
		for (int row = 0; row < iconIds.length; row++) {
			for (int col = 0; col < iconIds[row].length; col++) {
				if (squareLocations[row][col] != null) {

				}
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
		double minColumnSpacing = findMinSpacing(rows);
		// minimum space between squares in adjacent rows
		double minRowSpacing = findMinSpacing(cols);

		// only have to search rows for min because both rows and cols contain
		// all squares
		Point2D gridUpperLeft = findMin(rows);
		Point2D gridLowerRight = findMax(rows);
		int gridWidth = 1 + (int) Math.round((gridLowerRight.getX() - gridUpperLeft.getX()) / minColumnSpacing);
		int gridHeight = 1 + (int) Math.round((gridLowerRight.getY() - gridUpperLeft.getY()) / minRowSpacing);

		iconIds = new int[gridHeight][gridWidth];
		squareLocations = new Square[gridHeight][gridWidth];

		for (Square s : gridLocations) {
			int rowNum = (int) Math.round((s.getCenterY() - gridUpperLeft.getY()) / minRowSpacing);
			int colNum = (int) Math.round((s.getCenterX() - gridUpperLeft.getX()) / minColumnSpacing);
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
