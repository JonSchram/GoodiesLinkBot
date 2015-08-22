package goodieslink.processing.matching;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import goodieslink.processing.Square;

/**
 * Scans regions of an image and determines how similar the regions are, using
 * any given {@link SimilarityMeasure}
 * 
 * @author Jonathan Schram
 *
 */
public class RegionMatcher {
	/**
	 * Image that will be used for comparisons
	 */
	private BufferedImage image;
	/**
	 * Algorithm to measure similarity between individual pixels in the region
	 */
	private SimilarityMeasure measureAlgorithm;
	/**
	 * Maximum pixel shift that will be used when matching regions
	 */
	private int maxOffset;

	/**
	 * Pixel data from image, stored as a class variable to speed up repeated
	 * calls to compute similarity
	 */
	byte[] pixels;

	/**
	 * Whether this matcher has an image associated with it
	 */
	boolean initialized;

	/**
	 * Constructs a RegionMatcher with the given source image, similarity
	 * algorithm and maximum pixel offset when matching squares
	 * 
	 * @param source
	 *            Source image
	 * @param similarityAlgorithm
	 *            Algorithm used to compare pixels
	 * @param pixelTolerance
	 *            Maximum number of pixels to shift squares in an attempt to
	 *            match regions
	 */
	public RegionMatcher(BufferedImage source, SimilarityMeasure similarityAlgorithm, int pixelTolerance) {
		image = source;
		measureAlgorithm = similarityAlgorithm;
		maxOffset = pixelTolerance;
		pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		initialized = true;
	}

	public RegionMatcher(SimilarityMeasure similarityAlgorithm, int pixelTolerance) {
		measureAlgorithm = similarityAlgorithm;
		maxOffset = pixelTolerance;
		initialized = false;
	}

	/**
	 * Changes source image
	 * 
	 * @param source
	 *            New image for region detection
	 */
	public void setImage(BufferedImage source) {
		// update image and remember to save updated pixel data
		this.image = source;
		pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		initialized = true;
	}

	/**
	 * Best score for the similarity between two regions, shifting square 1
	 * within the limit specified when the RegionMatcher was created. The lower
	 * the number, the closer the match. "0" is a perfect match.
	 * 
	 * @param s1
	 *            First region
	 * @param s2
	 *            Second region
	 * @return Number representing similarity between the two square regions
	 */
	public double similarity(Square s1, Square s2) {
		if (initialized) {
			// runs faster if the matcher doesn't get a new copy of
			// pixel data each call
			// byte[] pixels = ((DataBufferByte)
			// image.getData().getDataBuffer()).getData();
			int pixelSize = image.getColorModel().getNumComponents();

			// attempt to match the image with every possible horizontal and
			// vertical shift
			// initialize error to infinity, meaning no match
			double error = Double.POSITIVE_INFINITY;
			double newError;
			for (int x = -maxOffset; x <= maxOffset; x++) {
				for (int y = -maxOffset; y <= maxOffset; y++) {
					newError = similarity(s1, s2, x, y, pixelSize);
					if (newError < error) {
						error = newError;
					}
				}
			}
			return error;
		}
		throw new NullPointerException("No image to compute region similarity");

	}

	/**
	 * Tests similarity between pixels within two squares. Specifies offsets to
	 * shift square 1 such that small deviations relative to detected squares
	 * will still be matched
	 * 
	 * @param s1
	 *            First square
	 * @param s2
	 *            Second square
	 * @param xOffset
	 *            Offset in x direction from current position
	 * @param yOffset
	 *            Offset in y direction from current position
	 * @param pixelSize
	 *            Number of bytes per pixel, expected to be 3 or 4 for color
	 *            images
	 * @return Measure of the similarity of the square regions for these offset
	 *         values
	 */
	private double similarity(Square s1, Square s2, int xOffset, int yOffset, int pixelSize) {
		Rectangle s1Rect = new Rectangle(s1.getX() + xOffset, s1.getY() + yOffset, s1.getSideLength(),
				s1.getSideLength());
		Rectangle s2Rect = new Rectangle(s2.getX(), s2.getY(), s2.getSideLength(), s2.getSideLength());
		// make processing a little easier and ensure that the search rectangle
		// is entirely in the image
		Rectangle imageBounds = new Rectangle(0, 0, image.getWidth(), image.getHeight());
		Rectangle s1Clipped = s1Rect.intersection(imageBounds);
		Rectangle s2Clipped = s2Rect.intersection(imageBounds);
		// to normalize error, transform to error per pixel
		double area = Math.min(s1Clipped.getWidth() * s1Clipped.getHeight(),
				s2Clipped.getWidth() * s2Clipped.getHeight());

		// int offset = (int) (intersection.getX() + intersection.getY()
		// * image.getWidth())
		// * pixelSize;
		int s1Offset = (int) (s1Clipped.getX() + s1Clipped.getY() * image.getWidth()) * pixelSize;
		int s2Offset = (int) (s2Clipped.getX() + s2Clipped.getY() * image.getWidth()) * pixelSize;
		// int s1Offset = (int) (offset - pixelSize
		// * ((intersection.getX() - s1Clipped.getX()) + (intersection
		// .getY() - s1Clipped.getY()) * image.getWidth()));
		// int s2Offset = (int) (offset - pixelSize
		// * ((intersection.getX() - s2Clipped.getX()) + (intersection
		// .getY() - s2Clipped.getY()) * image.getWidth()));
		// precise amount to get to beginning of next rectangle
		int s1Increment = (int) (pixelSize * (image.getWidth() - 1 - s1Clipped.getWidth()));
		int s2Increment = (int) (pixelSize * (image.getWidth() - 1 - s2Clipped.getWidth()));
		s1Increment = s2Increment = Math.max(s1Increment, s2Increment);
		// int iterationIncrement = (int) (pixelSize * (image.getWidth() -
		// intersection
		// .getWidth()));

		// all the ugly computations are done, the loop is simpler
		double sumError = 0;
		for (int y = 0; y <= (int) Math.min(s1Clipped.getHeight(), s2Clipped.getHeight()); y++) {
			for (int x = 0; x <= (int) Math.min(s1Clipped.getWidth(), s2Clipped.getWidth()); x++) {
				byte r1, r2, g1, g2, b1, b2;
				if (pixelSize == 1) {
					r1 = g1 = b1 = pixels[s1Offset];
					r2 = g2 = b2 = pixels[s2Offset];
				} else if (pixelSize == 3) {
					r1 = pixels[s1Offset];
					r2 = pixels[s2Offset];

					g1 = pixels[s1Offset + 1];
					g2 = pixels[s2Offset + 1];

					b1 = pixels[s1Offset + 2];
					b2 = pixels[s2Offset + 2];
				} else if (pixelSize == 4) {
					// has alpha component
					// first byte is alpha
					r1 = pixels[s1Offset + 1];
					r2 = pixels[s2Offset + 1];

					g1 = pixels[s1Offset + 2];
					g2 = pixels[s2Offset + 2];

					b1 = pixels[s1Offset + 3];
					b2 = pixels[s2Offset + 3];
				} else {
					r1 = r2 = g1 = g2 = b1 = b2 = 0;
				}
				// divide by # of possible colors per channel to normalize error
				sumError += measureAlgorithm.similarity(r1, g1, b1, r2, g2, b2) / 256;
				s1Offset += pixelSize;
				s2Offset += pixelSize;
			}
			s1Offset += s1Increment;
			s2Offset += s2Increment;
		}
		return sumError / area;
	}
}
