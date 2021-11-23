package wumpusworld.dijkstra;

import wumpusworld.Point;
import wumpusworld.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dijkstra {

    public static List<String> GetShortestPath(World world, Point point) {

        List<String> res = new ArrayList<>();

        Set<DijkstraPoint> visited = new HashSet<>();
        Set<DijkstraPoint> unvisited = new HashSet<>();

        DijkstraPoint start = new DijkstraPoint(world.getPlayerX(), world.getPlayerY(), 0, null, world.getDirection());
        DijkstraPoint end = new DijkstraPoint(point.x, point.y, Integer.MAX_VALUE, null, -1);
        unvisited.add(start);
        unvisited.add(end);
        //Add all explored nodes that have no Wumpus and no Pit to the unvisited list
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                if (!(i == end.getX() && j == end.getY()) && !(i == start.getX() && j == start.getY())) {
                    if (world.isVisited(i, j) && !(world.hasWumpus(i, j) && !(world.hasPit(i, j)))) {
                        unvisited.add(new DijkstraPoint(i, j, Integer.MAX_VALUE, null, -1));
                    }
                }
            }
        }

        while (unvisited.size() != 0) {
            DijkstraPoint currentPoint = getLowestCostPoint(unvisited);
            unvisited.remove(currentPoint);
            visited.add(currentPoint);
            if (currentPoint == end) break;
            //find neighbours and update their cost and the previous Point
            for (DijkstraPoint dijkstraPoint : unvisited) {
                if (dijkstraPoint.getY() == currentPoint.getY()) {
                    if (dijkstraPoint.getX() == currentPoint.getX() + 1) {
                        if (dijkstraPoint.getCost() > currentPoint.getCost() + 1) {
                            dijkstraPoint.setCost(currentPoint.getCost() + 1);
                            dijkstraPoint.setPrevious(currentPoint);
                            List<String> list = new ArrayList<>(currentPoint.getMoves());
                            list.addAll(getNewMoves(World.DIR_RIGHT, currentPoint.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_RIGHT);
                        }
                    } else if (dijkstraPoint.getX() == currentPoint.getX() - 1) {
                        if (dijkstraPoint.getCost() > currentPoint.getCost() + 1) {
                            dijkstraPoint.setCost(currentPoint.getCost() + 1);
                            dijkstraPoint.setPrevious(currentPoint);
                            List<String> list = new ArrayList<>(currentPoint.getMoves());
                            list.addAll(getNewMoves(World.DIR_LEFT, currentPoint.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_LEFT);
                        }
                    }
                } else if (dijkstraPoint.getX() == currentPoint.getX()) {
                    if (dijkstraPoint.getY() == currentPoint.getY() + 1) {
                        if (dijkstraPoint.getCost() > currentPoint.getCost() + 1) {
                            dijkstraPoint.setCost(currentPoint.getCost() + 1);
                            dijkstraPoint.setPrevious(currentPoint);
                            List<String> list = new ArrayList<>(currentPoint.getMoves());
                            list.addAll(getNewMoves(World.DIR_UP, currentPoint.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_UP);
                        }
                    } else if (dijkstraPoint.getY() == currentPoint.getY() - 1) {
                        if (dijkstraPoint.getCost() > currentPoint.getCost() + 1) {
                            dijkstraPoint.setCost(currentPoint.getCost() + 1);
                            dijkstraPoint.setPrevious(currentPoint);
                            List<String> list = new ArrayList<>(currentPoint.getMoves());
                            list.addAll(getNewMoves(World.DIR_DOWN, currentPoint.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_DOWN);
                        }
                    }
                }
            }
        }
        res = end.getMoves();
        return res;
    }


    private static List<String> getNewMoves(int move_dir, int dirCurrent) {
        List<String> moves = new ArrayList<>();
        if (dirCurrent == World.DIR_UP) {
            if (move_dir == World.DIR_RIGHT) {
                moves.add(World.A_TURN_RIGHT);
            } else if (move_dir == World.DIR_LEFT) {
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_DOWN) {
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            }
        } else if (dirCurrent == World.DIR_RIGHT) {
            if (move_dir == World.DIR_UP) {
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_LEFT) {
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_DOWN) {
                moves.add(World.A_TURN_RIGHT);
            }
        } else if (dirCurrent == World.DIR_DOWN) {
            if (move_dir == World.DIR_UP) {
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_LEFT) {
                moves.add(World.A_TURN_RIGHT);
            } else if (move_dir == World.DIR_RIGHT) {
                moves.add(World.A_TURN_LEFT);
            }
        } else {
            if (move_dir == World.DIR_UP) {
                moves.add(World.A_TURN_RIGHT);
            } else if (move_dir == World.DIR_RIGHT) {
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_DOWN) {
                moves.add(World.A_TURN_LEFT);
            }
        }
        moves.add(World.A_MOVE);
        return moves;
    }

    private static DijkstraPoint getLowestCostPoint(Set<DijkstraPoint> unvisited) {
        DijkstraPoint lowest = null;
        int lowestCost = Integer.MAX_VALUE;

        for (DijkstraPoint dijkstraPoint : unvisited) {
            if (dijkstraPoint.getCost() < lowestCost) {
                lowestCost = dijkstraPoint.getCost();
                lowest = dijkstraPoint;
            }
        }
        return lowest;
    }
}
