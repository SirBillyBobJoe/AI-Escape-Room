package nz.ac.auckland.se206.items;

import javafx.scene.image.Image;

/**
 * Wires class extends Object to represent a wire item in the game. It has a colour and a usage
 * status.
 */
public class Wires extends Object {

  /** The color of the wire. */
  private String colour;

  /** Indicates whether the wire has been used or not. */
  private boolean isUsed;

  /**
   * Constructs a Wire object with the specified color and image.
   *
   * @param colour The color of the wire.
   * @param image The image representing the wire.
   */
  public Wires(String colour, Image image) {
    super(image);
    this.colour = colour;
    this.message = "This is a " + colour + " wire.";
    this.isUsed = false;
  }

  /**
   * Checks if the wire is used.
   *
   * @return true if the wire has been used, false otherwise.
   */
  public boolean isUsed() {
    return this.isUsed;
  }

  /**
   * Gets the color of the wire.
   *
   * @return The color of the wire.
   */
  public String getColour() {
    return this.colour;
  }
}
