package wumpusworld;

import wumpusworld.dijkstra.Dijkstra;

import java.util.List;

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
        BoardProbabilities.Init(w);
        addNewDirections();
        BoardProbabilities.calculateNewProbabilities();

        Point wumpus_point = BoardProbabilities.testWumpusShooting();

        if (wumpus_point != null && w.hasArrow()) {
            List<String> moves = Dijkstra.GetShortestPath(w, wumpus_point);
            moves.remove(moves.size() - 1);
            for (String move : moves) {
                w.doAction(move);
            }
            w.doAction(World.A_SHOOT);
            if (!w.wumpusAlive()) {
                System.out.println("WUMPUS IS DEAD");
                return;
            }
        }

        Point newPosition = BoardProbabilities.GetNextPosition();

        List<String> moves = Dijkstra.GetShortestPath(w, newPosition);

        System.out.println("Moving to point(x={" + newPosition.x + "}|y={" + newPosition.y + "}");

        /* if (moves.size() == 0) {
            System.out.println("Moves is empty");
            w.doAction(World.A_MOVE);
        } */

        for (String move : moves) {
            w.doAction(move);
            // System.out.println("Moves: " + move);
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
            BoardProbabilities.addPointToFrontier(dir.x, dir.y);
        }
    }
}


