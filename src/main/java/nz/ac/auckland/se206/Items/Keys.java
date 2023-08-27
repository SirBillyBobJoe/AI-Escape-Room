package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

public class Keys extends Object {
  private int id;

  public Keys(int id) {
    super(new Image("/images/Inventory/key.png"));
    this.id = id;
  }

  public int getID() {
    return id;
  }
}
