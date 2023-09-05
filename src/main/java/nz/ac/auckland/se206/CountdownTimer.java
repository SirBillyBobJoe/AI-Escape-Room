package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Implements a countdown timer for the game. Singleton pattern is applied to ensure only one
 * instance of the timer.
 */
public class CountdownTimer {

  private static CountdownTimer countdownTimer = null;

  /**
   * Returns the singleton instance of the CountdownTimer class.
   *
   * @param time The time (in seconds) for the countdown.
   * @return The singleton instance of CountdownTimer.
   */
  public static CountdownTimer getInstance(int time) {
    if (countdownTimer == null) {
      countdownTimer = new CountdownTimer(time);
    }
    return countdownTimer;
  }

  private Timeline timeline;
  private final IntegerProperty timeSeconds = new SimpleIntegerProperty();
  private TextToSpeech tts = TextToSpeech.getInstance();

  /**
   * Private constructor for initializing a new CountdownTimer.
   *
   * @param time The time (in seconds) for the countdown.
   */
  private CountdownTimer(int time) {
    timeSeconds.set(time);
    timeline = new Timeline();
    timeline.setCycleCount(time);
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1), e -> timeSeconds.set(timeSeconds.get() - 1)));
    timeSeconds.addListener(
        (obs, oldTime, newTime) -> {
          if (newTime.intValue() % 30 == 0 && newTime.intValue() != 0) {
            Task<Void> task =
                new Task<Void>() {

                  @Override
                  protected Void call() throws Exception {

                    tts.speak(String.valueOf(newTime.intValue()) + "seconds left.");

                    return null;
                  }
                };

            new Thread(task).start();
          }
          if (newTime.intValue() <= 5) {
            Task<Void> task =
                new Task<Void>() {

                  @Override
                  protected Void call() throws Exception {

                    tts.speak(String.valueOf(newTime.intValue()));

                    return null;
                  }
                };

            new Thread(task).start();
          }
          if (newTime.intValue() == 0) {
            this.stop();
            Platform.runLater(
                () -> {
                  Task<Void> task =
                      new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {

                          tts.speak(String.valueOf("You Have Lost"));
                          ;
                          return null;
                        }
                      };

                  new Thread(task).start();

                  try {
                    SceneManager.setReinitialise(AppUi.ENDSCREEN);
                    App.setUserInterface(AppUi.ENDSCREEN);
                  } catch (IOException e1) {

                    e1.printStackTrace();
                  }
                });
          }
        });
  }

  /** Starts the countdown timer. */
  public void start() {
    timeline.playFromStart();
  }

  /** Stops the countdown timer. */
  public void stop() {
    timeline.stop();
  }

  /**
   * Retrieves the property representing the time left in seconds.
   *
   * @return The time left in seconds as an IntegerProperty.
   */
  public IntegerProperty timeSecondsProperty() {
    return timeSeconds;
  }

  /**
   * Sets the time left for the countdown timer.
   *
   * @param time The time (in seconds) to set for the countdown.
   */
  public void setTimeSecondsProperty(int time) {
    this.timeSeconds.set(time);
  }
}
