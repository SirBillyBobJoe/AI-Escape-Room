package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Rooms;

public class PuzzleRoomController {

  @FXML
  private void changeBack() {
    GameState.currentRoom.set(Rooms.MAINROOM);
  }
}
