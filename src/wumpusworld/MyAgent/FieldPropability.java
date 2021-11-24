package wumpusworld.MyAgent;

public class FieldPropability {
    private double wumpus_prob;
    private double pit_prob;
    private double danger_prob;
    private final double wumpus_weight = 5;
    private final double pit_weight = 1;

    public FieldPropability(double wumpus_prob, double pit_prob) {
        setWumpus_prob(wumpus_prob);
        setPit_prob(pit_prob);
        calculateDanagerProbability();
    }

    public double getWumpus_prob() {
        return wumpus_prob;
    }

    public void setWumpus_prob(double wumpus_prob) {
        this.wumpus_prob = wumpus_prob;
        calculateDanagerProbability();
    }

    public double getPit_prob() {
        return pit_prob;
    }

    public void setPit_prob(double pit_prob) {
        this.pit_prob = pit_prob;
        calculateDanagerProbability();
    }

    public double getDanger_prob() {
        return danger_prob;
    }

    public void calculateDanagerProbability() {
        double total_weight = wumpus_weight + pit_weight;
        this.danger_prob = ((wumpus_prob * wumpus_weight / total_weight) + (pit_prob * pit_weight / total_weight)) / 2;
    }

    public FieldPropability copy() {
        double w = wumpus_prob;
        double p = pit_prob;
        FieldPropability res = new FieldPropability(w, p);
        return res;
    }

}
