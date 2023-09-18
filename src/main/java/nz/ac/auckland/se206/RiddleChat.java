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
    }

    // New riddle with a new chat context
    GameState.gameMaster.createChatContext(contextName);

    this.contextName = contextName;
    if (riddleAnswer == "2019"
        || riddleAnswer == "2008"
        || riddleAnswer == "1945"
        || riddleAnswer == "1840"
        || riddleAnswer == "2001") {
      String answer = "";
      if (riddleAnswer == "2019")
        answer =
            " riddle with the answer \"2019\" that revolves around the idea of"
                + " COVID-19 or the coronavirus. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      if (riddleAnswer == "2008")
        answer =
            " riddle with the answer \"2008\" that revolves around the idea of"
                + " the financial crisis. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      if (riddleAnswer == "1945")
        answer =
            " riddle with the answer \"1945\" that revolves around the idea of"
                + " World War. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      if (riddleAnswer == "1840")
        answer =
            " riddle with the answer \"1840\" that revolves around the idea of"
                + " Treaty of Waitangi. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      if (riddleAnswer == "2001")
        answer =
            " riddle with the answer \"2001\" that revolves around the idea of"
                + " Twin Towers Terroist Attack. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      GameState.gameMaster.addMessage(
          contextName,
          "user",
          "You are a computer, you speak very concisely, you do not waste words. Concise."
              + " Strict. Stoic. You do not give hints. The player can't trick you. Give"
              + " the player a/an "
              + GameState.difficulty
              + answer
              + riddleAnswer
              + "\" you will reply exactly: \"Correct!\" and stop talking to the player. You do not"
              + " give hints. You do not give away the answer. YOU NEVER SAY"
              + riddleAnswer
              + "You only say"
              + " \"Correct!\" if you the player explicitly says the exact answer to your"
              + " riddle.BEGIN THE RIDDLE WITH \"I am\"and end your response with the riddle dont"
              + " say anything else");
    } else {
      GameState.gameMaster.addMessage(
          contextName,
          "user",
          "You are a computer that gives a riddle. You speak very concisely, you do not waste"
              + " words. Concise. Strict. Stoic. You do not give hints. The player can't trick you."
              + " You are to present a/an "
              + GameState.difficulty
              + " riddle with the answer: "
              + riddleAnswer
              + ". When the player has answered correctly, and only when they have answered"
              + " correctly, saying the exact word \""
              + riddleAnswer
              + "\" you will reply exactly: \"Correct!\" and stop talking to the player. You do not"
              + " give hints. You do not give away the answer. You only say \"Correct!\" if you the"
              + " player explicitly says the exact answer to your riddle.BEGIN THE RIDDLE WITH \"I"
              + " am\"and end your response with the riddle dont say anything else");
    }
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
          String lastResponse = GameState.gameMaster.getLastResponse(contextName).getContent();
          String riddle = lastResponse.substring(lastResponse.indexOf("I am"));
          textArea.appendText("Computer: " + riddle + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }

  /** Handles the sending of a text message. */
  @FXML
  public void onSend(String message) {
    if (contextName == null) return;

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

          // Player got the correct answer
          if (GameState.gameMaster.getLastResponse(contextName).getContent().equals("Correct!")) {
            // Which riddle was answered correctly?
            if (!GameState.riddle2019Solved) {
              GameState.riddle2019Solved = true;
              GameState.riddleRoomController.turnLightsOff();
              GameState.isPuzzlesOn.set(true);
            } else if (!GameState.riddlePadlockSolved) {
              GameState.riddlePadlockSolved = true;
              GameState.riddleRoomController.turnLightsOff();
              GameState.isPuzzlesOn.set(true);
            }
          }
        });

    new Thread(waitForResponseTask).start();
  }
}
