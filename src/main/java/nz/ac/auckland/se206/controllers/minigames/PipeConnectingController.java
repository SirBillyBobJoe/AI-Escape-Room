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
import nz.ac.auckland.se206.SceneManager.Puzzle;

/** Controller for the Pipe Connecting Mini-game. */
public class PipeConnectingController {
  @FXML private AnchorPane gridAnchor;

  @FXML private GridPane grid;

  private int gridHorizontalSize, gridverticalSize;
  private double gridCellSize;
  private double rectWidth;
  private double rectHeight;
  private int[][] mapSetup;
  private List<Position> inlets;
  private int[][] mapRotations;

  /** Represents a Position in the grid. */
  private class Position {
    private int horizontalValue;
    private int verticalValue;

    /**
     * Constructor to initialize the Position.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Position(int horizontalValue, int verticalValue) {
      this.horizontalValue = horizontalValue;
      this.verticalValue = verticalValue;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Position Position = (Position) obj;
      return horizontalValue == Position.horizontalValue && verticalValue == Position.verticalValue;
    }

    @Override
    public int hashCode() {
      int result = 17;
      result = 31 * result + horizontalValue;
      result = 31 * result + verticalValue;
      return result;
    }
  }

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
    // Was variable but is now constant
    gridHorizontalSize = 4;
    gridverticalSize = 3;
    gridCellSize = 80;

    rectWidth = gridCellSize / 4;
    rectHeight = gridCellSize / 2 + rectWidth / 2;
  }

  /** Resets the datastructures to default values */
  private void initializeDataStructures() {
    mapRotations = new int[gridHorizontalSize][gridverticalSize];

    inlets = new ArrayList<Position>();
  }

  /** Creates the grid represents by mapsetup and the game difficulty */
  private void createGrid() {
    // initiates a random variable
    Random rand = new Random();
    // randomly creates the grid
    gridAnchor.setLayoutX((800 - (gridHorizontalSize + 1) * gridCellSize) / 2);
    gridAnchor.setLayoutY((500 - (gridverticalSize + 1) * gridCellSize) / 2);
    gridAnchor.setPrefSize(
        (gridHorizontalSize + 1) * gridCellSize, (gridverticalSize + 1) * gridCellSize);
    grid.setLayoutX(gridCellSize / 2);
    grid.setLayoutY(gridCellSize / 2);
    // set the size
    grid.setPrefSize(gridHorizontalSize * gridCellSize, gridverticalSize * gridCellSize);
    for (int i = 0; i < gridHorizontalSize; i++) {
      grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(gridCellSize));
    }
    // loops through the grid
    for (int i = 0; i < gridverticalSize; i++) {
      grid.getRowConstraints().add(new javafx.scene.layout.RowConstraints(gridCellSize));
    }
    // gives the grid a style color
    grid.setStyle("-fx-background-color: #FFFFFF;");

    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridverticalSize; y++) {
        mapRotations[x][y] = rand.nextInt(4);
        Pane pane = createPane(mapSetup[x][y], x, y);
        ;
        grid.add(pane, x, y);
      }
    }
    // initialise the rectangles in the game
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
    mapSetup = new int[gridHorizontalSize][gridverticalSize];

    // Variable to store the starting Position for the grid paths
    Position start = null;

    // List to store inlet Positions around the grid boundary
    inlets = new ArrayList<Position>();

    // Randomly decide the number of inlets, minimum 2 and maximum 5
    int numInlets = rand.nextInt(4) + 2;
    System.out.println(numInlets);

    // Randomly place inlets on the grid boundary, starting from the top-left and moving clockwise
    for (int i = 0; i < numInlets; i++) {
      int position = rand.nextInt(gridHorizontalSize * 2 + gridverticalSize * 2);

      // Place on top edge
      if (position < gridHorizontalSize) {
        mapSetup[position][0] |= 0b1000;
        inlets.add(new Position(position, -1));
        if (start == null) {
          start = new Position(position, 0);
        }

        // Place on right edge
      } else if (position < gridHorizontalSize + gridverticalSize) {
        int x = gridHorizontalSize - 1;
        int y = position - gridHorizontalSize;
        mapSetup[x][y] |= 0b0100;
        inlets.add(new Position(gridHorizontalSize, y));
        if (start == null) {
          start = new Position(x, y);
        }

        // Place on bottom edge
      } else if (position < gridHorizontalSize * 2 + gridverticalSize) {
        int x = gridHorizontalSize - 1 - (position - gridHorizontalSize - gridverticalSize);
        int y = gridverticalSize - 1;
        mapSetup[x][y] |= 0b0010;
        inlets.add(new Position(x, gridverticalSize));
        if (start == null) {
          start = new Position(x, y);
        }

        // Place on left edge
      } else {
        int x = 0;
        int y = gridverticalSize - 1 - (position - gridHorizontalSize * 2 - gridverticalSize);
        mapSetup[x][y] |= 0b0001;
        inlets.add(new Position(-1, y));
        if (start == null) {
          start = new Position(x, y);
        }
      }
    }

    // Set up a list of available squares for path generation
    Set<Position> availableSquares = new HashSet<>();
    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridverticalSize; y++) {
        availableSquares.add(new Position(x, y));
      }
    }

    // Start path generation from the start Position
    List<Position> activePaths = new ArrayList<>();
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

      // Pick a random current Position from the active paths
      Position current = activePaths.get(rand.nextInt(activePaths.size()));

      // List to store neighboring Positions which haven't been visited
      List<Position> unvisitedNeighbors = new ArrayList<>();
      // List to store neighbors with exactly two connections
      List<Position> twoConnectedNeighbors = new ArrayList<>();

      // Identify valid neighbors of the current Position
      for (int[] d : directions) {
        Position neighbor =
            new Position(current.horizontalValue + d[0], current.verticalValue + d[1]);
        if (isWithinGrid(neighbor)) {
          if (availableSquares.contains(neighbor)) {
            unvisitedNeighbors.add(neighbor);
          } else {
            int connections =
                Integer.bitCount(mapSetup[neighbor.horizontalValue][neighbor.verticalValue]);
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

      // If no suitable neighbors are found, remove current Position from active paths
      if (unvisitedNeighbors.isEmpty() && twoConnectedNeighbors.isEmpty()) {
        activePaths.remove(current);
        continue;
      }

      // Choose a neighbor to extend the path to
      Position chosenNeighbor;
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
      if (chosenNeighbor.horizontalValue > current.horizontalValue) {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b0100;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0001;
      } else if (chosenNeighbor.horizontalValue < current.horizontalValue) {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b0001;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0100;
      } else if (chosenNeighbor.verticalValue > current.verticalValue) {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b0010;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b1000;
      } else {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b1000;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0010;
      }
    }

    // After main path generation, ensure all grid cells have at least two connections
    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridverticalSize; y++) {
        Position cell = new Position(x, y);
        if (Integer.bitCount(mapSetup[x][y]) == 1) {
          // Force a second connection for cells with only one connection
          for (int[] d : directions) {
            Position neighbor =
                new Position(cell.horizontalValue + d[0], cell.verticalValue + d[1]);
            if (isWithinGrid(neighbor)
                && Integer.bitCount(mapSetup[neighbor.horizontalValue][neighbor.verticalValue])
                    < 3) {
              if (neighbor.horizontalValue > cell.horizontalValue) {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b0100;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0001;
              } else if (neighbor.horizontalValue < cell.horizontalValue) {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b0001;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0100;
              } else if (neighbor.verticalValue > cell.verticalValue) {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b0010;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b1000;
              } else {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b1000;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0010;
              }
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Checks if a Position is within the grid.
   *
   * @param p Position to check
   * @return true if within grid, false otherwise
   */
  private boolean isWithinGrid(Position p) {
    return p.horizontalValue >= 0
        && p.horizontalValue < gridHorizontalSize
        && p.verticalValue >= 0
        && p.verticalValue < gridverticalSize;
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
    pane.setPrefSize(gridCellSize, gridCellSize);
    pane.setOnMouseClicked(this::handlePaneClick);
    pane.setRotate(mapRotations[x][y] * 90);

    double offsetIncrement = (gridCellSize - rectWidth) / 2;
    var children = pane.getChildren();
    // Add pipes
    if ((stucture & 0b1000) != 0) {
      // top
      Rectangle rect = new Rectangle(offsetIncrement, 0, rectWidth, rectHeight);
      rect.setStrokeWidth(0);
      children.add(rect);
    }
    if ((stucture & 0b0100) != 0) {
      // right
      Rectangle rect = new Rectangle(offsetIncrement, offsetIncrement, rectHeight, rectWidth);
      rect.setStrokeWidth(0);
      children.add(rect);
    }
    if ((stucture & 0b0010) != 0) {
      // bottom
      Rectangle rect = new Rectangle(offsetIncrement, offsetIncrement, rectWidth, rectHeight);
      rect.setStrokeWidth(0);
      children.add(rect);
    }
    if ((stucture & 0b0001) != 0) {
      // left
      Rectangle rect = new Rectangle(0, offsetIncrement, rectHeight, rectWidth);
      rect.setStrokeWidth(0);
      children.add(rect);
    }

    return pane;
  }

  /** Creates rectangles to represent inlets in the grid. */
  private void createInletRectangles() {
    for (Position inlet : inlets) {
      // Determine the position of the inlet
      int x = inlet.horizontalValue;
      int y = inlet.verticalValue;

      double layoutX, layoutY;
      double inletHeight = rectWidth * 1.5;
      boolean horizontal = false;
      if (y == -1) { // Top side
        layoutX = (x + 1) * gridCellSize - 0.5 * rectWidth;
        layoutY = gridCellSize / 2 - inletHeight;
      } else if (x == gridHorizontalSize) { // Right side
        layoutX = (gridHorizontalSize + 0.5) * gridCellSize;
        layoutY = (y + 1) * gridCellSize - 0.5 * rectWidth;
        horizontal = true;
      } else if (y == gridverticalSize) { // Bottom side
        layoutX = (x + 1) * gridCellSize - 0.5 * rectWidth;
        layoutY = (gridverticalSize + 0.5) * gridCellSize;
      } else { // Left side
        layoutX = gridCellSize / 2 - inletHeight;
        layoutY = (y + 1) * gridCellSize - 0.5 * rectWidth;
        horizontal = true;
      }

      // Create the rectangle for the inlet
      double width, height;
      if (horizontal) {
        width = inletHeight;
        height = rectWidth;
      } else {
        width = rectWidth;
        height = inletHeight;
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
   * @param cell The Position representing the current cell's coordinates.
   * @param direction The binary representation of the direction to check.
   * @return True if the cells are connected, otherwise false.
   */
  private boolean areAdjacentCellsConnected(Position cell, int direction) {
    int oppositeDirectionMask;
    Position adjacentCell;

    switch (direction) {
      case 0b1000:
        // Top code so it does the logic
        oppositeDirectionMask = 0b0010;
        adjacentCell = new Position(cell.horizontalValue, cell.verticalValue - 1);
        break;
      case 0b0100:
        // Right code so it does the logic
        oppositeDirectionMask = 0b0001;
        adjacentCell = new Position(cell.horizontalValue + 1, cell.verticalValue);
        break;
      case 0b0010:
        // Bottom code so it does the logic
        oppositeDirectionMask = 0b1000;
        adjacentCell = new Position(cell.horizontalValue, cell.verticalValue + 1);
        break;
      default:
        // Left code so it does the logiccode so it does the logic
        oppositeDirectionMask = 0b0100;
        adjacentCell = new Position(cell.horizontalValue - 1, cell.verticalValue);
    }

    if (!isWithinGrid(adjacentCell)) {
      // The adjacent cell is outside the grid, so the current cell should be an inlet
      boolean isValidInlet = inlets.contains(adjacentCell);
      if (!isValidInlet) {
        System.out.println(
            "An inlet at position "
                + adjacentCell.horizontalValue
                + ", "
                + adjacentCell.verticalValue
                + " does not exist!");
      }
      return isValidInlet;
    }

    return (mapSetup[adjacentCell.horizontalValue][adjacentCell.verticalValue]
            & oppositeDirectionMask)
        != 0;
  }

  /**
   * Checks the entire map's grid for connectivity. If any issues with the map's connectivity are
   * found, they will be printed to the console.
   */
  public void checkCompleteness() {
    // loops through and check if completed
    for (int i = 0; i < gridHorizontalSize; i++) {
      for (int j = 0; j < gridverticalSize; j++) {
        Position currentCell = new Position(i, j);
        int cellData = rotateCellData(mapSetup[i][j], mapRotations[i][j]);
        // if all these are true then compelted
        if ((cellData & 0b1000) != 0 && !areAdjacentCellsConnected(currentCell, 0b1000)) return;
        if ((cellData & 0b0100) != 0 && !areAdjacentCellsConnected(currentCell, 0b0100)) return;
        if ((cellData & 0b0010) != 0 && !areAdjacentCellsConnected(currentCell, 0b0010)) return;
        if ((cellData & 0b0001) != 0 && !areAdjacentCellsConnected(currentCell, 0b0001)) return;
      }
    }
    // check the inlet position if its true return
    for (Position inletPosition : inlets) {
      if (!isInletConnected(inletPosition)) return;
    }

    onComplete();
  }

  /**
   * Checks if a specified inlet has any connections.
   *
   * @param inlet The Position representing the inlet's coordinates.
   * @return True if the inlet has a connection, otherwise false.
   */
  private boolean isInletConnected(Position inlet) {
    // Define the adjacent Position and the bitmask for the direction from the cell to the inlet
    Position adjacentPosition = null;
    int mask = 0;

    // Top side
    if (inlet.verticalValue == -1) {
      adjacentPosition = new Position(inlet.horizontalValue, 0);
      mask = 0b1000; // Check for the upward pipe from the adjacent cell
    }
    // Right side
    else if (inlet.horizontalValue == gridHorizontalSize) {
      adjacentPosition = new Position(gridHorizontalSize - 1, inlet.verticalValue);
      mask = 0b0100; // Check for the rightward pipe from the adjacent cell
    }
    // Bottom side
    else if (inlet.verticalValue == gridverticalSize) {
      adjacentPosition = new Position(inlet.horizontalValue, gridverticalSize - 1);
      mask = 0b0010; // Check for the downward pipe from the adjacent cell
    }
    // Left side
    else if (inlet.horizontalValue == -1) {
      adjacentPosition = new Position(0, inlet.verticalValue);
      mask = 0b0001; // Check for the leftward pipe from the adjacent cell
    }

    if (adjacentPosition != null) {
      int cellData = mapSetup[adjacentPosition.horizontalValue][adjacentPosition.verticalValue];
      int rotatedCellData =
          rotateCellData(
              cellData,
              mapRotations[adjacentPosition.horizontalValue][adjacentPosition.verticalValue]);

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
    // get the value of the pippuzzle
    if (GameState.puzzleSolved.get(Puzzle.PIPEPUZZLE).getValue()) return;
    Pane pane = (Pane) event.getSource();
    int x = GridPane.getColumnIndex(pane);
    int y = GridPane.getRowIndex(pane);
    // rotates the pipes
    mapRotations[x][y] = (mapRotations[x][y] + 1) % 4;
    pane.setRotate(mapRotations[x][y] * 90);

    checkCompleteness();
  }

  /** Exits the current puzzle and resets the puzzle state to NONE. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  /** Called when the map is found to be complete. Prints a completion message to the console. */
  private void onComplete() {
    GameState.puzzleSolved.get(Puzzle.PIPEPUZZLE).set(true);
    System.out.println("Complete");
    GameState.pipePuzzleSolved = true;
    exitPuzzle();
  }
}
