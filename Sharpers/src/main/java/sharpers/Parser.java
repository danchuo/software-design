package sharpers;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Parser {
  private final Scanner scanner;

  Parser() {
    scanner = new Scanner(System.in, StandardCharsets.UTF_8);
  }

  public int tryGetArgumentFromArgs(
      String[] args, int numberOfArgument, String phrase, int min, int max) {
    if (args != null && args.length > numberOfArgument) {
      Integer result = parseIntOrNull(args[numberOfArgument]);
      if (result != null && result >= min && result <= max) {
        return result;
      }
    }

    return askNumber(phrase, min, max);
  }

  private int askNumber(String phrase, int min, int max) {
    Integer result;
    do {
      System.out.println(
          "Please enter the " + phrase + " that belongs to the range [" + min + ";" + max + "]...");

      result = parseIntOrNull(scanner.nextLine());
    } while (result == null || result < min || result > max);

    return result;
  }

  private static Integer parseIntOrNull(String value) {
    Integer returnValue = null;
    try {
      returnValue = Integer.parseInt(value);
    } catch (NumberFormatException ignored) {
    }
    return returnValue;
  }
}
