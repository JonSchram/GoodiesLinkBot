package goodieslink.ui.javafx;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Window;

public class RectangleBorder extends Rectangle {

	private enum Edge {
		NONE, NW, N, NE, E, SE, S, SW, W;
	}

	class RectangleResizer implements EventHandler<MouseEvent> {

		private double beginRightEdge;
		private double beginBottomEdge;

		private boolean resizeRight;
		private boolean resizeLeft;
		private boolean resizeTop;
		private boolean resizeBottom;

		private int cornerSize;
		private double thinBorderSize;
		private double thickBorderSize;

		public RectangleResizer(int cornerSize, int thinBorderSize, int thickBorderSize) {
			this.resizeRight = false;
			this.resizeLeft = false;
			this.resizeTop = false;
			this.resizeBottom = false;
			this.cornerSize = cornerSize;
			this.thinBorderSize = thinBorderSize;
			this.thickBorderSize = thickBorderSize;
		}

		private Cursor getCursor(Edge e) {
			switch (e) {
			case N:
				return Cursor.N_RESIZE;
			case E:
				return Cursor.E_RESIZE;
			case S:
				return Cursor.S_RESIZE;
			case W:
				return Cursor.W_RESIZE;
			case NW:
				return Cursor.NW_RESIZE;
			case NE:
				return Cursor.NE_RESIZE;
			case SW:
				return Cursor.SW_RESIZE;
			case SE:
				return Cursor.SE_RESIZE;
			default:
				return Cursor.DEFAULT;
			}
		}

		public double getThickBorderSize() {
			return thickBorderSize;
		}

		public double getThinBorderSize() {
			return thinBorderSize;
		}

		@Override
		public void handle(MouseEvent event) {
			EventType<? extends MouseEvent> eventType = event.getEventType();
			if (MouseEvent.MOUSE_PRESSED.equals(eventType)) {
				handlePress(event);
			} else if (MouseEvent.MOUSE_RELEASED.equals(eventType)) {
				handleRelease(event);
			} else if (MouseEvent.MOUSE_DRAGGED.equals(eventType)) {
				handleDrag(event);
			} else if (MouseEvent.MOUSE_ENTERED.equals(eventType)) {
				handleEntered(event);
			} else if (MouseEvent.MOUSE_EXITED.equals(eventType)) {
				handleExited(event);
			} else if (MouseEvent.MOUSE_MOVED.equals(eventType)) {
				handleMoved(event);
			}
		}

		private void handleDrag(MouseEvent event) {
			if (event.getTarget() instanceof RectangleBorder) {
				final int MIN_WIDTH = 15;
				final int MIN_HEIGHT = 15;
				Scene s = getScene();

				double newPosX = RectangleBorder.this.getX();
				double newPosY = RectangleBorder.this.getY();

				double newWidth = RectangleBorder.this.getWidth();
				double newHeight = RectangleBorder.this.getHeight();

				StrokeType st = RectangleBorder.this.getStrokeType();
				double strokeWidth = resizer.getThickBorderSize();

				double padding = 0;
				if (st.equals(StrokeType.CENTERED)) {
					padding = strokeWidth / 2;
				} else if (st.equals(StrokeType.OUTSIDE)) {
					padding = strokeWidth;
				}
				if (resizeRight) {
					newWidth = event.getX() - padding * 2;
				} else if (resizeLeft) {
					if (beginRightEdge - event.getScreenX() >= MIN_WIDTH) {
						double delta = s.getWindow().getX() - event.getScreenX();
						newPosX = RectangleBorder.this.getX() - delta;
						newWidth = beginRightEdge - event.getScreenX();
					}
				}

				if (resizeBottom) {
					newHeight = event.getY();
				} else if (resizeTop) {
					if (beginBottomEdge - event.getScreenY() >= MIN_HEIGHT) {
						double delta = RectangleBorder.this.getY() - event.getY();
						newPosY = RectangleBorder.this.getY() - delta;
						newHeight = beginBottomEdge - event.getScreenY();
					}
				}

				newWidth = Math.max(MIN_WIDTH, newWidth);
				newHeight = Math.max(MIN_HEIGHT, newHeight);
				setRectangleSize(newPosX, newPosY, newWidth, newHeight);
			}
		}

		private void handleEntered(MouseEvent event) {
			if (event.getButton().equals(MouseButton.NONE)) {
				setStrokeWidth(thickBorderSize);
				Edge edge = mouseLocation(event);
				getScene().setCursor(getCursor(edge));
			}
		}

		private void handleExited(MouseEvent event) {
			if (event.getButton().equals(MouseButton.NONE)) {
				setStrokeWidth(thinBorderSize);
				getScene().setCursor(Cursor.DEFAULT);
			}
		}

		private void handleMoved(MouseEvent event) {
			if (event.getButton().equals(MouseButton.NONE)) {
				Edge edge = mouseLocation(event);
				getScene().setCursor(getCursor(edge));
			}
		}

		private void handlePress(MouseEvent event) {
			Edge e = mouseLocation(event);

			RectangleBorder thisRect = RectangleBorder.this;
			beginRightEdge = thisRect.getWidth() + thisRect.getScene().getWindow().getX();
			beginBottomEdge = thisRect.getHeight() + thisRect.getScene().getWindow().getY();

			if (Edge.N.equals(e)) {
				resizeTop = true;
				resizeBottom = false;
				resizeLeft = false;
				resizeRight = false;

			} else if (Edge.NE.equals(e)) {
				resizeTop = true;
				resizeBottom = false;
				resizeLeft = false;
				resizeRight = true;

			} else if (Edge.NW.equals(e)) {
				resizeTop = true;
				resizeBottom = false;
				resizeLeft = true;
				resizeRight = false;

			} else if (Edge.S.equals(e)) {
				resizeTop = false;
				resizeBottom = true;
				resizeLeft = false;
				resizeRight = false;

			} else if (Edge.SE.equals(e)) {
				resizeTop = false;
				resizeBottom = true;
				resizeLeft = false;
				resizeRight = true;

			} else if (Edge.SW.equals(e)) {
				resizeTop = false;
				resizeBottom = true;
				resizeLeft = true;
				resizeRight = false;

			} else if (Edge.E.equals(e)) {
				resizeTop = false;
				resizeBottom = false;
				resizeLeft = false;
				resizeRight = true;

			} else if (Edge.W.equals(e)) {
				resizeTop = false;
				resizeBottom = false;
				resizeLeft = true;
				resizeRight = false;
			}

		}

		private void handleRelease(MouseEvent event) {
			setStrokeWidth(thinBorderSize);
			getScene().setCursor(Cursor.DEFAULT);
		}

		private Edge mouseLocation(MouseEvent event) {
			Edge currentEdge = Edge.NONE;
			if (event.getTarget() instanceof Rectangle) {
				Rectangle r = (Rectangle) event.getTarget();

				double distanceToLeft = Math.abs(event.getX() - r.getX());
				double distanceToRight = Math.abs(event.getX() - (r.getX() + r.getWidth()));

				double distanceToTop = Math.abs(event.getY() - r.getY());
				double distanceToBottom = Math.abs(event.getY() - (r.getY() + r.getHeight()));

				boolean onTop = distanceToTop <= cornerSize;
				boolean onBottom = distanceToBottom <= cornerSize;
				boolean onRight = distanceToRight <= cornerSize;
				boolean onLeft = distanceToLeft <= cornerSize;

				if (onTop) {
					if (onRight) {
						currentEdge = Edge.NE;
					} else if (onLeft) {
						currentEdge = Edge.NW;
					} else {
						currentEdge = Edge.N;
					}
				} else if (onBottom) {
					if (onRight) {
						currentEdge = Edge.SE;
					} else if (onLeft) {
						currentEdge = Edge.SW;
					} else {
						currentEdge = Edge.S;
					}
				} else if (onRight) {
					currentEdge = Edge.E;
				} else if (onLeft) {
					currentEdge = Edge.W;
				}
			}
			return currentEdge;
		}

		public void setThickBorderSize(double thickBorderSize) {
			this.thickBorderSize = thickBorderSize;
		}

		public void setThinBorderSize(double thinBorderSize) {
			this.thinBorderSize = thinBorderSize;
		}

	}

	final public int DEFAULT_CORNER_SIZE = 15;
	final public int DEFAULT_SELECTED_BORDER_SIZE = 2;
	final public int DEFAULT_UNSELECTED_BORDER_SIZE = 1;

	private RectangleResizer resizer;

	{
		resizer = new RectangleResizer(DEFAULT_CORNER_SIZE, DEFAULT_UNSELECTED_BORDER_SIZE,
				DEFAULT_SELECTED_BORDER_SIZE);

		this.setOnMousePressed(resizer);
		this.setOnMouseReleased(resizer);
		this.setOnMouseMoved(resizer);
		this.setOnMouseEntered(resizer);
		this.setOnMouseExited(resizer);
		this.setOnMouseDragged(resizer);
	}

	public RectangleBorder() {
		super();
	}

	public RectangleBorder(double width, double height) {
		super(width, height);
	}

	public RectangleBorder(double x, double y, double width, double height) {
		super(x, y, width, height);
	}

	public RectangleBorder(double width, double height, Paint fill) {
		super(width, height, fill);
	}

	public void setRectangleSize(double x, double y, double width, double height) {
		Window w = RectangleBorder.this.getScene().getWindow();

		StrokeType st = RectangleBorder.this.getStrokeType();
		double strokeWidth = resizer.getThickBorderSize();

		double padding = 0;
		if (st.equals(StrokeType.CENTERED)) {
			padding = strokeWidth / 2;
		} else if (st.equals(StrokeType.OUTSIDE)) {
			padding = strokeWidth;
		}

		w.setWidth(width + 2 * padding);
		w.setHeight(height + 2 * padding);

		w.setX(x + -padding + w.getX());
		w.setY(y + -padding + w.getY());

		this.setWidth(width);
		this.setHeight(height);
		setX(padding);
		setY(padding);
	}

	public void setSelectedStrokeWidth(double width) {
		resizer.setThickBorderSize(width);
	}

	public void setUnselectedStrokeWidth(double width) {
		resizer.setThinBorderSize(width);
	}
}