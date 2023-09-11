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
import nz.ac.auckland.se206.GameState;

/** Controller for the Pipe Connecting Mini-game. */
public class PipeConnectingController {
  @FXML private AnchorPane gridAnchor;

  @FXML private GridPane grid;

  private int gridXSize, gridYSize;
  private final int GRID_CELL_SIZE = 75;
  private final double CELL_SIZE = 75;

  /** Represents a point in the grid. */
  private class Point {
    private int x;
    private int y;

    /**
     * Constructor to initialize the Point.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
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

  private int[][] mapSetup;
  private List<Point> inlets;
  private int[][] mapRotations;
  private Pane[][] gridPanes;

  /** Initializes the grid based on the game's difficulty. */
  @FXML
  public void initialize() {
    setGamesDifficulty();
    initializeDataStructures();
    generateMapSetup();
    createGrid();
  }

  /** Sets the size of the grid based on the game's difficulty. */
  private void setGamesDifficulty() {
    if (GameState.difficulty.equals("easy")) {
      // Easy difficulty
      gridXSize = 4;
      gridYSize = 3;
    } else if (GameState.difficulty.equals("medium")) {
      // Medium difficulty
      gridXSize = 5;
      gridYSize = 4;
    } else if (GameState.difficulty.equals("hard")) {
      // Hard difficulty
      gridXSize = 6;
      gridYSize = 5;
    } else {
      // Default difficulty
      gridXSize = 4;
      gridYSize = 3;
    }
  }

  /** Resets the datastructures to default values */
  private void initializeDataStructures() {
    mapRotations = new int[gridXSize][gridYSize];
    gridPanes = new Pane[gridXSize][gridYSize];
    inlets = new ArrayList<Point>();
  }

  /** Creates the grid represents by mapsetup and the game difficulty */
  private void createGrid() {
    Random rand = new Random();

    gridAnchor.setLayoutX((800 - (gridXSize + 1) * GRID_CELL_SIZE) / 2);
    gridAnchor.setLayoutY((600 - (gridYSize + 1) * GRID_CELL_SIZE) / 2);
    gridAnchor.setPrefSize((gridXSize + 1) * GRID_CELL_SIZE, (gridYSize + 1) * GRID_CELL_SIZE);
    grid.setPrefSize(gridXSize * GRID_CELL_SIZE, gridYSize * GRID_CELL_SIZE);
    for (int i = 4; i < gridXSize; i++) {
      grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(GRID_CELL_SIZE));
    }
    for (int i = 3; i < gridYSize; i++) {
      grid.getRowConstraints().add(new javafx.scene.layout.RowConstraints(GRID_CELL_SIZE));
    }
    grid.setStyle("-fx-background-color: #FFFFFF;");

    for (int x = 0; x < gridXSize; x++) {
      for (int y = 0; y < gridYSize; y++) {
        mapRotations[x][y] = rand.nextInt(4);
        Pane pane = createPane(mapSetup[x][y], x, y);
        gridPanes[x][y] = pane;
        grid.add(pane, x, y);
      }
    }
    createInletRectangles();
  }

  /**
   * This method generates a randomized grid map setup. The grid is represented by a 2D integer
   * array with each integer encoding connection directions using binary representation. Bits
   * represent, in order, connections to the North, East, South, and West. For example, 0b0100
   * represents a connection to the East.
   */
  private void generateMapSetup() {
    // Initialize a random number generator
    Random rand = new Random();

    // Initialize the grid based on provided dimensions
    mapSetup = new int[gridXSize][gridYSize];

    // Variable to store the starting point for the grid paths
    Point start = null;

    // List to store inlet points around the grid boundary
    inlets = new ArrayList<Point>();

    // Randomly decide the number of inlets, minimum 2 and maximum 5
    int numInlets = rand.nextInt(4) + 2;
    System.out.println(numInlets);

    // Randomly place inlets on the grid boundary, starting from the top-left and moving clockwise
    for (int i = 0; i < numInlets; i++) {
      int position = rand.nextInt(gridXSize * 2 + gridYSize * 2);

      // Place on top edge
      if (position < gridXSize) {
        mapSetup[position][0] |= 0b1000;
        inlets.add(new Point(position, -1));
        if (start == null) {
          start = new Point(position, 0);
        }

        // Place on right edge
      } else if (position < gridXSize + gridYSize) {
        int x = gridXSize - 1;
        int y = position - gridXSize;
        mapSetup[x][y] |= 0b0100;
        inlets.add(new Point(gridXSize, y));
        if (start == null) {
          start = new Point(x, y);
        }

        // Place on bottom edge
      } else if (position < gridXSize * 2 + gridYSize) {
        int x = gridXSize - 1 - (position - gridXSize - gridYSize);
        int y = gridYSize - 1;
        mapSetup[x][y] |= 0b0010;
        inlets.add(new Point(x, gridYSize));
        if (start == null) {
          start = new Point(x, y);
        }

        // Place on left edge
      } else {
        int x = 0;
        int y = gridYSize - 1 - (position - gridXSize * 2 - gridYSize);
        mapSetup[x][y] |= 0b0001;
        inlets.add(new Point(-1, y));
        if (start == null) {
          start = new Point(x, y);
        }
      }
    }

    // Set up a list of available squares for path generation
    Set<Point> availableSquares = new HashSet<>();
    for (int x = 0; x < gridXSize; x++) {
      for (int y = 0; y < gridYSize; y++) {
        availableSquares.add(new Point(x, y));
      }
    }

    // Start path generation from the start point
    List<Point> activePaths = new ArrayList<>();
    activePaths.add(start);
    availableSquares.remove(start);

    // Define possible directions for path movement
    int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    // Set a limit on the maximum iterations to prevent infinite loops
    int maxIterations = 10000;
    int currentIterations = 0;

    // Generate paths until no more active paths remain or maxIterations is reached
    while (!activePaths.isEmpty() && currentIterations < maxIterations) {
      currentIterations++;

      // Pick a random current point from the active paths
      Point current = activePaths.get(rand.nextInt(activePaths.size()));

      // List to store neighboring points which haven't been visited
      List<Point> unvisitedNeighbors = new ArrayList<>();
      // List to store neighbors with exactly two connections
      List<Point> twoConnectedNeighbors = new ArrayList<>();

      // Identify valid neighbors of the current point
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

      // Log a warning if maxIterations is reached
      if (currentIterations >= maxIterations) {
        System.out.println("Warning: Maximum iterations reached. Exiting...");
      }

      // If no suitable neighbors are found, remove current point from active paths
      if (unvisitedNeighbors.isEmpty() && twoConnectedNeighbors.isEmpty()) {
        activePaths.remove(current);
        continue;
      }

      // Choose a neighbor to extend the path to
      Point chosenNeighbor;
      if (!unvisitedNeighbors.isEmpty()) {
        chosenNeighbor = unvisitedNeighbors.get(rand.nextInt(unvisitedNeighbors.size()));
        activePaths.add(chosenNeighbor);
        availableSquares.remove(chosenNeighbor);
      } else {
        chosenNeighbor = twoConnectedNeighbors.get(rand.nextInt(twoConnectedNeighbors.size()));
        // Do not continue walking from a thrice-connected cell
        activePaths.remove(current);
      }

      // Update the map to record the path connection
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

    // After main path generation, ensure all grid cells have at least two connections
    for (int x = 0; x < gridXSize; x++) {
      for (int y = 0; y < gridYSize; y++) {
        Point cell = new Point(x, y);
        if (Integer.bitCount(mapSetup[x][y]) == 1) {
          // Force a second connection for cells with only one connection
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

  /**
   * Checks if a point is within the grid.
   *
   * @param p Point to check
   * @return true if within grid, false otherwise
   */
  private boolean isWithinGrid(Point p) {
    return p.x >= 0 && p.x < gridXSize && p.y >= 0 && p.y < gridYSize;
  }

  /**
   * Creates a pane for a grid cell based on its structure.
   *
   * @param structure Configuration of the pipes
   * @param x x-coordinate
   * @param y y-coordinate
   * @return Pane for the grid cell
   */
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

  /** Creates rectangles to represent inlets in the grid. */
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
      } else if (x == gridXSize) { // Right side
        layoutX = (gridXSize + 0.5) * CELL_SIZE;
        layoutY = (y + 1) * CELL_SIZE - 0.5 * width;
        horizontal = true;
      } else if (y == gridYSize) { // Bottom side
        layoutX = (x + 1) * CELL_SIZE - 0.5 * width;
        layoutY = (gridYSize + 0.5) * CELL_SIZE;
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

  /**
   * Rotates the given cell data by a specified rotation.
   *
   * @param cellData The binary representation of cell's connections.
   * @param rotation Number of times the cell data should be rotated. Positive values rotate
   *     clockwise, negative values rotate counter-clockwise.
   * @return Rotated cell data.
   */
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

  /**
   * Checks if a cell and its adjacent cell in a specified direction are connected.
   *
   * @param cell The point representing the current cell's coordinates.
   * @param direction The binary representation of the direction to check.
   * @return True if the cells are connected, otherwise false.
   */
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

  /**
   * Checks the entire map's grid for connectivity. If any issues with the map's connectivity are
   * found, they will be printed to the console.
   */
  public void checkCompleteness() {
    for (int i = 0; i < gridXSize; i++) {
      for (int j = 0; j < gridYSize; j++) {
        Point currentCell = new Point(i, j);
        int cellData = rotateCellData(mapSetup[i][j], mapRotations[i][j]);

        if ((cellData & 0b1000) != 0 && !areAdjacentCellsConnected(currentCell, 0b1000)) return;
        if ((cellData & 0b0100) != 0 && !areAdjacentCellsConnected(currentCell, 0b0100)) return;
        if ((cellData & 0b0010) != 0 && !areAdjacentCellsConnected(currentCell, 0b0010)) return;
        if ((cellData & 0b0001) != 0 && !areAdjacentCellsConnected(currentCell, 0b0001)) return;
      }
    }

    for (Point inletPosition : inlets) {
      if (!isInletConnected(inletPosition)) return;
    }

    onComplete();
  }

  /**
   * Checks if a specified inlet has any connections.
   *
   * @param inlet The point representing the inlet's coordinates.
   * @return True if the inlet has a connection, otherwise false.
   */
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
    else if (inlet.x == gridXSize) {
      adjacentPoint = new Point(gridXSize - 1, inlet.y);
      mask = 0b0100; // Check for the rightward pipe from the adjacent cell
    }
    // Bottom side
    else if (inlet.y == gridYSize) {
      adjacentPoint = new Point(inlet.x, gridYSize - 1);
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

  /**
   * Event handler for pane clicks. Rotates the clicked pane and checks if the map is complete after
   * every click.
   *
   * @param event The MouseEvent triggered by the click.
   */
  private void handlePaneClick(MouseEvent event) {
    Pane pane = (Pane) event.getSource();
    int x = GridPane.getColumnIndex(pane);
    int y = GridPane.getRowIndex(pane);

    mapRotations[x][y] = (mapRotations[x][y] + 1) % 4;
    pane.setRotate(mapRotations[x][y] * 90);

    checkCompleteness();
  }

  /** Called when the map is found to be complete. Prints a completion message to the console. */
  private void onComplete() {
    System.out.println("Complete");
  }
}
