package nz.ac.auckland.se206.gpt.openai;


import de.saxsys.javafx.test.JfxRunner;
import org.junit.runner.RunWith;

@RunWith(JfxRunner.class)
public class GameMasterTest {

  // Doesn't seem to work outside of javafx threads. Seems like a task related issue
  //   @Test
  //   public void testGameMaster() throws InterruptedException {
  //     GameMaster gameMaster =
  //         new GameMaster().setN(1).setTemperature(1.5).setTopP(0.05).setMaxTokens(100);

  //     gameMaster
  //         .createChatContext("context1")
  //         .addMessage("system", "You are a helpful assistant. Reply in less than 20 words.")
  //         .addMessage("user", "Where is New Zealand?")
  //         .addMessage("user", "New Zealand is a country located in the southwestern Pacific
  // Ocean.")
  //         .addMessage("user", "What's the largest city there?");

  //     gameMaster
  //         .createChatContext("context2")
  //         .addMessage("system", "You are a helpful assistant. Reply in less than 20 words.")
  //         .addMessage("user", "Where is New Zealand?")
  //         .addMessage("user", "New Zealand is a country located in the southwestern Pacific
  // Ocean.")
  //         .addMessage("user", "What's the capital city there?");

  //     gameMaster.runContext("context1");
  //     gameMaster.runContext("context2");

  //     assertTrue(
  //         gameMaster.isBusy("context1") && gameMaster.isBusy("context2"),
  //         "Gamemaster not running both contexts at once");

  //     // Using Awaitility to wait until the contexts are no longer busy.
  //     gameMaster.waitForContext("context1");
  //     gameMaster.waitForContext("context2");

  //     assertFalse(gameMaster.isBusy("context1"), "Context 1 still busy");
  //     assertFalse(gameMaster.isBusy("context2"), "Context 2 still busy");

  //     assertTrue(gameMaster.getLastResponse("context1").getContent().contains("Auckland"));
  //     assertTrue(gameMaster.getLastResponse("context2").getContent().contains("Wellington"));
  //   }
}
