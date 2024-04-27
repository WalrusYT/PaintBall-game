package game;

import game.data_structures.Array;
import game.data_structures.Iterator;
import game.data_structures.SizedIterator;
import game.players.Player;
import game.players.Player.*;
import game.Building.CreateStatus;

/**
 * Interface that represents a Game
 */
public interface Game {

    int width();

    int height();


    GameStatus addBuilding(int x, int y, int treasury, String buildingName);
    GameStatus addTeam(String teamName, String buildingName);
    SizedIterator<Building> buildings();

    SizedIterator<Team> teams();
    /**
     * Creates a player in the game
     * @param color color of the player
     * @param bunkerName name of the bunker of the player
     * @return response either the player was successfully created
     */
    GameResponse<CreateStatus> createPlayer(Player.PlayerColor color, String bunkerName);

    /**
     * Moves player
     * @param x X coordinate of the player
     * @param y Y coordinate of the player
     * @param directions directions (from 1 to 3) where player should be moved
     * @return the iterator of the moves array
     */
    GameResponse<Iterator<Action>> movePlayerAt(int x, int y, Array<Direction> directions);

    GameResponse<Team> playersAttack();

    Iterator<Field.Cell> map();

    Team currentTeam();

    class GameResponse<T> {
        private final T result;
        private final GameStatus status;
        private final Team winner;

        public GameResponse(T result, GameStatus status, Team winner) {
            this.result = result;
            this.status = status;
            this.winner = winner;
        }

        public GameResponse(T result, GameStatus status) {
            this(result, status, null);
        }

        public GameResponse(GameStatus status) {
            this(null, status, null);
        }

        public T getResult() {
            return result;
        }

        public GameStatus getStatus() {
            return status;
        }

        public Team getWinner() {
            return winner;
        }
    }

    enum GameStatus {
        OK, TEAM_ELIMINATED, TEAM_ELIM_AND_GAME_OVER, GAME_OVER, INVALID_POSITION, NO_PLAYER, PLAYER_NOT_FROM_TEAM,
        INVALID_PLAYER_COLOR, INVALID_BUNKER_NAME, WRONG_TEAM_BUNKER, BUNKER_NOT_CREATED, TEAM_NOT_CREATED
    }
}
