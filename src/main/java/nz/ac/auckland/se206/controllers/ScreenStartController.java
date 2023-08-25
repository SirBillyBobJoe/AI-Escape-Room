package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.GameState;

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

  public void initialize() {
    anchorPane.requestFocus();
    System.out.println("AnchorPane focused: " + anchorPane.isFocused());
  }

  @FXML
  private void levelClick(MouseEvent event) {
    choseLevel();
  }

  @FXML
  private void timeClick(MouseEvent event) {
    choseTime();
  }

  @FXML
  private void backClick(MouseEvent event) {
    goBack();
  }

  @FXML
  private void overEasy(MouseEvent event) {
    removeSelectedLevel();
    selectedLevel = 0;
    onSelectLevel();
  }

  @FXML
  private void overMedium(MouseEvent event) {
    removeSelectedLevel();
    selectedLevel = 1;
    onSelectLevel();
  }

  @FXML
  private void overHard(MouseEvent event) {
    removeSelectedLevel();
    selectedLevel = 2;
    onSelectLevel();
  }

  @FXML
  private void over2(MouseEvent event) {
    removeSelectedTime();
    selectedTime = 2;
    onSelectTime();
  }

  @FXML
  private void over4(MouseEvent event) {
    removeSelectedTime();
    selectedTime = 4;
    onSelectTime();
  }

  @FXML
  private void over6(MouseEvent event) {
    removeSelectedTime();
    selectedTime = 6;
    onSelectTime();
  }

  @FXML
  private void overStart(MouseEvent event) {
    start.setImage(new Image("/images/StartScreen/startGreen.png"));
  }

  @FXML
  private void leaveStart(MouseEvent event) {
    start.setImage(new Image("/images/StartScreen/startBlue.png"));
  }

  @FXML
  private void overBack(MouseEvent event) {
    if (!onLevel) {
      goBack.setImage(new Image("/images/StartScreen/goBackGreen.png"));
    } else {
      goBack.setImage(new Image("/images/StartScreen/exitGreen.png"));
    }
  }

  @FXML
  private void leaveBack(MouseEvent event) {
    if (!onLevel) {
      goBack.setImage(new Image("/images/StartScreen/goBackBlue.png"));
    } else {
      goBack.setImage(new Image("/images/StartScreen/exitBlue.png"));
    }
  }

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
        GameState.hints = "Unlimited";
        GameState.difficulty = "easy";
        break;
      case 1:
        GameState.hints = "5";
        GameState.difficulty = "medium";
        break;
      case 2:
        GameState.hints = "0";
        GameState.difficulty = "hard";
        break;
    }
  }

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
