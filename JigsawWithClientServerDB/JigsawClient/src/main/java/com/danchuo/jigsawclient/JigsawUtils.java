package com.danchuo.jigsawclient;

import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class JigsawUtils {

  private JigsawUtils() {}

  public static String convertIntToStringTime(int allSeconds) {
    var minutes = allSeconds / Timer.SECONDS;
    var seconds = allSeconds % Timer.SECONDS;
    return (minutes > 9 ? "" : "0") + minutes + ':' + (seconds > 9 ? "" : "0") + seconds;
  }

  public static boolean isNodeAlreadyPlacedOnGrid(GridPane gridPane, int x, int y){
    for (var node : gridPane.getChildren()) {
      Integer currentX = GridPane.getColumnIndex(node);
      Integer currentY = GridPane.getRowIndex(node);
      if (currentX != null && currentY != null && currentX == x && currentY == y){
        return true;
      }

    }

    return false;
  }

  public static String generateTopTableFromAnswer(String answer, char delimiter) {
    var table = answer.split(String.valueOf(delimiter));
    if (table.length < 1) {
      return "no recorded games";
    }
    var top = new StringBuilder(10);
    top.append("top 10 winners:\n");
    int parameterIndex = 0;

    for (var parameter : table) {
      switch (parameterIndex) {
        case 0 -> {
          top.append("player ");
          top.append(parameter);
        }
        case 1 -> {
          var date = LocalDateTime.parse(parameter).atZone(ZoneId.systemDefault()).toLocalDateTime();
          DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss");
          OffsetDateTime timeUtc = date.atOffset(ZoneOffset.UTC);
          OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(OffsetDateTime.now().getOffset());
          top.append(", game ended at ");
          top.append(formatter2.format(offsetTime));
        }
        case 2 -> {
          top.append(", figures paced: ");
          top.append(parameter);
        }
        default -> {
          top.append(", time spent ");
          top.append(convertIntToStringTime(Integer.parseInt(parameter)));
          top.append("\n");
          parameterIndex = -1;
        }
      }
      ++parameterIndex;
    }

    return top.toString();
  }

}
