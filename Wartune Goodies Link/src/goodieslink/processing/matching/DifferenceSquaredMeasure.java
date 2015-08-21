package goodieslink.processing.matching;

/**
 * <p>
 * Implementation of {@link SimilarityMeasure} that computes the difference as
 * the sum of the square of the difference between corresponding color
 * components.
 * </p>
 * Example:<br>
 * Difference in red component = (r1 - r2) ^ 2
 * 
 * @author Jonathan Schram
 *
 */
public class DifferenceSquaredMeasure implements SimilarityMeasure {

	@Override
	public double similarity(byte r1, byte g1, byte b1, byte r2, byte g2, byte b2) {
		return Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2);
	}

}
