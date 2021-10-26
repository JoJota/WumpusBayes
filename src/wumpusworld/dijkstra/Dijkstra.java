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
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                if(!(i == end.getX() && j == end.getY()) && !(i == start.getX() && j == start.getY())){
                    if(world.isVisited(i, j) && !(world.hasWumpus(i,j)&&!(world.hasPit(i,j)))) {
                        unvisited.add(new DijkstraPoint(i, j, Integer.MAX_VALUE, null, -1));
                    }
                }
            }
        }

        while(unvisited.size()!=0){
            DijkstraPoint current = getLowestCostPoint(unvisited);
            unvisited.remove(current);
            visited.add(current);
            if(current == end) break;
            //find neighbours and update their cost and the previous Point
            for(DijkstraPoint dijkstraPoint: unvisited){
                if(dijkstraPoint.getY() == current.getY()){
                    if(dijkstraPoint.getX() == current.getX()+1){
                        if(dijkstraPoint.getCost() > current.getCost()+1){
                            dijkstraPoint.setCost(current.getCost()+1);
                            dijkstraPoint.setPrevious(current);
                            List<String> list = new ArrayList<>(current.getMoves());
                            list.addAll(getNewMoves(World.DIR_RIGHT, current.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_RIGHT);
                        }
                    }else if(dijkstraPoint.getX() == current.getX()-1){
                        if(dijkstraPoint.getCost() > current.getCost()+1){
                            dijkstraPoint.setCost(current.getCost()+1);
                            dijkstraPoint.setPrevious(current);
                            List<String> list = new ArrayList<>(current.getMoves());
                            list.addAll(getNewMoves(World.DIR_LEFT, current.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_LEFT);
                        }
                    }
                }else if(dijkstraPoint.getX() == current.getX()){
                    if(dijkstraPoint.getY() == current.getY() +1){
                        if(dijkstraPoint.getCost() > current.getCost()+1){
                            dijkstraPoint.setCost(current.getCost()+1);
                            dijkstraPoint.setPrevious(current);
                            List<String> list = new ArrayList<>(current.getMoves());
                            list.addAll(getNewMoves(World.DIR_UP, current.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_UP);
                        }
                    }else if(dijkstraPoint.getY() == current.getY()-1){
                        if(dijkstraPoint.getCost() > current.getCost()+1){
                            dijkstraPoint.setCost(current.getCost()+1);
                            dijkstraPoint.setPrevious(current);
                            List<String> list = new ArrayList<>(current.getMoves());
                            list.addAll(getNewMoves(World.DIR_DOWN, current.getDirection()));
                            dijkstraPoint.setMoves(list);
                            dijkstraPoint.setDirection(World.DIR_DOWN);
                        }
                    }
                }
            }
        }
        return res;
    }


    private static List<String> getNewMoves(int move_dir, int dirCurrent ){
        List<String> moves = new ArrayList<>();
        if (dirCurrent == World.DIR_UP) {
            if (move_dir == World.DIR_RIGHT) {
                System.out.println("I am moving right");
                moves.add(World.A_TURN_RIGHT);
            } else if (move_dir == World.DIR_LEFT) {
                System.out.println("I am moving left");
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_DOWN) {
                System.out.println("I am moving down");
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            } else {
                System.out.println("I am moving up");
            }
        } else if (dirCurrent == World.DIR_RIGHT) {
            if (move_dir == World.DIR_UP) {
                moves.add(World.A_TURN_LEFT);
                System.out.println("I am moving up");
            } else if (move_dir == World.DIR_LEFT) {
                System.out.println("I am moving left");
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_DOWN) {
                System.out.println("I am moving down");
                moves.add(World.A_TURN_RIGHT);
            } else {
                System.out.println("I am moving right");
            }
        } else if (dirCurrent == World.DIR_DOWN) {
            if (move_dir == World.DIR_UP) {
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
                System.out.println("I am moving up");
            } else if (move_dir == World.DIR_LEFT) {
                System.out.println("I am moving left");
                moves.add(World.A_TURN_RIGHT);
            } else if (move_dir == World.DIR_RIGHT) {
                System.out.println("I am moving right");
                moves.add(World.A_TURN_LEFT);
            } else {
                System.out.println("I am moving down");
            }
        } else {
            if (move_dir == World.DIR_UP) {
                moves.add(World.A_TURN_RIGHT);
                System.out.println("I am moving up");
            } else if (move_dir == World.DIR_RIGHT) {
                System.out.println("I am moving right");
                moves.add(World.A_TURN_LEFT);
                moves.add(World.A_TURN_LEFT);
            } else if (move_dir == World.DIR_DOWN) {
                System.out.println("I am moving down");
                moves.add(World.A_TURN_LEFT);
            } else {
                System.out.println("I am moving left");
            }
        }
        moves.add(World.A_MOVE);
        return moves;
    }

    private static DijkstraPoint getLowestCostPoint(Set<DijkstraPoint> unvisited) {
        DijkstraPoint lowest = null;
        int lowestCost = Integer.MAX_VALUE;

        for(DijkstraPoint dijkstraPoint: unvisited){
            if(dijkstraPoint.getCost() < lowestCost){
                lowestCost = dijkstraPoint.getCost();
                lowest = dijkstraPoint;
            }
        }
        return lowest;
    }
}
