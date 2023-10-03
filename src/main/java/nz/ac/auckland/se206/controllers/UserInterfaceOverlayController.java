package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GlitchSound;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.SharedChat;
import nz.ac.auckland.se206.gpt.GameMaster;

/**
 * Controller class for Room 1 in the escape room game. Manages the UI elements and interactions for
 * Room 1.
 */
public class UserInterfaceOverlayController {
  @FXML private AnchorPane mainPane;
  @FXML private Label countdownLabel;
  @FXML private Label hintLabel;
  @FXML private ImageView item0;
  @FXML private ImageView item1;
  @FXML private ImageView item2;
  @FXML private ImageView item3;
  @FXML private ImageView item4;
  @FXML private ImageView item5;
  @FXML private ImageView glitch;
  @FXML private TextArea promptArea;
  @FXML private TextArea itemChat;

  @FXML private Label lblRestart;
  @FXML private ImageView muteSound;
  private final DropShadow dropShadow = new DropShadow();

  @FXML private TextArea txaGameMaster;
  @FXML private ImageView imgGameMaster;
  @FXML private Button btnSend;

  @FXML private Pane roomPane; // Must remain so it can be swapped at the start
  private Pane loadedRoom;
  @FXML private Pane puzzlePane; // Must remain so it can be swapped at the start
  private Pane loadedPuzzle;

  private Timeline playerInteractionTimer;

  private String intro;

  private final Image SOUND_OFF_IMAGE = new Image("/images/overlay/sound-off.png");
  private final Image SOUND_ON_IMAGE = new Image("/images/overlay/sound-on.png");

  /** Initializes Room 1, binding the UI to the game state and setting up chat context. */
  public void initialize() {
    glitch.visibleProperty().bind(GameState.loading);
    GameState.gameMasterActions = new GameMasterActions(imgGameMaster, txaGameMaster);
    GameState.userInterfaceOverlayController = this;

    // Determine the hint text based on the game state
    String hint;
    if (GameState.hints.get().equals(GameState.infinity)) {
      hint = "infinite";
    } else {
      hint = GameState.hints.get();
    }

    // Create a chat context for main room
    GameState.gameMaster.createChatContext("main");

    // Generate a message for the Game Master
    String gptMsg =
        "This game the player will have "
            + hint
            + " hints. You are \"The Singularity\", master of this escape room. We are in the main"
            + " room. You speak extremely concisely, you do not waste"
            + " words. Concise. Strict. Stoic.";

    // Add the initial message to the chat context and run it
    GameState.gameMaster.addMessage("main", "user", gptMsg);
    GameState.gameMaster.runContext("main");

    // Bind UI elements to game state properties
    StringBinding timeDisplayBinding =
        Bindings.createStringBinding(
            () -> {
              // convert to seconds and minutes
              int totalSeconds = GameState.timer.timeSecondsProperty().get();
              int minutes = totalSeconds / 60;
              int seconds = totalSeconds % 60;
              // return minutes:seconds Format
              return String.format("%02d:%02d", minutes, seconds);
            },
            GameState.timer.timeSecondsProperty());
    countdownLabel.textProperty().bind(timeDisplayBinding);
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
    GameState.chat.setGameMasterActions(GameState.gameMasterActions);

    // Scroll to the bottom of the chat area when text changes
    GameState.chat
        .getTextProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              Platform.runLater(
                  () -> {
                    txaGameMaster.setScrollTop(Double.MAX_VALUE);
                  });
            });

    // Load the initial room and set up a listener for room changes
    GameState.currentRoom.addListener(
        (ObservableValue<? extends Rooms> o, Rooms oldVal, Rooms newVal) -> {
          changeRoom(newVal);
        });
    GameState.currentRoom.set(Rooms.MAINROOM);
    changeRoom(Rooms.MAINROOM);

    // Add a empty pane to the end of the ui overlay. This is what is swapped out when there are
    // puzzles
    mainPane.getChildren().add(SceneManager.getPuzzlePane(Puzzle.NONE));
    GameState.currentPuzzle.addListener(
        (ObservableValue<? extends Puzzle> o, Puzzle oldVal, Puzzle newVal) -> {
          changePuzzle(oldVal, newVal);
        });
    GameState.currentPuzzle.set(Puzzle.NONE);
    changePuzzle(Puzzle.NONE, Puzzle.NONE);

    // Set up a button drop shadow
    dropShadow.setColor(Color.web("#007aec"));
    dropShadow.setRadius(5.0);

    // Set up a timer to check for player interaction with game master
    playerInteractionTimer =
        new Timeline(
            new KeyFrame(
                Duration.seconds(15),
                e -> {
                  // This code will run after 20 seconds of player inactivity.
                  GameState.gameMasterActions.unactivate();
                }));

    // Generate a welcome message for the player after the game master activates (5 seconds after
    // game loads)
    GameState.gameMaster.createChatContext("intro");

    // Add the initial message to the chat context and run it
    GameState.gameMaster.addMessage(
        "intro",
        "user",
        "You are The Singularity, an omnipresent AI. You don't want the player to escape from your"
            + " domain. You introduce yourself extremely briefly.");
    GameState.gameMaster.runContext("intro");

    new Thread(
            () -> {
              GameState.gameMaster.waitForContext("intro");
              intro = GameState.gameMaster.getLastResponse("intro").getContent();
              GameState.gameMasterActions.activate(intro);
              GameState.loading.set(false);
            })
        .start();

    setMuteSoundImage(GameState.isGameMuted);
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

  /**
   * Changes the current puzzle with a fade transition.
   *
   * @param oldPuzzle The puzzle to transition from.
   * @param newPuzzle The puzzle to transition to.
   */
  private void changePuzzle(Puzzle oldPuzzle, Puzzle newPuzzle) {
    double fadeOutDuration = 300;
    double fadeInDuration = 300;
    if (oldPuzzle.equals(Puzzle.NONE)) {
      fadeOutDuration = 0;
    }
    if (newPuzzle.equals(Puzzle.NONE)) {
      fadeInDuration = 0;
    }

    final double fadeInDurationFinal = fadeInDuration;

    // Fade out the current puzzle
    FadeTransition fadeOut = new FadeTransition(Duration.millis(fadeOutDuration));
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setNode(loadedPuzzle);
    fadeOut.setOnFinished(
        e -> {
          loadedPuzzle = SceneManager.getPuzzlePane(newPuzzle);
          mainPane.getChildren().set(mainPane.getChildren().size() - 1, loadedPuzzle);
          // Fade in the new puzzle
          FadeTransition fadeIn = new FadeTransition(Duration.millis(fadeInDurationFinal));
          fadeIn.setFromValue(0.0);
          fadeIn.setToValue(1.0);
          fadeIn.setNode(loadedPuzzle);
          fadeIn.play();
        });

    fadeOut.play();
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

    // Navigate to the start screen
    SceneManager.setReinitialise(AppUi.UIOVERLAY);
    App.setUserInterface(AppUi.STARTSCREEN);

    // Stop the game timer and reset game state
    GameState.timer.stop();
    GameState.gameMaster = new GameMaster();
    GameState.chat.restart();
    GameState.resetGame();
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
   * Handles text message sending for the chat feature in the main room.
   *
   * @param event keypress event
   */
  @FXML
  private void uiOverlayOnKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      onSend(new ActionEvent());
    }
  }

  /**
   * Handles text message sending for the chat feature in the main room.
   *
   * @param event ActionEvent for sending a message.
   */
  @FXML
  private void onSend(ActionEvent event) {
    if (!promptArea.getText().trim().equals("")) {
      GameState.loading.set(true);
      GameState.chat.onSend(promptArea);
      new GlitchSound().play();
    }

    // Reset the player interaction timer
    playerInteractionTimer.stop();
    playerInteractionTimer.play();
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

  /** Hide and move Game Master to a different location for immersion */
  @FXML
  public void moveGameMaster() {
    // turns the right master invisible
    if (txaGameMaster.isVisible()) {
      txaGameMaster.setVisible(false);
      imgGameMaster.setVisible(false);
      promptArea.setVisible(false);
      btnSend.setVisible(false);
      // change properties
      glitch.visibleProperty().unbind();
      glitch.setVisible(false);
    } else {
      // set visible in the game
      txaGameMaster.setVisible(true);
      imgGameMaster.setVisible(true);
      promptArea.setVisible(true);
      btnSend.setVisible(true);
      // bind glitch property
      glitch.visibleProperty().bind(GameState.loading);
    }
  }

  /** Mutes the sound when the mute button is clicked */
  @FXML
  public void onMuteSoundClicked(MouseEvent event) {
    GameState.isGameMuted = !GameState.isGameMuted;
    new MouseClick().play();
    setMuteSoundImage(GameState.isGameMuted);
  }

  /**
   * Updates the muteSound button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the muteSound button.
   */
  @FXML
  private void muteSoundEntered(MouseEvent event) {
    muteSound.setEffect(dropShadow);
    muteSound.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: white; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /**
   * Updates the muteSound button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the muteSound button.
   */
  @FXML
  private void muteSoundExited(MouseEvent event) {
    muteSound.setEffect(null);
    muteSound.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: #bfbfbf; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /** Sets the mute sound image */
  public void setMuteSoundImage(boolean muted) {
    if (muted) {
      muteSound.setImage(SOUND_OFF_IMAGE);
    } else {
      muteSound.setImage(SOUND_ON_IMAGE);
    }
  }

  public String getGameMaster() {
    return txaGameMaster.getText();
  }

  public void setGameMaster(String message) {
    txaGameMaster.setText(message);
  }
}
