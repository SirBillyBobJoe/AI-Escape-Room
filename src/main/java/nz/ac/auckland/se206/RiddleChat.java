package nz.ac.auckland.se206;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class RiddleChat {
  private static RiddleChat instance;
  private String contextName;
  @FXML private TextArea textArea;

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

  public void newRiddle(String contextName, String riddleAnswer) {
    textArea.clear();

    // New riddle with new chat context
    GameState.gameMaster.createChatContext(contextName);
    this.contextName = contextName;
    GameState.gameMaster.addMessage(
        contextName,
        "user",
        "You are a computer that gives a riddle. You speak very concisely, you do not waste words."
            + " Concise. Strict. Stoic. You do not give hints. You are to present a/an "
            + GameState.difficulty
            + " riddle with the answer: "
            + riddleAnswer
            + ". When the player guesses correctly, say \"Correct!\".");
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
          textArea.appendText(
              "Computer: "
                  + GameState.gameMaster.getLastResponse(contextName).getContent()
                  + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }

  /** Handles the sending of a text message. */
  @FXML
  public void onSend(String message) {
    if (contextName == null) return;
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
          textArea.appendText(
              "Computer: "
                  + GameState.gameMaster.getLastResponse(contextName).getContent()
                  + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }
}
