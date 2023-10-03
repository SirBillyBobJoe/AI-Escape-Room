package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.items.Candle;
import nz.ac.auckland.se206.items.Hammer;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This class is the controller for the Puzzle Room. It handles the initialization and interaction
 * of puzzle items within the room.
 */
public class PuzzleRoomController {
  @FXML private ImageView background;
  @FXML private ImageView candle1;
  @FXML private ImageView candle2;
  @FXML private ImageView candle3;
  @FXML private ImageView candle4;
  @FXML private ImageView hammer;
  @FXML private ImageView greenWire;
  @FXML private CubicCurve riddleGlow1;
  @FXML private ImageView waterdrop;
  @FXML private ImageView puddle;

  private List<ImageView> candles;

  /**
   * Initializes the Puzzle Room, setting up its various elements like candles, hammer, wires, and
   * so on.
   */
  @FXML
  public void initialize() {
    // Initialise global variables for GameState from the puzle room
    GameState.puzzleRoom = background;
    GameState.riddleGlow1 = riddleGlow1;
    candles = new ArrayList<ImageView>();
    // logic for adding candles
    candles.add(candle1);
    candles.add(candle2);
    candles.add(candle3);
    candles.add(candle4);
    candle1.setUserData("candle");
    candle2.setUserData("candle");
    candle3.setUserData("candle");
    candle4.setUserData("candle");
    // logic for the wires
    GameState.currentRoomItems.put(greenWire, GameState.greenWire);
    GameState.currentRoomItems.put(candle1, new Candle());
    GameState.currentRoomItems.put(candle2, new Candle());
    GameState.currentRoomItems.put(candle3, new Candle());
    GameState.currentRoomItems.put(candle4, new Candle());
    // hammer logic
    hammer.setUserData("hammer");
    GameState.currentRoomItems.put(hammer, new Hammer());
    hammer.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PIPECONNECTING));

    greenWire.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING));
    puddle.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PIPECONNECTING).not());
    waterdrop.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PIPECONNECTING).not());
    // Add a listener to the visible property
    greenWire
        .visibleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) { // Check if the new value of the property is 'true'
                drop(greenWire);
              }
            });
    hammer
        .visibleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) { // Check if the new value of the property is 'true'
                drop(hammer);
              }
            });

    // Initialize Timeline
    Timeline timeline = new Timeline();

    // Create KeyFrame sequence
    KeyFrame kf =
        new KeyFrame(
            Duration.millis(2), // 5 milliseconds, approx 60fps
            new EventHandler<ActionEvent>() {
              private double deltaY = 1; // Set how much the image will move in each frame

              @Override
              public void handle(ActionEvent event) {
                // Update waterdrop position
                waterdrop.setLayoutY(waterdrop.getLayoutY() + deltaY);

                if (waterdrop.getLayoutY() > 450) {
                  waterdrop.setLayoutY(20); // Reset to just above the screen
                }
              }
            });

    // Add KeyFrame to Timeline
    timeline.getKeyFrames().add(kf);
    timeline.setCycleCount(Animation.INDEFINITE); // Makes the animation run indefinitely

    // Start the Timeline
    timeline.play();
  }

  /**
   * Handles clicking on game objects in the room.
   *
   * @param event MouseEvent for clicking an object.
   */
  @FXML
  private void objectClicked(MouseEvent event) throws IOException {
    Node source = (Node) event.getSource();
    String id = source.getId();
    new MouseClick().play();
    // if candles
    if (id.equals("candle1")
        || id.equals("candle2")
        || id.equals("candle3")
        || id.equals("candle4")) {
      GameState.inventory.setTextChat("You Need A Lighter");
      return;
    }
    // if its a brickwall
    if (id.equals("brickWall")) {
      GameState.inventory.setTextChat("You Need A Hammer");
    }
    // if its a door
    if (id.equals("rightDoor")) {

      GameState.currentRoom.set(Rooms.MAINROOM);
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) {
      // when puzzles are turned off

      Rectangle rectangle = (Rectangle) source;
      vibrate(rectangle);

    } else if (source.getId().equals("pipeGame")) {
      // if its pipgame
      GameState.currentPuzzle.set(Puzzle.PIPECONNECTING);
    } else if (source instanceof ImageView) {
      // if its a imageView
      System.out.println("unbinded" + source.getId());
      source.visibleProperty().unbind();

      GameState.inventory.onRegularItemClicked(event);
    }
  }

  /**
   * Turn Object Blue if imageView otherwise show clickable
   *
   * @param event MouseEvent for turning object blue or showing clickable
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    Node source = (Node) event.getSource();
    ColorAdjust colorAdjust = new ColorAdjust();
    if (source instanceof ImageView) {
      // Make it really blue when hovered over
      ImageView targetImageView = (ImageView) event.getSource();
      colorAdjust.setHue(1);
      // Max hue
      colorAdjust.setSaturation(1);
      // Max saturation
      targetImageView.setEffect(colorAdjust);
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) {
      // when puzzles are turned off turn red

      source.setOpacity(0.22);
      Rectangle rectangle = (Rectangle) source;
      rectangle.setFill(Color.RED);

    } else {
      source.setOpacity(0.22);
    }
  }

  /**
   * Removes blue tint from object
   *
   * @param event MouseEvent for turning object blue
   */
  @FXML
  private void onMouseExited(MouseEvent event) {
    Node source = (Node) event.getSource();

    if (source instanceof ImageView) {
      ImageView targetImageView = (ImageView) source;
      targetImageView.setEffect(null);
      // Remove the blue tint
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) {
      // when the puzzles are turned off turn red

      Rectangle rectangle = (Rectangle) source;
      rectangle.setFill(Color.web("#1F85FF"));
      source.setOpacity(0);

    } else {
      source.setOpacity(0);
      // Make the node invisible
    }
  }

  /**
   * Allows drag-and-drop actions to proceed.
   *
   * @param event DragEvent for dragging over a target.
   */
  @FXML
  private void onDragOver(DragEvent event) {
    GameState.inventory.onDragOver(event);
  }

  /**
   * Resets drag-and-drop effects when the drag action exits the target area.
   *
   * @param event DragEvent for exiting the target area.
   */
  @FXML
  private void onDragExited(DragEvent event) {
    // gets source node
    Node node = (Node) event.getSource();

    if (!GameState.isPuzzlesOn.getValue()
        // if it contains the id and rectangle return
        && GameState.puzzleName.contains(node.getId())
        && node instanceof Rectangle) {
      return;
    }
    // go to inventory and do its logic
    GameState.inventory.onDragExited(event);
  }

  /**
   * Completes drag-and-drop functionality for inventory items.
   *
   * @param event DragEvent for dropping an item.
   */
  @FXML
  private void onDragDropped(DragEvent event) {
    // when its dropped
    Node node = (Node) event.getSource();
    if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(node.getId())
        && node instanceof Rectangle) {
      // if its a rectangle
      Rectangle rectangle = (Rectangle) node;

      vibrate(rectangle);
      // keyframe for logic
      KeyFrame keyFrame =
          new KeyFrame(
              Duration.millis(500),
              e -> {
                rectangle.setOpacity(0);
                rectangle.setFill(Color.web("#1F85FF"));
              });

      Timeline timeline = new Timeline(keyFrame);
      timeline.play();

      return;
    }
    // use inventory logic
    GameState.inventory.onDragDropped(event, GameState.currentRoomItems);
    // if wall count is 0
    if (GameState.wallCount <= 0) {
      background.setImage(new Image("/images/puzzleroom/openwallroom.png"));
      GameState.wallRemoved = true;
      System.out.println("Wall Removed");
      GameState.isOpenWall = true;
      candle1.setVisible(true);
      candle2.setVisible(true);
      candle3.setVisible(true);
      candle4.setVisible(true);
      // if its 2
    } else if (GameState.wallCount == 2) {
      background.setImage(new Image("/images/puzzleroom/crack1.png"));
    } else if (GameState.wallCount == 1) {
      // if its 1
      background.setImage(new Image("/images/puzzleroom/crack2.png"));
    }
    if (node.getUserData() != null && node.getUserData().equals("candle")) {
      // if its a candle
      System.out.println(checkCandleGame());
      System.out.println(GameState.candleOrder);
      // check candlegame
      if (checkCandleGame()) {
        GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING).set(true);
        System.out.println("Complete");
        GameState.candlePuzzleSolved = true;
        GameState.isPuzzlesOn.set(false);
        // logic for when u solve the candle
        GameState.riddleRoomController.turnLightsOn();
        GameState.gameMasterActions.activate("Attention! You are to solve my riddle...");
        new Thread(
                () -> {
                  TextToSpeech textToSpeech = TextToSpeech.getInstance();
                  GameState.speechList.add(textToSpeech);
                  textToSpeech.speak("Attention! You are to solve my riddle...");
                })
            .start();
        System.out.println("Lights off");
      } else {
        // set it to false
        GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING).set(false);
      }
    }
  }

  /**
   * Sets the current puzzle state to 'PADLOCK' when called.
   *
   * @param event The ActionEvent that triggered this method.
   */
  @FXML
  private void onPadlock(ActionEvent event) {
    new MouseClick().play();
    GameState.currentPuzzle.set(Puzzle.PADLOCK);
  }

  /**
   * Checks if the candle game is complete based on the current state.
   *
   * @return true if the candle game is complete, false otherwise.
   */
  private boolean checkCandleGame() {

    for (int i = 0; i < GameState.candleOrder.size(); i++) {
      Candle candle = (Candle) GameState.currentRoomItems.get(candles.get(i));

      if (GameState.candleOrder.get(i) != candle.isLit()) {
        return false;
      }
    }

    return true;
  }

  /**
   * Drops a given Node by a specified amount.
   *
   * @param node The Node to be dropped.
   */
  private void drop(Node node) {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(node);
    translate.setDuration(Duration.millis(1000));
    translate.setByY(200);
    translate.play();
  }

  /**
   * Vibrates a given Node to simulate shaking.
   *
   * @param node The Node to be vibrated.
   */
  public void vibrate(Node node) {
    TranslateTransition tt = new TranslateTransition(Duration.millis(15), node);
    tt.setFromX(0f);
    tt.setByX(5f);
    tt.setCycleCount(4);
    tt.setAutoReverse(true);
    tt.play();
  }
}
