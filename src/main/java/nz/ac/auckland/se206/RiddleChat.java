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

  @FXML private TextArea textArea;
  @FXML private ImageView imgLoadingWheel;
  private String contextName;

  // Create a RotateTransition for the loading wheel
  private RotateTransition loadingAnimation;

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
  public void newRiddle(String riddleAnswer) {
    System.out.println("Generate new riddle");
    textArea.clear();

    // Start the loading animation
    if (loadingAnimation != null) {
      imgLoadingWheel.setVisible(true);
      loadingAnimation.play();
    }

    // New riddle with a new chat context
    final String generateContextName = "generate_riddle";
    GameState.gameMaster.createChatContext(generateContextName);
    String finalMessage;
    // if its a year
    if (GameState.numbers.contains(riddleAnswer)) {
      String answer = "";
      // if its 2019 logic
      if (riddleAnswer == "2019") {
        answer = "\"2019\" that revolves around the idea of COVID-19 or the coronavirus.";
      } else if (riddleAnswer == "2008") {
        // if its 2008 logic
        answer = "\"2008\" that revolves around the idea of the global financial crisis.";
      } else if (riddleAnswer == "1945") {
        // if its 1945 logic
        answer = "\"1945\" that revolves around the idea of World War 2.";
      } else if (riddleAnswer == "1840") {
        // if its 1840 logic
        answer = "\"1840\" that revolves around the idea of the Treaty of Waitangi.";
      } else if (riddleAnswer == "1969") {
        // if its 1969 logic
        answer = "\"1969\" that revolves around the idea of the Apollo moon landing";
      }
      // logic for the finalMessage
      finalMessage =
          "Give the player a short concise riddle with the answer "
              + answer
              + " Begin the riddle with \"I am\". Do not include the answer in the riddle.";
      // adds the message to gpts
      GameState.gameMaster.addMessage(generateContextName, "user", finalMessage);
    } else {
      // final message
      finalMessage =
          "Give the player a short consice riddle with the answer "
              + riddleAnswer
              + "Begin the riddle with \"I am\". Do not include the answer in the riddle.";
      // adds the message to gpt
      GameState.gameMaster.addMessage(generateContextName, "user", finalMessage);
    }
    System.out.println(finalMessage);
    // runs the context
    GameState.gameMaster.runContext(generateContextName);
    // threading so no lag
    // if it succeeds do some logic
    // start thread
    new Thread(
            () -> {
              GameState.gameMaster.waitForContext(generateContextName);

              // Stop the loading animation
              if (loadingAnimation != null) {
                loadingAnimation.stop();
                imgLoadingWheel.setVisible(false);
              }
              // get last response and append
              String lastResponse =
                  GameState.gameMaster.getLastResponse(generateContextName).getContent();
              String riddle = lastResponse;
              textArea.appendText("Computer: " + riddle + "\n\n");

              // Create the answering context
              contextName = riddleAnswer;
              GameState.gameMaster.createChatContext(contextName);
              String answeringPrompt =
                  "You are a computer, you speak very concisely, you do not waste"
                      + " words. Concise. Strict. Stoic. The player has been given the riddle \""
                      + riddle
                      + "\" with the only correct answer being \""
                      + riddleAnswer
                      + "\". The player is trying to guess the correct answer by talking to you."
                      + " You must respond to the player's guesses with \"Correct!\" or"
                      + " \"Incorrect\". Do not give any hints. The player can't trick you.";
              System.out.println(answeringPrompt);
              GameState.gameMaster.addMessage(contextName, "user", answeringPrompt);
            })
        .start();
  }

  /** Handles the sending of a text message. */
  @FXML
  public void onSend(String messageRaw) {
    if (contextName == null) {
      return;
    }

    // Start the loading animation
    if (loadingAnimation != null) {
      imgLoadingWheel.setVisible(true);
      loadingAnimation.play();
    }
    String message = "The player asked you \"" + messageRaw + "\".";
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
