package goodieslink.processing.matching;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import goodieslink.processing.Square;
import goodieslink.ui.ImagePreview;

public class RegionMatcher {
	private BufferedImage image;
	private SimilarityMeasure measureAlgorithm;
	private int maxOffset;

	public RegionMatcher(BufferedImage source, SimilarityMeasure similarityAlgorithm, int pixelTolerance) {
		image = source;
		measureAlgorithm = similarityAlgorithm;
		maxOffset = pixelTolerance;
	}

	public void setImage(BufferedImage source) {
		this.image = source;
	}

	/**
	 * The lower the number, the closer the match. "0" is a perfect match
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public double similarity(Square s1, Square s2) {
		byte[] pixels = ((DataBufferByte) image.getData().getDataBuffer()).getData();
		int pixelSize = image.getColorModel().getNumComponents();

		// attempt to match the image with every possible horizontal and
		// vertical shift
		// initialize error to infinity, meaning no match
		double error = Double.POSITIVE_INFINITY;
		double newError;
		for (int x = -maxOffset; x <= maxOffset; x++) {
			for (int y = -maxOffset; y <= maxOffset; y++) {
				newError = similarity(s1, s2, x, y, pixelSize, pixels);
				if (newError < error) {
					error = newError;
				}
			}
		}

		return error;
	}

	private double similarity(Square s1, Square s2, int xOffset, int yOffset, int pixelSize, byte[] pixels) {
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

	@SuppressWarnings("unused")
	private void previewScan(byte[] pixels) {
		BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		byte[] rasterData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, rasterData, 0, pixels.length);

		ImagePreview ip = new ImagePreview(bi);
		ip.show();

	}
}
