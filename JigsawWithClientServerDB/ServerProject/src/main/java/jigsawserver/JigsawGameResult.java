package jigsawserver;

import java.time.LocalDateTime;

public record JigsawGameResult(String login, LocalDateTime endGameTime, int amountOfTurns, int amountOfSeconds) {
}
