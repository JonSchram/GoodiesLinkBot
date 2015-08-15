package goodieslink.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import goodieslink.processing.Square;

public class ColumnGroup extends SquareGrouping {
	private int tolerance;
	private int xSum;
	private int count;

	public ColumnGroup(int tolerance) {
		this.tolerance = tolerance;
		xSum = 0;
		count = 0;
	}

	@Override
	public void add(Square s) {
		super.add(s);
		xSum += s.getCenterX();
		count++;
	}

	@Override
	public ArrayList<Square> getSorted() {
		ArrayList<Square> squares = getList();
		squares.sort(new Comparator<Square>() {
			@Override
			public int compare(Square o1, Square o2) {
				if (o1.getCenterY() > o2.getCenterY()) {
					return 1;
				} else if (o1.getCenterY() < o2.getCenterY()) {
					return -1;
				}
				return 0;
			}
		});
		return squares;
	}

	@Override
	public boolean belongs(Square s) {
		if (Math.abs(s.getCenterX() - averageX()) <= tolerance) {
			return true;
		}
		return false;
	}

	private double averageX() {
		if (count != 0) {
			return (double) xSum / count;
		} else {
			return 0;
		}
	}

	@Override
	public double minSpacing() {
		if (!isSorted()) {
			sort();
		}
		Iterator<Square> squareIterator = iterator();
		if (squareIterator.hasNext()) {
			Square previous = squareIterator.next();
			double minSpacing = -1;
			boolean hasMin = false;
			while (squareIterator.hasNext()) {
				Square current = squareIterator.next();
				double spacing = current.getCenterY() - previous.getCenterY();
				if (spacing < minSpacing || !hasMin) {
					minSpacing = spacing;
				}
				previous = current;
			}
			return minSpacing;
		} else {
			return -1;
		}
	}
}
