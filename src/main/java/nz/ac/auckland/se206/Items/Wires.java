package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

public class Wires extends Object {
  private String colour;
  private boolean isUsed;

  public Wires(String colour, Image image) {
    super(image);
    this.colour = colour;
    this.message = "this is a " + colour + " wire";

    this.isUsed = false;
  }

  public boolean isUsed() {
    return this.isUsed;
  }

  public String getColour() {
    return this.colour;
  }
}
