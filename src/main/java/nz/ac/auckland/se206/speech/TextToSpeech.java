package nz.ac.auckland.se206.speech;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

/** Text-to-speech API using the JavaX speech library. */
public class TextToSpeech {

  /** Singleton instance of the TextToSpeech class. */
  private static TextToSpeech instance = new TextToSpeech();

  private final Synthesizer synthesizer;

  /**
   * Returns the singleton instance of the TextToSpeech class.
   *
   * @return The singleton instance of the TextToSpeech class.
   */
  public static synchronized TextToSpeech getInstance() {
    if (instance == null) instance = new TextToSpeech();
    return instance;
  }

  /** Clears the singleton instance of the TextToSpeech class. */
  public static void clearInstance() {
    instance = null;
  }

  /** Custom unchecked exception for Text-to-speech issues. */
  static class TextToSpeechException extends RuntimeException {
    public TextToSpeechException(final String message) {
      super(message);
    }
  }

  /**
   * Constructs the TextToSpeech object by creating and allocating the speech synthesizer. The
   * default voice is set to English: com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory.
   */
  private TextToSpeech() {
    try {
      System.setProperty(
          "freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
      Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

      synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(java.util.Locale.ENGLISH));

      synthesizer.allocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }

  /**
   * Speaks the given list of sentences.
   *
   * @param sentences A sequence of strings to speak.
   */
  public void speak(final String... sentences) {
    boolean isFirst = true;
    for (final String sentence : sentences) {
      if (!isFirst) {
        // Add a pause between sentences.
        sleep();
      }
      speak(sentence);
      isFirst = false;
    }
  }

  /**
   * Speaks the given sentence.
   *
   * @param sentence A string to speak.
   */
  public void speak(final String sentence) {
    // if (sentence == null) {
    //   throw new IllegalArgumentException("Text cannot be null.");
    // }
    // try {
    //   synthesizer.resume();
    //   synthesizer.speakPlainText(sentence, null);
    //   synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    // } catch (final AudioException | InterruptedException e) {
    //   throw new TextToSpeechException(e.getMessage());
    // }
  }

  /** Sleeps for a short duration to introduce a pause between sentences. */
  private void sleep() {
    try {
      Thread.sleep(100);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Deallocates the speech synthesizer. If you are experiencing an IllegalThreadStateException,
   * avoid using this method and ensure the speak methods are used without premature termination.
   */
  public void terminate() {
    try {
      synthesizer.deallocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }
}
