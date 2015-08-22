package goodieslink.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;

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

		// JFrame.setDefaultLookAndFeelDecorated(false);
		JFrame.setDefaultLookAndFeelDecorated(true);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		boolean isUniformTranslucencySupported = gd
				.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT);

		if (isUniformTranslucencySupported) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					GoodieWindow goodieWindow = new GoodieWindow();
					goodieWindow.setVisible(true);
				}
			});
		}
	}

}
