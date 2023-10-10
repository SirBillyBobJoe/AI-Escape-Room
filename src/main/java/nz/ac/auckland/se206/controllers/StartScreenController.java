package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;

/** Class responsible for controlling the Start Screen of the game. */
public class StartScreenController {

  // 0 is easy 1 is medium 2 is hard
  private int selectedLevel = 0;
  private int selectedTime = 2;

  private Boolean onLevel = true;
  private Boolean onTime = false;
  @FXML private AnchorPane anchorPane;
  @FXML private Label screenStage;

  @FXML private Label lblExit;
  @FXML private ImageView muteSound;

  @FXML private Label lblEasy;
  @FXML private Label lblMedium;
  @FXML private Label lblHard;

  @FXML private Label lbl2Min;
  @FXML private Label lbl4Min;
  @FXML private Label lbl6Min;

  @FXML private Label lblHow;
  @FXML private TextArea txaHow;

  @FXML private Label timeSummary;
  @FXML private Label levelSummary;
  @FXML private Label timeSummaryVal;
  @FXML private Label levelSummaryVal;

  @FXML private Label lblStart;

  @FXML private ImageView imgLoading;
  @FXML private Rectangle loadingBackground;

  private final DropShadow dropShadow = new DropShadow();
  private final DropShadow startDropShadow = new DropShadow();

  private final Image soundOffImage = new Image("/images/overlay/sound-off.png");
  private final Image soundOnImage = new Image("/images/overlay/sound-on.png");

  /** Initializes the ScreenStartController, setting focus on anchorPane. */
  public void initialize() {
    anchorPane.requestFocus();
    System.out.println("AnchorPane focused: " + anchorPane.isFocused());

    // Button drop shadow
    dropShadow.setColor(Color.web("#007aec"));
    dropShadow.setRadius(5.0);

    // Start button drop shadow
    startDropShadow.setColor(Color.web("#c31212"));
    startDropShadow.setRadius(10.0);

    setMuteSoundImage(GameState.isGameMuted);

    // Disable text selection in the how to play text area
    txaHow.setTextFormatter(
        new TextFormatter<String>(
            change -> {
              change.setAnchor(change.getCaretPosition());
              return change;
            }));
    txaHow.setMouseTransparent(true);
  }

  /**
   * Handles mouse click events for selecting levels.
   *
   * @param event MouseEvent object.
   */
  @FXML
  private void levelClicked(MouseEvent event) {
    chooseLevel();
  }

  /**
   * Handles mouse click events for selecting time.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void timeClicked(MouseEvent event) {
    chooseTime();
  }

  /**
   * Handles mouse click events for the "Back" button.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void lblExitClicked(MouseEvent event) {
    goBack();
  }

  /**
   * Updates the exit button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblExitEntered(MouseEvent event) {
    setEntered(lblExit);
  }

  /**
   * Updates the exit button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblExitExited(MouseEvent event) {
    setExited(lblExit);
  }

  /**
   * Updates the "How to Play" button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblHowEntered(MouseEvent event) {
    setEntered(lblHow);
  }

  /**
   * Updates the "How to Play" button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblHowExited(MouseEvent event) {
    setExited(lblHow);
  }

  /**
   * Updates the easy button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblEasyEntered(MouseEvent event) {
    setEntered(lblEasy);
    selectedLevel = 0;
  }

  /**
   * Updates the medium button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblEasyExited(MouseEvent event) {
    setExited(lblEasy);
  }

  /**
   * Updates the medium button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblMediumEntered(MouseEvent event) {
    setEntered(lblMedium);
    selectedLevel = 1;
  }

  /**
   * Updates the hard button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblMediumExited(MouseEvent event) {
    setExited(lblMedium);
  }

  /**
   * Updates the hard button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblHardEntered(MouseEvent event) {
    setEntered(lblHard);
    selectedLevel = 2;
  }

  /**
   * Updates the hard button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblHardExited(MouseEvent event) {
    setExited(lblHard);
  }

  /**
   * Updates the 2 minutes button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lbl2MinEntered(MouseEvent event) {
    setEntered(lbl2Min);
    selectedTime = 2;
  }

  /**
   * Updates the 2 minutes button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lbl2MinExited(MouseEvent event) {
    setExited(lbl2Min);
  }

  /**
   * Updates the 4 minutes button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lbl4MinEntered(MouseEvent event) {
    setEntered(lbl4Min);
    selectedTime = 4;
  }

  /**
   * Updates the 4 minutes button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lbl4MinExited(MouseEvent event) {
    setExited(lbl4Min);
  }

  /**
   * Updates the 6 minutes button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lbl6MinEntered(MouseEvent event) {
    setEntered(lbl6Min);
    selectedTime = 6;
  }

  /**
   * Updates the 6 minutes button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lbl6MinExited(MouseEvent event) {
    setExited(lbl6Min);
  }

  /**
   * Updates the start button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the button.
   */
  @FXML
  private void lblStartEntered(MouseEvent event) {
    lblStart.setEffect(startDropShadow);
    lblStart.setTextFill(Color.web("#c31212"));
    lblStart.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: #c31212; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /**
   * Updates the start button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the button.
   */
  @FXML
  private void lblStartExited(MouseEvent event) {
    lblStart.setEffect(null);
    lblStart.setTextFill(Color.WHITE);
    lblStart.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: #bfbfbf; -fx-background-radius: 5px;"
            + " -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 7px;");
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

  /**
   * Initiates the game when the "Start" button is clicked.
   *
   * @param event The mouse event object.
   * @throws IOException if an I/O error occurs.
   */
  @FXML
  private void lblStartClicked(MouseEvent event) throws IOException {
    new MouseClick().play();
    GameState.timer.setTimeSecondsProperty(selectedTime * 60);

    // Loading screen
    imgLoading.setVisible(true);
    loadingBackground.setVisible(true);

    // Start the timer and set the UI in a new thread
    new Thread(
            () -> {
              SceneManager.loadUserInterfaceOverlay();

              // Use Platform.runLater to update the UI on the JavaFX Application Thread
              Platform.runLater(
                  () -> {
                    GameState.timer.start();
                    imgLoading.setVisible(false);
                    loadingBackground.setVisible(false);
                  });
            })
        .start();
  }

  /** Helper method to navigate back in the options. */
  private void goBack() {
    new MouseClick().play();
    if (onTime) {
      // when we go back
      lblEasy.setVisible(true);
      lblMedium.setVisible(true);
      lblHard.setVisible(true);
      lbl2Min.setVisible(false);
      lbl4Min.setVisible(false);
      lbl6Min.setVisible(false);
      onLevel = true;
      onTime = false;
      screenStage.setText("Difficulty Select");
      lblExit.setText("Exit");
    } else if (onLevel) {
      System.exit(0);
    } else {
      // sets visibility
      lbl2Min.setVisible(true);
      lbl4Min.setVisible(true);
      lbl6Min.setVisible(true);
      timeSummary.setVisible(false);
      levelSummary.setVisible(false);
      timeSummaryVal.setVisible(false);
      levelSummaryVal.setVisible(false);
      lblStart.setVisible(false);
      onTime = true;
      // selectime
      screenStage.setText("Time Select");
    }
  }

  /** Helper method to set the selected level and update the UI accordingly. */
  private void chooseLevel() {
    new MouseClick().play();
    // makes levels not visible but time visible

    onLevel = false;
    onTime = true;
    lblEasy.setVisible(false);
    lblMedium.setVisible(false);
    lblHard.setVisible(false);
    lbl2Min.setVisible(true);
    lbl4Min.setVisible(true);
    lbl6Min.setVisible(true);
    // sets back
    lblExit.setText("Back");
    screenStage.setText("Time Select");
    // level checks
    switch (selectedLevel) {
      case 0:
        // if its easy
        GameState.hints.set(GameState.infinity);
        GameState.difficulty = "easy";
        break;
      case 1:
        // if medium
        GameState.hints.set("5");
        GameState.difficulty = "medium";
        break;
      case 2:
        // if hard
        GameState.hints.set("0");
        GameState.difficulty = "hard";
        break;
    }
  }

  /** Helper method to set the selected time and update the UI accordingly. */
  private void chooseTime() {
    new MouseClick().play();
    // makes levels not visible but time visible

    onTime = false;
    lbl2Min.setVisible(false);
    lbl4Min.setVisible(false);
    lbl6Min.setVisible(false);
    lblStart.setVisible(true);
    timeSummary.setVisible(true);
    levelSummary.setVisible(true);
    timeSummaryVal.setVisible(true);
    levelSummaryVal.setVisible(true);
    // asks if ur ready

    screenStage.setText("Ready?");
    String difficultyText =
        GameState.difficulty.substring(0, 1).toUpperCase() + GameState.difficulty.substring(1);
    levelSummaryVal.setText(difficultyText);
    // set text
    timeSummaryVal.setText(selectedTime + " minutes");
    GameState.time = selectedTime;
    System.out.println("Level: " + selectedLevel);
    System.out.println("Time: " + selectedTime);
  }

  @FXML
  private void howClicked(MouseEvent event) {
    // plays clicking sounds
    new MouseClick().play();
    if (txaHow.isVisible()) {
      txaHow.setVisible(false);
      lblHow.setText("How to Play");
    } else {
      // closes the how to play
      txaHow.setVisible(true);
      lblHow.setText("Close");
    }
  }

  /** Mutes the sound when the mute button is clicked. */
  @FXML
  public void onMuteSoundClicked(MouseEvent event) {
    // sets the mute function
    GameState.isGameMuted = !GameState.isGameMuted;
    new MouseClick().play();
    setMuteSoundImage(GameState.isGameMuted);
    // stops all sounds
    GameState.cancelAllSpeech();
    GameState.glitchSound.stop();
    GameState.type.stop();
  }

  /**
   * Updates the muteSound button when the mouse hovers over it.
   *
   * @param event MouseEvent for hovering over the muteSound button.
   */
  @FXML
  private void muteSoundEntered(MouseEvent event) {
    muteSound.setEffect(dropShadow);
    muteSound.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: white; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /**
   * Updates the muteSound button when the mouse leaves its area.
   *
   * @param event MouseEvent for leaving the muteSound button.
   */
  @FXML
  private void muteSoundExited(MouseEvent event) {
    muteSound.setEffect(null);
    muteSound.setStyle(
        "-fx-border-radius: 5px; -fx-border-color: #bfbfbf; -fx-background-radius: 5px;"
            + " -fx-background-color: black; -fx-padding: 7px;");
  }

  /** Sets the mute sound image. */
  public void setMuteSoundImage(boolean muted) {
    if (muted) {
      muteSound.setImage(soundOffImage);
    } else {
      muteSound.setImage(soundOnImage);
    }
  }
}
