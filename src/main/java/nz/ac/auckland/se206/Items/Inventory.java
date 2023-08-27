package nz.ac.auckland.se206.Items;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class Inventory {
  private final SimpleListProperty<Object> inventoryProperty =
      new SimpleListProperty<Object>(FXCollections.observableArrayList());

  public Inventory() {
    for (int i = 0; i < 6; i++) {
      inventoryProperty.add(new Object(null));
    }
  }

  public ListProperty<Object> inventoryProperty() {
    return inventoryProperty;
  }

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

  public void removeObject(Object object) {
    inventoryProperty.remove(object);
  }

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

  public Object getObject(int position) {
    return inventoryProperty.size() > position ? inventoryProperty.get(position) : null;
  }

  public int getSize() {
    return inventoryProperty.size();
  }
}
