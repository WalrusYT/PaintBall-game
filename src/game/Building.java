package game;

import game.players.Player;
/**
 * Interface representing a Building
 */
public interface Building extends Entity {
    /**
     * Executes the instructions that happens in the game at the end of the turn
     */
    void endTurn();

    CreateStatus createPlayer(Player.PlayerColor color);

    String name();

    int treasury();

    enum CreateStatus {
        OK, NOT_ENOUGH_MONEY, OCCUPIED
    }
}
