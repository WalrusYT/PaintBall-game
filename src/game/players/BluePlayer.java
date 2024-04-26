package game.players;

import game.data_structures.*;
import game.Field;

public class BluePlayer extends Player {

    public BluePlayer() {}
    
    @Override
    public ActionStatus attack() {
        int x = fieldLocation().getX(), y = fieldLocation.getY();
        for (int i = 1; i < field.width(); i++) {
            int offset = (i - 1) / 2 + 1;
            Field.Cell cellToAttack;
            if (x + offset > field.width()) {
                cellToAttack = field.cellAt(field.width() - i, y);
            } else if (x - offset <= 0) {
                cellToAttack = field.cellAt(i + 1, y);
            } else {
                if (i % 2 != 0) { offset = -offset; }
                cellToAttack = field.cellAt(x + offset, y);
            }
            if (attackCell(cellToAttack) == ActionStatus.PLAYER_ELIMINATED) return ActionStatus.PLAYER_ELIMINATED;
        }
        return ActionStatus.SURVIVED;
    }

    @Override
    public Iterator<Action> move(Array<Direction>  dirs) {
        Array<Action> actions = new ArrayClass<>();
        if (dirs.size() != 1)
            actions.insertLast(new Action(ActionStatus.INVALID_MOVE));
        else
            actions.insertLast(moveDefault(dirs.get(0)));
        return actions.iterator();
    }

    @Override
    public PlayerColor color() {
        return PlayerColor.BLUE;
    }

    @Override
    public int cost() {
        return 2;
    }
}
