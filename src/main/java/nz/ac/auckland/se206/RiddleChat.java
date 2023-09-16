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

  public void newRiddle(String contextName) {
    GameState.gameMaster.createChatContext(contextName);
    this.contextName = contextName;

    textArea.clear();
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
              GameState.gameMaster.getLastResponse(contextName).getContent() + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }
}
