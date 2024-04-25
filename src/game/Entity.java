package game;

/**
 * Abstract class that represent an Entity
 */
public abstract class Entity {
    /**
     * Coordinates x and y of the entity
     */
    protected int x, y;
    /**
     * The name of the team that has this entity
     */
    protected String teamName;

    /**
     * Constructs an object Entity with the given coordinate x, coordinate y.
     * @param x Coordinate x of the entity
     * @param y Coordinate y of the entity
     */
    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Constructs an object Entity with the given coordinate x, coordinate y and a team name
     * @param x Coordinate x of the entity
     * @param y Coordinate y of the entity
     * @param team Name of the team of the entity
     */
    public Entity(int x, int y, String team) {
        this(x,y);
        this.teamName = team;
    }

    /**
     * Returns the name of the team that has this entity
     * @return the name of the team that has this entity
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Sets the name of the team that has this entity
     * @param team the name of the team that has this entity
     */
    public void setTeamName(String team) {
        this.teamName = team;
    }

    /**
     * Returns the X coordinate of the entity
     * @return the X coordinate of the entity
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the X coordinate of the entity
     * @param x the X coordinate of the entity
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the Y coordinate of the entity
     * @return the Y coordinate of the entity
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of the entity
     * @param y the Y coordinate of the entity
     */
    public void setY(int y) {
        this.y = y;
    }
}
