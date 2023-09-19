package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

/**
 * Represents a generic object in the game's inventory. Each object contains an image representation
 * and a position within the inventory.
 */
public class Object {
  protected Image image;
  protected String message;
  protected String itemIdentifier;

  private int position;

  /**
   * Constructs a new Object with a specified image.
   *
   * @param image The image that represents this object.
   */
  public Object(Image image) {
    this.image = image;
  }

  /**
   * Sets the position of this object within the inventory.
   *
   * @param position The position to set for this object.
   */
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * Retrieves the position of this object within the inventory.
   *
   * @return The position of this object.
   */
  public int getPosition() {
    return position;
  }

  /**
   * Retrieves the image representing this object.
   *
   * @return The image of this object.
   */
  public Image getImage() {
    return image;
  }

  /**
   * Retrieves the Message object holds
   *
   * @return The String of message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the Message object holds
   *
   * @param The String of message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * gets the identifier message
   *
   * @return returns the identifier
   */
  public String getItemIdentifier() {
    return itemIdentifier;
  }
}
