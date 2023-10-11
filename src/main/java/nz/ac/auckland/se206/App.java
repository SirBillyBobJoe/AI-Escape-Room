package nz.ac.auckland.se206;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {
  /** Holds the main scene object. */
  private static Scene scene;

  /**
   * Main entry point of the application.
   *
   * @param args Command-line arguments.
   */
  public static void main(final String[] args) {

    launch();
  }

  /**
   * Sets the root of the scene graph.
   *
   * @param fxml The name of the FXML file to load.
   * @throws IOException If the FXML file cannot be found.
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  public static Parent loadFxml(final String fxml) throws IOException {
    // gets the url and loads the files
    try {
      URL fileUrl = App.class.getResource("/fxml/" + fxml + ".fxml");
      if (fileUrl == null) {
        throw new java.io.FileNotFoundException("FXML file can't be found");
      }
      // prints errors if not work
      return FXMLLoader.load(fileUrl);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("No page " + fxml + " please check FXMLLoader.");
    }
    return null;
  }

  /**
   * Sets the root of the scene graph to a specific UI.
   *
   * @param ui The UI to set.
   * @throws IOException If loading the UI from fxml fails.
   */
  public static void setUserInterface(AppUi ui) throws IOException {
    Parent uiParent = SceneManager.getUi(ui);
    setUserInterface(ui, uiParent);
  }

  /**
   * Sets the root of the scene graph to a specific Parent.
   *
   * @param ui The ui to set.
   * @param uiParent the parent object to set.
   */
  public static void setUserInterface(AppUi ui, Parent uiParent) {
    // sets the root for the game
    scene.setRoot(uiParent);
    scene.getRoot().requestFocus();
    Stage stage = (Stage) scene.getWindow();
    // get the dimensions for the stage
    double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
    double additionalHeight = stage.getHeight() - stage.getScene().getHeight();

    if (ui.equals(AppUi.UIOVERLAY)) {
      // sets the dimensions for the stage
      stage.setWidth(1100 + additionalWidth);
      stage.setHeight(600 + additionalHeight);

      // Calculate screen dimensions
      Screen screen = Screen.getPrimary();
      Rectangle2D bounds = screen.getVisualBounds();

      // Calculate the position for the top-left corner
      double centerX =
          bounds.getMinX() + (bounds.getWidth() - 1100) / 2; // Adjust "800" for your window width
      double centerY =
          bounds.getMinY() + (bounds.getHeight() - 600) / 2; // Adjust "600" for your window height

      // Set the stage's position
      stage.setX(centerX);
      stage.setY(centerY);
    } else {
      // sets the dimensions if not overlay
      stage.setWidth(1100 + additionalWidth);
      stage.setHeight(600 + additionalHeight);

      // Calculate screen dimensions
      Screen screen = Screen.getPrimary();
      Rectangle2D bounds = screen.getVisualBounds();

      // Calculate the position for the top-left corner
      double centerX =
          bounds.getMinX() + (bounds.getWidth() - 1100) / 2; // Adjust "800" for your window width
      double centerY =
          bounds.getMinY() + (bounds.getHeight() - 600) / 2; // Adjust "600" for your window height

      // Set the stage's position
      stage.setX(centerX);
      stage.setY(centerY);
    }
    System.out.println(scene.getRoot().isFocused());
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    final Image image = new Image("images/menu/gameMaster.png");
    stage.getIcons().add(image);

    // starts the room
    scene = new Scene(SceneManager.getUi(AppUi.STARTSCREEN), 1100, 600);
    scene.getRoot().requestFocus();
    stage.setTitle("The Singularity's Grip");
    // resizes not possible
    stage.setResizable(false);
    stage.setScene(scene);

    // Calculate screen dimensions
    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();

    // Calculate the position for the top-left corner
    double centerX =
        bounds.getMinX() + (bounds.getWidth() - 1100) / 2; // Adjust "800" for your window width
    double centerY =
        bounds.getMinY() + (bounds.getHeight() - 600) / 2; // Adjust "600" for your window height

    // Set the stage's position
    stage.setX(centerX);
    stage.setY(centerY);

    stage.show();
    // ends app after closing
    stage.setOnCloseRequest(
        event -> {
          System.exit(0);
        });
  }
}
