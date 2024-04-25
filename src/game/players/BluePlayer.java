package game.players;

import game.data_structures.*;
import game.PaintballField;

public class BluePlayer extends Player {
    public BluePlayer(int x, int y, String team){
        super(x, y, team);
    }
    
    @Override
    public Iterator<PaintballField.Cell> attack(PaintballField field) {
        return new Iterator<>() {
            int i = 1;
            public boolean hasNext() { return i < field.width(); }
            public PaintballField.Cell next() {
                int offset = (i - 1) / 2 + 1;
                if (x + offset > field.width()) {
                    return field.getCell(field.width() - i++, y);
                }
                if (x - offset <= 0) {
                    return field.getCell(i++ + 1, y);
                }
                if (i % 2 != 0) { offset = -offset; }
                i++;
                return field.getCell(x + offset, y);
            }
        };
    }

    @Override
    public PlayerColor getColor() {
        return PlayerColor.BLUE;
    }
}
