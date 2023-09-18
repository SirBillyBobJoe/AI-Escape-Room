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
import nz.ac.auckland.se206.Items.Candle;
import nz.ac.auckland.se206.Items.Hammer;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class PuzzleRoomController {
  @FXML private ImageView background;
  @FXML private ImageView candle1, candle2, candle3, candle4;
  @FXML private ImageView hammer;
  @FXML private ImageView greenWire;
  @FXML private CubicCurve riddleGlow1;
  @FXML private ImageView waterdrop, puddle;

  private List<ImageView> candles;
  boolean isOpenWall = false;

  @FXML
  public void initialize() {

    GameState.riddleGlow1 = riddleGlow1;
    candles = new ArrayList<ImageView>();
    candles.add(candle1);
    candles.add(candle2);
    candles.add(candle3);
    candles.add(candle4);
    candle1.setUserData("candle");
    candle2.setUserData("candle");
    candle3.setUserData("candle");
    candle4.setUserData("candle");
    GameState.currentRoomItems.put(greenWire, GameState.greenWire);
    GameState.currentRoomItems.put(candle1, new Candle());
    GameState.currentRoomItems.put(candle2, new Candle());
    GameState.currentRoomItems.put(candle3, new Candle());
    GameState.currentRoomItems.put(candle4, new Candle());

    hammer.setUserData("hammer");
    GameState.currentRoomItems.put(hammer, new Hammer());
    hammer.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PIPEPUZZLE));

    greenWire.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING));
    puddle.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PIPEPUZZLE).not());
    waterdrop.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PIPEPUZZLE).not());
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
              double deltaY = 1; // Set how much the image will move in each frame

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
    if (id.equals("rightDoor")) {

      GameState.currentRoom.set(Rooms.MAINROOM);
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) { // when puzzles are turned off

      Rectangle rectangle = (Rectangle) source;
      vibrate(rectangle);

    } else if (source.getId().equals("pipeGame")) {
      GameState.currentPuzzle.set(Puzzle.PIPEPUZZLE);
    } else if (source instanceof ImageView) {
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
      colorAdjust.setHue(1); // Max hue
      colorAdjust.setSaturation(1); // Max saturation
      targetImageView.setEffect(colorAdjust);
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) { // when puzzles are turned off turn red

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
      targetImageView.setEffect(null); // Remove the blue tint
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) { // when the puzzles are turned off turn red

      Rectangle rectangle = (Rectangle) source;
      rectangle.setFill(Color.web("#1F85FF"));
      source.setOpacity(0);

    } else {
      source.setOpacity(0); // Make the node invisible
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
    Node node = (Node) event.getSource();
    if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(node.getId())
        && node instanceof Rectangle) {
      return;
    }
    GameState.inventory.onDragExited(event);
  }

  /**
   * Completes drag-and-drop functionality for inventory items.
   *
   * @param event DragEvent for dropping an item.
   */
  @FXML
  private void onDragDropped(DragEvent event) {
    Node node = (Node) event.getSource();
    if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(node.getId())
        && node instanceof Rectangle) {

      Rectangle rectangle = (Rectangle) node;

      vibrate(rectangle);

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
    GameState.inventory.onDragDropped(event, GameState.currentRoomItems);

    if (GameState.wallCount <= 0) {
      background.setImage(new Image("/images/puzzleroom/openwallroom.png"));
      GameState.wallRemoved = true;
      System.out.println("Wall Removed");
      isOpenWall = true;
      candle1.setVisible(true);
      candle2.setVisible(true);
      candle3.setVisible(true);
      candle4.setVisible(true);
    } else if (GameState.wallCount == 2) {
      background.setImage(new Image("/images/puzzleroom/crack1.png"));
    } else if (GameState.wallCount == 1) {
      background.setImage(new Image("/images/puzzleroom/crack2.png"));
    }
    if (node.getUserData() != null && node.getUserData().equals("candle")) {
      System.out.println(checkCandleGame());
      System.out.println(GameState.candleOrder);
      if (checkCandleGame()) {
        GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING).set(true);
        System.out.println("Complete");
        GameState.candlePuzzleSolved = true;
        GameState.isPuzzlesOn.set(false);
        GameState.riddleRoomController.turnLightsOn();
        GameState.gameMasterActions.activate("Attention! You are to solve my riddle...");
        TextToSpeech textToSpeech = TextToSpeech.getInstance();
        textToSpeech.speak("Attention! You are to solve my riddle...");
        System.out.println("Lights off");
      } else {
        GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING).set(false);
      }
    }
  }

  @FXML
  private void onPadlock(ActionEvent event) {
    new MouseClick().play();
    GameState.currentPuzzle.set(Puzzle.PADLOCK);
  }

  private boolean checkCandleGame() {

    for (int i = 0; i < GameState.candleOrder.size(); i++) {
      Candle candle = (Candle) GameState.currentRoomItems.get(candles.get(i));

      if (GameState.candleOrder.get(i) != candle.isLit()) {
        return false;
      }
    }

    return true;
  }

  private void drop(Node node) {
    TranslateTransition translate = new TranslateTransition();
    translate.setNode(node);
    translate.setDuration(Duration.millis(1000));
    translate.setByY(200);
    translate.play();
  }

  public void vibrate(Node node) {
    TranslateTransition tt = new TranslateTransition(Duration.millis(15), node);
    tt.setFromX(0f);
    tt.setByX(5f);
    tt.setCycleCount(4);
    tt.setAutoReverse(true);
    tt.play();
  }
}
