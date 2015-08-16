package goodieslink.model;

import java.util.ArrayList;
import java.util.Iterator;

import goodieslink.processing.Square;

public abstract class SquareGrouping implements Iterable<Square> {

	private ArrayList<Square> group;
	private boolean sorted;

	public SquareGrouping() {
		group = new ArrayList<>();
		sorted = true;
	}

	public ArrayList<Square> getList() {
		return new ArrayList<>(group);
	}

	public void sort() {
		group = getSorted();
		sorted = true;
	}

	public abstract ArrayList<Square> getSorted();

	public void add(Square s) {
		group.add(s);
		// since a new element was added we can't be sure the group is sorted
		sorted = false;
	}

	public Square get(int index) throws IndexOutOfBoundsException {
		return group.get(index);
	}

	public Iterator<Square> iterator() {
		return group.iterator();
	}

	public abstract boolean belongs(Square s);

	/**
	 * Returns the index of the specified square in the SquareGrouping, or -1 if
	 * the square doesn't exist. List is sorted if it is not already, and the
	 * search is, for now, a simple linear search through the ArrayList's
	 * indexOf() method
	 * 
	 * @param s
	 * @return
	 */
	public int find(Square s) {
		if (!sorted) {
			sort();
		}
		return group.indexOf(s);
	}

	public boolean isSorted() {
		return sorted;
	}

	public abstract double minSpacing();

	public abstract double averageMinSpacing();

	public int size() {
		return group.size();
	}

	protected double averageBoxSize() {
		int count = group.size();
		if (count != 0) {
			double sizeSum = 0;
			for (Square s : group) {
				sizeSum += s.getSideLength();
			}
			return sizeSum / count;
		} else {
			return -1;
		}
	}
}
