package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.application.Platform;
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
  }

  /** Enumerates different puzzles in the application. */
  public enum Puzzle {
    NONE,
    PIPECONNECTING,
    WIRELINKING,
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
      // reintilise rooms
      reinitialize.put(ui, false);
      // assuming the method is static
      Parent newUserInterface = App.loadFxml(ui.toString().toLowerCase());
      map.put(ui, newUserInterface);
    }
    return map.get(ui);
  }

  /** Load UIOverlay Ui in a way that it doesn't have to be run from favafx main thread. */
  public static void loadUIOverlay() {
    try {
      if (!reinitialize.containsKey(AppUi.UIOVERLAY) || reinitialize.get(AppUi.UIOVERLAY)) {
        System.out.println("Initialise Rooms");
        // If this room should be re-initialized, create a new instance of the puzzles.
        initialisePuzzles();
        initialiseRooms();
      }
      Parent uiParent = SceneManager.getUi(AppUi.UIOVERLAY);

      Platform.runLater(
          () -> {
            App.setUserInterface(AppUi.UIOVERLAY, uiParent);
          });

    } catch (IOException e) {
      e.printStackTrace();
    }
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
  private static void initialiseRooms() throws IOException {
    roomMap = new HashMap<Rooms, Pane>();
    for (Rooms room : Rooms.values()) {
      // Run each loadFxml call on the JavaFX thread seperately to avoid freezing the UI.
      try {
        roomMap.put(room, (Pane) App.loadFxml(room.toString().toLowerCase()));
      } catch (IOException e) {
        System.out.println("Error loading room: " + room.toString().toLowerCase());
        e.printStackTrace();
      }
    }
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
  private static void initialisePuzzles() throws IOException {
    // add the fxml files to the hashmap
    puzzleMap = new HashMap<Puzzle, Pane>();
    for (Puzzle puzzle : Puzzle.values()) {
      // Run each loadFxml call on the JavaFX thread seperately to avoid freezing the UI.
      try {
        puzzleMap.put(puzzle, (Pane) App.loadFxml(puzzle.toString().toLowerCase()));
      } catch (IOException e) {
        System.out.println("Error loading puzzle: " + puzzle.toString().toLowerCase());
        e.printStackTrace();
      }
    }

    // add the logic for the solved puzzles to the game
    GameState.puzzleSolved = new HashMap<Puzzle, BooleanProperty>();
    GameState.puzzleSolved.put(Puzzle.WIRELINKING, new SimpleBooleanProperty(false));
    GameState.puzzleSolved.put(Puzzle.PIPECONNECTING, new SimpleBooleanProperty(false));
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
