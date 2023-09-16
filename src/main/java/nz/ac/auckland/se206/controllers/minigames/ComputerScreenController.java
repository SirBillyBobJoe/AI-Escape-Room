package nz.ac.auckland.se206.controllers.minigames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class ComputerScreenController {
  @FXML private TextField txfGuess;
  @FXML private TextArea txaRiddle;

  @FXML
  private void initialize() {
    GameState.riddleChat.setTextArea(txaRiddle);
    GameState.riddleChat.newRiddle("riddle", "vase");
  }

  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  @FXML
  private void onGuess(ActionEvent event) {
    GameState.riddleChat.onSend(txfGuess.getText());
    txfGuess.clear();
  }
}
