package battleship;

public abstract class Ship {

  private final ChunkOfShip[] chunks;

  Ship(ChunkOfShip[] chunks) {
    this.chunks = chunks;
  }

  public boolean isSunk() {
    for (var chunk : chunks) {
      if (!chunk.isHit()) {
        return false;
      }
    }
    return true;
  }

  public abstract int getLength();

  public ChunkOfShip[] getChunks() {
    return chunks.clone();
  }

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
        return ResultOfHit.HIT;
      }
    }

    return ResultOfHit.MISS;
  }
}
