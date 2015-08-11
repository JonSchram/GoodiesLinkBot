package goodieslink.processing.matching;

public interface SimilarityMeasure {
	public double similarity(byte r1, byte g1, byte b1, byte r2, byte g2,
			byte b2);
}
