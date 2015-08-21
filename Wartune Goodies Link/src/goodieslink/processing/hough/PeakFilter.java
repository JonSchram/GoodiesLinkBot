package goodieslink.processing.hough;

import java.util.List;

/**
 * Interface defining methods for a filtering algorithm to resolve peaks in an
 * accumulator that are too close together
 * 
 * @author Jonathan Schram
 *
 * @param <T>
 *            The type of object that is represented in the accumulator
 */
public interface PeakFilter<T> {

	/**
	 * Determines whether two objects are too similar according to the filtering
	 * algorithm
	 * 
	 * @param s1
	 *            First object to compare
	 * @param s2
	 *            Second object to compare
	 * @return True if the objects are similar, false otherwise
	 */
	public boolean areSimilar(T s1, T s2);

	/**
	 * <p>
	 * Prompts the filtering algorithm to choose a single object that best
	 * represents the group of objects.
	 * </p>
	 * <p>
	 * The object returned is not required to be present in the original list,
	 * or a new one can be created; the interface makes no restriction.
	 * </p>
	 * Because the task of consolidating objects may require querying the
	 * original accumulator, an object can be passed to allow this.
	 * </p>
	 * 
	 * @param shapes
	 *            List of shapes that should be consolidated to a single
	 *            representative shape
	 * @param vq
	 *            Object to retrieve information from the accumulator.
	 * @return The best representative object from the list of objects
	 */
	public T chooseBest(List<T> shapes, VoteQuery vq);

}
