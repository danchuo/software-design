package battleship;

public class Destroyer extends Ship {

  private static final int SIZE_OF_DESTROYER = 2;

  Destroyer(ChunkOfShip[] chunks) {
    super(chunks);
  }

  @Override
  public int getLength() {
    return SIZE_OF_DESTROYER;
  }
}
