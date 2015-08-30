package goodieslink.ui.javafx;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.io.File;

import goodieslink.controller.GoodieAgent;
import goodieslink.logging.ImageDecorator;
import goodieslink.logging.ProgressLogger;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
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
						if (agent.hasMatch()) {
							// match was found so put a delay here before
							// clicking again
							agent.delayBetweenClicks();
						}
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
					if (GoodieStatusWindow.this.isShowing()) {
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
				}

			}).start();
		}

	}

	private GoodieAgent agent;
	private ScreenRegionSelect regionSelector;
	private DebugConsole outputConsole;
	private Label countRemainingLabel;
	private ProgressLogger imageLogger;
	private ImageDecorator decorator;

	private float squareBorderWidth;
	private float pathWidth;

	Rectangle captureRegion;

	boolean stopOperation;

	public GoodieStatusWindow() throws AWTException {
		stopOperation = false;
		regionSelector = new ScreenRegionSelect();
		outputConsole = new DebugConsole();
		imageLogger = new ProgressLogger();
		squareBorderWidth = 2;
		pathWidth = 10;
		decorator = new ImageDecorator(new BasicStroke(pathWidth), new java.awt.Color(228, 215, 0),
				new BasicStroke(squareBorderWidth), new java.awt.Color(255, 0, 0));

		// lowered threshold so that if part of a square is missing it will
		// still be found
		agent = new GoodieAgent(0.80, 19, 23, 20, 4, 10);
		agent.setDebugStream(outputConsole.getDebugStream());
		agent.setLogger(imageLogger);
		agent.setDecorator(decorator);
		create();
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
		gp.add(instructionLabel, 0, 0, 2, 1);

		Label instructionLabel2 = new Label(
				"Then click this start button and the mouse will automatically click matching squares.");
		instructionLabel2.setWrapText(true);
		gp.add(instructionLabel2, 0, 1, 2, 1);

		Label instructionLabel3 = new Label(
				"Please don't have your web browser on full screen mode because depending on your platform this window will not stay on top of the browser.");
		instructionLabel3.setWrapText(true);
		gp.add(instructionLabel3, 0, 2, 2, 1);

		Label instructionLabel4 = new Label("Press ESC to stop clicking.");
		gp.add(instructionLabel4, 0, 3, 2, 1);

		Button startButton = new Button("Start");
		startButton.setOnAction(new StartButtonHandler());
		gp.add(startButton, 0, 4, 1, 1);

		countRemainingLabel = new Label();
		gp.add(countRemainingLabel, 0, 5, 2, 1);

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

		GridPane imageDebugPanel = new GridPane();
		imageDebugPanel.setDisable(true);

		Label parentDirLabel = new Label();

		CheckBox saveImagesCheckBox = new CheckBox("Save path images");
		saveImagesCheckBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (saveImagesCheckBox.isSelected()) {
					DirectoryChooser folderChooser = new DirectoryChooser();
					folderChooser.setTitle("Select location where debug folder should be created");
					File chosenFolder = folderChooser.showDialog(GoodieStatusWindow.this);
					if (chosenFolder != null) {
						imageLogger.setLogging(true);
						imageLogger.setDirectory(chosenFolder);
						imageDebugPanel.setDisable(false);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								parentDirLabel.setText("Saving in \"" + imageLogger.getParentDirectoryName()
										+ File.separator + imageLogger.getDirectoryName() + "\"");
							}
						});

					} else {
						imageLogger.setLogging(false);
						saveImagesCheckBox.setSelected(false);
						imageDebugPanel.setDisable(true);
					}
				} else {
					imageDebugPanel.setDisable(true);
				}
			}
		});

		java.awt.Color borderColor = decorator.getSquareBorderColor();
		java.awt.Color pathColor = decorator.getPathColor();

		ColorPicker squareColorPicker = new ColorPicker(
				new Color((double) borderColor.getRed() / 255, (double) borderColor.getGreen() / 255,
						(double) borderColor.getBlue() / 255, (double) borderColor.getAlpha() / 255));
		ColorPicker pathColorPicker = new ColorPicker(
				new Color((double) pathColor.getRed() / 255, (double) pathColor.getGreen() / 255,
						(double) pathColor.getBlue() / 255, (double) pathColor.getAlpha() / 255));

		squareColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				decorator.setSquareBorderColor(newValue);
			}
		});

		pathColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				decorator.setPathColor(newValue);
			}
		});

		Label pathBorderLabel = new Label("Path line color:");
		pathBorderLabel.setWrapText(true);
		Label squareBorderLabel = new Label("Square border color:");
		squareBorderLabel.setWrapText(true);

		imageDebugPanel.add(parentDirLabel, 0, 0, 2, 1);
		imageDebugPanel.add(squareBorderLabel, 0, 1);
		imageDebugPanel.add(squareColorPicker, 1, 1);
		imageDebugPanel.add(pathBorderLabel, 0, 2);
		imageDebugPanel.add(pathColorPicker, 1, 2);

		gp.add(debugCheckBox, 0, 6, 2, 1);
		gp.add(saveImagesCheckBox, 0, 7, 2, 1);
		gp.add(imageDebugPanel, 0, 8, 2, 1);

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
		gp.add(upDownDelayLabel, 0, 9, 1, 1);
		gp.add(upDownDelaySpinner, 1, 10, 1, 1);

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
		gp.add(betweenClickDelayLabel, 0, 11, 1, 1);
		gp.add(betweenClickDelaySpinner, 1, 12, 1, 1);

		this.setScene(scene);

	}

	private void setCountRemainingText(int count) {
		countRemainingLabel.setText("Squares remaining: " + count);
	}
}
