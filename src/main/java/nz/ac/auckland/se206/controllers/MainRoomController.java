package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Items.Hammer;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.Items.Keys;
import nz.ac.auckland.se206.Items.Lighter;
import nz.ac.auckland.se206.Items.Lock;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.gpt.GameMaster;

/**
 * Controller class for Room 1 in the escape room game. Manages the UI elements and interactions for
 * the main room.
 */
public class MainRoomController {
  @FXML private ImageView key1;
  @FXML private ImageView lighter1;
  @FXML private ImageView lock1;
  @FXML private ImageView blueWire, redWire;
  @FXML private ImageView hammer;
  @FXML private Rectangle rightDoor, wireBox;
  @FXML private CubicCurve leftDoor, exitDoor;
  @FXML private Rectangle riddleGlow;

  /** Initializes Room 1, binding the UI to the game state and setting up chat context. */
  public void initialize() {
    String hint;

    if (GameState.hints.get().equals("\u221E")) {
      hint = "infinite";
    } else {
      hint = GameState.hints.get();
    }
    // room1 chat context
    GameState.gameMaster.createChatContext("room1");
    String gptMsg =
        "This game the player will have "
            + hint
            + " hints. You are the Game Master Of An Escape Room currently we are in room 1. Here"
            + " is some answers to the hints. The scroll is under the car, Switch is next to the"
            + " chains, light is next to the car, riddle answer is chicken, u need key to unlock"
            + " the door. Don't reply to this message reply but reply to following messages. Only"
            + " give one hint at a time";
    GameState.gameMaster.addMessage("room1", "user", gptMsg);
    GameState.gameMaster.runContext("room1");

    hammer.setUserData("hammer");
    lock1.setUserData("lock");
    // bind locks visibility to solving the padlock puzzle
    lock1.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.PADLOCK).not());
    // binds the keys visibility to solving the wire game
    key1.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.WIREPUZZLE));

    wireBox.visibleProperty().bind(GameState.puzzleSolved.get(Puzzle.WIREPUZZLE).not());
    // initialise objects in room 1 into HashMap
    GameState.currentRoomItems.put(key1, new Keys(1));
    GameState.currentRoomItems.put(lighter1, new Lighter());
    GameState.currentRoomItems.put(lock1, new Lock(1));

    GameState.riddleGlow = riddleGlow;
    GameState.currentRoomItems.put(redWire, GameState.redWire);
    GameState.currentRoomItems.put(blueWire, GameState.blueWire);
    GameState.currentRoomItems.put(hammer, new Hammer());
  }

  /**
   * Resets the game state and navigates back to the start screen.
   *
   * @param event MouseEvent for the restart button.
   * @throws IOException If the FXML for the start screen can't be loaded.
   */
  @FXML
  private void onRestart(MouseEvent event) throws IOException {
    new MouseClick().play();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    App.setUserInterface(AppUi.STARTSCREEN);
    double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
    double additionalHeight = stage.getHeight() - stage.getScene().getHeight();
    stage.setWidth(800 + additionalWidth);
    stage.setHeight(600 + additionalHeight);
    GameState.timer.stop();

    GameState.inventory = new Inventory();

    GameState.gameMaster = new GameMaster();
    GameState.chat.restart();
    SceneManager.setReinitialise(AppUi.UIOVERLAY);
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
    if (source instanceof ImageView) {
      System.out.println("unbinded" + source.getId());
      source.visibleProperty().unbind();
      if (id.equals("lock1") && !(((Lock) GameState.currentRoomItems.get(lock1)).isLocked())) {

        GameState.currentPuzzle.set(Puzzle.PADLOCK);
        return;
      }
      GameState.inventory.onRegularItemClicked(event);

    } else if (id.equals("leftDoor")) { // if click on the left door

      GameState.currentRoom.set(Rooms.PUZZLEROOM);

    } else if (id.equals("rightDoor")) { // if click on the right door

      GameState.currentRoom.set(Rooms.RIDDLEROOM);

    } else if (id.equals("exitDoor")) { // if clicked on the centre exit door
      if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) {
        GameState.timer.stop();
        GameState.escaped = true;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneManager.setReinitialise(AppUi.ENDSCREEN);
        App.setUserInterface(AppUi.ENDSCREEN);
        double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
        double additionalHeight = stage.getHeight() - stage.getScene().getHeight();
        stage.setWidth(800 + additionalWidth);
        stage.setHeight(600 + additionalHeight);
      } else {
        GameState.inventory.onRegularItemClicked(event);
      }

    } else if (id.equals("wireBox")) {
      GameState.currentPuzzle.set(Puzzle.WIREPUZZLE);
    } else if (id.equals("candlePainting")) {
      GameState.currentPuzzle.set(Puzzle.CANDLEPAINTING);
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
    GameState.inventory.onDragDropped(event, GameState.currentRoomItems);
  }

  /**
   * Turn Object Blue if imageView otherwise show clickable
   *
   * @param event MouseEvent for turning object blue or showing clickable
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    Node source = (Node) event.getSource();
    if (source instanceof ImageView) {
      // Make it really blue when hovered over
      ImageView targetImageView = (ImageView) event.getSource();
      ColorAdjust colorAdjust = new ColorAdjust();
      colorAdjust.setHue(1); // Max hue
      colorAdjust.setSaturation(1); // Max saturation
      targetImageView.setEffect(colorAdjust);
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
    } else {
      source.setOpacity(0); // Make the node invisible
    }
  }
}
