package game;

import game.PaintballField.Cell;
import game.data_structures.*;
import game.players.*;
import game.players.Player.PlayerColor;
import game.GameUtil.*;

/**
 * Class that represents a Paintball Game
 */
public class PaintballGame implements Game {
    /**
     * Field of the game
     */
    private final PaintballField field;
    /**
     * Array of all bunkers in the game
     */
    private final Array<Bunker> allBunkers = new ArrayClass<>();
    /**
     * Array of all abandonedBunkers in the game
     */
    private final Array<Bunker> abandonedBunkers = new ArrayClass<>();
    /**
     * Array of all teams of the game
     */
    private final Array<Team> teams = new ArrayClass<>();
    /**
     * Width of the field of the game
     */
    private final int width;
    /**
     * Height of the field of the game
     */
    private final int height;
    /**
     * Index of the current team (team that turn is at the particular moment)
     */
    private int currentTeamIndex = 0;

    /**
     * Constructs an object PaintballGame with the given width, height, team names and bunkers
     * @param width Width of the field of the game
     * @param height Height of the field of the game
     * @param teamNames Names of the teams of the game
     * @param bunkers Bunkers of the game
     */
    public PaintballGame(int width, int height, Array<String> teamNames, Array<Bunker> bunkers) {
        this.width = width;
        this.height = height;
        field = new PaintballField(width, height);
        for (int i = 0; i < teamNames.size(); i++)
            teams.insertLast(new Team(teamNames.get(i)));
        for (int i = 0; i < bunkers.size(); i++) {
            Bunker bunker = bunkers.get(i);
            String bunkerTeamName = bunker.getTeamName();
            if (bunkerTeamName == null) abandonedBunkers.insertLast(bunker);
            else getTeam(bunkerTeamName).addBunker(bunker);
            allBunkers.insertLast(bunker);
            field.addEntity(bunker);
        }
    }

    /**
     * Returns team by its name
     * @param name name of the team
     * @return team with the specific name
     */
    public Team getTeam(String name) {
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            if (team.getName().equals(name)) return team;
        }
        return null;
    }

    /**
     * Returns width of the field of the game
     * @return width of the field of the game
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns height of the field of the game
     * @return height of the field of the game
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns bunkers of the game
     * @return bunkers of the game
     */
    public Array<Bunker> getBunkers() {
        return allBunkers;
    }

    /**
     * Returns bunkers of the current team (team that turn is at the particular moment)
     * @return bunkers of the current team
     */
    public Array<Bunker> getCurrentTeamBunkers() {
        return getCurrentTeam().getBunkers();
    }

    /**
     * Returns players of the current team (team that turn is at the particular moment)
     * @return players of the current team
     */
    public Array<Player> getCurrentTeamPlayers(){
        return getCurrentTeam().getPlayers();
    }

    /**
     * Returns all teams of the game
     * @return all teams of the game
     */
    public Array<Team> getTeams(){
        return teams;
    }

    /**
     * Returns iterator of the field
     * @return iterator of the field
     */
    public PaintballField.FieldIterator getFieldIterator() {
        return field.getIterator();
    }

    /**
     * Returns current team (team that turn is at the particular moment)
     * @return current team
     */
    public Team getCurrentTeam() {
        return teams.get(currentTeamIndex);
    }

    /**
     * Returns if the bunker is occupied or not
     * @param bunker bunker
     * @return <code>true</code> if the bunker is occupied and <code>false</code> otherwise
     */
    public boolean isOccupied(Bunker bunker){
        Cell cell = field.getCell(bunker.getX(), bunker.getY());
        return cell.getPlayer() != null;
    }
    @Override
    public GameResponse createPlayer(PlayerColor color, String bunkerName) {
        GameResponse response = createPlayerNoNextTurn(color, bunkerName);
        nextTurn();
        return response;
    }

    /**
     * Creates a player without going to the next turn by its color and bunker name
     * @param color color of the player
     * @param bunkerName bunker where player should be created
     * @return the response of the game of the enum type <code>GameResponse</code>
     */
    private GameResponse createPlayerNoNextTurn(PlayerColor color, String bunkerName) {
        if (color == PlayerColor.NONE) return GameResponse.INVALID_PLAYER_COLOR;
        Bunker bunker = null;
        for (int i = 0; i < allBunkers.size(); i++){
            Bunker b = allBunkers.get(i);
            if (b.getName().equals(bunkerName)) {
                bunker = b;
                break;
            }
        }
        if (bunker == null) return GameResponse.INVALID_BUNKER_NAME;
        int treasury = bunker.getTreasury();
        int cost = color.getCost();
        if (!bunker.getTeamName().equals(this.getCurrentTeam().getName())){
            return GameResponse.WRONG_TEAM_BUNKER; // Bunker illegally invaded. + переход к след ходу
        }
        if (isOccupied(bunker)){
            return GameResponse.BUNKER_OCCUPIED; // Bunker not free.
        }
        if (treasury < cost){
            return GameResponse.NOT_ENOUGH_COINS; // Insufficient coins for recruitment.
        }
        bunker.setTreasury(treasury - cost);
        Player player;
        switch (color) {
            case PlayerColor.BLUE -> player = new BluePlayer(bunker.getX(), bunker.getY(), bunker.getTeamName());
            case PlayerColor.RED -> player = new RedPlayer(bunker.getX(), bunker.getY(), bunker.getTeamName());
            case PlayerColor.GREEN -> player = new GreenPlayer(bunker.getX(), bunker.getY(), bunker.getTeamName());
            default -> { return GameResponse.INVALID_PLAYER_COLOR; }
        }
        getCurrentTeam().addPlayer(player);
        field.addEntity(player);
        return GameResponse.PLAYER_CREATED; // created
    }

    /**
     * Removes empty teams
     */
    public void removeEmptyTeams() {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).isEmpty()) {
                teams.removeAt(i);
                i--;
                if (i < currentTeamIndex) currentTeamIndex--;
            }
        }
    }
    @Override
    public boolean isGameOver() { return teams.size() == 1; }

    /**
     * Returns winning team
     * @return winning team
     */
    public Team getWinner() { return isGameOver() ? teams.get(0) : null; }
    @Override
    public void nextTurn() {
        currentTeamIndex++;
        if (currentTeamIndex >= teams.size()) currentTeamIndex = 0;
        for (int i = 0; i < allBunkers.size(); i++) {
            Bunker bunker = allBunkers.get(i);
            bunker.endTurn();
        }
    }
    @Override
    public Iterator<Move> movePlayerAt(int x, int y, Array<Direction> directions) {
        MoveResponse response = movePlayerNoNextTurn(x, y, directions);
        removeEmptyTeams();
        if (isGameOver()) response.getMoves().insertLast(new Move(GameResponse.GAME_OVER));
        nextTurn();
        return response.getMoveIterator();
    }

    /**
     * Moves a player from the coordinates (X,Y) without going to the next turn with the given directions (from 1 to 3)
     * @param x X coordinate of the player
     * @param y Y coordinate of the player
     * @param directions directions (from 1 to 3)
     * @return response of the type <code>MoveResponse</code> containing moving state
     */
    private MoveResponse movePlayerNoNextTurn(int x, int y, Array<Direction> directions) {
        if (x < 0 || x > width || y < 0 || y >  height) {
            return new MoveResponse(GameResponse.INVALID_POSITION);
        }
        Cell oldCell = field.getCell(x,y);
        Player player = oldCell.player;
        if (player == null) return new MoveResponse(GameResponse.NO_PLAYER);
        if (!player.getTeamName().equals(this.getCurrentTeam().getName())) return new MoveResponse(GameResponse.PLAYER_NOT_FROM_TEAM);
        if (directions.size() == 0 || directions.size() > player.getColor().getMaxMoves()) return new MoveResponse(GameResponse.INVALID_MOVE);
        int initialX = x, initialY = y;
        Array<Move> moves = new ArrayClass<>();
        for (int i = 0; i < directions.size(); i++) {
            Direction dir = directions.get(i);
            if (dir == Direction.INVALID) {
                moves.insertLast(new Move(GameResponse.INVALID_DIRECTION));
                continue;
            }
            int oldX = player.getX(), oldY = player.getY();
            player.move(dir);
            int newX = player.getX(), newY = player.getY();
            if (newX <= 0 || newX > width || newY <= 0 || newY > height) {
                player.setX(oldX); player.setY(oldY);
                moves.insertLast(new Move(GameResponse.OFF_THE_MAP));
                continue;
            }
            Cell newCell = field.getCell(newX, newY);
            Player otherPlayer = newCell.player;
            Bunker bunker = newCell.bunker;
            GameResponse response = GameResponse.MOVE_SUCCESS;
            if (otherPlayer != null) {
                if (otherPlayer.getTeamName().equals(player.getTeamName())) {
                    player.setX(oldX); player.setY(oldY);
                    moves.insertLast(new Move(GameResponse.POSITION_OCCUPIED));
                    continue;
                }
                if (fight(player, otherPlayer)){
                    response = GameResponse.WON_FIGHT;
                    Team otherTeam = getTeam(otherPlayer.getTeamName());
                    otherTeam.removePlayer(otherPlayer);
                }
                else {
                    getCurrentTeam().removePlayer(player);
                    field.removePlayer(initialX, initialY);
                    moves.insertLast(new Move(GameResponse.PLAYER_ELIMINATED));
                    break;
                }
            }
            field.movePlayerFromTo(oldX, oldY, newX, newY);
            String playerTeamName = player.getTeamName();
            if (bunker != null && (bunker.getTeamName() == null || !playerTeamName.equals(bunker.getTeamName()))) {
                Team currentBunkerTeam = getTeam(bunker.getTeamName());
                if (currentBunkerTeam == null) abandonedBunkers.removeAt(abandonedBunkers.searchIndexOf(bunker));
                else currentBunkerTeam.removeBunker(bunker);
                getCurrentTeam().addBunker(bunker);
                response = response == GameResponse.WON_FIGHT ? GameResponse.WON_AND_SEIZED : GameResponse.BUNKER_SEIZED;
            }
            if (response != GameResponse.MOVE_SUCCESS) moves.insertLast(new Move(response));
            moves.insertLast(new Move(newX, newY, player.getColor()));
        }
        return new MoveResponse(moves);
    }

    /**
     * Make player attack without going to the next turn
     * @return the response of the game of type <code>GameResponse</code>
     */
    public GameResponse attack() {
        GameResponse response = GameResponse.ATTACK_SUCCESS;
        attackNoNextTurn();
        if (getCurrentTeam().isEmpty()) response = GameResponse.TEAM_ELIMINATED;
        removeEmptyTeams();
        if (isGameOver()) response = response == GameResponse.TEAM_ELIMINATED ? GameResponse.TEAM_ELIM_AND_GAME_OVER : GameResponse.GAME_OVER;
        nextTurn();
        return response;
    }

    /**
     * Make player attack without going to the next turn
     */
    private void attackNoNextTurn() {
        Array<Player> players = getCurrentTeam().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Iterator<Cell> attackIterator = player.attack(this.field);
            while (attackIterator.hasNext()) {
                Cell cell = attackIterator.next();
                Player defender = cell.player; Bunker bunker = cell.bunker;
                if (defender != null) {
                    if (defender.getTeamName().equals(player.getTeamName())) continue;
                    if (fight(player, defender)) {
                        field.removePlayer(defender.getX(), defender.getY());
                        Team otherTeam = getTeam(defender.getTeamName());
                        otherTeam.removePlayer(defender);
                    } else {
                        field.removePlayer(player.getX(), player.getY());
                        getCurrentTeam().removePlayer(player);
                        return;
                    }
                }
                if (bunker != null && !getCurrentTeam().getName().equals(bunker.getTeamName())) {
                    Team currentBunkerTeam = getTeam(bunker.getTeamName());
                    if (currentBunkerTeam == null) abandonedBunkers.removeAt(abandonedBunkers.searchIndexOf(bunker));
                    else currentBunkerTeam.removeBunker(bunker);
                    getCurrentTeam().addBunker(bunker);
                }
            }
        }
    }

    /**
     * Make player (attacker) fight with another one (defender)
     * @param attacker player, who is attacking
     * @param defender player, who is attacked
     * @return <code>true</code> if the attacker wins, and <code>false</code> otherwise
     */
    public boolean fight(Player attacker, Player defender){
        PlayerColor attackerColor = attacker.getColor();
        PlayerColor defenderColor = defender.getColor();
        if (attackerColor == defenderColor) return true;
        switch (attackerColor) {
            case PlayerColor.RED -> { return defenderColor == PlayerColor.BLUE; }
            case PlayerColor.BLUE -> {  return defenderColor == PlayerColor.GREEN; }
            case PlayerColor.GREEN -> { return defenderColor == PlayerColor.RED; }
            default -> { return true; }
        }
    }
}
