package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Items.Candle;
import nz.ac.auckland.se206.Items.Hammer;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;

public class PuzzleRoomController {
  @FXML ImageView background;
  @FXML ImageView candle1, candle2, candle3, candle4;
  @FXML ImageView hammer;
  @FXML private ImageView greenWire;
  private List<ImageView> candles;
  boolean isOpenWall = false;

  public void initialize() {
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
    Node node = (Node) event.getSource();
    if (GameState.wallCount <= 0) {
      background.setImage(new Image("/images/puzzleroom/openwallroom.png"));
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
      } else {
        GameState.puzzleSolved.get(Puzzle.CANDLEPAINTING).set(false);
      }
    }
  }

  @FXML
  private void onPipe(MouseEvent event) {
    new MouseClick().play();
    GameState.currentPuzzle.set(Puzzle.PIPEPUZZLE);
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
}
