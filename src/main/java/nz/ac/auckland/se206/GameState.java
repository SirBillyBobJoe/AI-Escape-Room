package nz.ac.auckland.se206;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.gpt.GameMaster;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  public static StringProperty hints = new SimpleStringProperty("\u221E");
  public static int time = 2;
  public static String difficulty = "easy";

  public static CountdownTimer timer = CountdownTimer.getInstance(GameState.time * 60);
  public static Inventory inventory = new Inventory();
  public static GameMaster gameMaster = new GameMaster();
  public static String name = "user";
  public static SharedChat sharedChat;

  public static String[] clueList = {
    "help", "hint", "what", "how", "where", "who", "why", "can", "do", "next", "stuck", "lost",
    "tell", "show", "give", "need", "find", "solve", "figure", "unlock", "explain"
  };
}
