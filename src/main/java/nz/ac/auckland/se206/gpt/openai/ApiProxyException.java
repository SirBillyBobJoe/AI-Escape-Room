package nz.ac.auckland.se206.gpt.openai;

/** Exception class for API proxy related errors. */
public class ApiProxyException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new ApiProxyException with the specified detail message.
   *
   * @param message the detail message
   */
  public ApiProxyException(String message) {
    super(message);
  }

  /**
   * Constructs a new ApiProxyException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public ApiProxyException(String message, Throwable cause) {
    super(message, cause);
  }
}
