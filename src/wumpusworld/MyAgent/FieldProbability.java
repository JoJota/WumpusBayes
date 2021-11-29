package wumpusworld.MyAgent;

public class FieldProbability {
    private double wumpus_prob;
    private double pit_prob;
    private double danger_prob;
    private final double wumpus_weight = 4;
    private final double pit_weight = 1;

    /**
     * this class is used to represent all the probabilities for one position in the world
     * with the @wumpus_prob and @the pit_prob the danger of the position is calculated with a weights for both values
     * @param wumpus_prob the probability, that the wumpus is at this
     * @param pit_prob the probability, that a pit is at this position
     */
    public FieldProbability(double wumpus_prob, double pit_prob) {
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
        this.danger_prob = ((wumpus_prob * wumpus_weight) + (pit_prob * pit_weight)) / total_weight;
    }

    public FieldProbability copy() {
        double w = wumpus_prob;
        double p = pit_prob;
        return new FieldProbability(w, p);
    }

}
