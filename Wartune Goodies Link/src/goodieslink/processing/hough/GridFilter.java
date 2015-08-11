package goodieslink.processing.hough;

import goodieslink.processing.Square;

import java.awt.geom.Point2D;
import java.util.List;

public class GridFilter implements PeakFilter<Square> {
	private double spacingTolerance;
	private double sideTolerance;

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

		if (spacing <= spacingTolerance
				&& Math.abs(s1.getSideLength() - s2.getSideLength()) <= sideTolerance) {
			// both tolerances are met, similar squares
			return true;
		}

		return false;
	}

	@Override
	public Square chooseBest(List<Square> shapes, VoteQuery vq) {
		Square bestSquare = shapes.get(0);
		int highestVote = vq.getVotes(bestSquare.getX(), bestSquare.getY(),
				(bestSquare.getSideLength() - 1) / 2);

		for (int i = 1; i < shapes.size(); i++) {
			Square temp = shapes.get(i);
			int tempVote = vq.getVotes(bestSquare.getX(), bestSquare.getY(),
					(bestSquare.getSideLength() - 1) / 2);
			if (tempVote > highestVote) {
				// current square has more votes than the highest
				highestVote = tempVote;
				bestSquare = temp;
			}
		}
		return bestSquare;
	}

}
