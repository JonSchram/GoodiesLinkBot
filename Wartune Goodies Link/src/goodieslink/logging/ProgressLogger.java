package goodieslink.logging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.imageio.ImageIO;

public class ProgressLogger {
	private File saveDirectory;
	private File parentDirectory;
	private int count;
	private boolean logging;

	public ProgressLogger() {
		count = 0;
		logging = false;
	}

	public void setLogging(boolean log) {
		logging = log;
	}

	public boolean isLogging() {
		return logging;
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
			LocalDateTime ldt = LocalDateTime.now();
			String subdirName = ldt.toString();
			subdirName = subdirName.replaceAll("[:.]", "-");
			System.out.println("Saving in: " + subdirName);
			parentDirectory = newDirectory;
			saveDirectory = new File(newDirectory, subdirName);
			return saveDirectory.mkdir();
		}
		return false;
	}

	public String getParentDirectoryName() {
		if (parentDirectory != null) {
			return parentDirectory.getName();
		} else {
			return "";
		}
	}

	public String getDirectoryName() {
		if (saveDirectory != null) {
			return saveDirectory.getName();
		} else {
			return "";
		}
	}

	public void logImage(BufferedImage image) throws IOException {
		ImageIO.write(image, "png", new File(saveDirectory, "DebugImg_" + count + ".png"));
		count++;
	}

	public void logImage(BufferedImage image, String prefix) throws IOException {
		ImageIO.write(image, "png", new File(saveDirectory, prefix + ".png"));
	}
}
