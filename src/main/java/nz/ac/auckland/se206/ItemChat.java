package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

public class ItemChat {
  private static ItemChat instance;

  /**
   * Returns the singleton instance of ItemChat.
   *
   * @return The singleton instance.
   */
  public static ItemChat getInstance() {
    if (instance == null) {
      instance = new ItemChat();
    }
    return instance;
  }

  private Thread currentThread;

  /** Private constructor for the singleton pattern. */
  private ItemChat() {}

  /**
   * Prints a chat message to the specified TextArea simulating typing.
   *
   * <p>Clears the TextArea and prints the message one character at a time to simulate typing. If a
   * message is currently being typed, it will be interrupted.
   *
   * @param itemChat The TextArea where the chat message will be displayed.
   * @param message The message to be printed.
   */
  public void printChatMessage(TextArea itemChat, String message) {
    if (message == null || message.isEmpty()) {
      GameState.type.stop();
      return;
    }
    // Interrupt previous thread if it's still running
    if (currentThread != null && currentThread.isAlive()) {
      currentThread.interrupt();
    }
    // clears the items chat
    itemChat.clear();
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // loops through and check the chat and then cancell
            GameState.type.play();
            for (char c : message.toCharArray()) {
              if (isCancelled()) {
                break;
              }
              String currentChar = String.valueOf(c);
              Platform.runLater(() -> itemChat.appendText(currentChar));
              // Sleep for 20 ms to simulate typing delay
              Thread.sleep(20);
            }
            GameState.type.stop();
            return null;
          }
        };
    // starts the thread and runs later for clear
    currentThread = new Thread(task);
    currentThread.setDaemon(true);
    task.setOnCancelled(e -> Platform.runLater(() -> itemChat.clear()));
    currentThread.start();
  }
}
