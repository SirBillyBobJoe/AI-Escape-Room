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
    // Create a list of ImageView objects representing candles.
    List<ImageView> candles = List.of(candle0, candle1, candle2, candle3);

    // Initialize a list to store the random candle order.
    GameState.candleOrder = new ArrayList<>();
    Random random = new Random();

    // Generate a random order for the candles.
    for (int i = 0; i < 4; i++) {
      boolean randomBoolean = random.nextBoolean();
      GameState.candleOrder.add(randomBoolean);
    }

    // Count the number of unlit candles in the random order.
    long unlitCount = GameState.candleOrder.stream().filter(b -> !b).count();

    // Ensure that at least one candle is lit by setting the first one to true if all are unlit.
    if (unlitCount == 4) {
      GameState.candleOrder.set(random.nextInt(4), true);
    }

    // Update the images of the candles based on the random order.
    for (int i = 0; i < 4; i++) {
      ImageView candle = candles.get(i);
      String imagePath =
          GameState.candleOrder.get(i)
              ? "/images/puzzleroom/litCandle.png"
              : "/images/puzzleroom/unlitCandle.png";
      candle.setImage(new Image(imagePath));
    }
  }

  /** Exits the current puzzle and returns to the main game. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }
}
