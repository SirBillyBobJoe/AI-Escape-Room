package nz.ac.auckland.se206;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  /** Indicates whether the key has been found. */
  public static boolean isKeyFound = false;

  public static String hints = "Unlimited";
  public static int time = 2;
  public static String difficulty = "easy";

  public static CountdownTimer timer = CountdownTimer.getInstance(GameState.time * 60);
}
