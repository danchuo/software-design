package sharpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
      var fileName = "src\\main\\resources\\names.txt";
      File file = new File(fileName);
      FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(fr);
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
      result = Integer.toString(0, Integer.MAX_VALUE / 2);
    }

    return result;
  }
}
