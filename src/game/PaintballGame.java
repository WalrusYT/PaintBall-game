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
     * Array of all buildings in the game
     */
    private final Array<Building> allBuildings = new ArrayClass<>();
    /**
     * Array of all teams of the game
     */
    private final Array<Team> teams = new ArrayClass<>();
    /**
     * Index of the current team (team that turn is at the particular moment)
     */
    private int currentTeamIndex = 0;

    /**
     * Create a new game with the specified {@link Field} width and height<br>
     * further initialization should happen using the {@link Game#addBuilding(int, int, int, String)}
     * and {@link Game#addTeam(String, String)} methods
     * @param width The width of the field
     * @param height The height of the field
     */
    public PaintballGame(int width, int height){
        this.field = new PaintballField(width, height);
    }

    @Override
    public GameStatus addTeam(String teamName, String bunkerName){
        for (int i = 0; i < teams.size(); i++){
            if (teamName.equals(teams.get(i).name())){
                return GameStatus.TEAM_NOT_CREATED;
            }
        }
        for (int i = 0; i < allBuildings.size(); i++){
            Building bunker = allBuildings.get(i);
            if (bunker.team() != null) continue;
            if (bunker.name().equals(bunkerName)) {
                Team team = new PaintballTeam(teamName);
                team.addBuilding(bunker);
                teams.insertLast(team);
                return GameStatus.OK;
            }
        }
        return GameStatus.TEAM_NOT_CREATED;
    }

    @Override
    public GameStatus addBuilding(int x, int y, int treasury, String bunkerName){
        if (x <= 0 || x > width() || y <= 0 || y > height() || treasury <= 0) {
            return GameStatus.BUNKER_NOT_CREATED;
        }
        if (field.cellAt(x,y).hasBuilding()) return GameStatus.BUNKER_NOT_CREATED;
        for (int i = 0; i < allBuildings.size(); i++){
            if (bunkerName.equals(allBuildings.get(i).name())){
                return GameStatus.BUNKER_NOT_CREATED;
            }
        }
        Building bunker = new Bunker(this.field, bunkerName, x, y, treasury);
        allBuildings.insertLast(bunker);
        return GameStatus.OK;
    }

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
        return new GameResponse<>(status); // created
    }

    /**
     * Removes empty teams, called at the end of some methods where
     * a team can undergo certain modifications
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

    /**
     * Check whether the game has ended
     * @return {@code true} if there's only 1 team left, otherwise {@code false}
     */
    private boolean isGameOver() { return teams.size() == 1; }

    private Team winner() {
        return teams.get(0);
    }

    /**
     * Performs some actions at the end of each team's turn
     */
    private void nextTurn() {
        currentTeamIndex++;
        if (currentTeamIndex >= teams.size()) currentTeamIndex = 0;
        for (int i = 0; i < allBuildings.size(); i++) {
            allBuildings.get(i).endTurn();
        }
    }

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
        return new GameResponse<>(actions);
    }

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
