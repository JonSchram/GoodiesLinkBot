package goodieslink.logging;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import goodieslink.model.GameBoard;
import goodieslink.processing.Square;
import goodieslink.processing.pathfinding.GoodiePath;

public class ImageDecorator {
	private Stroke pathStroke;
	private Stroke squareStroke;
	private Color pathColor;
	private Color squareBorderColor;

	public ImageDecorator(Stroke pathStroke, Color pathColor, Stroke squareStroke, Color squareBorderColor) {
		this.pathStroke = pathStroke;
		this.squareStroke = squareStroke;
		this.pathColor = pathColor;
		this.squareBorderColor = squareBorderColor;
	}

	public Stroke getPathStroke() {
		return pathStroke;
	}

	public void setPathStroke(Stroke pathStroke) {
		this.pathStroke = pathStroke;
	}

	public Stroke getSquareStroke() {
		return squareStroke;
	}

	public void setSquareStroke(Stroke squareStroke) {
		this.squareStroke = squareStroke;
	}

	public Color getPathColor() {
		return pathColor;
	}

	public void setPathColor(Color pathColor) {
		this.pathColor = pathColor;
	}

	public void setPathColor(javafx.scene.paint.Color color) {
		this.pathColor = new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(),
				(float) color.getOpacity());
	}

	public Color getSquareBorderColor() {
		return squareBorderColor;
	}

	public void setSquareBorderColor(Color squareBorderColor) {
		this.squareBorderColor = squareBorderColor;
	}

	public void setSquareBorderColor(javafx.scene.paint.Color color) {
		this.squareBorderColor = new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(),
				(float) color.getOpacity());
	}

	/**
	 * Adds squares and/or path illustrations to the given image. If the path or
	 * squares shouldn't be drawn, the parameter can be <code>null</code>
	 * 
	 * @param image
	 * @param squares
	 * @param path
	 */
	public void decorate(BufferedImage image, Square[][] squares, GoodiePath path, GameBoard b) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		// draw border on squares
		if (squares != null) {
			drawSquares(g2, squares);
		}

		// draw found path
		if (path != null) {
			drawPath(g2, path, b);
		}
		g2.dispose();
	}

	public void decorate(BufferedImage image, List<Square> squares, GoodiePath path, GameBoard b) {
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		// draw border on squares
		if (squares != null) {
			drawSquares(g2, squares);
		}

		// draw found path
		if (path != null) {
			drawPath(g2, path, b);
		}
		g2.dispose();
	}

	private void drawSquares(Graphics2D g2, Square[][] squares) {
		g2.setStroke(squareStroke);
		g2.setColor(squareBorderColor);
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				if (squares[i][j] != null) {
					Square s = squares[i][j];
					g2.drawRect(s.getX(), s.getY(), s.getSideLength(), s.getSideLength());
				}
			}
		}
	}

	private void drawSquares(Graphics2D g2, List<Square> squares) {
		g2.setStroke(squareStroke);
		g2.setColor(squareBorderColor);
		for (Square s : squares) {
			g2.drawRect(s.getX(), s.getY(), s.getSideLength(), s.getSideLength());
		}
	}

	private void drawPath(Graphics2D g2, GoodiePath path, GameBoard b) {
		g2.setStroke(pathStroke);
		g2.setColor(pathColor);

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
		Point prevPoint = b.gridToPixel(pointIterator.next());
		while (pointIterator.hasNext()) {
			Point currentPoint = b.gridToPixel(pointIterator.next());
			g2.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
			prevPoint = currentPoint;
		}
	}
}
