package nz.ac.auckland.se206.gpt.openai;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import org.junit.jupiter.api.Test;

public class ChatCompletionServiceTest {

  @Test
  public void testGptAuckland() {
    OpenAiService openAiService = new OpenAiService("apiproxy.config");

    ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest(openAiService);

    chatCompletionRequest
        .addMessage("system", "You are a helpful assistant. Reply in less than 20 words.")
        .addMessage("user", "Where is New Zealand?")
        .addMessage("system", "New Zealand is a country located in the southwestern Pacific Ocean.")
        .addMessage("user", "What's one city there?");

    chatCompletionRequest.setN(1);
    chatCompletionRequest.setTemperature(1.5);
    chatCompletionRequest.setTopP(0.05);
    chatCompletionRequest.setMaxTokens(100);
    Set<String> results = new HashSet<>();
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();

      System.out.println("model: " + chatCompletionResult.getModel());
      System.out.println("created: " + chatCompletionResult.getCreated());
      System.out.println("usagePromptTokens: " + chatCompletionResult.getUsagePromptTokens());
      System.out.println(
          "usageCompletionTokens: " + chatCompletionResult.getUsageCompletionTokens());
      System.out.println("usageTotalTokens: " + chatCompletionResult.getUsageTotalTokens());
      System.out.println("Number of choices: " + chatCompletionResult.getNumChoices());

      for (Choice choice : chatCompletionResult.getChoices()) {
        int index = choice.getIndex();
        String finishReason = choice.getFinishReason();
        ChatMessage message = choice.getChatMessage();
        String role = message.getRole();
        String content = message.getContent();
        results.add(content);
        System.out.println("Choice #" + index + ":");
        System.out.println("\tfinishReason: " + finishReason);
        System.out.println("\trole: " + role);
        System.out.println("\tcontent: " + content);
      }

    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
    boolean found = false;
    for (String s : results) {
      if (s.contains("Auckland")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }
}
