package game.players;

import game.data_structures.*;
import game.Field;

/**
 * Green variant of a {@link Player}<br>
 * Moves one direction at a time
 * Attacks every cell on the diagonals it's located in, altering between them clockwise
 */
public class GreenPlayer extends Player {

    public GreenPlayer() {}

    @Override
    public ActionStatus attack() {
        int x = fieldLocation.getX(), y = fieldLocation().getY();
        int[] diagLengths = new int[]{
                Math.min(x - 1, y - 1), Math.min(field.width() - x, y - 1),
                Math.min(x - 1, field.height() - y), Math.min(field.width() - x, field.height() - y)
        };
        final int[] diagVisitedCells = new int[]{ 0, 0, 0, 0 };
        int cells = 0;
        for (int i = 0; i < diagLengths.length; i++) cells += diagLengths[i];

        for (int i = 0; i < cells; i++) {
            for (int j = 0; j < 4; j++) {
                int dir = (i + j) % 4;
                if (diagVisitedCells[dir] >= diagLengths[dir]) continue;
                diagVisitedCells[dir]++;
                int offsetX = diagVisitedCells[dir], offsetY = offsetX;
                if (dir % 2 == 0) offsetX = -offsetX;
                if (dir / 2 == 0) offsetY = -offsetY;
                Field.Cell cellToAttack = field.cellAt(x + offsetX, y + offsetY);
                if (attackCell(cellToAttack) == ActionStatus.PLAYER_ELIMINATED) return ActionStatus.PLAYER_ELIMINATED;
            }
        }
        return ActionStatus.SURVIVED;
    }

    @Override
    public Iterator<Action> move(Array<Direction> dirs) {
        Array<Action> actions = new ArrayClass<>();
        if (dirs.size() != 1)
            actions.insertLast(new Action(ActionStatus.INVALID_MOVE));
        else
            actions.insertLast(moveDefault(dirs.get(0)));
        return actions.iterator();
    }

    @Override
    public PlayerColor color() {
        return PlayerColor.GREEN;
    }

    @Override
    public int cost() {
        return 2;
    }
}
