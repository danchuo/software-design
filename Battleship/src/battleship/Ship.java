package battleship;

public abstract class Ship {

  private ChunkOfShip[] chunks;

  private Alignment alignment;

  Ship() {
  }


  /**
   * Recover the ship.
   */
  public void recoverShip() {
    for (var chunk : chunks) {
      chunk.setHit(false);
    }
  }

  public boolean isSunk() {
    for (var chunk : chunks) {
      if (!chunk.isHit()) {
        return false;
      }
    }
    return true;
  }


  /**
   * @return Length of current ship.
   */
  public abstract int getLength();


  /**
   * The method tries to hit the ship with a rocket.
   *
   * @param rocket Attacking rocket
   * @return Returns the result of a missile attack on the ship.
   */
  public ResultOfHit hitShip(Rocket rocket) {
    for (var chunk : chunks) {
      if (chunk.areCoordinatesEqual(rocket)) {

        if (isSunk()) {
          return ResultOfHit.ALREADY_SUNK;
        }

        if (rocket.isTorpedo()) {
          for (var chunkOfShip : chunks) {
            chunkOfShip.setHit(true);
          }

          return ResultOfHit.HIT_AND_SUNK;
        }

        if (chunk.isHit()) {
          return ResultOfHit.ALREADY_HIT;
        }

        chunk.setHit(true);
        if (isSunk()) {
          return ResultOfHit.HIT_AND_SUNK;
        }
        return ResultOfHit.HIT;
      }
    }

    return ResultOfHit.MISS;
  }

  public ChunkOfShip[] getChunks() {
    return chunks;
  }

  public void setChunks(ChunkOfShip... chunks) {
    this.chunks = chunks.clone();
  }

  public Alignment getAlignment() {
    return alignment;
  }

  public void setAlignment(Alignment alignment) {
    this.alignment = alignment;
  }
}
