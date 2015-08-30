package goodieslink.ui.javafx;

import java.awt.AWTException;
import java.awt.Rectangle;

import goodieslink.controller.GoodieAgent;
import goodieslink.ui.javafx.console.DebugConsole;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GoodieStatusWindow extends Stage {
	class StartButtonHandler implements EventHandler<ActionEvent> {
		// make volatile so hopefully the clicker thread will stop properly
		private volatile boolean started;

		public StartButtonHandler() {
			started = false;
		}

		@Override
		public void handle(ActionEvent event) {
			if (!started) {
				started = true;
				captureRegion = regionSelector.getEnclosedRegion();
				outputConsole.getDebugStream().sendText("Screen capturing region " + captureRegion);
				// close the selector so that a screenshot can be taken
				regionSelector.saveState();
				regionSelector.close();
				outputConsole.getDebugStream().sendText("Starting clicks");
				performClicks();
			}
		}

		private void performClicks() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					stopOperation = false;
					try {
						// sleep because the screenshot is taken too
						// quickly after the window is hidden
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// agent must make these calls in this order to
					// initialize
					agent.setScreenRegion(captureRegion);
					agent.captureScreen();
					outputConsole.getDebugStream().sendText("Took screenshot");
					agent.detectSquares();
					outputConsole.getDebugStream().sendText("Detected squares");
					while (!agent.isDone() && !stopOperation) {
						boolean success = agent.clickOnMatch();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// try to see if the user wants to quit
								requestFocus();
							}
						});
						if (!success) {
							// was no match, detect icons again
							outputConsole.getDebugStream()
									.sendText("Could not click on match, taking another screenshot");
							agent.detectIcons();
							outputConsole.getDebugStream().sendText("Attempting to find another match");

						} else {
							outputConsole.getDebugStream().sendText("Match found");
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
					started = false;
					outputConsole.getDebugStream().sendText("Finished clicks");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							regionSelector.show();
							// make window appear where it was before
							regionSelector.restoreState();
							// regionSelector.adjustRectSize(captureRegion);
						}
					});
				}

			}).start();
		}

	}

	GoodieAgent agent;
	ScreenRegionSelect regionSelector;
	DebugConsole outputConsole;
	Label countRemainingLabel;

	Rectangle captureRegion;

	boolean stopOperation;

	public GoodieStatusWindow() throws AWTException {
		stopOperation = false;
		create();
		regionSelector = new ScreenRegionSelect();
		outputConsole = new DebugConsole();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				regionSelector.show();
				regionSelector.initRectSize();
			}
		});
	}

	private void create() throws AWTException {
		this.setAlwaysOnTop(true);
		// make all windows (including the region selector) close when this
		// window exits
		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stopOperation = true;
				Platform.exit();
			}
		});

		setTitle("The Goodie Linker");

		GridPane gp = new GridPane();
		gp.setVgap(3);
		gp.setPadding(new Insets(5));
		Scene scene = new Scene(gp);

		agent = new GoodieAgent(0.85, 19, 23, 20, 4, 10);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					stopOperation = true;
				}
			}
		});

		Label instructionLabel = new Label("Resize the red rectangle around the game board");
		instructionLabel.setWrapText(true);
		gp.add(instructionLabel, 1, 1, 2, 1);

		Label instructionLabel2 = new Label(
				"Then click this start button and the mouse will automatically click matching squares.");
		instructionLabel2.setWrapText(true);
		gp.add(instructionLabel2, 1, 2, 2, 1);

		Label instructionLabel3 = new Label(
				"Please don't have your web browser on full screen mode because depending on your platform this window will not stay on top of the browser.");
		instructionLabel3.setWrapText(true);
		gp.add(instructionLabel3, 1, 3, 2, 1);

		Label instructionLabel4 = new Label("Press ESC to stop clicking.");
		gp.add(instructionLabel4, 1, 4, 2, 1);

		Button startButton = new Button("Start");
		startButton.setOnAction(new StartButtonHandler());
		gp.add(startButton, 1, 5, 1, 1);

		countRemainingLabel = new Label();
		gp.add(countRemainingLabel, 2, 5, 1, 1);

		CheckBox debugCheckBox = new CheckBox("Debug mode");
		debugCheckBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				outputConsole.setDebugMode(debugCheckBox.isSelected());
				if (debugCheckBox.isSelected()) {
					outputConsole.show();
				} else {
					outputConsole.hide();
				}
			}
		});
		gp.add(debugCheckBox, 1, 6, 2, 1);

		Label upDownDelayLabel = new Label("Delay between pressing and releasing mouse button (millisec.):");
		upDownDelayLabel.setWrapText(true);
		Spinner<Integer> upDownDelaySpinner = new Spinner<>(10, 2000, agent.getDelayUpDown());
		upDownDelaySpinner.setMinWidth(100);
		upDownDelaySpinner.setEditable(true);
		upDownDelaySpinner.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				agent.setDelayUpDown(newValue);
			}
		});
		gp.add(upDownDelayLabel, 1, 7, 1, 1);
		gp.add(upDownDelaySpinner, 2, 7, 1, 1);

		Label betweenClickDelayLabel = new Label("Delay between clicks (millisec):");
		betweenClickDelayLabel.setWrapText(true);
		Spinner<Integer> betweenClickDelaySpinner = new Spinner<>(10, 2000, agent.getDelayBetweenClicks());
		betweenClickDelaySpinner.setMinWidth(100);
		betweenClickDelaySpinner.setEditable(true);
		betweenClickDelaySpinner.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				agent.setDelayBetweenClicks(newValue);
			}
		});
		gp.add(betweenClickDelayLabel, 1, 8, 1, 1);
		gp.add(betweenClickDelaySpinner, 2, 8, 1, 1);

		this.setScene(scene);

	}

	private void setCountRemainingText(int count) {
		countRemainingLabel.setText("Squares remaining: " + count);
	}
}
