package nz.ac.auckland.se206.items;

import javafx.scene.image.Image;

/**
 * Hammer class extends Object to represent a hammer item in the game. The hammer has an associated
 * image and a message to describe it.
 */
public class Hammer extends Object {

  /**
   * Constructs a new Hammer object with an associated image. Initializes the message and item
   * identifier to describe the hammer.
   */
  public Hammer() {
    super(new Image("/images/Inventory/hammer.png"));
    this.message = "You have found a hammer. Drag It To Interact With Objects In The Room";
    this.itemIdentifier = "This is a hammer. Drag It To Interact With Objects In The Room";
  }
}
