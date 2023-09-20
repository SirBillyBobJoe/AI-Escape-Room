package nz.ac.auckland.se206.controllers.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

/**
 * Controller class for the Candle Painting mini-game. Manages the interaction and state of candles
 * on the UI.
 */
public class CandlePaintingController {
  @FXML private ImageView candle0;
  @FXML private ImageView candle1;
  @FXML private ImageView candle2;
  @FXML private ImageView candle3;

  /**
   * Initializes the state of the CandlePaintingController. Randomly sets candles as either lit or
   * unlit.
   */
  public void initialize() {
    // intiialise values
    List<ImageView> candles = new ArrayList<ImageView>();
    candles.add(candle0);
    candles.add(candle1);
    candles.add(candle2);
    candles.add(candle3);
    GameState.candleOrder = new ArrayList<Boolean>();
    // only allows 4 candles
    for (int i = 0; i < 4; i++) {
      Random random = new Random();
      // random logic
      boolean randomBoolean = random.nextBoolean();
      GameState.candleOrder.add(randomBoolean);
      ImageView candle = candles.get(i);
      int count = 0;
      // ensures its not always turned off all of them
      for (Boolean bool : GameState.candleOrder) {
        if (!bool) {
          count++;
        }
      }
      // if it is set the first one to true
      if (count == 4) {
        GameState.candleOrder.set(0, true);
      }
      // links array to images
      if (randomBoolean) {
        candle.setImage(new Image("/images/puzzleroom/litCandle.png"));
      } else {
        candle.setImage(new Image("/images/puzzleroom/unlitCandle.png"));
      }
    }
  }

  /** Exits the current puzzle and returns to the main game. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }
}
