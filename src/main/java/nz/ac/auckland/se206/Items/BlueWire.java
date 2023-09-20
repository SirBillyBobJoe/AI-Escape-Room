package nz.ac.auckland.se206.items;

import javafx.scene.image.Image;

/**
 * BlueWire class extends Wires to represent a Blue wire item in the game. The BlueWire has a
 * specific color ("Blue") and an associated image.
 */
public class BlueWire extends Wires {

  /**
   * Constructs a new BlueWire object. Initializes the message and item identifier to describe the
   * BlueWire.
   */
  public BlueWire() {
    super("Blue", new Image("/images/Inventory/blueWire.png"));
    this.itemIdentifier = "This is a blue wire.";
  }
}
