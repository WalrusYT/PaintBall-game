package game;

import game.data_structures.Array;
import game.data_structures.Iterator;
import game.players.Direction;
import game.players.Player;
import game.GameUtil.*;

/**
 * Interface that represents a Game
 */
public interface Game {
    /**
     * Creates a player in the game
     * @param color color of the player
     * @param bunkerName name of the bunker of the player
     * @return response either the player was successfully created
     */
    GameResponse createPlayer(Player.PlayerColor color, String bunkerName);

    /**
     * Checks if the game is over
     * @return <code>true</code> if the game is over and <code>false</code> otherwise
     */
    boolean isGameOver();

    /**
     * Moves game to the next turn
     */
    void nextTurn();

    /**
     * Moves player
     * @param x X coordinate of the player
     * @param y Y coordinate of the player
     * @param directions directions (from 1 to 3) where player should be moved
     * @return the iterator of the moves array
     */
    Iterator<Move> movePlayerAtAt(int x, int y, Array<Direction> directions);

    
}
