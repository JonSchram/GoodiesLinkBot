package goodieslink.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.SystemUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import goodieslink.ImageDataUtils;
import goodieslink.model.GameBoard;
import goodieslink.processing.Square;
import goodieslink.processing.hough.GridFilter;
import goodieslink.processing.hough.SquareTransform;

/**
 * JUnit test case to confirm that game board icon IDs are correctly detected.
 * This has been written, with the help of GenerateBoardTest, to be independent
 * of the item ID assignment algorithm. This test will succeed as long as
 * identical regions of the image have the same item ID.
 * 
 * @author jonathan
 *
 */
public class TestAssignId {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// nu.pattern.OpenCV.loadShared();
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String libraryRootRelative = "lib/native";
		String libraryRootAbsolute = new File(libraryRootRelative).getAbsolutePath();
		int bits = Integer.parseInt(System.getProperty("sun.arch.data.model"));
		String os = System.getProperty("os.name");
		System.out.println(bits + ", " + os);
		System.out.println(SystemUtils.OS_ARCH);

		if (SystemUtils.IS_OS_LINUX) {
			if (bits == 64) {
				// only have x64 version for Linux
				System.out.println("Loading " + libraryRootAbsolute + "/linux64/libopencv_java2410.so");
				System.load(libraryRootAbsolute + "/linux64/libopencv_java2410.so");
			}
		} else if (SystemUtils.IS_OS_WINDOWS) {
			if (bits == 64) {
				System.out.println("Loading " + libraryRootAbsolute + "/windows64/opencv_java2410.dll");
				System.load(libraryRootAbsolute + "/windows64/opencv_java2410.dll");
			} else {
				System.out.println("Loading " + libraryRootAbsolute + "/windows32/opencv_java2410.dll");
				System.load(libraryRootAbsolute + "/windows32/opencv_java2410.dll");
			}
		} else if (SystemUtils.IS_OS_MAC) {
			// have no mac library
		}
		System.out.println("Done loading library");
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
			BufferedImage edgeImage = ImageDataUtils.toBufferedImage(imageMat);

			// one square will be too big
			SquareTransform st = new SquareTransform(edgeImage, 19, 23);
			st.process();
			List<Square> squares = st.getBoxes(.85, new GridFilter(20, 7));

			GameBoard gb = new GameBoard(inputImage, 20, 4, 10);
			gb.setGridLocations(squares);

			Dimension d = gb.getSize();
			assertEquals(12, d.width);
			assertEquals(6, d.height);

			// generated using GenerateBoardTest.java
			// will work as long as the matching tiles are in the same location
			// (since this is highly unlikely to happen by chance it only works
			// on this test image/images derived from it)
			assertEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 0));
			assertEquals(gb.getSquareId(1, 0), gb.getSquareId(0, 6));
			assertEquals(gb.getSquareId(0, 6), gb.getSquareId(3, 5));
			assertEquals(gb.getSquareId(0, 1), gb.getSquareId(1, 2));
			assertEquals(gb.getSquareId(1, 2), gb.getSquareId(2, 8));
			assertEquals(gb.getSquareId(2, 8), gb.getSquareId(4, 2));
			assertEquals(gb.getSquareId(4, 2), gb.getSquareId(5, 5));
			assertEquals(gb.getSquareId(5, 5), gb.getSquareId(5, 9));
			assertEquals(gb.getSquareId(0, 2), gb.getSquareId(0, 7));
			assertEquals(gb.getSquareId(4, 0), gb.getSquareId(1, 5));
			assertEquals(gb.getSquareId(1, 5), gb.getSquareId(0, 3));
			assertEquals(gb.getSquareId(0, 3), gb.getSquareId(1, 6));
			assertEquals(gb.getSquareId(1, 6), gb.getSquareId(3, 4));
			assertEquals(gb.getSquareId(3, 4), gb.getSquareId(4, 6));
			assertEquals(gb.getSquareId(0, 4), gb.getSquareId(2, 9));
			assertEquals(gb.getSquareId(2, 4), gb.getSquareId(0, 5));
			assertEquals(gb.getSquareId(0, 8), gb.getSquareId(2, 10));
			assertEquals(gb.getSquareId(2, 0), gb.getSquareId(0, 9));
			assertEquals(gb.getSquareId(1, 8), gb.getSquareId(0, 10));
			assertEquals(gb.getSquareId(0, 10), gb.getSquareId(3, 7));
			assertEquals(gb.getSquareId(3, 7), gb.getSquareId(3, 2));
			assertEquals(gb.getSquareId(3, 2), gb.getSquareId(3, 10));
			assertEquals(gb.getSquareId(3, 10), gb.getSquareId(5, 1));
			assertEquals(gb.getSquareId(0, 11), gb.getSquareId(3, 0));
			assertEquals(gb.getSquareId(3, 0), gb.getSquareId(4, 3));
			assertEquals(gb.getSquareId(4, 3), gb.getSquareId(1, 7));
			assertEquals(gb.getSquareId(1, 1), gb.getSquareId(4, 8));
			assertEquals(gb.getSquareId(1, 3), gb.getSquareId(5, 8));
			assertEquals(gb.getSquareId(1, 4), gb.getSquareId(5, 7));
			assertEquals(gb.getSquareId(1, 9), gb.getSquareId(3, 11));
			assertEquals(gb.getSquareId(1, 10), gb.getSquareId(3, 1));
			assertEquals(gb.getSquareId(2, 2), gb.getSquareId(3, 3));
			assertEquals(gb.getSquareId(3, 3), gb.getSquareId(1, 11));
			assertEquals(gb.getSquareId(1, 11), gb.getSquareId(2, 6));
			assertEquals(gb.getSquareId(2, 1), gb.getSquareId(4, 9));
			assertEquals(gb.getSquareId(4, 4), gb.getSquareId(2, 3));
			assertEquals(gb.getSquareId(3, 6), gb.getSquareId(2, 5));
			assertEquals(gb.getSquareId(2, 5), gb.getSquareId(5, 10));
			assertEquals(gb.getSquareId(5, 10), gb.getSquareId(5, 2));
			assertEquals(gb.getSquareId(2, 7), gb.getSquareId(5, 4));
			assertEquals(gb.getSquareId(5, 3), gb.getSquareId(2, 11));
			assertEquals(gb.getSquareId(4, 11), gb.getSquareId(3, 8));
			assertEquals(gb.getSquareId(3, 9), gb.getSquareId(5, 11));
			assertEquals(gb.getSquareId(4, 1), gb.getSquareId(5, 0));
			assertEquals(gb.getSquareId(4, 5), gb.getSquareId(4, 10));
			assertEquals(gb.getSquareId(5, 6), gb.getSquareId(4, 7));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(0, 1));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(0, 2));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(4, 0));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(0, 4));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(2, 4));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(0, 8));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(2, 0));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 8));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(0, 11));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 1));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 3));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 4));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 9));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(1, 10));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(2, 2));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(2, 1));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(4, 4));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(3, 6));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(2, 7));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(5, 3));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(4, 11));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(3, 9));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(4, 1));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(4, 5));
			assertNotEquals(gb.getSquareId(0, 0), gb.getSquareId(5, 6));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
