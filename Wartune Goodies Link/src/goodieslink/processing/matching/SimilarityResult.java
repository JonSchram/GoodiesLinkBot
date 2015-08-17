package goodieslink.processing.matching;

import java.awt.Point;

public class SimilarityResult {
	private Point source;
	private Point dest;
	private int sourceValue;
	private double similarity;

	public SimilarityResult() {
		similarity = -1;
	}

	public SimilarityResult(Point source, Point dest, int sourceValue) {
		this();
		this.source = source;
		this.dest = dest;
		this.sourceValue = sourceValue;
	}

	public Point getDest() {
		return dest;
	}

	public double getSimilarity() {
		return similarity;
	}

	public Point getSource() {
		return source;
	}

	public int getSourceValue() {
		return sourceValue;
	}

	public void setDest(Point dest) {
		this.dest = dest;
	}

	public void setSimilarity(double value) {
		similarity = value;
	}

	public void setSource(Point source) {
		this.source = source;
	}

	public void setSourceValue(int sourceValue) {
		this.sourceValue = sourceValue;
	}

}
