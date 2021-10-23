package battleship;

public class Destroyer extends Ship {

  private static final int SIZE_OF_DESTROYER = 2;

  @Override
  public int getLength() {
    return SIZE_OF_DESTROYER;
  }
}
