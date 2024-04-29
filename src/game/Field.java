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
     * @return A snapshot of the field in the form of {@link Map}
     * from the view of the specified {@link Team}
     */
    Map map(Team team);

    /**
     * @return A snapshot of the field in the form of {@link Map}
     */
    Map map();

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

    class Map {
        private final int width, height;
        private final Team team;
        private final Iterator<MapCell> mapCells;

        public Map(Field field) {
            this(field, null);
        }

        public Map(Field field, Team team) {
            this.team = team;
            this.width = field.width();
            this.height = field.height();
            this.mapCells = new Iterator<>() {
                int i = -1;

                public boolean hasNext() {
                    return i < width * height - 1;
                }

                public MapCell next() {
                    i++;
                    Cell cell = field.cellAt(i % width + 1, i / width + 1);
                    if (cell.player != null && team != cell.player.team()) return MapCell.NONE;
                    if (cell.building != null && team != cell.building.team()) return MapCell.NONE;
                    return MapCell.fromCell(cell);
                }
            };
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public Team getTeam() {
            return team;
        }

        public Iterator<MapCell> getMapCells() {
            return mapCells;
        }
    }

    enum MapCell {
        BUILDING_AND_PLAYER, BUILDING, PLAYER, NONE;

        public static MapCell fromCell(Cell cell) {
            if (cell.hasBuilding() && cell.hasPlayer()) return BUILDING_AND_PLAYER;
            if (cell.hasBuilding()) return BUILDING;
            if (cell.hasPlayer()) return PLAYER;
            return NONE;
        }
    }
}
