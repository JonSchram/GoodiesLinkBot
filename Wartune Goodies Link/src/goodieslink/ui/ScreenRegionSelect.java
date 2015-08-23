package goodieslink.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

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

		primaryStage.setScene(scene);
		primaryStage.show();
		sizeRect.setRectangleSize(0, 0, 100, 100);
	}
}
