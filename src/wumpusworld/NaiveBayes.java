package wumpusworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NaiveBayes {

    private final World w;

    private final Direction UP = Direction.UP;
    private final Direction RIGHT = Direction.RIGHT;
    private final Direction BOTTOM = Direction.BOTTOM;
    private final Direction LEFT = Direction.LEFT;

    public NaiveBayes(World w) {
        this.w = w;
    }

    public void calcMove() {
        BoardProbabilities.Init();
        addNewDirections();
        BoardProbabilities.CalculateNewProbabilities(w);
        Point newPosition = BoardProbabilities.GetNextPosition();

        List<String> moves = Dijkstra.GetShortestPath(w, newPosition);

        System.out.println("Moving to point(x={" + newPosition.x + "}|y={" + newPosition.y + "}");
        for (String move : moves) {
            w.doAction(move);
        }
    }

    private void addNewDirections() {
        int player_x = w.getPlayerX() - 1;
        int player_y = w.getPlayerY() - 1;

        UP.x = player_x;
        UP.y = player_y + 1;

        RIGHT.x = player_x + 1;
        RIGHT.y = player_y;

        BOTTOM.x = player_x;
        BOTTOM.y = player_y - 1;

        LEFT.x = player_x - 1;
        LEFT.y = player_y;

        addBorder(RIGHT);
        addBorder(UP);
        addBorder(BOTTOM);
        addBorder(LEFT);
    }

    private void addBorder(Direction dir) {
        if (w.isUnknown(dir.x + 1, dir.y + 1) && w.isValidPosition(dir.x + 1, dir.y + 1)) {
            BoardProbabilities.AddPointToFrontier(dir.x, dir.y);
        }
    }
}


