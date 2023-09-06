package nz.ac.auckland.se206.controllers;

import javafx.animation.FadeTransition;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class GameMasterActions {
  private ImageView image;
  private TextArea textArea;
  private boolean active;

  public GameMasterActions(ImageView image, TextArea textArea) {
    this.image = image;
    // Setting opacity to 0;
    this.image.setOpacity(0);
    this.textArea = textArea;
  }

  public void isActive(boolean active) {
    this.active = active;
  }

  /** Shows the GameMaster image with a fade-in animation. */
  public void show() {
    setActive();

    // Show the GameMaster image with a 1s fade-in animation.
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.0), image);
    fadeTransition.setFromValue(0.0);
    fadeTransition.setToValue(1.0);
    fadeTransition.play();

    // Show the GameMaster glow with a 1s fade-in animation.
  }

  public void hide() {
    setInactive();

    // Hide the GameMaster image with a 1s fade-in animation.
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.0), image);
    fadeTransition.setFromValue(1.0);
    fadeTransition.setToValue(0.0);
    fadeTransition.play();

    // Hide the GameMaster glow with a 1s fade-in animation.
  }

  private void setActive() {
    this.active = true;
  }

  private void setInactive() {
    this.active = false;
  }
}
