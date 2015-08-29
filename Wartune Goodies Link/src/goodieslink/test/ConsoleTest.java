package goodieslink.test;

import goodieslink.ui.javafx.console.DebugConsole;
import javafx.application.Application;
import javafx.stage.Stage;

public class ConsoleTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		DebugConsole console = new DebugConsole();
		ConsoleTestWindow ctw = new ConsoleTestWindow(console);
		console.show();
		ctw.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
