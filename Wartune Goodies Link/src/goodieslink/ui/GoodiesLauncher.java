package goodieslink.ui;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

import goodieslink.ui.javafx.GoodieStatusWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Performs initialization and begins UI
 * 
 * @author Jonathan Schram
 *
 */
public class GoodiesLauncher extends Application {

	static {
		String libraryRootRelative = "lib/native";
		String libraryRootAbsolute = new File(libraryRootRelative).getAbsolutePath();
		int bits = Integer.parseInt(System.getProperty("sun.arch.data.model"));
		String os = System.getProperty("os.name");
		System.out.println(bits + ", " + os);
		System.out.println(SystemUtils.OS_ARCH);

		if (SystemUtils.IS_OS_LINUX) {
			if (bits == 64) {
				// only have x64 version for Linux
				System.out.println("Loading " + libraryRootAbsolute + "/linux64/libopencv_java2410.so");
				System.load(libraryRootAbsolute + "/linux64/libopencv_java2410.so");
			}
		} else if (SystemUtils.IS_OS_WINDOWS) {
			if (bits == 64) {
				System.out.println("Loading " + libraryRootAbsolute + "/windows64/opencv_java2410.dll");
				System.load(libraryRootAbsolute + "/windows64/opencv_java2410.dll");
			} else {
				System.out.println("Loading " + libraryRootAbsolute + "/windows32/opencv_java2410.dll");
				System.load(libraryRootAbsolute + "/windows32/opencv_java2410.dll");
			}
		} else if (SystemUtils.IS_OS_MAC) {
			// have no mac library
		}
		System.out.println("Done loading library");
	}

	public static void main(String[] args) {
		// thanks to
		// https://github.com/PatternConsulting/opencv
		// for pointing out that using this library requires this extra call
		// nu.pattern.OpenCV.loadShared();
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		displayInterface(args);
	}

	private static void displayInterface(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage statusWindow = new GoodieStatusWindow();
		statusWindow.show();
	}

}
