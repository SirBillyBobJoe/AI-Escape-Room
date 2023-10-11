package nz.ac.auckland.se206.controllers.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** A controller responsible for handling the Wirelinking mini-game logic and interactions. */
public class WirelinkingController {

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

    /** Checks if it has found the correct end. */
    public boolean isCorrect(Circle start, Circle end) {
      return start == startHole && end == endHole || start == endHole && end == startHole;
    }
  }

  @FXML private VBox leftHoleBox;
  @FXML private VBox rightHoleBox;
  @FXML private Pane drawingArea;

  private Line animationWire;
  private ImageView image = new ImageView(new Image("/images/wireConnecting/cursor.png"));

  private Line currentWire;
  private CorrectPath currentCorrectPath;
  private Circle currentStartHole;
  private Map<Circle, CorrectPath> correctPaths = new HashMap<>();
  private Color backgroundColor = Color.WHITE;
  private Point2D startCenter;
  private Point2D endCenter;

  /**
   * Event handler for initiating a wire drag operation. Determines the correct path for the wire
   * based on the hole that initiated the drag and starts the visual representation of the wire
   * drag.
   */
  private EventHandler<MouseEvent> handleStartDrag =
      event -> {
        Circle sourceHole = (Circle) event.getSource();
        Paint colour = sourceHole.getStroke();
        if (colour.equals(Color.RED) && !GameState.inventory.containsItem(GameState.redWire)) {
          new MouseClick().play();
          GameState.inventory.setTextChat("You need a red wire");
          return;
        }
        if (colour.equals(Color.BLUE) && !GameState.inventory.containsItem(GameState.blueWire)) {
          GameState.inventory.setTextChat("You need a blue wire");
          new MouseClick().play();
          return;
        }
        if (colour.equals(Color.GREEN) && !GameState.inventory.containsItem(GameState.greenWire)) {
          GameState.inventory.setTextChat("You need a green wire");
          new MouseClick().play();
          return;
        }

        if (correctPaths.get(sourceHole).isComplete()) {
          return;
        }
        GameState.userStartedDragging.set(true);

        currentStartHole = sourceHole;
        currentCorrectPath = correctPaths.get(sourceHole);
        drawingArea.startFullDrag();

        resetCurrentWire();
        initiateCurrentWire(sourceHole, event);
      };

  /** Initializes the controller, sets up mouse event listeners and initiates holes. */
  @FXML
  public void initialize() {
    initializeHoles();
    drawingArea.setOnMouseDragOver(
        this::handleWireDragging); // Set up mouse drag over event handler
    drawingArea.setOnMouseDragReleased(
        this::handleDragStop); // Set up mouse drag release event handler
    startRedWireAnimation();
    animationWire.visibleProperty().bind(GameState.userStartedDragging.not());
    image.visibleProperty().bind(GameState.userStartedDragging.not());
  }

  /** Initializes and positions the colored holes in the interface. */
  private void initializeHoles() {
    // list of colors
    List<Color> colorsLeft = new ArrayList<>(List.of(Color.RED, Color.GREEN, Color.BLUE));
    List<Color> colorsRight = new ArrayList<>(List.of(Color.RED, Color.GREEN, Color.BLUE));
    Random random = new Random();
    // sets bound for the vertical boxes
    leftHoleBox.setPickOnBounds(false);
    rightHoleBox.setPickOnBounds(false);
    HashMap<Color, List<Circle>> map = new HashMap<Color, List<Circle>>();
    int count = 0;
    // loops until one of the lists is empty
    while (!colorsLeft.isEmpty()) {
      // chooses a random color
      int index = random.nextInt(colorsLeft.size());
      Color leftColor = colorsLeft.remove(index);
      Circle leftHole = createHole(leftColor, true);
      leftHoleBox.getChildren().add(count, leftHole);
      // adds color to map and path
      map.putIfAbsent(leftColor, new ArrayList<Circle>());
      map.get(leftColor).add(leftHole);

      // chooses random color
      index = random.nextInt(colorsRight.size());
      // adds it randomly
      Color rightColor = colorsRight.remove(index);
      Circle rightHole = createHole(rightColor, false);
      rightHoleBox.getChildren().add(count, rightHole);
      map.putIfAbsent(rightColor, new ArrayList<Circle>());
      map.get(rightColor).add(rightHole);

      // sets the position based on order of adding them
      if (leftHole.getStroke() == Color.RED) {
        if (count == 0) {
          startCenter = new Point2D(10, 67);
        } else if (count == 1) {
          startCenter = new Point2D(10, 100);
        } else {
          startCenter = new Point2D(10, 133);
        }
      }
      // logic for right hole
      if (rightHole.getStroke() == Color.RED) {
        if (count == 0) {
          endCenter = new Point2D(280, 67);
        } else if (count == 1) {
          endCenter = new Point2D(280, 100);
        } else {
          endCenter = new Point2D(280, 133);
        }
      }
      count++;
    }
    System.out.println(map);
    // loops through map to set the correct path
    for (Color val : map.keySet()) {
      List<Circle> circle = map.get(val);
      if (circle == null || circle.isEmpty()) {
        continue;
      }
      CorrectPath path = new CorrectPath(circle.get(0), circle.get(1));
      correctPaths.put(circle.get(0), path); // Associate holes with correct paths
      correctPaths.put(circle.get(1), path); // Associate holes with correct paths
    }
  }

  private void startRedWireAnimation() {

    animationWire =
        new Line(startCenter.getX(), startCenter.getY(), startCenter.getX(), startCenter.getY());
    animationWire.setStroke(Color.RED);
    animationWire.setStrokeWidth(3);
    animationWire.setOpacity(0.5);
    drawingArea.getChildren().add(animationWire);

    // Width in pixels
    image.setFitWidth(10);
    image.setFitHeight(15);
    image.setOpacity(0.5);

    // Bind the ImageView's position to the wire's end position
    image.xProperty().bind(animationWire.endXProperty());
    image.yProperty().bind(animationWire.endYProperty());

    // Add the ImageView to the drawing area
    drawingArea.getChildren().add(image);

    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                new KeyValue(animationWire.endXProperty(), startCenter.getX()),
                new KeyValue(animationWire.endYProperty(), startCenter.getY())),
            new KeyFrame(
                Duration.seconds(1.5),
                new KeyValue(animationWire.endXProperty(), endCenter.getX()),
                new KeyValue(animationWire.endYProperty(), endCenter.getY())));

    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.setAutoReverse(false);
    timeline.play();

    timeline.setOnFinished(
        event -> {
          if (!GameState.userStartedDragging.getValue()) {
            drawingArea.getChildren().remove(animationWire);
          }
        });
  }

  /**
   * Creates a draggable hole with a specific color.
   *
   * @param color The color of the hole.
   * @param isLeft Whether the hole is on the left side or not.
   * @return A new hole circle.
   */
  private Circle createHole(Color color, boolean isLeft) {
    // creates a new hole
    Circle hole = new Circle(10, backgroundColor);
    hole.setStroke(color);
    hole.setStrokeWidth(2.5);
    // determines if its a left or right
    hole.setOnDragDetected(handleStartDrag);
    // make it drooppable
    hole.setOnMouseDragEntered(event -> updateCurrentWireEndPosition(event));
    hole.setOnMouseDragReleased(this::handleDropOnRightHole);

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
    Point2D holeCenterInVerticalox = hole.localToParent(holeCenterLocal);

    // Calculate the center of the hole relative to the scene
    double finalCenterX = hole.getParent().getLayoutX() + holeCenterInVerticalox.getX();
    double finalCenterY = hole.getParent().getLayoutY() + holeCenterInVerticalox.getY();

    return new Point2D(finalCenterX, finalCenterY);
  }

  /**
   * Updates the end position of the currently dragged wire based on the event's location.
   *
   * @param event MouseEvent that holds the information about the mouse's current position.
   */
  private void updateCurrentWireEndPosition(MouseEvent event) {
    if (currentWire == null) {
      return;
    }

    Circle sourceHole = (Circle) event.getSource();
    Point2D holeCenter = getHoleCenter(sourceHole);

    currentWire.setEndX(holeCenter.getX());
    currentWire.setEndY(holeCenter.getY());
  }

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
    // gets the source of mouse event
    Point2D holeCenter = getHoleCenter(sourceHole);
    Color color = (Color) sourceHole.getStroke();
    // creates a wire
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
    // sets the size of the wire
    currentWire.setStrokeWidth(3);
    // creates a shadow and off sets the positio
    DropShadow shadow = new DropShadow();
    shadow.setRadius(5.0);
    shadow.setOffsetX(3.0);
    shadow.setOffsetY(3.0);
    // gives the wire color
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
    if (currentWire != null
        && currentCorrectPath.isCorrect(currentStartHole, (Circle) event.getSource())) {
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
    // get the source
    Circle sourceHole = (Circle) event.getSource();
    Point2D holeCenter = getHoleCenter(sourceHole);
    // creates a permanent wire
    Line permanentWire =
        new Line(
            currentWire.getStartX(), currentWire.getStartY(), holeCenter.getX(), holeCenter.getY());
    permanentWire.setStroke(currentWire.getStroke());
    permanentWire.setStrokeWidth(currentWire.getStrokeWidth());
    permanentWire.setEffect(currentWire.getEffect());
    // this drags and if it reaches a source creates a permanent wire
    drawingArea.getChildren().add(permanentWire);
    sourceHole.setFill(permanentWire.getStroke());
    currentStartHole.setFill(permanentWire.getStroke());
    currentCorrectPath.setComplete();
    // gives the wire some colour
    Paint colour = sourceHole.getStroke();
    if (colour.equals(Color.RED) && GameState.inventory.containsItem(GameState.redWire)) {
      GameState.inventory.removeObject(GameState.redWire);
    }
    if (colour.equals(Color.BLUE) && GameState.inventory.containsItem(GameState.blueWire)) {
      GameState.inventory.removeObject(GameState.blueWire);
    }
    if (colour.equals(Color.GREEN) && GameState.inventory.containsItem(GameState.greenWire)) {
      GameState.inventory.removeObject(GameState.greenWire);
    }

    checkCompleteness();
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
    currentStartHole = null;
  }

  /**
   * Checks if all paths in the wire puzzle are complete. If they are, various game states are
   * updated to indicate that the puzzle is solved, lights are turned on, and a new riddle is
   * activated.
   */
  private void checkCompleteness() {
    // check if its complete
    for (CorrectPath path : correctPaths.values()) {
      if (!path.isComplete()) {
        return;
      }
    }
    // set wirepuzzle to true
    GameState.puzzleSolved.get(Puzzle.WIRELINKING).set(true);
    System.out.println("Completed");
    System.out.println(GameState.puzzleSolved.get(Puzzle.WIRELINKING).getValue());
    GameState.wirePuzzleSolved = true;
    // turns the lights on
    GameState.riddleRoomController.turnLightsOn();
    GameState.gameMasterActions.activate("Stop! You need to solve my riddle now...");
    new Thread(
            () -> {
              TextToSpeech textToSpeech = TextToSpeech.getInstance();
              GameState.speechList.add(textToSpeech);
              textToSpeech.speak("Stop! You need to solve my riddle now...");
            })
        .start();
    GameState.riddleChat.newRiddle(GameState.padlockAnswer);
    GameState.isPuzzlesOn.set(false);
    exitPuzzle(null);
  }

  /**
   * Handler to exit the puzzle.
   *
   * @param event The MouseEvent triggering this action.
   */
  @FXML
  private void exitPuzzle(MouseEvent event) {
    GameState.currentPuzzle.set(Puzzle.NONE);
    System.out.println("Exit");
  }
}
