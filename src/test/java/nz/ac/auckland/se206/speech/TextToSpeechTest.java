package nz.ac.auckland.se206.speech;

import nz.ac.auckland.se206.speech.TextToSpeech.TextToSpeechException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextToSpeechTest {
  private TextToSpeech textToSpeech;

  @BeforeEach
  void setUp() throws TextToSpeechException {
    textToSpeech = new TextToSpeech();
  }

  @Test
  void textToSpeech() {
    textToSpeech.speak("hello");
  }

  @Test
  void multipleTextToSpeech() {
    textToSpeech.speak("hello", "how are you?");
    textToSpeech.speak("today is a great day");
  }
}
