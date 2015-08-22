package goodieslink.model;

import java.util.ArrayList;
import java.util.Iterator;

import goodieslink.processing.Square;

/**
 * Class storing a group of squares related in some way. Requires some way to
 * determine if a new square would belong in this group, and some method of
 * sorting the squares, though the implementations of these are up to derived
 * classes.
 * 
 * @author Jonathan Schram
 *
 */
public abstract class SquareGrouping implements Iterable<Square> {

	/**
	 * Squares stored in this SquareGrouping
	 */
	private ArrayList<Square> group;
	/**
	 * Whether this group is sorted
	 */
	private boolean sorted;

	/**
	 * Constructs a new SquareGrouping with no squares
	 */
	public SquareGrouping() {
		group = new ArrayList<>();
		// no squares yet is still sorted
		sorted = true;
	}

	/**
	 * Returns a reference to the list of squares in this group
	 * 
	 * @return
	 */
	public ArrayList<Square> getList() {
		return group;
	}

	/**
	 * Sort the group and overwrite the original group with the sorted version
	 */
	public void sort() {
		sorted = true;
	}

	/**
	 * Adds a new square to the group
	 * 
	 * @param s
	 *            Square to add
	 */
	public void add(Square s) {
		group.add(s);
		// since a new element was added we can't be sure the group is sorted
		sorted = false;
	}

	/**
	 * Gets the square at the specified index
	 * 
	 * @param index
	 *            Index of square to get
	 * @return Square it the index
	 * @throws IndexOutOfBoundsException
	 */
	public Square get(int index) throws IndexOutOfBoundsException {
		return group.get(index);
	}

	@Override
	public Iterator<Square> iterator() {
		return group.iterator();
	}

	/**
	 * Determines whether this new square belongs in the SquareGrouping
	 * 
	 * @param s
	 *            Square to test
	 * @return True if the square is determined to belong, false otherwise
	 */
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

	/**
	 * Returns whether the group is known to be sorted
	 * 
	 * @return True if the group is sorted, false otherwise
	 */
	public boolean isSorted() {
		return sorted;
	}

	/**
	 * Returns the minimum spacing between group members
	 * 
	 * @return Minimum spacing between group members
	 */
	public abstract double minSpacing();

	/**
	 * Returns the average minimum spacing of group members
	 * 
	 * @return Average minimum spacing in group
	 */
	public abstract double averageMinSpacing();

	/**
	 * Returns the number of members in the group
	 * 
	 * @return Size of group
	 */
	public int size() {
		return group.size();
	}

	/**
	 * Returns average side length of squares in group
	 * 
	 * @return Average square side length
	 */
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
