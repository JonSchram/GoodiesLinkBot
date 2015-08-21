package goodieslink.processing.hough;

import goodieslink.processing.Square;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Implementation of the PeakFilter for Square objects. Assumes that squares are
 * to be arranged in a grid.
 * 
 * @author Jonathan Schram
 *
 */
public class GridFilter implements PeakFilter<Square> {
	/**
	 * Maximum distance between two squares for them to be considered similar
	 */
	private double spacingTolerance;
	/**
	 * Maximum Difference in side length for two squares to be considered to
	 * have a similar side length
	 */
	private double sideTolerance;

	/**
	 * Constructs a GridFilter with the given spacing and size constraints
	 * 
	 * @param minSpacing
	 *            Maximum distance between two squares for them to be considered
	 *            similar
	 * @param sizeTolerance
	 *            Difference in square side length below which should be
	 *            considered similar size
	 */
	public GridFilter(double minSpacing, double sizeTolerance) {
		spacingTolerance = minSpacing;
		sideTolerance = sizeTolerance;
	}

	@Override
	public boolean areSimilar(Square s1, Square s2) {
		Point2D.Double center1 = s1.getCenter();
		Point2D.Double center2 = s2.getCenter();

		// cast as ints so that the center is properly represented
		int x1 = (int) center1.getX();
		int y1 = (int) center1.getY();

		int x2 = (int) center2.getX();
		int y2 = (int) center2.getY();

		double spacing = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

		if (spacing <= spacingTolerance && Math.abs(s1.getSideLength() - s2.getSideLength()) <= sideTolerance) {
			// both tolerances are met, similar squares
			return true;
		}

		return false;
	}

	@Override
	public Square chooseBest(List<Square> shapes, VoteQuery vq) {
		Square bestSquare = shapes.get(0);
		int highestVote = vq.getVotes(bestSquare.getX(), bestSquare.getY(), (bestSquare.getSideLength() - 1) / 2);

		for (int i = 1; i < shapes.size(); i++) {
			Square temp = shapes.get(i);
			int tempVote = vq.getVotes(bestSquare.getX(), bestSquare.getY(), (bestSquare.getSideLength() - 1) / 2);
			if (tempVote > highestVote) {
				// current square has more votes than the highest
				highestVote = tempVote;
				bestSquare = temp;
			}
		}
		return bestSquare;
	}

}
