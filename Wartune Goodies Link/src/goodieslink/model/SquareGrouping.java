package goodieslink.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import goodieslink.processing.Square;

public abstract class SquareGrouping implements Iterable<Square> {

	private ArrayList<Square> group;

	public SquareGrouping() {
		group = new ArrayList<>();
	}

	public List<Square> getList() {
		return new ArrayList<>(group);
	}

	public abstract List<Square> getSorted();

	public void add(Square s) {
		group.add(s);
	}

	public Iterator<Square> iterator() {
		return group.iterator();
	}

	public abstract boolean belongs(Square s);
}
