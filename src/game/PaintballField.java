package game;

import game.players.Player;
import game.data_structures.Iterator;

/**
 * Class that represents a Paintball Field
 */
public class PaintballField implements Field {
    /**
     * Width of the field
     */
    private final int width;
    /**
     * Height of the field
     */
    private final int height;
    /**
     * Double array of objects of the type Cell
     */
    private final Cell[][] cells;

    /**
     * Constructs an object Paintballfield with the given width and height
     * @param width Width of the field
     * @param height Height of the field
     */
    public PaintballField(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                cells[i][j] = new Cell();
    }
    @Override
    public int getWidth (){
        return width;
    }
    @Override
    public int getHeight(){
        return height;
    }
    @Override
    public void addEntity(Entity entity) {
        Cell cell = cells[entity.getY() - 1][entity.getX() - 1];
        if (entity instanceof Player player) cell.player = player;
        else if (entity instanceof Bunker bunker) cell.bunker = bunker;
    }

    /**
     * Adds entity to the field
     * @param entity entity that should be added on the field
     * @param x coordinate X of the entity
     * @param y coordinate Y of the entity
     */
    public void addEntity(Entity entity, int x, int y) {
        entity.setX(x); entity.setY(y);
        this.addEntity(entity);
    }

    /**
     * Removes a player from the field
     * @param x X coordinate of the player
     * @param y Y coordinate of the player
     */
    public void removePlayer(int x, int y) {
        getCell(x, y).player = null;
    }

    /**
     * Moves player from old cell to the new one
     * @param x X coordinate of the old cell
     * @param y Y coordinate of the old cell
     * @param newX X coordinate of the new cell
     * @param newY Y coordinate of the new cell
     */
    public void movePlayerFromTo(int x, int y, int newX, int newY) {
        getCell(newX, newY).player = getCell(x, y).player;
        removePlayer(x, y);
    }

    /**
     * Returns a cell with the coordinate X, Y
     * @param x coordinate X of the cell
     * @param y coordinate Y of the cell
     * @return a cell with the coordinate X, Y
     */
    public Cell getCell(int x, int y){
        return cells[y - 1][x - 1];
    }

    /**
     * Returns an iterator of the field
     * @return an iterator of the field
     */
    public FieldIterator getIterator() { return new FieldIterator(this); }

    /**
     * Subclass of the cell of the field
     */
    public static class Cell{
        /**
         * Player that exists on the Cell
         */
        protected Player player;
        /**
         * Bunker that exists on the Cell
         */
        protected Bunker bunker;

        /**
         * Returns a player that is on the cell
         * @return a player that is on the cell
         */
        public Player getPlayer() { return player; }

        /**
         * Returns a bunker that is on the cell
         * @return a bunker that is on the cell
         */
        public Bunker getBunker() { return bunker; }
    }

    /**
     * Subclass of the iterator of the field
     */
    public static class FieldIterator implements Iterator<Cell> {
        /**
         * Field where the iterator goes in
         */
        PaintballField field;
        /**
         * Counter of the iterator
         */
        int i = -1;

        /**
         * Constructs an object FieldIterator with the given field
         * @param field field of the iterator
         */
        private FieldIterator(PaintballField field) { this.field = field; }

        /**
         * Checks if there are more elements in the collection to iterate over.
         * @return <code>true</code> if there are more elements, <code>false</code> otherwise
         */
        public boolean hasNext() { return i < field.width * field.height - 1; }

        /**
         * Returns the next element in the collection.
         * @return the next element in the collection
         * @pre hasNext()
         */
        public Cell next() {
            i++;
            return field.cells[i / field.width][i % field.width];
        }
    }
}
