package nz.ac.auckland.se206.controllers.minigames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class ComputerScreenController {
  @FXML private TextField txfGuess;
  @FXML private TextArea txaRiddle;
  @FXML private ImageView imgLoadingWheel;

  @FXML
  private void initialize() {
    GameState.riddleChat.setTextArea(txaRiddle);
    GameState.riddleChat.setLoadingWheel(imgLoadingWheel);
    GameState.riddleChat.newRiddle("riddle", GameState.passcodeAnswer);
  }

  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  @FXML
  private void onGuess(ActionEvent event) {
    GameState.riddleChat.onSend(txfGuess.getText());
    txaRiddle.appendText("You: " + txfGuess.getText() + "\n\n");
    txfGuess.clear();
  }
}
