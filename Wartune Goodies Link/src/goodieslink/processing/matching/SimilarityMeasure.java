package goodieslink.processing.matching;

/**
 * Interface defining a method of determining how similar two pixels are to one
 * another, independent of algorithm used.
 * 
 * @author Jonathan
 *
 */
public interface SimilarityMeasure {
	/**
	 * Perform similarity algorithm on the red, green, and blue color channels.
	 * 
	 * @param r1
	 *            Red component of pixel 1
	 * @param g1
	 *            Green component of pixel 1
	 * @param b1
	 *            Blue component of pixel 1
	 * @param r2
	 *            Red component of pixel 2
	 * @param g2
	 *            Green component of pixel 2
	 * @param b2
	 *            Blue component of pixel 2
	 * @return Result of similarity algorithm, as a double precision floating
	 *         point value
	 */
	public double similarity(byte r1, byte g1, byte b1, byte r2, byte g2, byte b2);
}
