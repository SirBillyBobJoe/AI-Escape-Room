package nz.ac.auckland.se206;

import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Manages sounds for the game.
 *
 * <p>This class handles the audio functionality for sounds within the game. It uses the Java Sound
 * API to play the sound file.
 */
public class Sound {

  private Clip clip;

  /**
   * Constructs a new sound object.
   *
   * <p>Upon construction, this object will load the "sound.wav" sound file and prepare it for
   * playback.
   */
  public Sound(String sound) {
    try {
      // get the sounds
      InputStream audioSrc = getClass().getResourceAsStream(sound);
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
      // play the clip
      clip = AudioSystem.getClip();
      clip.open(inputStream);
    } catch (Exception e) {
      System.out.println("Uh oh, stinky...");
    }
  }

  /**
   * Plays the sound.
   *
   * <p>This method stops any currently playing sound, rewinds it, and then plays it from the
   * beginning.
   */
  public void play() {
    if (GameState.isGameMuted) {
      return;
    }
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

  /**
   * Stops the sound.
   *
   * <p>This method stops any currently playing sound, rewinds it.
   */
  public void stop() {
    clip.setFramePosition(0); // Must always rewind!
    clip.stop();
  }
}
