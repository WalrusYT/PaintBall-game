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
     * @return Reference to a {@link Cell} where the player was set
     */
    Cell setPlayerAt(Player player, int x, int y);

    /**
     * Removes a {@link Player} from the field at the specified location
     * @param x Coordinate x of the location
     * @param y y Coordinate y of the location
     * @return {@code true} if the player was removed successfully, otherwise {@code false}
     */
    boolean removePlayerAt(int x, int y);

    /**
     * Sets the location of the specified building on this field<br>
     * @param building {@link Building} which will be placed on this field
     * @param x Coordinate x of the location
     * @param y Coordinate y of the location
     * @return Reference to a {@link Cell} where the building was set
     */
    Cell setBuildingAt(Building building, int x, int y);

    /**
     * Removes a {@link Building} from the field at the specified location
     * @param x Coordinate x of the location
     * @param y Coordinate y of the location
     * @return {@code true} if the building was removed successfully, otherwise {@code false}
     */
    boolean removeBuildingAt(int x, int y);

    /**
     * Gets a reference to a {@link Cell} at the specified location
     * @param x Coordinate x of the location
     * @param y Coordinate y of the location
     * @return {@link Cell} at the specified location
     */
    Cell cellAt(int x, int y);

    /**
     * Gets an {@link Iterator} over all the cells of this field
     * @return {@link Iterator} over {@link Cell}
     */
    Iterator<Cell> iterator();

    /**
     * Unit of space of the field, may contain some entities
     */
    class Cell {
        /**
         * {@link Player} located in this cell
         */
        protected Player player;
        /**
         * {@link Building} located in this cell
         */
        protected Building building;
        /**
         * Coordinates of this cell on its parent {@link Field}
         */
        protected int x, y;

        /**
         * Cells should be initialized by its parent {@link Field}
         * @param x Coordinate x of this cell on the field
         * @param y Coordinate y of this cell on the field
         */
        protected Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * @return Reference to a {@link Player} located in this cell
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Check whether this cell contains a {@link Player}
         * @return {@code true} if the reference to a player is not {@code null}, otherwise {@code false}
         */
        public boolean hasPlayer() {
            return player != null;
        }

        /**
         * @return Reference to a {@link Building} located in this cell
         */
        public Building getBuilding() {
            return building;
        }

        /**
         * Check whether this cell contains a {@link Building}
         * @return {@code true} if the reference to a building is not {@code null}, otherwise {@code false}
         */
        public boolean hasBuilding() {
            return building != null;
        }

        /**
         * @return X coordinate of this cell on its parent {@link Field}
         */
        public int getX() {
            return x;
        }

        /**
         * @return Y coordinate of this cell on its parent {@link Field}
         */
        public int getY() {
            return y;
        }
    }
}
