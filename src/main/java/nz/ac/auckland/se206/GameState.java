package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.Items.Object;
import nz.ac.auckland.se206.SceneManager.Rooms;
import nz.ac.auckland.se206.gpt.GameMaster;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  /** Indicates whether the riddle room is active or not */
  public static boolean riddleRoomActive = false;

  public static StringProperty hints = new SimpleStringProperty("\u221E");

  public static int time = 2;
  public static String difficulty = "easy";

  public static boolean escaped = false;
  public static String escapeMessage = "You made it out! Good job...";

  public static CountdownTimer timer = CountdownTimer.getInstance(GameState.time * 60);
  public static Inventory inventory = new Inventory();
  public static HashMap<ImageView, Object> currentRoomItems = new HashMap<ImageView, Object>();
  public static GameMaster gameMaster = new GameMaster();
  public static String name = "user";
  public static SharedChat chat = new SharedChat();
  public static SimpleObjectProperty<Rooms> currentRoom =
      new SimpleObjectProperty<SceneManager.Rooms>(Rooms.MAINROOM);

  public static int numInventorySlots = 0;

  public static String[] clueFirst = {
    "help", "hint", "what", "how", "where", "who", "why", "can", "do", "stuck", "lost", "tell",
    "show", "give", "need", "find", "solve", "figure", "unlock", "explain", "Im", "I'm"
  };

  public static String[] clueSecond = {
    "key",
    "lock",
    "door",
    "safe",
    "code",
    "map",
    "box",
    "book",
    "flashlight",
    "mirror",
    "painting",
    "clock",
    "compass",
    "notebook",
    "candles",
    "chessboard",
    "cards",
    "symbols",
    "dice",
    "magnifier",
    "password",
    "riddle",
    "sequence",
    "timer",
    "clue",
    "pattern",
    "combination",
    "lever",
    "switch",
    "cipher",
    "equation",
    "maze",
    "colors",
    "numbers",
    "letters",
    "compartment",
    "trapdoor",
    "light",
    "sound",
    "next",
    "proceed",
    "hints",
    "do",
    "scroll",
    "stuck"
  };
}
