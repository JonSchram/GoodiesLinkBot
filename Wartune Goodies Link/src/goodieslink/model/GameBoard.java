package goodieslink.model;

import goodieslink.processing.Square;

import java.awt.image.BufferedImage;
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
