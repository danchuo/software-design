package com.danchuo.jigsawclient;

import java.util.ArrayList;
import java.util.List;

public final class FigureCreator {

  private FigureCreator() {}

  public static ArrayList<Point> getFigure(int figure, int orientation) {
    var points = new ArrayList<Point>();

    switch (figure) {
      case 0 -> generateJ(points, orientation);
      case 1 -> generateL(points, orientation);
      case 2 -> generateS(points, orientation);
      case 3 -> generateLargeA(points, orientation);
      case 4 -> generateLargeT(points, orientation);
      case 5 -> generateI(points, orientation);
      case 6 -> generateD(points, orientation);
      case 7 -> generateA(points, orientation);
      default -> generateT(points, orientation);
    }

    return points;
  }

  private static void generateJ(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0 -> {
        points.add(new Point(1, 0));
        points.add(new Point(2, 0));
        points.add(new Point(1, 2));
      }
      case 1 -> {
        points.add(new Point(0, 0));
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
      }
      case 2 -> {
        points.add(new Point(1, 0));
        points.add(new Point(1, 2));
        points.add(new Point(0, 2));
      }
      default -> {
        points.add(new Point(2, 2));
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
      }
    }
  }

  private static void generateL(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0 -> {
        points.add(new Point(1, 0));
        points.add(new Point(0, 0));
        points.add(new Point(1, 2));
      }
      case 1 -> {
        points.add(new Point(2, 0));
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
      }
      case 2 -> {
        points.add(new Point(1, 0));
        points.add(new Point(1, 2));
        points.add(new Point(2, 2));
      }
      default -> {
        points.add(new Point(0, 2));
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
      }
    }
  }

  private static void generateS(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0 -> {
        points.add(new Point(1, 0));
        points.add(new Point(2, 1));
        points.add(new Point(2, 2));
      }
      case 1 -> {
        points.add(new Point(2, 1));
        points.add(new Point(0, 2));
        points.add(new Point(1, 2));
      }
      case 2 -> {
        points.add(new Point(1, 0));
        points.add(new Point(0, 1));
        points.add(new Point(0, 2));
      }
      default -> {
        points.add(new Point(2, 2));
        points.add(new Point(1, 2));
        points.add(new Point(0, 1));
      }
    }
  }

    private static void generateLargeA(List<Point> points, int orientation) {
      switch (orientation) {
        case 0 -> {
          points.add(new Point(2, 2));
          points.add(new Point(0, 2));
          points.add(new Point(1, 2));
          points.add(new Point(2, 0));
          points.add(new Point(2, 1));
        }
        case 1 -> {
          points.add(new Point(2, 2));
          points.add(new Point(0, 2));
          points.add(new Point(1, 2));
          points.add(new Point(0, 0));
          points.add(new Point(0, 1));
        }
        case 2 -> {
          points.add(new Point(0, 0));
          points.add(new Point(0, 1));
          points.add(new Point(0, 2));
          points.add(new Point(1, 0));
          points.add(new Point(2, 0));
        }
        default -> {
          points.add(new Point(0, 0));
          points.add(new Point(2, 1));
          points.add(new Point(2, 2));
          points.add(new Point(1, 0));
          points.add(new Point(2, 0));
        }
      }
    }


  private static void generateLargeT(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0 -> {
        points.add(new Point(1, 0));
        points.add(new Point(1, 2));
        points.add(new Point(0, 2));
        points.add(new Point(2, 2));
      }
      case 1 -> {
        points.add(new Point(1, 0));
        points.add(new Point(1, 2));
        points.add(new Point(0, 0));
        points.add(new Point(2, 0));
      }
      case 2 -> {
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
        points.add(new Point(0, 0));
        points.add(new Point(0, 2));
      }
      default -> {
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
        points.add(new Point(2, 0));
        points.add(new Point(2, 2));
      }
    }
  }

  private static void generateI(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0:
      case 1:
        points.add(new Point(0, 1));
        points.add(new Point(2, 1));
        break;
      case 2:
      default:
        points.add(new Point(1, 0));
        points.add(new Point(1, 2));
        break;
    }
  }

  private static void generateD(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
  }

  private static void generateA(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0 -> {
        points.add(new Point(2, 1));
        points.add(new Point(1, 2));
      }
      case 1 -> {
        points.add(new Point(0, 1));
        points.add(new Point(1, 2));
      }
      case 2 -> {
        points.add(new Point(1, 0));
        points.add(new Point(0, 1));
      }
      default -> {
        points.add(new Point(1, 0));
        points.add(new Point(2, 1));
      }
    }
  }

  private static void generateT(List<Point> points, int orientation) {
    points.add(new Point(1, 1));
    switch (orientation) {
      case 0 -> {
        points.add(new Point(1, 2));
        points.add(new Point(0, 2));
        points.add(new Point(2, 2));
      }
      case 1 -> {
        points.add(new Point(1, 0));
        points.add(new Point(0, 0));
        points.add(new Point(2, 0));
      }
      case 2 -> {
        points.add(new Point(0, 1));
        points.add(new Point(0, 0));
        points.add(new Point(0, 2));
      }
      default -> {
        points.add(new Point(2, 1));
        points.add(new Point(2, 0));
        points.add(new Point(2, 2));
      }
    }
  }
}
