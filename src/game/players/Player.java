package game.players;

import game.*;
import game.data_structures.Array;
import game.data_structures.Iterator;

/**
 * Entity which can move to other locations on the field and attack other entities in various ways
 */
public abstract class Player implements Entity {

    /**
     * Reference to the player's team
     */
    protected Team team;
    /**
     * Reference to the field where the player resides
     */
    protected Field field;
    /**
     * Reference to the cell on the field where the player resides
     */
    protected Field.Cell fieldLocation;

    /**
     * Empty constructor, the initialization is handled by
     * {@link Building#createPlayer(PlayerColor)} method
     */
    public Player() {}

    /**
     * Performs an attack on the field, eliminating players
     * and seizing buildings from other teams<br>
     * Eliminated players may be removed from their respective teams and fields<br>
     * The attacker may eliminate themselves if they lose the fight
     * @return status of the attack
     */
    public abstract ActionStatus attack();

    /**
     * Moves this player in several directions, changing its position on the field<br>
     * The player may eliminate players and seize buildings from other teams<br>
     * The player may also eliminate themselves if they lose the fight
     * @param dirs {@link Array} of {@link Direction} in which the player will move
     * @return Information about players movement in the specified directions
     * in the form of {@link Iterator} over the {@link Action}
     */
    public abstract Iterator<Action> move(Array<Direction> dirs);

    /**
     * @return Player's color
     */
    public abstract PlayerColor color();

    /**
     * Gets the cost of the player
     * (used in {@link Building#createPlayer(PlayerColor)} to create a player)
     * @return Player's cost
     */
    public abstract int cost();

    /**
     * Gets an adjacent cell from the field, offset in a certain direction from the current location
     * @param dir {@link Direction} of offset
     * @return {@link Field.Cell}, adjacent to the current location<br>
     * {@code null} if the coordinates of the {@link Field.Cell} are out of bounds
     */
    protected Field.Cell nextCellInDirection(Direction dir) {
        int moveX = 0, moveY = 0;
        switch (dir) {
            case NORTH -> moveY = -1;
            case SOUTH -> moveY = 1;
            case EAST -> moveX = 1;
            case WEST -> moveX = -1;
            default -> {}
        }
        int newX = fieldLocation.getX() + moveX, newY = fieldLocation.getY() + moveY;
        if (newX <= 0 || newX > field.width() || newY <= 0 || newY > field.height())
            return null;
        return field.cellAt(newX, newY);
    }

    /**
     * Default method for moving the player in one direction<br>
     * The player may eliminate players or seize buildings from other teams<br>
     * The player may also eliminate themselves if they lose the fight
     * @param dir {@link Direction}, in which the player will move
     * @return {@link Action} with a status:<br>
     * {@link ActionStatus#INVALID_DIRECTION} if the direction is {@link Direction#INVALID}<br>
     * {@link ActionStatus#OFF_THE_MAP} if the next location is off the map<br>
     * {@link ActionStatus#POSITION_OCCUPIED} if the next location is occupied
     * by a player from the same team<br>
     * Otherwise, the {@link Action} will consist
     * of the player's updated {@link Field.Cell} location
     * and a status from the {@link #attackCell(Field.Cell)} method
     */
    protected Action moveDefault(Direction dir) {
        if (dir == Direction.INVALID) {
            return new Action(ActionStatus.INVALID_DIRECTION);
        }
        Field.Cell newLocation = nextCellInDirection(dir);
        if (newLocation == null) {
            return new Action(ActionStatus.OFF_THE_MAP);
        }
        if (newLocation.hasPlayer() && newLocation.getPlayer().team() == this.team()) {
            return new Action(ActionStatus.POSITION_OCCUPIED);
        }
        ActionStatus status = attackCell(newLocation);
        if (status == ActionStatus.PLAYER_ELIMINATED) {
            return new Action(fieldLocation, status);
        }
        field.removePlayerAt(fieldLocation.getX(), fieldLocation().getY());
        this.fieldLocation = field.setPlayerAt(this, newLocation.getX(), newLocation.getY());
        return new Action(newLocation, status);
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public Team team() {
        return team;
    }

    @Override
    public Field.Cell fieldLocation() {
        return fieldLocation;
    }

    @Override
    public void setFieldLocation(Field field, int x, int y) {
        this.field = field;
        fieldLocation = field.setPlayerAt(this, x, y);
    }

    /**
     * Performs an attack on the specified {@link Field.Cell}
     * The player may eliminate a player or seize a building from the other team<br>
     * The player may also eliminate themselves if they lose the fight<br>
     * @param cell {@link Field.Cell} where the player will perform an attack
     * @return {@link ActionStatus#NOTHING} if nothing happened during the attack<br>
     * {@link ActionStatus#PLAYER_ELIMINATED} if the attacker was eliminated during the attack<br>
     * {@link ActionStatus#WON_FIGHT} if the attacker eliminated the other player<br>
     * {@link ActionStatus#BUNKER_SEIZED} if the attacker seized the building<br>
     * {@link ActionStatus#WON_AND_SEIZED} if the attacker eliminated
     * the other player and seized the building<br>
     */
    public ActionStatus attackCell(Field.Cell cell) {
        Player defender = cell.getPlayer();
        ActionStatus status = ActionStatus.NOTHING;
        if (defender != null) {
            if (defender.team() == this.team()) return status;
            boolean wonFight = fight(defender);
            if (!wonFight) {
                field.removePlayerAt(this.fieldLocation.getX(), this.fieldLocation.getY());
                this.team().removePlayer(this);
                return ActionStatus.PLAYER_ELIMINATED;
            } else {
                field.removePlayerAt(defender.fieldLocation.getX(), defender.fieldLocation.getY());
                defender.team().removePlayer(defender);
                status = ActionStatus.WON_FIGHT;
            }
        }
        Building building = cell.getBuilding();
        if (building != null && building.team() != this.team()) {
            if (building.team() != null) building.team().removeBuilding(building);
            this.team().addBuilding(building);
            status = status == ActionStatus.WON_FIGHT ?
                ActionStatus.WON_AND_SEIZED : ActionStatus.BUNKER_SEIZED;
        }
        return status;
    }

    /**
     * Make player fight with another one (defender)
     * @param defender player, who is attacked
     * @return {@code true} if the attacker wins, and {@code false} otherwise
     */
    public abstract boolean fight(Player defender);

    /**
     * Color of the player, used in {@link Building#createPlayer(PlayerColor)}
     */
    public enum PlayerColor {

        RED, GREEN, BLUE;

        /**
         * Converts the {@link String} equivalent of the color to {@link PlayerColor}
         * @param name {@link String} of the color
         * @return {@link PlayerColor} corresponding to its {@link String} equivalent<br>
         * {@code null} if the color is invalid
         */
        public static PlayerColor fromName(String name) {
            switch (name) {
                case "green" -> { return PlayerColor.GREEN; }
                case "red" -> { return PlayerColor.RED; }
                case "blue" -> { return PlayerColor.BLUE; }
                default -> { return null; }
            }
        }
    }

    /**
     * 2D directions, used in {@link Player#move(Array)}
     */
    public enum Direction {
        NORTH, SOUTH, EAST, WEST, INVALID;

        /**
         * Converts the {@link String} equivalent of the direction to {@link Direction}
         * @param name {@link String} of the direction
         * @return {@link Direction} corresponding to its {@link String} equivalent<br>
         * {@link Direction#INVALID} if the direction is invalid
         */
        public static Direction fromName(String name) {
            switch (name) {
                case "north" -> { return Direction.NORTH; }
                case "south" -> { return Direction.SOUTH; }
                case "east" -> { return Direction.EAST; }
                case "west" -> { return Direction.WEST; }
                default -> { return Direction.INVALID; }
            }
        }
    }

    /**
     * Represents the state of the player during a {@link Player#move(Array)} in one direction
     * or during an {@link Player#attack()} on a cell
     * Contains reference to the player, its location
     * and the status {@link ActionStatus} after the action
     */
    public class Action {
        private final Field.Cell location;
        private final Player player;
        private final ActionStatus status;

        public Action(Field.Cell location, Player player, ActionStatus status) {
            this.location = location;
            this.player = player;
            this.status = status;
        }

        public Action(Field.Cell location, ActionStatus status) {
            this(location, Player.this, status);
        }

        public Action(ActionStatus status) {
            this(Player.this.fieldLocation, Player.this, status);
        }

        public Field.Cell getLocation() {
            return location;
        }

        public Player getPlayer() {
            return player;
        }

        public ActionStatus getStatus() {
            return status;
        }
    }

    /**
     * Status of the action, used to easily determine what happened during an action (move, attack)
     */
    public enum ActionStatus {
        NOTHING, WON_FIGHT, PLAYER_ELIMINATED, BUNKER_SEIZED, WON_AND_SEIZED,
        INVALID_MOVE, INVALID_DIRECTION, OFF_THE_MAP, POSITION_OCCUPIED, SURVIVED
    }
}
