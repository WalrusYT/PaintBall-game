package game;

/**
 * Class that represents a Field
 */
public interface Field {
    /**
     * Returns width of the field
     * @return width of the field
     */
    int getWidth();

    /**
     * Return height of the field
     * @return height of the field
     */
    int getHeight();

    /**
     * Adds an entity on the field
     * @param entity entity that should be added on the field
     */
    void addEntity(Entity entity);

    void addEntity(Entity entity, int x, int y);
}
