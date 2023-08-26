package nz.ac.auckland.se206.speech;

import nz.ac.auckland.se206.speech.TextToSpeech.TextToSpeechException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextToSpeechTest {
  @BeforeEach
  void setUp() throws TextToSpeechException {
    TextToSpeech.clearInstance();
  }

  @Test
  void textToSpeech() {
    TextToSpeech.getInstance().speak("hello");
  }

  @Test
  void multipleTextToSpeech() {
    TextToSpeech.getInstance().speak("hello", "how are you?");
    TextToSpeech.getInstance().speak("today is a great day");
  }
}
