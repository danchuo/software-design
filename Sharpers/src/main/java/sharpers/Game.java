package sharpers;

import java.util.ArrayList;

public class Game {

  public static final int GAME_TIME = 10000;

  public static void main(String[] args) throws InterruptedException {
    var deck = new Deck();
    var playersClub = new ArrayList<Player>();
    for (int i = 0; i < 10; ++i) {
      playersClub.add(new Player(Integer.toString(i), deck));
    }

    for (var player : playersClub) {
      player.play();
    }
    Thread.sleep(GAME_TIME);

    for (var player : playersClub) {
      player.interrupt();
    }

    for (var player : playersClub) {
      player.waitToFinish();
    }

    for (var player : playersClub) {
      System.out.print(player.getGameResult());
    }
  }
}
