package sharpers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Game {

  public static final int GAME_TIME_SECONDS = 10;
  public static final int MAX_PLAYERS_NUMBER = 100;
  public static final int MAX_SHARPERS_NUMBER = 100;
  private static final NameGenerator NAME_GENERATOR = new NameGenerator();
  private final Deck deck;

  public Game() {
    deck = new Deck();
  }

  public static void main(String... args) throws InterruptedException {
    var game = new Game();
    var parser = new Parser();
    int numberOfPLayers =
        parser.tryGetArgumentFromArgs(args, 0, "number of players", 1, MAX_PLAYERS_NUMBER);
    int numberOfSharpers =
        parser.tryGetArgumentFromArgs(args, 1, "number of sharpers", 0, MAX_SHARPERS_NUMBER);

    var gameLog = game.playGame(numberOfPLayers, numberOfSharpers);
    System.out.println(gameLog);
  }

  public String playGame(int numberOfPlayers, int numberOfSharpers) throws InterruptedException {
    var playersClub = generatePlayers(numberOfPlayers);

    var sharpersClub = generateSharpers(numberOfSharpers, playersClub);

    var allPlayers = new ArrayList<>(playersClub);
    allPlayers.addAll(sharpersClub);

    startGame(allPlayers);

    System.out.println("The game has begun! Please, wait " + GAME_TIME_SECONDS + " seconds.\n");
    Thread.sleep(GAME_TIME_SECONDS * 1000);

    stopGame(allPlayers);

    waitAllPlayers(allPlayers);

    return createResult(allPlayers);
  }

  private static String createResult(List<Player> players) {
    var stringBulider = new StringBuilder(100);
    Player playerWithMaxBalance = findPlayerWithMaxBalance(players);
    for (var player : players) {
      stringBulider.append(player.getGameResult());
    }

    var winners = findAllPlayersWithBalance(players, playerWithMaxBalance.getBalance());

    for (var winner : winners) {
      String gameResult =
          MessageFormat.format(
              "\nCongratulations! {0} {1} has the most points - {2} and wins the game!\n",
              winner.getClass().getSimpleName(), winner.getName(), winner.getBalance());
      stringBulider.append(gameResult);
    }

    return stringBulider.toString();
  }

  private static List<Player> findAllPlayersWithBalance(List<Player> players, int balance) {
    var playersWithMaxBalance = new ArrayList<Player>();
    for (var player : players) {
      if (player.getBalance() == balance) {
        playersWithMaxBalance.add(player);
      }
    }

    return playersWithMaxBalance;
  }

  private static Player findPlayerWithMaxBalance(List<Player> players) {
    Player playerWithMaxBalance = players.get(0);
    for (var player : players) {
      if (player.getBalance() > playerWithMaxBalance.getBalance()) {
        playerWithMaxBalance = player;
      }
    }
    return playerWithMaxBalance;
  }

  private static void startGame(List<Player> players) {
    for (var player : players) {
      player.startPlay();
    }
  }

  private static void stopGame(List<Player> players) {
    for (var player : players) {
      player.interrupt();
    }
  }

  private static void waitAllPlayers(List<Player> players) {
    for (var player : players) {
      player.waitToFinish();
    }
  }

  private List<Sharper> generateSharpers(int numberOfSharpers, List<Player> playersClub) {
    var sharpersClub = new ArrayList<Sharper>();

    for (int i = 0; i < numberOfSharpers; ++i) {
      sharpersClub.add(new Sharper(playersClub, NAME_GENERATOR.getRandomName(), deck));
    }
    return sharpersClub;
  }

  private List<Player> generatePlayers(int numberOfPlayers) {
    var playersClub = new ArrayList<Player>();
    for (int i = 0; i < numberOfPlayers; ++i) {
      playersClub.add(new Player(NAME_GENERATOR.getRandomName(), deck));
    }
    return playersClub;
  }
}
