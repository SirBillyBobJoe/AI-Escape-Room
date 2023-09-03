package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

/**
 * Represents a lighter object in the game. This class extends the Object class and is instantiated
 * with a predefined image.
 */
public class Lighter extends Object {
  /** Constructs a new lighter object. The lighter is initialized with a specific image. */
  public Lighter() {
    super(new Image("/images/Inventory/lighter.png"));
    this.message = "You have found a lighter";
  }
}
