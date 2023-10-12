package nz.ac.auckland.se206.controllers.minigames;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.controllers.GameMasterActions;

/** Controller class for the Computer Screen mini-game. Handles user interactions with the GUI. */
public class ComputerScreenController {
  @FXML private TextField txfGuess;
  @FXML private TextArea txaRiddle;
  @FXML private ImageView imgLoadingWheel;
  @FXML private Button sendButton;

  @FXML private TextArea promptArea2;
  @FXML private TextArea txaGameMaster2;
  @FXML private ImageView imgGameMaster2;
  @FXML private ImageView glitch;

  @FXML private Timeline playerInteractionTimer;

  /**
   * Initializes the state of the ComputerScreenController. Sets up bindings and initializes game
   * state.
   */
  @FXML
  private void initialize() {
    // binds the loading image to loading state
    glitch.visibleProperty().bind(GameState.loading);
    System.out.println("Initialise Computer Screen Controller");
    GameState.riddleChat.setTextArea(txaRiddle);
    GameState.riddleChat.setLoadingWheel(imgLoadingWheel);
    // sets answers to riddles
    GameState.riddleChat.newRiddle(GameState.passcodeAnswer);

    sendButton.disableProperty().bind(GameState.riddleRoomActive.not());
    // screen controller is this right now
    GameState.computerScreenController = this;
  }

  /** Exits the current puzzle and returns to the main game. */
  @FXML
  private void exitPuzzle() {
    exit();
  }

  private void exit() {
    System.out.println("Exit");
    GameState.isInComputer = false;
    GameState.currentPuzzle.setValue(Puzzle.NONE);
    GameState.chat.setGameMasterActions(GameState.gameMasterActions);
    GameState.userInterfaceOverlayController.moveGameMaster();

    if (GameState.riddle2019Solved) {
      GameState.riddleChat.newRiddle(GameState.padlockAnswer);
    }
  }

  /**
   * Handles text message sending for the chat feature in the main room.
   *
   * @param event keypress event
   */
  @FXML
  private void riddleComputerOnKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER && GameState.riddleRoomActive.getValue()) {
      onGuess(new ActionEvent());
    }
  }

  /**
   * Handles the event when the user makes a guess.
   *
   * @param event The ActionEvent object representing the triggering event.
   */
  @FXML
  private void onGuess(ActionEvent event) {
    // gets the text
    String text = txfGuess.getText();
    // return if empty

    if (text.trim().isEmpty()) {
      return;
    }

    txaRiddle.appendText("You: " + txfGuess.getText() + "\n\n");
    // send to gpt
    if (GameState.riddleChat.onSend(txfGuess.getText())) {
      exit();
    }

    txfGuess.clear();
  }

  /**
   * Handles text message sending for the gamemaster feature in the computer.
   *
   * @param event keypress event.
   */
  @FXML
  private void riddleComputerGameMasterOnKeyPress(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      onSend(new ActionEvent());
    }
  }

  /**
   * Handles text message sending for the chat feature in the computer.
   *
   * @param event ActionEvent for sending a message.
   */
  @FXML
  private void onSend(ActionEvent event) {
    if (!promptArea2.getText().trim().isEmpty()) {
      GameState.loading.set(true);
      GameState.chat.onSend(promptArea2);
      GameState.chat.setGameMasterActions(new GameMasterActions(imgGameMaster2, txaGameMaster2));

      GameState.glitchSound.play();
    }

    // Reset the player interaction timer
    playerInteractionTimer.stop();
    playerInteractionTimer.play();
  }

  public void setGameMaster(String message) {
    txaGameMaster2.setText(message);
  }

  public String getGameMaster() {
    return txaGameMaster2.getText();
  }
}
