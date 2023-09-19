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

/** Controller for the Pipe Connecting Mini-game. */
public class PadlockController {
  @FXML private AnchorPane gridAnchor;
  @FXML private Pane hBox;
  private String answer;
  private List<List<String>> letterOptions = new ArrayList<List<String>>();
  private List<StringProperty> selectedLetters = new ArrayList<StringProperty>();

  private final String[] alphabet = {
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
    "T", "U", "V", "W", "X", "Y", "Z"
  };

  /** Initializes the grid based on the game's difficulty. */
  @FXML
  private void initialize() throws IOException {
    Random rand = new Random();
    answer = GameState.padlockAnswer.toUpperCase();

    // Loop through each letter in the answer
    for (int i = 0; i < answer.length(); i++) {
      String correctLetter = answer.substring(i, i + 1);
      Pane newLetterField = (Pane) App.loadFxml("padlockselector");
      List<String> individualLetterOptions = new ArrayList<String>();
      int numOptions = rand.nextInt(4) + 6;

      // Generate random letter options
      for (int n = 0; n < numOptions; n++) {
        String nextLetter = alphabet[rand.nextInt(26)];
        if (individualLetterOptions.contains(nextLetter) || correctLetter.equals(nextLetter)) {
          numOptions++;
        } else {
          individualLetterOptions.add(nextLetter);
        }
      }

      // Set one of the options to be the correct letter
      individualLetterOptions.set(rand.nextInt(individualLetterOptions.size()), correctLetter);
      StringProperty selectedLetter =
          new SimpleStringProperty(
              individualLetterOptions.get(rand.nextInt(individualLetterOptions.size())));
      ((Label) newLetterField.lookup("#label")).textProperty().bind(selectedLetter);
      Node upArrow = newLetterField.lookup("#upArrow");

      // Handle up arrow click
      upArrow.setOnMouseClicked(
          e -> {
            if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) return;

            int index = individualLetterOptions.indexOf(selectedLetter.getValue());
            if (index == 0) {
              selectedLetter.setValue(
                  individualLetterOptions.get(individualLetterOptions.size() - 1));
            } else {
              selectedLetter.setValue(individualLetterOptions.get(index - 1));
            }
          });
      upArrow.setOnMouseEntered(this::onMouseEntered);
      upArrow.setOnMouseExited(this::onMouseExited);
      Node downArrow = newLetterField.lookup("#downArrow");

      // Handle down arrow click
      downArrow.setOnMouseClicked(
          e -> {
            if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) return;

            int index = individualLetterOptions.indexOf(selectedLetter.getValue());
            if (index == individualLetterOptions.size() - 1) {
              selectedLetter.setValue(individualLetterOptions.get(0));
            } else {
              selectedLetter.setValue(individualLetterOptions.get(index + 1));
            }
          });
      downArrow.setOnMouseEntered(this::onMouseEntered);
      downArrow.setOnMouseExited(this::onMouseExited);
      selectedLetter.addListener(this::onLetterChange);
      selectedLetters.add(selectedLetter);
      letterOptions.add(individualLetterOptions);
      hBox.getChildren().add(newLetterField);
    }
    System.out.println(answer);
  }

  /** Exits the current puzzle and returns to the main game. */
  @FXML
  private void exitPuzzle() {
    System.out.println("Exit");
    GameState.currentPuzzle.setValue(Puzzle.NONE);
  }

  /**
   * Listens for changes in the selected letters and checks if the current selection forms the
   * correct answer.
   *
   * @param o The observable value being watched, representing a selected letter.
   * @param oldVal The old value of the observed letter.
   * @param newVal The new value of the observed letter.
   */
  private void onLetterChange(ObservableValue<? extends String> o, String oldVal, String newVal) {
    String currentAnswer = "";
    for (StringProperty letter : selectedLetters) {
      currentAnswer += letter.getValue();
    }
    if (answer.equals(currentAnswer)) {
      onComplete();
    }
  }

  /**
   * Turn button blue to show it is clickable
   *
   * @param event MouseEvent for turning object blue or showing clickable
   */
  @FXML
  private void onMouseEntered(MouseEvent event) {
    if (GameState.puzzleSolved.get(Puzzle.PADLOCK).getValue()) return;

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
  @FXML
  private void onMouseExited(MouseEvent event) {
    Polygon source = (Polygon) event.getSource();
    source.setEffect(null);
  }

  /** Called when the map is found to be complete. Prints a completion message to the console. */
  private void onComplete() {
    GameState.puzzleSolved.get(Puzzle.PADLOCK).set(true);
    GameState.padlockPuzzleSolved = true;
    System.out.println("Complete");
    exitPuzzle();
  }
}
