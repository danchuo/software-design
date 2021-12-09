package sharpers;

import java.util.ArrayList;

public class Game {

  public static final int GAME_TIME = 10000;

  public static void main(String... args) throws InterruptedException {
    var deck = new Deck();
    var nameGenerator = new NameGenerator();
    var playersClub = new ArrayList<Player>();
    for (int i = 0; i < 10; ++i) {
      playersClub.add(new Player(nameGenerator.getRandomName(), deck));
    }

    var sharpersClub = new ArrayList<Sharper>();

    for (int i = 0; i < 10; ++i) {
      sharpersClub.add(new Sharper(playersClub, nameGenerator.getRandomName(), deck));
    }

    var random = new ArrayList<>(playersClub);
    random.addAll(sharpersClub);

    for (var player : random) {
      player.startPlay();
    }
    Thread.sleep(GAME_TIME);

    for (var player : random) {
      player.interrupt();
    }

    for (var player : random) {
      player.waitToFinish();
    }

    for (var player : random) {
      System.out.print(player.getGameResult());
    }
  }
}
