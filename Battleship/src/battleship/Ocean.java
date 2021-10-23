package battleship;

import java.util.ArrayList;
import java.util.List;

public class Ocean {

  private final int totalNumberOfShips;
  private final List<Ship> ships;
  private final List<Rocket> rockets;
  private final int rows;
  private final int columns;

  private Ship lastAttackedShip;
  private OceanMap oceanMap;
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
    numberOfShipsSunk = 0;
    rockets = new ArrayList<>();
    ships = fillOcean(submarines, destroyers, cruisers, battleships, carriers);
  }

  private List<Ship> fillOcean(
      int submarines, int destroyers, int cruisers, int battleships, int carriers) {
    List<Ship> ships = new ArrayList<>();

    oceanMap = new OceanMap(rows, columns, ships, rockets);

    addSomeShips(Carrier::new, ships, carriers);
    addSomeShips(Battleship::new, ships, battleships);
    addSomeShips(Cruiser::new, ships, cruisers);
    addSomeShips(Destroyer::new, ships, destroyers);
    addSomeShips(Submarine::new, ships, submarines);

    return ships;
  }

  private void addSomeShips(
      CreateShipInterface creator, List<Ship> ships, int amount) {
    for (int i = 0; i < amount; ++i) {
      Ship ship = creator.createShip();
      oceanMap.positionShip(ship);
      ships.add(ship);
    }
  }

  public ResultOfHit attackOcean(Rocket rocket) {
    ResultOfHit result = ResultOfHit.MISS;
    for (var ship : ships) {
      result = ship.hitShip(rocket);
      if (result != ResultOfHit.MISS) {
        lastAttackedShip = ship;
        if (result == ResultOfHit.HIT_AND_SUNK) {
          ++numberOfShipsSunk;
        }
        break;
      }
    }

    rockets.add(rocket);
    return result;
  }

  public static Ocean createOceanWithUserParameters(String[] args) {
    var parser = new Parser();
    int rows = parser.tryGetArgumentFromArgs(args, 0, "number of rows", 3, 45);
    int columns = parser.tryGetArgumentFromArgs(args, 1, "number of columns", 3, 45);
    int submarines = parser.tryGetArgumentFromArgs(args, 2, "number of submarines", 0, 30);
    int destroyers = parser.tryGetArgumentFromArgs(args, 3, "number of destroyers", 0, 30);
    int cruisers = parser.tryGetArgumentFromArgs(args, 4, "number of cruisers", 0, 30);
    int battleships = parser.tryGetArgumentFromArgs(args, 5, "number of battleships", 0, 30);
    int carriers = parser.tryGetArgumentFromArgs(args, 6, "number of carriers", 0, 30);

    return new Ocean(rows, columns, submarines, destroyers, cruisers, battleships, carriers);
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

}
