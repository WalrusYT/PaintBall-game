package game;

import game.players.*;
import game.players.Player.PlayerColor;

/**
 * Class that represents a Bunker
 */
public class Bunker implements Building {
    /**
     * Amount of coins in the bunker
     */
    private int treasury;
    /**
     * Name of the bunker
     */
    private final String name;
    /**
     * Reference to the building's team
     */
    private Team team;
    /**
     * Reference to the field where the building is located
     */
    private final Field field;
    /**
     * Reference to the cell on the field where the building is located
     */
    private Field.Cell fieldLocation;

    /**
     * Constructs an object Bunker with the given treasury, coordinate x, coordinate y and name.
     * @param field {@link Field} where the bunker will be located
     * @param treasury Amount of coins in the bunker
     * @param x Coordinate X of the bunker
     * @param y Coordinate Y of the bunker
     * @param name Name of the bunker
     */
    public Bunker(Field field, String name, int x, int y, int treasury) {
        this.treasury = treasury;
        this.name = name;
        this.field = field;
        fieldLocation = field.setBuildingAt(this, x, y);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int treasury() {
        return treasury;
    }
    
    @Override
    public void endTurn(){
        treasury++;
    }

    @Override
    public CreateStatus createPlayer(PlayerColor color) {
        if (this.fieldLocation().hasPlayer()) return CreateStatus.OCCUPIED;
        Player player = null;
        switch (color) {
            case RED -> player = new RedPlayer();
            case BLUE -> player = new BluePlayer();
            case GREEN -> player = new GreenPlayer();
        }
        if (player.cost() > treasury) return CreateStatus.NOT_ENOUGH_MONEY;
        treasury -= player.cost();
        player.setFieldLocation(field, fieldLocation().getX(), fieldLocation().getY());
        this.team().addPlayer(player);
        return CreateStatus.OK;
    }

    @Override
    public Field.Cell fieldLocation() {
        return fieldLocation;
    }

    @Override
    public void setFieldLocation(Field field, int x, int y) {
        fieldLocation = field.setBuildingAt(this, x, y);
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public Team team() {
        return team;
    }
}
