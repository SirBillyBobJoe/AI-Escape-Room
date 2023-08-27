package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Items.Inventory;
import nz.ac.auckland.se206.Items.Keys;
import nz.ac.auckland.se206.Items.Lighter;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class Room1Controller {
  @FXML private Label countdownLabel;
  @FXML private ImageView restart;
  @FXML private ImageView item0, item1, item2, item3, item4, item5;
  @FXML private ImageView key1, key2, key3;
  @FXML private ImageView lighter1, lighter2, lighter3;

  // countdown timer linked to gamestate timer
  public void initialize() {
    item0.setUserData(0); // Index 0
    item1.setUserData(1); // Index 1
    item2.setUserData(2); // Index 2
    item3.setUserData(3); // Index 3
    item4.setUserData(4); // Index 4
    item5.setUserData(5); // Index 5
    countdownLabel.textProperty().bind(GameState.timer.timeSecondsProperty().asString());
    ImageView[] images = {item0, item1, item2, item3, item4, item5};

    GameState.inventory
        .inventoryProperty()
        .addListener(
            (obs, oldInventory, newInventory) -> {
              for (int i = 0; i < images.length; i++) {
                if (i < newInventory.size()) {
                  images[i].setImage(newInventory.get(i).getImage());
                } else {
                  images[i].setImage(null);
                }
              }
            });
  }

  @FXML
  private void onRestart(MouseEvent event) throws IOException {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    App.setUserInterface(AppUi.SCREEN_START);
    stage.setWidth(630);
    stage.setHeight(630);
    GameState.timer.stop();

    GameState.inventory = new Inventory();
    SceneManager.setReinitialise(AppUi.ROOM1);
  }

  @FXML
  private void leaveRestart(MouseEvent event) {
    restart.setImage(new Image("/images/room1/restartBlue.png"));
  }

  @FXML
  private void overRestart(MouseEvent event) {
    restart.setImage(new Image("/images/room1/restartGreen.png"));
  }

  @FXML
  private void objectClicked(MouseEvent event) {

    ImageView imageView = (ImageView) event.getSource();
    imageView.setVisible(false);
    if (imageView == key1) {
      GameState.inventory.addObject(new Keys(1));
      return;
    } else if (imageView == key2) {
      GameState.inventory.addObject(new Keys(2));
      return;
    } else if (imageView == key3) {
      GameState.inventory.addObject(new Keys(3));
      return;
    } else if (imageView == lighter1) {
      GameState.inventory.addObject(new Lighter());
      return;
    } else if (imageView == lighter2) {
      GameState.inventory.addObject(new Lighter());
      return;
    } else if (imageView == lighter3) {
      GameState.inventory.addObject(new Lighter());
      return;
    }
  }

  @FXML
  private void onDragDetected(MouseEvent event) {
    ImageView sourceImageView = (ImageView) event.getSource();

    // Start the drag-and-drop operation
    Dragboard db = sourceImageView.startDragAndDrop(javafx.scene.input.TransferMode.ANY);
    // Set the visual representation of the drag-and-drop with the image from the ImageView
    db.setDragView(sourceImageView.getImage());
    // Use stored user data to get the index of this ImageView
    int index = (int) sourceImageView.getUserData();

    // Store the ID of the ImageView being dragged in the ClipboardContent
    ClipboardContent content = new ClipboardContent();
    content.putString(String.valueOf(index));

    // Add the ClipboardContent to the Dragboard
    db.setContent(content);

    // Consume the MouseEvent
    event.consume();
  }

  @FXML
  private void onDragOver(DragEvent event) {
    // Allow this ImageView to accept the drag-and-drop if it is not the source and contains a
    // string
    if (event.getGestureSource() != event.getSource() && event.getDragboard().hasString()) {
      event.acceptTransferModes(TransferMode.MOVE);

      // Make it really blue when hovered over
      ImageView targetImageView = (ImageView) event.getSource();
      ColorAdjust colorAdjust = new ColorAdjust();
      colorAdjust.setHue(1); // Max hue
      colorAdjust.setSaturation(1); // Max saturation
      targetImageView.setEffect(colorAdjust);
    }

    // Consume the DragEvent
    event.consume();
  }

  @FXML
  private void onDragExited(DragEvent event) {
    // Remove the blue tint after dropping
    ImageView targetImageView = (ImageView) event.getSource();
    targetImageView.setEffect(null);
  }

  @FXML
  private void onDragDropped(DragEvent event) {
    // Remove the blue tint after dropping
    ImageView targetImageView = (ImageView) event.getSource();
    targetImageView.setEffect(null);

    // Get Dragboard
    Dragboard db = event.getDragboard();

    // check if drop was successful
    boolean success = false;

    // Check if Dragboard has a String(we stored earlier)
    if (db.hasString()) {
      int originalIndex = Integer.parseInt(db.getString());

      ImageView tartgetImageView = (ImageView) event.getSource();

      int targetIndex = (int) tartgetImageView.getUserData();

      GameState.inventory.swapObject(originalIndex, targetIndex);

      success = true;
    }

    event.setDropCompleted(success);
    event.consume();
  }
}
