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
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

/** A controller responsible for handling the Wirelinking mini-game logic and interactions. */
public class WirelinkingController {

  @FXML private VBox leftHoleBox;
  @FXML private VBox rightHoleBox;
  @FXML private Pane drawingArea;

  private Line currentWire;
  private CorrectPath currentCorrectPath;
  private Map<Circle, CorrectPath> correctPaths = new HashMap<>();
  private Color backgroundColor = Color.WHITE;

  /** Internal class to represent the correct paths for wires. */
  private class CorrectPath {
    private Circle startHole;
    private Circle endHole;
    private boolean isComplete = false;

    /**
     * Constructor to create a correct path.
     *
     * @param startHole The starting hole of the path.
     * @param endHole The ending hole of the path.
     */
    public CorrectPath(Circle startHole, Circle endHole) {
      this.startHole = startHole;
      this.endHole = endHole;
    }

    /**
     * Check if the path is completed.
     *
     * @return True if the path is complete, false otherwise.
     */
    public boolean isComplete() {
      return isComplete;
    }

    /** Marks the path as completed. */
    public void setComplete() {
      isComplete = true;
    }
  }

  /** Initializes the controller, sets up mouse event listeners and initiates holes. */
  @FXML
  public void initialize() {
    initializeHoles();
    drawingArea.setOnMouseDragOver(
        this::handleWireDragging); // Set up mouse drag over event handler
    drawingArea.setOnMouseDragReleased(
        this::handleDragStop); // Set up mouse drag release event handler
  }

  /** Initializes and positions the colored holes in the interface. */
  private void initializeHoles() {
    for (Color color : List.of(Color.RED, Color.GREEN, Color.BLUE)) {
      Circle leftHole = createHole(color, true);
      leftHoleBox.getChildren().add(leftHole);
      Circle rightHole = createHole(color, false);
      rightHoleBox.getChildren().add(rightHole);
      correctPaths.put(
          leftHole, new CorrectPath(leftHole, rightHole)); // Associate holes with correct paths
    }
  }

  /**
   * Creates a draggable hole with a specific color.
   *
   * @param color The color of the hole.
   * @param isLeft Whether the hole is on the left side or not.
   * @return A new hole circle.
   */
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

  /**
   * Gets the center position of the hole.
   *
   * @param hole The circle hole to find the center of.
   * @return The Point2D representing the center position.
   */
  private Point2D getHoleCenter(Circle hole) {
    // Calculate the center of the hole relative to its parent VBox
    Point2D holeCenterLocal =
        new Point2D(hole.getLayoutBounds().getCenterX(), hole.getLayoutBounds().getCenterY());
    Point2D holeCenterInVBox = hole.localToParent(holeCenterLocal);

    // Calculate the center of the hole relative to the scene
    double finalCenterX = hole.getParent().getLayoutX() + holeCenterInVBox.getX();
    double finalCenterY = hole.getParent().getLayoutY() + holeCenterInVBox.getY();

    return new Point2D(finalCenterX, finalCenterY);
  }

  /**
   * Updates the end position of the currently dragged wire based on the event's location.
   *
   * @param event MouseEvent that holds the information about the mouse's current position.
   */
  private void updateCurrentWireEndPosition(MouseEvent event) {
    if (currentWire == null) return;

    Circle sourceHole = (Circle) event.getSource();
    Point2D holeCenter = getHoleCenter(sourceHole);

    currentWire.setEndX(holeCenter.getX());
    currentWire.setEndY(holeCenter.getY());
  }

  /**
   * Event handler for initiating a wire drag operation. Determines the correct path for the wire
   * based on the hole that initiated the drag and starts the visual representation of the wire
   * drag.
   */
  private EventHandler<MouseEvent> handleStartDrag =
      event -> {
        Circle sourceHole = (Circle) event.getSource();
        if (correctPaths.get(sourceHole).isComplete()) return;

        currentCorrectPath = correctPaths.get(sourceHole);
        drawingArea.startFullDrag();

        resetCurrentWire();
        initiateCurrentWire(sourceHole, event);
      };

  /** Removes the current wire visual from the drawing area if it exists. */
  private void resetCurrentWire() {
    if (currentWire != null) {
      drawingArea.getChildren().remove(currentWire);
    }
  }

  /**
   * Initiates the visual representation of the wire being dragged. Sets up the wire's start
   * position, color, shadow effect and adds it to the drawing area.
   *
   * @param sourceHole Circle representing the hole where the drag started.
   * @param event MouseEvent that holds the information about the mouse's current position.
   */
  private void initiateCurrentWire(Circle sourceHole, MouseEvent event) {
    Point2D holeCenter = getHoleCenter(sourceHole);
    Color color = (Color) sourceHole.getStroke();

    currentWire =
        new Line(holeCenter.getX(), holeCenter.getY(), holeCenter.getX(), holeCenter.getY());
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

  /**
   * Handles the event where a wire is dropped onto the right hole. Checks if the wire connects to
   * the correct hole and finalizes the connection if true.
   *
   * @param event MouseEvent that holds the information about the drop position and source.
   */
  private void handleDropOnRightHole(MouseEvent event) {
    drawingArea.getChildren().remove(currentWire);
    if (currentWire != null && currentCorrectPath.endHole == (Circle) event.getSource()) {
      finalizeWireConnection(event);
    }
    resetCurrentWireAndPath();
  }

  /**
   * Finalizes the wire connection by creating a permanent wire between the starting hole and the
   * ending hole. Updates the hole colors and marks the correct path as complete.
   *
   * @param event MouseEvent that holds the information about the drop position and source.
   */
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

  /**
   * Updates the end position of the current wire visual based on the dragging event's location.
   *
   * @param event MouseEvent that holds the information about the mouse's current position.
   */
  private void handleWireDragging(MouseEvent event) {
    if (currentWire != null) {
      currentWire.setEndX(event.getX());
      currentWire.setEndY(event.getY());
    }
  }

  /**
   * Handles the end of the wire drag operation and resets the wire and its associated path.
   *
   * @param event MouseEvent that indicates the dragging operation has ended.
   */
  private void handleDragStop(MouseEvent event) {
    resetCurrentWireAndPath();
  }

  /** Resets the visual wire and its associated correct path. */
  private void resetCurrentWireAndPath() {
    resetCurrentWire();
    currentCorrectPath = null;
  }

  /**
   * Handler to exit the puzzle.
   *
   * @param event The MouseEvent triggering this action.
   */
  @FXML
  private void exitPuzzle(MouseEvent event) {
    GameState.currentPuzzle.set(Puzzle.NONE);
  }
}
