package nz.ac.auckland.se206;

import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MouseClick {

  private Clip clip;

  public MouseClick() {
    try {
      InputStream audioSrc = getClass().getResourceAsStream("/sounds/mouseClick.wav");
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
      clip = AudioSystem.getClip();
      clip.open(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void play() {
    if (clip.isRunning()) {
      clip.stop(); // Stop the player if it is still running
    }
    clip.setFramePosition(0); // Must always rewind!
    clip.start();
  }
}
