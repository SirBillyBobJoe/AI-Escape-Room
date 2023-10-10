package nz.ac.auckland.se206.controllers.minigames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.MouseClick;
import nz.ac.auckland.se206.SceneManager.Puzzle;

/** Controller for the Pipe Connecting Mini-game. */
public class PipeConnectingController {
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
      // checks if 2 objects are equal
      if (this == obj) {
        return true;
      }
      // returns the classes
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      // get position of object
      Position position = (Position) obj;
      return horizontalValue == position.horizontalValue && verticalValue == position.verticalValue;
    }

    @Override
    public int hashCode() {
      int result = 17;
      result = 31 * result + horizontalValue;
      result = 31 * result + verticalValue;
      return result;
    }
  }

  enum Direction {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT
  }

  @FXML private AnchorPane gridAnchor;

  @FXML private GridPane grid;
  @FXML private GridPane solutionGrid;

  @FXML private Rectangle rectangle;
  @FXML private Label lblClose;

  private int gridHorizontalSize;
  private int gridVerticalSize;
  private double gridCellSize;
  private double rectWidth;
  private double rectHeight;
  private int[][] mapSetup;
  private int[][] mapSolution;
  private List<Position> inlets;
  private List<SimpleBooleanProperty> waterLeaksShowing;
  private int[][] mapRotations;

  /** Initializes the grid based on the game's difficulty. */
  @FXML
  public void initialize() throws IOException {
    // sets the game difficulty
    setGamesDifficulty();

    initializeDataStructures();
    generateMapSetup();
    createGrid();
    // check if its compelete
    checkCompleteness();
    // makes solution invisible
    solutionGrid.setVisible(false);
    rectangle.setVisible(false);
    lblClose.setVisible(false);
  }

  /** Sets the size of the grid based on the game's difficulty. */
  private void setGamesDifficulty() {
    // Was variable but is now constant
    gridHorizontalSize = 4;
    gridVerticalSize = 3;
    gridCellSize = 80;

    rectWidth = gridCellSize / 4;
    rectHeight = gridCellSize / 2 + rectWidth / 2;
  }

  /** Resets the datastructures to default values */
  private void initializeDataStructures() {
    mapRotations = new int[gridHorizontalSize][gridVerticalSize];

    inlets = new ArrayList<Position>();
  }

  /**
   * Creates the grid represents by mapsetup and the game difficulty
   *
   * @throws IOException
   */
  private void createGrid() throws IOException {
    // initiates a random variable
    Random rand = new Random();
    // randomly creates the grid
    gridAnchor.setLayoutX((800 - (gridHorizontalSize + 1) * gridCellSize) / 2);
    gridAnchor.setLayoutY((500 - (gridVerticalSize + 1) * gridCellSize) / 2);
    gridAnchor.setPrefSize(
        (gridHorizontalSize + 1) * gridCellSize, (gridVerticalSize + 1) * gridCellSize);
    grid.setLayoutX(gridCellSize / 2);
    grid.setLayoutY(gridCellSize / 2);
    solutionGrid.setLayoutX(grid.getLayoutX() + gridAnchor.getLayoutX());
    solutionGrid.setLayoutY(grid.getLayoutY() + gridAnchor.getLayoutY());
    // set the size
    grid.setPrefSize(gridHorizontalSize * gridCellSize, gridVerticalSize * gridCellSize);
    solutionGrid.setPrefSize(gridHorizontalSize * gridCellSize, gridVerticalSize * gridCellSize);
    for (int i = 0; i < gridHorizontalSize; i++) {
      grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(gridCellSize));
      solutionGrid
          .getColumnConstraints()
          .add(new javafx.scene.layout.ColumnConstraints(gridCellSize));
    }
    // loops through the grid
    for (int i = 0; i < gridVerticalSize; i++) {
      grid.getRowConstraints().add(new javafx.scene.layout.RowConstraints(gridCellSize));
      solutionGrid.getRowConstraints().add(new javafx.scene.layout.RowConstraints(gridCellSize));
    }
    // gives the grid a style color
    grid.setStyle("-fx-background-color: #140a07;");
    solutionGrid.setStyle("-fx-background-color: #3a1d14;");

    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridVerticalSize; y++) {
        mapRotations[x][y] = rand.nextInt(4);
        Pane pane = createPane(mapSetup[x][y], x, y);
        grid.add(pane, x, y);
        Pane solutionPane = createCorrectPane(mapSolution[x][y]);
        solutionGrid.add(solutionPane, x, y);
      }
    }

    waterLeaksShowing =
        new ArrayList<SimpleBooleanProperty>(gridHorizontalSize * gridVerticalSize * 4);
    createWaterLeaks();

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
    mapSetup = new int[gridHorizontalSize][gridVerticalSize];
    mapSolution = new int[gridHorizontalSize][gridVerticalSize];

    // Variable to store the starting Position for the grid paths
    Position start = null;

    // List to store inlet Positions around the grid boundary
    inlets = new ArrayList<Position>();

    // Set to 2 inlet
    int numInlets = 2;

    // Randomly place inlets on the grid boundary, starting from the top-left and moving clockwise
    ArrayList<Integer> inletPositions = new ArrayList<Integer>();
    for (int i = 0; i < numInlets; i++) {
      int position = rand.nextInt(gridHorizontalSize * 2 + gridVerticalSize * 2);
      while (inletPositions.contains(position)) {
        position = rand.nextInt(gridHorizontalSize * 2 + gridVerticalSize * 2);
      }
      inletPositions.add(position);

      // Place on top edge
      if (position < gridHorizontalSize) {
        mapSetup[position][0] |= 0b1000;
        mapSolution[position][0] |= 0b1000;
        inlets.add(new Position(position, -1));
        if (start == null) {
          start = new Position(position, 0);
        }

        // Place on right edge
      } else if (position < gridHorizontalSize + gridVerticalSize) {
        int x = gridHorizontalSize - 1;
        int y = position - gridHorizontalSize;
        mapSetup[x][y] |= 0b0100;
        mapSolution[x][y] |= 0b0100;
        inlets.add(new Position(gridHorizontalSize, y));
        if (start == null) {
          start = new Position(x, y);
        }

        // Place on bottom edge
      } else if (position < gridHorizontalSize * 2 + gridVerticalSize) {
        int x = gridHorizontalSize - 1 - (position - gridHorizontalSize - gridVerticalSize);
        int y = gridVerticalSize - 1;
        mapSetup[x][y] |= 0b0010;
        mapSolution[x][y] |= 0b0010;
        inlets.add(new Position(x, gridVerticalSize));
        if (start == null) {
          start = new Position(x, y);
        }

        // Place on left edge
      } else {
        int x = 0;
        int y = gridVerticalSize - 1 - (position - gridHorizontalSize * 2 - gridVerticalSize);
        mapSetup[x][y] |= 0b0001;
        mapSolution[x][y] |= 0b0001;
        inlets.add(new Position(-1, y));
        if (start == null) {
          start = new Position(x, y);
        }
      }
    }

    // Set up a list of available squares for path generation
    Set<Position> availableSquares = new HashSet<>();
    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridVerticalSize; y++) {
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

        mapSolution[current.horizontalValue][current.verticalValue] |= 0b0100;
        mapSolution[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0001;
      } else if (chosenNeighbor.horizontalValue < current.horizontalValue) {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b0001;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0100;

        mapSolution[current.horizontalValue][current.verticalValue] |= 0b0001;
        mapSolution[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0100;
      } else if (chosenNeighbor.verticalValue > current.verticalValue) {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b0010;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b1000;

        mapSolution[current.horizontalValue][current.verticalValue] |= 0b0010;
        mapSolution[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b1000;
      } else {
        mapSetup[current.horizontalValue][current.verticalValue] |= 0b1000;
        mapSetup[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0010;

        mapSolution[current.horizontalValue][current.verticalValue] |= 0b1000;
        mapSolution[chosenNeighbor.horizontalValue][chosenNeighbor.verticalValue] |= 0b0010;
      }
    }

    // After main path generation, ensure all grid cells have at least two connections
    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridVerticalSize; y++) {
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

                mapSolution[cell.horizontalValue][cell.verticalValue] |= 0b0100;
                mapSolution[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0001;
              } else if (neighbor.horizontalValue < cell.horizontalValue) {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b0001;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0100;

                mapSolution[cell.horizontalValue][cell.verticalValue] |= 0b0001;
                mapSolution[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0100;
              } else if (neighbor.verticalValue > cell.verticalValue) {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b0010;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b1000;

                mapSolution[cell.horizontalValue][cell.verticalValue] |= 0b0010;
                mapSolution[neighbor.horizontalValue][neighbor.verticalValue] |= 0b1000;
              } else {
                mapSetup[cell.horizontalValue][cell.verticalValue] |= 0b1000;
                mapSetup[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0010;

                mapSolution[cell.horizontalValue][cell.verticalValue] |= 0b1000;
                mapSolution[neighbor.horizontalValue][neighbor.verticalValue] |= 0b0010;
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
        && p.verticalValue < gridVerticalSize;
  }

  /**
   * Creates a pane for a grid cell based on its structure.
   *
   * @param structure Configuration of the pipes
   * @param x x-coordinate
   * @param y y-coordinate
   * @return Pane for the grid cell
   */
  private Pane createPane(int stucture, int x, int y) throws IOException {
    Pane pane = new Pane();
    pane.setPrefSize(gridCellSize, gridCellSize);
    pane.setOnMouseClicked(this::handlePaneClick);
    pane.setRotate(mapRotations[x][y] * 90);
    addRectangles(pane, stucture);

    return pane;
  }

  /**
   * Creates the correct pane for a grid cell based on its structure.
   *
   * @param structure Configuration of the pipes
   * @return Pane for the grid cell
   */
  private Pane createCorrectPane(int stucture) throws IOException {
    Pane pane = new Pane();
    pane.setPrefSize(gridCellSize, gridCellSize);
    pane.setRotate(0);
    addRectangles(pane, stucture);

    return pane;
  }

  private void addRectangles(Pane pane, int stucture) {

    double offsetIncrement = (gridCellSize - rectWidth) / 2;
    var children = pane.getChildren();
    // Add pipes
    if ((stucture & 0b1000) != 0) {
      // top
      Rectangle rect = new Rectangle(offsetIncrement, 0, rectWidth, rectHeight);
      rect.setStrokeWidth(0);
      rect.setFill(Color.web("#8a7f80"));
      children.add(rect);
    }
    if ((stucture & 0b0100) != 0) {
      // right
      Rectangle rect = new Rectangle(offsetIncrement, offsetIncrement, rectHeight, rectWidth);
      rect.setStrokeWidth(0);
      rect.setFill(Color.web("#8a7f80"));
      children.add(rect);
    }
    if ((stucture & 0b0010) != 0) {
      // bottom
      Rectangle rect = new Rectangle(offsetIncrement, offsetIncrement, rectWidth, rectHeight);
      rect.setStrokeWidth(0);
      rect.setFill(Color.web("#8a7f80"));
      children.add(rect);
    }
    if ((stucture & 0b0001) != 0) {
      // left
      Rectangle rect = new Rectangle(0, offsetIncrement, rectHeight, rectWidth);
      rect.setStrokeWidth(0);
      rect.setFill(Color.web("#8a7f80"));
      children.add(rect);
    }
  }

  /** Creates rectangles to represent inlets in the grid. */
  private void createInletRectangles() throws IOException {
    for (Position inlet : inlets) {
      // Determine the position of the inlet
      int x = inlet.horizontalValue;
      int y = inlet.verticalValue;

      double layoutX;
      double layoutY;
      double inletHeight = rectWidth * 1.5;
      boolean horizontal = false;
      double waterLeakLayoutX;
      double waterLeakLayoutY;
      double waterLeakRotation;
      if (y == -1) { // Top side
        layoutX = (x + 1) * gridCellSize - 0.5 * rectWidth;
        layoutY = gridCellSize / 2 - inletHeight;

        waterLeakLayoutX = (x + 0.5) * gridCellSize;
        waterLeakLayoutY = -gridCellSize / 2;
        waterLeakRotation = 180;
      } else if (x == gridHorizontalSize) { // Right side
        layoutX = (gridHorizontalSize + 0.5) * gridCellSize;
        layoutY = (y + 1) * gridCellSize - 0.5 * rectWidth;
        horizontal = true;

        waterLeakLayoutX = (gridHorizontalSize + 0.5) * gridCellSize;
        waterLeakLayoutY = (y + 0.5) * gridCellSize;
        waterLeakRotation = 270;
      } else if (y == gridVerticalSize) { // Bottom side
        layoutX = (x + 1) * gridCellSize - 0.5 * rectWidth;
        layoutY = (gridVerticalSize + 0.5) * gridCellSize;

        waterLeakLayoutX = (x + 0.5) * gridCellSize;
        waterLeakLayoutY = layoutY;
        waterLeakRotation = 0;
      } else { // Left side
        layoutX = gridCellSize / 2 - inletHeight;
        layoutY = (y + 1) * gridCellSize - 0.5 * rectWidth;
        horizontal = true;

        waterLeakLayoutX = -gridCellSize / 2;
        waterLeakLayoutY = (y + 0.5) * gridCellSize;
        waterLeakRotation = 90;
      }

      // Create the rectangle for the inlet
      double width;
      double height;
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
      inletRectangle.setFill(Color.web("#8a7f80"));

      gridAnchor.getChildren().add(inletRectangle);

      var waterLeak = createWaterLeak(waterLeakLayoutX, waterLeakLayoutY, waterLeakRotation);
      gridAnchor.getChildren().add(waterLeak);
    }
  }

  /**
   * Creates water leaks on a grid. Loops through each cell in the grid and creates water leaks for
   * each direction.
   *
   * @throws IOException if an I/O error occurs while loading the FXML file.
   */
  private void createWaterLeaks() throws IOException {
    // a continous loop on the grid
    for (int x = 0; x < gridHorizontalSize; x++) {
      for (int y = 0; y < gridVerticalSize; y++) {
        // loops through and for each set the top,left,bottom,right
        gridAnchor.getChildren().add(createGridWaterLeak(x, y, Direction.TOP));
        gridAnchor.getChildren().add(createGridWaterLeak(x, y, Direction.RIGHT));
        gridAnchor.getChildren().add(createGridWaterLeak(x, y, Direction.BOTTOM));
        gridAnchor.getChildren().add(createGridWaterLeak(x, y, Direction.LEFT));
      }
    }
  }

  /**
   * Creates a water leak Pane on the grid at the given coordinates and direction.
   *
   * @param x The x-coordinate on the grid.
   * @param y The y-coordinate on the grid.
   * @param d The direction in which the water leak should be created.
   * @return A Pane object representing the water leak.
   * @throws IOException if an I/O error occurs while loading the FXML file.
   */
  private Pane createGridWaterLeak(int x, int y, Direction d) throws IOException {
    // determines the grid
    double layoutX = x * gridCellSize + 0.5 * gridCellSize;
    double layoutY = y * gridCellSize + 0.5 * gridCellSize;
    double rotate = 0;
    // logic for if its top left right or bottom to rotate
    switch (d) {
      case TOP:
        rotate = 0;
        break;
      case RIGHT:
        rotate = 90;
        break;
      case BOTTOM:
        rotate = 180;
        break;
      case LEFT:
        rotate = 270;
        break;
    }
    return createWaterLeak(layoutX, layoutY, rotate);
  }

  /**
   * Creates a water leak with specific attributes.
   *
   * @param layoutX The x-coordinate for layout placement.
   * @param layoutY The y-coordinate for layout placement.
   * @param rotate The rotation angle.
   * @return A Pane object representing the water leak.
   * @throws IOException if an I/O error occurs while loading the FXML file.
   */
  private Pane createWaterLeak(double layoutX, double layoutY, double rotate) throws IOException {
    // logic for creating the waterleak
    Pane topWaterLeak = (Pane) App.loadFxml("waterleak");
    topWaterLeak.setPrefSize(gridCellSize, gridCellSize);
    topWaterLeak.setLayoutX(layoutX);
    topWaterLeak.setLayoutY(layoutY);
    topWaterLeak.setRotate(rotate);
    // make the leak to visible
    topWaterLeak.setMouseTransparent(true);
    topWaterLeak.setVisible(false);

    var leakShowing = new SimpleBooleanProperty(false);
    // make them fade out rather then immediately dissapear
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), topWaterLeak);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(0.8);

    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), topWaterLeak);
    fadeOut.setToValue(0);
    // set a animation for the images so it seems realistic
    Timeline waterFlow =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(topWaterLeak.opacityProperty(), 0.7)),
            new KeyFrame(Duration.seconds(0.8), new KeyValue(topWaterLeak.opacityProperty(), 1)));
    waterFlow.setAutoReverse(true);
    waterFlow.setCycleCount(Timeline.INDEFINITE);
    // add a listener to check for values if it matches solution then stop leak
    leakShowing.addListener(
        (observable, oldValue, newValue) -> {
          if (oldValue == newValue) {
            return;
          }
          if (newValue) {
            topWaterLeak.opacityProperty().set(0);
            topWaterLeak.setVisible(true);
            fadeIn.setOnFinished(
                e -> {
                  if (!leakShowing.get()) {
                    // If the player presses the button fast
                    return;
                  }
                  waterFlow.play();
                });
            fadeIn.play();
          } else {
            // stop waterfkiw
            waterFlow.stop();
            fadeOut.play();
            fadeOut.setOnFinished(
                e -> {
                  if (leakShowing.get()) {
                    // If the player presses the button fast
                    return;
                  }
                  topWaterLeak.setVisible(false);
                });
          }
        });
    // show leaks
    waterLeaksShowing.add(leakShowing);

    return topWaterLeak;
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
    if (rotation < 0) {
      rotation += 4;
    } // handle negative rotations

    while (rotation-- > 0) {
      boolean rightMostBit = (cellData & 0b0001) != 0;
      cellData = (cellData >> 1) & 0b1111; // right shift and mask to ensure only 4 bits remain
      if (rightMostBit) {
        cellData |= 0b1000;
      } // wrap around
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

    return (rotateCellData(
                mapSetup[adjacentCell.horizontalValue][adjacentCell.verticalValue],
                mapRotations[adjacentCell.horizontalValue][adjacentCell.verticalValue])
            & oppositeDirectionMask)
        != 0;
  }

  /**
   * Checks the entire map's grid for connectivity. If any issues with the map's connectivity are
   * found, they will be printed to the console.
   */
  public void checkCompleteness() {
    // loops through and check if completed
    boolean complete = true;
    boolean[] waterLeakShowing = new boolean[waterLeaksShowing.size()];
    for (int i = 0; i < gridHorizontalSize; i++) {
      for (int j = 0; j < gridVerticalSize; j++) {
        Position currentCell = new Position(i, j);
        int cellData = rotateCellData(mapSetup[i][j], mapRotations[i][j]);
        // if all these are true then compelted
        if ((cellData & 0b1000) != 0 && !areAdjacentCellsConnected(currentCell, 0b1000)) {
          complete = false;
          waterLeakShowing[getWaterLeakIndex(i, j, Direction.TOP)] = true;
        }
        if ((cellData & 0b0100) != 0 && !areAdjacentCellsConnected(currentCell, 0b0100)) {
          complete = false;
          waterLeakShowing[getWaterLeakIndex(i, j, Direction.RIGHT)] = true;
        }
        if ((cellData & 0b0010) != 0 && !areAdjacentCellsConnected(currentCell, 0b0010)) {
          complete = false;
          waterLeakShowing[getWaterLeakIndex(i, j, Direction.BOTTOM)] = true;
        }
        if ((cellData & 0b0001) != 0 && !areAdjacentCellsConnected(currentCell, 0b0001)) {
          complete = false;
          waterLeakShowing[getWaterLeakIndex(i, j, Direction.LEFT)] = true;
        }
      }
    }
    // Check the inlet position if its true return
    for (int i = 0; i < inlets.size(); i++) {
      if (!isInletConnected(inlets.get(i))) {
        complete = false;
        waterLeakShowing[getInletWaterLeakIndex(i)] = true;
      }
    }

    // Update different water leaks
    for (int i = 0; i < waterLeakShowing.length; i++) {
      var booleanProperty = waterLeaksShowing.get(i);
      if (booleanProperty.get() != waterLeakShowing[i]) {
        booleanProperty.set(waterLeakShowing[i]);
      }
    }

    if (!complete) {
      return;
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
    Position adjacentPosition;
    int mask;

    // Top side
    if (inlet.verticalValue == -1) {
      adjacentPosition = new Position(inlet.horizontalValue, 0);
      mask = 0b1000; // Check for the upward pipe from the adjacent cell
    } else if (inlet.horizontalValue == gridHorizontalSize) {
      // Right side
      adjacentPosition = new Position(gridHorizontalSize - 1, inlet.verticalValue);
      mask = 0b0100; // Check for the rightward pipe from the adjacent cell
    } else if (inlet.verticalValue == gridVerticalSize) {
      // Bottom side
      adjacentPosition = new Position(inlet.horizontalValue, gridVerticalSize - 1);
      mask = 0b0010; // Check for the downward pipe from the adjacent cell
    } else if (inlet.horizontalValue == -1) {
      // Left side
      adjacentPosition = new Position(0, inlet.verticalValue);
      mask = 0b0001; // Check for the leftward pipe from the adjacent cell
    } else {
      throw new IllegalArgumentException("Position is not an inlet!");
    }

    int cellData = mapSetup[adjacentPosition.horizontalValue][adjacentPosition.verticalValue];
    int rotatedCellData =
        rotateCellData(
            cellData,
            mapRotations[adjacentPosition.horizontalValue][adjacentPosition.verticalValue]);

    // Check if the adjacent cell has a pipe in the direction of the inlet
    return (rotatedCellData & mask) != 0;
  }

  private int getWaterLeakIndex(int x, int y, Direction d) {
    // gets the index of the leak
    var index = (x * gridVerticalSize + y) * 4;
    // logic for the top, left,right, bottom
    switch (d) {
      case TOP:
        return index;
      case RIGHT:
        return index + 1;
      case BOTTOM:
        return index + 2;
      case LEFT:
        return index + 3;
      default:
        return -1;
    }
  }

  private int getInletWaterLeakIndex(int inletIndex) {
    return gridHorizontalSize * gridVerticalSize * 4 + inletIndex;
  }

  /**
   * Event handler for pane clicks. Rotates the clicked pane and checks if the map is complete after
   * every click.
   *
   * @param event The MouseEvent triggered by the click.
   */
  private void handlePaneClick(MouseEvent event) {
    // get the value of the pippuzzle
    if (GameState.puzzleSolved.get(Puzzle.PIPECONNECTING).getValue()) {
      return;
    }
    new MouseClick().play();
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

  /**
   * Event handler for clue clicks.
   *
   * @param event The MouseEvent triggered by the click.
   */
  @FXML
  private void clickClue(MouseEvent event) {
    // determine logic for clicking on the clue
    new MouseClick().play();
    System.out.println(GameState.pipePuzzleSolved);
    if (GameState.pipePuzzleSolved) {
      return;
    }
    // makes the grid invisible
    if (!solutionGrid.isVisible()) {
      if (!GameState.hints.get().equals(GameState.infinity)
          && Integer.parseInt(GameState.hints.get()) < 1) {
        GameState.gameMasterActions.clear();
        GameState.gameMasterActions.say("You have no hints left.");
        return;
      } else {
        // puts up the new solution of the grid
        solutionGrid.setVisible(true);
        rectangle.setVisible(true);
        lblClose.setVisible(true);
        // if not infinite hints decrease hint count
        if (!GameState.hints.get().equals(GameState.infinity)) {
          GameState.hints.set(Integer.toString(Integer.parseInt(GameState.hints.get()) - 1));
        }
      }
    } else {
      // turns off the visibility of the grid
      solutionGrid.setVisible(false);
      rectangle.setVisible(false);
      lblClose.setVisible(false);
    }
  }

  /** Called when the map is found to be complete. Prints a completion message to the console. */
  private void onComplete() {
    GameState.puzzleSolved.get(Puzzle.PIPECONNECTING).set(true);
    System.out.println("Complete");
    GameState.pipePuzzleSolved = true;
    exitPuzzle();
  }
}
