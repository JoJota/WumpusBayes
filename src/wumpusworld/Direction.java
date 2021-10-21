package wumpusworld;

public enum Direction {
    UP(),
    RIGHT(),
    BOTTOM(),
    LEFT();

    public int x;
    public int y;

    Direction() {
    }

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
