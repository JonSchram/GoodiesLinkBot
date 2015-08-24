package goodieslink.ui.javafx;

import java.awt.AWTException;
import java.awt.Rectangle;

import goodieslink.controller.GoodieAgent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GoodieStatusWindow {
	class StartButtonHandler implements EventHandler<ActionEvent> {
		private boolean started;

		public StartButtonHandler() {
			started = false;
		}

		@Override
		public void handle(ActionEvent event) {
			if (!started) {
				started = true;
				captureRegion = regionSelector.getEnclosedRegion();
				// close the selector so that a screenshot can be taken
				regionSelector.getStage().close();
			}
		}

	}

	GoodieAgent agent;
	ScreenRegionSelect regionSelector;
	Label countRemainingLabel;
	Stage stage;

	Rectangle captureRegion;

	boolean stopOperation;

	public GoodieStatusWindow() {
		stage = null;
		stopOperation = false;
		this.regionSelector = new ScreenRegionSelect();
		regionSelector.create();
		setCloseHandler();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				regionSelector.getStage().show();
				regionSelector.initRectSize();
			}
		});
	}

	public Stage create() throws AWTException {
		if (stage == null) {
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

			Label instructionLabel4 = new Label("Press ESC to stop clicking.");
			gp.add(instructionLabel4, 1, 4, 2, 1);

			Button startButton = new Button("Start");
			startButton.setOnAction(new StartButtonHandler());
			gp.add(startButton, 1, 5, 1, 1);

			countRemainingLabel = new Label();
			gp.add(countRemainingLabel, 2, 5, 1, 1);

			s.setScene(scene);

			agent = new GoodieAgent(0.85, 19, 23, 20, 4, 10);
			stage = s;
			scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ESCAPE) {
						stopOperation = true;
					}
				}
			});
		}
		return stage;
	}

	private void setCloseHandler() {
		regionSelector.getStage().setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (stage.isShowing()) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							stopOperation = false;
							try {
								// sleep because the screenshot is taken too
								// quickly
								// after the window is hidden
								Thread.sleep(15);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							// agent must make these calls in this order to
							// initialize
							agent.setScreenRegion(captureRegion);
							agent.captureScreen();
							agent.detectSquares();
							while (!agent.isDone() && !stopOperation) {
								boolean success = agent.clickOnMatch();
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										// try to see if the user wants to quit
										stage.requestFocus();
									}
								});
								if (!success) {
									// was no match, detect icons again
									agent.detectIcons();
								} else {

									final int remaining = agent.countRemaining();
									// submit count remaining to status
									// label
									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											setCountRemainingText(remaining);
										}
									});
								}
							}
							regionSelector = new ScreenRegionSelect();

							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									regionSelector.create();
									setCloseHandler();
									regionSelector.getStage().show();
									regionSelector.adjustRectSize(captureRegion);
								}
							});
						}

					}).start();
				}
			}
		});
	}

	private void setCountRemainingText(int count) {
		countRemainingLabel.setText("Squares remaining: " + count);
	}
}
