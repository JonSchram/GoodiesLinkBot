package goodieslink.model;

import goodieslink.processing.Square;

import java.awt.image.BufferedImage;
import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameBoard {
	private BufferedImage sourceImage;
	private List<Square> gridLocations;
	private int tolerance;

	public void setGridLocations(BufferedImage sourceImage, List<Square> gridLocations, int tolerance) {
		this.sourceImage = sourceImage;
		this.gridLocations = gridLocations;
		this.tolerance = tolerance;
		// TODO: use squares to match regions on source image
	}

	public void updateBoardState(BufferedImage newImage) {
		sourceImage = newImage;
		// TODO: decide which squares contain matching images
	}

	private void convertToArray() {
		ArrayList<RowGroup> rows = new ArrayList<>();
		ArrayList<ColumnGroup> cols = new ArrayList<>();
		getRowColumnList(rows, cols);

		for (RowGroup row : rows) {
			row.sort();
		}
		for (ColumnGroup col : cols) {
			col.sort();
		}

	}

	private double findMinSpacing(ArrayList<SquareGrouping> squareGroups) {
		Iterator<SquareGrouping> groupIterator = squareGroups.iterator();
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
