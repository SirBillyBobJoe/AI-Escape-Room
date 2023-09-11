package nz.ac.auckland.se206.controllers;

import java.io.IOException;
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
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Rooms;

public class PuzzleRoomController {
  @FXML ImageView background;
  @FXML ImageView candle1, candle2, candle3, candle4;

  boolean isOpenWall = false;

  public void initialize() {
    candle1.setUserData("candle");
    candle2.setUserData("candle");
    candle3.setUserData("candle");
    candle4.setUserData("candle");
    GameState.currentRoomItems.put(candle1, new Candle());
    GameState.currentRoomItems.put(candle2, new Candle());
    GameState.currentRoomItems.put(candle3, new Candle());
    GameState.currentRoomItems.put(candle4, new Candle());
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
   * Changes to the wall
   *
   * @param event ActionEvent for changing the wall.
   */
  @FXML
  private void pressButton(ActionEvent event) {
    if (isOpenWall) {
      background.setImage(new Image("/images/puzzleroom/room.png"));
      isOpenWall = false;
      candle1.setVisible(false);
      candle2.setVisible(false);
      candle3.setVisible(false);
      candle4.setVisible(false);
    } else {
      background.setImage(new Image("/images/puzzleroom/openwallroom.png"));
      isOpenWall = true;
      candle1.setVisible(true);
      candle2.setVisible(true);
      candle3.setVisible(true);
      candle4.setVisible(true);
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
}
