package battleship;

import java.util.Scanner;

public class Parser {

  private final Scanner scanner;

  Parser() {
    scanner = new Scanner(System.in);
  }

  public Rocket parseRocket(int rows, int columns, int numberOfAvailableTorpedoes) {
    System.out.println("\nEnter the chunk you want to attack in the format \"T x y\",\n"
        + "where 'T' is an optional flag indicating whether the missile is a torpedo ("
        + numberOfAvailableTorpedoes +
        " torpedoes available),\n"
        + "'x' is the horizontal coordinate from 0 to " + (columns - 1)
        + ", 'y' is the vertical coordinate from 0 to " + (rows - 1) + ".");

    String input = scanner.nextLine();
    String[] words = input.split(" ");
    Integer x;
    Integer y;

    if (words.length == 3) {
      if (words[0].equals("T")) {
        if (numberOfAvailableTorpedoes > 0) {
          x = parseIntOrNull(words[1]);
          y = parseIntOrNull(words[2]);
          if (x != null && y != null && x < columns && y < rows && x >= 0 && y >= 0) {
            return new Rocket(x, y, true);
          } else {
            System.out.println("Wrong coordinates!");
            return parseRocket(rows, columns, numberOfAvailableTorpedoes);
          }
        } else {
          System.out.println("No torpedoes available!");
          return parseRocket(rows, columns, numberOfAvailableTorpedoes);
        }
      } else {
        System.out.println("Wrong first parameter!");
        return parseRocket(rows, columns, numberOfAvailableTorpedoes);
      }
    } else {
      if (words.length == 2) {
        x = parseIntOrNull(words[0]);
        y = parseIntOrNull(words[1]);
        if (x != null && y != null && x < columns && y < rows && x >= 0 && y >= 0) {
          return new Rocket(x, y, false);
        } else {
          System.out.println("Wrong coordinates!");
          return parseRocket(rows, columns, numberOfAvailableTorpedoes);
        }
      } else {
        System.out.println("Wrong number of parameters!");
        return parseRocket(rows, columns, numberOfAvailableTorpedoes);
      }
    }
  }


  public int tryGetArgumentFromArgs(String[] args, int numberOfArgument, String phrase, int min,
      int max) {
    Integer result;

    if (args != null && args.length > numberOfArgument) {
      result = parseIntOrNull(args[numberOfArgument]);
      if (result != null && result >= min && result <= max) {
        return result;
      }
    }

    return askNumber(phrase, min, max);
  }

  public int askNumber(String phrase, int min, int max) {
    Integer result;
    do {
      System.out.println("Please enter the " + phrase +
          " that belongs to the range [" + min + ";" + max + "]...");
      result = parseIntOrNull(scanner.nextLine());

    } while (result == null || result < min || result > max);

    return result;
  }

  public void makeManyBlankLines() {
    System.out.println(System.lineSeparator().repeat(50));
  }

  public Integer parseIntOrNull(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
