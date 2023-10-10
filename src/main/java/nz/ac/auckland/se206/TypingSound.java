package nz.ac.auckland.se206;


/**
 * Manages mouse click sounds for the game.
 *
 * <p>This class handles the audio functionality for mouse clicks within the game. It uses the Java
 * Sound API to play the "mouseClick.wav" sound file.
 */
public class TypingSound extends Sound {

  /**
   * Constructs a new MouseClick object.
   *
   * <p>Upon construction, this object will load the "mouseClick.wav" sound file and prepare it for
   * playback.
   *
   * @return
   */
  public TypingSound() {

    super("/sounds/typing.wav");
  }
}
