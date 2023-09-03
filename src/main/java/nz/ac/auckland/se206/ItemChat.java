package nz.ac.auckland.se206;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

public class ItemChat {
  private static ItemChat instance;
  private Thread currentThread;

  private ItemChat() {}

  public static ItemChat getInstance() {
    if (instance == null) {
      instance = new ItemChat();
    }
    return instance;
  }

  public void printChatMessage(TextArea itemChat, String message) {
    // Interrupt previous thread if it's still running
    if (currentThread != null && currentThread.isAlive()) {
      currentThread.interrupt();
    }
    itemChat.clear();
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            for (char c : message.toCharArray()) {
              if (isCancelled()) {
                break;
              }
              String currentChar = String.valueOf(c);
              Platform.runLater(() -> itemChat.appendText(currentChar));
              Thread.sleep(20); // Sleep for 20 ms to simulate typing delay
            }
            return null;
          }
        };

    currentThread = new Thread(task);
    currentThread.setDaemon(true);
    task.setOnCancelled(e -> Platform.runLater(() -> itemChat.clear()));
    currentThread.start();
  }
}
