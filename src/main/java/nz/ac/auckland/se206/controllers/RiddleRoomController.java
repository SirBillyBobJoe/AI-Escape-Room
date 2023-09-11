package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Rooms;

public class RiddleRoomController {
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
    if (id.equals("leftDoor")) {

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
}
