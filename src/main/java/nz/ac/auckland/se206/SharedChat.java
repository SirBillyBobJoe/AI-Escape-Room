package nz.ac.auckland.se206;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import nz.ac.auckland.se206.controllers.GameMasterActions;

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

  private GameMasterActions gameMasterActions;
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
   */
  public void onSend(TextArea textField) {
    Boolean isHint = false;
    // Get the player's message
    String msg1 = "";
    String msg = textField.getText();
    String answer = "";

    if (msg.trim().isEmpty()) {
      return;
    }
    if (GameState.containsYear(msg)) {
      answer =
          " Tell the player that they need to provide the answer into the computer thats to the"
              + " left IT IS VERY IMPORTANT YOU SAY THIS. ";
    }
    // If the player gives the correct answer to the game master, instead of the computer
    if (msg.contains(GameState.passcodeAnswer)) {
      msg1 =
          "The player is correct. Tell the player to enter their correct answer in the computer"
              + " instead of telling you. ";
      textField.clear();
    } else {
      msg = msg.toLowerCase();
      textField.clear();
      // if it contains both words
      if (GameState.containsHint(msg)) {
        isHint = true;
        if (GameState.hints.get().equals(GameState.infinity)) {
          // tell them they have infinite hints
          msg1 = "Do not mention a hint number or say \"Hint: \". ";

        } else if (Integer.parseInt(GameState.hints.get()) == 0) {
          // with no more hints tell them
          msg1 =
              "Tell the player they have no more hints left. YOU ARE TO NOT GIVE THEM ANY MORE"
                  + " ANSWERS TO HINTS";

        } else {
          // remove 1 hint and ask gpt
          GameState.hints.set(Integer.toString(Integer.parseInt(GameState.hints.get()) - 1));
          msg1 = "Do not mention a hint number or say \"Hint: \". ";
        }
      }
    }

    // Get the right hint based on the current step
    String stepBasedHintPrompt = "";
    if (GameState.riddleRoomActive.getValue()) {
      if (!GameState.riddle2019Solved) {
        if (!GameState.isInComputer) {
          stepBasedHintPrompt =
              "Tell the player to go to the right room and to solve the riddle thats in the"
                  + " computer";

        } else {
          // logic for the riddles
          stepBasedHintPrompt =
              "The player is solving the riddle \""
                  + GameState.currentRiddle
                  + "\". With the answer \""
                  + GameState.passcodeAnswer
                  + "\". Please give the player a hint to help them solve the riddle."
                  + " BUT NEVER UNDER ANY CIRCUMSTANCE SAY: \""
                  + GameState.passcodeAnswer
                  + "\".";
        }

      } else if (!GameState.riddlePadlockSolved) {
        if (!GameState.isInComputer) {
          stepBasedHintPrompt =
              "Tell the player to go to the right room and to solve the riddle thats in the"
                  + " computer";
        } else {
          // logic for the padlock
          stepBasedHintPrompt =
              "The player is solving the riddle \""
                  + GameState.currentRiddle
                  + "\". With the answer \""
                  + GameState.padlockAnswer
                  + "\". Please give the player a hint to help them solve the riddle."
                  + " BUT NEVER UNDER ANY CIRCUMSTANCE SAY: \""
                  + GameState.padlockAnswer
                  + "\".";
        }
      }

    } else if (!GameState.pipePuzzleSolved) {
      // logic for the pipe
      stepBasedHintPrompt =
          "Tell the player a hint about solving a pipe puzzle. They need tp fix the pipes that are"
              + " leaking in the left room. Please Specify The Room";
    } else if (!GameState.isHammerFound) {
      // logic for the wall
      stepBasedHintPrompt =
          "Tell the player a hint about clicking on the hammer to pick it up in the left room."
              + " Please Specify The Room";
    } else if (!GameState.wallRemoved) {
      // logic for the wall
      stepBasedHintPrompt =
          "Tell the player a hint about breaking down a brick wall or using a hammer. There is a"
              + " brick wall they need to break in the left room. Please Specify The Room";
    } else if (!GameState.lighter) {
      // logic for pieces
      stepBasedHintPrompt =
          "Tell the player a"
              + " hint about a looking for items in a wall in the center room. They are next to the"
              + " escape door. Please Specify The Room";
    } else if (!GameState.isLighterFound) {
      // logic for pieces
      stepBasedHintPrompt =
          "Tell the player a hint about a looking clicking on the lighter in the center room to add"
              + " it to the inventory. Please Specify The Room";
    } else if (!GameState.candlePuzzleSolved) {
      // logic for candkle game
      stepBasedHintPrompt =
          "Tell the player a hint about lighting the candles in the left room. The painting of"
              + " candles in the center room can help the player.  Please Specify The Rooms";
    } else if (!GameState.chestPuzzleSolved) {
      // logic for chest puzzle
      stepBasedHintPrompt =
          "Tell the player a"
              + " hint about a chest. There is a chest in the center room with a number lock. The"
              + " player should remember their previous riddle answer: "
              + GameState.passcodeAnswer
              + " Please Specify The Room";
    } else if (!GameState.wire) {
      // logic for the wire
      stepBasedHintPrompt =
          "Tell the player a hint about wires. The player might have missed a red wire in behind"
              + " the wall in the center room. Please Specify The Room";

    } else if (!GameState.isRedWire) {
      // logic for the wire
      stepBasedHintPrompt =
          "Tell the player a hint clicking on the red Wire in the center room to pick it up. Please"
              + " Specify The Room";
    } else if (!GameState.isBlueWire) {
      // logic for the wire
      stepBasedHintPrompt =
          "Tell the player a hint clicking on the blue Wire in the center room to pick it up."
              + " Please Specify The Room";
    } else if (!GameState.isGreenWire) {
      // logic for the wire
      stepBasedHintPrompt =
          "Tell the player a hint clicking on the green Wire in the left room to pick it up. Please"
              + " Specify The Room";
    } else if (!GameState.wirePuzzleSolved) {
      // logic for the wire
      stepBasedHintPrompt =
          "Tell the player a hint about wires. There is a electrical box with wires that are"
              + " disconnected in the center room. Please Specify The Room";
    } else if (!GameState.isKeyFound) {
      // logic for key
      stepBasedHintPrompt =
          "Tell the player a hint about clicking on the key to pick it up in the center room."
              + " Please Specify The Room";
    } else if (!GameState.padlockPuzzleSolved) {
      // logic for the padlock
      stepBasedHintPrompt =
          "Tell the player a"
              + " hint about a lock. There is a combination word padlock in the center room. Use a"
              + " Key To Unlock It and the player should remember the previous riddle answer: "
              + GameState.padlockAnswer
              + " to open the lock. Please Specify The Room";
    } else {
      // logic for the escape
      stepBasedHintPrompt =
          "Tell the player a "
              + GameState.difficulty
              + " about using their wit to escape through the center door in the center room."
              + " Please Specify The Room";
    }

    String finalMessage;
    // tell them no more hitns when hitns are out
    if (GameState.riddle2019Solved && !GameState.riddlePadlockSolved && GameState.isInComputer) {
      finalMessage =
          "The riddle answer is: "
              + GameState.padlockAnswer
              + " if it seems like they are answering the riddle  tell them to provide the answer"
              + " in the computer on the left otherwise interact with them normally. UNDER NO"
              + " CIRCUMSTANCE ARE YOU ALLOWED TO REVEAL THE RIDDLE ANSWER TO THE PLAYER. "
              + msg1
              + " The player says: "
              + msg
              + ". ";
    } else if (GameState.hints.get().equals("0") || !isHint) {
      finalMessage = answer + msg1 + " The player says: " + msg + ". ";
      System.out.println("no hint");
    } else {
      // give step based hint
      finalMessage = msg1 + " The player says: " + msg + ". " + stepBasedHintPrompt;
      System.out.println("Give hint");
    }
    // Get the Game Master's response
    final String chatContext = "main";
    GameState.gameMaster.addMessage(chatContext, "user", finalMessage);
    System.out.println(finalMessage);
    GameState.gameMaster.runContext(chatContext);

    // create a task for threading
    Task<Void> waitForResponseTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            // chat for game master
            GameState.gameMaster.waitForContext(chatContext);
            return null;
          }
        };
    // logic for success
    waitForResponseTask.setOnSucceeded(
        e -> {
          gameMasterActions.activate(
              // get the last response
              GameState.gameMaster.getLastResponse(chatContext).getContent() + "\n\n");
          GameState.loading.set(false);
        });

    new Thread(waitForResponseTask).start();
  }
}
