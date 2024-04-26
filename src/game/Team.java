package game;

import game.data_structures.SizedIterator;
import game.players.Player;

public interface Team {

    boolean isEmpty();

    /**
     * Returns name of the team
     * @return name of the team
     */
    String name();

    void addPlayer(Player player);

    void removePlayer(Player player);

    void addBuilding(Building building);

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
