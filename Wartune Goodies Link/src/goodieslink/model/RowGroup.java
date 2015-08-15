package goodieslink.model;

import java.util.Comparator;
import java.util.List;

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
	public List<Square> getSorted() {
		List<Square> squares = getList();
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

}
