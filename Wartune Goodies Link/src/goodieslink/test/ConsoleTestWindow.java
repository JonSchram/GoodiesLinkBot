package goodieslink.test;

import goodieslink.ui.javafx.console.DebugConsole;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ConsoleTestWindow extends Stage {
	public ConsoleTestWindow(DebugConsole console) {
		Button goButton = new Button("GO");
		goButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 100; i++) {
							console.println("TESTING: " + i);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				t.start();
			}
		});
		BorderPane bp = new BorderPane();
		bp.setCenter(goButton);
		Scene s = new Scene(bp);
		setScene(s);
	}

}
