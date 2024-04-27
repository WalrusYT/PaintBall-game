package game;

/**
 * Base entity for the game with a certain location on the {@link Field} and belonging to some {@link Team}
 */
public interface Entity {

    /**
     * The location of the entity on the field
     * @return Reference to a {@link Field.Cell} where this entity is located
     */
    Field.Cell fieldLocation();

    /**
     * Change the location of the entity on the field
     * @param field {@link Field}, where the entity will be placed
     * @param x Coordinate x of the new location
     * @param y Coordinate y of the new location
     */
    void setFieldLocation(Field field, int x, int y);

    /**
     * @return Reference to a {@link Team} that owns this entity
     */
    Team team();

    /**
     * Changes the reference to a {@link Team} that owns this entity<br>
     * This method should be called by the {@link Team} when being modified
     * @param team New owner {@link Team}
     */
    void setTeam(Team team);
}
