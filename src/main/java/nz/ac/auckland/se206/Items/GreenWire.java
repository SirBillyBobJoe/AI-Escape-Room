package nz.ac.auckland.se206.items;

import javafx.scene.image.Image;

/**
 * GreenWire class extends Wires to represent a Green wire item in the game. The GreenWire has a
 * specific color ("Green") and an associated image.
 */
public class GreenWire extends Wires {

  /**
   * Constructs a new GreenWire object. Initializes the message and item identifier to describe the
   * GreenWire.
   */
  public GreenWire() {
    super("Green", new Image("/images/Inventory/greenWire.png"));
    this.itemIdentifier = "This is a green wire.";
  }
}
