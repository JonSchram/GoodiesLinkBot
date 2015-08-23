package goodieslink.ui.javafx;

import java.awt.AWTException;

import goodieslink.controller.GoodieAgent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GoodieStatusWindow {
	GoodieAgent agent;
	ScreenRegionSelect regionSelector;
	Label countRemainingLabel;

	public GoodieStatusWindow(ScreenRegionSelect regionSelector) {
		this.regionSelector = regionSelector;
	}

	public Stage create() throws AWTException {
		Stage s = new Stage();
		s.setAlwaysOnTop(true);
		// make all windows (including the region selector) close when this
		// window exits
		s.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
			}
		});

		GridPane gp = new GridPane();
		gp.setVgap(3);
		gp.setPadding(new Insets(5));
		Scene scene = new Scene(gp);

		Label instructionLabel = new Label("Resize the red rectangle around the game board");
		instructionLabel.setWrapText(true);
		gp.add(instructionLabel, 1, 1, 2, 1);

		Label instructionLabel2 = new Label(
				"Then click this start button and the mouse will automatically click matching squares.");
		instructionLabel2.setWrapText(true);
		gp.add(instructionLabel2, 1, 2, 2, 1);

		Label instructionLabel3 = new Label(
				"Please don't have your web browser on full screen mode because depending on your platform the rectangle will not stay on top of the browser.");
		instructionLabel3.setWrapText(true);
		gp.add(instructionLabel3, 1, 3, 2, 1);

		Button startButton = new Button("Start");
		startButton.setOnAction(new StartButtonHandler());
		gp.add(startButton, 1, 4, 1, 1);

		countRemainingLabel = new Label();
		gp.add(countRemainingLabel, 2, 4, 1, 1);

		s.setScene(scene);

		agent = new GoodieAgent(0.85, 19, 23, 20, 4, 10);

		return s;
	}

	private void setCountRemainingText(int count) {
		countRemainingLabel.setText("Squares remaining: " + count);
	}

	class StartButtonHandler implements EventHandler<ActionEvent> {
		private boolean started;

		public StartButtonHandler() {
			started = false;
		}

		@Override
		public void handle(ActionEvent event) {
			if (!started) {
				started = true;
				new Thread(new Runnable() {

					@Override
					public void run() {
						agent.setScreenRegion(regionSelector.getEnclosedRegion());
						agent.detectIcons();
						while (!agent.isDone()) {
							boolean success = agent.clickOnMatch();
							if (!success) {
								// was no match
								agent.captureScreen();
								agent.processScreen();
							} else {
								final int remaining = agent.countRemaining();
								// submit count remaining to status label
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										setCountRemainingText(remaining);
									}
								});
							}
						}
					}
				}).start();
			}
		}

	}
}
