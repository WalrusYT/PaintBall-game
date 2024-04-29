package game;

import game.data_structures.Array;
import game.data_structures.Iterator;
import game.data_structures.SizedIterator;
import game.players.Player.*;
import game.Building.CreateStatus;

/**
 * Interface that represents a Game
 */
public interface Game {

    /**
     * @return Width of the underlying {@link Field}
     */
    int width();

    /**
     * @return Height of the underlying {@link Field}
     */
    int height();

    /**
     * Initializes a new {@link Field} for this game with the specified width and height
     * @param width The width of the new field
     * @param height The height of the new field
     * @return {@link GameStatus#INVALID_SIZE} if either the width or the height are less than 10<br>
     * {@link GameStatus#OK} if the field was successfully initialized
     */
    GameStatus setField(int width, int height);

    /**
     * Creates and adds a new instance of {@link Building} to the game<br>
     * Mostly used when initializing a new game
     * @param x Coordinate X of the building
     * @param y Coordinate Y of the building
     * @param treasury Initial amount of money in the building
     * @param buildingName Name of the new building
     * @return {@link GameStatus#OK} if the building was added successfully<br>
     * Otherwise, {@link GameStatus#BUNKER_NOT_CREATED} is returned
     */
    GameStatus addBuilding(int x, int y, int treasury, String buildingName);

    /**
     * Creates and adds a new instance of {@link Team} to the game,
     * containing a {@link Building} with a specified name
     * @param teamName Name of the new team
     * @param buildingName Name of some existing bunker in the game
     * @return {@link GameStatus#OK} if the team was added successfully<br>
     * Otherwise, {@link GameStatus#TEAM_NOT_CREATED} is returned
     */
    GameStatus addTeam(String teamName, String buildingName);

    /**
     * Gets all the buildings currently in this game
     * @return {@link SizedIterator} over {@link Building}
     */
    SizedIterator<Building> buildings();

    /**
     * Gets all the teams currently in this game
     * @return {@link SizedIterator} over {@link Team}
     */
    SizedIterator<Team> teams();

    /**
     * Creates a player with the specified {@link PlayerColor}
     * in a {@link Building} with the specified name
     * @param color color of the player
     * @param bunkerName name of the bunker, where the player will be created
     * @return {@link GameResponse} with a status:<br>
     * {@link GameStatus#INVALID_PLAYER_COLOR} if the color is {@code null}<br>
     * {@link GameStatus#INVALID_BUNKER_NAME} if the building with the specified name doesn't exist<br>
     * {@link GameStatus#WRONG_TEAM_BUNKER} if a team tries to create player in a building they don't own<br>
     * Otherwise, returns {@link GameStatus#OK} with the {@link CreateStatus} from the {@link Building#createPlayer(PlayerColor)} method
     */
    GameResponse<CreateStatus> createPlayer(PlayerColor color, String bunkerName);

    /**
     * Moves the player at the specified location in the specified directions<br>
     * The player may eliminate a player or seize a building from the other team<br>
     * The player may also eliminate themselves if they lose the fight<br>
     * @param x X coordinate of the player
     * @param y Y coordinate of the player
     * @param directions Directions in which the player will attempt to move
     * @return {@link GameResponse} with a status:<br>
     * {@link GameStatus#INVALID_POSITION} if the specified coordinates are out of bounds<br>
     * {@link GameStatus#NO_PLAYER} if there is no player at the specified location<br>
     * {@link GameStatus#PLAYER_NOT_FROM_TEAM} if a team tries to move a player they don't own<br>
     * Otherwise, returns {@link GameStatus#OK} with the result of the move operation, which is an {@link Iterator}
     * over all the states ({@link Action}) the player was in after moving in one direction<br>
     * May also return {@link GameStatus#GAME_OVER}, indicating that the game has ended during this method's execution
     */
    GameResponse<Iterator<Action>> movePlayerAt(int x, int y, Array<Direction> directions);

    /**
     * Forces every player of the current team to attack the field<br>
     * Players may eliminate players or seize buildings from other teams<br>
     * Players may also eliminate themselves if they lose the fight<br>
     * @return {@link GameResponse} with a status:<br>
     * {@link GameStatus#GAME_OVER} if the game has ended during this method's execution<br>
     * {@link GameStatus#TEAM_ELIMINATED} if the attacker's team has been eliminated during this method's execution<br>
     * {@link GameStatus#TEAM_ELIM_AND_GAME_OVER} if both conditions mentioned above are met at the same time<br>
     * Otherwise, returns {@link GameStatus#OK} if none of the above happened<br>
     * The result {@link Team} of {@link GameResponse} is redundant, since it contains the winner team,
     * which is also stored in the dedicated winner field of game response
     */
    GameResponse<Field.Map> playersAttack();

    Field.Map map();

    Field.Map map(Team team);

    /**
     * Gets a reference to the {@link Team} that is currently making a move
     * @return {@link Team} that currently moves
     */
    Team currentTeam();

    /**
     * Check whether the game is currently in progress or not
     * @return {@code true} if the game is in progress, otherwise {@code false}
     */
    boolean inProgress();

    /**
     * Starts the game if certain conditions are met (2 or more teams must exist)<br>
     * @return {@link GameStatus#NOT_ENOUGH_TEAMS} if the game fails to start due to there being less than 2 teams<br>
     * {@link GameStatus#OK} if the conditions are met and the game has started successfully
     */
    GameStatus start();

    /**
     * Stops the game that is currently in progress by clearing its state
     */
    void stop();

    /**
     * Represents a general response of the game, containing some status of the logic execution and
     * some result, if the execution was successful
     * @param <T> Type of the result, which is returned after a successful execution of some logic
     */
    class GameResponse<T> {
        /**
         * Result, which is returned after a successful execution of some logic
         */
        private final T result;
        /**
         * Status of the logic execution<<br>
         * Most of the time {@link GameStatus#OK} represents success and other statues - failure
         */
        private final GameStatus status;
        /**
         * Contains a reference to the winning {@link Team} in case the game has ended
         * (Usually indicated by {@link GameStatus#GAME_OVER} or {@link GameStatus#TEAM_ELIM_AND_GAME_OVER} statuses)
         */
        private final Team winner;

        /**
         * Used to specify all the fields, including the winner,
         * mostly used when initializing a response when the game has ended
         * @param result The result of the logic execution
         * @param status The status of the logic execution
         * @param winner The winning {@link Team}
         */
        public GameResponse(T result, GameStatus status, Team winner) {
            this.result = result;
            this.status = status;
            this.winner = winner;
        }

        /**
         * Initializes a successful response with a custom success status other than {@link GameStatus#OK}
         * and some result
         * @param result The result of the logic execution
         * @param status The status of the logic execution
         */
        public GameResponse(T result, GameStatus status) {
            this(result, status, null);
        }

        /**
         * Creates a new game response with the specified result and a default success status {@link GameStatus#OK},
         * mostly used when initializing a response when the logic has successfully executed
         * @param result The result of the logic execution
         */
        public GameResponse(T result) {
            this(result, GameStatus.OK, null);
        }

        /**
         * Creates a new game response with the specified status,
         * mostly used when initializing a response when the logic has failed during execution
         * @param status Some status indicating failure
         */
        public GameResponse(GameStatus status) {
            this(null, status, null);
        }

        /**
         * @return The result of the logic execution
         */
        public T getResult() {
            return result;
        }

        /**
         * @return The status of the logic execution
         */
        public GameStatus getStatus() {
            return status;
        }

        /**
         * @return The winning {@link Team}
         */
        public Team getWinner() {
            return winner;
        }
    }

    /**
     * All possible statuses of the game logic execution
     */
    enum GameStatus {
        OK, TEAM_ELIMINATED, TEAM_ELIM_AND_GAME_OVER, GAME_OVER, INVALID_POSITION, NO_PLAYER, PLAYER_NOT_FROM_TEAM, INVALID_SIZE,
        INVALID_PLAYER_COLOR, INVALID_BUNKER_NAME, WRONG_TEAM_BUNKER, BUNKER_NOT_CREATED, TEAM_NOT_CREATED, NOT_ENOUGH_TEAMS
    }
}
