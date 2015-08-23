package goodieslink.ui.swing;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A simple window utilizing {@link ImagePanel} to assist in debugging.
 * 
 * @author jonathan
 *
 */
public class ImagePreview {
	private JFrame mainWindow;
	private ImagePanel imagePanel;

	public ImagePreview(BufferedImage image) {
		mainWindow = new JFrame();
		imagePanel = new ImagePanel(image);
		mainWindow.setLayout(new BorderLayout());
		mainWindow.add(imagePanel, BorderLayout.CENTER);
		mainWindow.setSize(400, 400);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
	}

	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ImagePreview.this.mainWindow.setVisible(true);
			}
		});
	}

}
