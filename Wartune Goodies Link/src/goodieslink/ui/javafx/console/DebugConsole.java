package goodieslink.ui.javafx.console;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DebugConsole extends Stage {
	private ConsoleTextArea consoleText;
	private DebugStream debugStream;
	private boolean debugMode;

	public DebugConsole() {
		create();
		debugStream = new DebugStream();
		debugMode = false;
	}

	private void create() {
		// hide away messier stage creation code
		consoleText = new ConsoleTextArea();
		this.setTitle("Debugging Console");
		BorderPane bp = new BorderPane();
		bp.setCenter(consoleText);
		Scene s = new Scene(bp);
		setScene(s);
	}

	public void setDebugMode(boolean debug) {
		debugMode = debug;
	}

	public boolean getDebugMode() {
		return debugMode;
	}

	public DebugStream getDebugStream() {
		return debugStream;
	}

	public void println(String text) {
		consoleText.println(text);
	}

	public class DebugStream {

		public DebugStream() {
		}

		public void sendText(String text) {
			if (debugMode) {
				consoleText.println(text);
			}
		}

	}

}
