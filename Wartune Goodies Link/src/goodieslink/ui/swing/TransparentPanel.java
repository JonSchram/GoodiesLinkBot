package goodieslink.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8657966254239508199L;

	public TransparentPanel() {
		setOpaque(false);
	}

	// good explanation of transparent parts of windows from Java SE docs
	// https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html
	@Override
	protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D) g;
			Paint p = new Color(0, 0, 0, 0);
			g2.setPaint(p);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}
