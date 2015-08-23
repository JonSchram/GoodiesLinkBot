package goodieslink.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScreenRegionSelect extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	Stage stage;

	Scene scene;

	RectangleBorder sizeRect;

	@Override
	public void start(final Stage primaryStage) {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setAlwaysOnTop(true);

		Pane shapes = new Pane();
		stage = primaryStage;
		scene = new Scene(shapes, primaryStage.getWidth(), primaryStage.getHeight(), null);
		// shapes.setPadding(new Insets(5));

		sizeRect = new RectangleBorder(0, 0, 100, 100);
		sizeRect.setStrokeType(StrokeType.OUTSIDE);
		sizeRect.setFill(null);
		sizeRect.setStroke(Color.RED);
		sizeRect.setStrokeWidth(3);
		sizeRect.setUnselectedStrokeWidth(3);
		sizeRect.setSelectedStrokeWidth(10);

		shapes.getChildren().add(sizeRect);

		primaryStage.setTitle("Rectangular select");
		primaryStage.setScene(scene);
		primaryStage.show();
		sizeRect.setRectangleSize(0, 0, 100, 100);
	}
}
