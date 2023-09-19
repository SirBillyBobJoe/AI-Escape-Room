package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.GameMaster;
import nz.ac.auckland.se206.items.Inventory;

/**
 * Controller for the End Screen of the game. Handles the state labels that indicate whether the
 * player has escaped or not.
 */
public class EndScreenController {
  @FXML private Label lblState;
  @FXML private Label lblExit;
  @FXML private Label lblRestart;

  private final DropShadow dropShadow = new DropShadow();

  /**
   * Initializes the end screen. Sets the state label text based on the GameState's escaped status.
   */
  public void initialize() {
    if (GameState.escaped) {
      lblState.setText(GameState.escapeMessage);
    }

    // Button drop shadow
    dropShadow.setColor(Color.web("#007aec"));
    dropShadow.setRadius(5.0);
  }

  /**
   * Exits the game when the exit button is clicked.
   *
   * @param event ActionEvent for clicking the button.
   */
  @FXML
  private void lblExitClicked() {
    new MouseClick().play();
    Platform.exit();
    System.exit(0);
  }

  /**
   * Restarts the game when the restart button is clicked.
   *
   * @param event ActionEvent for clicking the button.
   */
  @FXML
  private void lblRestartClicked() throws IOException {
    // sets the escape to false
    GameState.escaped = false;
    new MouseClick().play();
    // stops the timers
    GameState.timer.stop();
    // creates a new inventory
    GameState.inventory = new Inventory();

    GameState.gameMaster = new GameMaster();
    GameState.chat.restart();
    // goes back to startscreen
    SceneManager.setReinitialise(AppUi.UIOVERLAY);
    SceneManager.setReinitialise(AppUi.STARTSCREEN);
    App.setUserInterface(AppUi.STARTSCREEN);
  }

  /**
   * Updates the 6 minutes button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblExitEntered(MouseEvent event) {
    setEntered(lblExit);
  }

  /**
   * Updates the 6 minutes button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblExitExited(MouseEvent event) {
    setExited(lblExit);
  }

  /**
   * Updates the 6 minutes button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblRestartEntered(MouseEvent event) {
    setEntered(lblRestart);
  }

  /**
   * Updates the 6 minutes button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblRestartExited(MouseEvent event) {
    setExited(lblRestart);
  }

  /**
   * Helper method to update the UI when hovering over buttons.
   *
   * @param label The label to update.
   */
  private void setEntered(Label label) {
    label.setEffect(dropShadow);
    label.setTextFill(Color.WHITE);
    label.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: white; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /**
   * Helper method to update the UI when leaving buttons.
   *
   * @param label The label to update.
   */
  private void setExited(Label label) {
    label.setEffect(null);
    label.setTextFill(Color.web("#bfbfbf"));
    label.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: #bfbfbf; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }
}
