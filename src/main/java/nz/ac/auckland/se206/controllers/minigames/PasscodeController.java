package nz.ac.auckland.se206.controllers.minigames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.Puzzle;

public class PasscodeController {

  /** Controller for the Pipe Connecting Mini-game. */
  @FXML private AnchorPane gridAnchor;

  @FXML private Pane horozontalBox;
  private String answer;
  private List<List<String>> numberOptions = new ArrayList<List<String>>();
  private List<StringProperty> selectedNumbers = new ArrayList<StringProperty>();

  private final String[] numbers = {"9", "8", "7", "6", "5", "4", "3", "2", "1", "0"};

  /** Initializes the grid based on the game's difficulty. */
  @FXML
  public void initialize() throws IOException {
    Random rand = new Random();
    answer = GameState.passcodeAnswer;

    // Loop through each number in the answer
    for (int i = 0; i < answer.length(); i++) {
      Pane newNumberField = (Pane) App.loadFxml("padlockselector");
      List<String> individualNumberOptions = new ArrayList<String>();
      int numOptions = 10;

      // Generate random number options
      for (int n = 0; n < numOptions; n++) {
        String nextNumber = numbers[n];

        individualNumberOptions.add(nextNumber);
      }

      // Set one of the options to be the correct number

      StringProperty selectedNumber =
          new SimpleStringProperty(
              individualNumberOptions.get(rand.nextInt(individualNumberOptions.size())));
      ((Label) newNumberField.lookup("#label")).textProperty().bind(selectedNumber);
      Node upArrow = newNumberField.lookup("#upArrow");

      // Handle up arrow click
      upArrow.setOnMouseClicked(
          e -> {
            if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) return;

            int index = individualNumberOptions.indexOf(selectedNumber.getValue());
            if (index == 0) {
              selectedNumber.setValue(
                  individualNumberOptions.get(individualNumberOptions.size() - 1));
            } else {
              selectedNumber.setValue(individualNumberOptions.get(index - 1));
            }
          });
      upArrow.setOnMouseEntered(this::onMouseEntered);
      upArrow.setOnMouseExited(this::onMouseExited);
      Node downArrow = newNumberField.lookup("#downArrow");

      // Handle down arrow click
      downArrow.setOnMouseClicked(
          e -> {
            if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) return;

            int index = individualNumberOptions.indexOf(selectedNumber.getValue());
            if (index == individualNumberOptions.size() - 1) {
              selectedNumber.setValue(individualNumberOptions.get(0));
            } else {
              selectedNumber.setValue(individualNumberOptions.get(index + 1));
            }
          });
      downArrow.setOnMouseEntered(this::onMouseEntered);
      downArrow.setOnMouseExited(this::onMouseExited);
      selectedNumber.addListener(this::onNumberChange);
      selectedNumbers.add(selectedNumber);
      numberOptions.add(individualNumberOptions);
      horozontalBox.getChildren().add(newNumberField);
    }
    System.out.println(answer);
  }

  /** Exits the current puzzle and resets the puzzle state to NONE. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  /**
   * Listens for changes in the selected numbers and checks if the current selection forms the
   * correct answer.
   *
   * @param o The observable value being watched, representing a selected number.
   * @param oldVal The old value of the observed number.
   * @param newVal The new value of the observed number.
   */
  private void onNumberChange(ObservableValue<? extends String> o, String oldVal, String newVal) {
    StringBuilder currentAnswerBuilder = new StringBuilder();
    for (StringProperty number : selectedNumbers) {
      currentAnswerBuilder.append(number.getValue());
    }
    String currentAnswer = currentAnswerBuilder.toString();
    if (answer.equals(currentAnswer)) {
      onComplete();
    }
  }

  /**
   * Turn button blue to show it is clickable
   *
   * @param event MouseEvent for turning object blue or showing clickable
   */
  private void onMouseEntered(MouseEvent event) {
    if (GameState.puzzleSolved.get(Puzzle.PASSCODE).getValue()) return;

    Node source = (Polygon) event.getSource();
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setHue(1); // Max hue
    colorAdjust.setSaturation(1); // Max saturation
    source.setEffect(colorAdjust);
  }

  /**
   * Removes blue tint from object
   *
   * @param event MouseEvent for turning object blue
   */
  private void onMouseExited(MouseEvent event) {
    Polygon source = (Polygon) event.getSource();
    source.setEffect(null);
  }

  /** Called when the map is found to be complete. Prints a completion message to the console. */
  private void onComplete() {
    GameState.puzzleSolved.get(Puzzle.PASSCODE).set(true);
    System.out.println("Complete");
    exitPuzzle();
  }
}
