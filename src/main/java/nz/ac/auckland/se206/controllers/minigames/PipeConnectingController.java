package nz.ac.auckland.se206.controllers.minigames;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
  private final double CELL_SIZE = 75;

  private class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }

      Point point = (Point) obj;
      return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
      int result = 17;
      result = 31 * result + x;
      result = 31 * result + y;
      return result;
    }
  }

  // bit 3 - top
  // bit 2 - right
  // bit 1 - bottom
  // bit 0 - left
  private int[][] mapSetup;
  private List<Point> inlets = new ArrayList<Point>();

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
    createInletRectangles();
  }

  private void generateMapSetup() {
    Random rand = new Random();
    mapSetup = new int[GRID_X_SIZE][GRID_Y_SIZE];

    Point start = null;
    inlets = new ArrayList<Point>();
    int numInlets = rand.nextInt(4) + 2;
    System.out.println(numInlets);
    // start from top left and goes clockwises
    for (int i = 0; i < numInlets; i++) {
      int position = rand.nextInt(GRID_X_SIZE * 2 + GRID_Y_SIZE * 2);
      if (position < GRID_X_SIZE) {
        mapSetup[position][0] |= 0b1000;
        inlets.add(new Point(position, -1));
        if (start == null) {
          start = new Point(position, 0);
        }
      } else if (position < GRID_X_SIZE + GRID_Y_SIZE) {
        int x = GRID_X_SIZE - 1;
        int y = position - GRID_X_SIZE;
        mapSetup[x][y] |= 0b0100;
        inlets.add(new Point(GRID_X_SIZE, y));
        if (start == null) {
          start = new Point(x, y);
        }
      } else if (position < GRID_X_SIZE * 2 + GRID_Y_SIZE) {
        int x = GRID_X_SIZE - 1 - (position - GRID_X_SIZE - GRID_Y_SIZE);
        int y = GRID_Y_SIZE - 1;
        mapSetup[x][y] |= 0b0010;
        inlets.add(new Point(x, GRID_Y_SIZE));
        if (start == null) {
          start = new Point(x, y);
        }
      } else {
        int x = 0;
        int y = GRID_Y_SIZE - 1 - (position - GRID_X_SIZE * 2 - GRID_Y_SIZE);
        mapSetup[x][y] |= 0b0001;
        inlets.add(new Point(-1, y));
        if (start == null) {
          start = new Point(x, y);
        }
      }
    }

    Set<Point> availableSquares = new HashSet<>();
    for (int x = 0; x < GRID_X_SIZE; x++) {
      for (int y = 0; y < GRID_Y_SIZE; y++) {
        availableSquares.add(new Point(x, y));
      }
    }

    List<Point> activePaths = new ArrayList<>();
    activePaths.add(start);
    availableSquares.remove(start);

    int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    int maxIterations = 10000; // some large number based on grid size and other parameters
    int currentIterations = 0;
    while (!activePaths.isEmpty() && currentIterations < maxIterations) {
      currentIterations++;

      Point current = activePaths.get(rand.nextInt(activePaths.size()));

      List<Point> unvisitedNeighbors = new ArrayList<>();
      List<Point> twoConnectedNeighbors = new ArrayList<>();
      for (int[] d : directions) {
        Point neighbor = new Point(current.x + d[0], current.y + d[1]);
        if (isWithinGrid(neighbor)) {
          if (availableSquares.contains(neighbor)) {
            unvisitedNeighbors.add(neighbor);
          } else {
            int connections = Integer.bitCount(mapSetup[neighbor.x][neighbor.y]);
            if (connections == 2) {
              twoConnectedNeighbors.add(neighbor);
            }
          }
        }
      }
      if (currentIterations >= maxIterations) {
        System.out.println("Warning: Maximum iterations reached. Exiting...");
      }

      // If no unvisited neighbors, and none with only two connections, terminate this path
      if (unvisitedNeighbors.isEmpty() && twoConnectedNeighbors.isEmpty()) {
        activePaths.remove(current);
        continue;
      }

      Point chosenNeighbor;
      if (!unvisitedNeighbors.isEmpty()) {
        chosenNeighbor = unvisitedNeighbors.get(rand.nextInt(unvisitedNeighbors.size()));
        activePaths.add(chosenNeighbor);
        availableSquares.remove(chosenNeighbor);
      } else {
        chosenNeighbor = twoConnectedNeighbors.get(rand.nextInt(twoConnectedNeighbors.size()));
        // Do not continue walking from a thrice-connected cell.
        activePaths.remove(current);
      }

      if (chosenNeighbor.x > current.x) {
        mapSetup[current.x][current.y] |= 0b0100;
        mapSetup[chosenNeighbor.x][chosenNeighbor.y] |= 0b0001;
      } else if (chosenNeighbor.x < current.x) {
        mapSetup[current.x][current.y] |= 0b0001;
        mapSetup[chosenNeighbor.x][chosenNeighbor.y] |= 0b0100;
      } else if (chosenNeighbor.y > current.y) {
        mapSetup[current.x][current.y] |= 0b0010;
        mapSetup[chosenNeighbor.x][chosenNeighbor.y] |= 0b1000;
      } else {
        mapSetup[current.x][current.y] |= 0b1000;
        mapSetup[chosenNeighbor.x][chosenNeighbor.y] |= 0b0010;
      }
    }

    // Ensure all cells have at least two connections.
    for (int x = 0; x < GRID_X_SIZE; x++) {
      for (int y = 0; y < GRID_Y_SIZE; y++) {
        Point cell = new Point(x, y);
        if (Integer.bitCount(mapSetup[x][y]) == 1) {
          // Force a second connection
          for (int[] d : directions) {
            Point neighbor = new Point(cell.x + d[0], cell.y + d[1]);
            if (isWithinGrid(neighbor) && Integer.bitCount(mapSetup[neighbor.x][neighbor.y]) < 3) {
              if (neighbor.x > cell.x) {
                mapSetup[cell.x][cell.y] |= 0b0100;
                mapSetup[neighbor.x][neighbor.y] |= 0b0001;
              } else if (neighbor.x < cell.x) {
                mapSetup[cell.x][cell.y] |= 0b0001;
                mapSetup[neighbor.x][neighbor.y] |= 0b0100;
              } else if (neighbor.y > cell.y) {
                mapSetup[cell.x][cell.y] |= 0b0010;
                mapSetup[neighbor.x][neighbor.y] |= 0b1000;
              } else {
                mapSetup[cell.x][cell.y] |= 0b1000;
                mapSetup[neighbor.x][neighbor.y] |= 0b0010;
              }
              break;
            }
          }
        }
      }
    }
  }

  private boolean isWithinGrid(Point p) {
    return p.x >= 0 && p.x < GRID_X_SIZE && p.y >= 0 && p.y < GRID_Y_SIZE;
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

  private void createInletRectangles() {
    for (Point inlet : inlets) {
      // Determine the position of the inlet
      int x = inlet.x;
      int y = inlet.y;

      double layoutX, layoutY;
      double width = 20, height = 30;
      boolean horizontal = false;
      if (y == -1) { // Top side
        layoutX = (x + 1) * CELL_SIZE - 0.5 * width;
        layoutY = 7.5;
      } else if (x == GRID_X_SIZE) { // Right side
        layoutX = (GRID_X_SIZE + 0.5) * CELL_SIZE;
        layoutY = (y + 1) * CELL_SIZE - 0.5 * width;
        horizontal = true;
      } else if (y == GRID_Y_SIZE) { // Bottom side
        layoutX = (x + 1) * CELL_SIZE - 0.5 * width;
        layoutY = (GRID_Y_SIZE + 0.5) * CELL_SIZE;
      } else { // Left side
        layoutX = 7.5;
        layoutY = (y + 1) * CELL_SIZE - 0.5 * width;
        horizontal = true;
      }

      // Create the rectangle for the inlet
      if (horizontal) {
        double temp = width;
        width = height;
        height = temp;
      }
      Rectangle inletRectangle = new Rectangle(width, height);
      inletRectangle.setLayoutX(layoutX);
      inletRectangle.setLayoutY(layoutY);
      inletRectangle.setStrokeWidth(0);

      gridAnchor.getChildren().add(inletRectangle);
    }
  }

  private int rotateCellData(int cellData, int rotation) {
    rotation = rotation % 4; // since there are only 4 possible states
    if (rotation < 0) rotation += 4; // handle negative rotations

    while (rotation-- > 0) {
      boolean leftMostBit = (cellData & 0b1000) != 0;
      cellData = (cellData << 1) & 0b1111; // left shift and mask to ensure only 4 bits remain
      if (leftMostBit) cellData |= 0b0001; // wrap around
    }

    return cellData;
  }

  private boolean areAdjacentCellsConnected(Point cell, int direction) {
    int oppositeDirectionMask;
    Point adjacentCell;

    switch (direction) {
      case 0b1000: // Top
        oppositeDirectionMask = 0b0010;
        adjacentCell = new Point(cell.x, cell.y - 1);
        break;
      case 0b0100: // Right
        oppositeDirectionMask = 0b0001;
        adjacentCell = new Point(cell.x + 1, cell.y);
        break;
      case 0b0010: // Bottom
        oppositeDirectionMask = 0b1000;
        adjacentCell = new Point(cell.x, cell.y + 1);
        break;
      default: // Left
        oppositeDirectionMask = 0b0100;
        adjacentCell = new Point(cell.x - 1, cell.y);
    }

    if (!isWithinGrid(adjacentCell)) {
      // The adjacent cell is outside the grid, so the current cell should be an inlet
      boolean isValidInlet = inlets.contains(adjacentCell);
      if (!isValidInlet) {
        System.out.println(
            "An inlet at position " + adjacentCell.x + ", " + adjacentCell.y + " does not exist!");
      }
      return isValidInlet;
    }

    return (mapSetup[adjacentCell.x][adjacentCell.y] & oppositeDirectionMask) != 0;
  }

  public void checkCompleteness() {
    for (int i = 0; i < GRID_X_SIZE; i++) {
      for (int j = 0; j < GRID_Y_SIZE; j++) {
        Point currentCell = new Point(i, j);
        int cellData = rotateCellData(mapSetup[i][j], mapRotations[i][j]);

        if ((cellData & 0b1000) != 0 && !areAdjacentCellsConnected(currentCell, 0b1000)) {
          System.out.println("Map is not fully connected!");
          return;
        }
        if ((cellData & 0b0100) != 0 && !areAdjacentCellsConnected(currentCell, 0b0100)) {
          System.out.println("Map is not fully connected!");
          return;
        }
        if ((cellData & 0b0010) != 0 && !areAdjacentCellsConnected(currentCell, 0b0010)) {
          System.out.println("Map is not fully connected!");
          return;
        }
        if ((cellData & 0b0001) != 0 && !areAdjacentCellsConnected(currentCell, 0b0001)) {
          System.out.println("Map is not fully connected!");
          return;
        }
      }
    }

    for (Point inletPosition : inlets) {
      if (!isInletConnected(inletPosition)) {
        System.out.println(
            "An inlet at position " + inletPosition + " does not have any connection!");
        return;
      }
    }

    onComplete();
  }

  private boolean isInletConnected(Point inlet) {
    // Define the adjacent point and the bitmask for the direction from the cell to the inlet
    Point adjacentPoint = null;
    int mask = 0;

    // Top side
    if (inlet.y == -1) {
      adjacentPoint = new Point(inlet.x, 0);
      mask = 0b1000; // Check for the upward pipe from the adjacent cell
    }
    // Right side
    else if (inlet.x == GRID_X_SIZE) {
      adjacentPoint = new Point(GRID_X_SIZE - 1, inlet.y);
      mask = 0b0100; // Check for the rightward pipe from the adjacent cell
    }
    // Bottom side
    else if (inlet.y == GRID_Y_SIZE) {
      adjacentPoint = new Point(inlet.x, GRID_Y_SIZE - 1);
      mask = 0b0010; // Check for the downward pipe from the adjacent cell
    }
    // Left side
    else if (inlet.x == -1) {
      adjacentPoint = new Point(0, inlet.y);
      mask = 0b0001; // Check for the leftward pipe from the adjacent cell
    }

    if (adjacentPoint != null) {
      int cellData = mapSetup[adjacentPoint.x][adjacentPoint.y];
      int rotatedCellData =
          rotateCellData(cellData, mapRotations[adjacentPoint.x][adjacentPoint.y]);

      // Check if the adjacent cell has a pipe in the direction of the inlet
      return (rotatedCellData & mask) != 0;
    }
    return false;
  }

  private void handlePaneClick(MouseEvent event) {
    Pane pane = (Pane) event.getSource();
    int x = GridPane.getColumnIndex(pane);
    int y = GridPane.getRowIndex(pane);

    mapRotations[x][y] = (mapRotations[x][y] + 1) % 4;
    pane.setRotate(mapRotations[x][y] * 90);

    checkCompleteness();
  }

  private void onComplete() {
    System.out.println("Complete");
  }
}
