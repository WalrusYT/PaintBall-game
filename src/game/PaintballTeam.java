package game;

import game.data_structures.*;
import game.players.Player;

/**
 * Class that represents a Team
 */
public class PaintballTeam implements Team {
    /**
     * Name of the team
     */
    private final String name;
    private final Array<Player> players = new ArrayClass<>();
    private final Array<Building> buildings = new ArrayClass<>();

    /**
     * Constructs an object Team with the given name
     * @param name name of the team
     */
    public PaintballTeam(String name) {
        this.name = name;
    }

    /**
     * Checks if the team is empty
     * @return <code>true</code> if the team is empty and <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return players.size() + buildings.size() == 0;
    }

    /**
     * Returns name of the team
     * @return name of the team
     */
    public String name() {
        return name;
    }

    public void addPlayer(Player player) {
        player.setTeam(this);
        players.insertLast(player);
    }

    public void removePlayer(Player player) {
        players.removeAt(players.searchIndexOf(player));
        player.setTeam(null);
    }

    public void addBuilding(Building building) {
        building.setTeam(this);
        buildings.insertLast(building);
    }

    public void removeBuilding(Building building) {
        buildings.removeAt(buildings.searchIndexOf(building));
        building.setTeam(null);
    }

    /**
     * Returns bunkers of the team
     * @return bunkers of the team
     */
    public SizedIterator<Building> buildings() {
        return buildings.iterator();
    }

    /**
     * Returns players of the team
     * @return players of the team
     */
    public SizedIterator<Player> players() {
        return players.iterator();
    }
}
