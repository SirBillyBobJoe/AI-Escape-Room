package nz.ac.auckland.se206.controllers;

import javafx.animation.FadeTransition;
import javafx.scene.control.TextArea;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameMasterActions {
  private ImageView image;
  private TextArea textArea;
  private boolean active;

  public GameMasterActions() {}

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

  public void isActive(boolean active) {
    this.active = active;
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

  /** Hides the GameMaster with a fade-out animation. */
  public void unactivate() {
    if (!isActive()) {
      return;
    }
    setInactive();

    // Hide the GameMaster image with a 1s fade-in animation.
    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), image);
    fadeTransition.setFromValue(1.0);
    fadeTransition.setToValue(0.0);
    fadeTransition.play();

    // Hide the GameMaster glow
    InnerShadow innerShadow = new InnerShadow();
    innerShadow.setColor(Color.WHITE);
    innerShadow.setRadius(10.0);
    textArea.setEffect(innerShadow);
  }

  public boolean isActive() {
    return this.active;
  }

  private void setActive() {
    this.active = true;
  }

  private void setInactive() {
    this.active = false;
  }
}
