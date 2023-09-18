package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class Lock extends Object {
  private boolean isLocked = true;
  private int id;

  public Lock(int id) {
    super(new Image("/images/Inventory/lock.png"));
    this.id = id;
    this.message = "This padlock is locked.";
  }

  public boolean isLocked() {
    return this.isLocked;
  }

  public int getId() {
    return id;
  }

  public void unlockLock() {
    isLocked = false;
    this.message = "You have unlocked the padlock.";
    GameState.currentPuzzle.set(Puzzle.PADLOCK);
  }
}
