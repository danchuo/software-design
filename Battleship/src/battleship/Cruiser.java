package battleship;

public class Cruiser extends Ship {

  private static final int SIZE_OF_CRUISER = 3;

  @Override
  public int getLength() {
    return SIZE_OF_CRUISER;
  }
}
