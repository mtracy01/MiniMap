package map.minimap.frameworks.gameResources;

import android.content.Context;

/**
 * Created by Matthew on 5/18/2015.
 * Class for managing the leave game event.  Mainly built to learn how greenrobot's EventBus library works
 */
public class QuitGameEvent {
    public Context eventContext;

    public void MessageEvent(Context eventContext){
        this.eventContext = eventContext;
    }
}
