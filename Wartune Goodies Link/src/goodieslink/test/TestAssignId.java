package goodieslink.test;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import goodieslink.model.GameBoard;
import goodieslink.processing.Square;
import goodieslink.processing.hough.GridFilter;
import goodieslink.processing.hough.SquareTransform;
import goodieslink.ui.SquareOverlay;

public class TestAssignId {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	@Test
	public void test() {

		String imageFilename = "cropped sample image.png";
		BufferedImage inputImage;
		try {
			inputImage = ImageIO.read(new File(imageFilename));
			Mat imageMat = Highgui.imread(imageFilename, Highgui.IMREAD_ANYCOLOR);

			Imgproc.blur(imageMat, imageMat, new Size(1, 1));
			Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGBA2GRAY);
			Imgproc.Canny(imageMat, imageMat, 50, 150, 3, false);
			BufferedImage edgeImage = toBufferedImage(imageMat);

			// one square will be too big
			SquareTransform st = new SquareTransform(edgeImage, 19, 23);
			st.process();
			List<Square> squares = st.getBoxes(.85, new GridFilter(20, 7));

			GameBoard gb = new GameBoard(inputImage, 20, 4, 10);
			gb.setGridLocations(squares);

			Dimension d = gb.getSize();
			assertEquals(12, d.width);
			assertEquals(6, d.height);

			// obtained by running program and manually verifying
			int[][] expected = { { 1, 2, 3, 4, 5, 6, 1, 3, 7, 8, 9, 10 }, { 1, 11, 2, 12, 13, 4, 4, 10, 9, 14, 15, 16 },
					{ 8, 17, 16, 18, 6, 19, 16, 20, 2, 5, 7, 21 }, { 10, 15, 9, 16, 4, 1, 19, 9, 22, 23, 9, 14 },
					{ 4, 24, 2, 10, 18, 25, 4, 26, 11, 17, 25, 22 }, { 24, 9, 19, 21, 20, 2, 26, 13, 12, 2, 19, 23 } };

			for (int y = 0; y < d.height; y++) {
				for (int x = 0; x < d.width; x++) {
					assertEquals(expected[y][x], gb.getSquareId(y, x));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

}
