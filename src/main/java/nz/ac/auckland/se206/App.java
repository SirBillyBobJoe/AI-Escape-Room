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

  private static Scene scene;

  public static void main(final String[] args) {
    launch();
  }

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
    try {
      URL fileUrl = App.class.getResource("/fxml/" + fxml + ".fxml");
      if (fileUrl == null) {
        throw new java.io.FileNotFoundException("FXML file can't be found");
      }

      return FXMLLoader.load(fileUrl);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("No page " + fxml + " please check FXMLLoader.");
    }
    return null;
  }

  public static void setUserInterface(AppUi ui) throws IOException {
    scene.setRoot(SceneManager.getUi(ui));
    scene.getRoot().requestFocus();
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
    scene = new Scene(SceneManager.getUi(AppUi.SCREENSTART), 630, 630);
    scene.getRoot().requestFocus();
    stage.setScene(scene);
    stage.show();
    stage.setOnCloseRequest(
        event -> {
          System.exit(0);
        });
  }
}
