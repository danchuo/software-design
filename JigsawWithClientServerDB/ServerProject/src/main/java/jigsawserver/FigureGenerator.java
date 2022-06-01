package jigsawserver;

import java.security.SecureRandom;
import java.util.ArrayList;

public class FigureGenerator {

  private static final char[] FIGURES = {'1','2','3','4','5','6','7','8','9'};

  private final ArrayList<String> createdFigures = new ArrayList<>();

  public String generateFigure(int numberOfFigure) {
    if (createdFigures.size() < numberOfFigure + 1) {
      var randomFigure = FIGURES[new SecureRandom().nextInt(FIGURES.length)];
      var randomStanding = new SecureRandom().nextInt(4);
      createdFigures.add(randomFigure + " " + randomStanding);
    }

    return createdFigures.get(numberOfFigure);
  }
}
