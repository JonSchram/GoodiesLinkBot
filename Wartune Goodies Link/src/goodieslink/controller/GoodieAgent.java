package goodieslink.controller;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import goodieslink.ImageDataUtils;
import goodieslink.model.GameBoard;
import goodieslink.processing.Square;
import goodieslink.processing.hough.GridFilter;
import goodieslink.processing.hough.SquareTransform;
import goodieslink.processing.pathfinding.GoodiePath;
import goodieslink.processing.pathfinding.Pathfinder;
import goodieslink.ui.javafx.console.DebugConsole.DebugStream;
import goodieslink.ui.swing.ImagePreview;

/**
 * 
 * Utilizes the {@link Robot} class to automatically move the mouse and click on
 * regions of the game board
 * 
 * @author Jonathan Schram
 *
 */
public class GoodieAgent {

	private int delayBetweenClicks = 100;
	private int delayUpDown = 100;
	private int delayMouseMove = 50;

	private BufferedImage image;

	private Robot goodieRobot;

	private Rectangle screenRegion;

	private GameBoard board;

	private Pathfinder matchDetector;

	private SquareTransform squareDetector;

	private DebugStream debugStream;
	private double squareDetectionThreshold;
	private int minSquareRadius;

	private int maxSquareRadius;

	/**
	 * Initializes a GoodieAgent to interact with the Goodies link game.
	 * 
	 * @param squareDetectionThreshold
	 *            Proportion of border that must be present in edge image for a
	 *            square to be detected
	 * @param minRadius
	 *            Minimum radius of squares to detect
	 * @param maxRadius
	 *            Maximum radius of squares to detect
	 * @param locationTolerance
	 *            Maximum difference in square location before squares are no
	 *            longer considered to be in the same row/column
	 * @param searchMargin
	 *            Maximum distance to shift squares when searching for a
	 *            matching icon
	 * @param similarityThreshold
	 *            Similarity score required for two icons to be considered
	 *            identical. Must be positive. Closer to 0 is a closer match.
	 * 
	 * @throws AWTException
	 *             If the security context prohibits creation of a java Robot
	 */
	public GoodieAgent(double squareDetectionThreshold, int minRadius, int maxRadius, int locationTolerance,
			int searchMargin, int similarityThreshold) throws AWTException {
		this.squareDetectionThreshold = squareDetectionThreshold;
		minSquareRadius = minRadius;
		maxSquareRadius = maxRadius;
		board = new GameBoard(locationTolerance, searchMargin, similarityThreshold);
		screenRegion = new Rectangle();
		goodieRobot = new Robot();
		matchDetector = new Pathfinder(board);
		debugStream = null;
	}

	/**
	 * Get screenshot and store in the GoodieAgent but don't pass to the game
	 * board. <br>
	 * Must be done this way because in order to detect squares an image must be
	 * obtained, and passing the image immediately to the game board will
	 * attempt to match squares. This separation of image assignments allows the
	 * screen to be captured, then squares detected, then icons.
	 * 
	 */
	public void captureScreen() {
		BufferedImage boardImage = goodieRobot.createScreenCapture(screenRegion);
		// effectively convert screen capture to another image type such that
		// the resulting image is a specific format
		// TYPE_3BYTE_BGR is stored as bytes
		BufferedImage convertedImage = new BufferedImage(boardImage.getWidth(), boardImage.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		convertedImage.getGraphics().drawImage(boardImage, 0, 0, null);
		// ImagePreview ip = new ImagePreview(convertedImage);
		// ip.show();
		this.image = convertedImage;
	}

	/**
	 * Attempts to click on a pair of matching icons. Returns whether this
	 * action succeeded
	 * 
	 * @return
	 */
	public boolean clickOnMatch() {
		GoodiePath foundPath = matchDetector.findPath();
		if (foundPath != null) {
			// there was a path found
			Point startPoint = foundPath.getStartPoint();
			Point endPoint = foundPath.getEndPoint();

			Point startScreen = board.gridToPixel(startPoint);
			Point endScreen = board.gridToPixel(endPoint);

			trySendText("Clicking path: " + foundPath);
			moveAndClick(startScreen);
			goodieRobot.delay(delayBetweenClicks);
			moveAndClick(endScreen);

			board.removeSpace(startPoint);
			board.removeSpace(endPoint);

			return true;
		}
		return false;
	}

	public boolean hasMatch() {
		GoodiePath foundPath = matchDetector.findPath();
		return foundPath != null;
	}

	public int countRemaining() {
		return board.getCountRemaining();
	}

	public void delayBetweenClicks() {
		goodieRobot.delay(delayBetweenClicks);
	}

	public void detectIcons() {
		captureScreen();
		trySendText("Goodie agent took screenshot");
		processScreen();
		trySendText("Goodie agent processed screenshot");
	}

	public void detectSquares() {
		// conversion of bufferedImage to OpenCV Mat is adapted from:
		// http://enfanote.blogspot.com/2013/06/converting-java-bufferedimage-to-opencv.html
		byte[] imageData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat imageMat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		imageMat.put(0, 0, imageData);

		// edge detection requires a blurred grayscale image
		Imgproc.blur(imageMat, imageMat, new Size(1, 1));
		Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGBA2GRAY);
		Imgproc.Canny(imageMat, imageMat, 50, 150, 3, false);
		BufferedImage edgeImage = ImageDataUtils.toBufferedImage(imageMat);

		squareDetector = new SquareTransform(edgeImage, minSquareRadius, maxSquareRadius);
		squareDetector.process();

		int averageRadius = (minSquareRadius + maxSquareRadius) / 2;
		List<Square> squares = squareDetector.getBoxes(squareDetectionThreshold,
				new GridFilter(averageRadius, averageRadius));

		board.setImageQuiet(image);
		board.setGridLocations(squares);
	}

	public int getDelayBetweenClicks() {
		return delayBetweenClicks;
	}

	public int getDelayMouseMove() {
		return delayMouseMove;
	}

	public int getDelayUpDown() {
		return delayUpDown;
	}

	public boolean isDone() {
		return board.isEmpty();
	}

	private void moveAndClick(Point imageLocation) {
		goodieRobot.mouseMove(screenRegion.x + imageLocation.x, screenRegion.y + imageLocation.y);
		goodieRobot.delay(delayMouseMove);
		goodieRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		goodieRobot.delay(delayUpDown);
		goodieRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	public void processScreen() {
		board.setImage(image);
	}

	public void setDebugStream(DebugStream stream) {
		debugStream = stream;
	}

	public void setDelayBetweenClicks(int delayBetweenClicks) {
		if (delayBetweenClicks >= 0) {
			this.delayBetweenClicks = delayBetweenClicks;
		}
	}

	public void setDelayMouseMove(int delayMouseMove) {
		if (delayMouseMove >= 0) {
			this.delayMouseMove = delayMouseMove;
		}
	}

	public void setDelayUpDown(int delayUpDown) {
		if (delayUpDown >= 0) {
			this.delayUpDown = delayUpDown;
		}
	}

	public void setScreenRegion(Rectangle region) {
		this.screenRegion = region;
	}

	private void trySendText(String text) {
		if (debugStream != null) {
			debugStream.sendText(text);
		}
	}
}
