package goodieslink.processing.hough;

import java.util.List;

public interface PeakFilter<T> {

	/**
	 * Determines whether
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public boolean areSimilar(T s1, T s2);

	public T chooseBest(List<T> shapes, VoteQuery vq);

}
