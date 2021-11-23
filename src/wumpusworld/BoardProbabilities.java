package wumpusworld;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoardProbabilities {
    private static FieldPropability[][] _boardProbabilities = null;
    private static FieldPropability[][] _calcProbabilities = null;
    private static FieldPropability[][] _wumpusProbabilities = null;
    private static List<Point> _frontier;
    private final static double _pitProbability = (3.0 / 15);
    private final static double _wumpusProbability = (1.0 / 15);
    private static List<Double> _pitFrontierValues;
    private static World _world;
    private static DecimalFormat df;

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
        df = new DecimalFormat("###.##");
        df.setMinimumFractionDigits(5);
        df.setMaximumFractionDigits(5);
    }

    private static void initBoard() {
        _boardProbabilities = new FieldPropability[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                FieldPropability prob = new FieldPropability(j, i);
                prob.setPit_prob(3.0 / 15);
                prob.setWumpus_prob(1.0 / 15);
                prob.calculateDanagerProbability();

                _boardProbabilities[i][j] = prob;
            }
        }
        _boardProbabilities[0][0] = new FieldPropability(0, 0);
    }

    public static void addPointToFrontier(int x, int y) {
        Point p = new Point(x, y);
        if (!_frontier.contains(p)) {
            _frontier.add(p);
        }
    }

    public static void calculateNewProbabilities(World world) {
        _world = world;
        for (Point point : _frontier) {
            double p_prob = 0;
            if (breezeAround(point)) {
                p_prob = calculateNewPitProb(point);
            }
            FieldPropability fieldPropability = new FieldPropability(p_prob);
            _boardProbabilities[point.y][point.x] = fieldPropability;
        }

        if (_world.wumpusAlive()) {
            calculateNewWumpusProb();
        } else {
            for (int x = 0; x < _boardProbabilities.length; x++) {
                for (int y = 0; y < _boardProbabilities.length; y++) {
                    _boardProbabilities[y][x].setWumpus_prob(0);
                }
            }
        }

        System.out.println("Pit Prob");
        printPitProbabilities();
        System.out.println("Wumpus Prob");
        printWumpusProbabilities();
        System.out.println("Danger Prob");
        printDangerProbabilities();
    }

    private static void printPitProbabilities() {
        for (int i = _boardProbabilities.length - 1; i >= 0; i--) {
            for (int j = 0; j < _boardProbabilities[0].length; j++) {
                Point p = new Point(j, i);
                if (_frontier.contains(p)) {
                    System.out.print("[" + _boardProbabilities[i][j].getPit_prob() + "]");
                } else {
                    System.out.print(_boardProbabilities[i][j].getPit_prob());
                }
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static void printWumpusProbabilities() {
        for (int i = _boardProbabilities.length - 1; i >= 0; i--) {
            for (int j = 0; j < _boardProbabilities[0].length; j++) {
                Point p = new Point(j, i);
                if (_frontier.contains(p)) {
                    System.out.print("[" + df.format(_boardProbabilities[i][j].getWumpus_prob()) + "]");
                } else {
                    System.out.print(df.format(_boardProbabilities[i][j].getWumpus_prob()));
                }
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static void printDangerProbabilities() {
        for (int i = _boardProbabilities.length - 1; i >= 0; i--) {
            for (int j = 0; j < _boardProbabilities[0].length; j++) {
                Point p = new Point(j, i);
                if (_frontier.contains(p)) {
                    System.out.print("[" + df.format(_boardProbabilities[i][j].getDanger_prob()) + "]");
                } else {
                    System.out.print(df.format(_boardProbabilities[i][j].getDanger_prob()));
                }
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    private static void calculateNewWumpusProb() {
        // TODO wumpus probability ist kleiner bei stench in der Nähe als normal (map 4)
        // TODO calculateNewWumpusProb methode umschreiben, sodass er nur von einem wumpus ausgeht -> ES GIBT NUR EINEN WUMPUS (map 1)
        // TODO Wumpus tot machen
        //double sum = 0;
        //return sum * (1.0 / 15); //1/15 = wumpus probability
        setWumpusFrontierValues();
        _wumpusProbabilities = deepCopy(_boardProbabilities);
    }

    private static double calculateNewPitProb(Point point) {
        System.out.println("\nCalculatePitProb for point: " + point.x + "/" + point.y + " ");
        List<Point> newFrontier = new ArrayList<>(_frontier);
        newFrontier.remove(point);
        //System.out.println("Pit");

        _calcProbabilities = deepCopy(_boardProbabilities);
        for (int i = _calcProbabilities.length - 1; i >= 0; i--) {
            for (int j = 0; j < _calcProbabilities[0].length; j++) {
                if (_calcProbabilities[i][j].getPit_prob() != 0 && _calcProbabilities[i][j].getPit_prob() != 1) {
                    _calcProbabilities[i][j].setPit_prob(_pitProbability);
                    //_calcProbabilities[i][j].setDanger_prob(_pitProbability);
                }
            }
        }

        _calcProbabilities[point.y][point.x].setPit_prob(1);

        setPitFrontierValues(deepCopy(_calcProbabilities), newFrontier, new ArrayList<>(), new ArrayList<>());
        double sum_pit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();

        List<Point> pits = new ArrayList<>();
        pits.add(point);
        _pitFrontierValues.clear();
        //System.out.println("NoPit");
        setPitFrontierValues(deepCopy(_calcProbabilities), newFrontier, new ArrayList<>(), pits);
        double sum_noPit = _pitFrontierValues.stream().mapToDouble(f -> f).sum();

        double pitValue = sum_pit * _pitProbability;
        double noPitValue = sum_noPit * (1 - _pitProbability);
        double alpha = pitValue / noPitValue;
        System.out.println("pitValue: " + pitValue + ", noPitValue: " + noPitValue + " alpha: " + alpha);

        _pitFrontierValues.clear();
        return pitValue * alpha;
    }

    //private static String prefix = "";

    private static void setPitFrontierValues(FieldPropability[][] pip_probability, List<Point> frontier, List<Double> probabilities, List<Point> pits) {
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
            //System.out.println(prefix + "FrontierVals: " + _pitFrontierValues);
            //prefix = prefix.substring(0, prefix.length() - 1);
            return;
        }

        Point point = frontier.get(0);
        //System.out.print(prefix + "Point " + point.x + "/" + point.y + " ");
        // Does the field has a breeze around it
        if (breezeAround(point)) {
            //System.out.print("has a breeze around and ");
            //System.out.println("breezeAround (" + point.x + ", " + point.y + "):" + breezeAround(point));
            // Field has breeze around and is a pit with certainty
            if (hasToBePit(point, pits, pip_probability)) {
                //System.out.print("is certainly a pit\n");
                //System.out.println("hasToBePit (" + point.x + ", " + point.y + "):" + hasToBePit(point, pits));
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(1.0);
                FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                //prefix = prefix.concat(" ");
                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities, pits);
                // Field has breeze around it and might be a pit
            } else {
                //System.out.print("could be a pit\n");
                List<Point> newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                List<Double> newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(_calcProbabilities[point.y][point.x].getPit_prob());
                FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(1);

                //System.out.println(prefix + "Assume that " + point.x + "/" + point.y + " is a pit");

                //prefix = prefix.concat(" ");
                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities, pits);

                newFrontier = new ArrayList<>(frontier);
                newFrontier.remove(point);
                newProbabilities = new ArrayList<>(probabilities);
                newProbabilities.add(1 - _calcProbabilities[point.y][point.x].getPit_prob());
                new_pit_probability = deepCopy(pip_probability);
                new_pit_probability[point.y][point.x].setPit_prob(0);

                //System.out.println(prefix + "Assume that " + point.x + "/" + point.y + " is not a pit");
                //prefix = prefix.concat(" ");
                setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities, pits);
            }
            // Field is not a pit
        } else {
            //System.out.print("is not a pit\n");
            List<Point> newFrontier = new ArrayList<>(frontier);
            newFrontier.remove(point);
            List<Double> newProbabilities = new ArrayList<>(probabilities);
            _calcProbabilities[point.y][point.x].setPit_prob(0.0);
            FieldPropability[][] new_pit_probability = deepCopy(pip_probability);
            new_pit_probability[point.y][point.x].setPit_prob(0);

            //prefix = prefix.concat(" ");
            setPitFrontierValues(new_pit_probability, newFrontier, newProbabilities, pits);
        }
        //System.out.println("");
        //prefix = "";
    }

    private static void setWumpusFrontierValues() {

        HashSet<Point> probableWumpusField = new HashSet<>();

        HashSet<Point> stenchFields = new HashSet<>();
        for (int x=0; x < _boardProbabilities.length; x++) {
            for (int y=0; y < _boardProbabilities.length; y++) {
                if (_world.hasStench(x+1, y+1)) {
                    stenchFields.add(new Point(x, y));
                }
            }
        }

        for (Point f : stenchFields) {
            /*if (stenchAround(f)) {
                List<Point> stenchNeighbors = getVisitedNeighbors(f);
                stenchNeighbors.removeIf(neighbor -> !_world.hasStench(neighbor.x + 1, neighbor.y + 1));
                for (Point sn : stenchNeighbors) {*/
            for (Point n : getUnvisitedNeighbors(f)) {
                if (hasToBeWumpus(n)) {
                    System.out.println("Point: " + n + " has to be wumpus");
                    for (int x = 0; x < _boardProbabilities.length; x++) {
                        for (int y = 0; y < _boardProbabilities.length; y++) {
                            _boardProbabilities[y][x].setWumpus_prob(0);
                        }
                    }
                    _boardProbabilities[n.y][n.x].setWumpus_prob(1);
                    return;
                }
                probableWumpusField.add(n);
            }
                /*}
            }*/
        }
        for (int x = 0; x < _boardProbabilities.length; x++) {
            for (int y = 0; y < _boardProbabilities.length; y++) {
                _boardProbabilities[y][x].setWumpus_prob(0);
            }
        }
        for (Point i : probableWumpusField) {
            _boardProbabilities[i.y][i.x].setWumpus_prob(1.0 / probableWumpusField.size());
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

    private static boolean stenchAround(Point point) {
        List<Point> neighbors = getNeighbors(point);
        for (Point p : neighbors) {
            if (_world.hasStench(p.x + 1, p.y + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasToBePit(Point point, List<Point> pits, FieldPropability[][] pip_probability) {
        for (Point p : getVisitedNeighbors(point)) {
            if (_world.hasBreeze(p.x + 1, p.y + 1)) {
                List<Point> pitPossibilities = new ArrayList<>();
                for (Point q : getNeighbors(p)) {
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

    private static boolean hasToBeWumpus(Point point) {
        for (Point p : getVisitedNeighbors(point)) {
            if (_world.hasStench(p.x + 1, p.y + 1)) {
                List<Point> wumpusPossibilities = new ArrayList<>();
                for (Point q : getNeighbors(p)) {
                    if (!_world.isVisited(q.x + 1, q.y + 1)) {
                        wumpusPossibilities.add(q);
                    }
                }
                if (wumpusPossibilities.contains(point) && wumpusPossibilities.size() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<Point> getVisitedNeighbors(Point point) {
        List<Point> neighborsvisited = getNeighbors(point);
        neighborsvisited.removeIf(n -> !_world.isVisited(n.x + 1, n.y + 1));
        return neighborsvisited;
    }

    private static List<Point> getUnvisitedNeighbors(Point point) {
        List<Point> neighborsunvisited = getNeighbors(point);
        neighborsunvisited.removeIf(n -> _world.isVisited(n.x + 1, n.y + 1));
        return neighborsunvisited;
    }

    private static List<Point> getNeighbors(Point point) {
        int x = point.x;
        int y = point.y;

        List<Point> res = new ArrayList<>();

        if (_world.isValidPosition(x - 1 + 1, y + 1)) {
            res.add(new Point(x - 1, y));
        }
        if (_world.isValidPosition(x + 1 + 1, y + 1)) {
            res.add(new Point(x + 1, y));
        }
        if (_world.isValidPosition(x + 1, y - 1 + 1)) {
            res.add(new Point(x, y - 1));
        }
        if (_world.isValidPosition(x + 1, y + 1 + 1)) {
            res.add(new Point(x, y + 1));
        }

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

    public static FieldPropability[][] deepCopy(FieldPropability[][] original) {
        FieldPropability[][] result = new FieldPropability[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                result[i][j] = original[i][j].copy();
            }
        }
        return result;
    }

    public static Point testWumpusShooting() {
        for (int i=0; i < _boardProbabilities.length; i++) {
            for (int k=0; k < _boardProbabilities.length; k++) {
                if (_boardProbabilities[i][k].getWumpus_prob() == 1) {
                    return new Point(k + 1, i + 1);
                }
            }
        }
        return null;
    }
}
