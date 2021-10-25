package battleship;

import java.util.List;
import java.util.Random;

public class OceanMap {

  public static final char SYMBOL_OF_OCEAN = 'o';
  public static final char SYMBOL_OF_SUNK_SHIP = 's';
  public static final char SYMBOL_OF_DAMAGED_CHUNK = 'h';
  public static final char SYMBOL_OF_PLACE_MISSED_ROCKET = 'x';

  private final int rows;
  private final int columns;
  private final List<Ship> ships;
  private final List<Rocket> rockets;
  private final char[][] map;

  OceanMap(int rows, int columns, List<Ship> ships, List<Rocket> rockets) {
    this.rows = rows;
    this.columns = columns;
    this.ships = ships;
    this.rockets = rockets;
    map = new char[rows][columns];

    clearMap();
  }


  /**
   * Prints a map of the ocean into console.
   */
  public void printMap() {
    rewriteMap();
    int maxFormatLength = Math.max(Integer.toString(rows).length(),
        Integer.toString(columns).length()) + 1;

    for (int i = -1; i < rows; ++i) {
      for (int j = -1; j < columns; ++j) {

        if (i == -1) {
          System.out.format("%" + maxFormatLength + "d", Math.abs(j));
        } else if (j == -1) {
          System.out.format("%" + maxFormatLength + "d", Math.abs(i));
        } else {
          System.out.format("%" + maxFormatLength + "c", map[i][j]);
        }
      }
      System.out.println();
    }
  }

  /**
   * The method updates the map by adding information about rockets fired, hit and sunk ships.
   */
  private void rewriteMap() {
    clearMap();
    for (var rocket : rockets) {
      map[rocket.getY()][rocket.getX()] = SYMBOL_OF_PLACE_MISSED_ROCKET;
    }

    for (var ship : ships) {
      if (ship.isSunk()) {
        for (var chunk : ship.getChunks()) {
          if (chunk.isHit()) {
            map[chunk.getY()][chunk.getX()] = SYMBOL_OF_SUNK_SHIP;
          }
        }
        markPlaceAroundShip(ship);
      } else {
        for (var chunk : ship.getChunks()) {
          if (chunk.isHit()) {
            map[chunk.getY()][chunk.getX()] = SYMBOL_OF_DAMAGED_CHUNK;
          }
        }
      }
    }
  }


  private void clearMap() {
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < columns; ++j) {
        map[i][j] = SYMBOL_OF_OCEAN;
      }
    }
  }


  /**
   * Tries 4000 times to place the ship on the map in a random place, throws an StackOverflowError
   * if it fails.
   *
   * @param ship
   */
  public void positionShip(Ship ship) throws StackOverflowError {
    Random rand = new Random();

    ChunkOfShip[] chunks = new ChunkOfShip[ship.getLength()];
    for (int i = 0; i < ship.getLength(); ++i) {
      chunks[i] = new ChunkOfShip(0, 0);
    }

    boolean isPositionFind = false;
    for (int i = 0; i < 4000; ++i) {
      int randomAlignment = rand.nextInt(0, 4);
      int randomX = rand.nextInt(0, columns);
      int randomY = rand.nextInt(0, rows);

      switch (randomAlignment) {
        case 0 -> {
          if (randomY - ship.getLength() + 1 >= 0) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY - j][randomX] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[ship.getLength() - j - 1].setX(randomX);
              chunks[ship.getLength() - j - 1].setY(randomY - j);
            }
          }
        }
        case 1 -> {
          if (randomX - ship.getLength() + 1 >= 0) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY][randomX - j] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[ship.getLength() - j - 1].setX(randomX - j);
              chunks[ship.getLength() - j - 1].setY(randomY);
            }
          }
        }
        case 2 -> {
          if (randomY + ship.getLength() - 1 < rows) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY + j][randomX] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[j].setX(randomX);
              chunks[j].setY(randomY + j);
            }
          }
        }
        case 3 -> {
          if (randomX + ship.getLength() - 1 < columns) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY][randomX + j] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[j].setX(randomX + j);
              chunks[j].setY(randomY);
            }
          }
        }
      }

      if (isPositionFind) {
        ship.setChunks(chunks);

        for (var chunk : chunks) {
          map[chunk.getY()][chunk.getX()] = SYMBOL_OF_SUNK_SHIP;
        }

        if (randomAlignment % 2 == 0) {
          ship.setAlignment(Alignment.VERTICAL);
        } else {
          ship.setAlignment(Alignment.HORIZONTAL);
        }

        markPlaceAroundShip(ship);
        break;
      }
    }

    if (!isPositionFind) {
      throw new StackOverflowError("A lot of ships");
    }
  }


  /**
   * The method marks the place around the ship by SYMBOL_OF_PLACE_MISSED_ROCKET at the map depends
   * on its alignment.
   *
   * @param ship Ship to mark.
   */
  private void markPlaceAroundShip(Ship ship) {
    switch (ship.getAlignment()) {
      case HORIZONTAL -> markPlaceAroundHorizontalShip(ship);
      case VERTICAL -> markPlaceAroundVerticalShip(ship);
    }
  }


  private void markPlaceAroundHorizontalShip(Ship ship) {
    for (int i = 0; i < ship.getLength(); ++i) {
      var currentChunk = ship.getChunks()[i];

      if (i == 0 && currentChunk.getX() - 1 >= 0) {
        map[currentChunk.getY()][currentChunk.getX()
            - 1] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
        if (currentChunk.getY() - 1 >= 0) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
        if (currentChunk.getY() + 1 < rows) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
      }

      if (i == ship.getLength() - 1 && currentChunk.getX() + 1 < columns) {
        map[currentChunk.getY()][currentChunk.getX()
            + 1] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
        if (currentChunk.getY() - 1 >= 0) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
        if (currentChunk.getY() + 1 < rows) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
      }

      if (currentChunk.getY() - 1 >= 0) {
        map[currentChunk.getY()
            - 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
      }
      if (currentChunk.getY() + 1 < rows) {
        map[currentChunk.getY()
            + 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
      }
    }
  }

  private void markPlaceAroundVerticalShip(Ship ship) {
    for (int i = 0; i < ship.getLength(); ++i) {
      var currentChunk = ship.getChunks()[i];

      if (i == 0 && currentChunk.getY() - 1 >= 0) {
        map[currentChunk.getY()
            - 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
        if (currentChunk.getX() - 1 >= 0) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
        if (currentChunk.getX() + 1 < columns) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
      }

      if (i == ship.getLength() - 1 && currentChunk.getY() + 1 < rows) {
        map[currentChunk.getY()
            + 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
        if (currentChunk.getX() - 1 >= 0) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
        if (currentChunk.getX() + 1 < columns) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_MISSED_ROCKET;
        }
      }

      if (currentChunk.getX() - 1 >= 0) {
        map[currentChunk.getY()][currentChunk.getX()
            - 1] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
      }
      if (currentChunk.getX() + 1 < columns) {
        map[currentChunk.getY()][currentChunk.getX()
            + 1] =
            SYMBOL_OF_PLACE_MISSED_ROCKET;
      }
    }
  }
}
