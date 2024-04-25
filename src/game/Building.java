package game;

/**
 * Interface representing a Building
 */
public interface Building {
    /**
     * Executes the instructions that happens in the game at the end of the turn
     */
    void endTurn();

    /**
     * Returns the name of the bunker
     * @return the name of the bunker
     */
    String getName();

    /**
     * Sets the name of the bunker
     * @param name the name of the bunker
     */
    void setName(String name);


}
