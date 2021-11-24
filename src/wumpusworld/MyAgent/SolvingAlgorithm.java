package wumpusworld.MyAgent;

import wumpusworld.Direction;
import wumpusworld.Point;
import wumpusworld.World;

public class SolvingAlgorithm {

    private final World w;

    private final Direction UP = Direction.UP;
    private final Direction RIGHT = Direction.RIGHT;
    private final Direction BOTTOM = Direction.BOTTOM;
    private final Direction LEFT = Direction.LEFT;

    public SolvingAlgorithm(World w) {
        this.w = w;
    }

    public void calcNextMove() {
        BoardProbabilities.Init(w);
        addNewDirections();

        BoardProbabilities.CalculateNewProbabilities();
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

        addDirectionIfUnknownAndValid(RIGHT);
        addDirectionIfUnknownAndValid(UP);
        addDirectionIfUnknownAndValid(BOTTOM);
        addDirectionIfUnknownAndValid(LEFT);
    }

    private void addDirectionIfUnknownAndValid(Direction dir) {
        if (w.isUnknown(dir.x + 1, dir.y + 1) && w.isValidPosition(dir.x + 1, dir.y + 1)) {
            BoardProbabilities.AddPointToFrontier(dir.x, dir.y);
        }
    }
}
