package map.minimap.frameworks;

/**
 * Created by joe on 2/21/2015.
 */
public abstract class Game {

    /*
     *   Each specific game type will be a subclass of Game
     *   Game will not actually provide much except for a means of
     *      refering to any game type as a Game
     */

    public Game() {}
    public abstract void processLogic ();
    public abstract void handleMessage();

}
