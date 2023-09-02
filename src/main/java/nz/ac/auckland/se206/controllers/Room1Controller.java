package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.Items.Keys;
import nz.ac.auckland.se206.Items.Lighter;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SharedChat;
import nz.ac.auckland.se206.gpt.GameMaster;

/**
 * Controller class for Room 1 in the escape room game. Manages the UI elements and interactions for
 * Room 1.
 */
public class Room1Controller {
  @FXML private Label countdownLabel;
  @FXML private Label hintLabel;
  @FXML private ImageView restart;
  @FXML private ImageView item0, item1, item2, item3, item4, item5;
  @FXML private ImageView key1, key2, key3;
  @FXML private ImageView lighter1, lighter2, lighter3;
  @FXML private TextArea textArea;
  @FXML private TextField textField;

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
    GameState.gameMaster.addMessage(
        "room1",
        "user",
        "You are the Game Master Of An Escape Room currently we are in room 1. If the hints left is"
            + " 0 and the player asks anythign questions needing help in any form YOU MUST NOT GIVE"
            + " ANY HINTS. This game the player will have "
            + hint
            + " hints");
    GameState.gameMaster.runContext("room1");

    item0.setUserData(0); // Index 0
    item1.setUserData(1); // Index 1
    item2.setUserData(2); // Index 2
    item3.setUserData(3); // Index 3
    item4.setUserData(4); // Index 4
    item5.setUserData(5); // Index 5
    countdownLabel.textProperty().bind(GameState.timer.timeSecondsProperty().asString());
    hintLabel.textProperty().bind(GameState.hints);
    ImageView[] images = {item0, item1, item2, item3, item4, item5};

    GameState.inventory
        .inventoryProperty()
        .addListener(
            (obs, oldInventory, newInventory) -> {
              for (int i = 0; i < images.length; i++) {
                if (i < newInventory.size()) {
                  images[i].setImage(newInventory.get(i).getImage());
                } else {
                  images[i].setImage(null);
                }
              }
            });

    // binds the text areas of the 2 controllers together
    GameState.sharedChat = SharedChat.getInstance();
    textArea.textProperty().bind(GameState.sharedChat.getTextProperty());
    textArea.setWrapText(true);
  }

  /**
   * Resets the game state and navigates back to the start screen.
   *
   * @param event MouseEvent for the restart button.
   * @throws IOException If the FXML for the start screen can't be loaded.
   */
  @FXML
  private void onRestart(MouseEvent event) throws IOException {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    App.setUserInterface(AppUi.SCREEN_START);
    stage.setWidth(630);
    stage.setHeight(630);
    GameState.timer.stop();

    GameState.inventory = new Inventory();

    GameState.gameMaster = new GameMaster();
    GameState.sharedChat.restart();
    SceneManager.setReinitialise(AppUi.ROOM1);
  }

  /**
   * Updates the restart button's image when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the restart button.
   */
  @FXML
  private void leaveRestart(MouseEvent event) {
    restart.setImage(new Image("/images/room1/restartBlue.png"));
  }

  /**
   * Updates the restart button's image when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the restart button.
   */
  @FXML
  private void overRestart(MouseEvent event) {
    restart.setImage(new Image("/images/room1/restartGreen.png"));
  }

  /**
   * Handles clicking on game objects in the room.
   *
   * @param event MouseEvent for clicking an object.
   */
  @FXML
  private void objectClicked(MouseEvent event) {

    ImageView imageView = (ImageView) event.getSource();
    imageView.setVisible(false);
    if (imageView == key1) {
      GameState.inventory.addObject(new Keys(1));
      return;
    } else if (imageView == key2) {
      GameState.inventory.addObject(new Keys(2));
      return;
    } else if (imageView == key3) {
      GameState.inventory.addObject(new Keys(3));
      return;
    } else if (imageView == lighter1) {
      GameState.inventory.addObject(new Lighter());
      return;
    } else if (imageView == lighter2) {
      GameState.inventory.addObject(new Lighter());
      return;
    } else if (imageView == lighter3) {
      GameState.inventory.addObject(new Lighter());
      return;
    }
  }

  /**
   * Initiates drag-and-drop functionality for inventory items.
   *
   * @param event MouseEvent for starting a drag-and-drop.
   */
  @FXML
  private void onDragDetected(MouseEvent event) {
    GameState.inventory.onDragDetected(event);
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
    GameState.inventory.onDragDropped(event);
  }

  /**
   * Handles text message sending for the chat feature in Room 1.
   *
   * @param event ActionEvent for sending a message.
   */
  @FXML
  private void onSend(ActionEvent event) {
    GameState.sharedChat.onSend(textField, "room1");
  }
}
