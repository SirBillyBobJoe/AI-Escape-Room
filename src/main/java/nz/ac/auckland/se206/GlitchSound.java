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
public class GlitchSound {

  private Clip clip;

  /**
   * Constructs a new MouseClick object.
   *
   * <p>Upon construction, this object will load the "mouseClick.wav" sound file and prepare it for
   * playback.
   *
   * @return
   */
  public GlitchSound() {
    // pick a random sound (1 or 2)
    int sound = (int) (Math.random() * 2) + 1;

    try {
      // get the sounds
      if (sound == 1) {
        InputStream audioSrc = getClass().getResourceAsStream("/sounds/glitchSound.wav");
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
        // play the clip
        clip = AudioSystem.getClip();
        clip.open(inputStream);
      } else {
        InputStream audioSrc = getClass().getResourceAsStream("/sounds/droneSound.wav");
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
        // play the clip
        clip = AudioSystem.getClip();
        clip.open(inputStream);
      }
    } catch (Exception e) {
      e.printStackTrace();
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
