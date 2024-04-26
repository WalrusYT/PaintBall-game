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
    private Team team;
    private Field field;
    private Field.Cell fieldLocation;

    /**
     * Constructs an object Bunker with the given treasury, coordinate x, coordinate y and name.
     * @param treasury Amount of coins in the bunker
     * @param x Coordinate X of the bunker
     * @param y Coordinate Y of the bunker
     * @param name Name of the bunker
     */
    public Bunker(Field field, PaintballTeam team, String name, int x, int y, int treasury) {
        this(team, name, treasury);
        this.field = field;
        fieldLocation = field.setBuildingAt(this, x, y);
    }

    public Bunker(PaintballTeam team, String name, int x, int y, int treasury) {
        this(team, name, treasury);
        fieldLocation = new Field.Cell(x, y);
    }

    public Bunker(String name, int x, int y, int treasury) {
        this(null, name, x, y, treasury);
    }

    private Bunker(PaintballTeam team, String name, int treasury) {
        this.team = team;
        this.name = name;
        this.treasury = treasury;
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
