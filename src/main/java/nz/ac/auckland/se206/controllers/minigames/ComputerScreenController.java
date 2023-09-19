package nz.ac.auckland.se206.controllers.minigames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

/** Controller class for the Computer Screen mini-game. Handles user interactions with the GUI. */
public class ComputerScreenController {
  @FXML private TextField txfGuess;
  @FXML private TextArea txaRiddle;
  @FXML private ImageView imgLoadingWheel;
  @FXML private Button sendButton;

  /**
   * Initializes the state of the ComputerScreenController. Sets up bindings and initializes game
   * state.
   */
  @FXML
  private void initialize() {
    GameState.riddleChat.setTextArea(txaRiddle);
    GameState.riddleChat.setLoadingWheel(imgLoadingWheel);
    GameState.riddleChat.newRiddle("riddle", GameState.passcodeAnswer);

    sendButton.disableProperty().bind(GameState.riddleRoomActive.not());
  }

  /** Exits the current puzzle and returns to the main game. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  /**
   * Handles the event when the user makes a guess.
   *
   * @param event The ActionEvent object representing the triggering event.
   */
  @FXML
  private void onGuess(ActionEvent event) {
    String text = txfGuess.getText();
    if (text.trim().isEmpty()) {
      return;
    }
    GameState.riddleChat.onSend(txfGuess.getText());
    txaRiddle.appendText("You: " + txfGuess.getText() + "\n\n");
    txfGuess.clear();
  }
}
