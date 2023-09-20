package nz.ac.auckland.se206.items;

import java.util.HashMap;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
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
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.ItemChat;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;

/** Represents the inventory of objects within the game. */
public class Inventory {
  private TextArea itemChat;

  private final SimpleListProperty<Object> inventoryProperty =
      new SimpleListProperty<Object>(FXCollections.observableArrayList());

  /** Creates an inventory with 6 empty slots. */
  public Inventory() {
    for (int i = 0; i < 6; i++) {
      inventoryProperty.add(new Object(null));
    }
  }

  /**
   * sets the itemChat for the inventory
   *
   * @param itemChat text area
   */
  public void setItemChat(TextArea itemChat) {
    this.itemChat = itemChat;
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
    if (inventoryProperty.contains(object)) {
      int index = inventoryProperty.indexOf(object);
      inventoryProperty.set(index, new Object(null));
    }
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
    // logic for inventory
    Object object = (j < inventoryProperty.size()) ? inventoryProperty.get(j) : null;
    // sets the position
    inventoryProperty.set(i, object);
    if (object != null) {
      object.setPosition(i);
    }
    // sets the property
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
      ColorAdjust colorAdjust = new ColorAdjust();
      event.acceptTransferModes(TransferMode.MOVE);
      Node targetImageNode = (Node) event.getSource();
      // when puzzles are off
      if (!GameState.isPuzzlesOn.getValue()) {
        if (targetImageNode instanceof Rectangle) {
          Rectangle rectangle = (Rectangle) targetImageNode;
          rectangle.setFill(Color.RED);
          targetImageNode.setOpacity(0.22);
          return;
        } else {
          /// Max hue
          colorAdjust.setHue(-0.5);
          // Max saturation
          colorAdjust.setSaturation(1);
          targetImageNode.setEffect(colorAdjust);
          return;
        }
      }
      // if its a rectangle
      if (targetImageNode instanceof Rectangle) {
        targetImageNode.setOpacity(0.22);
        return;
      }
      // Make it really blue when hovered over
      ImageView targetImageView = (ImageView) event.getSource();
      // Max hue
      colorAdjust.setHue(1);
      // Max saturation
      colorAdjust.setSaturation(1);
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
    Node targetImageNode = (Node) event.getSource();
    if (targetImageNode instanceof Rectangle) {
      targetImageNode.setOpacity(0);
      return;
    }
    // Remove the blue tint after dropping
    ImageView targetImageView = (ImageView) event.getSource();
    targetImageView.setEffect(null);
  }

  /**
   * Handles the event when a dragged object is dropped onto another object in the inventory.
   *
   * @param event The DragEvent triggered by the drop.
   */
  public void onDragDropped(DragEvent event, HashMap<ImageView, Object> room1Items) {
    Node targetImageNode = (Node) event.getSource();
    targetImageNode.setEffect(null);

    // Get Dragboard
    Dragboard db = event.getDragboard();

    // Check if drop was successful
    boolean success = false;

    // Check if Dragboard has a String (we stored earlier)
    if (db.hasString()) {
      int originalIndex = Integer.parseInt(db.getString());
      Object draggedItem = GameState.inventory.getObject(originalIndex);
      // Retrieve the dragged object based on the index.
      if (targetImageNode instanceof Rectangle && targetImageNode.getId().equals("brickWall")) {
        System.out.println(GameState.wallCount);
        if (draggedItem instanceof Hammer) {
          new MouseClick().play();
          // if wall count is 1
          if (!(GameState.wallCount-- > 1)) {
            targetImageNode.setVisible(false);
            ItemChat.getInstance().printChatMessage(itemChat, "You have broken the wall.");
          }
          // prints a message
          ItemChat.getInstance().printChatMessage(itemChat, "You have cracked the wall.");
        } else {
          ItemChat.getInstance().printChatMessage(itemChat, "You need to use a hammer.");
        }
        return;
      }
      // Remove the blue tint after dropping
      ImageView targetImageView = (ImageView) event.getSource();

      // Check if it's a lock
      if ("lock".equals(targetImageView.getUserData())) {
        // This is where you would unlock the lock and make it invisible
        Lock lockItem = (Lock) room1Items.get(targetImageView);

        // Check whether the dragged item is a key and its ID matches the lock's ID
        if (draggedItem instanceof Keys && lockItem != null && lockItem.isLocked()) {
          Keys keyItem = (Keys) draggedItem;
          if (keyItem.getIdentification() == lockItem.getId()) {
            lockItem.unlockLock();
            String message = lockItem.getMessage();
            ItemChat.getInstance().printChatMessage(itemChat, message);
            inventoryProperty.set(originalIndex, new Object(null));

            success = true; // Only set success to true if the operation is successful.
          } else {
            ItemChat.getInstance()
                .printChatMessage(itemChat, "You need a key to unlock The padlock.");
          }
        } else { // if its not a key
          ItemChat.getInstance()
              .printChatMessage(itemChat, "You need a key to unlock The padlock.");
        }
      } else if ("candle".equals(targetImageView.getUserData())) { // if its a candle

        Candle candleItem = (Candle) room1Items.get(targetImageView);

        // Check whether the dragged item is a lighter
        if (draggedItem instanceof Lighter && candleItem != null) {

          candleItem.changeCandle();
          targetImageView.setImage(candleItem.getImage());
          String message = candleItem.getMessage();
          ItemChat.getInstance().printChatMessage(itemChat, message);

          success = true; // Only set success to true if the operation is successful.

        } else { // if its not a lighter
          ItemChat.getInstance().printChatMessage(itemChat, "You need a lighter to light candles.");
        }

      } else {
        // gets the userdata
        int targetIndex = (int) targetImageView.getUserData();
        GameState.inventory.swapObject(originalIndex, targetIndex);
        success = true;
      }
    }

    event.setDropCompleted(success);
    event.consume();
  }

  /**
   * Handles the event when a regular item in the game room is clicked. Displays messages to the
   * chat and updates the game state accordingly.
   *
   * @param event The MouseEvent triggered by clicking on the item.
   */
  public void onRegularItemClicked(MouseEvent event) {
    itemChat.clear();
    Node node = (Node) event.getSource();
    if (node.getId().equals("exitDoor") // if its the exit door tell them u cant escape yet
        && !GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) {
      ItemChat.getInstance()
          .printChatMessage(itemChat, "You need to solve the padLock combination.");
      return;
    }
    ImageView imageView = (ImageView) event.getSource();

    Object item = GameState.currentRoomItems.get(imageView);

    // if not a lock
    if (item != null) {
      // if its not a lock
      if (!(item instanceof Lock)) {

        imageView.setVisible(false);
        GameState.inventory.addObject(item);
      }
      // adds message to item chat as if it was typing
      String message = item.getMessage();
      ItemChat.getInstance().printChatMessage(itemChat, message);
    }
  }

  /**
   * Handles the event when an item in the inventory is clicked. Displays item information in the
   * chat.
   *
   * @param event The MouseEvent triggered by clicking on the inventory item.
   */
  public void onInventoryClicked(MouseEvent event) {
    int index = (int) ((ImageView) event.getSource()).getUserData();
    String message = inventoryProperty.get(index).getItemIdentifier();
    ItemChat.getInstance().printChatMessage(itemChat, message);
  }

  /**
   * Checks if the inventory contains a specific object.
   *
   * @param object The object to check for in the inventory.
   * @return true if the inventory contains the object, false otherwise.
   */
  public boolean containsItem(Object object) {
    return inventoryProperty.contains(object);
  }

  /**
   * Sets a message to be displayed in the item chat.
   *
   * @param msg The message string to be displayed.
   */
  public void setTextChat(String msg) {
    ItemChat.getInstance().printChatMessage(itemChat, msg);
  }
}
