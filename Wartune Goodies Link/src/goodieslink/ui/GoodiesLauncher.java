package goodieslink.ui;

import org.opencv.core.Core;

import goodieslink.ui.javafx.GoodieStatusWindow;
import goodieslink.ui.javafx.ScreenRegionSelect;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Performs initialization and begins UI
 * 
 * @author Jonathan Schram
 *
 */
public class GoodiesLauncher extends Application {
	public static void main(String[] args) {
		// thanks to
		// https://github.com/PatternConsulting/opencv
		// for pointing out that using this library requires this extra call
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		displayInterface(args);
	}

	private static void displayInterface(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ScreenRegionSelect regionSelectCreator = new ScreenRegionSelect();
		Stage regionSelector = regionSelectCreator.create();
		Stage statusWindow = new GoodieStatusWindow(regionSelector).create();

		regionSelector.show();
		statusWindow.show();
		regionSelectCreator.initRectSize();
	}

}
