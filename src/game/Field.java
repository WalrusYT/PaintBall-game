package game;

import game.players.Player;
import game.data_structures.Iterator;

/**
 * Interface that represents a Field
 */
public interface Field {

    /**
     * @return Width of this field
     */
    int width();

    /**
     * @return Height of this field
     */
    int height();

    /**
     * Sets the location of the specified player on this field<br>
     * @param player {@link Player} which will be placed on this field
     * @param x Coordinate x of the location
     * @param y Coordinate y of the location
     * @return Reference to a {@link Field.Cell} where the player was set
     */
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
