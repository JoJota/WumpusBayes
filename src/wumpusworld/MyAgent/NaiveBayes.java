package wumpusworld.MyAgent;

import wumpusworld.*;
import wumpusworld.dijkstra.Dijkstra;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

        //get values for point = pit
        _calcProbabilities[point.y][point.x].setPit_prob(1);
        setPitFrontierValues(deepCopy(_calcProbabilities), deepCopy(newFrontier), new ArrayList<>());
        double sum_pit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();
        _pitFrontierValues.clear();

        //get values for point != pit
        _calcProbabilities[point.y][point.x].setPit_prob(0);
        setPitFrontierValues(deepCopy(_calcProbabilities), deepCopy(newFrontier), new ArrayList<>());
        double sum_noPit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();
        _pitFrontierValues.clear();

        double pitValue = sum_pit * _pitProbability;
        double noPitValue = sum_noPit * (1 - _pitProbability);
        double alpha = pitValue / noPitValue;

        return pitValue * alpha;
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
            System.out.print("has a breeze around and ");
            // Field has breeze around and is a pit with certainty
            if (hasToBePit(point, pip_probability)) {
                System.out.print("is certainly a pit\n");
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(1.0);
                FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);
                // Field has breeze around it and might be a pit
            } else {
                System.out.print("could be a pit\n");
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(_calcProbabilities[point.y][point.x].getPit_prob());
                FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);

                newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(1 - _calcProbabilities[point.y][point.x].getPit_prob());
                new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(0);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);
            }
            // Field is not a pit
        } else {
            System.out.print("is not a pit\n");
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

    private static boolean hasToBePit(Point point, FieldPropability[][] pip_probability) {
        for (Point p : getVisitedNeighbors(point, _world)) {
            if (_world.hasBreeze(p.x + 1, p.y + 1)) {
                List<Point> pitPossibilities = new ArrayList<>();
                for (Point q : getNeighbors(p, _world)) {
                    if (_world.hasPit(q.x + 1, q.y + 1) || pip_probability[q.y][q.x].getPit_prob() == 1) {
                        pitPossibilities.clear();
                        break;
                    }
                    if (!_world.isVisited(q.x + 1, q.y + 1) && pip_probability[q.y][q.x].getPit_prob() == 0.2) {
                        pitPossibilities.add(q);
                    }
                }
                if (pitPossibilities.contains(point) && pitPossibilities.size() == 1) {
                    return true;
                }
            }
        }
        return false;
    }


}

