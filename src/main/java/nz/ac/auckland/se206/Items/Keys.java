package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

/** Represents a key object in the game. This class extends the Object class to include an ID. */
public class Keys extends Object {
  private int id;

  /**
   * Constructs a new key object with a specified ID.
   *
   * @param id The unique identifier for this key.
   */
  public Keys(int id) {
    super(new Image("/images/Inventory/key.png"));
    this.id = id;
    this.message = "You have found key " + id;
  }

  /**
   * Returns the ID of this key.
   *
   * @return The unique identifier for this key.
   */
  public int getID() {
    return id;
  }
}
