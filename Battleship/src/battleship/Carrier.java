package battleship;

public class Carrier extends Ship {

  private static final int SIZE_OF_CARRIER = 5;

  @Override
  public int getLength() {
    return SIZE_OF_CARRIER;
  }
}
