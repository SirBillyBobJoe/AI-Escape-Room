package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class Room1Controller {
  @FXML private Label countdownLabel;
  @FXML private ImageView restart;

  // countdown timer linked to gamestate timer
  public void initialize() {
    countdownLabel.textProperty().bind(GameState.timer.timeSecondsProperty().asString());
  }

  @FXML
  private void onRestart(MouseEvent event) throws IOException {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    App.setUserInterface(AppUi.SCREEN_START);
    stage.setWidth(630);
    stage.setHeight(630);
    GameState.timer.stop();
  }

  @FXML
  private void leaveRestart(MouseEvent event) {
    restart.setImage(new Image("/images/room1/restartBlue.png"));
  }

  @FXML
  private void overRestart(MouseEvent event) {
    restart.setImage(new Image("/images/room1/restartGreen.png"));
  }
}
