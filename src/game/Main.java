package game;

import game.data_structures.Array;
import game.data_structures.ArrayClass;
import game.data_structures.Iterator;
import game.players.*;
import game.players.Player.PlayerColor;
import game.GameUtil.*;

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
    %s - Create a new game\n%s - Show available commands\n
    %s - End program execution\n
    """, START_GAME, HELP, QUIT);
    public static final String COMMANDS_IN_GAME = String.format("""
    %s - Create a new game\n%s - Move a player\n%s - Create a player in a bunker\n
    %s - Attack with all players of the current team\n%s - Show the current state of the game\n
    %s - Show the map of the current team\n%s - List the bunkers of the current team, by the order they were seized\n
    %s - List the active players of the current team, by the order they were created\n%s - Show available commands\n
    %s - End program execution\n
    """, START_GAME, MOVE, CREATE, ATTACK, STATUS, MAP, BUNKERS, PLAYERS, HELP, QUIT);
    public static final String NO_ENOGH_ARGS = "ERROR: NOT ENOUGH ARGUMENTS";
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
    /**
     * Main method. Invokes the command interpreter
     * @param args command-line arguments (not used in this program)
     */
    public static void main (String[] args) {
        Game game = null;
        Scanner in = new Scanner(System.in);
        String comm;
        String commandArgs;
        boolean isInputEmpty = false;
        do {
            if (!isInputEmpty) {
                String prefix = game == null ? "" : game.getCurrentTeam().getName();
                System.out.print(prefix + "> ");
            }
            String[] input = in.nextLine().split(" ", 2);
            comm = input[0];
            commandArgs = input.length > 1 ? input[1] : "" ;
            if (comm.isEmpty()) { isInputEmpty = true; continue; }
            isInputEmpty = false;
            switch (comm) {
                case HELP -> help(game);
                case START_GAME -> newGame(game, in, commandArgs);
                case STATUS -> status(game);
                case MAP -> map(game);
                case BUNKERS -> bunkers(game);
                case PLAYERS -> players(game);
                case CREATE -> create(game, commandArgs);
                case MOVE -> move(game, commandArgs);
                case ATTACK -> attack(game);
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
        if (game == null){
            System.out.printf(COMMANDS_NO_GAME);
        }
        else {
            System.out.printf(COMMANDS_IN_GAME);
        }
    }

    /**
     * Starts a new game
     * @param in Scanner object to read user input
     * @param args arguments of the command (width, height, number of teams, number of bunkers)
     */
    private static void newGame(Game game, Scanner in, String args){
        // game 10 10 5 5
        game = null;
        String[] commandArgs = args.split(" ");
        if (commandArgs.length<4){
            System.out.println(NO_ENOGH_ARGS);
            return;
        }
        int width, height, teamsNumber, bunkerNumber;
        width = Integer.parseInt(commandArgs[0]);
        height = Integer.parseInt(commandArgs[1]);
        teamsNumber = Integer.parseInt(commandArgs[2]);
        bunkerNumber = Integer.parseInt(commandArgs[3]);
        if (width<10 && height<10){
            System.out.println(SIZE_NOT_OK);
            return;
        }
        if (teamsNumber < 2){
            System.out.println(NOT_ENOUGH_TEAMS);
            return;
        }

        System.out.println(bunkerNumber + BUNKERS_LIST);
        Array<Bunker> bunkers = new ArrayClass<Bunker>();
        bunkerLoop:
        for (int i = 0; i < bunkerNumber; i++) { 
            String[] input = in.nextLine().split(" ", 4);
            int x, y, treasury;
            String name = input[3];
            
            try {
                x = Integer.parseInt(input[0]);
                y = Integer.parseInt(input[1]);
                treasury = Integer.parseInt(input[2]);
            } catch (NumberFormatException e){
                System.out.println(BUNKER_NOT_CREATED);
                continue;
            }

            if ((x < 0 || x > width) || (y < 0 || y > height) || treasury <= 0) {
                System.out.println(BUNKER_NOT_CREATED);
                continue;
            }
            Iterator<Bunker> iterator = bunkers.iterator();
            while (iterator.hasNext()){
                Bunker bunker = iterator.next();
                if (bunker.getName().equals(name) || (bunker.getX() == x && bunker.getY() == y)) {
                    System.out.println(BUNKER_NOT_CREATED);
                    continue bunkerLoop;
                }
            }
            bunkers.insertLast(new Bunker(treasury, x, y, name));
        }

        System.out.println(teamsNumber + TEAMS_LIST);
        Array<String> teams = new ArrayClass<String>();
        teamLoop:
        for (int i = 0; i < teamsNumber; i++) {
            String[] input = in.nextLine().split(" ", 2);
            if (input.length < 2) {
                System.out.println(TEAM_NOT_CREATED);
                continue;
            }

            String teamName = input[0], bunkerName = input[1];
            boolean bunkerExists = false;
            Iterator<String> iterator = teams.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(teamName)) {
                    System.out.println(TEAM_NOT_CREATED);
                    continue teamLoop;
                }
            }

            for (int j = 0; j < bunkers.size(); j++) {
                Bunker bunker = bunkers.get(j);
                if (bunker.getTeamName()!=null) continue;
                if (bunker.getName().equals(bunkerName)) {
                    bunker.setTeamName(teamName);
                    teams.insertLast(teamName);
                    bunkerExists = true;
                    break;
                }
            }

            if (!bunkerExists) {
                System.out.println(TEAM_NOT_CREATED);
            }
        }

        if (teams.size() < 2){
            System.out.println(NOT_ENOUGH_TEAMS);
            return;
        }

        game = new PaintballGame(width, height, teams, bunkers);
    }

    /**
     * Displays information on the current state of the game
     */
    private static void status(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }

        System.out.println(game.getWidth() + " " + game.height());
        Array<Bunker> bunkers = game.getBunkers();
        int bunkersNumber = bunkers.size();
        
        System.out.println(bunkersNumber + BUNKERS_LIST);
        for (int i = 0; i < bunkersNumber; i++) {
            Bunker bunker = bunkers.get(i);
            String team = bunker.getTeamName();
            if (team == null) team = WITHOUT_OWNER;
            System.out.println(bunker.getName() + " (" + team + ")");
        }
        
        Array<Team> teams = game.getTeams();
        int teamsNumber = teams.size();
        System.out.println(teamsNumber + TEAMS_LIST);
        String teamList =  "";
        for (int i = 0; i < teamsNumber; i++) {
            teamList += teams.get(i).getName() + "; ";
        }
        teamList = teamList.substring(0, teamList.length() - 2);
        System.out.println(teamList);
    }

    /**
     * Current team’s map view of players and bunkers
     */
    private static void map(Game game) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }
        map(game.getCurrentTeam().getName());
    }

    /**
     * Team’s map view of players and bunkers
     * @param teamName The name of the team
     */
    private static void map(Game game, String teamName) {
        int width = game.getWidth(), height = game.height();
        System.out.println(width + " " + height);
        PaintballField.FieldIterator iterator = game.getFieldIterator();
        System.out.print("**");
        for (int i = 1; i < width; i++) System.out.printf("%d ", i);
        System.out.printf("%d\n", width);

        int i = 0;
        while (iterator.hasNext()) {
            PaintballField.Cell cell = iterator.next();
            char c = '.';
            Bunker bunker = cell.getBunker(); Player player = cell.getPlayer();
            if (player != null && bunker != null && teamName.equals(bunker.getTeamName())) c = OCCUPIED_BUNKER;
            else if (player != null && teamName.equals(player.getTeamName())) c = PLAYER;
            else if (bunker != null && teamName.equals(bunker.getTeamName())) c = BUNKER;
            if (i % width == 0) System.out.print(i / width + 1);
            System.out.print(" " + c);
            if (i % width == width - 1) System.out.println();
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

        Array<Bunker> bunkers = game.getCurrentTeamBunkers();
        int bunkersNumber = bunkers.size();
        if (bunkersNumber == 0) { System.out.println(WITHOUT_BUNKERS); return; }
        System.out.println(bunkersNumber + BUNKERS_LIST);
        for (int i = 0; i < bunkers.size(); i++){ 
            Bunker bunker = bunkers.get(i);
            System.out.printf("%s with %d %s (%d, %d)\n", bunker.getName(), bunker.getTreasury(), COINS_IN_POSITION, bunker.getX(), bunker.getY());
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
        
        Array<Player> players = game.getCurrentTeamPlayers();
        int playersNumber = players.size();
        if (playersNumber == 0) { System.out.println(WITHOUT_PLAYERS); return; }
        System.out.println(playersNumber+PLAYERS_LIST);
        for (int i = 0; i < playersNumber; i++){ 
            Player player = players.get(i);
            String color = player.getColor().getName();;
            System.out.printf("%s %s (%d, %d)\n", color, PLAYER_IN_POSITION, player.getX(), player.getY());
        }
    }

    /**
     * Create a player in a bunker
     * @param args arguments of the command (type of Player, name of Bunker)
     */
    private static void create(Game game, String args) {
        if (game == null) {
            System.out.println(INVALID_COMMAND);
            return;
        }
        String[] commandArgs = args.split(" ", 2);
        if (commandArgs.length < 2){
            System.out.println(NO_ENOGH_ARGS);
            return;
        }
        String playerType = commandArgs[0];
        String bunkerName = commandArgs[1];
        PlayerColor color = PlayerColor.fromName(playerType);
        switch (game.createPlayer(color, bunkerName)) {
            case INVALID_PLAYER_COLOR -> System.out.println("Non-existent player type.");
            case INVALID_BUNKER_NAME -> System.out.println("Non-existent bunker.");
            case WRONG_TEAM_BUNKER -> System.out.println("Bunker illegally invaded.");
            case BUNKER_OCCUPIED -> System.out.println("Bunker not free.");
            case NOT_ENOUGH_COINS -> System.out.println("Insufficient coins for recruitment.");
            case PLAYER_CREATED -> System.out.printf("%s player created in %s\n", playerType, bunkerName);
            default -> System.out.println("Unexpected error");
        }
    }

    /**
     * Moves a player
     * @param args arguments of the command (player x coordinate, player y coordinate, directions (from 1 to 3))
     */
    private static void move(Game game, String args) {
        if (game == null) {
            System.out.println("Invalid command.");
            return;
        }
        int x, y;
        Array<Direction> dirs = new ArrayClass<>();
        String[] commandArgs = args.split(" ");
        try {
            x = Integer.parseInt(commandArgs[0]);
            y = Integer.parseInt(commandArgs[1]);
        } catch(NumberFormatException e){
            System.out.println("ERROR: THE ARGUMENTS MUST BE NUMERIC!");
            return;
        }
        for (int i = 2; i<commandArgs.length; i++){
            switch(commandArgs[i]){
                case "north" -> dirs.insertLast(Direction.NORTH);
                case "south" -> dirs.insertLast(Direction.SOUTH);
                case "west" -> dirs.insertLast(Direction.WEST);
                case "east" -> dirs.insertLast(Direction.EAST);
                default -> dirs.insertLast(Direction.INVALID);
            }
        }
        
        Iterator<Move> moveIterator = game.movePlayerAt(x, y, dirs);
        while (moveIterator.hasNext()) {
            Move move = moveIterator.next();
            switch (move.getEvent()) {
                case INVALID_POSITION -> System.out.println("Invalid position.");
                case INVALID_DIRECTION -> System.out.println("Invalid direction.");
                case NO_PLAYER -> System.out.println("No player in that position.");
                case INVALID_MOVE -> System.out.println("Invalid move.");
                case POSITION_OCCUPIED -> System.out.println("Position occupied.");
                case OFF_THE_MAP -> System.out.println("Trying to move off the map.");
                case BUNKER_SEIZED -> System.out.println("Bunker seized.");
                case WON_FIGHT -> System.out.println("Won the fight.");
                case PLAYER_ELIMINATED -> System.out.println("Player eliminated.");
                case WON_AND_SEIZED -> System.out.println("Won the fight and bunker seized.");
                case PLAYER_NOT_FROM_TEAM -> System.out.println("Unable to move player from the enemy team.");
                case MOVE_SUCCESS -> System.out.printf("%s player in position (%d, %d)\n", move.color.getName(), move.x, move.y);
                case GAME_OVER -> {
                    System.out.printf("Winner is %s.\n", game.getWinner().getName());
                    game = null;
                }
                default -> System.out.println("Unexpected error.");
            }
        }
    }

    /**
     * Attacking a team
     */
    private static void attack(Game game) {
        if (game == null) {
            System.out.println("Invalid command.");
            return;
        }
        String attackerTeam = game.getCurrentTeam().getName();

        switch (game.attack()){
            case TEAM_ELIM_AND_GAME_OVER -> {
                System.out.println("All players eliminated.");
                System.out.printf("Winner is %s.\n", game.getWinner().getName());
                game = null;
            }
            case GAME_OVER -> {
                if (game.getWinner().getName().equals(attackerTeam)) map(attackerTeam);
                System.out.printf("Winner is %s.\n", game.getWinner().getName());
                game = null;
            }
            case TEAM_ELIMINATED -> System.out.println("All players eliminated.");
            case ATTACK_SUCCESS -> map(attackerTeam);
            default -> System.out.println("Unexpected error.");
        }
    }
}