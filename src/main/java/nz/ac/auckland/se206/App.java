package nz.ac.auckland.se206;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
   * @throws IOException If setting the UI fails.
   */
  public static void setUserInterface(AppUi ui) throws IOException {
    // sets the root for the game
    scene.setRoot(SceneManager.getUi(ui));
    scene.getRoot().requestFocus();
    Stage stage = (Stage) scene.getWindow();
    // get the dimensions for the stage
    double additionalWidth = stage.getWidth() - stage.getScene().getWidth();
    double additionalHeight = stage.getHeight() - stage.getScene().getHeight();
    if (ui.equals(AppUi.UIOVERLAY)) {
      // sets the dimensions for the stage
      stage.setWidth(1100 + additionalWidth);
      stage.setHeight(600 + additionalHeight);
    } else {
      // sets the dimensions if not overlay
      stage.setWidth(800 + additionalWidth);
      stage.setHeight(600 + additionalHeight);
      System.out.println("800");
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
    // starts the room
    scene = new Scene(SceneManager.getUi(AppUi.STARTSCREEN), 800, 600);
    scene.getRoot().requestFocus();
    // resizes not possible
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();
    // ends app after closing
    stage.setOnCloseRequest(
        event -> {
          System.exit(0);
        });
  }
}
