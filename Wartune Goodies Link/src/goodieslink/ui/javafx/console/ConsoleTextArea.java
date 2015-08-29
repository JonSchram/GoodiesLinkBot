package goodieslink.ui.javafx.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ConsoleTextArea extends TextArea {

	private ConsolePrintStream stream;

	public ConsoleTextArea() {
		stream = new ConsolePrintStream(new ConsoleStream());
		this.setEditable(false);
	}

	public void println(String text) {
		stream.println(text);
	}

	public OutputStream getOutputStream() {
		return stream;
	}

	private class ConsoleStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ConsoleTextArea.this.appendText(String.valueOf((char) b));
				}
			});
		}
	}

	private class ConsolePrintStream extends PrintStream {

		public ConsolePrintStream(OutputStream out) {
			super(out);
		}

		public void println(String text) {
			ConsolePrintStream.super.println(text);
		};

	}
}
