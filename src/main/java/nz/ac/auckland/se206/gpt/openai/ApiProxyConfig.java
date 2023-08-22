package nz.ac.auckland.se206.gpt.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;

/** Represents the configuration for the API proxy. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiProxyConfig {

  /**
   * Reads the API proxy configuration from a file.
   *
   * @param file the file to read the configuration from
   * @return the API proxy configuration
   * @throws ApiProxyException if there is an error reading the configuration
   */
  public static ApiProxyConfig readConfig(File file) throws ApiProxyException {
    try {
      ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

      // Read the configuration from the file
      ApiProxyConfig config = objectMapper.readValue(file, ApiProxyConfig.class);

      // Validate the email
      if (isEmpty(config.getEmail())) {
        throw new ApiProxyException(
            "The email in " + file.getAbsolutePath() + " is missing or empty.");
      }

      // Validate the API key
      if (isEmpty(config.getApiKey())) {
        throw new ApiProxyException(
            "The apiKey in " + file.getAbsolutePath() + " is missing or empty.");
      }

      return config;
    } catch (Exception e) {
      String message =
          "Unable to read "
              + file.getAbsolutePath()
              + ". Please check the file exists and is valid.";
      throw new ApiProxyException(message);
    }
  }

  /**
   * Checks if a string value is empty.
   *
   * @param value the string value to check
   * @return true if the string is empty, false otherwise
   */
  private static boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String email;
  private String apiKey;

  private ApiProxyConfig() {}

  private ApiProxyConfig(String email, String apiKey) {
    this.email = email;
    this.apiKey = apiKey;
  }

  /**
   * Returns the email associated with the API proxy.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the API key associated with the API proxy.
   *
   * @return the API key
   */
  public String getApiKey() {
    return apiKey;
  }
}
