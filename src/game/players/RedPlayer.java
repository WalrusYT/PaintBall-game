package game.players;

import game.data_structures.*;
import game.Field;

/**
 * Red variant of a {@link Player}<br>
 * Moves up to three directions at a time
 * Attacks every cell in a rectangular area from its position to the bottom right cell
 */
public class RedPlayer extends Player {

    public RedPlayer() {}

    @Override
    public ActionStatus attack() {
        int x = fieldLocation.getX(), y = fieldLocation().getY();
        int attackWidth = field.width() - x + 1, attackHeight = field.height() - y + 1;
        for (int i = 1; i < attackWidth * attackHeight; i++) {
            Field.Cell cellToAttack = field.cellAt(x + i % attackWidth, y + i / attackWidth);
            if (attackCell(cellToAttack) == ActionStatus.PLAYER_ELIMINATED) return ActionStatus.PLAYER_ELIMINATED;
        }
        return ActionStatus.SURVIVED;
    }

    @Override
    public Iterator<Action> move(Array<Direction>  dirs) {
        Array<Action> actions = new ArrayClass<>();
        if (dirs.size() < 1 || dirs.size() > 3) {
            actions.insertLast(new Action(ActionStatus.INVALID_MOVE));
            return actions.iterator();
        }
        for (int i = 0; i < dirs.size(); i++) {
            Action action = moveDefault(dirs.get(i));
            actions.insertLast(action);
            if (action.getStatus() == ActionStatus.PLAYER_ELIMINATED) break;
        }
        return actions.iterator();
    }

    @Override
    public PlayerColor color() {
        return PlayerColor.RED;
    }

    @Override
    public int cost() {
        return 4;
    }
}
