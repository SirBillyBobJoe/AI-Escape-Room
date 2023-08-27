package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.util.Duration;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CountdownTimer {

  private static CountdownTimer countdownTimer = null;

  public static CountdownTimer getInstance(int time) {
    if (countdownTimer == null) {
      countdownTimer = new CountdownTimer(time);
    }
    return countdownTimer;
  }

  private Timeline timeline;
  private final IntegerProperty timeSeconds = new SimpleIntegerProperty();
  private TextToSpeech tts = TextToSpeech.getInstance();

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

                  Platform.exit();
                  System.exit(0);
                });
          }
        });
  }

  public void start() {
    timeline.playFromStart();
  }

  public void stop() {
    timeline.stop();
  }

  public IntegerProperty timeSecondsProperty() {
    return timeSeconds;
  }

  public void setTimeSecondsProperty(int time) {
    this.timeSeconds.set(time);
  }
}
