package goodieslink.processing.hough;

import goodieslink.processing.Square;
import goodieslink.ui.ImagePreview;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

public class SquareTransform {
	private BufferedImage src;
	// private List<Rectangle> boxes;
	private int minRadius;
	private int maxRadius;
	private Accumulator acc;

	public SquareTransform(BufferedImage src, int minRadius, int maxRadius) {
		this.src = src;
		// this.boxes = new ArrayList<Rectangle>();
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
	}

	public void process() {
		// int[] pixels = src.getRaster().getPixels(0, 0, src.getWidth(),
		// src.getHeight(), (int[]) null);

		byte[] pixels = ((DataBufferByte) src.getData().getDataBuffer())
				.getData();
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

	private void scanVicinity(byte[] pixels, int offset, int pixelSize,
			int pointX, int pointY) {
		int upperLeft = offset - pixelSize
				* (maxRadius + maxRadius * src.getWidth());
		// scan top
		int scan = upperLeft;
		int minX = Math.max(0, pointX - maxRadius);
		int maxX = Math.min(src.getWidth() - 1, pointX + maxRadius);
		// int maxX = pointX + maxRadius;
		int minY = Math.max(0, pointY - maxRadius);
		// int maxY = Math.min(src.getHeight(), pointY - minRadius);
		int maxY = pointY - minRadius;
		int iterationIncrement = pixelSize
				* (src.getWidth() - (maxX - minX + 1));

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
		int middleSkip = Math.min(minRightX, maxRightX)
				- Math.max(minLeftX, maxLeftX) - 1
				+ (maxLeftX < minLeftX ? 1 : 0);
		int increment = iterationIncrement;
		increment = pixelSize
				* (src.getWidth() - (maxRightX
						+ (maxRightX < minRightX ? -1 : 0) - minLeftX + 1));
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

	public List<Square> getBoxes() {
		return getBoxes(0.9);
	}

	public List<Square> getBoxes(double thresholdProportion) {
		return acc.getPeaks(thresholdProportion);
	}

	public List<Square> getBoxes(double thresholdProportion,
			PeakFilter<Square> filter) {
		return acc.consolidate(acc.getPeaks(thresholdProportion), filter);
	}

	@SuppressWarnings("unused")
	private void previewScan(byte[] pixels) {
		BufferedImage bi = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);
		byte[] rasterData = ((DataBufferByte) bi.getRaster().getDataBuffer())
				.getData();
		System.arraycopy(pixels, 0, rasterData, 0, pixels.length);

		ImagePreview ip = new ImagePreview(bi);
		ip.show();

	}

}
