package goodieslink.processing.matching;

public class DifferenceSquaredMeasure implements SimilarityMeasure {

	@Override
	public double similarity(byte r1, byte g1, byte b1, byte r2, byte g2,
			byte b2) {
		return Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2)
				+ Math.pow(b1 - b2, 2);
	}

}
