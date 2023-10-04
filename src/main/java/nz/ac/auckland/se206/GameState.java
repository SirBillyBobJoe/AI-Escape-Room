package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.controllers.GameMasterActions;
import nz.ac.auckland.se206.controllers.RiddleRoomController;
import nz.ac.auckland.se206.controllers.UserInterfaceOverlayController;
import nz.ac.auckland.se206.controllers.minigames.ComputerScreenController;
import nz.ac.auckland.se206.gpt.GameMaster;
import nz.ac.auckland.se206.items.BlueWire;
import nz.ac.auckland.se206.items.GreenWire;
import nz.ac.auckland.se206.items.Inventory;
import nz.ac.auckland.se206.items.Object;
import nz.ac.auckland.se206.items.RedWire;
import nz.ac.auckland.se206.speech.TextToSpeech;

/** Represents the state of the game. */
public class GameState {

  public static GlitchSound glitchSound = new GlitchSound();

  public static List<TextToSpeech> speechList = new ArrayList<TextToSpeech>();

  public static boolean isGameMuted = false;

  /** The infinity symbol. */
  public static String infinity = "\u221E";

  /** Indicates whether the pipe puzzle has been solved */
  public static boolean pipePuzzleSolved = false;

  /* Indicates whether the hammer has been found */
  public static boolean isHammerFound = false;

  /* Indicates whether the lighter has been found */
  public static boolean isLighterFound = false;

  /* Indicates whether the red wire has been found */
  public static boolean isRedWire = false;

  /* Indicates whether the greenWire has been found */
  public static boolean isGreenWire = false;

  /* Indicates whether the blue wire has been found */
  public static boolean isBlueWire = false;

  /** Indicates whether the player has hammered down the wall */
  public static boolean wallRemoved = false;

  /** Indicates whether the both wall panels have been removed */
  public static boolean lighter = false;

  /** Indicates whether the both wall panels have been removed */
  public static boolean wire = false;

  /** Indicates whether the player has solved the candle puzzle */
  public static boolean candlePuzzleSolved = false;

  /** Indicates whether the player has solved the 2019 riddle */
  public static boolean riddle2019Solved = false;

  /** Indicates whether the player has solved the chest puzzle */
  public static boolean chestPuzzleSolved = false;

  /** Indicates whether the player has solved the wire puzzle */
  public static boolean wirePuzzleSolved = false;

  /** Indicates whether the player has solved the padlock riddle */
  public static boolean riddlePadlockSolved = false;

  /** Indicates whether the player has solved the padlock puzzle */
  public static boolean padlockPuzzleSolved = false;

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  /** Indicates whether the wall is open. */
  public static boolean isOpenWall = false;

  public static RiddleRoomController riddleRoomController;

  public static UserInterfaceOverlayController userInterfaceOverlayController;

  public static ComputerScreenController computerScreenController;

  /** Indicates whether the riddle room is active or not */
  public static SimpleBooleanProperty riddleRoomActive = new SimpleBooleanProperty(false);

  /** List to track the order in which candles are interacted with in the game. */
  public static List<Boolean> candleOrder = new ArrayList<Boolean>();

  /** The current hints available for the game. Initially set to infinity symbol. */
  public static StringProperty hints = new SimpleStringProperty("5");

  /** Indicates if the game is in a loading state. Initially set to true. */
  public static SimpleBooleanProperty loading = new SimpleBooleanProperty(true);

  /** The time allocated for the game, in minutes. Initially set to 6. */
  public static int time = 6;

  /** The difficulty level of the game. Initially set to 'easy'. */
  public static String difficulty = "easy";

  /** Flag indicating whether the player has successfully escaped. */
  public static boolean escaped = false;

  /** The message to display upon successful escape. */
  public static String escapeMessage = "You made it out! Good job...";

  /** Flag indicating whether the last message in the chat was from the player. */
  public static boolean lastMessageWasFromPlayer = false;

  /** Singleton instance of the CountdownTimer class, initialized with a time limit. */
  public static CountdownTimer timer = CountdownTimer.getInstance(GameState.time * 60);

  /** Inventory object to hold items collected by the player. */
  public static Inventory inventory = new Inventory();

  /** Hash map holding the items present in the current room, mapped by their ImageView. */
  public static HashMap<ImageView, Object> currentRoomItems = new HashMap<ImageView, Object>();

  /** Instance of the GameMaster class, responsible for managing game logic. */
  public static GameMaster gameMaster = new GameMaster();

  /** Name of the user, initially set to 'user'. */
  public static String name = "user";

  /** Instance of SharedChat for general in-game chat features. */
  public static SharedChat chat = new SharedChat();

  /** Instance of RiddleChat for managing riddles in the game. */
  public static RiddleChat riddleChat = RiddleChat.getInstance();

  /** Stores the riddle the player is currently trying to solve */
  public static String currentRiddle = "";

  /** Instance of GameMasterActions to manage actions related to the game master. */
  public static GameMasterActions gameMasterActions;

  // Room Control

  /** Property to manage the current room the player is in. Initialized to MAINROOM. */
  public static SimpleObjectProperty<Rooms> currentRoom =
      new SimpleObjectProperty<SceneManager.Rooms>(Rooms.MAINROOM);

  /**
   * Property to manage the current puzzle the player is attempting to solve. Initialized to NONE.
   */
  public static SimpleObjectProperty<Puzzle> currentPuzzle =
      new SimpleObjectProperty<SceneManager.Puzzle>(Puzzle.NONE);

  /**
   * Map to keep track of puzzles that have been solved. Maps a Puzzle to a BooleanProperty
   * indicating its solved status.
   */
  public static Map<Puzzle, BooleanProperty> puzzleSolved = new HashMap<Puzzle, BooleanProperty>();

  // Padlock Game

  /** Array of words used in the padlock game. */
  public static String[] words = {"apple", "table", "chair", "piano", "dance"};

  /** Array of numbers used in the padlock game. */
  public static List<String> numbers = List.of("2019", "2008", "1969", "1945", "1840");

  /** Random generator for selecting words and numbers in the padlock game. */
  public static Random random = new Random();

  /** Random word selected from the 'words' array. */
  public static String randomWord = words[random.nextInt(words.length)];

  /** Random number selected from the 'numbers' array. */
  public static String randomNumbers = numbers.get(random.nextInt(numbers.size()));

  /** The answer for the padlock puzzle, initialized to a randomly selected word. */
  public static String padlockAnswer = randomWord;

  /** The answer for the passcode puzzle, initialized to a randomly selected number. */
  public static String passcodeAnswer = randomNumbers;

  /** Instance of RedWire, used for wire puzzles. */
  public static RedWire redWire = new RedWire();

  /** Instance of GreenWire, used for wire puzzles. */
  public static GreenWire greenWire = new GreenWire();

  /** Instance of BlueWire, used for wire puzzles. */
  public static BlueWire blueWire = new BlueWire();

  /** The number of slots available in the player's inventory. Initialized to 0. */
  public static int numInventorySlots = 0;

  /** The count of walls. Used for some game logic. Initialized to 3. */
  public static int wallCount = 3;

  /**
   * Property to manage the state of puzzles in the game. Initialized to true, indicating that
   * puzzles are enabled.
   */
  public static BooleanProperty isPuzzlesOn = new SimpleBooleanProperty(true);

  /** A set to store the names of puzzles. */
  public static Set<String> puzzleName = new HashSet<String>();

  /** Array of first clue words for hints or help. */
  public static String[] clueFirst = {
    "help",
    "hint",
    "what",
    "how",
    "where",
    "who",
    "why",
    "can",
    "do",
    "stuck",
    "lost",
    "tell",
    "show",
    "give",
    "need",
    "find",
    "solve",
    "figure",
    "unlock",
    "explain",
    "when",
    "im ",
    "i'm",
    "i ",
    "need",
    "hints",
    "wat",
    "opened",
    "unlock",
    "confused",
  };

  /** Array of second clue words for more specific hints or help. */
  public static String[] clueSecond = {
    "wall",
    "brick",
    "smash",
    "chest",
    "hammer",
    "key",
    "lock",
    "padlock",
    "puzzle",
    "door",
    "safe",
    "code",
    "painting",
    "candle",
    "computer",
    "water",
    "pipe",
    "riddle",
    "drip",
    "puddle",
    "cards",
    "password",
    "riddle",
    "sequence",
    "timer",
    "clue",
    "pattern",
    "combination",
    "cipher",
    "equation",
    "pipe",
    "green",
    "red",
    "blue",
    "wire",
    "numbers",
    "letters",
    "compartment",
    "light",
    "sound",
    "next",
    "proceed",
    "hints",
    "hint",
    "do",
    "stuck",
    "lighter",
    "click",
    "object",
    "help",
    "twin",
    "tower",
    "treaty",
    "waitangi",
    "year",
    "signed",
    "global",
    "financial",
    "crisis",
    "gfc",
    "WW2",
    "world",
    "war",
    "covid",
    "virus",
    "apollo",
    "moon",
    "landing",
    "space",
    "riddle",
    "apple",
    "table",
    "chair",
    "piano",
    "dance",
    "happened",
  };

  /** A rectangle shape for highlighting riddles in the UI. */
  @FXML public static Rectangle riddleGlow;

  /** A cubic curve shape for adding visual effects to the riddles in the UI. */
  @FXML public static CubicCurve riddleGlow1;

  /** Image view representing the main room in the game. */
  @FXML public static ImageView mainRoom;

  /** Image view representing the puzzle room in the game. */
  @FXML public static ImageView puzzleRoom;

  /**
   * Sets the visibility of riddle highlight indicators based on the riddleRoomActive field. If
   * riddleRoomActive is true, the riddle indicators become visible. Otherwise, they are hidden.
   */
  public static void setRiddleGlow() {
    // riddle glow logic
    if (riddleRoomActive.getValue()) {
      riddleGlow1.setVisible(true);
      riddleGlow.setVisible(true);
    } else {
      // logic for riddles
      riddleGlow1.setVisible(false);
      riddleGlow.setVisible(false);
    }
  }

  /** Resets the game to its initial state. */
  public static void resetGame() {
    if (difficulty.equals("medium")) {
      hints.set("5");
    }
    wallCount = 3;
    // Assuming you want to empty the list
    candleOrder.clear();
    escaped = false;
    // Assuming getInstance resets the timer
    timer = CountdownTimer.getInstance(time * 60);
    currentRoom.set(Rooms.MAINROOM);
    currentPuzzle.set(Puzzle.NONE);
    // reset the puzzles
    pipePuzzleSolved = false;
    wallRemoved = false;
    lighter = false;
    wire = false;
    candlePuzzleSolved = false;
    chestPuzzleSolved = false;
    riddle2019Solved = false;
    wirePuzzleSolved = false;
    // reset the locks
    riddlePadlockSolved = false;
    padlockPuzzleSolved = false;
    isRiddleResolved = false;
    // reset the keys and actives
    isKeyFound = false;
    riddleRoomActive.set(false);
    isHammerFound = false;

    inventory = new Inventory();
    escaped = false;
    gameMaster = new GameMaster();

    // reset items
    isLighterFound = false;

    isRedWire = false;

    isGreenWire = false;

    isBlueWire = false;

    // Random values
    random = new Random();
    randomWord = GameState.words[random.nextInt(words.length)];
    randomNumbers = GameState.numbers.get(random.nextInt(numbers.size()));
    padlockAnswer = GameState.randomWord;
    passcodeAnswer = GameState.randomNumbers;
    isPuzzlesOn.set(true);
    loading.set(true);
  }

  public static boolean containsHint(String msg) {
    // determines if its a hint from keywords or if its a general conversation

    for (String keyWords1 : GameState.clueFirst) {
      for (String keyWords2 : GameState.clueSecond) {
        // if contains return true
        if (msg.contains(keyWords1) && msg.contains(keyWords2)) {
          System.out.println(keyWords1 + " " + keyWords2);
          return true;
        }
      }
    }
    // if it dosent return false
    return false;
  }

  public static void cancelAllSpeech() {
    for (TextToSpeech speech : speechList) {
      speech.cancel();
    }
  }
}
