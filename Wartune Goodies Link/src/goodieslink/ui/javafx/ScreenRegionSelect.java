package goodieslink.ui.javafx;

import java.awt.Rectangle;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScreenRegionSelect extends Stage {

	public ScreenRegionSelect() {
		create();
	}

	private void create() {
		this.initStyle(StageStyle.TRANSPARENT);
		setWidth(100);
		setHeight(100);
		setAlwaysOnTop(true);

		Pane shapes = new Pane();
		scene = new Scene(shapes, getWidth(), getHeight(), null);
		scene.setFill(Color.TRANSPARENT);

		sizeRect = new RectangleBorder(0, 0, 100, 100);
		sizeRect.setStrokeType(StrokeType.OUTSIDE);
		sizeRect.setFill(null);
		sizeRect.setStroke(Color.RED);
		sizeRect.setStrokeWidth(3);
		sizeRect.setUnselectedStrokeWidth(3);
		sizeRect.setSelectedStrokeWidth(10);

		shapes.getChildren().add(sizeRect);

		setTitle("Rectangular select");
		setScene(scene);
		scene.getRoot().setStyle("-fx-background-color: transparent");

	}

	private Scene scene;

	private RectangleBorder sizeRect;

	public Rectangle getEnclosedRegion() {
		return new Rectangle((int) (sizeRect.getX() + getX()), (int) (sizeRect.getY() + getY()),
				(int) sizeRect.getWidth(), (int) sizeRect.getHeight());
	}

	public void adjustRectSize(Rectangle r) {
		sizeRect.setRectangleSize(r.x, r.y, r.width, r.height);
	}

	public void initRectSize() {
		sizeRect.setRectangleSize(0, 0, 100, 100);
	}
}
