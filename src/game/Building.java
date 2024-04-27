package game;

import game.players.Player;

/**
 * Interface representing a Building
 */
public interface Building extends Entity {
    /**
     * Executes the instructions that happens in the game at the end of the turn
     */
    void endTurn();

    /**
     * Creates an instance of a {@link Player} in the building
     * and adds it to the building's {@link Team} and {@link Field}
     * @param color {@link Player.PlayerColor} of the player to create
     * @return {@link CreateStatus} indicating the status of player creation<br>
     * {@link CreateStatus#OK} if the player was successfully created
     * {@link CreateStatus#NOT_ENOUGH_MONEY} if there isn't enough money to afford creating a new player
     * {@link CreateStatus#OCCUPIED} if the building is already occupied by another player
     */
    CreateStatus createPlayer(Player.PlayerColor color);

    /**
     * Unique identifier of a building
     * @return Name of the building
     */
    String name();

    /**
     * @return The amount of money in the building
     */
    int treasury();

    /**
     * Status of the {@link Building#createPlayer(Player.PlayerColor)} method
     */
    enum CreateStatus {
        OK, NOT_ENOUGH_MONEY, OCCUPIED
    }
}
