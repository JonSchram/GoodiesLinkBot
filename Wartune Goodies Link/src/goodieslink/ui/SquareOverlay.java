package goodieslink.ui;

import goodieslink.processing.Square;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A simple window to assist in debugging, which allows the user to add a
 * background image and a list of shapes to display on top of that image.
 * 
 * @author Jonathan Schram
 *
 */
public class SquareOverlay {

	private JFrame mainWindow;
	private ShapePanel canvas;

	public SquareOverlay(BufferedImage image) {
		mainWindow = new JFrame();
		canvas = new ShapePanel(image);
		mainWindow.setLayout(new BorderLayout());
		mainWindow.add(canvas, BorderLayout.CENTER);
		mainWindow.setSize(400, 400);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
	}

	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SquareOverlay.this.mainWindow.setVisible(true);
			}
		});
	}

	public void addSquares(List<Square> squares) {
		canvas.addShapes(squares);
	}

}

/**
 * An extension of ImagePanel that allows shapes to be drawn on top of the
 * background image.
 * 
 * @author Jonathan Schram
 *
 */
class ShapePanel extends ImagePanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3593913588303979454L;
	private List<Shape> displayShapes;

	public ShapePanel(BufferedImage image) {
		super(image);
		displayShapes = new ArrayList<>();
	}

	public void addShapes(List<? extends Shape> shapes) {
		displayShapes.addAll(shapes);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.RED);

		double scale = getScaleFactor();
		int imageWidth = (int) (scale * getImageWidth());
		int imageHeight = (int) (scale * getImageHeight());
		int x = (getWidth() - imageWidth) / 2;
		int y = (getHeight() - imageHeight) / 2;

		AffineTransform initialTransform = g2.getTransform();
		g2.translate(x, y);
		g2.scale(scale, scale);

		for (Shape s : displayShapes) {
			g2.draw(s);
		}

		g2.setTransform(initialTransform);
	}

}