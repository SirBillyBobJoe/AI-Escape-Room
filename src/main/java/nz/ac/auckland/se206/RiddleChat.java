package nz.ac.auckland.se206;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class RiddleChat {
  private static RiddleChat instance;
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

  /** Handles the sending of a text message. */
  @FXML
  public void onSend(String message) {
    GameState.gameMaster.addMessage("riddle", "user", message);
    System.out.println(message);
    GameState.gameMaster.runContext("riddle");

    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.gameMaster.waitForContext("riddle");
            return null;
          }
        };

    waitForResponseTask.setOnSucceeded(
        e -> {
          textArea.setText(GameState.gameMaster.getLastResponse("riddle").getContent() + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }
}
