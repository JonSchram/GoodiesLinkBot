package goodieslink.processing.matching;

import java.awt.Point;

/**
 * Class to store region similarity to assist in returning results when
 * multithreading. Stores sufficient information to indicate which grid
 * locations are compared and which grid location should get assigned an ID
 * 
 * @author Jonathan Schram
 *
 */
public class SimilarityResult {
	/**
	 * Source location in grid
	 */
	private Point source;
	/**
	 * Destination location in grid
	 */
	private Point dest;
	/**
	 * Source icon ID
	 */
	private int sourceValue;
	/**
	 * Result obtained from similarity algorithm
	 */
	private double similarity;

	/**
	 * Constructs default SimilarityResult storage object
	 */
	public SimilarityResult() {
		similarity = -1;
	}

	/**
	 * Constructs SimilarityResult with specified parameters
	 * 
	 * @param source
	 *            Location in grid of source region
	 * @param dest
	 *            Location in grid of destination region
	 * @param sourceValue
	 */
	public SimilarityResult(Point source, Point dest, int sourceValue) {
		this();
		this.source = source;
		this.dest = dest;
		this.sourceValue = sourceValue;
	}

	/**
	 * Get destination grid location
	 * 
	 * @return
	 */
	public Point getDest() {
		return dest;
	}

	/**
	 * Get calculated similarity between the regions represented by the pair of
	 * grid coordinates
	 * 
	 * @return
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * Returns the source location in the grid
	 * 
	 * @return
	 */
	public Point getSource() {
		return source;
	}

	/**
	 * Returns the item ID of the source grid space
	 * 
	 * @return
	 */
	public int getSourceValue() {
		return sourceValue;
	}

	/**
	 * Sets the destination location in grid
	 * 
	 * @param dest
	 */
	public void setDest(Point dest) {
		this.dest = dest;
	}

	/**
	 * Sets the computed similarity value for the two grid regions
	 * 
	 * @param value
	 */
	public void setSimilarity(double value) {
		similarity = value;
	}

	/**
	 * Set the source grid location
	 * 
	 * @param source
	 */
	public void setSource(Point source) {
		this.source = source;
	}

	/**
	 * Set source ID
	 * 
	 * @param sourceValue
	 */
	public void setSourceValue(int sourceValue) {
		this.sourceValue = sourceValue;
	}

}
