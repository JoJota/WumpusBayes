package wumpusworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardProbabilities {
    private static FieldPropability[][] _boardProbabilities = null;
    private static List<Point> _frontier;
    private final static double _pitProbability = (3.0/15);
    private final static double _wumpusProbability = (1.0/15);
    private static List<Double> _pitFrontierValues;
    private static World _world;

    public static double GetDangerProbability(int x, int y) {
        FieldPropability fieldPropability = _boardProbabilities[y][x];
        return fieldPropability.getDanger_prob();
    }

    public static void Init() {
        if (_boardProbabilities == null) {
            initBoard();
            _pitFrontierValues = new ArrayList<>();
            _frontier = new ArrayList<>();
        }
    }

    private static void initBoard() {
        _boardProbabilities = new FieldPropability[4][4];
        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                FieldPropability prob = new FieldPropability();
                prob.setPit_prob(3.0/15);
                prob.setWumpus_prob(1.0/15);
                prob.calculateDanagerProbability();

                _boardProbabilities[i][j] = prob;
            }
        }
        _boardProbabilities[0][0] = new FieldPropability(0, 0);
    }

    public static void AddPointToFrontier(int x, int y) {
        Point p = new Point(x, y);
        if (!_frontier.contains(p)) {
            _frontier.add(p);
        }
    }

    public static void CalculateNewProbabilities(World world) {
        _world = world;
        for (Point point : _frontier) {
            double w_prob = calculateNewWumpusProb(point);
            double p_prob = 0;
            if (breezeAround(point)) {
                p_prob = calculateNewPitProb(point);
            }

            FieldPropability fieldPropability = new FieldPropability(w_prob, p_prob);
            _boardProbabilities[point.y][point.x] = fieldPropability;
        }

        printPitProbabilities();
    }

    private static void printPitProbabilities() {
        for (int i = _boardProbabilities.length - 1;i >= 0;i--) {
            for (int j = 0;j < _boardProbabilities[0].length;j++) {
                Point p = new Point(j, i);
                if (_frontier.contains(p)) {
                    System.out.print("[" + _boardProbabilities[i][j].getPit_prob() + "]");
                }
                else {
                    System.out.print(_boardProbabilities[i][j].getPit_prob());
                }
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static double calculateNewWumpusProb(Point point) {

        double sum = 0;


        return sum * (1.0/15); //1/15 = wumpus probability
    }

    private static double calculateNewPitProb(Point point) {
        List<Point> newFrontier = new ArrayList<>(_frontier);
        newFrontier.remove(point);
        setPitFrontierValues(_boardProbabilities, newFrontier, new ArrayList<>());
        double sum_pit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();
        double sum_noPit = _pitFrontierValues.stream().mapToDouble(f -> (1 - f)).sum();

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
            }
            else {
                for (Double d : probabilities) {
                    res *= d;
                }
            }

            _pitFrontierValues.add(res);
            return;
        }

        Point point = frontier.get(0);
        frontier.remove(0);
        if (breezeAround(point)) {
            if (hasToBePit(point)) {
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                _boardProbabilities[point.y][point.x].setPit_prob(1);
                newProbabilities.add(1.0);
                FieldPropability[][] new_pip_probability = Arrays.copyOf(pip_probability, pip_probability.length);
                new_pip_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pip_probability, newFrontier, newProbabilities);
            }
            else{
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(_boardProbabilities[point.y][point.x].getPit_prob());
                FieldPropability[][] new_pip_probability = Arrays.copyOf(pip_probability, pip_probability.length);
                new_pip_probability[point.y][point.x].setPit_prob(1);

                setPitFrontierValues(new_pip_probability, newFrontier, newProbabilities);


                newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                newProbabilities = new ArrayList<>(probabilities);
                System.out.println("-------------");
                printPitProbabilities();
                System.out.println("-------------");
                newProbabilities.add(1 - _boardProbabilities[point.y][point.x].getPit_prob());
                new_pip_probability = Arrays.copyOf(pip_probability, pip_probability.length);
                new_pip_probability[point.y][point.x].setPit_prob(0);

                setPitFrontierValues(new_pip_probability, newFrontier, newProbabilities);
            }
        }
        else {
            List<Point> newFrontier = new ArrayList<>(frontier);
            newFrontier.remove(point);
            List<Double> newProbabilities = new ArrayList<>(probabilities);
            _boardProbabilities[point.y][point.x].setPit_prob(0.0);
            newProbabilities.add(1.0);
            FieldPropability[][] new_pip_probability = Arrays.copyOf(pip_probability, pip_probability.length);
            new_pip_probability[point.y][point.x].setPit_prob(0);

            setPitFrontierValues(new_pip_probability, newFrontier, newProbabilities);
        }
    }

    private static boolean breezeAround(Point point) {
        List<Point> neighbors = getNeighbors(point);
        for (Point p : neighbors) {
            if (_world.hasBreeze(p.x + 1, p.y + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasToBePit(Point point) {
        if (breezeAround(point)) {
            for (Point p : getNeighbors(point)) {
                if (_world.hasBreeze(p.x + 1, p.y + 1)) {
                    boolean res = true;
                    for (Point q : getNeighbors(p)) {
                        if (!_world.isVisited(q.x + 1, q.y + 1)) {
                            res = false;
                            break;
                        }
                        if (!_world.hasPit(q.x + 1, q.y + 1)) {
                            res = false;
                            break;
                        }
                    }
                    if (res) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static List<Point> getNeighbors(Point point) {
        int x = point.x;
        int y = point.y;

        List<Point> res = new ArrayList<>();
        res.add(new Point(x - 1, y));
        res.add(new Point(x + 1, y));
        res.add(new Point(x, y - 1));
        res.add(new Point(x, y + 1));

        return res;
    }

    public static Point GetNextPosition() {
        double minDanger = Double.POSITIVE_INFINITY;
        int x = 0;
        int y = 0;

        for (Point p : _frontier) {
            double danger = GetDangerProbability(p.x, p.y);
            if (danger < minDanger) {
                minDanger = danger;
                x = p.x;
                y = p.y;
            }
        }

        Point res = new Point(x + 1, y + 1);
        _frontier.remove(new Point(x, y));
        return res;
    }


}
