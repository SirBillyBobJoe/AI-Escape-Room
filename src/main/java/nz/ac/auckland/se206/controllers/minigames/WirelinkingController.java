package nz.ac.auckland.se206.controllers.minigames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class WirelinkingController {

  @FXML private VBox leftHoleBox;
  @FXML private VBox rightHoleBox;
  @FXML private Pane drawingArea;

  private Line currentWire;
  private CorrectPath currentCorrectPath;
  private Map<Circle, CorrectPath> correctPaths = new HashMap<>();
  private Color backgroundColor = Color.WHITE;

  private class CorrectPath {
    private Circle startHole;
    private Circle endHole;
    private boolean isComplete = false;

    public CorrectPath(Circle startHole, Circle endHole) {
      this.startHole = startHole;
      this.endHole = endHole;
    }

    public boolean isComplete() {
      return isComplete;
    }

    public void setComplete() {
      isComplete = true;
    }
  }

  @FXML
  public void initialize() {
    initializeHoles();
    drawingArea.setOnMouseDragOver(this::handleWireDragging);
    drawingArea.setOnMouseDragReleased(this::handleDragStop);
  }

  private void initializeHoles() {
    for (Color color : List.of(Color.RED, Color.GREEN, Color.BLUE)) {
      Circle leftHole = createHole(color, true);
      leftHoleBox.getChildren().add(leftHole);
      Circle rightHole = createHole(color, false);
      rightHoleBox.getChildren().add(rightHole);
      correctPaths.put(leftHole, new CorrectPath(leftHole, rightHole));
    }
  }

  private Circle createHole(Color color, boolean isLeft) {
    Circle hole = new Circle(15, backgroundColor);
    hole.setStroke(color);

    if (isLeft) {
      hole.setOnDragDetected(handleStartDrag);
    } else {
      hole.setOnMouseDragEntered(event -> updateCurrentWireEndPosition(event));
      hole.setOnMouseDragReleased(this::handleDropOnRightHole);
    }

    return hole;
  }

  private Point2D getHoleCenter(Circle hole) {
    Point2D holeCenterLocal =
        new Point2D(hole.getLayoutBounds().getCenterX(), hole.getLayoutBounds().getCenterY());
    Point2D holeCenterInVBox = hole.localToParent(holeCenterLocal);

    double finalCenterX = hole.getParent().getLayoutX() + holeCenterInVBox.getX();
    double finalCenterY = hole.getParent().getLayoutY() + holeCenterInVBox.getY();

    return new Point2D(finalCenterX, finalCenterY);
  }

  private void updateCurrentWireEndPosition(MouseEvent event) {
    if (currentWire == null) return;

    Circle sourceHole = (Circle) event.getSource();
    Point2D holeCenter = getHoleCenter(sourceHole);

    currentWire.setEndX(holeCenter.getX());
    currentWire.setEndY(holeCenter.getY());
  }

  private EventHandler<MouseEvent> handleStartDrag =
      event -> {
        Circle sourceHole = (Circle) event.getSource();
        if (correctPaths.get(sourceHole).isComplete()) return;

        currentCorrectPath = correctPaths.get(sourceHole);
        drawingArea.startFullDrag();

        resetCurrentWire();
        initiateCurrentWire(sourceHole, event);
      };

  private void resetCurrentWire() {
    if (currentWire != null) {
      drawingArea.getChildren().remove(currentWire);
    }
  }

  private void initiateCurrentWire(Circle sourceHole, MouseEvent event) {
    Point2D holeCenter = getHoleCenter(sourceHole);
    Color color = (Color) sourceHole.getStroke();

    currentWire =
        new Line(holeCenter.getX(), holeCenter.getY(), event.getSceneX(), event.getSceneY());
    currentWire.setStroke(
        new LinearGradient(
            0,
            0,
            1,
            0,
            true,
            CycleMethod.REPEAT,
            new Stop(0, color),
            new Stop(0.1, color.darker())));
    currentWire.setStrokeWidth(3);

    DropShadow shadow = new DropShadow();
    shadow.setRadius(5.0);
    shadow.setOffsetX(3.0);
    shadow.setOffsetY(3.0);
    shadow.setColor(Color.GRAY);
    currentWire.setEffect(shadow);

    drawingArea.getChildren().add(currentWire);
  }

  private void handleDropOnRightHole(MouseEvent event) {
    drawingArea.getChildren().remove(currentWire);
    if (currentWire != null && currentCorrectPath.endHole == (Circle) event.getSource()) {
      finalizeWireConnection(event);
    }
    resetCurrentWireAndPath();
  }

  private void finalizeWireConnection(MouseEvent event) {
    Circle sourceHole = (Circle) event.getSource();
    Point2D holeCenter = getHoleCenter(sourceHole);
    Line permanentWire =
        new Line(
            currentWire.getStartX(), currentWire.getStartY(), holeCenter.getX(), holeCenter.getY());
    permanentWire.setStroke(currentWire.getStroke());
    permanentWire.setStrokeWidth(currentWire.getStrokeWidth());
    permanentWire.setEffect(currentWire.getEffect());

    drawingArea.getChildren().add(permanentWire);
    sourceHole.setFill(permanentWire.getStroke());
    currentCorrectPath.startHole.setFill(permanentWire.getStroke());
    currentCorrectPath.setComplete();
  }

  private void handleWireDragging(MouseEvent event) {
    if (currentWire != null) {
      currentWire.setEndX(event.getSceneX());
      currentWire.setEndY(event.getSceneY());
    }
  }

  private void handleDragStop(MouseEvent event) {
    resetCurrentWireAndPath();
  }

  private void resetCurrentWireAndPath() {
    resetCurrentWire();
    currentCorrectPath = null;
  }
}
