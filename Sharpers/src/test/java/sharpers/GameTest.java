package sharpers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Test;

class GameTest {

  @Test
  void playGame_CasualGame_CorrectEnding() throws InterruptedException {
    var game = new Game();
    var result = game.playGame(1, 3);
    assertTrue(Objects.nonNull(result));
  }
}
