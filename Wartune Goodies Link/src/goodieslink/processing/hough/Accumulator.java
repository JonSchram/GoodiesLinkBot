package goodieslink.processing.hough;

import goodieslink.processing.Square;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores an array of votes for squares of various lengths during the Square
 * Hough transform. When the voting is compete the array can be queried for the
 * largest values, which indicate the dimensions of the most detectable squares.
 * <br>
 * This accumulator is written to store a center position and a radius with int
 * precision, which has the side effect of only producing squares with an odd
 * side length.
 * 
 * @author Jonathan Schram
 *
 */
public class Accumulator {
	/**
	 * 3D array to represent votes for a specific width, height, and side length
	 */
	private int[][][] bins;
	/**
	 * Parameter for <code>bins[][][]</code> dimensions
	 */
	private int width, height, minRadius, maxRadius;

	/**
	 * Creates an accumulator with given parameters for width, height, and valid
	 * radius
	 * 
	 * @param width
	 *            Width of image that will be voted on
	 * @param height
	 *            Height of image that will be voted on
	 * @param minRadius
	 *            Minimum radius of a detected square
	 * @param maxRadius
	 *            Maximum radius of a detected square
	 */
	public Accumulator(int width, int height, int minRadius, int maxRadius) {
		this.width = width;
		this.height = height;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		bins = new int[width][height][maxRadius - minRadius + 1];
	}

	/**
	 * Gets width of accumulator
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets height of accumulator
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets minimum radius of squares that can be detected
	 * 
	 * @return
	 */
	public int getMinRadius() {
		return minRadius;
	}

	/**
	 * Gets maximum radius of squares that can be detected
	 * 
	 * @return
	 */
	public int getMaxRadius() {
		return maxRadius;
	}

	/**
	 * Cast a vote for a square with specific dimensions, indicates that a pixel
	 * supports the existence of a square with these dimensions
	 * 
	 * @param x
	 * @param y
	 * @param size
	 */
	public void vote(int x, int y, int size) {
		if (size >= minRadius && size <= maxRadius) {
			bins[x][y][size - minRadius]++;
		}
	}

	/**
	 * Gets a list of squares that have at least a certain proportion of the
	 * perimeter present.
	 * 
	 * @param borderProportion
	 *            Proportion of square's perimeter that must be present for it
	 *            to be considered a peak in the voting bin
	 * @return A list of squares that have enough of the border present to be
	 *         detected.
	 */
	public List<Square> getPeaks(double borderProportion) {
		ArrayList<Square> peaks = new ArrayList<Square>();
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				for (int r = 0; r < maxRadius - minRadius + 1; r++) {
					// calculate # of pixels in the border
					int borderPixels = 8 * (r + minRadius);
					if (bins[w][h][r] / (double) borderPixels >= borderProportion) {
						// for now accept all peaks and don't attempt to
						// consolidate nearby ones
						int radius = r + minRadius;
						peaks.add(new Square(w - radius, h - radius, 1 + 2 * radius));
					}
				}
			}
		}
		return peaks;
	}

	/**
	 * Returns a list of squares with at least a certain number of pixels
	 * present on the border. Not recommended because larger squares will have
	 * larger perimeters, which will allow more pixels to vote for the square
	 * while it may not actually be a square, while smaller squares will need to
	 * have nearly all of the border present to be detected
	 * 
	 * @param threshold
	 *            Number of votes a square needs to be detected
	 * @return A list of squares with at least the minimum number of votes
	 */
	public List<Square> getPeaks(int threshold) {
		ArrayList<Square> peaks = new ArrayList<Square>();
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				for (int r = 0; r < maxRadius - minRadius + 1; r++) {
					if (bins[w][h][r] >= threshold) {
						// for now accept all peaks and don't attempt to
						// consolidate nearby ones
						int radius = r + minRadius;
						peaks.add(new Square(w - radius, h - radius, 1 + 2 * radius));
					}
				}
			}
		}
		return peaks;
	}

	/**
	 * Applies the {@link PeakFilter} to the list of squares to eliminate
	 * squares that are too similar in dimensions or location.
	 * 
	 * @param peaks
	 *            List of squares representing peaks in the accumulator.
	 * @param filter
	 *            Filtering algorithm which will be used to determine which
	 *            squares are too similar
	 * @return
	 */
	public List<Square> consolidate(List<Square> peaks, PeakFilter<Square> filter) {
		ArrayList<Square> result = new ArrayList<>();
		List<Square> peaksCopy = new ArrayList<Square>(peaks);

		ArrayList<Square> similarSquares = new ArrayList<>();
		Iterator<Square> iter;
		VoteQuery queryObject = new AccumulatorVoteQuery();
		while (!peaksCopy.isEmpty()) {
			Square temp = peaksCopy.remove(0);
			similarSquares.clear();
			similarSquares.add(temp);
			iter = peaksCopy.iterator();
			while (iter.hasNext()) {
				Square temp2 = iter.next();
				if (filter.areSimilar(temp, temp2)) {
					iter.remove();
					similarSquares.add(temp2);
				}
			}
			result.add(filter.chooseBest(similarSquares, queryObject));
		}

		return result;
	}

	/**
	 * Makes vote counts in accumulator available to other classes, specifically
	 * for use in a PeakFilter
	 * 
	 * @author Jonathan Schram
	 *
	 */
	public class AccumulatorVoteQuery implements VoteQuery {
		public int getVotes(int x, int y, int radius) {
			if (radius >= minRadius && radius <= maxRadius) {
				return bins[x][y][radius - minRadius];
			}
			return -1;
		}

		@Override
		public int getVotes(int... args) {
			if (args.length == 3) {
				return getVotes(args[0], args[1], args[2]);
			}
			return -1;
		}
	}

}
