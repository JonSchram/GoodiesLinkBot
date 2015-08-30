package goodieslink.logging;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import goodieslink.processing.Square;
import goodieslink.processing.pathfinding.GoodiePath;

public class ImageDecorator {
	private Stroke pathStroke;
	private Stroke squareStroke;

	public ImageDecorator(Stroke pathStroke, Stroke squareStroke) {
		this.pathStroke = pathStroke;
		this.squareStroke = squareStroke;
	}

	/**
	 * Adds squares and/or path illustrations to the given image. If the path or
	 * squares shouldn't be drawn, the parameter can be <code>null</code>
	 * 
	 * @param image
	 * @param squares
	 * @param path
	 */
	public void decorate(BufferedImage image, List<Square> squares, GoodiePath path) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		// draw border on squares
		if (squares != null) {
			g2.setStroke(squareStroke);
			for (Square s : squares) {
				g2.drawRect(s.getX(), s.getY(), s.getSideLength(), s.getSideLength());
			}
		}

		// draw found path
		if (path != null) {
			g2.setStroke(pathStroke);

			LinkedList<Point> pathPoints = new LinkedList<>();
			pathPoints.add(path.getStartPoint());
			Point corner1 = path.getCorner1();
			Point corner2 = path.getCorner2();
			if (corner1 != null) {
				pathPoints.add(corner1);
			}
			if (corner2 != null) {
				pathPoints.add(corner2);
			}
			pathPoints.add(path.getEndPoint());

			Iterator<Point> pointIterator = pathPoints.iterator();
			Point prevPoint = pointIterator.next();
			while (pointIterator.hasNext()) {
				Point currentPoint = pointIterator.next();
				g2.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
			}
		}
	}
}
