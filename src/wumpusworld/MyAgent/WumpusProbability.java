package wumpusworld.MyAgent;

import wumpusworld.Point;
import wumpusworld.World;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static wumpusworld.MyAgent.Common.*;

public class WumpusProbability {
    //region private variables

    private static World _world;
    private static DecimalFormat df;
    private static FieldPropability[][] _wumpusProbabilities;

    //endregion

    //region public methods

    public static void Init(World world) {
        _world = world;
        df = new DecimalFormat("#.##");
    }

    public static void calculateNewProbabilities() {
        if (_world.wumpusAlive()) {
            calculateNewWumpusProb();
        } else {
            resetWumpusProb();
        }
    }

    public static Point testWumpusShooting() {
        if (!BoardProbabilities.IsInitialized()) {
            return null;
        }
        int size = BoardProbabilities.GetBoardSize();
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                if (BoardProbabilities.get_wumpusProbability(i, k) == 1) {
                    return new Point(i + 1, k + 1);
                }
            }
        }
        return null;
    }

    //endregion

    //region private methods

    private static void setWumpusFrontierValues() {
        HashSet<Point> probableWumpusField = new HashSet<>();
        int size = BoardProbabilities.GetBoardSize();

        HashSet<Point> stenchFields = new HashSet<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (_world.hasStench(x + 1, y + 1)) {
                    stenchFields.add(new Point(x, y));
                }
            }
        }
        for (Point point : stenchFields) {
            for (Point unvisitedNeighbor : getUnvisitedNeighbors(point, _world)) {
                Point wumpusPoint = hasToBeWumpus(unvisitedNeighbor);
                if (wumpusPoint != null) {
                    System.out.println("FOUND WUMPUS AT: " + wumpusPoint);
                    resetWumpusProb();
                    BoardProbabilities.set_wumpusProbability(wumpusPoint.x, wumpusPoint.y, 1);
                    return;
                }
                probableWumpusField.add(unvisitedNeighbor);
            }
        }

        for (Point point : probableWumpusField) {
            double newWumpusProb = 1.0 / probableWumpusField.size();
            BoardProbabilities.set_wumpusProbability(point.x, point.y, newWumpusProb);
        }
    }

    private static void resetWumpusProb() {
        int size = BoardProbabilities.GetBoardSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                BoardProbabilities.set_wumpusProbability(x, y, 0);
            }
        }
    }

    private static void calculateNewWumpusProb() {
        setWumpusFrontierValues();
        _wumpusProbabilities = BoardProbabilities.GetDeepCopy();
    }

    private static Point hasToBeWumpus(Point point) {
        List<Point> wumpusPossibilities = new ArrayList<>();
        for (Point visitedNeighbor : getVisitedNeighbors(point, _world)) {
            if (_world.hasStench(visitedNeighbor.x + 1, visitedNeighbor.y + 1)) {
                for (Point neighbor : getNeighbors(visitedNeighbor, _world)) {
                    if (!_world.isVisited(neighbor.x + 1, neighbor.y + 1)) {
                        if (wumpusPossibilities.contains(neighbor) && neighbor.x == point.x && neighbor.y == point.y) {
                            System.out.println("1: " + point);
                            return point;
                        } else {
                            if (!checkVisitedNeighborsWithoutStench(point, neighbor)) {
                                wumpusPossibilities.add(neighbor);
                            }
                        }
                    }
                }
            }
        }

        if (wumpusPossibilities.size() == 1) {
            return wumpusPossibilities.get(0);
        }

        return null;
    }

    private static boolean checkVisitedNeighborsWithoutStench(Point point, Point neighbor) {
        for (Point vN : getVisitedNeighbors(neighbor, _world)) {
            if (!_world.hasStench(vN.x + 1, vN.y + 1)) {
                System.out.println("hi from point: " + point + " neighbor: " + neighbor);
                return true;
            }
        }
        return false;
    }
    //endregion
}
