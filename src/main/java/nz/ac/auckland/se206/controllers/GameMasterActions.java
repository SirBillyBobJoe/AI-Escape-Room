package nz.ac.auckland.se206.controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nz.ac.auckland.se206.GameState;

/**
 * Manages the actions of the Game Master within the game. Handles displaying text and images for
 * Game Master actions.
 */
public class GameMasterActions {
  private Thread thread;
  private ImageView image;
  private TextArea textArea;
  private boolean active;

  /** Default constructor. */
  public GameMasterActions() {}

  /**
   * Constructor that initializes the GameMasterActions with an image and text area.
   *
   * @param image The ImageView object where the Game Master's image will be displayed.
   * @param textArea The TextArea object where the Game Master's messages will be displayed.
   */
  public GameMasterActions(ImageView image, TextArea textArea) {
    this.image = image;
    this.image.setOpacity(0);

    this.textArea = textArea;
    textArea.setText("");
    InnerShadow innerShadow = new InnerShadow();
    innerShadow.setColor(Color.WHITE);
    innerShadow.setRadius(10.0);
    textArea.setEffect(innerShadow);
  }

  /**
   * Sets the active status of the GameMasterActions.
   *
   * @param active A boolean indicating whether the GameMasterActions is currently active.
   */
  public void isActive(boolean active) {
    this.active = active;
  }

  /**
   * Checks whether the GameMasterActions is currently active.
   *
   * @return A boolean value indicating the active status.
   */
  public boolean isActive() {
    return this.active;
  }

  /** Shows the GameMaster with a fade-in animation. */
  public void activate() {
    if (isActive()) {
      return;
    }
    setActive();

    // Show the GameMaster image with a 1s fade-in animation.
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), image);
    fadeTransition.setFromValue(0.0);
    fadeTransition.setToValue(1.0);
    fadeTransition.play();

    // Show the GameMaster glow
    InnerShadow innerShadow = new InnerShadow();
    innerShadow.setColor(Color.RED);
    innerShadow.setRadius(10.0);
    textArea.setEffect(innerShadow);
  }

  /** Shows the GameMaster with a message. */
  public void activate(String message) {
    activate();
    say(message);
  }

  public TextArea getTextArea() {
    return textArea;
  }

  /** Displays the GameMaster text with a typing animation. */
  public void say(String message) {
    // Don't say anything if the GameMaster is not active
    if (!isActive()) {
      return;
    }

    // Clear the text area
    textArea.clear();

    // Interrupt previous thread if it's still running
    if (thread != null && thread.isAlive()) {
      thread.interrupt();
    }
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            GameState.type.play();
            for (char c : message.toCharArray()) {
              if (isCancelled()) {
                break;
              }
              String currentChar = String.valueOf(c);
              Platform.runLater(() -> textArea.appendText(currentChar));
              Thread.sleep(
                  Math.max(
                      3,
                      Math.min(
                          700 / message.length(),
                          20))); // Dynamic sleep based on length for typing effect
            }
            GameState.type.stop();
            return null;
          }
        };

    thread = new Thread(task);
    thread.setDaemon(true);
    task.setOnCancelled(e -> Platform.runLater(() -> textArea.clear()));
    thread.start();
  }

  /** Clears the text from the text area. */
  public void clear() {
    textArea.clear();
  }

  /** Sets the active status of GameMasterActions to true. */
  private void setActive() {
    this.active = true;
  }
}
