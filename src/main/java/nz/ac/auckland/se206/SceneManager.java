package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {
  public enum AppUi {
    SCREEN_START
  }

  private static HashMap<AppUi, Parent> map = new HashMap<AppUi, Parent>();
  private static HashMap<AppUi, Boolean> reinitialize = new HashMap<AppUi, Boolean>();

  public static void addAppUi(AppUi ui, Parent root, boolean reinit) {
    map.put(ui, root);
    reinitialize.put(ui, reinit);
  }

  public static Parent getUi(AppUi ui) throws IOException {
    if (reinitialize.get(ui)) {
      // If this room should be re-initialized, create a new instance of the UI.
      // For example, load a new instance from a FXML file:

      Parent newUserInterface =
          App.loadFxml(ui.toString().toLowerCase()); // assuming the method is static
      map.put(ui, newUserInterface);
    }
    return map.get(ui);
  }

  public static void setReinitialise(AppUi ui) throws IOException {
    Parent newUserInterface =
        App.loadFxml(ui.toString().toLowerCase()); // assuming the method is static
    map.put(ui, newUserInterface);
  }
}
