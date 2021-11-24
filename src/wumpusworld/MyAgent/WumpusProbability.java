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
                if (hasToBeWumpus(unvisitedNeighbor)) {
                    resetWumpusProb();
                    //TODO maybe swap x and y ????
                    //BoardProbabilities.set_wumpusProbability(unvisitedNeighbor.y, unvisitedNeighbor.x, 1);
                    BoardProbabilities.set_wumpusProbability(unvisitedNeighbor.x, unvisitedNeighbor.y, 1);
                    return;
                }
                probableWumpusField.add(unvisitedNeighbor);
            }
                /*}
            }*/
        }

        /*for (int x = 0; x < _boardProbabilities.length; x++) {
            for (int y = 0; y < _boardProbabilities.length; y++) {
                if (!_world.isVisited(x+1, y+1)) {
                    _boardProbabilities[y][x].setWumpus_prob(_wumpusProbability);
                }
            }
        }*/

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
        // TODO wumpus probability ist kleiner bei stench in der Nähe als normal (map 4)
        // TODO calculateNewWumpusProb methode umschreiben, sodass er nur von einem wumpus ausgeht -> ES GIBT NUR EINEN WUMPUS (map 1)
        //double sum = 0;
        //return sum * (1.0 / 15); //1/15 = wumpus probability
        setWumpusFrontierValues();
        _wumpusProbabilities = BoardProbabilities.GetDeepCopy();
    }

    private static boolean hasToBeWumpus(Point point) {
        List<Point> wumpusPossibilities = new ArrayList<>();
        for (Point visitedNeighbor : getVisitedNeighbors(point, _world)) {
            if (_world.hasStench(visitedNeighbor.x + 1, visitedNeighbor.y + 1)) {
                for (Point neighbor : getNeighbors(visitedNeighbor, _world)) {
                    if (!_world.isVisited(neighbor.x + 1, neighbor.y + 1)) {
                        if (wumpusPossibilities.contains(neighbor) && neighbor.x == point.x && neighbor.y == point.y) {
                            return true;
                        } else if (checkVisitedNeighborsWithoutStench(neighbor)) {
                            return true;
                        } else {
                            wumpusPossibilities.add(neighbor);
                        }
                    }
                }
                /*if (wumpusPossibilities.contains(point) && wumpusPossibilities.do) {
                    return true;
                }*/
            }
        }
        return false;
    }

    private static boolean checkVisitedNeighborsWithoutStench(Point neighbor) {
        for (Point vN : getVisitedNeighbors(neighbor, _world)) {
            if (!_world.hasStench(vN.x + 1, vN.y + 1)) {
                return true;
            }
        }
        return false;
    }

    //endregion
}
