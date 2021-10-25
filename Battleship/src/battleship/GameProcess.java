package battleship;

import java.util.Scanner;

public class GameProcess {

  private final boolean isRecoveringMode;
  private final int numberOfAvailableTorpedoes;
  private final Parser parser;
  private Ocean ocean;

  public static void main(String... args) {
    var game = new GameProcess(args);
    game.play();
  }

  GameProcess(String... args) {
    parser = new Parser();

    try {
      ocean = Ocean.createOceanWithUserParameters(args);
    } catch (StackOverflowError e) {
      parser.makeNBlankLines(50);
      System.out.println("Sorry, but so many ships couldn't fit on an ocean of your size. "
          + "Try to re-enter the input parameters or quit the game.");
      restart();
    }

    numberOfAvailableTorpedoes = parser.tryGetArgumentFromArgs(args, 7, "number of torpedoes", 0,
        ocean.getTotalNumberOfShips());

    isRecoveringMode =
        parser.tryGetArgumentFromArgs(args, 6,
            "boolean variable \"is recovering mode on\", where \"0\" means false, \"1\" true,", 0,
            1) != 0;
  }

  /**
   * Starts the created game until it finishes.
   */
  public void play() {
    parser.makeNBlankLines(50);
    printInstruction();
    int currentNumberOfAvailableTorpedoes = numberOfAvailableTorpedoes;
    int numberOfRockets = 0;

    while (ocean.getNumberOfShipsSunk() < ocean.getTotalNumberOfShips()) {
      System.out.println("\nThis is your ocean:");
      ocean.printMap();
      var rocket = parser.parseRocket(ocean.getRows(), ocean.getColumns(),
          currentNumberOfAvailableTorpedoes);
      ++numberOfRockets;

      if (rocket.isTorpedo()) {
        --currentNumberOfAvailableTorpedoes;
      }

      var resultOfHit = ocean.attackOcean(rocket);
      System.out.println(createReactionToHit(resultOfHit));
      if (isRecoveringMode) {
        if (ocean.getPreLastAttackedShip() != null
            && ocean.getPreLastAttackedShip() != ocean.getLastAttackedShip()
            && !ocean.getPreLastAttackedShip().isSunk()) {
          ocean.recoverShip(ocean.getPreLastAttackedShip());
          System.out.println("The previous damaged ship has been completely restored!");
        }
      }

      System.out.println("Press [ENTER] to continue...");
      Scanner scanner = new Scanner(System.in);
      scanner.nextLine();
    }

    System.out.println(
        "Congratulations! You just won the game and sunk " + ocean.getNumberOfShipsSunk()
            + " ships with " + numberOfRockets + " rockets (which contained "
            + (numberOfAvailableTorpedoes - currentNumberOfAvailableTorpedoes)
            + " torpedoes) out of "
            + ocean.getLengthOfAllShips()
            + " required (length of all ships).\nSo your score is " + (double) numberOfRockets
            / ocean.getLengthOfAllShips()
            + ".\nDo you want to quit the game or play again?");
    restart();
  }

  private String createReactionToHit(ResultOfHit result) {
    return switch (result) {
      case HIT -> "You hit the ship!";
      case MISS -> "You missed!";
      case HIT_AND_SUNK -> "You just have sunk a " + ocean.getLastAttackedShip().getClass()
          .getSimpleName() + "!";
      case ALREADY_HIT -> "This chunk of the ship has already been hit!";
      case ALREADY_SUNK -> "This ship has already been sunk!";
    };
  }

  private void restart() {
    boolean reEnter =
        parser.tryGetArgumentFromArgs(null, 6,
            "boolean variable \"quit the game\", where \"0\" means false, \"1\" true,", 0,
            1) == 0;
    if (reEnter) {
      new GameProcess((String) null).play();
    } else {
      System.exit(0);
    }
  }

  private static void printInstruction() {
    System.out.println(
        "Battleship is a simple game. "
            + "You need to sink all the ships that the computer will place.\n"
            + "The game implements torpedo firing and ship recovery modes.\n"
            + "A few notations: the symbol [" + OceanMap.SYMBOL_OF_OCEAN
            + "] means the ocean cell, \n"
            + "the symbol [x] means an ocean cell without a ship, which was hit by a rocket,\n"
            + "the symbol [h] means an ocean cell with a ship hit by a rocket,\n"
            + "the symbol [s] means an ocean cell with a completely sunk ship.\n"
            + "Also, if you have sunk the ship, "
            + "then all the cells around it will be marked with the symbol [x] for your convenience,"
            + " but this does not mean that you cannot hit them.\n"
            + "The score of the game consists of the ratio of spent rockets to the length of all ships, \n"
            + "respectively, 1.0 is the best score, "
            + "where rockets hit all ships without misses (with the torpedo mod, the score may be less than one).");
  }
}
