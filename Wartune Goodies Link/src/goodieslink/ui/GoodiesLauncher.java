package goodieslink.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import goodieslink.ImageDataUtils;

/**
 * Performs initialization and begins UI
 * 
 * @author Jonathan Schram
 *
 */
public class GoodiesLauncher {
	public static void main(String[] args) {
		// thanks to
		// https://github.com/PatternConsulting/opencv
		// for pointing out that using this library requires this extra call
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try {
			String imageFilename = "Goodies link edge detection/Screenshot from 2015-04-18 16:36:12.png";
			BufferedImage bi = ImageIO.read(new File(imageFilename));

			// BufferedImage bi = ImageIO.read(new File(
			// "Goodies link edge detection/blue screenshot.png"));

			// CannyEdgeDetector edgeDetector = new CannyEdgeDetector();

			// long startCanny, endCanny;
			// edgeDetector.setSourceImage(bi);
			// edgeDetector.setGaussianKernelRadius(1f);
			// edgeDetector.setGaussianKernelWidth(4);
			// edgeDetector.setHighThreshold(10f);
			// edgeDetector.setLowThreshold(5f);

			// startCanny = System.nanoTime();
			// edgeDetector.process();
			// endCanny = System.nanoTime();
			// System.out.println("CannyEdgeDetector: " + (endCanny -
			// startCanny)
			// + " ns");
			// BufferedImage edgeImage = edgeDetector.getEdgesImage();
			// ImageIO.write(edgeImage, "PNG", new File(
			// "edge image CannyEdgeDetector.png"));

			// ImagePreview imageWindow = new ImagePreview(bi);
			// imageWindow.show();
			//
			// ImagePreview edgeWindow = new ImagePreview(edgeImage);
			// edgeWindow.show();

			Mat imageMat = Highgui.imread(imageFilename, Highgui.IMREAD_ANYCOLOR);

			Imgproc.blur(imageMat, imageMat, new Size(1, 1));
			Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGBA2GRAY);
			ImagePreview ip = new ImagePreview(ImageDataUtils.toBufferedImage(imageMat));

			ip.show();
			long startOpenCV, endOpenCV;
			startOpenCV = System.nanoTime();
			Imgproc.Canny(imageMat, imageMat, 50, 150, 3, false);
			endOpenCV = System.nanoTime();
			System.out.println("OpenCV : " + (endOpenCV - startOpenCV) / 1000000. + " ms");
			Highgui.imwrite("edge image OpenCV.png", imageMat);
			BufferedImage openCvImage = ImageDataUtils.toBufferedImage(imageMat);
			ImagePreview cvWindow = new ImagePreview(openCvImage);

			// ImagePreview cvWindow = new ImagePreview(ImageIO.read(new File(
			// "edge image OpenCV.png")));
			cvWindow.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
