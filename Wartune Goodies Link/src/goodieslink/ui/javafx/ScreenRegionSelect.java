package goodieslink.ui.javafx;

import java.awt.Rectangle;

import org.opencv.core.Rect;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScreenRegionSelect {

	public Stage create() {
		Stage s = new Stage(StageStyle.TRANSPARENT);
		s.setWidth(100);
		s.setHeight(100);
		s.setAlwaysOnTop(true);

		Pane shapes = new Pane();
		stage = s;
		scene = new Scene(shapes, s.getWidth(), s.getHeight(), null);
		scene.setFill(Color.TRANSPARENT);

		sizeRect = new RectangleBorder(0, 0, 100, 100);
		sizeRect.setStrokeType(StrokeType.OUTSIDE);
		sizeRect.setFill(null);
		sizeRect.setStroke(Color.RED);
		sizeRect.setStrokeWidth(3);
		sizeRect.setUnselectedStrokeWidth(3);
		sizeRect.setSelectedStrokeWidth(10);

		shapes.getChildren().add(sizeRect);

		s.setTitle("Rectangular select");
		s.setScene(scene);
		scene.getRoot().setStyle("-fx-background-color: transparent");

		return s;
	}

	private Stage stage;

	private Scene scene;

	private RectangleBorder sizeRect;

	public Rectangle getEnclosedRegion() {
		return new Rectangle((int) (sizeRect.getX() + stage.getX()), (int) (sizeRect.getY() + stage.getY()),
				(int) sizeRect.getWidth(), (int) sizeRect.getHeight());
	}

	public Stage getStage() {
		return stage;
	}

	public void adjustRectSize(Rectangle r) {
		sizeRect.setRectangleSize(r.x, r.y, r.width, r.height);
	}

	public void initRectSize() {
		sizeRect.setRectangleSize(0, 0, 100, 100);
	}
}
