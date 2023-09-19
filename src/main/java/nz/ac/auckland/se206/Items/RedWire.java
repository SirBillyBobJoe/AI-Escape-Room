package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

/**
 * RedWire class extends Wires to represent a Red Wire item in the game. It has a predetermined
 * colour ("Red") and a usage status inherited from the Wires class.
 */
public class RedWire extends Wires {

  /**
   * Constructs a RedWire object with a predetermined color ("Red") and a specific image. It also
   * sets the item identifier for this specific wire.
   */
  public RedWire() {
    super("Red", new Image("/images/Inventory/redWire.png"));
    this.itemIdentifier = "This is a red wire.";
  }
}
