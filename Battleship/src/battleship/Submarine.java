package battleship;

public class Submarine extends Ship {

  private static final int SIZE_OF_SUBMARINE = 1;

  @Override
  public int getLength() {
    return SIZE_OF_SUBMARINE;
  }
}
