package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/** Manages different scenes or UI states in the application. */
public class SceneManager {
  /** Enumerates different states or scenes in the application. */
  public enum AppUi {
    STARTSCREEN,
    UIOVERLAY,
    ENDSCREEN
  }

  public enum Rooms {
    MAINROOM,
    RIDDLEROOM,
    PUZZLEROOM,
  }

  private static HashMap<AppUi, Parent> map = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, Boolean> reinitialize = new HashMap<AppUi, Boolean>();
  private static HashMap<Rooms, Pane> roomMap = new HashMap<Rooms, Pane>();

  /**
   * Returns the Parent root object for a given UI state.
   *
   * @param ui The enum value representing the UI state.
   * @return The root Parent object for the UI state.
   * @throws IOException If UI initialization fails.
   */
  public static Parent getUi(AppUi ui) throws IOException {
    if (!reinitialize.containsKey(ui) || reinitialize.get(ui)) {
      // If this room should be re-initialized, create a new instance of the UI.
      // For example, load a new instance from a FXML file:

      if (ui.equals(AppUi.UIOVERLAY)) {
        System.out.println("Initialise Rooms");
        initialiseRooms();
      }

      reinitialize.put(ui, false);

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
    reinitialize.put(ui, true);
  }

  /** */
  public static void initialiseRooms() throws IOException {
    roomMap = new HashMap<Rooms, Pane>();
    roomMap.put(Rooms.MAINROOM, (Pane) App.loadFxml("mainroom"));
    roomMap.put(Rooms.PUZZLEROOM, (Pane) App.loadFxml("puzzleroom"));
    roomMap.put(Rooms.RIDDLEROOM, new Pane());
  }

  public static Pane getRoomPane(Rooms room) {
    return roomMap.get(room);
  }
}
