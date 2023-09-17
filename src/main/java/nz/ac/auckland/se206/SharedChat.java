package nz.ac.auckland.se206;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import nz.ac.auckland.se206.controllers.GameMasterActions;

/** A singleton class that manages the shared chat functionality in the application. */
public class SharedChat {
  private static SharedChat instance;
  private GameMasterActions gameMasterActions;

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
    this.text = new SimpleStringProperty("");
  }

  public void setGameMasterActions(GameMasterActions gameMasterActions) {
    this.gameMasterActions = gameMasterActions;
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
    this.text.set("Welcome To My Dungeon Click On The Hints Button If You Are Stuck!!!\n\n");
  }

  /**
   * Handles the send operation for the chat. Checks for hints and clues, updates the game state,
   * and triggers asynchronous actions for waiting for a response.
   *
   * @param textField The TextField containing the user message.
   * @param room The room context for the message.
   */
  public void onSend(TextArea textField, String room) {
    // Get the player's message
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
            msg1 = "Do not mention a hint number.";
            break outerloop;
          }
        }
      }
    }

    // Get the right hint based on the current step
    String stepBasedHintPrompt = "";
    if (!GameState.pipePuzzleSolved) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about solving a pipe puzzle. There is a leak from the pipe.";
    } else if (!GameState.wallRemoved) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about breaking down a wall or using a hammer. There is a blank brick wall";
    } else if (!GameState.wallPiecesFound) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about a looking for items in a wall. There are suspicious looking spots on"
              + " the wall of the main room.";
    } else if (!GameState.candlePuzzleSolved) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about lighting candles. There is a painting of candles.";
    } else if (!GameState.chestPuzzleSolved) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about a chest. There is a chest with a number lock.";
    } else if (!GameState.wirePuzzleSolved) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about wires. There is a electrical box with wires that are disconnected.";
    } else if (!GameState.padlockPuzzleSolved) {
      stepBasedHintPrompt =
          "Tell the player a/an "
              + GameState.difficulty
              + " hint about a lock. There is a padlock on the main door and a key for it hidden"
              + " somewhere in the main room (The wall).";
    } else {
      stepBasedHintPrompt =
          "Tell the player a/an " + GameState.difficulty + " about using their wit to escape.";
    }

    // Get the Game Master's response
    GameState.gameMaster.addMessage(room, "user", msg1 + msg);
    System.out.println(msg1 + msg + ". " + stepBasedHintPrompt);
    GameState.gameMaster.runContext(room);

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
          gameMasterActions.activate(
              GameState.gameMaster.getLastResponse(room).getContent() + "\n\n");
        });

    new Thread(waitForResponseTask).start();
  }
}
