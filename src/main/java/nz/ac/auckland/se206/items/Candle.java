package nz.ac.auckland.se206.items;

import javafx.scene.image.Image;

/**
 * Candle class extends Object to represent a candle item in the game. The candle has two states:
 * lit and unlit, and its image changes accordingly.
 */
public class Candle extends Object {
  private boolean isLit = false;

  /**
   * Constructs a new Candle object. Initializes the state to "unlit" and sets the appropriate
   * message.
   */
  public Candle() {
    super(new Image("/images/puzzleroom/unlitCandle.png"));

    this.message = "The candle is unlit.";
  }

  /** Toggles the state of the candle between lit and unlit. */
  public void changeCandle() {
    // do the opposite of its current light status
    if (isLit) {
      isLit = false;
      // changes the image to unlit
      this.message = "You unlit the candle.";
      this.image = new Image("/images/puzzleroom/unlitCandle.png");
    } else {
      isLit = true;
      // changes image to lit
      this.message = "You lit the candle.";
      this.image = new Image("/images/puzzleroom/litCandle.png");
    }
  }

  /**
   * Checks if the candle is lit.
   *
   * @return true if the candle is lit, false otherwise.
   */
  public boolean isLit() {
    return isLit;
  }
}
