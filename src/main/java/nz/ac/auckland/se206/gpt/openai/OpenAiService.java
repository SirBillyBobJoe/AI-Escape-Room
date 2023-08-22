package nz.ac.auckland.se206.gpt.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.File;

/** OpenAI services delegated to store the login credentials. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiService {

  /**
   * Utility method to check if a string is empty or null.
   *
   * @param value The string to check.
   * @return True if the string is null or empty, false otherwise.
   */
  private static boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  /**
   * Checks if the provided email and apiKey are valid input.
   *
   * @param email The email to check.
   * @param apiKey The apiKey to check.
   * @throws IllegalArgumentException if the email or apiKey is null or empty.
   */
  private static void checkValidInput(String email, String apiKey) {
    if (isEmpty(email)) {
      throw new IllegalArgumentException("email cannot be null or empty");
    }

    if (isEmpty(apiKey)) {
      throw new IllegalArgumentException("apiKey cannot be null or empty");
    }
  }

  private String email;
  private String apiKey;

  /**
   * Creates an instance of OpenAiService with the provided email and apiKey.
   *
   * @param email The email for the OpenAI service.
   * @param apiKey The API key for the OpenAI service.
   * @throws IllegalArgumentException if the email or apiKey is null or empty.
   */
  public OpenAiService(String email, String apiKey) {
    checkValidInput(email, apiKey);
    this.email = email;
    this.apiKey = apiKey;
  }

  /**
   * Creates an instance of OpenAiService with the email and apiKey read from the specified file.
   *
   * @param fileName The name of the file containing the API proxy configuration.
   */
  public OpenAiService(String fileName) {
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig(new File(fileName));
      checkValidInput(config.getEmail(), config.getApiKey());
      this.email = config.getEmail();
      this.apiKey = config.getApiKey();
    } catch (ApiProxyException e) {
      // TODO handle exception appropriately
      e.printStackTrace();
    }
  }

  /**
   * Returns the API key associated with the OpenAI service.
   *
   * @return The API key.
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * Returns the email associated with the OpenAI service.
   *
   * @return The email.
   */
  public String getEmail() {
    return email;
  }
}
