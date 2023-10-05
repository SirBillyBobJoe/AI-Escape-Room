package nz.ac.auckland.se206.controllers.minigames;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class HintController {
  @FXML ImageView clue;

  /** Exits the current puzzle and resets the puzzle state to NONE. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  /**
   * Event handler for clue clicks.
   *
   * @param event The MouseEvent triggered by the click.
   */
  @FXML
  private void clickClue(MouseEvent event) {
    new MouseClick().play();
    GameState.currentPuzzle.setValue(Puzzle.PIPECONNECTING);
  }

  /**
   * Event handler for entering mouse.
   *
   * @param event The MouseEvent triggered by the entering.
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    clue.setImage(new Image("/images/PipeConnecting/clueBlack.png"));
  }

  /**
   * Event handler for exiting mouse.
   *
   * @param event The MouseEvent triggered by the exiting
   */
  @FXML
  private void onMouseExited(MouseEvent event) {
    clue.setImage(new Image("/images/PipeConnecting/clueWhite.png"));
  }
}
