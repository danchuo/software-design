package sharpers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DeckTest {

  @Test
  void getCart_NewDeck_RightIntervalOfGenerator() {
    var deck = new Deck();
    for (int i = 0; i < 10; ++i) {
      var result = deck.getCart();
      assertTrue(result >= Deck.MIN_CARD_VALUE);
      assertTrue(result <= Deck.MAX_CARD_VALUE);
    }
  }
}
