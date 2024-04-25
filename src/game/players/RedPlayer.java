package game.players;

import game.data_structures.*;
import game.PaintballField;

public class RedPlayer extends Player {
    public RedPlayer(int x, int y, String team){
        super(x, y, team);
    }

    @Override
    public Iterator<PaintballField.Cell> attack(PaintballField field) {
        return new Iterator<>() {
            int i = 0;
            final int attackWidth = field.width() - x + 1;
            final int attackHeight = field.height() - y + 1;

            public boolean hasNext() { return i < attackWidth * attackHeight - 1; }

            public PaintballField.Cell next() {
                i++;
                return field.getCell(x + i % attackWidth, y + i / attackWidth);
            }
        };
    }

    @Override
    public PlayerColor getColor() {
        return PlayerColor.RED;
    }
}
