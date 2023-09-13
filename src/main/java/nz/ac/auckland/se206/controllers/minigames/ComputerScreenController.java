package nz.ac.auckland.se206.controllers.minigames;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class ComputerScreenController {

  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }
}
