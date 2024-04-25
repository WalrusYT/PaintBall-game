package game;

import game.data_structures.*;
import game.players.Player;

/**
 * Class that represents a Team
 */
public class Team {
    /**
     * Name of the team
     */
    private final String name;
    /**
     * Array of bunkers of the team
     */
    private final Array<Bunker> bunkers = new ArrayClass<Bunker>();
    /**
     * Array of players of the team
     */
    private final Array<Player> players = new ArrayClass<Player>();

    /**
     * Constructs an object Team with the given name
     * @param name name of the team
     */
    public Team(String name) {
        this.name = name;
    }

    /**
     * Checks if the team is empty
     * @return <code>true</code> if the team is empty and <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return bunkers.size() == 0 && players.size() == 0;
    }

    /**
     * Returns name of the team
     * @return name of the team
     */
    public String getName() {
        return name;
    }

    /**
     * Returns bunkers of the team
     * @return bunkers of the team
     */
    public Array<Bunker> getBunkers() {
        return bunkers;
    }

    /**
     * Returns players of the team
     * @return players of the team
     */
    public Array<Player> getPlayers() {
        return players;
    }

    /**
     * Adds bunker to the team
     * @param bunker bunker that should be added
     */
    public void addBunker(Bunker bunker) {
        bunker.setTeamName(name);
        bunkers.insertLast(bunker);
    }

    /**
     * Removes bunker from the team
     * @param bunker bunker that should be removed
     */
    public void removeBunker(Bunker bunker) {
        bunkers.removeAt(bunkers.searchIndexOf(bunker));
        bunker.setTeamName(null);
    }

    /**
     * Adds player to the tem
     * @param player player that should be added
     */
    public void addPlayer(Player player) {
        player.setTeamName(name);
        players.insertLast(player);
    }

    /**
     * Removes player from the team
     * @param player player that should be removed
     */
    public void removePlayer(Player player) {
        players.removeAt(players.searchIndexOf(player));
        player.setTeamName(null);
    }
}
