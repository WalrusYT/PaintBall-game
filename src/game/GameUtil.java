package game;

import game.data_structures.Array;
import game.data_structures.ArrayClass;
import game.data_structures.Iterator;
import game.players.Player;

/**
 * Class that represents a GameUtil
 */
public class GameUtil {
    /**
     * Constants of all possible game responses
     */
    public enum GameResponse {
        INVALID_PLAYER_COLOR, INVALID_BUNKER_NAME, WRONG_TEAM_BUNKER, BUNKER_OCCUPIED, NOT_ENOUGH_COINS, PLAYER_CREATED, NO_PLAYER, INVALID_POSITION, ATTACK_SUCCESS, INVALID_DIRECTION,
        INVALID_MOVE, OFF_THE_MAP, POSITION_OCCUPIED, WON_FIGHT, PLAYER_ELIMINATED, MOVE_SUCCESS, BUNKER_SEIZED, WON_AND_SEIZED, PLAYER_NOT_FROM_TEAM, GAME_OVER, TEAM_ELIMINATED, TEAM_ELIM_AND_GAME_OVER
    }

    /**
     * Class for processing moves of the player
     */
    public static class MoveResponse {
        /**
         * Array of the moves of the player
         */
        private Array<Move> moves = new ArrayClass<>();

        /**
         * Returns the moves of the player
         * @return the moves of the player
         */
        public Array<Move> getMoves() {
            return moves;
        }

        /**
         * Constructs an object MoveResponse with the given array moves.
         * @param moves array of moves of the player
         */
        public MoveResponse(Array<Move> moves) {
            this.moves = moves;
        }

        /**
         * Constructs an object MoveResponse with the given response of the game
         * @param response response of the game
         */
        public MoveResponse(GameResponse response) {
            moves.insertLast(new Move(response));
        }

        /**
         * Returns the iterator of the array containing the moves of the player
         * @return the iterator of the array containing the moves of the player
         */
        public Iterator<Move> getMoveIterator() {
            return moves.iterator();
        }
    }

    /**
     * Subclass Move that is used for processing separate moves
     */
    public static class Move {
        /**
         * Coordinates X and Y where player moves to
         */
        public int x, y;
        /**
         * Event that happens as a result of the move
         */
        GameResponse event;
        /**
         * Color of the player who moves
         */
        Player.PlayerColor color;

        /**
         * Constructs an object Move with the given event
         * @param event Event that happens as a result of the move
         */
        public Move(GameResponse event) {
            this.event = event;
        }

        /**
         * Returns the event that happens as a result of the move
         * @return the event that happens as a result of the move
         */
        public GameResponse getEvent() {
            return event;
        }

        /**
         * Returns the color of the player who moves
         * @return the color of the player who moves
         */
        public Player.PlayerColor getColor() {
            return color;
        }

        /**
         * Constructs an object Move with the given x, y and color of the player
         * @param x X coordinate of the player who moves
         * @param y Y coordinate of the player who moves
         * @param color color of the player who moves
         */
        public Move(int x, int y, Player.PlayerColor color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.event = GameResponse.MOVE_SUCCESS;
        }
    }
}
