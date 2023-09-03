package nz.ac.auckland.se206;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;

/** A singleton class that manages the shared chat functionality in the application. */
public class SharedChat {
  private static SharedChat instance;

  /**
   * Retrieves the singleton instance of SharedChat.
   *
   * @return The singleton instance.
   */
  public static SharedChat getInstance() {
    if (instance == null) {
      instance = new SharedChat();
    }
    return instance;
  }

  private final StringProperty text;

  /** Constructs a new SharedChat instance with an initial welcome message. */
  public SharedChat() {
    this.text =
        new SimpleStringProperty(
            "Grand Wizard: Welcome To My Dungeon Click On The Hints Button If You Are Stuck!!!"
                + "\n"
                + "\n");
  }

  /**
   * Retrieves the StringProperty for text messages.
   *
   * @return The StringProperty for text messages.
   */
  public StringProperty getTextProperty() {
    return text;
  }

  /**
   * Retrieves the text message content.
   *
   * @return The text message content as a string.
   */
  public final String getText() {
    return getTextProperty().get();
  }

  /**
   * Sets the text message content.
   *
   * @param text The new text message content.
   */
  public final void setText(String text) {
    getTextProperty().set(text);
  }

  /** Resets the instance fields to their initial values. (to be implemented) */
  public void restart() {
    this.text.set(
        "Grand Wizard: Welcome To My Dungeon Click On The Hints Button If You Are Stuck!!!\n\n");
  }

  /**
   * Handles the send operation for the chat. Checks for hints and clues, updates the game state,
   * and triggers asynchronous actions for waiting for a response.
   *
   * @param textField The TextField containing the user message.
   * @param room The room context for the message.
   */
  public void onSend(TextField textField, String room) {
    String msg1 = "";
    String msg = textField.getText();

    if (msg.trim().isEmpty()) {
      return;
    }
    msg = msg.toLowerCase();
    textField.clear();
    // determines if its a hint from keywords or if its a general conversation
    outerloop:
    for (String keyWords1 : GameState.clueFirst) {
      for (String keyWords2 : GameState.clueSecond) {
        if (msg.contains(keyWords1) && msg.contains(keyWords2) && (!keyWords1.equals(keyWords2))) {
          if (GameState.hints.get().equals("\u221E")) {
            break outerloop;
          } else if (Integer.parseInt(GameState.hints.get()) == 0) {
            msg =
                "Tell the player they have no more hints left. YOU ARE TO NOT GIVE THEM ANY MORE"
                    + " ANSWERS TO HINTS";
            break outerloop;
          } else {
            GameState.hints.set(Integer.toString(Integer.parseInt(GameState.hints.get()) - 1));
            msg1 = "I have " + GameState.hints.get() + " hints left ";
            break outerloop;
          }
        }
      }
    }

    GameState.gameMaster.addMessage(room, "user", msg1 + msg);
    System.out.println(msg1 + msg);
    GameState.gameMaster.runContext(room);

    String message = GameState.name + ": " + msg;
    this.setText(this.getText() + message + "\n\n");

    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.gameMaster.waitForContext(room);
            return null;
          }
        };

    waitForResponseTask.setOnSucceeded(
        e -> {
          this.setText(
              GameState.name
                  + ": "
                  + this.getText()
                  + GameState.gameMaster.getLastResponse(room).getContent()
                  + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }
}
