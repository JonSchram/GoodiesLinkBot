package goodieslink.ui;

import goodieslink.processing.Square;
import goodieslink.processing.hough.GridFilter;
import goodieslink.processing.hough.SquareTransform;
import goodieslink.processing.matching.DifferenceSquaredMeasure;
import goodieslink.processing.matching.RegionMatcher;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String[] args) {
		// String imageFilename =
		// "Goodies link edge detection/Screenshot from 2015-04-18 16:36:12.png";
		// String imageFilename = "edge image OpenCV.png";
		String imageFilename = "cropped OpenCV image.png";
		BufferedImage inputImage;
		try {
			inputImage = ImageIO.read(new File(imageFilename));
			SquareTransform st = new SquareTransform(inputImage, 19, 23);
			st.process();
			List<Square> squares = st.getBoxes(.85, new GridFilter(20, 7));
			// for (Square s : squares) {
			// System.out.println(s);
			// }
			SquareOverlay so = new SquareOverlay(inputImage);

			so.addSquares(squares);
			so.show();

			RegionMatcher rm = new RegionMatcher(inputImage,
					new DifferenceSquaredMeasure(), 3);
			rm.similarity(new Square(32, 34, 41), new Square(32, 78, 41));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
