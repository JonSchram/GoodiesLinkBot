package goodieslink;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import goodieslink.ui.ImagePreview;

/**
 * Utility class containing several common functions relating to working with
 * OpenCV objects
 * 
 * @author Jonathan Schram
 *
 */
public class ImageDataUtils {

	/**
	 * Method to allow an OpenCV image matrix to be converted to a
	 * {@link BufferedImage}.
	 * <p>
	 * Adapted from answer by stack overflow user
	 * <a href="http://stackoverflow.com/users/1297062/dannyxyz22">http://
	 * stackoverflow.com/users/1297062/dannyxyz22</a>
	 * </p>
	 * question URL at: <a href=
	 * "http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui">
	 * http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui
	 * </a>
	 * 
	 * @param m
	 *            Matrix to convert
	 * @return Matrix data in a buffered image
	 */
	public static BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		byte[] b = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		m.get(0, 0, b);
		return image;
	}

	/**
	 * Debug method to display which pixels have been scanned. Accomplished by
	 * modifying scan code to overwrite pixels that are scanned.
	 * 
	 * @param pixels
	 *            Byte array of image pixel data to display in an ImagePreview
	 *            frame
	 */
	public void previewScan(byte[] pixels, int width, int height, int type) {
		BufferedImage bi = new BufferedImage(width, height, type);
		byte[] rasterData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, rasterData, 0, pixels.length);

		ImagePreview ip = new ImagePreview(bi);
		ip.show();
	}

}
