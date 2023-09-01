package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.scene.Parent;

/** Manages different scenes or UI states in the application. */
public class SceneManager {
  /** Enumerates different states or scenes in the application. */
  public enum AppUi {
    SCREEN_START,
    ROOM1
  }

  private static HashMap<AppUi, Parent> map = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, Boolean> reinitialize = new HashMap<AppUi, Boolean>();

  /**
   * Adds a new UI state and its corresponding root Parent object to the scene manager.
   *
   * @param ui The enum value representing the UI state.
   * @param root The root Parent object for this UI state.
   * @param reinit Flag indicating if the UI should be re-initialized when accessed again.
   */
  public static void addAppUi(AppUi ui, Parent root, boolean reinit) {
    map.put(ui, root);
    reinitialize.put(ui, reinit);
  }

  /**
   * Returns the Parent root object for a given UI state.
   *
   * @param ui The enum value representing the UI state.
   * @return The root Parent object for the UI state.
   * @throws IOException If UI initialization fails.
   */
  public static Parent getUi(AppUi ui) throws IOException {
    if (reinitialize.get(ui)) {
      // If this room should be re-initialized, create a new instance of the UI.
      // For example, load a new instance from a FXML file:

      Parent newUserInterface =
          App.loadFxml(ui.toString().toLowerCase()); // assuming the method is static
      map.put(ui, newUserInterface);
    }
    return map.get(ui);
  }

  /**
   * Sets the UI state to be re-initialized the next time it is accessed.
   *
   * @param ui The enum value representing the UI state.
   * @throws IOException If UI re-initialization fails.
   */
  public static void setReinitialise(AppUi ui) throws IOException {
    Parent newUserInterface =
        App.loadFxml(ui.toString().toLowerCase()); // assuming the method is static
    map.put(ui, newUserInterface);
  }
}
