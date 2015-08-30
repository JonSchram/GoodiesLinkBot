package goodieslink.logging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ProgressLogger {
	private File saveDirectory;
	private int count;

	public ProgressLogger() {
		count = 0;
	}

	/**
	 * Sets the output directory, and returns whether the operations succeeded.
	 * 
	 * @param newDirectory
	 *            File indicating which directory to save images to. If this is
	 *            a file or doesn't exist, this method will have no effect
	 * @return True if the directory was set, false otherwise
	 */
	public boolean setDirectory(File newDirectory) {
		if (newDirectory.isDirectory()) {
			saveDirectory = newDirectory;
			return true;
		}
		return false;
	}

	public void logImage(BufferedImage image) throws IOException {
		ImageIO.write(image, "png", new File(saveDirectory, "DebugImg_" + count + ".png"));
		count++;
	}

	public void logImage(BufferedImage image, String prefix) throws IOException {
		ImageIO.write(image, "png", new File(saveDirectory, prefix + ".png"));
	}
}
