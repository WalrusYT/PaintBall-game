package game;

/**
 * Class that represents a Bunker
 */
public class Bunker extends Entity implements Building {
    /**
     * Amount of coins in the bunker
     */
    private int treasury;
    /**
     * Name of the bunker
     */
    private String name;

    /**
     * Constructs an object Bunker with the given treasury, coordinate x, coordinate y and name.
     * @param treasury Amount of coins in the bunker
     * @param x Coordinate X of the bunker
     * @param y Coordinate Y of the bunker
     * @param name Name of the bunker
     */
    Bunker(int treasury, int x, int y, String name){
        super(x,y);
        this.treasury = treasury;
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the amount of coins in a treasury of the bunker
     * @return the amount of coins in a treasury of the bunker
     */
    public int getTreasury(){
        return treasury;
    }
    @Override
    public void endTurn(){
        treasury++;
    }

    /**
     * Sets the amount of coins in a treasury of the bunker
     * @param treasury the amount of coins in a treasury of the bunker
     */
    public void setTreasury(int treasury){
        this.treasury = treasury;
    }
}
