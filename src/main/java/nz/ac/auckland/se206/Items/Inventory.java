package nz.ac.auckland.se206.Items;

import java.util.HashMap;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.ItemChat;

/** Represents the inventory of objects within the game. */
public class Inventory {
  private final SimpleListProperty<Object> inventoryProperty =
      new SimpleListProperty<Object>(FXCollections.observableArrayList());

  /** Creates an inventory with 6 empty slots. */
  public Inventory() {
    for (int i = 0; i < 6; i++) {
      inventoryProperty.add(new Object(null));
    }
  }

  /**
   * Returns the inventory property.
   *
   * @return The inventory as a ListProperty of Object.
   */
  public ListProperty<Object> inventoryProperty() {
    return inventoryProperty;
  }

  /**
   * Adds an object to the first empty slot in the inventory.
   *
   * @param object The object to add.
   */
  public void addObject(Object object) {
    // Find the first empty slot and replace it
    for (int i = 0; i < inventoryProperty.size(); i++) {
      if (inventoryProperty.get(i).getImage() == null) {
        inventoryProperty.set(i, object);
        object.setPosition(i);
        return;
      }
    }

    // If no empty slots, add the object at the end
    inventoryProperty.add(object);
    object.setPosition(inventoryProperty.size() - 1);
  }

  /**
   * Removes a specific object from the inventory.
   *
   * @param object The object to remove.
   */
  public void removeObject(Object object) {
    inventoryProperty.remove(object);
  }

  /**
   * Swaps the positions of two objects in the inventory.
   *
   * @param i The index of the first object.
   * @param j The index of the second object.
   */
  public void swapObject(int i, int j) {
    if (i >= inventoryProperty.size() || j >= inventoryProperty.size()) {
      return; // out of bounds, do nothing
    }
    Object temp = inventoryProperty.get(i);

    Object jObj = (j < inventoryProperty.size()) ? inventoryProperty.get(j) : null;

    inventoryProperty.set(i, jObj);
    if (jObj != null) {
      jObj.setPosition(i);
    }

    inventoryProperty.set(j, temp);
    if (temp != null) {
      temp.setPosition(j);
    }

    // Ensure observable list is updated
    inventoryProperty.set(FXCollections.observableArrayList(inventoryProperty));
  }

  /**
   * Gets the object at a specific position in the inventory.
   *
   * @param position The index of the object.
   * @return The object at the given position or null if the position is out of bounds.
   */
  public Object getObject(int position) {
    return inventoryProperty.size() > position ? inventoryProperty.get(position) : null;
  }

  /**
   * Returns the size of the inventory.
   *
   * @return The size of the inventory.
   */
  public int getSize() {
    return inventoryProperty.size();
  }

  /**
   * Handles the event when a drag is detected on an object in the inventory.
   *
   * @param event The MouseEvent triggered by the drag.
   */
  public void onDragDetected(MouseEvent event) {
    ImageView sourceImageView = (ImageView) event.getSource();

    // Configure SnapshotParameters
    SnapshotParameters snapshotParameters = new SnapshotParameters();
    snapshotParameters.setFill(Color.TRANSPARENT);

    // Create a snapshot of the ImageView
    WritableImage snapshot = sourceImageView.snapshot(snapshotParameters, null);

    // Start the drag-and-drop operation
    Dragboard db = sourceImageView.startDragAndDrop(TransferMode.ANY);

    // Use the snapshot as the drag view
    db.setDragView(snapshot);
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

  /**
   * Handles the event when an object is dragged over another object in the inventory.
   *
   * @param event The DragEvent triggered by the drag.
   */
  public void onDragOver(DragEvent event) {
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

  /**
   * Handles the event when a dragged object exits another object in the inventory.
   *
   * @param event The DragEvent triggered by the exit.
   */
  public void onDragExited(DragEvent event) {
    // Remove the blue tint after dropping
    ImageView targetImageView = (ImageView) event.getSource();
    targetImageView.setEffect(null);
  }

  /**
   * Handles the event when a dragged object is dropped onto another object in the inventory.
   *
   * @param event The DragEvent triggered by the drop.
   */
  public void onDragDropped(
      DragEvent event, HashMap<ImageView, Object> room1Items, TextArea itemChat) {
    // Remove the blue tint after dropping
    ImageView targetImageView = (ImageView) event.getSource();
    targetImageView.setEffect(null);

    // Get Dragboard
    Dragboard db = event.getDragboard();

    // Check if drop was successful
    boolean success = false;

    // Check if Dragboard has a String (we stored earlier)
    if (db.hasString()) {
      int originalIndex = Integer.parseInt(db.getString());
      Object draggedItem =
          GameState.inventory.getObject(
              originalIndex); // Retrieve the dragged object based on the index.

      targetImageView = (ImageView) event.getSource(); // renamed to match the same variable name

      // Check if it's a lock
      if ("lock".equals(targetImageView.getUserData())) {
        // This is where you would unlock the lock and make it invisible
        Lock lockItem = (Lock) room1Items.get(targetImageView);

        // Check whether the dragged item is a key and its ID matches the lock's ID
        if (draggedItem instanceof Keys && lockItem != null && lockItem.isLocked()) {
          Keys keyItem = (Keys) draggedItem;
          if (keyItem.getID() == lockItem.getId()) {
            lockItem.unlockLock();
            targetImageView.setVisible(false);
            String message = lockItem.getMessage();
            ItemChat.getInstance().printChatMessage(itemChat, message);
            inventoryProperty.set(originalIndex, new Object(null));

            success = true; // Only set success to true if the operation is successful.
          } else {
            ItemChat.getInstance()
                .printChatMessage(
                    itemChat, "You Need Key " + lockItem.getId() + " To Unlock The Lock");
          }
        } else { // if its not a key
          ItemChat.getInstance().printChatMessage(itemChat, "You Need A Key To Unlock The Lock");
        }
      } else {
        int targetIndex = (int) targetImageView.getUserData();
        GameState.inventory.swapObject(originalIndex, targetIndex);
        success = true;
      }
    }

    event.setDropCompleted(success);
    event.consume();
  }

  public void onInventoryClicked(MouseEvent event, TextArea itemChat) {
    int index = (int) ((ImageView) event.getSource()).getUserData();
    String message = inventoryProperty.get(index).getItemIdentifier();
    ItemChat.getInstance().printChatMessage(itemChat, message);
  }
}
