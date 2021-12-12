package sharpers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SharperTest {

  private static final Deck DECK = new Deck();
  private static final String NAME = "TESTSHARPER";

  List<Player> generatePlayersList() {
    var playersClub = new ArrayList<Player>();
    for (int i = 0; i < 10; ++i) {
      playersClub.add(new Player(Integer.toString(i), DECK));
    }
    return playersClub;
  }

  @Test
  void makeMove_NewPlayer_BalanceProbablyIncreased() {
    var sharper = new Sharper(generatePlayersList(), NAME, DECK);
    for (int i = 0; i < 5; ++i) {
      try {
        sharper.makeMove();
      } catch (InterruptedException ignored) {
      }
    }

    assertTrue(sharper.getBalance() >= 0);
    assertTrue(sharper.getBalance() <= Deck.MAX_CARD_VALUE * 5);
  }
}
