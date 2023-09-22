package nz.ac.auckland.se206;

import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Manages mouse click sounds for the game.
 *
 * <p>This class handles the audio functionality for mouse clicks within the game. It uses the Java
 * Sound API to play the "mouseClick.wav" sound file.
 */
public class MouseClick {

  private Clip clip;

  /**
   * Constructs a new MouseClick object.
   *
   * <p>Upon construction, this object will load the "mouseClick.wav" sound file and prepare it for
   * playback.
   */
  public MouseClick() {
    try {
      // get the sounds
      InputStream audioSrc = getClass().getResourceAsStream("/sounds/mouseClick.wav");
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
      // play the clip
      clip = AudioSystem.getClip();
      clip.open(inputStream);
    } catch (Exception e) {
      System.out.println("Uh oh, stinky...");
    }
  }

  /**
   * Plays the mouse click sound.
   *
   * <p>This method stops any currently playing mouse click sound, rewinds it, and then plays it
   * from the beginning.
   */
  public void play() {
    try {
      if (clip.isRunning()) {
        clip.stop(); // Stop the player if it is still running
      }
      clip.setFramePosition(0); // Must always rewind!
      clip.start();
    } catch (Exception e) {
      System.out.println("Uh oh, stinky...");
    }
  }
}
