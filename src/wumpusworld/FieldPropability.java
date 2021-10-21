package wumpusworld;

public class FieldPropability {
    private Direction dir;
    private double wumpus_prob;
    private double pit_prob;
    private double danger_prob;

    public FieldPropability(Direction dir) {
        this.dir = dir;
    }
    public FieldPropability(Direction dir, double wumpus_prob, double pit_prob, double danger_prob) {
        this.dir = dir;
        this.wumpus_prob = wumpus_prob;
        this.pit_prob = pit_prob;
        this.danger_prob = danger_prob;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public double getWumpus_prob() {
        return wumpus_prob;
    }

    public void setWumpus_prob(double wumpus_prob) {
        this.wumpus_prob = wumpus_prob;
    }

    public double getPit_prob() {
        return pit_prob;
    }

    public void setPit_prob(double pit_prob) {
        this.pit_prob = pit_prob;
    }

    public double getDanger_prob() {
        return danger_prob;
    }

    public void setDanger_prob(double danger_prob) {
        this.danger_prob = danger_prob;
    }

}
