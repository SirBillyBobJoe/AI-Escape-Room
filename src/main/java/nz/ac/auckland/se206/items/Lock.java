package nz.ac.auckland.se206.items;

import javafx.scene.image.Image;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

/**
 * Lock class extends Object to represent a padlock item in the game. The Lock object has an ID and
 * a locking status.
 */
public class Lock extends Object {

  private boolean isLocked = true;
  private int id;

  /**
   * Constructs a new Lock object with a specific ID and an associated image. Initializes the
   * message to indicate that the padlock is locked.
   *
   * @param id The unique identifier for this Lock.
   */
  public Lock(int id) {
    super(new Image("/images/Inventory/lock.png"));
    this.id = id;
    this.message = "This padlock is locked. Use a key to unlock it";
  }

  /**
   * Checks if the padlock is locked.
   *
   * @return true if the padlock is locked, false otherwise.
   */
  public boolean isLocked() {
    return this.isLocked;
  }

  /**
   * Gets the ID of this Lock.
   *
   * @return The unique identifier for this Lock.
   */
  public int getId() {
    return id;
  }

  /**
   * Unlocks the padlock, updates its status and message, and sets the current puzzle to PADLOCK in
   * the game state.
   */
  public void unlockLock() {
    isLocked = false;
    this.message = "You have unlocked the padlock.";
    GameState.currentPuzzle.set(Puzzle.PADLOCK);
  }
}
