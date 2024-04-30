import game.*;
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
 * @author Ilia Taitsel 67258, Taisiia Hlukha 67398 23/24
 */
public class Main {
    /**
     * Main method. Invokes the command interpreter
     * @param args command-line arguments (not used in this program)
     */
    public static void main(String[] args) {
        Game game = new PaintballGame();
        Scanner in = new Scanner(System.in);
        String command;
        do {
            String prefix = game.inProgress() ? game.currentTeam().name() : "";
            System.out.printf("%s> ", prefix);
            command = in.next().toLowerCase();
            handleCommand(game,in, command);
        }
        while (!command.equals(Commands.QUIT));
        in.close();
    }

    public static void handleCommand(Game game, Scanner in, String command) {
        switch (command) {
            case Commands.HELP -> help(game);
            case Commands.START_GAME -> newGame(game, in);
            case Commands.STATUS -> status(game);
            case Commands.MAP -> map(game);
            case Commands.BUNKERS -> bunkers(game);
            case Commands.PLAYERS -> players(game);
            case Commands.CREATE -> create(game, in);
            case Commands.MOVE -> move(game, in);
            case Commands.ATTACK -> attack(game);
            case Commands.QUIT -> quit(game);
            default -> System.out.println(Feedback.INVALID_COMMAND);
        }
    }

    /**
     * Informs the user about the available commands
     */
    private static void help(Game game){
        if (game.inProgress()) {
            System.out.print(Feedback.COMMANDS_IN_GAME);
            return;
        }
        System.out.print(Feedback.COMMANDS_NO_GAME);
    }

    /**
     * Stops the game
     * The program should end after this is called
     */
    private static void quit(Game game) {
        System.out.println(Feedback.GO_QUIT);
        game.stop();
    }

    /**
     * Attempts to start a new game<br>
     * If the amount of teams during game's initialization is less than 2,
     * the game will not start
     * @param in Scanner object to read user input
     */
    private static void newGame(Game game, Scanner in) {
        game.stop();
        int width = in.nextInt(), height = in.nextInt();
        int teamsNumber = in.nextInt(), bunkersNumber = in.nextInt();
        if (game.setField(width, height) != GameStatus.OK) {
            game.stop();
            System.out.println(Feedback.SIZE_NOT_OK);
            return;
        }
        addBunkers(game, in, bunkersNumber);
        addTeams(game, in, teamsNumber);
        if (game.start() != GameStatus.OK) {
            game.stop();
            System.out.println(Feedback.NOT_ENOUGH_TEAMS);
        }
    }

    /**
     * Initialize bunkers that will be in the new game
     * @param in Scanner object to read user input
     * @param bunkersNumber Amount of bunkers to expect from the user
     */
    private static void addBunkers(Game game, Scanner in, int bunkersNumber) {
        System.out.printf(Feedback.BUNKERS_LIST, bunkersNumber);
        for (int i = 0; i < bunkersNumber; i++) { 
            int x = in.nextInt(), y = in.nextInt(), treasury = in.nextInt();
            String name = in.nextLine().trim();
            GameStatus status = game.addBuilding(x, y, treasury, name);
            if (status == GameStatus.BUNKER_NOT_CREATED)
                System.out.println(Feedback.BUNKER_NOT_CREATED);
        }
    }

    /**
     * Initialize teams that will be in the new game
     * @param in Scanner object to read user input
     * @param teamsNumber Amount of teams to expect from the user
     */
    private static void addTeams(Game game, Scanner in, int teamsNumber) {
        System.out.printf(Feedback.TEAMS_LIST, teamsNumber);
        for (int i = 0; i < teamsNumber; i++) {
            String teamName = in.next(), bunkerName = in.nextLine().trim();
            GameStatus status = game.addTeam(teamName, bunkerName);
            if (status == GameStatus.TEAM_NOT_CREATED)
                System.out.println(Feedback.TEAM_NOT_CREATED);
        }
    }

    /**
     * Displays information on the current state of the game
     */
    private static void status(Game game) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            return;
        }
        System.out.printf("%d %d\n", game.width(), game.height());
        SizedIterator<Building> bunkers = game.buildings();
        System.out.printf(Feedback.BUNKERS_LIST, bunkers.size());
        for (int i = 0; i < bunkers.size(); i++) {
            Building bunker = bunkers.next();
            String teamName = Feedback.WITHOUT_OWNER;
            if (bunker.team() != null) teamName = bunker.team().name();
            System.out.printf("%s (%s)\n", bunker.name(), teamName);
        }
        SizedIterator<Team> teams = game.teams();
        System.out.printf(Feedback.TEAMS_LIST, teams.size());
        String teamList =  teams.next().name();
        for (int i = 1; i < teams.size(); i++) teamList += "; " + teams.next().name();
        System.out.println(teamList);
    }

    /**
     * Prints out the {@link Field.Map} from the point of view of the current {@link Team}
     */
    private static void map(Game game) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            return;
        }

        Field.Map map = game.map(game.currentTeam());
        printMap(map);
    }

    /**
     * Prints out the specified {@link Field.Map}
     * @param map The instance of {@link Field.Map} to print out
     */
    private static void printMap(Field.Map map) {
        int width = map.getWidth(), height = map.getHeight();
        System.out.printf("%d %d\n**", width, height);
        for (int i = 1; i < width; i++) System.out.printf("%d ", i);
        System.out.printf("%d\n", width);
        int i = 0;
        while (map.getMapCells().hasNext()) {
            Field.MapCell mapCell = map.getMapCells().next();
            char c = mapCellToChar(mapCell);
            if (i % width == 0) System.out.print(i / width + 1);
            System.out.printf(" %c", c);
            if (i % width == width - 1)  System.out.println();
            i++;
        }
    }
    
    /**
     * Converts a specified {@link Field.MapCell} to its {@code char} equivalent
     * @param mapCell Instance of a {@link Field.MapCell} to convert
     * @return Map cell char equivalent
     */
    private static char mapCellToChar(Field.MapCell mapCell) {
        return switch (mapCell) {
            case NONE -> MapChar.NOTHING;
            case BUILDING -> MapChar.BUNKER;
            case PLAYER -> MapChar.PLAYER;
            case BUILDING_AND_PLAYER -> MapChar.OCCUPIED_BUNKER;
        };
    }

    /**
     * Informs about the bunkers in the current team
     */
    private static void bunkers(Game game) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            return;
        }
        SizedIterator<Building> bunkers = game.currentTeam().buildings();
        if (bunkers.size() == 0) {
            System.out.println(Feedback.WITHOUT_BUNKERS);
            return;
        }
        System.out.printf(Feedback.BUNKERS_LIST, bunkers.size());
        for (int i = 0; i < bunkers.size(); i++){
            Building bunker = bunkers.next();
            int x = bunker.fieldLocation().getX(), y = bunker.fieldLocation().getY();
            System.out.printf(Feedback.BUNKER_INFO, bunker.name(), bunker.treasury(), x, y);
        }
    }

    /**
     * Informs about the players of the current team
     */
    private static void players(Game game) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            return;
        }
        SizedIterator<Player> players = game.currentTeam().players();
        if (players.size() == 0) {
            System.out.println(Feedback.WITHOUT_PLAYERS);
            return;
        }
        System.out.printf(Feedback.PLAYERS_LIST, players.size());
        for (int i = 0; i < players.size(); i++){ 
            Player player = players.next();
            String color = player.color().name().toLowerCase();
            int x = player.fieldLocation().getX(), y = player.fieldLocation().getY();
            System.out.printf(Feedback.PLAYER_INFO, color, x, y);
        }
    }

    /**
     * Create a player in a bunker
     * @param in Scanner object to read user input
     */
    private static void create(Game game, Scanner in) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            in.nextLine();
            return;
        }
        String playerTpe = in.next(), bunkerName = in.nextLine().trim();
        PlayerColor color = PlayerColor.fromName(playerTpe);
        GameResponse<CreateStatus> response = game.createPlayer(color, bunkerName);
        switch (response.getStatus()) {
            case OK -> {
                switch (response.getResult()) {
                    case OK -> System.out.printf(Feedback.PLAYER_CREATED, playerTpe, bunkerName);
                    case NOT_ENOUGH_MONEY -> System.out.println(Feedback.NOT_ENOUGH_COINS);
                    case OCCUPIED -> System.out.println(Feedback.BUNKER_OCCUPIED);
                }
            }
            case INVALID_PLAYER_COLOR -> System.out.println(Feedback.INVALID_PLAYER_COLOR);
            case INVALID_BUNKER_NAME -> System.out.println(Feedback.INVALID_BUNKER_NAME);
            case WRONG_TEAM_BUNKER -> System.out.println(Feedback.WRONG_TEAM_BUNKER);
            default -> System.out.println(Feedback.UNEXPECTED_ERROR);
        }
    }

    /**
     * Moves a player
     * @param in Scanner object to read user input
     */
    private static void move(Game game, Scanner in) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            in.nextLine();
            return;
        }
        int x = in.nextInt(), y = in.nextInt();
        String[] userDirs = in.nextLine().trim().split(" ");
        Array<Direction> dirs = new ArrayClass<>();
        for (int i = 0; i < userDirs.length; i++) dirs.insertLast(Direction.fromName(userDirs[i]));
        GameResponse<Iterator<Action>> response = game.movePlayerAt(x, y, dirs);
        switch (response.getStatus()) {
            case INVALID_POSITION -> System.out.println(Feedback.INVALID_POSITION);
            case NO_PLAYER -> System.out.println(Feedback.NO_PLAYER);
            case PLAYER_NOT_FROM_TEAM -> System.out.println(Feedback.PLAYER_NOT_FROM_TEAM);
            case OK, GAME_OVER -> {
                printMoves(response.getResult());
                if (response.getStatus() == GameStatus.GAME_OVER)
                    System.out.printf(Feedback.WINNER_IS, response.getWinner().name());
            }
            default -> System.out.println(Feedback.UNEXPECTED_ERROR);
        }
    }

    private static void printMoves(Iterator<Action> moves) {
        while (moves.hasNext()) {
            Action move = moves.next();
            ActionStatus status = move.getStatus();
            switch (status) {
                case INVALID_DIRECTION -> System.out.println(Feedback.INVALID_DIRECTION);
                case OFF_THE_MAP -> System.out.println(Feedback.OFF_THE_MAP);
                case POSITION_OCCUPIED -> System.out.println(Feedback.POSITION_OCCUPIED);
                case PLAYER_ELIMINATED -> System.out.println(Feedback.PLAYER_ELIMINATED);
                case INVALID_MOVE -> System.out.println(Feedback.INVALID_MOVE);
                case BUNKER_SEIZED -> System.out.println(Feedback.BUNKER_SEIZED);
                case WON_FIGHT -> System.out.println(Feedback.WON_FIGHT);
                case WON_AND_SEIZED -> System.out.println(Feedback.WON_AND_SEIZED);
                case NOTHING -> {}
                default -> System.out.println(Feedback.UNEXPECTED_ERROR);
            }
            if (status == ActionStatus.BUNKER_SEIZED || status == ActionStatus.WON_FIGHT ||
                status == ActionStatus.WON_AND_SEIZED || status == ActionStatus.NOTHING) {
                String color = move.getPlayer().color().name().toLowerCase();
                int newX = move.getLocation().getX(), newY = move.getLocation().getY();
                System.out.printf(Feedback.PLAYER_INFO, color, newX, newY);
            }
        }
    }

    /**
     * Attacking a team
     */
    private static void attack(Game game) {
        if (!game.inProgress()) {
            System.out.println(Feedback.INVALID_COMMAND);
            return;
        }
        GameResponse<Field.Map> response = game.playersAttack();
        switch (response.getStatus()) {
            case TEAM_ELIM_AND_GAME_OVER -> {
                System.out.println(Feedback.PLAYERS_ELIMINATED);
                System.out.printf(Feedback.WINNER_IS, response.getWinner().name());
            }
            case GAME_OVER -> {
                if (response.getWinner() == response.getResult().getTeam())
                    printMap(response.getResult());
                System.out.printf(Feedback.WINNER_IS, response.getWinner().name());
            }
            case TEAM_ELIMINATED -> System.out.println(Feedback.PLAYERS_ELIMINATED);
            case OK -> printMap(response.getResult());
            default -> System.out.println(Feedback.UNEXPECTED_ERROR);
        }
    }

    /**
     * Commands which allow users to interact with this program and the game
     */
    public static class Commands {
        public static final String
        START_GAME = "game", MOVE = "move", CREATE = "create",
        ATTACK = "attack", STATUS = "status", MAP = "map", BUNKERS = "bunkers",
        PLAYERS = "players", HELP = "help", QUIT = "quit";
    }

    /**
     * Feedback given by the program
     */
    public static class Feedback {
        public static final String
        COMMANDS_NO_GAME = String.format("""
        %s - Create a new game
        %s - Show available commands
        %s - End program execution
        """, Commands.START_GAME, Commands.HELP, Commands.QUIT),

        COMMANDS_IN_GAME = String.format("""
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
        """, Commands.START_GAME, Commands.MOVE, Commands.CREATE, Commands.ATTACK, Commands.STATUS,
        Commands.MAP, Commands.BUNKERS, Commands.PLAYERS, Commands.HELP, Commands.QUIT),

        GO_QUIT = "Bye.",
        SIZE_NOT_OK = "ERROR: FIELD RESOLUTION IS NOT OK",
        NOT_ENOUGH_TEAMS = "FATAL ERROR: Insufficient number of teams.",
        BUNKERS_LIST = "%d bunkers:%n",
        BUNKER_NOT_CREATED = "Bunker not created.",
        TEAMS_LIST = "%d teams:%n",
        TEAM_NOT_CREATED = "Team not created.",
        INVALID_COMMAND = "Invalid command.",
        WITHOUT_OWNER = "without owner",
        WITHOUT_BUNKERS = "Without bunkers.",
        BUNKER_INFO = "%s with %d coins in position (%d, %d)%n",
        WITHOUT_PLAYERS = "Without players.",
        PLAYERS_LIST = "%d players:%n",
        PLAYER_INFO = "%s player in position (%d, %d)%n",
        INVALID_PLAYER_COLOR = "Non-existent player type.",
        INVALID_BUNKER_NAME = "Non-existent bunker.",
        WRONG_TEAM_BUNKER = "Bunker illegally invaded.",
        BUNKER_OCCUPIED = "Bunker not free.",
        NOT_ENOUGH_COINS = "Insufficient coins for recruitment.",
        PLAYER_CREATED = "%s player created in %s%n",
        UNEXPECTED_ERROR = "Unexpected error",
        INVALID_POSITION = "Invalid position.",
        INVALID_DIRECTION = "Invalid direction.",
        NO_PLAYER = "No player in that position.",
        INVALID_MOVE = "Invalid move.",
        POSITION_OCCUPIED = "Position occupied.",
        OFF_THE_MAP = "Trying to move off the map.",
        BUNKER_SEIZED = "Bunker seized.",
        WON_FIGHT = "Won the fight.",
        PLAYER_ELIMINATED = "Player eliminated.",
        WON_AND_SEIZED = "Won the fight and bunker seized.",
        PLAYER_NOT_FROM_TEAM = "Unable to move player from the enemy team.",
        WINNER_IS = "Winner is %s.%n",
        PLAYERS_ELIMINATED = "All players eliminated.";
    }

    /**
     * {@code char} equivalent to {@link Field.MapCell}
     */
    public static class MapChar {
        public static final char
        NOTHING = '.', PLAYER = 'P', BUNKER = 'B', OCCUPIED_BUNKER = 'O';
    }
}