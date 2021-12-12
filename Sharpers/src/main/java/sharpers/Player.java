package sharpers;

import java.security.SecureRandom;
import java.text.MessageFormat;

public class Player {

  public static final int MAX_SLEEP_TIME = 200;
  public static final int MIN_SLEEP_TIME = 100;

  private final Deck deck;
  private final Thread playersThread;
  private final SecureRandom random;

  private int balance;
  private String gameResult;

  Player(String name, Deck deck) {
    this.deck = deck;
    random = new SecureRandom();
    balance = 0;
    gameResult =
        MessageFormat.format(
            "The game has not started yet or {0} {1} has not finished playing.\n",
            getClass().getSimpleName(), name);
    playersThread = new Thread(this::playGameUtilInterrupt, name);
  }

  public synchronized int getBalance() {
    return balance;
  }

  public synchronized void increaseBalance(int by) {
    balance += by;
  }

  private void playGameUtilInterrupt() {
    try {
      while (true) {
        makeMove();
      }
    } catch (InterruptedException ignored) {
      gameResult =
          MessageFormat.format(
              "{0} {1} left the game with a balance of {2} point.\n",
              getClass().getSimpleName(), playersThread.getName(), getBalance());
    }
  }

  public void makeMove() throws InterruptedException {
    increaseBalance(deck.getCart());
    Thread.sleep(random.nextInt(MIN_SLEEP_TIME, MAX_SLEEP_TIME + 1));
  }

  public synchronized int tryDecreaseBalance(int by) {
    int decreasingResult;
    if (balance >= by) {
      balance -= by;
      decreasingResult = by;
    } else {
      decreasingResult = balance;
      balance = 0;
    }
    return decreasingResult;
  }

  public String getGameResult() {
    return gameResult;
  }

  public void startPlay() {
    playersThread.start();
  }

  public void interrupt() {
    playersThread.interrupt();
  }

  public void waitToFinish() {
    try {
      playersThread.join();
    } catch (InterruptedException ignored) {
    }
  }

  public SecureRandom getRandom() {
    return random;
  }

  public String getName() {
    return playersThread.getName();
  }
}
