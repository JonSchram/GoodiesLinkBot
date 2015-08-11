package goodieslink.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

class ImagePanel extends JPanel {
	/**
		 * 
		 */
	private static final long serialVersionUID = 2841533117563999309L;
	private BufferedImage displayImage;

	public ImagePanel(BufferedImage image) {
		displayImage = image;
	}

	protected double getScaleFactor() {
		double s = 1;

		int width = getWidth();
		int height = getHeight();

		int imageWidth = displayImage.getWidth();
		int imageHeight = displayImage.getHeight();

		double widthRatio = (double) width / imageWidth;
		double heightRatio = (double) height / imageHeight;

		s = Math.min(widthRatio, heightRatio);

		return s;
	}

	public int getImageWidth() {
		return displayImage.getWidth();
	}

	public int getImageHeight() {
		return displayImage.getHeight();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		double scale = getScaleFactor();
		int imageWidth = (int) (scale * displayImage.getWidth());
		int imageHeight = (int) (scale * displayImage.getHeight());
		int x = (getWidth() - imageWidth) / 2;
		int y = (getHeight() - imageHeight) / 2;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.drawImage(displayImage, x, y, imageWidth, imageHeight, null);
	}
}