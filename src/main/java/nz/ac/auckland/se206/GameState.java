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
import nz.ac.auckland.se206.Items.BlueWire;
import nz.ac.auckland.se206.Items.GreenWire;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.Items.Object;
import nz.ac.auckland.se206.Items.RedWire;
import nz.ac.auckland.se206.SceneManager.Puzzle;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.controllers.GameMasterActions;
import nz.ac.auckland.se206.controllers.RiddleRoomController;
import nz.ac.auckland.se206.gpt.GameMaster;

/** Represents the state of the game. */
public class GameState {

  // Steps in the game, in order

  /** Indicates whether the pipe puzzle has been solved */
  public static boolean pipePuzzleSolved = false;

  /** Indicates whether the player has hammered down the wall */
  public static boolean wallRemoved = false;

  /** Indicates whether the both wall panels have been removed */
  public static boolean wallPiecesFound = false;

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

  public static RiddleRoomController riddleRoomController;

  /** Indicates whether the riddle room is active or not */
  public static boolean riddleRoomActive = false;

  @FXML public static Rectangle riddleGlow;
  @FXML public static CubicCurve riddleGlow1;
  @FXML public static ImageView mainRoom;
  @FXML public static ImageView puzzleRoom;

  public static void setRiddleGlow() {
    if (riddleRoomActive) {
      riddleGlow1.setVisible(true);
      riddleGlow.setVisible(true);
    } else {
      riddleGlow1.setVisible(false);
      riddleGlow.setVisible(false);
    }
  }

  public static List<Boolean> candleOrder = new ArrayList<Boolean>();

  public static StringProperty hints = new SimpleStringProperty("\u221E");

  public static int time = 6;
  public static String difficulty = "easy";

  public static boolean escaped = false;
  public static String escapeMessage = "You made it out! Good job...";
  public static boolean lastMessageWasFromPlayer = false;

  public static CountdownTimer timer = CountdownTimer.getInstance(GameState.time * 60);
  public static Inventory inventory = new Inventory();
  public static HashMap<ImageView, Object> currentRoomItems = new HashMap<ImageView, Object>();
  public static GameMaster gameMaster = new GameMaster();
  public static String name = "user";
  public static SharedChat chat = new SharedChat();
  public static RiddleChat riddleChat = new RiddleChat();
  public static GameMasterActions gameMasterActions;
  // Room Control
  public static SimpleObjectProperty<Rooms> currentRoom =
      new SimpleObjectProperty<SceneManager.Rooms>(Rooms.MAINROOM);
  public static SimpleObjectProperty<Puzzle> currentPuzzle =
      new SimpleObjectProperty<SceneManager.Puzzle>(Puzzle.NONE);
  public static Map<Puzzle, BooleanProperty> puzzleSolved = new HashMap<Puzzle, BooleanProperty>();

  // Padlock Game
  static String[] words = {"apple", "table", "chair", "piano", "dance"};
  static Random random = new Random();
  static int randomIndex = random.nextInt(words.length);
  static String randomWord = words[randomIndex];
  public static String padlockAnswer = randomWord;

  public static RedWire redWire = new RedWire();
  public static GreenWire greenWire = new GreenWire();
  public static BlueWire blueWire = new BlueWire();

  public static int numInventorySlots = 0;

  public static int wallCount = 3;

  public static BooleanProperty isPuzzlesOn = new SimpleBooleanProperty(true);
  public static Set<String> puzzleName = new HashSet<String>();
  public static String passcodeAnswer = "2019";

  public static String[] clueFirst = {
    "help", "hint", "what", "how", "where", "who", "why", "can", "do", "stuck", "lost", "tell",
    "show", "give", "need", "find", "solve", "figure", "unlock", "explain", "im", "i'm", "i",
    "need", "hint", "wat", "opened", "unlock"
  };

  public static String[] clueSecond = {
    "wall",
    "brick",
    "smash",
    "chest",
    "hammer",
    "key",
    "lock",
    "door",
    "safe",
    "code",
    "box",
    "flashlight",
    "painting",
    "candle",
    "computer",
    "water",
    "drip",
    "puddle",
    "chessboard",
    "cards",
    "symbols",
    "dice",
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
    "trapdoor",
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
  };

  public static void resetGame() {
    wallCount = 3;
    candleOrder.clear(); // Assuming you want to empty the list
    escaped = false;
    timer = CountdownTimer.getInstance(time * 60); // Assuming getInstance resets the timer
    currentRoom.set(Rooms.MAINROOM);
    currentPuzzle.set(Puzzle.NONE);

    pipePuzzleSolved = false;
    wallRemoved = false;
    wallPiecesFound = false;
    candlePuzzleSolved = false;
    chestPuzzleSolved = false;
    riddle2019Solved = false;
    wirePuzzleSolved = false;
    riddlePadlockSolved = false;
    padlockPuzzleSolved = false;
    isRiddleResolved = false;
    isKeyFound = false;
    riddleRoomActive = false;
  }
}
