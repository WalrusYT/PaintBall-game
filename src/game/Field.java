package game;

import game.players.Player;
import game.data_structures.Iterator;

/**
 * Class that represents a Field
 */
public interface Field {

    int width();

    int height();
    
    Cell setPlayerAt(Player player, int x, int y);

    boolean removePlayerAt(int x, int y);

    Cell setBuildingAt(Building building, int x, int y);

    boolean removeBuildingAt(int x, int y);

    Cell cellAt(int x, int y);

    Iterator<Cell> iterator();

    class Cell {
        protected Player player;
        protected Building building;
        protected int x, y;

        public Cell() {}

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Player getPlayer() {
            return player;
        }

        public boolean hasPlayer() {
            return player != null;
        }

        public Building getBuilding() {
            return building;
        }

        public boolean hasBuilding() {
            return building != null;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
