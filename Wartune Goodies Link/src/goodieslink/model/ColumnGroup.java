package goodieslink.model;

import java.util.ArrayList;
import java.util.Comparator;

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

}
