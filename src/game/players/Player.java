package game.players;

import game.Entity;
import game.data_structures.Iterator;
import game.PaintballField;

public abstract class Player extends Entity implements Movable {
    public Player(int x, int y, String team) {
        super(x, y, team);
    }

    public abstract Iterator<PaintballField.Cell> attack(PaintballField field);

    public abstract PlayerColor getColor();
    @Override
    public void move(Direction dir) {
        switch (dir) {
            case NORTH -> y -= 1;
            case SOUTH -> y += 1;
            case EAST -> x += 1;
            case WEST -> x -= 1;
        }
    }

    public enum PlayerColor {

        RED(4, 3, "red"), GREEN(2, 1, "green"), BLUE(2, 1, "blue"), NONE(0, 0, "colorless");
        
        private final int cost;
        private final int maxMoves;
        private final String name;

        PlayerColor(int cost, int maxMoves, String name) {
            this.cost = cost;
            this.maxMoves = maxMoves;
            this.name = name;
        }

        public int getCost() {
            return cost;
        }

        public int getMaxMoves() {
            return maxMoves;
        }

        public String getName() {
            return name;
        }

        public static PlayerColor fromName(String name) {
            PlayerColor color = NONE;
            switch (name){
                case "green" -> color = PlayerColor.GREEN;
                case "red" -> color = PlayerColor.RED;
                case "blue" -> color = PlayerColor.BLUE;
            }
            return color;
        }
    }


}
