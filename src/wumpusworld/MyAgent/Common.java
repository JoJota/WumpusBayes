package wumpusworld.MyAgent;

import wumpusworld.Point;
import wumpusworld.World;

import java.util.ArrayList;
import java.util.List;

/**
 * this class contains some general static methods that are used in several other classes
 */
public class Common {
    public static List<Point> getUnvisitedNeighbors(Point point, World world) {
        List<Point> res = getNeighbors(point, world);
        res.removeIf(n -> world.isVisited(n.x + 1, n.y + 1));
        return res;
    }

    public static List<Point> getVisitedNeighbors(Point point, World world) {
        List<Point> res = getNeighbors(point, world);
        res.removeIf(n -> !world.isVisited(n.x + 1, n.y + 1));
        return res;
    }

    public static List<Point> getNeighbors(Point point, World world) {
        int x = point.x;
        int y = point.y;

        List<Point> res = new ArrayList<>();

        if (world.isValidPosition(x - 1 + 1, y + 1)) {
            res.add(new Point(x - 1, y));
        }
        if (world.isValidPosition(x + 1 + 1, y + 1)) {
            res.add(new Point(x + 1, y));
        }
        if (world.isValidPosition(x + 1, y - 1 + 1)) {
            res.add(new Point(x, y - 1));
        }
        if (world.isValidPosition(x + 1, y + 1 + 1)) {
            res.add(new Point(x, y + 1));
        }

        return res;
    }

    public static FieldProbability[][] deepCopy(FieldProbability[][] original) {
        FieldProbability[][] result = new FieldProbability[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                result[i][j] = original[i][j].copy();
            }
        }
        return result;
    }

    public static List<Point> deepCopy(List<Point> original) {
        List<Point> result = new ArrayList<>();
        for (Point value : original) {
            Point point = value.Copy();
            result.add(point);
        }
        return result;
    }
}
