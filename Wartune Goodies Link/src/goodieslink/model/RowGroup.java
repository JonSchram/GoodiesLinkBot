package goodieslink.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import goodieslink.processing.Square;

public class RowGroup extends SquareGrouping {
	private int tolerance;
	private int ySum;
	private int count;

	public RowGroup(int tolerance) {
		this.tolerance = tolerance;
		ySum = 0;
		count = 0;
	}

	@Override
	public ArrayList<Square> getSorted() {
		ArrayList<Square> squares = getList();
		squares.sort(new Comparator<Square>() {
			@Override
			public int compare(Square o1, Square o2) {
				if (o1.getCenterX() > o2.getCenterX()) {
					return 1;
				} else if (o1.getCenterX() < o2.getCenterX()) {
					return -1;
				}
				return 0;
			}
		});
		return squares;
	}

	@Override
	public void add(Square s) {
		super.add(s);
		ySum += s.getCenterY();
		count++;
	}

	@Override
	public boolean belongs(Square s) {
		if (Math.abs(s.getCenterY() - averageY()) <= tolerance) {
			return true;
		}
		return false;
	}

	private double averageY() {
		if (count != 0) {
			return (double) ySum / count;
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
				double spacing = current.getCenterX() - previous.getCenterX();
				if (spacing < minSpacing || !hasMin) {
					minSpacing = spacing;
					hasMin = true;
				}
				previous = current;
			}
			return minSpacing;
		} else {
			return -1;
		}
	}

	@Override
	public double averageMinSpacing() {
		if (!isSorted()) {
			sort();
		}
		ArrayList<Double> spacings = new ArrayList<>();
		Iterator<Square> squareIterator = iterator();
		if (squareIterator.hasNext()) {
			Square previous = squareIterator.next();
			while (squareIterator.hasNext()) {
				Square current = squareIterator.next();
				double spacing = current.getCenterX() - previous.getCenterX();
				spacings.add(spacing);
				previous = current;
			}
			double minSpace = Collections.min(spacings);
			int count = 0;
			int spaceSum = 0;
			final double maxError = .1;
			for (double space : spacings) {
				// % difference formula
				if (2 * (space - minSpace) / (space + minSpace) < maxError) {
					// spacing is determined to be within acceptable error range
					spaceSum += space;
					count++;
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
}
