package goodieslink.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import goodieslink.processing.Square;

/**
 * SquareGrouping representing a group of squares aligned in the same column in
 * a grid
 * 
 * @author Jonathan Schram
 *
 */
public class ColumnGroup extends SquareGrouping {
	/**
	 * Maximum deviation from column alignment when determing whether to add
	 * another square to the group
	 */
	private int tolerance;
	/**
	 * Sum of all square x-coordinates
	 */
	private int xSum;
	/**
	 * Number of squares in the group
	 */
	private int count;

	/**
	 * Creates a new ColumnGroup with specified tolerance
	 * 
	 * @param tolerance
	 *            Maximum difference in average column center and new square
	 *            center when determining if a square belongs to the group
	 */
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
	public void sort() {
		super.sort();
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
	}

	@Override
	public boolean belongs(Square s) {
		if (Math.abs(s.getCenterX() - averageX()) <= tolerance) {
			return true;
		}
		return false;
	}

	/**
	 * Calculates the average x coordinate of the squares in this column
	 * 
	 * @return
	 */
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
				double spacing = current.getCenterY() - previous.getCenterY();
				spacings.add(spacing);
				previous = current;
			}
			// threw exception here, the set was empty
			// should return -1 to say there is no min
			if (spacings.size() > 0) {
				double minSpace = Collections.min(spacings);
				int count = 0;
				int spaceSum = 0;
				final double maxError = .1;
				for (double space : spacings) {
					// % difference formula
					if (2 * (space - minSpace) / (space + minSpace) < maxError) {
						// spacing is determined to be within acceptable error
						// range
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
				// set is empty
				return -1;
			}
		} else {
			return -1;
		}
	}
}
