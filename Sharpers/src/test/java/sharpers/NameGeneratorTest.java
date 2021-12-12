package sharpers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Test;

class NameGeneratorTest {

  @Test
  void getRandomName_NewGenerator_GetCorrectName() {
    var generator = new NameGenerator();
    var name = generator.getRandomName();
    assertTrue(Objects.nonNull(name));
  }
}
