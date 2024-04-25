package game.players;

import game.data_structures.*;
import game.PaintballField;

public class GreenPlayer extends Player {
    public GreenPlayer(int x, int y, String team){
        super(x, y, team);
    }

    @Override
    public Iterator<PaintballField.Cell> attack(PaintballField field) {
        return new Iterator<PaintballField.Cell>() {
            int i = 0;
            final int[] diagLengths = new int[]{
                Math.min(x - 1, y - 1), Math.min(field.width() - x, y - 1),
                Math.min(x - 1, field.height() - y), Math.min(field.width() - x, field.height() - y)
            };
            int cells = 0;
            {
                for (int i = 0; i < diagLengths.length; i++) cells += diagLengths[i];
            }
            final int[] diagVisitedCells = new int[]{ 0, 0, 0, 0 };

            public boolean hasNext() { return i < cells; }

            public PaintballField.Cell next() {
                for (int j = 0; j < 4; j++) {
                    int dir = (i + j) % 4;
                    if (diagVisitedCells[dir] >= diagLengths[dir]) continue;
                    diagVisitedCells[dir]++;
                    int offsetX = diagVisitedCells[dir], offsetY = offsetX;
                    if (dir % 2 == 0) offsetX = -offsetX;
                    if (dir / 2 == 0) offsetY = -offsetY;
                    i++;
                    return field.getCell(x + offsetX, y + offsetY);
                }
                return null;
            }
        };
    }

    @Override
    public PlayerColor getColor() {
        return PlayerColor.GREEN;
    }
}
