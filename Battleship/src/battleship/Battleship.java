package battleship;

public class Battleship extends Ship {
  private static final int SIZE_OF_BATTLESHIP = 4;

  Battleship(ChunkOfShip[] chunks) {
    super(chunks);
  }

  @Override
  public int getLength() {
    return SIZE_OF_BATTLESHIP;
  }
}
