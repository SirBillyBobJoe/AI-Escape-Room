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

  /** Enumerates different rooms in the application. */
  public enum Rooms {
    MAINROOM,
    RIDDLEROOM,
    PUZZLEROOM,
    LEFTROOM,
  }

  /** Enumerates different puzzles in the application. */
  public enum Puzzle {
    NONE,
    PIPEPUZZLE,
    WIREPUZZLE,
    PADLOCK,
    CANDLEPAINTING,
    COMPUTERSCREEN,
    PASSCODE,
  }

  // initialise the puzzles and rooms
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
      // initialises the rooms
      if (ui.equals(AppUi.UIOVERLAY)) {
        System.out.println("Initialise Rooms");
        // If this room should be re-initialized, create a new instance of the puzzles.
        initialisePuzzles();
        initialiseRooms();
      }
      // reintilise rooms
      reinitialize.put(ui, false);
      // assuming the method is static
      Parent newUserInterface = App.loadFxml(ui.toString().toLowerCase());
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

  /**
   * Initializes room panes by loading them from their corresponding FXML files.
   *
   * <p>This method populates the roomMap with panes that correspond to different rooms in the game.
   *
   * @throws IOException If the FXML files for rooms can't be loaded.
   */
  public static void initialiseRooms() throws IOException {
    roomMap = new HashMap<Rooms, Pane>();
    roomMap.put(Rooms.MAINROOM, (Pane) App.loadFxml("mainroom"));
    roomMap.put(Rooms.PUZZLEROOM, (Pane) App.loadFxml("puzzleroom"));
    roomMap.put(Rooms.RIDDLEROOM, (Pane) App.loadFxml("riddleroom"));
  }

  /**
   * Retrieves the associated Pane for a given Rooms enum value.
   *
   * @param room The Rooms enum value for which to retrieve the associated Pane.
   * @return The Pane corresponding to the given Rooms enum value, or null if the Rooms enum value
   *     is not found.
   */
  public static Pane getRoomPane(Rooms room) {
    return roomMap.get(room);
  }

  /**
   * Initializes puzzle panes and their solved states.
   *
   * <p>This method populates the puzzleMap with panes for different puzzles and sets their initial
   * solved states.
   *
   * @throws IOException If the FXML files for puzzles can't be loaded.
   */
  public static void initialisePuzzles() throws IOException {
    // add the fxml files to the hashmap
    puzzleMap = new HashMap<Puzzle, Pane>();
    puzzleMap.put(Puzzle.NONE, (Pane) App.loadFxml("none"));
    puzzleMap.put(Puzzle.WIREPUZZLE, (Pane) App.loadFxml("wirelinking"));
    puzzleMap.put(Puzzle.PIPEPUZZLE, (Pane) App.loadFxml("pipeconnecting"));
    puzzleMap.put(Puzzle.PADLOCK, (Pane) App.loadFxml("padlock"));
    puzzleMap.put(Puzzle.CANDLEPAINTING, (Pane) App.loadFxml("candlepainting"));
    puzzleMap.put(Puzzle.COMPUTERSCREEN, (Pane) App.loadFxml("computerscreen"));
    puzzleMap.put(Puzzle.PASSCODE, (Pane) App.loadFxml("passcode"));

    // add the logic for the solved puzzles to the game
    GameState.puzzleSolved = new HashMap<Puzzle, BooleanProperty>();
    GameState.puzzleSolved.put(Puzzle.WIREPUZZLE, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.PIPEPUZZLE, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.PADLOCK, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.CANDLEPAINTING, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.COMPUTERSCREEN, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.PASSCODE, new SimpleBooleanProperty(false));

    // adds the puzzles to the names
    GameState.puzzleName.add("wireBox");
    GameState.puzzleName.add("candlePainting");
    GameState.puzzleName.add("pipeGame");
    GameState.puzzleName.add("brickWall");
    GameState.puzzleName.add("lock1");
    GameState.puzzleName.add("hide1");
    GameState.puzzleName.add("hide2");
    GameState.puzzleName.add("passcode");
  }

  /**
   * Retrieves the associated Pane for a given Puzzle object.
   *
   * <p>This method looks up the Puzzle object in the puzzleMap and returns the corresponding Pane.
   *
   * @param puzzle The Puzzle object for which to retrieve the associated Pane.
   * @return The Pane associated with the given Puzzle object, or null if the Puzzle is not found.
   */
  public static Pane getPuzzlePane(Puzzle puzzle) {
    return puzzleMap.get(puzzle);
  }
}
