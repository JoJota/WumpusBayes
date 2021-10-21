package wumpusworld;

import java.util.ArrayList;
import java.util.Random;

public class NaiveBayes {

    private World w;
    private int player_x;
    private int player_y;

    private ArrayList<FieldPropability> borderFields = new ArrayList<>();
    private static Direction move_dir;
    private Direction UP = Direction.UP;
    private Direction RIGHT = Direction.RIGHT;
    private Direction BOTTOM = Direction.BOTTOM;
    private Direction LEFT = Direction.LEFT;

    private int wumpusProb = 1/15;
    private int pitProb = 3/15;

    public NaiveBayes(World w) {
        this.w = w;
    }

    public void calcMove() {
        player_x = w.getPlayerX();
        player_y = w.getPlayerY();

        UP.x = player_x;
        UP.y = player_y + 1;

        RIGHT.x = player_x + 1;
        RIGHT.y = player_y;

        BOTTOM.x = player_x;
        BOTTOM.y = player_y - 1;

        LEFT.x = player_x - 1;
        LEFT.y = player_y;

        borderFields.clear();
        addBorder(UP);
        addBorder(RIGHT);
        addBorder(BOTTOM);
        addBorder(LEFT);

        if (borderFields.isEmpty()) {
            borderFields.add(new FieldPropability(move_dir));
        }

        FieldPropability minDanger = borderFields.get(0);
        for (FieldPropability fp : borderFields) {
            if (fp.getDanger_prob() < minDanger.getDanger_prob()) {
                minDanger = fp;
            }
        }

        move_dir = minDanger.getDir();

        if (w.getDirection() == World.DIR_UP) {
            if (move_dir == RIGHT) {
                System.out.println("I am moving right");
                w.doAction(World.A_TURN_RIGHT);
            } else if (move_dir == LEFT) {
                System.out.println("I am moving left");
                w.doAction(World.A_TURN_LEFT);
            } else if (move_dir == BOTTOM) {
                System.out.println("I am moving down");
                w.doAction(World.A_TURN_LEFT);
                w.doAction(World.A_TURN_LEFT);
            } else {
                System.out.println("I am moving up");
            }
        } else if (w.getDirection() == World.DIR_RIGHT) {
            if (move_dir == UP) {
                w.doAction(World.A_TURN_LEFT);
                System.out.println("I am moving up");
            } else if (move_dir == LEFT) {
                System.out.println("I am moving left");
                w.doAction(World.A_TURN_LEFT);
                w.doAction(World.A_TURN_LEFT);
            } else if (move_dir == BOTTOM) {
                System.out.println("I am moving down");
                w.doAction(World.A_TURN_RIGHT);
            } else {
                System.out.println("I am moving right");
            }
        } else if (w.getDirection() == World.DIR_DOWN) {
            if (move_dir == UP) {
                w.doAction(World.A_TURN_LEFT);
                w.doAction(World.A_TURN_LEFT);
                System.out.println("I am moving up");
            } else if (move_dir == LEFT) {
                System.out.println("I am moving left");
                w.doAction(World.A_TURN_RIGHT);
            } else if (move_dir == RIGHT) {
                System.out.println("I am moving right");
                w.doAction(World.A_TURN_LEFT);
            } else {
                System.out.println("I am moving down");
            }
        } else {
            if (move_dir == UP) {
                w.doAction(World.A_TURN_RIGHT);
                System.out.println("I am moving up");
            } else if (move_dir == RIGHT) {
                System.out.println("I am moving right");
                w.doAction(World.A_TURN_LEFT);
                w.doAction(World.A_TURN_LEFT);
            } else if (move_dir == BOTTOM) {
                System.out.println("I am moving down");
                w.doAction(World.A_TURN_LEFT);
            } else {
                System.out.println("I am moving left");
            }
        }
        w.doAction(World.A_MOVE);
    }

    private void addBorder(Direction dir) {
        if (w.isUnknown(dir.x, dir.y) && w.isValidPosition(dir.x, dir.y)) {
            FieldPropability fp = new FieldPropability(dir);
            calcProb(fp);
            borderFields.add(fp);
        }
    }

    private void calcProb(FieldPropability fp) {
        //w.hasBreeze(fp.getDir().x, fp.getDir().y);
        //w.hasStench(fp.getDir().x, fp.getDir().y);

        //w.hasPit(fp.getDir().x, fp.getDir().y);
        //w.hasWumpus(fp.getDir().x, fp.getDir().y);

        fp.setDanger_prob(Math.random());
    }
}


