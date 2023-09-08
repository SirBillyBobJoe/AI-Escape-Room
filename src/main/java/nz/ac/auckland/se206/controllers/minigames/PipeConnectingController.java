package nz.ac.auckland.se206.controllers.minigames;

import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class PipeConnectingController {
  @FXML private AnchorPane gridAnchor;

  @FXML private GridPane grid;

  private final int GRID_X_SIZE = 5;
  private final int GRID_Y_SIZE = 4;
  private final int GRID_CELL_SIZE = 75;

  // bit 3 - top
  // bit 2 - right
  // bit 1 - bottom
  // bit 0 - left
  private int[][] mapSetup;

  private int[][] mapRotations = new int[GRID_X_SIZE][GRID_Y_SIZE];

  private Pane[][] gridPanes = new Pane[GRID_X_SIZE][GRID_Y_SIZE];

  @FXML
  public void initialize() {
    grid.setStyle("-fx-background-color: #FFFFFF;");
    generateMapSetup();
    createGrid();
  }

  private void createGrid() {
    Random rand = new Random();
    for (int x = 0; x < GRID_X_SIZE; x++) {
      for (int y = 0; y < GRID_Y_SIZE; y++) {
        mapRotations[x][y] = rand.nextInt(4);
        Pane pane = createPane(mapSetup[x][y], x, y);
        gridPanes[x][y] = pane;
        grid.add(pane, x, y);
      }
    }
  }

  private void generateMapSetup() {
    Random rand = new Random();
    mapSetup = new int[GRID_X_SIZE][GRID_Y_SIZE];

    // Work out what walls should have a connection through them
    for (int x = 0; x < GRID_X_SIZE - 1; x++) {
      for (int y = 0; y < GRID_Y_SIZE; y++) {
        if (rand.nextInt(3) > 1) {
          mapSetup[x][y] |= 0b0100;
          mapSetup[x + 1][y] |= 0b0001;
        }
      }
    }

    for (int x = 0; x < GRID_X_SIZE; x++) {
      for (int y = 0; y < GRID_Y_SIZE - 1; y++) {
        if (rand.nextInt(3) > 1) {
          mapSetup[x][y] |= 0b0010;
          mapSetup[x][y + 1] |= 0b1000;
        }
      }
    }

    int numInlets = rand.nextInt(4) + 2;
    System.out.println(numInlets);
    // start from top left and goes clockwises
    for (int i = 0; i < numInlets; i++) {
      int position = rand.nextInt(GRID_X_SIZE * 2 + GRID_Y_SIZE * 2);
      System.out.println(position);
      if (position < GRID_X_SIZE) {
        mapSetup[position][0] |= 0b1000;
      } else if (position < GRID_X_SIZE + GRID_Y_SIZE) {
        int x = GRID_X_SIZE - 1;
        int y = position - GRID_X_SIZE;
        mapSetup[x][y] |= 0b0100;
      } else if (position < GRID_X_SIZE * 2 + GRID_Y_SIZE) {
        int x = GRID_X_SIZE - 1 - (position - GRID_X_SIZE - GRID_Y_SIZE);
        int y = GRID_Y_SIZE - 1;
        mapSetup[x][y] |= 0b0010;
      } else {
        int x = 0;
        int y = GRID_Y_SIZE - 1 - (position - GRID_X_SIZE * 2 - GRID_Y_SIZE);
        mapSetup[x][y] |= 0b0001;
      }
    }
  }

  private Pane createPane(int stucture, int x, int y) {
    Pane pane = new Pane();
    pane.setPrefSize(GRID_CELL_SIZE, GRID_CELL_SIZE);
    pane.setOnMouseClicked(this::handlePaneClick);
    pane.setRotate(mapRotations[x][y] * 90);

    var children = pane.getChildren();
    // Add pipes
    if ((stucture & 0b1000) != 0) {
      // top
      Rectangle rect = new Rectangle(27.5, 0, 20, 47.5);
      rect.setStrokeWidth(0);
      children.add(rect);
    }
    if ((stucture & 0b0100) != 0) {
      // right
      Rectangle rect = new Rectangle(27.5, 27.5, 47.5, 20);
      rect.setStrokeWidth(0);
      children.add(rect);
    }
    if ((stucture & 0b0010) != 0) {
      // bottom
      Rectangle rect = new Rectangle(27.5, 27.5, 20, 47.5);
      rect.setStrokeWidth(0);
      children.add(rect);
    }
    if ((stucture & 0b0001) != 0) {
      // left
      Rectangle rect = new Rectangle(0, 27.5, 47.5, 20);
      rect.setStrokeWidth(0);
      children.add(rect);
    }

    return pane;
  }

  private void handlePaneClick(MouseEvent event) {
    Pane pane = (Pane) event.getSource();
    int x = GridPane.getColumnIndex(pane);
    int y = GridPane.getRowIndex(pane);

    mapRotations[x][y] = (mapRotations[x][y] + 1) % 4;
    pane.setRotate(mapRotations[x][y] * 90);

    boolean complete = true;
    for (int i = 0; i < GRID_X_SIZE; i++) {
      for (int j = 0; j < GRID_Y_SIZE; j++) {
        if (mapRotations[i][j] != 0) {
          complete = false;
          break;
        }
      }
    }

    if (complete) {
      onComplete();
    }
  }

  private void onComplete() {
    System.out.println("Complete");
  }
}
