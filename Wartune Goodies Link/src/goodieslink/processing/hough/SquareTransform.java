package goodieslink.processing.hough;

import goodieslink.processing.Square;
import goodieslink.ui.ImagePreview;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

/**
 * Processes an image and finds the locations of squares in the image. Squares
 * must not be rotated at all and because of the accumulator used, detected
 * squares always have an odd side length.
 * 
 * @author Jonathan
 *
 */
public class SquareTransform {
	/**
	 * Edge image that will be used to detect squares
	 */
	private BufferedImage src;
	// private List<Rectangle> boxes;
	/**
	 * Minimum radius of squares to detect
	 */
	private int minRadius;
	/**
	 * Maximum radius of squares to detect
	 */
	private int maxRadius;
	/**
	 * Accumulator to store votes for square sizes
	 */
	private Accumulator acc;

	/**
	 * Creates a SquareTransform object with specified edge image and radius
	 * constraints.
	 * 
	 * @param src
	 *            Black and white edge image (Best is produced with Canny edge
	 *            detector) to use to scan for squares.
	 * @param minRadius
	 *            Minimum radius of squares to be detected
	 * @param maxRadius
	 *            Maximum radius of squares to be detected
	 */
	public SquareTransform(BufferedImage src, int minRadius, int maxRadius) {
		this.src = src;
		// this.boxes = new ArrayList<Rectangle>();
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
	}

	/**
	 * Scan through the entire image for each valid square dimension
	 */
	public void process() {
		// int[] pixels = src.getRaster().getPixels(0, 0, src.getWidth(),
		// src.getHeight(), (int[]) null);

		byte[] pixels = ((DataBufferByte) src.getData().getDataBuffer()).getData();
		int pixelSize = src.getColorModel().getNumComponents();

		int width = src.getWidth();
		int height = src.getHeight();

		acc = new Accumulator(width, height, minRadius, maxRadius);

		int off = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				scanVicinity(pixels, off, pixelSize, x, y);
				off += pixelSize;
			}
		}

	}

	/**
	 * Detect edges lying around the given center point of a square, casting a
	 * vote for each edge pixel detected
	 * 
	 * @param pixels
	 *            Pixel data of edge image
	 * @param offset
	 *            Array index to center of square in pixel data
	 * @param pixelSize
	 *            Number of bytes per pixel, should be 1 for a black and white
	 *            image.
	 * @param pointX
	 *            X coordinate of center of square
	 * @param pointY
	 *            Y coordinate of center of square
	 */
	private void scanVicinity(byte[] pixels, int offset, int pixelSize, int pointX, int pointY) {
		int upperLeft = offset - pixelSize * (maxRadius + maxRadius * src.getWidth());
		// scan top
		int scan = upperLeft;
		int minX = Math.max(0, pointX - maxRadius);
		int maxX = Math.min(src.getWidth() - 1, pointX + maxRadius);
		// int maxX = pointX + maxRadius;
		int minY = Math.max(0, pointY - maxRadius);
		// int maxY = Math.min(src.getHeight(), pointY - minRadius);
		int maxY = pointY - minRadius;
		int iterationIncrement = pixelSize * (src.getWidth() - (maxX - minX + 1));

		// adjust scan position such that it is inside the image
		scan += pixelSize * (maxRadius - pointY + minY) * src.getWidth();
		scan += pixelSize * (maxRadius - pointX + minX);

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				if (pixels[scan] == -1) {
					// edge pixel counts in both x and y direction
					// as a consequence, corners will count twice (which may be
					// a good thing)
					acc.vote(pointX, pointY, pointY - y);
					acc.vote(pointX, pointY, Math.abs(pointX - x));
				}
				scan += pixelSize;
			}
			scan += iterationIncrement;
		}

		// for (int scanOff = Math.max(0, upperLeft); scanOff < upperLeft + 2
		// * maxRadius + maxRadius; scanOff += pixelSize) {}

		// scan left + right sides
		int minLeftX = Math.max(0, pointX - maxRadius);
		int maxLeftX = pointX - minRadius;
		int minRightX = pointX + minRadius;
		int maxRightX = Math.min(src.getWidth() - 1, pointX + maxRadius);
		int minMidY = Math.max(0, pointY - minRadius + 1);
		int maxMidY = Math.min(src.getHeight() - 1, pointY + minRadius - 1);
		int middleSkip = Math.min(minRightX, maxRightX) - Math.max(minLeftX, maxLeftX) - 1
				+ (maxLeftX < minLeftX ? 1 : 0);
		int increment = iterationIncrement;
		increment = pixelSize * (src.getWidth() - (maxRightX + (maxRightX < minRightX ? -1 : 0) - minLeftX + 1));
		for (int y = minMidY; y <= maxMidY; y++) {
			for (int x = minLeftX; x <= maxLeftX; x++) {
				if (pixels[scan] == -1) {
					acc.vote(pointX, pointY, Math.abs(pointY - y));
					acc.vote(pointX, pointY, pointX - x);
				}
				scan += pixelSize;
			}
			scan += middleSkip;
			for (int x = minRightX; x <= maxRightX; x++) {
				if (pixels[scan] == -1) {
					acc.vote(pointX, pointY, Math.abs(pointY - y));
					acc.vote(pointX, pointY, -pointX + x);
				}
				scan += pixelSize;
			}
			scan += increment;
		}

		int minBotY = pointY + minRadius;
		// int maxY = Math.min(src.getHeight(), pointY - minRadius);
		int maxBotY = Math.min(src.getHeight() - 1, pointY + maxRadius);
		// scan bottom
		for (int y = minBotY; y <= maxBotY; y++) {
			for (int x = minX; x <= maxX; x++) {
				if (pixels[scan] == -1) {
					// edge pixel counts in both x and y direction
					// as a consequence, corners will count twice (which may be
					// a good thing)
					acc.vote(pointX, pointY, -pointY + y);
					acc.vote(pointX, pointY, Math.abs(pointX - x));
				}
				scan += pixelSize;
			}
			scan += iterationIncrement;
		}

	}

	/**
	 * Returns squares that meet a default border proportion threshold of 0.9
	 * 
	 * @return List of boxes that meet the border threshold
	 */
	public List<Square> getBoxes() {
		return getBoxes(0.9);
	}

	/**
	 * Returns a list of squares that have the given proportion of border pixels
	 * present
	 * 
	 * @param thresholdProportion
	 *            Proportion of edge pixels that must be present
	 * @return List of squares that meet the threshold
	 */
	public List<Square> getBoxes(double thresholdProportion) {
		return acc.getPeaks(thresholdProportion);
	}

	/**
	 * Get a list of squares that have the given proportion of border pixels
	 * present, and removes duplicate squares using the given {@link PeakFilter}
	 * .
	 * 
	 * @param thresholdProportion
	 *            Proportion of edge pixels that must be present
	 * @param filter
	 *            PeakFilter that will be used to determine how to resolve
	 *            duplicate squares
	 * @return A list of squares that both meet the threshold and are not
	 *         similar to any other square in the list, according to the
	 *         PeakFilter
	 */
	public List<Square> getBoxes(double thresholdProportion, PeakFilter<Square> filter) {
		return acc.consolidate(acc.getPeaks(thresholdProportion), filter);
	}

	/**
	 * debug method to view the pixels that were scanned, which was accomplished
	 * by modifying the scan procedure to change the pixel data
	 * 
	 * @param pixels
	 */
	@SuppressWarnings("unused")
	private void previewScan(byte[] pixels) {
		BufferedImage bi = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte[] rasterData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, rasterData, 0, pixels.length);

		ImagePreview ip = new ImagePreview(bi);
		ip.show();

	}

}
