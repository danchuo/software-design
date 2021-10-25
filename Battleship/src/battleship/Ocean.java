package battleship;

import java.util.ArrayList;
import java.util.List;

public class Ocean {

  private static final int MAX_OCEAN_SIZE = 45;
  private final int totalNumberOfShips;
  private final int rows;
  private final int columns;
  private final List<Ship> ships;
  private final List<Rocket> rockets;
  private final OceanMap oceanMap;

  private Ship lastAttackedShip;
  private Ship preLastAttackedShip;
  private int numberOfShipsSunk;

  Ocean(
      int rows,
      int columns,
      int submarines,
      int destroyers,
      int cruisers,
      int battleships,
      int carriers) {
    this.rows = rows;
    this.columns = columns;
    totalNumberOfShips = submarines + destroyers + cruisers + battleships + carriers;
    lastAttackedShip = null;
    preLastAttackedShip = null;
    numberOfShipsSunk = 0;
    rockets = new ArrayList<>();
    ships = new ArrayList<>();
    oceanMap = new OceanMap(rows, columns, ships, rockets);
    fillOcean(submarines, destroyers, cruisers, battleships, carriers);
  }

  private void fillOcean(
      int submarines, int destroyers, int cruisers, int battleships, int carriers) {
    addSomeShips(Carrier::new, carriers);
    addSomeShips(Battleship::new, battleships);
    addSomeShips(Cruiser::new, cruisers);
    addSomeShips(Destroyer::new, destroyers);
    addSomeShips(Submarine::new, submarines);
  }

  private void addSomeShips(
      CreateShipInterface creator, int amount) {
    for (int i = 0; i < amount; ++i) {
      Ship ship = creator.createShip();
      oceanMap.positionShip(ship);
      ships.add(ship);
    }
  }


  /**
   * The method tries to attack all the ships in the ocean and returns the result of it. Also
   * remembers the last two affected ships.
   *
   * @param rocket Attacking rocket.
   * @return Result of hit.
   */
  public ResultOfHit attackOcean(Rocket rocket) {
    ResultOfHit result = ResultOfHit.MISS;
    for (var ship : ships) {
      result = ship.hitShip(rocket);
      if (result != ResultOfHit.MISS) {
        preLastAttackedShip = lastAttackedShip;
        lastAttackedShip = ship;
        if (result == ResultOfHit.HIT_AND_SUNK) {
          ++numberOfShipsSunk;
        }
        break;
      }
    }

    if (result == ResultOfHit.MISS) {
      preLastAttackedShip = lastAttackedShip;
      lastAttackedShip = null;
    }

    rockets.add(rocket);
    return result;
  }


  /**
   * The method tries to create an ocean from the input data and requests them if any were
   * incorrect.
   *
   * @param args Input array.
   * @return Created ocean.
   */
  public static Ocean createOceanWithUserParameters(String... args) {
    var parser = new Parser();
    int rows = parser.tryGetArgumentFromArgs(args, 0, "number of rows", 3, MAX_OCEAN_SIZE);
    int columns = parser.tryGetArgumentFromArgs(args, 1, "number of columns", 3, MAX_OCEAN_SIZE);
    int submarines = parser.tryGetArgumentFromArgs(args, 2, "number of submarines", 0,
        columns * rows / 3);
    int destroyers = parser.tryGetArgumentFromArgs(args, 3, "number of destroyers", 0,
        columns * rows / 5);
    int cruisers = parser.tryGetArgumentFromArgs(args, 4, "number of cruisers", 0,
        columns * rows / 6);
    int battleships = parser.tryGetArgumentFromArgs(args, 5, "number of battleships", 0,
        columns * rows / 8);
    int carriers = parser.tryGetArgumentFromArgs(args, 6, "number of carriers", 0,
        columns * rows / 9);

    return new Ocean(rows, columns, submarines, destroyers, cruisers, battleships, carriers);
  }


  /**
   * Recover the ship and removes all the rockets that were fired at it.
   *
   * @param ship Ship to recover.
   */
  public void recoverShip(Ship ship) {
    ship.recoverShip();

    for (var chunk : ship.getChunks()) {
      for (var rocket : rockets) {
        if (chunk.areCoordinatesEqual(rocket)) {
          rockets.remove(rocket);
          break;
        }
      }
    }
  }


  public int getLengthOfAllShips() {
    int length = 0;
    for (var ship : ships) {
      length += ship.getLength();
    }
    return length;
  }

  public void printMap() {
    oceanMap.printMap();
  }

  public int getTotalNumberOfShips() {
    return totalNumberOfShips;
  }

  public Ship getLastAttackedShip() {
    return lastAttackedShip;
  }

  public int getNumberOfShipsSunk() {
    return numberOfShipsSunk;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public Ship getPreLastAttackedShip() {
    return preLastAttackedShip;
  }
}
