package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ScreenStartController {
  @FXML private ImageView easy;
  @FXML private ImageView medium;
  @FXML private ImageView hard;
  // 0 is easy 1 is medium 2 is hard
  private int selected = 0;

  @FXML
  private void overEasy(MouseEvent event) {
    removeSelected();
    selected = 0;
    onSelect();
  }

  @FXML
  private void overMedium(MouseEvent event) {
    removeSelected();
    selected = 1;
    onSelect();
  }

  @FXML
  private void overHard(MouseEvent event) {
    removeSelected();
    selected = 2;
    onSelect();
  }

  private void removeSelected() {
    switch (selected) {
      case 0:
        easy.setImage(new Image("/images/easyBlue.png"));
        break;
      case 1:
        medium.setImage(new Image("/images/mediumBlue.png"));
        break;
      case 2:
        hard.setImage(new Image("/images/hardBlue.png"));
        break;
    }
  }

  private void onSelect() {
    switch (selected) {
      case 0:
        easy.setImage(new Image("/images/easyGreen.png"));
        break;
      case 1:
        medium.setImage(new Image("/images/mediumGreen.png"));
        break;
      case 2:
        hard.setImage(new Image("/images/hardGreen.png"));
        break;
    }
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {

    if (event.getCode() == KeyCode.UP) {
      removeSelected();
      if (selected-- < 0) {
        selected = 2;
      }
      onSelect();
    } else if (event.getCode() == KeyCode.DOWN) {
      removeSelected();
      if (selected++ > 2) {
        selected = 0;
      }
      onSelect();
    }
  }
}
