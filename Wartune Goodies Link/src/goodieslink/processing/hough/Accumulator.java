package goodieslink.processing.hough;

import goodieslink.processing.Square;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Accumulator {
	private int[][][] bins;
	private int width, height, minRadius, maxRadius;

	public Accumulator(int width, int height, int minRadius, int maxRadius) {
		this.width = width;
		this.height = height;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		bins = new int[width][height][maxRadius - minRadius + 1];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMinRadius() {
		return minRadius;
	}

	public int getMaxRadius() {
		return maxRadius;
	}

	public void vote(int x, int y, int size) {
		if (size >= minRadius && size <= maxRadius) {
			bins[x][y][size - minRadius]++;
		}
	}

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
						peaks.add(new Square(w - radius, h - radius,
								1 + 2 * radius));
					}
				}
			}
		}
		return peaks;
	}

	public List<Square> getPeaks(int threshold) {
		ArrayList<Square> peaks = new ArrayList<Square>();
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				for (int r = 0; r < maxRadius - minRadius + 1; r++) {
					if (bins[w][h][r] >= threshold) {
						// for now accept all peaks and don't attempt to
						// consolidate nearby ones
						int radius = r + minRadius;
						peaks.add(new Square(w - radius, h - radius,
								1 + 2 * radius));
					}
				}
			}
		}
		return peaks;
	}

	public List<Square> consolidate(List<Square> peaks,
			PeakFilter<Square> filter) {
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
	 * @author jonathan
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
