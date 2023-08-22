package nz.ac.auckland.se206.gpt.openai;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
 public class ResponseChatCompletion {

  public final Boolean success;
  public final Integer code;
  public final String message;
  public final Map<String, Object> chatCompletion;

  public ResponseChatCompletion(
      @JsonProperty("success") Boolean success,
      @JsonProperty("code") Integer code,
      @JsonProperty("message") String message,
      @JsonProperty("chat_completion") Map<String, Object> chatCompletion) {
    this.success = success;
    this.code = code;
    this.message = message;
    this.chatCompletion = chatCompletion;
  }
}
