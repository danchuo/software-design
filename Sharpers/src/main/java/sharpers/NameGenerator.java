package sharpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class NameGenerator {

  private final List<String> names;
  private final SecureRandom random;

  NameGenerator() {
    random = new SecureRandom();
    names = new ArrayList<>();
    try {
      var fileName = "/names.txt";
      InputStream input = getClass().getResourceAsStream(fileName);
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
      String line = reader.readLine();
      while (line != null) {
        names.add(line);
        line = reader.readLine();
      }
    } catch (IOException ignored) {
    }
  }

  public String getRandomName() {
    String result;

    if (names.size() > 1) {
      result = names.get(random.nextInt(0, names.size()));
    } else {
      result = Double.toString(System.currentTimeMillis() * 0.00001 * random.nextInt(0, 100));
    }

    return result;
  }
}
