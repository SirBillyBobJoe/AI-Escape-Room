package nz.ac.auckland.se206.gpt;

import javafx.concurrent.Task;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/**
 * Represents a context for chat communication using the GPT model. Provides both synchronous and
 * asynchronous methods to interact with GPT and retrieve messages. The class is considered busy
 * when a chat is in progress.
 */
public class ChatContext {
  private String contextName;
  private boolean isBusy = false;
  private boolean didFail = false;
  private ChatCompletionRequest chatCompletionRequest;
  private ChatMessage lastResponse = null;
  private Thread runApiThread = null;

  /**
   * Constructs a ChatContext with the specified parameters.
   *
   * @param contextName name to identify this chat context
   * @param n number of responses desired from the model
   * @param temperature controls randomness in the model's responses
   * @param topP controls diversity of the model's responses
   * @param maxTokens the maximum length of the response in terms of tokens
   */
  public ChatContext(String contextName, int n, double temperature, double topP, int maxTokens) {
    this.contextName = contextName;
    chatCompletionRequest =
        new ChatCompletionRequest()
            .setN(n)
            .setTemperature(temperature)
            .setTopP(topP)
            .setMaxTokens(maxTokens);
  }

  /**
   * Adds a message to the chat context.
   *
   * @param message the chat message
   * @return the current ChatContext instance for chaining
   */
  public ChatContext addMessage(ChatMessage message) {
    chatCompletionRequest.addMessage(message);
    return this;
  }

  /**
   * Adds a message to the chat context using role and content strings.
   *
   * @param role role associated with the message (e.g., "user", "system")
   * @param content content of the message
   * @return the current ChatContext instance for chaining
   */
  public ChatContext addMessage(String role, String content) {
    chatCompletionRequest.addMessage(new ChatMessage(role, content));
    return this;
  }

  /**
   * Executes the GPT model synchronously and retrieves a chat message.
   *
   * @return the response chat message, or null if there's an exception
   * @throws ApiProxyException if there's an error communicating with the API proxy
   */
  private ChatMessage runGpt() throws ApiProxyException {
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      // Assuming we only ever want 1 response
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Asynchronously runs the GPT model, retrieving a response and passing it to the text-to-speech
   * engine.
   */
  public void asyncRunGpt() {
    isBusy = true;
    Task<Void> runApiTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            lastResponse = runGpt();

            isBusy = false;

            // Calculate the first sentence which is what word-to-speech will read out
            /*  String firstSentence =
                lastResponse.getContent().substring(0, lastResponse.getContent().indexOf(".") + 1);
            TextToSpeech.getInstance().speak(firstSentence);*/
            return null;
          }
        };

    runApiTask.setOnFailed(
        (e) -> {
          System.err.println("Failed to run GPT in context " + contextName);
          System.err.println(e);
          isBusy = false;
          didFail = true;
        });

    runApiThread = new Thread(runApiTask);
    runApiThread.start();
  }

  /**
   * Waits until the asynchronous chat task completes.
   *
   * @throws InterruptedException if the waiting thread is interrupted
   */
  public void waitTillComplete() throws InterruptedException {
    runApiThread.join(); // Wait for the thread to finish
  }

  /**
   * Checks if the chat context is currently busy processing a request.
   *
   * @return true if busy, false otherwise
   */
  public boolean isBusy() {
    return isBusy;
  }

  /**
   * Checks if the last chat interaction failed.
   *
   * @return true if there was a failure, false otherwise
   */
  public boolean didFail() {
    return didFail;
  }

  /**
   * Retrieves the last response from the GPT model.
   *
   * @return the last chat message, or null if there was no response
   */
  public ChatMessage getLastResponse() {
    return lastResponse;
  }
}
