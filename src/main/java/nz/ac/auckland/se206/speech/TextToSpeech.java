package nz.ac.auckland.se206.speech;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import nz.ac.auckland.se206.GameState;

/** Text-to-speech API using the JavaX speech library. */
public class TextToSpeech {
  /** Custom unchecked exception for Text-to-speech issues. */
  static class TextToSpeechException extends RuntimeException {
    public TextToSpeechException(final String message) {
      super(message);
    }
  }

  /** Singleton instance of the TextToSpeech class. */
  private static TextToSpeech instance = new TextToSpeech();

  /**
   * Returns the singleton instance of the TextToSpeech class.
   *
   * @return The singleton instance of the TextToSpeech class.
   */
  public static synchronized TextToSpeech getInstance() {
    if (instance == null) {
      instance = new TextToSpeech();
    }
    return instance;
  }

  /** Clears the singleton instance of the TextToSpeech class. */
  public static void clearInstance() {
    instance = null;
  }

  private final Synthesizer synthesizer;
  private AtomicBoolean cancelRequested;

  /**
   * Constructs the TextToSpeech object by creating and allocating the speech synthesizer. The
   * default voice is set to English: com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory.
   */
  private TextToSpeech() {
    // try the api call
    try {
      System.setProperty(
          "freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
      Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

      synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(java.util.Locale.ENGLISH));
      cancelRequested = new AtomicBoolean(false);
      synthesizer.allocate();
      // if it dosent work
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
    // cancels if requested
    for (final String sentence : sentences) {
      if (cancelRequested.get()) {
        break;
      }
      // speaks and cancels
      if (isFirst) {
        isFirst = false;
      } else {
        sleep();
      }

      speak(sentence);
    }
  }

  public void cancel() {
    synthesizer.cancel();
    cancelRequested.set(true);
  }

  /**
   * Speaks the given sentence.
   *
   * @param sentence A string to speak.
   */
  public void speak(final String sentence) {
    if (GameState.isGameMuted) {
      return;
    }

    if (sentence == null) {
      throw new IllegalArgumentException("Text cannot be null.");
    }
    try {
      // try if it dosent work
      synthesizer.resume();
      synthesizer.speakPlainText(sentence, null);
      synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
      // plays audio
    } catch (final AudioException | InterruptedException e) {
      throw new TextToSpeechException(e.getMessage());
    }
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
