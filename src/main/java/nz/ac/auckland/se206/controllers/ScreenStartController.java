package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

/** Class responsible for controlling the Start Screen of the game. */
public class ScreenStartController {
  @FXML private ImageView easy;
  @FXML private ImageView medium;
  @FXML private ImageView medium1;
  @FXML private ImageView hard;
  @FXML private ImageView two;
  @FXML private ImageView four;
  @FXML private ImageView six;
  @FXML private ImageView screenStage;
  @FXML private ImageView timeSummary;
  @FXML private ImageView levelSummary;
  @FXML private ImageView timeSummaryVal;
  @FXML private ImageView levelSummaryVal;
  @FXML private ImageView start;
  @FXML private ImageView goBack;
  @FXML AnchorPane anchorPane;
  // 0 is easy 1 is medium 2 is hard
  private int selectedLevel = 0;
  private int selectedTime = 2;

  private Boolean onLevel = true;
  private Boolean onTime = false;

  /** Initializes the ScreenStartController, setting focus on anchorPane. */
  public void initialize() {
    anchorPane.requestFocus();
    System.out.println("AnchorPane focused: " + anchorPane.isFocused());
  }

  /**
   * Handles mouse click events for selecting levels.
   *
   * @param event MouseEvent object.
   */
  @FXML
  private void levelClick(MouseEvent event) {
    choseLevel();
  }

  /**
   * Handles mouse click events for selecting time.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void timeClick(MouseEvent event) {
    choseTime();
  }

  /**
   * Handles mouse click events for the "Back" button.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void backClick(MouseEvent event) {
    goBack();
  }

  /**
   * Changes the UI when hovering over the "Easy" option.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void overEasy(MouseEvent event) {
    removeSelectedLevel();
    selectedLevel = 0;
    onSelectLevel();
  }

  /**
   * Changes the UI when hovering over the "Medium" option.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void overMedium(MouseEvent event) {
    removeSelectedLevel();
    selectedLevel = 1;
    onSelectLevel();
  }

  /**
   * Changes the UI when hovering over the "Hard" option.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void overHard(MouseEvent event) {
    removeSelectedLevel();
    selectedLevel = 2;
    onSelectLevel();
  }

  /**
   * Changes the UI when hovering over the "2 Minutes" option.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void over2(MouseEvent event) {
    removeSelectedTime();
    selectedTime = 2;
    onSelectTime();
  }

  /**
   * Changes the UI when hovering over the "4 Minutes" option.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void over4(MouseEvent event) {
    removeSelectedTime();
    selectedTime = 4;
    onSelectTime();
  }

  /**
   * Changes the UI when hovering over the "6 Minutes" option.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void over6(MouseEvent event) {
    removeSelectedTime();
    selectedTime = 6;
    onSelectTime();
  }

  /**
   * Changes the UI when hovering over the "Start" button.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void overStart(MouseEvent event) {
    start.setImage(new Image("/images/StartScreen/startGreen.png"));
  }

  /**
   * Reverts the UI when no longer hovering over the "Start" button.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void leaveStart(MouseEvent event) {
    start.setImage(new Image("/images/StartScreen/startBlue.png"));
  }

  /**
   * Changes the UI when hovering over the "Back" button.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void overBack(MouseEvent event) {
    if (!onLevel) {
      goBack.setImage(new Image("/images/StartScreen/goBackGreen.png"));
    } else {
      goBack.setImage(new Image("/images/StartScreen/exitGreen.png"));
    }
  }

  /**
   * Reverts the UI when no longer hovering over the "Back" button.
   *
   * @param event The mouse event object.
   */
  @FXML
  private void leaveBack(MouseEvent event) {
    if (!onLevel) {
      goBack.setImage(new Image("/images/StartScreen/goBackBlue.png"));
    } else {
      goBack.setImage(new Image("/images/StartScreen/exitBlue.png"));
    }
  }

  /**
   * Initiates the game when the "Start" button is clicked.
   *
   * @param event The mouse event object.
   * @throws IOException if an I/O error occurs.
   */
  @FXML
  private void onStart(MouseEvent event) throws IOException {
    GameState.timer.setTimeSecondsProperty(selectedTime * 60);

    // Start the timer.
    GameState.timer.start();

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    App.setUserInterface(AppUi.ROOM1);
    stage.setWidth(720);
    stage.setHeight(570);
  }

  /** Helper method to remove the currently selected level highlight. */
  private void removeSelectedLevel() {
    switch (selectedLevel) {
      case 0:
        easy.setImage(new Image("/images/StartScreen/easyBlue.png"));
        break;
      case 1:
        medium.setImage(new Image("/images/StartScreen/mediumBlue.png"));
        break;
      case 2:
        hard.setImage(new Image("/images/StartScreen/hardBlue.png"));
        break;
    }
  }

  /** Helper method to remove the currently selected time highlight. */
  private void removeSelectedTime() {
    switch (selectedTime) {
      case 2:
        two.setImage(new Image("/images/StartScreen/2Blue.png"));
        break;
      case 4:
        four.setImage(new Image("/images/StartScreen/4Blue.png"));
        break;
      case 6:
        six.setImage(new Image("/images/StartScreen/6Blue.png"));
        break;
    }
  }

  /** Helper method to highlight the newly selected level. */
  private void onSelectLevel() {
    switch (selectedLevel) {
      case 0:
        easy.setImage(new Image("/images/StartScreen/easyGreen.png"));
        break;
      case 1:
        medium.setImage(new Image("/images/StartScreen/mediumGreen.png"));
        break;
      case 2:
        hard.setImage(new Image("/images/StartScreen/hardGreen.png"));
        break;
    }
  }

  /** Helper method to highlight the newly selected time. */
  private void onSelectTime() {
    switch (selectedTime) {
      case 2:
        two.setImage(new Image("/images/StartScreen/2Green.png"));
        break;
      case 4:
        four.setImage(new Image("/images/StartScreen/4Green.png"));
        break;
      case 6:
        six.setImage(new Image("/images/StartScreen/6Green.png"));
        break;
    }
  }

  /**
   * Handles key press events for navigation and selection within the start screen.
   *
   * @param event KeyEvent object.
   */
  @FXML
  private void onKeyPressed(KeyEvent event) {
    if (onLevel) {
      if (event.getCode() == KeyCode.UP) {
        removeSelectedLevel();

        if (selectedLevel-- < 1) {
          selectedLevel = 2;
        }
        onSelectLevel();
      } else if (event.getCode() == KeyCode.DOWN) {
        removeSelectedLevel();
        if (selectedLevel++ > 1) {
          selectedLevel = 0;
        }
        onSelectLevel();
      } else if (event.getCode() == KeyCode.ENTER) {
        choseLevel();
      } else if (event.getCode() == KeyCode.ESCAPE) {
        goBack();
      }
    } else if (onTime) {
      if (event.getCode() == KeyCode.LEFT) {
        removeSelectedTime();
        if (selectedTime - 2 < 2) {
          selectedTime = 6;
        } else {
          selectedTime -= 2;
        }
        onSelectTime();
      } else if (event.getCode() == KeyCode.RIGHT) {
        removeSelectedTime();
        if (selectedTime + 2 > 6) {
          selectedTime = 2;
        } else {
          selectedTime += 2;
        }
        onSelectTime();
      } else if (event.getCode() == KeyCode.ENTER) {
        choseTime();
      } else if (event.getCode() == KeyCode.ESCAPE) {
        goBack();
      }
    } else {
      if (event.getCode() == KeyCode.ESCAPE) {
        goBack();
      }
    }
  }

  /** Helper method to navigate back in the options. */
  private void goBack() {
    if (onTime) {
      easy.setVisible(true);
      medium.setVisible(true);
      hard.setVisible(true);
      two.setVisible(false);
      four.setVisible(false);
      six.setVisible(false);
      onLevel = true;
      onTime = false;
      screenStage.setImage(new Image("/images/StartScreen/chooseLevel.png"));
      goBack.setImage(new Image("/images/StartScreen/exitBlue.png"));
    } else if (onLevel) {
      System.exit(0);
    } else {
      two.setVisible(true);
      four.setVisible(true);
      six.setVisible(true);
      timeSummary.setVisible(false);
      levelSummary.setVisible(false);
      timeSummaryVal.setVisible(false);
      levelSummaryVal.setVisible(false);
      medium1.setVisible(false);
      start.setVisible(false);
      onTime = true;
      screenStage.setImage(new Image("/images/StartScreen/chooseTime.png"));
    }
  }

  /** Helper method to set the selected level and update the UI accordingly. */
  private void choseLevel() {
    // makes levels not visible but time visible

    onLevel = false;
    onTime = true;
    easy.setVisible(false);
    medium.setVisible(false);
    hard.setVisible(false);
    two.setVisible(true);
    four.setVisible(true);
    six.setVisible(true);

    goBack.setImage(new Image("/images/StartScreen/goBackBlue.png"));
    screenStage.setImage(new Image("/images/StartScreen/chooseTime.png"));
    switch (selectedLevel) {
      case 0:
        GameState.hints.set("\u221E");
        GameState.difficulty = "easy";
        break;
      case 1:
        GameState.hints.set("5");
        GameState.difficulty = "medium";
        break;
      case 2:
        GameState.hints.set("0");
        GameState.difficulty = "hard";
        break;
    }
  }

  /** Helper method to set the selected time and update the UI accordingly. */
  private void choseTime() {
    // makes levels not visible but time visible

    onTime = false;
    two.setVisible(false);
    four.setVisible(false);
    six.setVisible(false);
    start.setVisible(true);
    timeSummary.setVisible(true);
    levelSummary.setVisible(true);
    timeSummaryVal.setVisible(true);
    levelSummaryVal.setVisible(true);

    screenStage.setImage(new Image("/images/StartScreen/Summary.png"));
    if (GameState.difficulty != "medium") {
      levelSummaryVal.setImage(
          new Image("/images/StartScreen/" + GameState.difficulty + "Blue.png"));
    } else {
      levelSummaryVal.setVisible(false);
      medium1.setVisible(true);
    }
    timeSummaryVal.setImage(new Image("/images/StartScreen/" + selectedTime + "Blue.png"));
    GameState.time = selectedTime;
    System.out.println(selectedTime);
    System.out.println(selectedLevel);
  }
}
