package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

public class Lock extends Object {
  private boolean isLocked = true;
  private int id;

  public Lock(int id) {
    super(new Image("/images/Inventory/lock.png"));
    this.id = id;
    this.message = "lock " + id + " is locked";
  }

  public boolean isLocked() {
    return this.isLocked;
  }

  public int getId() {
    return id;
  }

  public void unlockLock() {
    this.message = "you have unlocked lock " + id;
  }
}
