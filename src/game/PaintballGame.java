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
    private Field field;
    /**
     * Array of all buildings in the game
     */
    private Array<Building> allBuildings = new ArrayClass<>();
    /**
     * Array of all teams of the game
     */
    private Array<Team> teams = new ArrayClass<>();
    /**
     * Index of the current team (team that turn is at the particular moment)
     */
    private int currentTeamIndex = 0;

    /**
     * Indication of a game being in progress
     */
    boolean inProgress = false;

    @Override
    public int width() {
        return field.width();
    }

    @Override
    public int height() {
        return field.height();
    }

    @Override
    public GameStatus setField(int width, int height) {
        if (width < 10 || height < 10) return GameStatus.INVALID_SIZE;
        field = new PaintballField(width, height);
        return GameStatus.OK;
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
        if (x <= 0 || x > field.width() || y <= 0 || y > field.height() || treasury <= 0) {
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
        if (building.team() != this.currentTeam()){
            nextTurn();
            return new GameResponse<>(GameStatus.WRONG_TEAM_BUNKER); // Bunker illegally invaded. + переход к след ходу
        }
        CreateStatus status = building.createPlayer(color);
        nextTurn();
        return new GameResponse<>(status); // created
    }

    @Override
    public boolean inProgress() {
        return inProgress;
    }

    @Override
    public GameStatus start() {
        if (teams.size() < 2) return GameStatus.NOT_ENOUGH_TEAMS;
        inProgress = true;
        return GameStatus.OK;
    }


    @Override
    public void stop() {
        field = null;
        currentTeamIndex = 0;
        allBuildings = new ArrayClass<>();
        teams = new ArrayClass<>();
        inProgress = false;
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

    /**
     * Get the first {@link Team} from the teams {@link Array}, which is considered the winner if the size of the array is 1
     * @return Reference to the winner team
     */
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
        if (player.team() != this.currentTeam()) {
            nextTurn();
            return new GameResponse<>(GameStatus.PLAYER_NOT_FROM_TEAM);
        }
        Iterator<Action> actions = player.move(directions);
        removeEmptyTeams();
        if (isGameOver()) {
            Team winner = winner();
            this.stop();
            return new GameResponse<>(actions, GameStatus.GAME_OVER, winner);
        }
        nextTurn();
        return new GameResponse<>(actions);
    }

    @Override
    public GameResponse<Field.Map> playersAttack() {
        GameStatus status = GameStatus.OK;
        Iterator<Player> players = currentTeam().players();
        while (players.hasNext()) {
            players.next().attack();
        }
        Field.Map mapAfterAttack = map(this.currentTeam());

        if (currentTeam().isEmpty()) status = GameStatus.TEAM_ELIMINATED;
        removeEmptyTeams();
        if (isGameOver()) {
            Team winner = winner();
            this.stop();
            if (status == GameStatus.TEAM_ELIMINATED)
                return new GameResponse<>(mapAfterAttack, GameStatus.TEAM_ELIM_AND_GAME_OVER, winner);
            return new GameResponse<>(mapAfterAttack, GameStatus.GAME_OVER, winner);
        }
        nextTurn();
        return new GameResponse<>(mapAfterAttack, status);
    }

    @Override
    public Field.Map map() {
        return field.map();
    }

    @Override
    public Field.Map map(Team team) {
        return field.map(team);
    }
}
