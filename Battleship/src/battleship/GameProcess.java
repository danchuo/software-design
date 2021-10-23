package battleship;

public class GameProcess {

  private final boolean isRecoveringMode;
  private int numberOfAvailableTorpedoes;
  private final Ocean ocean;
  private final Parser parser;

  GameProcess(String[] args) {
    ocean = Ocean.createOceanWithUserParameters(args);
    parser = new Parser();
    numberOfAvailableTorpedoes = parser.tryGetArgumentFromArgs(args, 7, "number of torpedoes", 0,
        ocean.getTotalNumberOfShips());

    isRecoveringMode =
        parser.tryGetArgumentFromArgs(args, 6,
            "boolean variable \"is recovering mode on\", where \"0\" means false,\"1\" true,", 0,
            1) != 0;
  }

  public static void main(String[] args) {
    var game = new GameProcess(args);
    game.play();
  }

  public void play() {
    parser.makeManyBlankLines();
    Ship previousDamagedShip = null;

    while (ocean.getNumberOfShipsSunk() < ocean.getTotalNumberOfShips()) {
      System.out.println();
      ocean.printMap();
      var rocket = parser.parseRocket(ocean.getRows(), ocean.getColumns(),
          numberOfAvailableTorpedoes);

      if (rocket.isTorpedo()) {
        --numberOfAvailableTorpedoes;
      }

      var resultOfHit = ocean.attackOcean(rocket);
      System.out.println(createReactionToHit(resultOfHit));
      if (isRecoveringMode) {
        if (previousDamagedShip != null && previousDamagedShip != ocean.getLastAttackedShip()
            && !previousDamagedShip.isSunk()) {
          previousDamagedShip.recoverShip();
          System.out.println("The previous damaged ship has been completely restored!");
        }
        previousDamagedShip = ocean.getLastAttackedShip();
      }
    }

    // congratulations
  }

  private String createReactionToHit(ResultOfHit result) {
    return switch (result) {
      case HIT -> "You hit the ship!";
      case MISS -> "You missed!";
      case HIT_AND_SUNK -> "You just have sunk a " + ocean.getLastAttackedShip().getClass()
          .getSimpleName() + "!";
      case ALREADY_HIT -> "This chunk of the ship has already been hit!";
      case ALREADY_SUNK -> "This piece of the ship has already been sunk!";
    };
  }

}
