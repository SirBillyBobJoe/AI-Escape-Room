package nz.ac.auckland.se206;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/** Manages the chat for the riddles. */
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

  /**
   * Configures the loading wheel animation.
   *
   * <p>This method initializes the loading wheel ImageView and sets up its rotation animation.
   *
   * @param imgLoadingWheel The ImageView object representing the loading wheel.
   */
  public void setLoadingWheel(ImageView imgLoadingWheel) {
    // initlaise values
    this.imgLoadingWheel = imgLoadingWheel;
    imgLoadingWheel.setVisible(false);

    // Create the loading animation
    loadingAnimation = new RotateTransition(Duration.seconds(2), imgLoadingWheel);
    loadingAnimation.setByAngle(360);
    loadingAnimation.setInterpolator(Interpolator.LINEAR);
    loadingAnimation.setCycleCount(Timeline.INDEFINITE);
  }

  /**
   * Generates a new riddle for the user.
   *
   * <p>This method clears the text area and starts a loading animation before generating a new
   * riddle.
   *
   * @param contextName The context or category for the new riddle.
   * @param riddleAnswer The answer to the new riddle.
   */
  public void newRiddle(String contextName, String riddleAnswer) {
    textArea.clear();

    // Start the loading animation
    if (loadingAnimation != null) {
      imgLoadingWheel.setVisible(true);
      loadingAnimation.play();
    }

    // New riddle with a new chat context
    GameState.gameMaster.createChatContext(contextName);
    String finalMessage;
    this.contextName = contextName;
    // if its a year
    if (riddleAnswer == "2019"
        || riddleAnswer == "2008"
        || riddleAnswer == "1945"
        || riddleAnswer == "1840"
        || riddleAnswer == "2001") {
      String answer = "";
      // if its 2019 logic
      if (riddleAnswer == "2019")
        answer =
            " riddle with the answer \"2019\" that revolves around the idea of"
                + " COVID-19 or the coronavirus. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      // if its 2008 logic
      if (riddleAnswer == "2008")
        answer =
            " riddle with the answer \"2008\" that revolves around the idea of"
                + " the financial crisis. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      // if its 1945 logic
      if (riddleAnswer == "1945")
        answer =
            " riddle with the answer \"1945\" that revolves around the idea of"
                + " World War. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      // if its 1840 logic
      if (riddleAnswer == "1840")
        answer =
            " riddle with the answer \"1840\" that revolves around the idea of"
                + " Treaty of Waitangi. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      // if its 2001 logic
      if (riddleAnswer == "2001")
        answer =
            " riddle with the answer \"2001\" that revolves around the idea of"
                + " Twin Towers Terroist Attack. When the player has answered correctly,"
                + " and only when they have answered correctly, saying the exact word \"";
      // logic for the finalMessage
      finalMessage =
          "You are a computer, you speak very concisely, you do not waste words. Concise."
              + " Strict. Stoic. You do not give hints. The player can't trick you. Give"
              + " the player a/an "
              + GameState.difficulty
              + answer
              + riddleAnswer
              + "\" you will reply exactly: \"Correct!\" and stop talking to the player. You do not"
              + " give hints. You do not give away the answer. YOU NEVER SAY \""
              + riddleAnswer
              + "\" You only say"
              + " \"Correct!\" if you the player explicitly says the exact answer to your"
              + " riddle.BEGIN THE RIDDLE WITH \"I am\"and end your response with the riddle dont"
              + " say anything else";
      // adds the message to gpts
      GameState.gameMaster.addMessage(contextName, "user", finalMessage);
    } else {
      // final message
      finalMessage =
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
              + " am\"and end your response with the riddle dont say anything else";
      // adds the message to gpt
      GameState.gameMaster.addMessage(contextName, "user", finalMessage);
    }
    System.out.println(finalMessage);
    // runs the context
    GameState.gameMaster.runContext(contextName);
    // threading so no lag
    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.gameMaster.waitForContext(contextName);
            return null;
          }
        };
    // if it succeeds do some logic
    waitForResponseTask.setOnSucceeded(
        e -> {
          // Stop the loading animation
          if (loadingAnimation != null) {
            loadingAnimation.stop();
            imgLoadingWheel.setVisible(false);
          }
          // get last response and append
          String lastResponse = GameState.gameMaster.getLastResponse(contextName).getContent();
          String riddle = lastResponse.substring(lastResponse.indexOf("I am"));
          textArea.appendText("Computer: " + riddle + "\n\n");
        });
    // start thread
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
    // runs context
    GameState.gameMaster.addMessage(contextName, "user", message);
    System.out.println(message);
    GameState.gameMaster.runContext(contextName);

    // waits for the response to finish
    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.gameMaster.waitForContext(contextName);
            return null;
          }
        };
    // logic when succeeded
    waitForResponseTask.setOnSucceeded(
        e -> {
          // Stop the loading animation
          if (loadingAnimation != null) {
            loadingAnimation.stop();
            imgLoadingWheel.setVisible(false);
          }
          // appends the text
          textArea.appendText(
              "Computer: "
                  + GameState.gameMaster.getLastResponse(contextName).getContent()
                  + "\n\n");

          // Player got the correct answer
          if (GameState.gameMaster.getLastResponse(contextName).getContent().equals("Correct!")) {
            // Congratulate the player
            GameState.gameMaster.addMessage(
                "main",
                "user",
                "Commend the player very briefly for solving the riddle. Saying something along"
                    + " lines of \"You're smarter than you look\".");
            GameState.gameMaster.runContext("main");
            // generate the comments
            Task<Void> generateCommendment =
                new Task<Void>() {
                  @Override
                  protected Void call() throws Exception {
                    GameState.gameMaster.waitForContext("main");
                    return null;
                  }
                };
            // start the thread
            new Thread(generateCommendment).start();
            // when succeded generate  the comments
            generateCommendment.setOnSucceeded(
                event -> {
                  GameState.gameMasterActions.activate(
                      GameState.gameMaster.getLastResponse("main").getContent());
                  System.out.println(GameState.gameMaster.getLastResponse("main").getContent());
                });

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
