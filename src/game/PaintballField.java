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
                cells[i][j] = new Cell(j + 1, i + 1);
    }


    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public Cell setPlayerAt(Player player, int x, int y) {
        Cell cell = cellAt(x, y);
        cell.player = player;
        return cell;
    }

    @Override
    public boolean removePlayerAt(int x, int y) {
        boolean removed = cellAt(x, y).player != null;
        setPlayerAt(null, x, y);
        return removed;
    }

    @Override
    public Cell setBuildingAt(Building building, int x, int y) {
        Cell cell = cellAt(x, y);
        cell.building = building;
        return cell;
    }

    @Override
    public boolean removeBuildingAt(int x, int y) {
        boolean removed = cellAt(x, y).building != null;
        setBuildingAt(null, x, y);
        return removed;
    }

    /**
     * Returns a cell with the coordinate X, Y
     * @param x coordinate X of the cell
     * @param y coordinate Y of the cell
     * @return a cell with the coordinate X, Y
     */
    @Override
    public Cell cellAt(int x, int y) {
        return cells[y - 1][x - 1];
    }

    /**
     * Returns an iterator of the field
     * @return an iterator of the field
     */
    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<>() {
            int i = -1;

            public boolean hasNext() {
                return i < width * height - 1;
            }

            public Cell next() {
                i++;
                return cells[i / width][i % width];
            }
        };
    }
}
