package wumpusworld.MyAgent;

import wumpusworld.*;
import wumpusworld.dijkstra.Dijkstra;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static wumpusworld.MyAgent.BoardProbabilities._pitProbability;
import static wumpusworld.MyAgent.Common.*;

public class NaiveBayes {

    //region private variables

    private static World _world;
    private static FieldPropability[][] _calcProbabilities;
    private static List<Double> _pitFrontierValues;

    //endregion

    //region public methods

    public static void Init(World world) {
        _world = world;
        _pitFrontierValues = new ArrayList<>();
    }

    public static void calculateNewProbabilities() {
        int size = BoardProbabilities.GetBoardSize();

        Point p = new Point(_world.getPlayerX(), _world.getPlayerY());
        setNeighborPitProbabilities(p);

        for (int i = size - 1; i >= 0; i--) {
            for (int j = 0; j < size; j++) {
                if (_world.hasPit(j+1, i+1)) {
                    BoardProbabilities.set_pitProbability(j, i, 1);
                }
            }
        }
        for (Point point : BoardProbabilities.get_frontier()) {
            double p_prob = 0;
            if (breezeAround(point)) {
                p_prob = calculateNewPitProb(point);
            }
            //TODO maybe swap X and Y
            BoardProbabilities.set_pitProbability(point.x, point.y, p_prob);
        }
    }

    private static void setNeighborPitProbabilities(Point point) {
        if (!_world.hasBreeze(point.x, point.y)) {
            Point myPoint = new Point(point.x - 1, point.y - 1);
            List<Point> neighbors = getNeighbors(myPoint, _world);
            for (Point neighbor : neighbors) {
                BoardProbabilities.set_pitProbability(neighbor.x, neighbor.y, 0);
            }
        }
    }

    //endregion



    private static double calculateNewPitProb(Point point) {
        List<Point> newFrontier = deepCopy(BoardProbabilities.get_frontier());
        newFrontier.remove(point);
        _calcProbabilities = BoardProbabilities.GetDeepCopy();

        System.out.println("Point: " + point);

        //get values for point = pit
        double sum_pit = 0;
        if (couldBePit(point)) {
            _calcProbabilities[point.y][point.x].setPit_prob(1);
            setPitFrontierValues(deepCopy(_calcProbabilities), deepCopy(newFrontier), new ArrayList<>());
            sum_pit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();

            System.out.println("pit sum:");
            System.out.println(Arrays.toString(_pitFrontierValues.toArray()));
            _pitFrontierValues.clear();
        }

        double sum_noPit = 0;
        //get values for point != pit
        System.out.println("hastobepit: " + point + " " + hasToBePit(point));
        if (!hasToBePit(point)) {
            _calcProbabilities[point.y][point.x].setPit_prob(0);
            setPitFrontierValues(deepCopy(_calcProbabilities), deepCopy(newFrontier), new ArrayList<>());
            sum_noPit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();
            System.out.println("no pit sum:");
            System.out.println(Arrays.toString(_pitFrontierValues.toArray()));
            _pitFrontierValues.clear();
        }


        double pitValue = sum_pit * _pitProbability;
        double noPitValue = sum_noPit * (1 - _pitProbability);
        double alpha = pitValue + noPitValue;

        System.out.println("--------------------------------------------");
        double res = pitValue / alpha;
        return res;
    }

    private static void setPitFrontierValues(FieldPropability[][] pip_probability, List<Point> frontier, List<Double> probabilities) {
        if (frontier.isEmpty()) {
            double res = 1;
            if (probabilities.isEmpty()) {
                res = 0;
            } else {
                for (Double d : probabilities) {
                    res *= d;
                }
            }
            _pitFrontierValues.add(res);
            return;
        }

        Point point = frontier.get(0);
        // Does the field has a breeze around it
        if (breezeAround(point)) {
            // Field has breeze around and is a pit with certainty
            System.out.println("hastobepit: " + point + " " + hasToBePit(point));
            if (hasToBePit(point)) {
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                //newProbabilities.add(_calcProbabilities[point.y][point.x].getPit_prob());
                newProbabilities.add(0.2);
                FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);
                // Field has breeze around it and might be a pit
            } else {
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                //newProbabilities.add(_calcProbabilities[point.y][point.x].getPit_prob());
                newProbabilities.add(0.2);
                FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);

                newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                newProbabilities = new ArrayList<>(probabilities);
                //newProbabilities.add(1 - _calcProbabilities[point.y][point.x].getPit_prob());
                newProbabilities.add(0.8);
                new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(0);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);
            }
            // Field is not a pit
        } else {
            List<Point> newFrontier = deepCopy(frontier);
            newFrontier.remove(point);
            List<Double> newProbabilities = new ArrayList<>(probabilities);
            _calcProbabilities[point.y][point.x].setPit_prob(0.0);
            FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
            new_pit_probability[point.y][point.x].setPit_prob(0);

            setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);
        }
    }

    private static boolean breezeAround(Point point) {
        List<Point> neighbors = getNeighbors(point, _world);
        for (Point p : neighbors) {
            if (_world.hasBreeze(p.x + 1, p.y + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasToBePit(Point point) {
        for (Point p : getVisitedNeighbors(point, _world)) {
            if (_world.hasBreeze(p.x + 1, p.y + 1)) {
                List<Point> pitPossibilities = new ArrayList<>();
                for (Point q : getNeighbors(p, _world)) {
                    // CARINA: if (_world.hasPit(q.x + 1, q.y + 1) || pip_probability[q.x][q.y].getPit_prob() == 1) {
                    if (_world.hasPit(q.x + 1, q.y + 1) || _calcProbabilities[q.y][q.x].getPit_prob() == 1) {
                        pitPossibilities.clear();
                        break;
                    }
                    // CARINA if (!_world.isVisited(q.x + 1, q.y + 1) && pip_probability[q.x][q.y].getPit_prob() == 0.2) {
                    if (!_world.isVisited(q.x + 1, q.y + 1) && _calcProbabilities[q.y][q.x].getPit_prob() == 0.2) {
                        // CARINA if(couldBePit(q)) pitPossibilities.add(q);
                        pitPossibilities.add(q);
                    }
                }

                if (pitPossibilities.contains(point) && pitPossibilities.size() == 1) {
                    return true;
                }
                // ANFANG solution johannes
                List<Point> unvisitedBreezeNeighbors = getUnvisitedNeighbors(p, _world);
                unvisitedBreezeNeighbors.remove(point);
                for (Point vBN : unvisitedBreezeNeighbors) {
                    List<Point> visitedUnvisitedBreezeNeighbors = getVisitedNeighbors(vBN, _world);
                    for (Point vUBN : visitedUnvisitedBreezeNeighbors) {
                        if (!_world.hasBreeze(vUBN.x+1, vUBN.y+1)) {
                            List<Point> res = getUnvisitedNeighbors(p, _world);
                            res.remove(vBN);
                            if (res.size() == 1) {
                                return true;
                            }
                        }
                    }
                }
                // ENDE SOLUTION JOHANNES
                //System.out.println(pitPossibilities);
            }
        }
        return false;
    }

    public static boolean couldBePit(Point point){
        List<Point> neighbors = getVisitedNeighbors(point, _world);
        boolean couldBe = true;
        for(Point neighbor : neighbors ){
            if(!_world.hasBreeze(neighbor.x + 1, neighbor.y + 1)) couldBe = false;
        }
        return couldBe;
    }

}


