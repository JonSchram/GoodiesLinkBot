package goodieslink.processing.hough;

/**
 * Interface that allows query access to an accumulator with any number of
 * dimensions
 * 
 * @author Jonathan Schram
 *
 */
public interface VoteQuery {

	/**
	 * Queries for the number of votes for an object represented by any number
	 * of integer dimensions
	 * 
	 * @param args
	 *            List of dimensions composing the query
	 * @return The number of votes in the accumulator bin queried
	 */
	public int getVotes(int... args);
}
