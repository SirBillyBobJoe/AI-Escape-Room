package nz.ac.auckland.se206.controllers.minigames;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class WirelinkingController {

  @FXML private VBox leftHoleBox;

  @FXML private VBox rightHoleBox;

  @FXML private Pane drawingArea;

  private Line currentWire;

  // This can be a list of colors or any other way you want to represent wires.
  private List<Color> colors = List.of(Color.RED, Color.GREEN, Color.BLUE);

  private Color backgroundColor = Color.WHITE;

  @FXML
  public void initialize() {
    // Initialize holes on both sides
    for (Color color : colors) {
      createHole(color, leftHoleBox, true);
      createHole(color, rightHoleBox, false);
    }

    drawingArea.setOnMouseMoved(this::handleWireDragging);
  }

  private Point2D getHoleCenter(Circle hole) {
    Point2D holeCenterLocal =
        new Point2D(hole.getLayoutBounds().getCenterX(), hole.getLayoutBounds().getCenterY());
    Point2D holeCenterInVBox = hole.localToParent(holeCenterLocal);

    double finalCenterX = hole.getParent().getLayoutX() + holeCenterInVBox.getX();
    double finalCenterY = hole.getParent().getLayoutY() + holeCenterInVBox.getY();
    return new Point2D(finalCenterX, finalCenterY);
  }

  private void createHole(Color color, VBox vBox, boolean isLeft) {
    Circle hole = new Circle(15);

    vBox.getChildren().add(hole);
    hole.setStroke(color); // set border color
    hole.setFill(backgroundColor); // transparent center

    hole.setOnMouseEntered(
        event -> {
          if (currentWire != null) {
            Circle sourceHole = (Circle) event.getSource();
            Point2D holeCenter = getHoleCenter(sourceHole);
            currentWire.setEndX(holeCenter.getX());
            currentWire.setEndY(holeCenter.getY());
          }
        });

    if (isLeft) {
      hole.setOnMousePressed(this::handleStartDrag);
    } else {
      hole.setOnMouseReleased(
          event -> {
            if (currentWire != null && currentWire.getStroke() == color) {
              drawingArea.getChildren().remove(currentWire);
              Circle sourceHole = (Circle) event.getSource();
              Point2D holeCenter = getHoleCenter(sourceHole);
              Line permanentWire =
                  new Line(
                      currentWire.getStartX(),
                      currentWire.getStartY(),
                      holeCenter.getX(),
                      holeCenter.getY());
              permanentWire.setStroke(color);
              drawingArea.getChildren().add(permanentWire);
              currentWire = null;
            }
          });
    }
  }

  private void handleStartDrag(MouseEvent event) {
    System.out.println("Start Dragging");
    if (currentWire != null) {
      drawingArea.getChildren().remove(currentWire);
    }

    Circle sourceHole = (Circle) event.getSource();
    Point2D holeCenter = getHoleCenter(sourceHole);
    Color color = (Color) sourceHole.getStroke();

    currentWire = new Line();
    currentWire.setStroke(color);
    currentWire.setStartX(holeCenter.getX());
    currentWire.setStartY(holeCenter.getY());
    currentWire.setEndX(event.getSceneX());
    currentWire.setEndY(event.getSceneY());

    drawingArea.getChildren().add(currentWire);
  }

  private void handleWireDragging(MouseEvent event) {
    if (currentWire != null) {
      currentWire.setEndX(event.getSceneX());
      currentWire.setEndY(event.getSceneY());
    }
  }
}
