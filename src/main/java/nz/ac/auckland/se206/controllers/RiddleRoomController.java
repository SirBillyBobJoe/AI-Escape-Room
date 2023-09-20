package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;

public class RiddleRoomController {
  @FXML private Rectangle leftDoor;
  @FXML private Rectangle computer;
  @FXML private ImageView imgRoom;

  // Brightness changes for the other rooms
  private ColorAdjust colorAdjust = new ColorAdjust();

  public void initialize() {
    GameState.riddleRoomController = this;
  }

  /** Turns the lights on in the riddle room */
  @FXML
  public void turnLightsOn() {
    // changes the lights in the rom
    imgRoom.setImage(new Image("/images/riddleRoom/riddleRoomLight.png"));
    // activates the riddles
    GameState.riddleRoomActive.set(true);
    GameState.setRiddleGlow();
    GameState.isPuzzlesOn.set(false);
    // cahnge brightness
    colorAdjust.setBrightness(-0.5);
    GameState.mainRoom.setEffect(colorAdjust);
    GameState.puzzleRoom.setEffect(colorAdjust);
  }

  /** Turns the lights off in the riddle room */
  @FXML
  public void turnLightsOff() {
    // gets the image for dark and turns it off
    imgRoom.setImage(new Image("/images/riddleRoom/riddleRoomDark.png"));
    GameState.riddleRoomActive.set(false);
    // set the riddle glow
    GameState.setRiddleGlow();
    colorAdjust.setBrightness(0);
    // adjust colors
    GameState.mainRoom.setEffect(colorAdjust);
    GameState.puzzleRoom.setEffect(colorAdjust);
  }

  /**
   * Handles clicking on game objects in the room.
   *
   * @param event MouseEvent for clicking an object.
   */
  @FXML
  private void objectClicked(MouseEvent event) throws IOException {
    // gets sourcenode
    Node source = (Node) event.getSource();
    String id = source.getId();
    new MouseClick().play();
    // if its the leftdoor go to main room
    if (id.equals("leftDoor")) {

      GameState.currentRoom.set(Rooms.MAINROOM);
    } else if (id.equals("computer")) {
      // if its the computer go to the computerscreen
      if (GameState.riddleRoomActive.getValue()) {
        GameState.currentPuzzle.set(Puzzle.COMPUTERSCREEN);
      }
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
      // Everything but the door shouldn't be hoverable if the riddle room is inactive
      if (source == computer) {
        if (GameState.riddleRoomActive.getValue()) {
          source.setOpacity(0.22);
        }
      } else {
        source.setOpacity(0.22);
      }
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
    } else {
      source.setOpacity(0);
      // Make the node invisible
    }
  }
}
