package game.players;

import game.*;
import game.data_structures.Array;
import game.data_structures.Iterator;

public abstract class Player implements Entity {

    protected Team team;
    protected Field field;
    protected Field.Cell fieldLocation;

    public Player(Field field, PaintballTeam team, int x, int y) {
        this.field = field;
        this.team = team;
        fieldLocation = field.setPlayerAt(this, x, y);
    }

    public Player(int x, int y) {
        this(null, x, y);
    }

    public Player(PaintballTeam team, int x, int y) {
        this.team = team;
        fieldLocation = new Field.Cell(x, y);
    }

    public Player(PaintballTeam team) {
        this.team = team;
    }

    public Player() {}

    public abstract ActionStatus attack();

    public abstract Iterator<Action> move(Array<Direction> dirs);

    public abstract PlayerColor color();

    public abstract int cost();

    protected Field.Cell nextCellInDirection(Direction dir) {
        int moveX = 0, moveY = 0;
        switch (dir) {
            case NORTH -> moveY = -1;
            case SOUTH -> moveY = 1;
            case EAST -> moveX = 1;
            case WEST -> moveX = -1;
        }
        int newX = fieldLocation.getX() + moveX, newY =fieldLocation.getY() + moveY;
        if (newX <= 0 || newX > field.width() || newY <= 0 || newY > field.height())
            return null;
        return field.cellAt(newX, newY);
    }

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

    public ActionStatus attackCell(Field.Cell cell) {
        Player defender = cell.getPlayer();
        ActionStatus status = ActionStatus.NOTHING;
        if (defender != null) {
            if (defender.team().equals(this.team())) return status;
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
            status = status == ActionStatus.WON_FIGHT ? ActionStatus.WON_AND_SEIZED : ActionStatus.BUNKER_SEIZED;
        }
        return status;
    }

    /**
     * Make player fight with another one (defender)
     * @param defender player, who is attacked
     * @return <code>true</code> if the attacker wins, and <code>false</code> otherwise
     */
    public boolean fight(Player defender) {
        PlayerColor attackerColor = this.color();
        PlayerColor defenderColor = defender.color();
        if (attackerColor == defenderColor) return true;
        switch (attackerColor) {
            case PlayerColor.RED -> { return defenderColor == PlayerColor.BLUE; }
            case PlayerColor.BLUE -> {  return defenderColor == PlayerColor.GREEN; }
            case PlayerColor.GREEN -> { return defenderColor == PlayerColor.RED; }
            default -> { return true; }
        }
    }

    public enum PlayerColor {

        RED, GREEN, BLUE;

        public static PlayerColor fromName(String name) {
            switch (name) {
                case "green" -> { return PlayerColor.GREEN; }
                case "red" -> { return PlayerColor.RED; }
                case "blue" -> { return PlayerColor.BLUE; }
                default -> { return null; }
            }
        }
    }

    public enum Direction {
        NORTH, SOUTH, EAST, WEST, INVALID;

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

    public enum ActionStatus {
        NOTHING, WON_FIGHT, PLAYER_ELIMINATED, BUNKER_SEIZED, WON_AND_SEIZED,
        INVALID_MOVE, INVALID_DIRECTION, OFF_THE_MAP, POSITION_OCCUPIED, SURVIVED
    }
}
