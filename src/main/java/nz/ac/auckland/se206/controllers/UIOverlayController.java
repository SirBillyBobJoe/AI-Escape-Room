package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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
  @FXML private ImageView item0, item1, item2, item3, item4, item5;
  @FXML private TextArea textArea;
  @FXML private TextField textField;
  @FXML private TextArea itemChat;

  @FXML Label lblRestart;
  private final DropShadow dropShadow = new DropShadow();

  @FXML private TextArea txaGameMaster;
  @FXML private ImageView imgGameMaster;
  GameMasterActions gameMaster = new GameMasterActions();

  @FXML private Pane replacePane; // Must remain so it can be swapped at the start
  private Pane loadedRoom;

  /** Initializes Room 1, binding the UI to the game state and setting up chat context. */
  public void initialize() {
    // Initialize the Game Master actions
    gameMaster = new GameMasterActions(imgGameMaster, txaGameMaster);

    // Determine the hint text based on the game state
    String hint;
    if (GameState.hints.get().equals("\u221E")) {
      hint = "infinite";
    } else {
      hint = GameState.hints.get();
    }

    // Create a chat context for Room 1
    GameState.gameMaster.createChatContext("room1");

    // Generate a message for the Game Master
    String gptMsg =
        "This game the player will have "
            + hint
            + " hints. You are the Game Master Of An Escape Room currently we are in room 1. Here"
            + " is some answers to the hints. The scroll is under the car, Switch is next to the"
            + " chains, light is next to the car, riddle answer is chicken, u need key to unlock"
            + " the door. Don't reply to this message reply but reply to following messages. Only"
            + " give one hint at a time";

    // Add the initial message to the chat context and run it
    GameState.gameMaster.addMessage("room1", "user", gptMsg);
    GameState.gameMaster.runContext("room1");
    System.out.println(gptMsg);

    // Bind UI elements to game state properties
    countdownLabel.textProperty().bind(GameState.timer.timeSecondsProperty().asString());
    hintLabel.textProperty().bind(GameState.hints);

    // Initialize inventory control images
    ImageView[] images = {item0, item1, item2, item3, item4, item5};
    // Set user data for each inventory item
    for (int i = 0; i < images.length; i++) {
      images[i].setUserData(i);
    }

    // Update the inventory UI when it changes
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

    // Bind text areas of the 2 controllers together for chat
    GameState.chat = SharedChat.getInstance();
    textArea.textProperty().bind(GameState.chat.getTextProperty());
    textArea.setWrapText(true);

    // Scroll to the bottom of the chat area when text changes
    GameState.chat
        .getTextProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              Platform.runLater(
                  () -> {
                    textArea.setScrollTop(Double.MAX_VALUE);
                  });
            });

    // Load the initial room and set up a listener for room changes
    GameState.currentRoom.addListener(
        (ObservableValue<? extends Rooms> o, Rooms oldVal, Rooms newVal) -> {
          changeRoom(newVal);
        });
    GameState.currentRoom.set(Rooms.MAINROOM);
    changeRoom(Rooms.MAINROOM);

    // Set up a button drop shadow
    dropShadow.setColor(Color.web("#007aec"));
    dropShadow.setRadius(5.0);
  }

  /**
   * Changes the current game room with a fade transition.
   *
   * @param room The room to transition to.
   */
  private void changeRoom(Rooms room) {
    // Fade out the current room
    FadeTransition fadeOut = new FadeTransition(Duration.millis(300));
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setNode(loadedRoom);
    fadeOut.setOnFinished(
        e -> {
          loadedRoom = SceneManager.getRoomPane(room);
          mainPane.getChildren().set(0, loadedRoom);
          // Fade in the new room
          FadeTransition fadeIn = new FadeTransition(Duration.millis(300));
          fadeIn.setFromValue(0.0);
          fadeIn.setToValue(1.0);
          fadeIn.setNode(loadedRoom);
          fadeIn.play();
        });

    fadeOut.play();
  }

  // DELETEME
  private boolean justActivated = false;

  /**
   * Handles the click event for the game master, activating or deactivating it.
   *
   * @param event The mouse click event.
   */
  @FXML
  private void gameMasterClicked(MouseEvent event) {
    if (!justActivated) {
      justActivated = true;
      gameMaster.activate("Hello, I am the game master. Do not defy me. blah blah blah.");
    } else {
      gameMaster.unactivate();
      justActivated = false;
    }
  }

  /**
   * Resets the game state and navigates back to the start screen.
   *
   * @param event The mouse click event for the restart button.
   * @throws IOException If the FXML for the start screen can't be loaded.
   */
  @FXML
  private void lblRestartClicked(MouseEvent event) throws IOException {
    // Play a click sound effect
    new MouseClick().play();

    // Retrieve the stage and navigate to the start screen
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    SceneManager.setReinitialise(AppUi.UIOVERLAY);
    App.setUserInterface(AppUi.STARTSCREEN);

    // Adjust the stage dimensions
    double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
    double additionalHeight = stage.getHeight() - stage.getScene().getHeight();
    stage.setWidth(800 + additionalWidth);
    stage.setHeight(600 + additionalHeight);

    // Stop the game timer and reset game state
    GameState.timer.stop();
    GameState.inventory = new Inventory();
    GameState.gameMaster = new GameMaster();
    GameState.chat.restart();
  }

  /**
   * Updates the restart button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the restart button.
   */
  @FXML
  private void lblRestartEntered(MouseEvent event) {
    lblRestart.setEffect(dropShadow);
    lblRestart.setTextFill(Color.WHITE);
    lblRestart.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: white; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /**
   * Updates the restart button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the restart button.
   */
  @FXML
  private void lblRestartExited(MouseEvent event) {
    lblRestart.setEffect(null);
    lblRestart.setTextFill(Color.web("#bfbfbf"));
    lblRestart.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: #bfbfbf; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
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
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    SceneManager.setReinitialise(AppUi.ENDSCREEN);
    App.setUserInterface(AppUi.ENDSCREEN);
    double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
    double additionalHeight = stage.getHeight() - stage.getScene().getHeight();
    stage.setWidth(800 + additionalWidth);
    stage.setHeight(600 + additionalHeight);
  }
}