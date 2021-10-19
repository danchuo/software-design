package battleship;

public class Cruiser extends Ship {

  private static final int SIZE_OF_CRUISER = 3;

  Cruiser(ChunkOfShip[] chunks) {
    super(chunks);
  }

  @Override
  public int getLength() {
    return SIZE_OF_CRUISER;
  }
}
