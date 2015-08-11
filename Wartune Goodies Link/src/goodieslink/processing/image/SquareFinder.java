package goodieslink.processing.image;

import goodieslink.processing.hough.SquareTransform;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jonathan
 * @deprecated by SquareTransform
 * @see SquareTransform
 */
public class SquareFinder {
	private BufferedImage src;
	private List<Rectangle> boxes;

	public SquareFinder(BufferedImage src) {
		this.src = src;
		boxes = new ArrayList<Rectangle>();
	}

	public void process() {
		src.getRaster();
	}

	public List<Rectangle> getBoxes() {
		return boxes;
	}

}
