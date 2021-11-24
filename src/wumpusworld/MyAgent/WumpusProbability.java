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

        System.out.println("Wumpus Prob");
        printWumpusProbabilities();
    }

    public static Point testWumpusShooting() {
        int size = BoardProbabilities.GetBoardProbabilities().length;
        for (int i=0; i < size; i++) {
            for (int k=0; k < size; k++) {
                if (BoardProbabilities.GetBoardProbabilities()[i][k].getWumpus_prob() == 1) {
                    return new Point(k + 1, i + 1);
                }
            }
        }
        return null;
    }

    //endregion

    //region private methods

    private static void setWumpusFrontierValues() {
        HashSet<Point> probableWumpusField = new HashSet<>();
        int size = BoardProbabilities.GetBoardProbabilities().length;

        HashSet<Point> stenchFields = new HashSet<>();
        for (int x=0; x < size; x++) {
            for (int y=0; y < size; y++) {
                if (_world.hasStench(x+1, y+1)) {
                    stenchFields.add(new Point(x, y));
                }
            }
        }

        for (Point point : stenchFields) {
            /*if (stenchAround(f)) {
                List<Point> stenchNeighbors = getVisitedNeighbors(f);
                stenchNeighbors.removeIf(neighbor -> !_world.hasStench(neighbor.x + 1, neighbor.y + 1));
                for (Point sn : stenchNeighbors) {*/
            for (Point unvisitedNeighbor : getUnvisitedNeighbors(point, _world)) {
                if (hasToBeWumpus(unvisitedNeighbor)) {
                    System.out.println("Point: " + unvisitedNeighbor + " has to be wumpus");
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
            BoardProbabilities.GetBoardProbabilities()[point.y][point.x].setWumpus_prob(newWumpusProb);
        }
    }

    private static void resetWumpusProb() {
        int size = BoardProbabilities.GetBoardProbabilities().length;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                BoardProbabilities.GetBoardProbabilities()[y][x].setWumpus_prob(0);
            }
        }
    }

    private static void calculateNewWumpusProb() {
        // TODO wumpus probability ist kleiner bei stench in der Nähe als normal (map 4)
        // TODO calculateNewWumpusProb methode umschreiben, sodass er nur von einem wumpus ausgeht -> ES GIBT NUR EINEN WUMPUS (map 1)
        //double sum = 0;
        //return sum * (1.0 / 15); //1/15 = wumpus probability
        setWumpusFrontierValues();
        _wumpusProbabilities = deepCopy(BoardProbabilities.GetBoardProbabilities());
    }

    private static void printWumpusProbabilities() {
        for (int i = BoardProbabilities.GetBoardProbabilities().length - 1; i >= 0; i--) {
            for (int j = 0; j < BoardProbabilities.GetBoardProbabilities()[0].length; j++) {
                Point p = new Point(j, i);
                System.out.print(df.format(BoardProbabilities.GetBoardProbabilities()[i][j].getWumpus_prob()));
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static boolean hasToBeWumpus(Point point) {
        for (Point visitedNeighbor : getVisitedNeighbors(point, _world)) {
            if (_world.hasStench(visitedNeighbor.x + 1, visitedNeighbor.y + 1)) {
                List<Point> wumpusPossibilities = new ArrayList<>();
                for (Point neighbor : getNeighbors(visitedNeighbor, _world )) {
                    if (!_world.isVisited(neighbor.x + 1, neighbor.y + 1)) {
                        wumpusPossibilities.add(neighbor);
                    }
                }
                if (wumpusPossibilities.contains(point) && wumpusPossibilities.size() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    //endregion
}
