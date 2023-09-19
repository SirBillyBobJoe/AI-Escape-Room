package nz.ac.auckland.se206.controllers.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class CandlePaintingController {
  @FXML ImageView candle0, candle1, candle2, candle3;

  public void initialize() {
    List<ImageView> candles = new ArrayList<ImageView>();
    candles.add(candle0);
    candles.add(candle1);
    candles.add(candle2);
    candles.add(candle3);
    GameState.candleOrder = new ArrayList<Boolean>();
    for (int i = 0; i < 4; i++) {
      Random random = new Random();

      boolean randomBoolean = random.nextBoolean();
      GameState.candleOrder.add(randomBoolean);
      ImageView candle = candles.get(i);
      if (randomBoolean) {
        candle.setImage(new Image("/images/puzzleroom/litCandle.png"));
      } else {
        candle.setImage(new Image("/images/puzzleroom/unlitCandle.png"));
      }
    }
    int count = 0;
    for (Boolean bool : GameState.candleOrder) {
      if (!bool) {
        count++;
      }
    }
    if (count == 4) {
      GameState.candleOrder.set(0, true);
    }
  }

  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }
}
