package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

public class Hammer extends Object {
  public Hammer() {
    super(new Image("/images/Inventory/hammer.png"));
    this.message = "You have found hammer";
    this.itemIdentifier = "this is hammer";
  }
}
