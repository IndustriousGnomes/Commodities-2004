/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

/**
 *  The DailyDataLoadedListener is an EventListener that listens for when
 *  the daily prices have been loaded.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2005.05.02
 */

public interface DailyDataLoadedListener extends EventListener {
    /**
     *  Invoked when the daily data has been loaded.
     *  @param  e   the DailyDataLoadedEvent
     */
    public void dailyDataLoaded(DailyDataLoadedEvent e);

}
