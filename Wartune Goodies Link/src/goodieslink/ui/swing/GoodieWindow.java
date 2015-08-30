package goodieslink.ui.swing;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GoodieWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3639924285978298408L;

	enum GoodieDisplayMode {
		DEFAULT, LEGACY;
	}

	@SuppressWarnings("unused")
	private GoodieDisplayMode currentMode;
	private TransparentPanel transparentPanel;
	private JPanel visiblePanel;

	public GoodieWindow() {
		this.setBackground(new Color(0, 0, 0, 0));
		setLayout(new GridLayout(1, 2));
		transparentPanel = new TransparentPanel();
		visiblePanel = new JPanel();
		visiblePanel.setBackground(Color.RED);

		this.add(transparentPanel);
		this.add(visiblePanel);
		this.setSize(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setMode(GoodieDisplayMode mode) {
		currentMode = mode;
	}
}
