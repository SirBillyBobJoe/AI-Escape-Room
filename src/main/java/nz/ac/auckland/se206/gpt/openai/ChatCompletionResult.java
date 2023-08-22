package nz.ac.auckland.se206.gpt.openai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nz.ac.auckland.se206.gpt.ChatMessage;

/** Represents the result of a chat completion request. */
public class ChatCompletionResult {

  /** Represents a choice in the chat completion result. */
  public class Choice {

    private ChatMessage message;
    private int index;
    private String finishReason;

    /**
     * Constructs a new Choice object with the specified parameters.
     *
     * @param message the chat message
     * @param index the index of the choice
     * @param finishReason the reason for finishing the choice
     */
    protected Choice(ChatMessage message, int index, String finishReason) {
      this.message = message;
      this.index = index;
      this.finishReason = finishReason;
    }

    /**
     * Returns the chat message of the choice.
     *
     * @return the chat message
     */
    public ChatMessage getChatMessage() {
      return message;
    }

    /**
     * Returns the reason for finishing the choice.
     *
     * @return the finish reason
     */
    public String getFinishReason() {
      return finishReason;
    }

    /**
     * Returns the index of the choice.
     *
     * @return the index
     */
    public int getIndex() {
      return index;
    }
  }

  private String model;
  private long created;

  private int usagePromptToken;
  private int usageCompletionTokens;
  private int usageTotalTokens;

  private List<Choice> choices;

  /**
   * Constructs a new ChatCompletionResult object with the provided chat completion data.
   *
   * @param chatCompletion the chat completion data
   */
  protected ChatCompletionResult(Map<String, Object> chatCompletion) {
    choices = new ArrayList<>();
    parse(chatCompletion);
  }

  private void parse(Map<String, Object> chatCompletion) {
    model = chatCompletion.get("model").toString();
    created =
        chatCompletion.get("created") == null
            ? 0
            : Long.parseLong(chatCompletion.get("created").toString());
    usagePromptToken = getUsage("prompt_tokens", chatCompletion);
    usageCompletionTokens = getUsage("completion_tokens", chatCompletion);
    usageTotalTokens = getUsage("total_tokens", chatCompletion);

    List<?> choicesJson = (List<?>) chatCompletion.get("choices");
    for (int c = 0; c < choicesJson.size(); c++) {
      Map<?, ?> choiceJson = (Map<?, ?>) choicesJson.get(c);
      Map<?, ?> messageJson = (Map<?, ?>) choiceJson.get("message");

      String role = messageJson.get("role").toString();
      String content = messageJson.get("content").toString();

      int index = Integer.parseInt(choiceJson.get("index").toString());
      String finishReason = choiceJson.get("finish_reason").toString();

      choices.add(new Choice(new ChatMessage(role, content), index, finishReason));
    }
  }

  /**
   * Returns the number of prompt tokens used.
   *
   * @return the number of prompt tokens
   */
  public int getUsagePromptTokens() {
    return usagePromptToken;
  }

  /**
   * Returns the number of completion tokens used.
   *
   * @return the number of completion tokens
   */
  public int getUsageCompletionTokens() {
    return usageCompletionTokens;
  }

  /**
   * Returns the total number of tokens used.
   *
   * @return the total number of tokens
   */
  public int getUsageTotalTokens() {
    return usageTotalTokens;
  }

  /**
   * Returns the model used for the chat completion.
   *
   * @return the model name
   */
  public String getModel() {
    return model;
  }

  /**
   * Returns the timestamp when the chat completion result was created.
   *
   * @return the creation timestamp
   */
  public long getCreated() {
    return created;
  }

  private int getUsage(String key, Map<String, Object> chatCompletion) {
    Map<?, ?> usage = (Map<?, ?>) chatCompletion.get("usage");
    return Integer.parseInt(usage.get(key).toString());
  }

  /**
   * Returns the choice at the specified index.
   *
   * @param index the index of the choice
   * @return the choice
   * @throws IllegalArgumentException if the index is out of bounds
   */
  public Choice getChoice(int index) {
    if (index < 0 || index >= choices.size()) {
      throw new IllegalArgumentException(
          "Choice index out of bounds. Index: " + index + ", Choices: " + choices.size());
    }
    return choices.get(index);
  }

  /**
   * Returns the number of choices in the chat completion result.
   *
   * @return the number of choices
   */
  public int getNumChoices() {
    return choices.size();
  }

  /**
   * Returns an iterable of all the choices in the chat completion result.
   *
   * @return an iterable of choices
   */
  public Iterable<Choice> getChoices() {
    return choices;
  }
}
