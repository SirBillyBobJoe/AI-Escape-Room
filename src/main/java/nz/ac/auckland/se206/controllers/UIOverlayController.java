package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.SharedChat;
import nz.ac.auckland.se206.gpt.GameMaster;

/**
 * Controller class for Room 1 in the escape room game. Manages the UI elements and interactions for
 * Room 1.
 */
public class UIOverlayController {
  @FXML private AnchorPane mainPane;
  @FXML private Label countdownLabel;
  @FXML private Label hintLabel;
  @FXML private ImageView restart;
  @FXML private ImageView item0, item1, item2, item3, item4, item5;
  @FXML private TextArea textArea;
  @FXML private TextField textField;
  @FXML private TextArea itemChat;
  @FXML private TextArea txaGameMaster;
  @FXML private ImageView imgGameMaster;
  GameMasterActions gameMaster = new GameMasterActions();

  @FXML private Pane replacePane; // Must remain so it can be swapped at the start
  private Pane loadedRoom;

  /** Initializes Room 1, binding the UI to the game state and setting up chat context. */
  public void initialize() {
    String hint;

    gameMaster = new GameMasterActions(imgGameMaster, txaGameMaster);

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
    System.out.println(gptMsg);

    countdownLabel.textProperty().bind(GameState.timer.timeSecondsProperty().asString());
    hintLabel.textProperty().bind(GameState.hints);

    // controls for inventory indexing with the images
    ImageView[] images = {item0, item1, item2, item3, item4, item5};
    item0.setUserData(0); // Index 0
    item1.setUserData(1); // Index 1
    item2.setUserData(2); // Index 2
    item3.setUserData(3); // Index 3
    item4.setUserData(4); // Index 4
    item5.setUserData(5); // Index 5
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
    GameState.inventory.setItemChat(itemChat);
    // end of inventory initialising

    // binds the text areas of the 2 controllers together
    GameState.chat = SharedChat.getInstance();
    textArea.textProperty().bind(GameState.chat.getTextProperty());
    textArea.setWrapText(true);
    // Listen for changes in the textProperty of the textArea
    GameState.chat
        .getTextProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Scroll to the bottom in the JavaFX Application Thread
              Platform.runLater(
                  () -> {
                    textArea.setScrollTop(Double.MAX_VALUE);
                  });
            });
    // Load room 1 internal
    GameState.currentRoom.addListener(
        new ChangeListener<Rooms>() {
          @Override
          public void changed(ObservableValue o, Rooms oldVal, Rooms newVal) {
            changeRoom(newVal);
          }
        });
    GameState.currentRoom.set(Rooms.MAINROOM);
    changeRoom(Rooms.MAINROOM);
  }

  private void changeRoom(Rooms room) {
    loadedRoom = SceneManager.getRoomPane(room);
    mainPane.getChildren().set(0, loadedRoom);
  }

  // DELETEME
  private boolean justActivated = false;

  @FXML
  private void gameMasterClicked(MouseEvent event) {
    if (!justActivated) {
      justActivated = true;
      gameMaster.activate(
          "Hello, I am the game master. Do not defy me. blah blah blhaHello, I am the game"
              + " master.");
    } else {
      gameMaster.unactivate();
      justActivated = false;
    }
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
    SceneManager.setReinitialise(AppUi.UIOVERLAY);
    App.setUserInterface(AppUi.SCREENSTART);
    stage.setWidth(630);
    stage.setHeight(630);
    GameState.timer.stop();

    GameState.inventory = new Inventory();

    GameState.gameMaster = new GameMaster();
    GameState.chat.restart();
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
    GameState.inventory.onDragDropped(event, GameState.currentRoomItems);
  }

  /**
   * Handles text message sending for the chat feature in Room 1.
   *
   * @param event ActionEvent for sending a message.
   */
  @FXML
  private void onSend(ActionEvent event) {
    GameState.chat.onSend(textField, "room1");
  }

  /**
   * Turn Object Blue
   *
   * @param event MouseEvent for turning object blue
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    // Make it really blue when hovered over
    ImageView targetImageView = (ImageView) event.getSource();
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setHue(1); // Max hue
    colorAdjust.setSaturation(1); // Max saturation
    targetImageView.setEffect(colorAdjust);
  }

  /**
   * Removes blue tint from object
   *
   * @param event MouseEvent for turning object blue
   */
  @FXML
  private void onMouseExited(MouseEvent event) {
    // Remove the blue tint after dropping
    ImageView targetImageView = (ImageView) event.getSource();
    targetImageView.setEffect(null);
  }

  /**
   * Handles events when we click on the inventory Items
   *
   * @param event MouseEvent for turning object blue
   */
  @FXML
  private void onInventoryClicked(MouseEvent event) {
    new MouseClick().play();
    GameState.inventory.onInventoryClicked(event);
  }

  /**
   * Handles button events for escaping
   *
   * @param event ActionEvent for button click
   * @throws IOException
   */
  @FXML
  private void onEscape(ActionEvent event) throws IOException {
    GameState.timer.stop();
    GameState.escaped = true;
    SceneManager.setReinitialise(AppUi.ENDSCREEN);
    App.setUserInterface(AppUi.ENDSCREEN);
  }
}
