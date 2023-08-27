package nz.ac.auckland.se206.Items;

import javafx.scene.image.Image;

public class Object {
  private Image image;

  private int position;

  public Object(Image image) {
    this.image = image;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getPosition() {
    return position;
  }

  public Image getImage() {
    return image;
  }
}
