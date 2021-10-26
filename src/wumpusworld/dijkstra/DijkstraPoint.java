package wumpusworld.dijkstra;

import java.util.ArrayList;
import java.util.List;

public class DijkstraPoint {
    private int x;
    private int y;
    private int cost;
    private DijkstraPoint previous;
    private List<String> moves = new ArrayList<>();
    private int direction;



    public DijkstraPoint(int x, int y, int cost, DijkstraPoint previous, int direction) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.previous = previous;
        this.direction = direction;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public DijkstraPoint getPrevious() {
        return previous;
    }

    public void setPrevious(DijkstraPoint previous) {
        this.previous = previous;
    }
    public void setMoves(List<String> moves){
        this.moves = moves;
    }
    public List<String> getMoves(){
        return moves;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
