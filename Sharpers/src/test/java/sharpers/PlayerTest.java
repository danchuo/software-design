package sharpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private static final Deck DECK = new Deck();
  private static final String NAME = "TESTPLAYER";
  private static final int SUM = 100;
  // [UnitOfWork_StateUnderTest_ExpectedBehavior]

  private Player testPlayer;

  @BeforeEach
  void initTestPlayer() {
    testPlayer = new Player(NAME, DECK);
  }

  @Test
  void getBalance_NewPlayer_ZeroBalance() {
    assertEquals(testPlayer.getBalance(), 0);
  }

  @Test
  void increaseBalance_NewPlayer_BalanceEqualsIncreasing() {
    testPlayer.increaseBalance(SUM);
    assertEquals(testPlayer.getBalance(), SUM);
  }

  @Test
  void tryDecreaseBalance_NotEnoughMoneyForOperation_BalanceNonNegaive() {
    testPlayer.tryDecreaseBalance(SUM);
    assertTrue(testPlayer.getBalance() >= 0);
  }

  @Test
  void tryDecreaseBalance_PLayerWithSUMBalance_BalanceEqualsHalfOfSUM() {
    testPlayer.increaseBalance(SUM);
    testPlayer.tryDecreaseBalance(SUM / 2);
    assertEquals(testPlayer.getBalance(), SUM / 2);
  }

  @Test
  void makeMove_NewPlayer_BalanceIncreasedOnce() {
    try {
      testPlayer.makeMove();
    } catch (InterruptedException ignored) {
    }

    assertTrue(testPlayer.getBalance() >= Deck.MIN_CARD_VALUE);
    assertTrue(testPlayer.getBalance() <= Deck.MAX_CARD_VALUE);
  }

  @Test
  void getGameResult_NewPLayer_GameHasNotStarted() {
    var result =
        "The game has not started yet or "
            + testPlayer.getClass().getSimpleName()
            + " "
            + NAME
            + " has not finished playing.\n";
    assertEquals(testPlayer.getGameResult(), result);
  }

  @Test
  void startPlay_NewPlayerPlayOnceSecond_BalanceIncreased() {
    testPlayer.startPlay();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignored) {
    }

    testPlayer.interrupt();
    testPlayer.waitToFinish();
    assertTrue(testPlayer.getBalance() > 0);
  }

  @Test
  void getRandom_NewPlayer_CorrectRandomObject() {
    assertTrue(Objects.nonNull(testPlayer.getRandom()));
    assertSame(testPlayer.getRandom().getClass(), SecureRandom.class);
  }

  @Test
  void getName_NewPlayer_CorrectName() {
    assertTrue(Objects.nonNull(testPlayer.getName()));
    assertEquals(testPlayer.getName(), NAME);
  }
}
