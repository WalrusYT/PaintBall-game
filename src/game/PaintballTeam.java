package game;

import game.data_structures.*;
import game.players.Player;

/**
 * Class that represents a Paintball Team
 */
public class PaintballTeam implements Team {
    /**
     * Name of the team
     */
    private final String name;
    /**
     * Array of players of the team
     */
    private final Array<Player> players = new ArrayClass<>();
    /**
     * Array of buildings of the team
     */
    private final Array<Building> buildings = new ArrayClass<>();

    /**
     * Constructs an object Team with the given name
     * @param name name of the team
     */
    public PaintballTeam(String name) {
        this.name = name;
    }

    @Override
    public boolean isEmpty() {
        return players.size() + buildings.size() == 0;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void addPlayer(Player player) {
        player.setTeam(this);
        players.insertLast(player);
    }
    @Override
    public void removePlayer(Player player) {
        players.removeAt(players.searchIndexOf(player));
        player.setTeam(null);
    }
    @Override
    public void addBuilding(Building building) {
        building.setTeam(this);
        buildings.insertLast(building);
    }
    @Override
    public void removeBuilding(Building building) {
        buildings.removeAt(buildings.searchIndexOf(building));
        building.setTeam(null);
    }

    @Override
    public SizedIterator<Building> buildings() {
        return buildings.iterator();
    }

    @Override
    public SizedIterator<Player> players() {
        return players.iterator();
    }
}
