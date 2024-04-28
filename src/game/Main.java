package game;

import game.data_structures.Array;
import game.data_structures.ArrayClass;
import game.data_structures.Iterator;
import game.data_structures.SizedIterator;
import game.players.*;
import game.Game.*;
import game.Building.*;
import game.players.Player.*;

import java.util.Scanner;
/**
 * Main program for the PaintBall Game
 * @author Ilia Taitsel, Taisiia Hlukha 24/25
 */
public class Main {
    /**
     * User commands
     */
    public static final String START_GAME = "game";
    public static final String MOVE = "move";
    public static final String CREATE = "create";
    public static final String ATTACK = "attack";
    public static final String STATUS = "status";
    public static final String MAP = "map";
    public static final String BUNKERS = "bunkers";
    public static final String PLAYERS = "players";
    public static final String HELP = "help";
    public static final String QUIT = "quit";
    /**
     * Feedback given by the program
     */
    public static final String GO_QUIT = "Bye.";
    public static final String COMMANDS_NO_GAME = String.format("""
    %s - Create a new game
    %s - Show available commands
    %s - End program execution
    """, START_GAME, HELP, QUIT);
    public static final String COMMANDS_IN_GAME = String.format("""
    %s - Create a new game
    %s - Move a player
    %s - Create a player in a bunker
    %s - Attack with all players of the current team
    %s - Show the current state of the game
    %s - Show the map of the current team
    %s - List the bunkers of the current team, by the order they were seized
    %s - List the active players of the current team, by the order they were created
    %s - Show available commands
    %s - End program execution
    """, START_GAME, MOVE, CREATE, ATTACK, STATUS, MAP, BUNKERS, PLAYERS, HELP, QUIT);
    public static final String NOT_ENOUGH_ARGS = "ERROR: NOT ENOUGH ARGUMENTS";
    public static final String SIZE_NOT_OK = "ERROR: FIELD RESOLUTION IS NOT OK";
    public static final String NOT_ENOUGH_TEAMS = "FATAL ERROR: Insufficient number of teams.";
    public static final String BUNKERS_LIST = " bunkers:";
    public static final String BUNKER_NOT_CREATED = "Bunker not created.";
    public static final String TEAMS_LIST = " teams:";
    public static final String TEAM_NOT_CREATED = "Team not created.";
    public static final String INVALID_COMMAND = "Invalid command.";
    public static final String WITHOUT_OWNER = "without owner";
    public static final char PLAYER = 'P';
    public static final char BUNKER = 'B';
    public static final char OCCUPIED_BUNKER = 'O';
    public static final String WITHOUT_BUNKERS = "Without bunkers.";
    public static final String COINS_IN_POSITION = "coins in position";
    public static final String WITHOUT_PLAYERS = "Without players.";
    public static final String PLAYERS_LIST = " players:";
    public static final String PLAYER_IN_POSITION = "player in position";
    public static final String INVALID_PLAYER_COLOR = "Non-existent player type.";
    public static final String INVALID_BUNKER_NAME = "Non-existent bunker.";
    public static final String WRONG_TEAM_BUNKER = "Bunker illegally invaded.";
    public static final String BUNKER_OCCUPIED = "Bunker not free.";
    public static final String NOT_ENOUGH_COINS = "Insufficient coins for recruitment.";
    public static final String PLAYER_CREATED = "player created";
    public static final String UNEXPECTED_ERROR = "Unexpected error";
    public static final String INVALID_POSITION = "Invalid position.";
    public static final String INVALID_DIRECTION = "Invalid direction.";
    public static final String NO_PLAYER = "No player in that position.";
    public static final String INVALID_MOVE = "Invalid move.";
    public static final String POSITION_OCCUPIED = "Position occupied.";
    public static final String OFF_THE_MAP = "Trying to move off the map.";
    public static final String BUNKER_SEIZED = "Bunker seized.";
    public static final String WON_FIGHT = "Won the fight.";
    public static final String PLAYER_ELIMINATED = "Player eliminated.";
    public static final String WON_AND_SEIZED = "Won the fight and bunker seized.";
    public static final String PLAYER_NOT_FROM_TEAM = "Unable to move player from the enemy team.";
    public static final String WINNER_IS = "Winner is";
    public static final String PLAYERS_ELIMINATED = "All players eliminated.";
    /**
     * Main method. Invokes the command interpreter
     * @param args command-line arguments (not used in this program)
     */
    public static void main(String[] args) {
        Game game = null;
        Scanner in = new Scanner(System.in);
        String comm;
        String commandArgs;
        boolean isInputEmpty = false;
        do {
            if (!isInputEmpty) {
                String prefix = game == null ? "" : game.currentTeam().name();
                System.out.print(prefix + "> ");
            }
            String[] input = in.nextLine().split(" ", 2);
            comm = input[0];
            commandArgs = input.length > 1 ? input[1] : "" ;
            if (comm.isEmpty()) { isInputEmpty = true; continue; }
            isInputEmpty = false;
            switch (comm) {
                case HELP -> help(game);
                case START_GAME -> game = newGame(in, commandArgs);
                case STATUS -> status(game);
                case MAP -> map(game);
                case BUNKERS -> bunkers(game);
                case PLAYERS -> players(game);
                case CREATE -> game = create(game, commandArgs);
                case MOVE -> game = move(game, commandArgs);
                case ATTACK -> game = attack(game);
                case QUIT -> { System.out.println(GO_QUIT); game = null; }
                default -> System.out.println(INVALID_COMMAND);
            }
        }
        while (!comm.equals(QUIT));
        in.close();
    }

    /**
     * Informs the user about the available commands
     */
    private static void help(Game game){
        System.out.printf(game == null ? COMMANDS_NO_GAME : COMMANDS_IN_GAME);
    }

    /**
     * Starts a new game
     * @param in Scanner object to read user input
     * @param args arguments of the command (width, height, number of teams, number of bunkers)
     * @return {@link Game} game that is created
     */
    private static Game newGame(Scanner in, String args) {
        String[] commandArgs = args.split(" ");
        if (commandArgs.length < 4){
            System.out.println(NOT_ENOUGH_ARGS);
            return null;
        }
        int width, height, teamsNumber, bunkerNumber;
        width = Integer.parseInt(commandArgs[0]);
        height = Integer.parseInt(commandArgs[1]);
        teamsNumber = Integer.parseInt(commandArgs[2]);
        bunkerNumber = Integer.parseInt(commandArgs[3]);
        if (width < 10 && height < 10){
            System.out.println(SIZE_NOT_OK);
            return null;
        }
        if (teamsNumber < 2){
            System.out.println(NOT_ENOUGH_TEAMS);
            return null;
        }
        Game game = new PaintballGame(width, height);
        System.out.println(bunkerNumber + BUNKERS_LIST);
        for (int i = 0; i < bunkerNumber; i++) { 
            String[] input = in.nextLine().split(" ", 4);
            int x = Integer.parseInt(input[0]);
            int y = Integer.parseInt(input[1]);
            int treasury = Integer.parseInt(input[2]);
            String name = input[3];
            GameStatus status = game.addBuilding(x, y, treasury, name);
            if (status == GameStatus.BUNKER_NOT_CREATED) System.out.println(BUNKER_NOT_CREATED);
        }
        System.out.println(teamsNumber + TEAMS_LIST);
        for (int i = 0; i < teamsNumber; i++) {
            String[] input = in.nextLine().split(" ", 2);
            if (input.length < 2) {
                System.out.println(TEAM_NOT_CREATED);
                continue;
            }
            String teamName = input[0], bunkerName = input[1];
            GameStatus status = game.addTeam(teamName, bunkerName);
            if (status == GameStatus.TEAM_NOT_CREATED) System.out.println(TEAM_NOT_CREATED);
        }
        if (game.teams().size() < 2){
            System.out.println(NOT_ENOUGH_TEAMS);
            return null;
        }
        return game;
    }

    /**
     * Displays information on the current state of the game
     */
    private static void status(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }

        System.out.println(game.width() + " " + game.height());
        SizedIterator<Building> bunkers = game.buildings();
        int bunkersNumber = bunkers.size();
        System.out.println(bunkersNumber + BUNKERS_LIST);
        for (int i = 0; i < bunkersNumber; i++) {
            Building bunker = bunkers.next();
            Team team = bunker.team();
            String teamName = team == null ? WITHOUT_OWNER : team.name();
            System.out.println(bunker.name() + " (" + teamName + ")");
        }
        
        SizedIterator<Team> teams = game.teams();
        int teamsNumber = teams.size();
        System.out.println(teamsNumber + TEAMS_LIST);
        String teamList =  "";
        for (int i = 0; i < teamsNumber; i++) {
            teamList += teams.next().name() + "; ";
        }
        teamList = teamList.substring(0, teamList.length() - 2);
        System.out.println(teamList);
    }

    /**
     * Team’s map view of players and bunkers
     */
    private static void map(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }

        teamMap(game, game.currentTeam());
    }

    private static void teamMap(Game game, Team team) {
        Iterator<Field.Cell> map = game.map();
        int width = game.width(), height = game.height();
        System.out.printf("%d %d\n", width, height);
        System.out.print("**");
        for (int i = 1; i < width; i++) System.out.printf("%d ", i);
        System.out.printf("%d\n", width);

        int i = 0;
        while (map.hasNext()) {
            Field.Cell cell = map.next();
            char c = '.';
            Building bunker = cell.getBuilding(); Player player = cell.getPlayer();
            if (player != null && bunker != null && team.equals(bunker.team())) c = OCCUPIED_BUNKER;
            else if (player != null && team.equals(player.team())) c = PLAYER;
            else if (bunker != null && team.equals(bunker.team())) c = BUNKER;
            if (i % width == 0) System.out.print(i / width + 1);
            System.out.print(" " + c);
            if (i % width == width - 1)  System.out.println();
            i++;
        }
    }

    /**
     * Informs about the bunkers in the current team
     */
    private static void bunkers(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }

        SizedIterator<Building> bunkers = game.currentTeam().buildings();
        int bunkerNumber = bunkers.size();
        if (bunkerNumber == 0) { System.out.println(WITHOUT_BUNKERS); return; }
        System.out.println(bunkerNumber + BUNKERS_LIST);
        for (int i = 0; i < bunkerNumber; i++){
            Building bunker = bunkers.next();
            System.out.printf("%s with %d %s (%d, %d)\n", bunker.name(), bunker.treasury(),
                    COINS_IN_POSITION, bunker.fieldLocation().getX(), bunker.fieldLocation().getY());
        }
    }

    /**
     * Informs about the players of the current team
     */
    private static void players(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }
        
        SizedIterator<Player> players = game.currentTeam().players();
        int playersNumber = players.size();
        if (playersNumber == 0) { System.out.println(WITHOUT_PLAYERS); return; }
        System.out.println(playersNumber+PLAYERS_LIST);
        for (int i = 0; i < playersNumber; i++){ 
            Player player = players.next();
            String color = player.color().name().toLowerCase();
            System.out.printf("%s %s (%d, %d)\n", color,
                    PLAYER_IN_POSITION, player.fieldLocation().getX(), player.fieldLocation().getY());
        }
    }

    /**
     * Create a player in a bunker
     * @param args arguments of the command (type of Player, name of Bunker)
     */
    private static Game create(Game game, String args) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return null;
        }
        String[] commandArgs = args.split(" ", 2);
        if (commandArgs.length < 2){
            System.out.println(NOT_ENOUGH_ARGS);
            return game;
        }

        String playerType = commandArgs[0];
        String bunkerName = commandArgs[1];
        PlayerColor color = PlayerColor.fromName(playerType);
        GameResponse<CreateStatus> response = game.createPlayer(color, bunkerName);
        switch (response.getStatus()) {
            case GameStatus.OK -> {
                switch (response.getResult()) {
                    case CreateStatus.OK -> System.out.printf("%s %s in %s\n", playerType, PLAYER_CREATED, bunkerName);
                    case CreateStatus.NOT_ENOUGH_MONEY -> System.out.println(NOT_ENOUGH_COINS);
                    case CreateStatus.OCCUPIED -> System.out.println(BUNKER_OCCUPIED);
                }
            }
            case INVALID_PLAYER_COLOR -> System.out.println(INVALID_PLAYER_COLOR);
            case INVALID_BUNKER_NAME -> System.out.println(INVALID_BUNKER_NAME);
            case WRONG_TEAM_BUNKER -> System.out.println(WRONG_TEAM_BUNKER);
            default -> System.out.println(UNEXPECTED_ERROR);
        }

        return game;
    }

    /**
     * Moves a player
     * @param args arguments of the command (player x coordinate, player y coordinate, directions (from 1 to 3))
     */
    private static Game move(Game game, String args) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return null;
        }
        Array<Direction> dirs = new ArrayClass<>();
        String[] commandArgs = args.split(" ");
        int x = Integer.parseInt(commandArgs[0]);
        int y = Integer.parseInt(commandArgs[1]);
        for (int i = 2; i < commandArgs.length; i++){
            dirs.insertLast(Direction.fromName(commandArgs[i]));
        }
        
        GameResponse<Iterator<Action>> response = game.movePlayerAt(x, y, dirs);
        switch (response.getStatus()) {
            case INVALID_POSITION -> System.out.println(INVALID_POSITION);
            case NO_PLAYER -> System.out.println(NO_PLAYER);
            case PLAYER_NOT_FROM_TEAM -> System.out.println(PLAYER_NOT_FROM_TEAM);
            case OK, GAME_OVER -> {
                Iterator<Action> moves = response.getResult();  
                while (moves.hasNext()) {
                    Action move = moves.next();
                    switch (move.getStatus()) {
                        case INVALID_DIRECTION -> System.out.println(INVALID_DIRECTION);
                        case OFF_THE_MAP -> System.out.println(OFF_THE_MAP);
                        case POSITION_OCCUPIED -> System.out.println(POSITION_OCCUPIED);
                        case PLAYER_ELIMINATED -> System.out.println(PLAYER_ELIMINATED);
                        case INVALID_MOVE -> System.out.println(INVALID_MOVE);
                        case BUNKER_SEIZED -> {
                            System.out.println(BUNKER_SEIZED);
                            System.out.printf("%s %s (%d, %d)\n", move.getPlayer().color().name().toLowerCase(),
                                    PLAYER_IN_POSITION, move.getLocation().getX(), move.getLocation().getY());
                        }
                        case WON_FIGHT -> {
                            System.out.println(WON_FIGHT);
                            System.out.printf("%s %s (%d, %d)\n", move.getPlayer().color().name().toLowerCase(),
                                    PLAYER_IN_POSITION, move.getLocation().getX(), move.getLocation().getY());
                        }
                        case WON_AND_SEIZED -> {
                            System.out.println(WON_AND_SEIZED);
                            System.out.printf("%s %s (%d, %d)\n", move.getPlayer().color().name().toLowerCase(),
                                    PLAYER_IN_POSITION, move.getLocation().getX(), move.getLocation().getY());
                        }
                        case NOTHING -> System.out.printf("%s %s (%d, %d)\n", move.getPlayer().color().name().toLowerCase(),
                                PLAYER_IN_POSITION, move.getLocation().getX(), move.getLocation().getY());
                        default -> System.out.println(UNEXPECTED_ERROR);
                    }
                }
                if (response.getStatus() == GameStatus.GAME_OVER) {
                    System.out.printf("%s %s.\n", WINNER_IS, response.getWinner().name());
                    return null;
                }
            }
        }
        return game;
    }

    /**
     * Attacking a team
     */
    private static Game attack(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return null;
        }

        GameResponse<Team> response = game.playersAttack();
        switch (response.getStatus()) {
            case TEAM_ELIM_AND_GAME_OVER -> {
                System.out.println(PLAYERS_ELIMINATED);
                System.out.printf("%s %s.\n", WINNER_IS, response.getWinner().name());
                return null;
            }
            case GAME_OVER -> {
                if (response.getWinner().equals(response.getResult()))
                    teamMap(game, response.getResult());
                System.out.printf("%s %s.\n", WINNER_IS, response.getWinner().name());
                return null;
            }
            case TEAM_ELIMINATED -> System.out.println(PLAYERS_ELIMINATED);
            case OK -> teamMap(game, response.getResult());
            default -> System.out.println(UNEXPECTED_ERROR);
        }
        return game;
    }
}