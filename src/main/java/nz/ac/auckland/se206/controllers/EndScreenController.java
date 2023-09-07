package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.GameMaster;

public class EndScreenController {
  @FXML Text text;

  public void initialize() {
    if (GameState.escaped) {
      text.setText(GameState.escapeMessage);
    }
  }

  @FXML
  private void onClose(ActionEvent event) {
    new MouseClick().play();
    Platform.exit();
    System.exit(0);
  }

  @FXML
  private void onRestart(ActionEvent event) throws IOException {
    GameState.escaped = false;
    new MouseClick().play();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
    double additionalHeight = stage.getHeight() - stage.getScene().getHeight();
    stage.setWidth(800 + additionalWidth);
    stage.setHeight(600 + additionalHeight);
    GameState.timer.stop();

    GameState.inventory = new Inventory();

    GameState.gameMaster = new GameMaster();
    GameState.chat.restart();
    SceneManager.setReinitialise(AppUi.UIOVERLAY);
    SceneManager.setReinitialise(AppUi.STARTSCREEN);
    App.setUserInterface(AppUi.STARTSCREEN);
  }
}
