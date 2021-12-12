package sharpers;

import java.util.ArrayList;
import java.util.List;

public class Sharper extends Player {

  public static final int MAX_SLEEP_TIME = 300;
  public static final int MIN_SLEEP_TIME = 180;
  public static final int MAX_CARD_VALUE = 8;

  private final List<Player> players;

  Sharper(List<Player> players, String name, Deck deck) {
    super(name, deck);
    this.players = new ArrayList<>(players);
  }

  @Override
  public void makeMove() throws InterruptedException {
    if (getRandom().nextInt(0, 10) < 4) {
      var randomPlayer = players.get(getRandom().nextInt(0, players.size()));
      var numberOfPoint = getRandom().nextInt(0, MAX_CARD_VALUE + 1);
      increaseBalance(randomPlayer.tryDecreaseBalance(numberOfPoint));
      Thread.sleep(getRandom().nextInt(MIN_SLEEP_TIME, MAX_SLEEP_TIME + 1));
    } else {
      super.makeMove();
    }
  }
}
