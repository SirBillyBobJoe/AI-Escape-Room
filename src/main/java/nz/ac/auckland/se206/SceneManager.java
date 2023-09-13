package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/** Manages different scenes or UI states in the application. */
public class SceneManager {
  /** Enumerates different states or scenes in the application. */
  public enum AppUi {
    STARTSCREEN,
    UIOVERLAY,
    ENDSCREEN,
    PIPECONNECTING,
  }

  public enum Rooms {
    MAINROOM,
    RIDDLEROOM,
    PUZZLEROOM,
    LEFTROOM,
  }

  public enum Puzzle {
    NONE,
    PIPEPUZZLE,
    WIREPUZZLE,
    PADLOCK,
    CANDLEPAINTING,
  }

  private static HashMap<AppUi, Parent> map = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, Boolean> reinitialize = new HashMap<AppUi, Boolean>();
  private static HashMap<Rooms, Pane> roomMap = new HashMap<Rooms, Pane>();
  private static HashMap<Puzzle, Pane> puzzleMap = new HashMap<Puzzle, Pane>();

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
      initialisePuzzles();
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

  public static void initialiseRooms() throws IOException {
    roomMap = new HashMap<Rooms, Pane>();
    roomMap.put(Rooms.MAINROOM, (Pane) App.loadFxml("mainroom"));
    roomMap.put(Rooms.PUZZLEROOM, (Pane) App.loadFxml("puzzleroom"));
    roomMap.put(Rooms.RIDDLEROOM, (Pane) App.loadFxml("riddleroom"));
  }

  public static Pane getRoomPane(Rooms room) {
    return roomMap.get(room);
  }

  public static void initialisePuzzles() throws IOException {
    puzzleMap = new HashMap<Puzzle, Pane>();
    puzzleMap.put(Puzzle.NONE, (Pane) App.loadFxml("none"));
    puzzleMap.put(Puzzle.WIREPUZZLE, (Pane) App.loadFxml("wirelinking"));
    puzzleMap.put(Puzzle.PIPEPUZZLE, (Pane) App.loadFxml("pipeconnecting"));
    puzzleMap.put(Puzzle.PADLOCK, (Pane) App.loadFxml("padlock"));
    puzzleMap.put(Puzzle.CANDLEPAINTING, (Pane) App.loadFxml("candlepainting"));

    GameState.puzzleSolved = new HashMap<Puzzle, BooleanProperty>();
    GameState.puzzleSolved.put(Puzzle.WIREPUZZLE, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.PIPEPUZZLE, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.PADLOCK, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.CANDLEPAINTING, new SimpleBooleanProperty(false));
  }

  public static Pane getPuzzlePane(Puzzle puzzle) {
    return puzzleMap.get(puzzle);
  }
}
