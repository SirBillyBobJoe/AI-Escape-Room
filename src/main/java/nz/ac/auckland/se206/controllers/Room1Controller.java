package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import javafx.application.Platform;
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
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.ItemChat;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.Items.Keys;
import nz.ac.auckland.se206.Items.Lighter;
import nz.ac.auckland.se206.Items.Lock;
import nz.ac.auckland.se206.Items.Object;
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
  @FXML private ImageView lock1;
  @FXML private TextArea textArea;
  @FXML private TextField textField;
  @FXML private TextArea itemChat;
  private final HashMap<ImageView, Object> room1Items = new HashMap<ImageView, Object>();

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
    // GameState.gameMaster.addMessage("room1", "user", gptMsg);
    // GameState.gameMaster.runContext("room1");
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
    lock1.setUserData("lock");
    lock1.setOnDragOver(event -> onDragOver(event));
    lock1.setOnDragDropped(event -> onDragDropped(event));
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
    // end of inventory initialising

    // binds the text areas of the 2 controllers together
    GameState.room1Chat = SharedChat.getInstance();
    textArea.textProperty().bind(GameState.room1Chat.getTextProperty());
    textArea.setWrapText(true);
    // Listen for changes in the textProperty of the textArea
    GameState.room1Chat
        .getTextProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Scroll to the bottom in the JavaFX Application Thread
              Platform.runLater(
                  () -> {
                    textArea.setScrollTop(Double.MAX_VALUE);
                  });
            });

    // initialise objects in room 1 into HashMap
    room1Items.put(key1, new Keys(1));
    room1Items.put(key2, new Keys(2));
    room1Items.put(key3, new Keys(3));
    room1Items.put(lighter1, new Lighter());
    room1Items.put(lighter2, new Lighter());
    room1Items.put(lighter3, new Lighter());
    room1Items.put(lock1, new Lock(1));
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
    GameState.room1Chat.restart();
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

    itemChat.clear();
    ImageView imageView = (ImageView) event.getSource();
    Object item = room1Items.get(imageView);

    // if not a lock
    if (item != null) {
      // if its not a lock
      if (!(item instanceof Lock)) {

        imageView.setVisible(false);
        GameState.inventory.addObject(item);
      }
      // adds message to item chat as if it was typing
      String message = item.getMessage();
      ItemChat.getInstance().printChatMessage(itemChat, message);
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
    GameState.inventory.onDragDropped(event, room1Items, itemChat);
  }

  /**
   * Handles text message sending for the chat feature in Room 1.
   *
   * @param event ActionEvent for sending a message.
   */
  @FXML
  private void onSend(ActionEvent event) {
    GameState.room1Chat.onSend(textField, "room1");
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
    GameState.inventory.onInventoryClicked(event, itemChat);
  }
}
