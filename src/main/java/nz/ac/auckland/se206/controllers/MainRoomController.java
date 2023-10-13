package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.items.Keys;
import nz.ac.auckland.se206.items.Lighter;
import nz.ac.auckland.se206.items.Lock;

/**
 * Controller class for Room 1 in the escape room game. Manages the UI elements and interactions for
 * the main room.
 */
public class MainRoomController {
  @FXML private ImageView key1;
  @FXML private ImageView lighter1;
  @FXML private ImageView lock1;
  @FXML private ImageView blueWire;
  @FXML private ImageView redWire;
  @FXML private ImageView background;
  @FXML private Rectangle rightDoor;
  @FXML private Rectangle wireBox;
  @FXML private Rectangle passcode;
  @FXML private CubicCurve leftDoor;
  @FXML private CubicCurve exitDoor;
  @FXML private Rectangle riddleGlow;
  @FXML private Rectangle hide1;
  @FXML private Rectangle hide2;

  /** Initializes Room 1, binding the UI to the game state and setting up chat context. */
  public void initialize() {

    lock1.setUserData("lock");
    // bind locks visibility to solving the padlock puzzle
    lock1.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PADLOCK).not());
    // binds the keys visibility to solving the wire game
    // Bind the visible property to some condition (in your case, puzzle solved)
    key1.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.WIRELINKING));

    // Add a listener to the visible property
    key1.visibleProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) { // Check if the new value of the property is 'true'
                drop(key1);
              }
            });

    wireBox.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.WIRELINKING).not());
    passcode.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PASSCODE).not());
    // Add a listener to the visible property
    passcode
        .visibleProperty()
        .addListener(
            (observable1, oldValue1, newValue1) -> {
              System.out.println("Passcode visibility changed to: " + newValue1); // Debug print
              if (!newValue1) { // Check if the new value of the property is 'true'
                background.setImage(new Image("/images/mainRoom/openChest.png"));
                blueWire.setVisible(true);
                GameState.chestPuzzleSolved = true;
              }
            });

    // initialise objects in room 1 into HashMap
    GameState.currentRoomItems.put(key1, new Keys(1));
    GameState.currentRoomItems.put(lighter1, new Lighter());
    GameState.currentRoomItems.put(lock1, new Lock(1));

    GameState.riddleGlow = riddleGlow;
    GameState.mainRoom = background;
    GameState.currentRoomItems.put(redWire, GameState.redWire);
    GameState.currentRoomItems.put(blueWire, GameState.blueWire);
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
    if (!GameState.isPuzzlesOn.getValue() && GameState.puzzleName.contains(source.getId())) {
      // when puzzles are turned off
      GameState.inventory.setTextChat("You need to solve the riddle");
      vibrate(source);

    } else if (source instanceof ImageView) {

      // sets found for the items
      if (id.equals("key1")) {
        GameState.isKeyFound = true;
        source.visibleProperty().unbind();
      } else if (id.equals("lighter1")) {
        GameState.isLighterFound = true;
      } else if (id.equals("redWire")) {
        GameState.isRedWire = true;
      } else if (id.equals("blueWire")) {
        GameState.isBlueWire = true;
      }

      if (id.equals("lock1") && !(((Lock) GameState.currentRoomItems.get(lock1)).isLocked())) {

        GameState.currentPuzzle.set(Puzzle.PADLOCK);
        return;
      }
      GameState.inventory.onRegularItemClicked(event);

    } else if (id.equals("leftDoor")) {
      // if click on the left door

      GameState.currentRoom.set(Rooms.PUZZLEROOM);

    } else if (id.equals("rightDoor")) {
      // if click on the right door

      GameState.currentRoom.set(Rooms.RIDDLEROOM);

    } else if (id.equals("exitDoor")) {
      // if clicked on the centre exit door
      if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) {
        GameState.timer.stop();
        GameState.escaped = true;
        SceneManager.setReinitialise(AppUi.ENDSCREEN);
        App.setUserInterface(AppUi.ENDSCREEN);
      } else {
        GameState.inventory.onRegularItemClicked(event);
      }
      // if its a wirebox
    } else if (id.equals("wireBox")) {
      GameState.currentPuzzle.set(Puzzle.WIRELINKING);
    } else if (id.equals("candlePainting")) {
      // if its a painting
      GameState.currentPuzzle.set(Puzzle.CANDLEPAINTING);
    } else if (id.equals("passcode")) {
      // if its a passcode
      GameState.currentPuzzle.set(Puzzle.PASSCODE);
    } else if (id.equals("hide1") || id.equals("hide2")) {
      source.setVisible(false);
      if (hide2.isVisible() == false) {
        GameState.lighter = true;
      }
      if (hide1.isVisible() == false) {
        GameState.wire = true;
      }
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

    // when u drop it checks values
    if (!GameState.isPuzzlesOn.getValue() && GameState.puzzleName.contains(node.getId())) {
      GameState.inventory.setTextChat("You need to solve the riddle");
      vibrate(node);
      // gets keyframe for delays
      KeyFrame keyFrame =
          new KeyFrame(
              Duration.millis(500),
              e -> {
                node.setEffect(null);
              });

      Timeline timeline = new Timeline(keyFrame);
      timeline.play();

      return;
    }
    GameState.inventory.onDragDropped(event, GameState.currentRoomItems);
  }

  /**
   * Turn Object Blue if imageView otherwise show clickable.
   *
   * @param event MouseEvent for turning object blue or showing clickable
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    Node source = (Node) event.getSource();
    String id = source.getId();
    ColorAdjust colorAdjust = new ColorAdjust();
    if (!GameState.isPuzzlesOn.getValue() && GameState.puzzleName.contains(source.getId())) {
      // when puzzles are turned off turn red
      if (source instanceof Rectangle) {
        // if its a rectangle

        if (!(id.equals("hide1") || id.equals("hide2"))) {
          source.setOpacity(0.22);
        }
        Rectangle rectangle = (Rectangle) source;
        rectangle.setFill(Color.RED);
      } else {
        colorAdjust.setHue(-0.5);
        // Max hue
        colorAdjust.setSaturation(1);
        // Max saturation
        source.setEffect(colorAdjust);
      }

    } else if (source instanceof ImageView) {
      // Make it really blue when hovered over
      ImageView targetImageView = (ImageView) event.getSource();
      colorAdjust.setHue(1);
      // Max hue
      colorAdjust.setSaturation(1);
      // Max saturation
      targetImageView.setEffect(colorAdjust);
    } else if (id.equals("hide1") || id.equals("hide2")) {
      Rectangle rectangle = (Rectangle) source;
      rectangle.setFill(Color.web("#1F85FF"));
    } else {
      source.setOpacity(0.22);
    }
  }

  /**
   * Removes blue tint from object.
   *
   * @param event MouseEvent for turning object blue
   */
  @FXML
  private void onMouseExited(MouseEvent event) {
    Node source = (Node) event.getSource();
    String id = source.getId();
    if (source instanceof ImageView) {
      ImageView targetImageView = (ImageView) source;
      targetImageView.setEffect(null);
      // Remove the blue tint
    } else if (!GameState.isPuzzlesOn.getValue()
        && GameState.puzzleName.contains(source.getId())
        && source instanceof Rectangle) {
      // when the puzzles are turned off turn red

      Rectangle rectangle = (Rectangle) source;

      if (!(id.equals("hide1") || id.equals("hide2"))) {
        rectangle.setFill(Color.web("#1F85FF"));
        source.setOpacity(0);
      } else {
        rectangle.setFill(Color.web("#0c0f15"));
      }
      // if its hide1 and hide2
    } else if (id.equals("hide1") || id.equals("hide2")) {
      Rectangle rectangle = (Rectangle) source;
      rectangle.setFill(Color.web("#0c0f15"));
    } else {
      source.setOpacity(0);
      // Make the node invisible
    }
  }

  /**
   * Drops the given Node vertically by 200 units.
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
   * Vibrates the given Node horizontally, moving it back and forth.
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
