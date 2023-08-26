package nz.ac.auckland.se206.gpt;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a master controller for creating, managing, and interacting with multiple chat
 * contexts in a game.
 */
public class GameMaster {

  // API specific values that affect the behavior of the chat
  private int nApiValue = 1;
  private double temperatureApiValue = 0.5;
  private double topPApiValue = 0.5;
  private int maxTokensApiValue = 100;

  // Maps a name of a context to its ChatContext instance
  private Map<String, ChatContext> chatContexts = new HashMap<String, ChatContext>();

  /** Default constructor, initializes with default API values. */
  public GameMaster() {}

  /**
   * Constructor allowing custom initialization of API values.
   *
   * @param nApiValue Value affecting the chat.
   * @param temperatureApiValue Value affecting the chat.
   * @param topPApiValue Value affecting the chat.
   * @param maxTokensApiValue Maximum tokens allowed.
   */
  public GameMaster(
      int nApiValue, double temperatureApiValue, double topPApiValue, int maxTokensApiValue) {
    this.nApiValue = nApiValue;
    this.temperatureApiValue = temperatureApiValue;
    this.topPApiValue = topPApiValue;
    this.maxTokensApiValue = maxTokensApiValue;
  }

  // Below are setter methods with fluent API style returning 'this' for chaining

  public GameMaster setN(int n) {
    this.nApiValue = n;
    return this;
  }

  public GameMaster setTemperature(double temperature) {
    this.temperatureApiValue = temperature;
    return this;
  }

  public GameMaster setTopP(double topP) {
    this.topPApiValue = topP;
    return this;
  }

  public GameMaster setMaxTokens(int maxTokens) {
    this.maxTokensApiValue = maxTokens;
    return this;
  }

  /**
   * Creates a new chat context with the provided context name.
   *
   * @param contextName Unique name for the chat context.
   * @return The created ChatContext.
   */
  public ChatContext createChatContext(String contextName) {
    ChatContext chatContext =
        new ChatContext(
            contextName, nApiValue, temperatureApiValue, topPApiValue, maxTokensApiValue);
    chatContexts.put(contextName, chatContext);
    return chatContext;
  }

  /**
   * Retrieves a ChatContext by its name.
   *
   * @param contextName Name of the chat context to retrieve.
   * @return The ChatContext associated with the provided name.
   */
  public ChatContext getChatContext(String contextName) {
    return chatContexts.get(contextName);
  }

  /**
   * Removes a ChatContext by its name.
   *
   * @param contextName Name of the chat context to remove.
   */
  public void clearChatContext(String contextName) {
    chatContexts.remove(contextName);
  }

  /**
   * Adds a message to a ChatContext using a provided role and content.
   *
   * @param contextName Context to which the message will be added.
   * @param role Role of the entity sending the message.
   * @param content Content of the message.
   * @return The ChatContext to which the message was added.
   */
  public ChatContext addMessage(String contextName, String role, String content) {
    return addMessage(contextName, new ChatMessage(role, content));
  }

  /**
   * Adds a message to a ChatContext.
   *
   * @param contextName Context to which the message will be added.
   * @param message Message to be added.
   * @return The ChatContext to which the message was added.
   */
  public ChatContext addMessage(String contextName, ChatMessage message) {
    return chatContexts.get(contextName).addMessage(message);
  }

  /**
   * Initiates a GPT run for a ChatContext.
   *
   * @param contextName Name of the chat context to run.
   */
  public void runContext(String contextName) {
    chatContexts.get(contextName).asyncRunGpt();
  }

  /**
   * Retrieves the last response from a ChatContext.
   *
   * @param contextName Context from which to retrieve the last response.
   * @return The last ChatMessage response.
   */
  public ChatMessage getLastResponse(String contextName) {
    return chatContexts.get(contextName).getLastResponse();
  }

  /**
   * Waits for a ChatContext's GPT run to complete.
   *
   * @param contextName Name of the chat context to wait for.
   */
  public void waitForContext(String contextName) {
    try {
      chatContexts.get(contextName).waitTillComplete();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks if a ChatContext is busy.
   *
   * @param contextName Name of the chat context to check.
   * @return true if the context is busy, false otherwise.
   */
  public boolean isBusy(String contextName) {
    return chatContexts.get(contextName).isBusy();
  }
}
