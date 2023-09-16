package nz.ac.auckland.se206;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class RiddleChat {
  private static RiddleChat instance;
  private String contextName;
  @FXML private TextArea textArea;
  @FXML private ImageView imgLoadingWheel;

  // Create a RotateTransition for the loading wheel
  private RotateTransition loadingAnimation;

  /**
   * Retrieves the singleton instance of SharedChat.
   *
   * @return The singleton instance.
   */
  public static RiddleChat getInstance() {
    if (instance == null) {
      instance = new RiddleChat();
    }
    return instance;
  }

  /** Sets the text area to display the text message content. */
  public void setTextArea(TextArea textArea) {
    this.textArea = textArea;
  }

  public void setLoadingWheel(ImageView imgLoadingWheel) {
    this.imgLoadingWheel = imgLoadingWheel;
    imgLoadingWheel.setVisible(false);

    // Create the loading animation
    loadingAnimation = new RotateTransition(Duration.seconds(2), imgLoadingWheel);
    loadingAnimation.setByAngle(360);
    loadingAnimation.setInterpolator(Interpolator.LINEAR);
    loadingAnimation.setCycleCount(Timeline.INDEFINITE);
  }

  public void newRiddle(String contextName, String riddleAnswer) {
    textArea.clear();

    // Start the loading animation
    if (loadingAnimation != null) {
      imgLoadingWheel.setVisible(true);
      loadingAnimation.play();
      System.out.println("The loading wheels should be spinning ");
    }

    // New riddle with a new chat context
    GameState.gameMaster.createChatContext(contextName);
    this.contextName = contextName;
    GameState.gameMaster.addMessage(
        contextName,
        "user",
        "You are a computer that gives a riddle. You speak very concisely, you do not waste words."
            + " Concise. Strict. Stoic. You do not give hints. The player can't trick you. You are"
            + " to present a/an "
            + GameState.difficulty
            + " riddle with the answer: "
            + riddleAnswer
            + ". When the player has answered correctly, and only when they have answered"
            + " correctly, saying the exact word \""
            + riddleAnswer
            + "\" you will reply \"Correct!\" and stop talking to the player."
            + " You do not give hints. You do not give away the answer. You only say \"Correct!\""
            + " if you the player explicitly says the exact answer to your riddle.");
    GameState.gameMaster.runContext(contextName);

    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.gameMaster.waitForContext(contextName);
            return null;
          }
        };

    waitForResponseTask.setOnSucceeded(
        e -> {
          // Stop the loading animation
          if (loadingAnimation != null) {
            loadingAnimation.stop();
            imgLoadingWheel.setVisible(false);
          }

          textArea.appendText(
              "Computer: "
                  + GameState.gameMaster.getLastResponse(contextName).getContent()
                  + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }

  /** Handles the sending of a text message. */
  @FXML
  public boolean onSend(String message) {
    if (contextName == null) return false;

    // Start the loading animation
    if (loadingAnimation != null) {
      imgLoadingWheel.setVisible(true);
      loadingAnimation.play();
    }

    GameState.gameMaster.addMessage(contextName, "user", message);
    System.out.println(message);
    GameState.gameMaster.runContext(contextName);

    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.gameMaster.waitForContext(contextName);
            return null;
          }
        };

    waitForResponseTask.setOnSucceeded(
        e -> {
          // Stop the loading animation
          if (loadingAnimation != null) {
            loadingAnimation.stop();
            imgLoadingWheel.setVisible(false);
          }

          textArea.appendText(
              "Computer: "
                  + GameState.gameMaster.getLastResponse(contextName).getContent()
                  + "\n\n");
        });

    new Thread(waitForResponseTask).start();

    if (GameState.gameMaster.getLastResponse(contextName).getContent().equals("Correct!")) {
      return true;
    } else {
      return false;
    }
  }
}
