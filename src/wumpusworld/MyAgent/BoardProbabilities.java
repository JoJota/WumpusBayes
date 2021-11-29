package wumpusworld.MyAgent;

import wumpusworld.GUI;
import wumpusworld.Point;
import wumpusworld.World;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static wumpusworld.MyAgent.Common.deepCopy;

public class BoardProbabilities {
    //region private variables

    private static FieldProbability[][] _boardProbabilities = null;
    private static List<Point> _frontier;
    private static DecimalFormat df;
    private static boolean isInitialized = false;

    //endregion

    //region public variables

    public final static double _pitProbability = (3.0 / 15);
    public final static double _wumpusProbability = (1.0 / 15);

    //endregion

    //region initialization

    public static void Init(World world) {
        if (_boardProbabilities == null) {
            initBoard();
            _frontier = new ArrayList<>();
        }

        WumpusProbability.Init(world);
        NaiveBayes.Init(world);
    }

    private static void initBoard() {
        _boardProbabilities = new FieldProbability[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                FieldProbability prob = new FieldProbability(_wumpusProbability, _pitProbability);
                prob.calculateDanagerProbability();

                _boardProbabilities[i][j] = prob;
            }
        }
        _boardProbabilities[0][0] = new FieldProbability(0, 0);
        isInitialized = true;
    }

    public static void Reset() {
        _boardProbabilities = null;
        isInitialized = false;
    }

    //endregion

    //region public methods

    public static void AddPointToFrontier(int x, int y) {
        Point p = new Point(x, y);
        if (!_frontier.contains(p)) {
            _frontier.add(p);
        }
    }

    public static void CalculateNewProbabilities() {
        //printDebuggingInfo();
        NaiveBayes.calculateNewProbabilities();
        WumpusProbability.calculateNewProbabilities();
    }

    /**
     * @return the point in the frontier with the lowest danger value
     */
    public static Point GetNextPosition() {
        double minDanger = Double.POSITIVE_INFINITY;
        int x = 0;
        int y = 0;
        if( _frontier.size() == 1){
            x = _frontier.get(0).x;
            y = _frontier.get(0).y;
        }else
            for (Point p : _frontier) {
                double danger = getDangerProbability(p.x, p.y);
                if (danger < minDanger) {
                    minDanger = danger;
                    x = p.x;
                    y = p.y;
                }
            }

        Point res = new Point(x + 1, y + 1);
        _frontier.remove(new Point(x, y));

        //You can use this to visualize the probabilities on the board
        //GUI.SetBoardProbabilities(_boardProbabilities);

        return res;
    }

    //endregion

    //region getter and setter

    public static void set_wumpusProbability(int X, int Y, double wumpusProbability) {
        _boardProbabilities[X][Y].setWumpus_prob(wumpusProbability);
    }

    public static void set_pitProbability(int X, int Y, double pitProbability) {
        if (pitProbability != 0 && pitProbability != 1) {
            int x = 3;
        }

        _boardProbabilities[X][Y].setPit_prob(pitProbability);
    }

    public static double get_wumpusProbability(int X, int Y) {
        return _boardProbabilities[X][Y].getWumpus_prob();
    }

    public static List<Point> get_frontier() {
        return _frontier;
    }

    public static int GetBoardSize() {
        return _boardProbabilities.length;
    }

    public static boolean IsInitialized() {
        return isInitialized;
    }

    public static FieldProbability[][] GetDeepCopy() {
        return deepCopy(_boardProbabilities);
    }

    //endregion

    //region private methods

    private static double getDangerProbability(int x, int y) {
        FieldProbability fieldProbability = _boardProbabilities[x][y];
        return fieldProbability.getDanger_prob();
    }

    //endregion

    //region debugging

    /**
     * this method is used for debugging
     */
    private static void printDebuggingInfo() {
        df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(3);
        df.setMaximumFractionDigits(3);

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

    private static void printWumpusProbabilities() {
        for (int i = _boardProbabilities.length - 1; i >= 0; i--) {
            for (int j = 0; j < _boardProbabilities.length; j++) {
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

    //endregion


}
