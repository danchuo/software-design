package sharpers;

import java.security.SecureRandom;

public class Deck {

  public static final int MAX_CARD_VALUE = 10;
  public static final int MIN_CARD_VALUE = 1;

  private final SecureRandom random;

  Deck() {
    random = new SecureRandom();
  }

  public synchronized int getCart() {
    return random.nextInt(MIN_CARD_VALUE, MAX_CARD_VALUE + 1);
  }
}
