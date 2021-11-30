package wumpusworld.MyAgent;

import wumpusworld.Agent;
import wumpusworld.GUI;
import wumpusworld.Point;
import wumpusworld.World;
import wumpusworld.dijkstra.Dijkstra;

import java.util.List;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private final World w;
    
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
        BoardProbabilities.Reset();
        GUI.ClearTextArea();
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {
        //Location of the player
        int X = w.getPlayerX();
        int Y = w.getPlayerY();
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(X, Y)) {
            grabGold();
            return;
        }

        //Basic action:
        //We are in a pit. Climb up.
        climbOutOfHoleIfPossible();

        //decide next move
        SolvingAlgorithm algo = new SolvingAlgorithm(w);
        algo.calcNextMove();

        //Basic Action
        //Shoot Wumpus if we know its position
         if (shootWumpusIfPossible()) {
             return;
         }

        //use this for debugging
        //pintDebuggingInfo(X, Y);

        //move to the new point
        moveToPoint();


    }

    /**
     * This method gets the best position to explore next(calculated with Naive Bayes)
     * Than Dijkstra is used to find the best way to get to the point
     */
    private void moveToPoint() {
        Point point = BoardProbabilities.GetNextPosition();
        List<String> moves = Dijkstra.GetShortestPath(w, point);

        GUI.AppendToTextArea("Moving from point (x=" + w.getPlayerX() + "|y=" + w.getPlayerY() + ") to point(x=" + point.x + "|y=" + point.y + ")");

        for (String move : moves) {
            w.doAction(move);
        }
    }

    /**
     * used to debug the program
     */
    private void pintDebuggingInfo(int X, int Y) {
        //Test the environment
        System.out.println("Next move, current score: " + w.getScore());

        if (w.hasBreeze(X, Y))
        {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(X, Y))
        {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(X, Y))
        {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT)
        {
            System.out.println("I am facing Right");
        }
        if (w.getDirection() == World.DIR_LEFT)
        {
            System.out.println("I am facing Left");
        }
        if (w.getDirection() == World.DIR_UP)
        {
            System.out.println("I am facing Up");
        }
        if (w.getDirection() == World.DIR_DOWN)
        {
            System.out.println("I am facing Down");
        }
    }

    /**
     * Checks if the position of the wumpus is known
     * Shoots the wumpus if the position is known
     * @return true if the wumpus was shot, false if it was not shot
     */
    private boolean shootWumpusIfPossible() {
        Point wumpus_point = WumpusProbability.testWumpusShooting();

        if (wumpus_point != null && w.hasArrow()) {
            List<String> moves = Dijkstra.GetShortestPath(w, wumpus_point);
            moves.remove(moves.size() - 1);
            for (String move : moves) {
                w.doAction(move);
            }
            GUI.AppendToTextArea("Shooting arrow at point (x=" + wumpus_point.x + "|y=" + wumpus_point.y + ")");
            w.doAction(World.A_SHOOT);
            if (!w.wumpusAlive()) {
                GUI.AppendToTextArea("You killed the Wumpus!");
            }
            else {
                GUI.AppendToTextArea("You missed the Wumpus :(");
            }
            return true;
        }
        return false;
    }

    private void grabGold() {
        w.doAction(World.A_GRAB);

        GUI.AppendToTextArea("You found the Gold !!!");
        GUI.AppendToTextArea("Congratulations, you finished this level with a score of " + w.getScore());
    }

    private void climbOutOfHoleIfPossible() {
        if (w.isInPit())
        {
            GUI.AppendToTextArea("Climbing out of the hole");
            w.doAction(World.A_CLIMB);
        }
    }
}