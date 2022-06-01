package jigsawserver;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class ParseUtils {

  private ParseUtils() {}

  public static int askNumber(String phrase, int min, int max) {
    Integer result;
    do {
      System.out.println(
          "Please enter the " + phrase + " that belongs to the range [" + min + ";" + max + "]...");

      result = parseIntOrNull(new Scanner(System.in, StandardCharsets.UTF_8).nextLine());
    } while (result == null || result < min || result > max);

    return result;
  }

  public static Integer parseIntOrNull(String value) {
    Integer returnValue = null;
    try {
      returnValue = Integer.parseInt(value);
    } catch (NumberFormatException ignored) {
    }
    return returnValue;
  }
}
