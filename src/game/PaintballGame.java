package game;

import game.data_structures.*;
import game.players.*;
import game.players.Player.*;
import game.Building.CreateStatus;

/**
 * Class that represents a Paintball Game
 */
public class PaintballGame implements Game {
    /**
     * Field of the game
     */
    private final Field field;
    /**
     * Array of all bunkers in the game
     */
    private final Array<Building> allBuildings;
    /**
     * Array of all teams of the game
     */
    private final Array<Team> teams;
    /**
     * Index of the current team (team that turn is at the particular moment)
     */
    private int currentTeamIndex = 0;

    /**
     * Constructs an object PaintballGame with the given width, height, team names and bunkers
     * @param teams Names of the teams of the game
     * @param allBuildings Bunkers of the game
     */
    public PaintballGame(Field field, Array<Team> teams, Array<Building> allBuildings) {
        this.field = field;
        this.teams = teams;
        this.allBuildings = allBuildings;
    }

    /**
     * Returns all teams of the game
     * @return all teams of the game
     */
    public SizedIterator<Team> getTeams() {
        return teams.iterator();
    }

    /**
     * Returns current team (team that turn is at the particular moment)
     * @return current team
     */
    @Override
    public Team currentTeam() {
        return teams.get(currentTeamIndex);
    }

    @Override
    public int width() {
        return field.width();
    }

    @Override
    public int height() {
        return field.height();
    }

    @Override
    public SizedIterator<Building> buildings() {
        return allBuildings.iterator();
    }

    @Override
    public SizedIterator<Team> teams() {
        return teams.iterator();
    }

    /**
     * Creates a player without going to the next turn by its color and bunker name
     * @param color color of the player
     * @param bunkerName bunker where player should be created
     * @return the response of the game of the enum type <code>GameResponse</code>
     */
    @Override
    public GameResponse<CreateStatus> createPlayer(PlayerColor color, String bunkerName) {
        if (color == null) {
            nextTurn();
            return new GameResponse<>(GameStatus.INVALID_PLAYER_COLOR);
        }
        Building building = null;
        for (int i = 0; i < allBuildings.size(); i++){
            Building b = allBuildings.get(i);
            if (b.name().equals(bunkerName)) {
                building = b;
                break;
            }
        }
        if (building == null) {
            nextTurn();
            return new GameResponse<>(GameStatus.INVALID_BUNKER_NAME);
        }
        if (!building.team().equals(this.currentTeam())){
            nextTurn();
            return new GameResponse<>(GameStatus.WRONG_TEAM_BUNKER); // Bunker illegally invaded. + переход к след ходу
        }
        CreateStatus status = building.createPlayer(color);
        nextTurn();
        return new GameResponse<>(status, GameStatus.OK); // created
    }

    /**
     * Removes empty teams
     */
    private void removeEmptyTeams() {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).isEmpty()) {
                teams.removeAt(i);
                i--;
                if (i < currentTeamIndex) currentTeamIndex--;
            }
        }
    }

    private boolean isGameOver() { return teams.size() == 1; }

    private Team winner() {
        return teams.get(0);
    }

    private void nextTurn() {
        currentTeamIndex++;
        if (currentTeamIndex >= teams.size()) currentTeamIndex = 0;
        for (int i = 0; i < allBuildings.size(); i++) {
            allBuildings.get(i).endTurn();
        }
    }

    /**
     * Moves a player from the coordinates (X,Y) without going to the next turn with the given directions (from 1 to 3)
     * @param x X coordinate of the player
     * @param y Y coordinate of the player
     * @param directions directions (from 1 to 3)
     * @return response of the type <code>MoveResponse</code> containing moving state
     */
    @Override
    public GameResponse<Iterator<Action>> movePlayerAt(int x, int y, Array<Direction> directions) {
        if (x < 0 || x > field.width() || y < 0 || y > field.height()) {
            nextTurn();
            return new GameResponse<>(GameStatus.INVALID_POSITION);
        }
        Player player = field.cellAt(x, y).player;
        if (player == null) {
            nextTurn();
            return new GameResponse<>(GameStatus.NO_PLAYER);
        }
        if (!player.team().equals(this.currentTeam())) {
            nextTurn();
            return new GameResponse<>(GameStatus.PLAYER_NOT_FROM_TEAM);
        }
        Iterator<Action> actions = player.move(directions);
        removeEmptyTeams();
        if (isGameOver()) {
            return new GameResponse<>(actions, GameStatus.GAME_OVER, this.winner());
        }
        nextTurn();
        return new GameResponse<>(actions, GameStatus.OK);
    }

    /**
     * Make player attack without going to the next turn
     * @return the response of the game of type <code>GameResponse</code>
     */
    @Override
    public GameResponse<Team> playersAttack() {
        Team attackerTeam = this.currentTeam();
        GameStatus status = GameStatus.OK;
        Iterator<Player> players = currentTeam().players();
        while (players.hasNext()) {
            players.next().attack();
        }

        if (currentTeam().isEmpty()) status = GameStatus.TEAM_ELIMINATED;
        removeEmptyTeams();
        if (isGameOver()) {
            status = status == GameStatus.TEAM_ELIMINATED ? GameStatus.TEAM_ELIM_AND_GAME_OVER : GameStatus.GAME_OVER;
        }
        nextTurn();
        return new GameResponse<>(attackerTeam, status, winner());
    }

    @Override
    public Iterator<Field.Cell> map() {
        return field.iterator();
    }
}
