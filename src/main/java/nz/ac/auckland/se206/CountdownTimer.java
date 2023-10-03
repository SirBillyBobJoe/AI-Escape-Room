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
    GameState.speechList.add(tts);
    timeSeconds.set(time);
    timeline = new Timeline();
    // set the time count
    timeline.setCycleCount(time);
    // add frames
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1), e -> timeSeconds.set(timeSeconds.get() - 1)));
    timeSeconds.addListener(
        (obs, oldTime, newTime) -> {
          if (newTime.intValue() % 60 == 0 && newTime.intValue() != 0) {
            // starts the tasks
            Task<Void> task =
                new Task<Void>() {

                  @Override
                  protected Void call() throws Exception {
                    // uses tts
                    tts.speak(String.valueOf(newTime.intValue() / 60) + "minutes left.");

                    return null;
                  }
                };
            // starts the thread
            new Thread(task).start();
          }
          // if less than 5 logic
          if (newTime.intValue() <= 5) {
            Task<Void> task =
                new Task<Void>() {

                  @Override
                  protected Void call() throws Exception {
                    // tts speaks value
                    tts.speak(String.valueOf(newTime.intValue()));

                    return null;
                  }
                };

            new Thread(task).start();
          }
          // stops when = 0
          if (newTime.intValue() == 0) {
            this.stop();
            Platform.runLater(
                () -> {
                  // starts new task
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
                  // skips to endscreen when 0
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
