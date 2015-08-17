package goodieslink.test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import goodieslink.model.GameBoard;
import goodieslink.processing.Square;
import goodieslink.processing.hough.GridFilter;
import goodieslink.processing.hough.SquareTransform;
import goodieslink.processing.pathfinding.GoodiePath;
import goodieslink.processing.pathfinding.Pathfinder;
import goodieslink.ui.SquareOverlay;

public class Test {
	public static void main(String[] args) {
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// String imageFilename =
		// "Goodies link edge detection/Screenshot from 2015-04-18
		// 16:36:12.png";
		// String imageFilename = "edge image OpenCV.png";
		// String imageFilename = "cropped OpenCV image.png";
		String imageFilename = "cropped sample image.png";
		BufferedImage inputImage;
		try {
			inputImage = ImageIO.read(new File(imageFilename));
			long startRead, endRead;
			startRead = System.nanoTime();
			Mat imageMat = Highgui.imread(imageFilename, Highgui.IMREAD_ANYCOLOR);
			endRead = System.nanoTime();
			System.out.println("Image read: " + (endRead - startRead) / 1.0e6 + " ms");

			long startEdge, endEdge;
			startEdge = System.nanoTime();
			Imgproc.blur(imageMat, imageMat, new Size(1, 1));
			Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGBA2GRAY);
			Imgproc.Canny(imageMat, imageMat, 50, 150, 3, false);
			BufferedImage edgeImage = toBufferedImage(imageMat);
			endEdge = System.nanoTime();
			System.out.println("Edge detection: " + (endEdge - startEdge) / 1.0e6 + " ms");

			// square size is limited to not give too-big square
			// SquareTransform st = new SquareTransform(edgeImage, 19, 21);
			// one square will be too big
			long startSquare, endSquare;
			startSquare = System.nanoTime();
			SquareTransform st = new SquareTransform(edgeImage, 19, 23);
			st.process();
			endSquare = System.nanoTime();
			System.out.println("Square detection: " + (endSquare - startSquare) / 1.0e6 + " ms");
			List<Square> squares = st.getBoxes(.85, new GridFilter(20, 7));
			// for (Square s : squares) {
			// System.out.println(s);
			// }
			SquareOverlay so = new SquareOverlay(inputImage);

			so.addSquares(squares);
			so.show();

			long startSimilarity, endSimilarity;
			startSimilarity = System.nanoTime();
			GameBoard gb = new GameBoard(inputImage, 20, 4, 10);
			gb.setGridLocations(squares);
			endSimilarity = System.nanoTime();
			System.out.println("Square similarity: " + (endSimilarity - startSimilarity) / 1.0e6 + " ms");
			System.out.println("DONE");

			Pathfinder pathFinder = new Pathfinder(gb);
			GoodiePath path = pathFinder.findPath();
			System.out.println(path);

			// RegionMatcher rm = new RegionMatcher(inputImage,
			// new DifferenceSquaredMeasure(), 3);
			// rm.similarity(new Square(32, 34, 41), new Square(32, 78, 41));
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
