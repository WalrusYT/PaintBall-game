package game;

import game.data_structures.SizedIterator;
import game.players.Player;

/**
 * Interface that represents a Team
 */
public interface Team {
    /**
     * Checks if the team is empty
     * @return <code>true</code> if the team is empty and <code>false</code> otherwise
     */
    boolean isEmpty();

    /**
     * Returns name of the team
     * @return name of the team
     */
    String name();

    /**
     * Adds a player to the team
     * @param player player that should be added to the team
     */
    void addPlayer(Player player);

    /**
     * Removes a player from the team
     * @param player player that should be removed from the team
     */
    void removePlayer(Player player);

    /**
     * Adds a building to the team
     * @param building building that should be added to the team
     */
    void addBuilding(Building building);

    /**
     * Removes a building from the team
     * @param building building that should be removed from the team
     */
    void removeBuilding(Building building);

    /**
     * Returns bunkers of the team
     * @return bunkers of the team
     */
    SizedIterator<Building> buildings();

    /**
     * Returns players of the team
     * @return players of the team
     */
    SizedIterator<Player> players();
}
