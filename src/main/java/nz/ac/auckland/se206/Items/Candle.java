package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

public class Candle extends Object {
  private boolean isLit = false;

  public Candle() {
    super(new Image("/images/puzzleroom/unlitCandle.png"));

    this.message = "The candle is unlit.";
  }

  public boolean isLocked() {
    return this.isLit;
  }

  public void changeCandle() {
    if (isLit) {
      isLit = false;
      this.message = "You unlit the candle.";
      this.image = new Image("/images/puzzleroom/unlitCandle.png");
    } else {
      isLit = true;
      this.message = "You lit the candle.";
      this.image = new Image("/images/puzzleroom/litCandle.png");
    }
  }

  public boolean isLit() {
    return isLit;
  }
}
