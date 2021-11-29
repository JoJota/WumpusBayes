package wumpusworld.MyAgent;

import wumpusworld.*;

import java.util.ArrayList;
import java.util.List;

import static wumpusworld.MyAgent.BoardProbabilities._pitProbability;
import static wumpusworld.MyAgent.Common.*;

/**
 * This class uses a Naive Bayes approach to calculate the probabilities for pits at all the positions in the frontier
 */
public class NaiveBayes {

    //region private variables

    private static World _world;
    private static FieldProbability[][] _calcProbabilities;
    private static List<Double> _pitFrontierValues;

    //endregion

    //region public methods

    public static void Init(World world) {
        _world = world;
        _pitFrontierValues = new ArrayList<>();
    }

    /**
     * calculates all the new probabilities for the values in the frontier
     */
    public static void calculateNewProbabilities() {
        int size = BoardProbabilities.GetBoardSize();

        Point p = new Point(_world.getPlayerX(), _world.getPlayerY());
        setNeighborPitProbabilities(p);
        setNeighborWumpusProbabilities(p);

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
            BoardProbabilities.set_pitProbability(point.x, point.y, p_prob);
        }
    }

    /**
     * checks if there is a breeze at the @point
     * if there is no breeze, all the neighbors of this point cant be pits
     * so the pit probability is set to 0
     */
    private static void setNeighborPitProbabilities(Point point) {
        if (!_world.hasBreeze(point.x, point.y)) {
            Point myPoint = new Point(point.x - 1, point.y - 1);
            List<Point> neighbors = getNeighbors(myPoint, _world);
            for (Point neighbor : neighbors) {
                BoardProbabilities.set_pitProbability(neighbor.x, neighbor.y, 0);
            }
        }
    }

    /**
     * checks if there is a stench at the @point
     * if there is no breeze, all the neighbors of this point cant be pits
     * so the wumpus probability is set to 0
     */
    private static void setNeighborWumpusProbabilities(Point point) {
        if (!_world.hasStench(point.x, point.y)) {
            Point myPoint = new Point(point.x - 1, point.y - 1);
            List<Point> neighbors = getNeighbors(myPoint, _world);
            for (Point neighbor : neighbors) {
                BoardProbabilities.set_wumpusProbability(neighbor.x, neighbor.y, 0);
            }
        }
    }

    //endregion

    //region calculations

    /**
     * Naive Bayes is used to calculate the new pit probability for a point
     * @param point a position in the wumpus world
     * @return the new probability
     */
    private static double calculateNewPitProb(Point point) {
        List<Point> newFrontier = deepCopy(BoardProbabilities.get_frontier());
        newFrontier.remove(point);
        _calcProbabilities = BoardProbabilities.GetDeepCopy();

        //get values for point = pit
        double sum_pit = 0;
        double sum_noPit = 0;

        if (couldBePit(point)) {
            _calcProbabilities[point.y][point.x].setPit_prob(1);
            setPitFrontierValues(deepCopy(_calcProbabilities), deepCopy(newFrontier), new ArrayList<>());
            sum_pit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();

            _pitFrontierValues.clear();

            //get values for point != pit
            if (!hasToBePit(point)) {
                _calcProbabilities[point.y][point.x].setPit_prob(0);
                setPitFrontierValues(deepCopy(_calcProbabilities), deepCopy(newFrontier), new ArrayList<>());
                sum_noPit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();
                _pitFrontierValues.clear();
            }
        }

        double pitValue = sum_pit * _pitProbability;
        double noPitValue = sum_noPit * (1 - _pitProbability);
        //alpha is used to normalize the values
        double alpha = pitValue + noPitValue;

        if (alpha == 0) {
            return 0;
        }

        return pitValue / alpha;
    }

    private static boolean couldBePit(Point point){
        List<Point> neighbors = getVisitedNeighbors(point, _world);
        boolean couldBe = true;
        for(Point neighbor : neighbors ){
            if(!_world.hasBreeze(neighbor.x + 1, neighbor.y + 1)) couldBe = false;
        }
        return couldBe;
    }

    //endregion

    //region recursion

    /**
     * this method is a big recursion
     * it is used to calculate the new pit probabilities
     * in each recursion one point in the frontier is selected
     * after that it is recursively calculated, what the probabilities are for the point to be a pit or not
     * @param pip_probability representation of the world with the pit probabilities
     * @param frontier the frontier (all the point we need to calculate a new probability for)
     * @param probabilities a list with all the probabilities of previous positions being a pit or not
     */
    private static void setPitFrontierValues(FieldProbability[][] pip_probability, List<Point> frontier, List<Double> probabilities) {
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
        // Does the field have a breeze around it
        if (breezeAround(point)) {
            // Field has breeze around and is a pit with certainty
            if (hasToBePit(point)) {
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(0.2);
                FieldProbability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);

                // Field has breeze around it and might be a pit
            } else {
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(0.2);
                FieldProbability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);

                newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                newProbabilities = new ArrayList<>(probabilities);
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
            newProbabilities.add(1.0);
            _calcProbabilities[point.y][point.x].setPit_prob(0.0);
            FieldProbability[][] new_pit_probability = deepCopy(pip_probability);
            new_pit_probability[point.y][point.x].setPit_prob(0);

            setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities);
        }
    }

    //endregion

    //region check position

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
        for (Point breezeNeighbor : getNeighbors(point, _world)) {
            if (_world.hasBreeze(breezeNeighbor.x + 1, breezeNeighbor.y + 1)) {
                if (onlyPossiblePitAroundBreeze(breezeNeighbor, point)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean onlyPossiblePitAroundBreeze(Point breezePoint, Point pitPoint) {
        List<Point> neighbors = getNeighbors(breezePoint, _world);
        neighbors.remove(pitPoint);
        for (Point neighbor : neighbors) {
            if (canBePit(neighbor)) {
                return false;
            }
        }
        return true;
    }

    private static boolean canBePit(Point point) {
        if (_world.isVisited(point.x + 1, point.y + 1)) {
            return _world.hasPit(point.x + 1, point.y + 1);
        }

        double probability = _calcProbabilities[point.y][point.x].getPit_prob();
        return probability > 0;
    }

    //endregion
}


