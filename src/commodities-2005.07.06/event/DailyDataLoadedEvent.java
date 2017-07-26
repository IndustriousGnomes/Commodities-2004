/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

/**
 *  PriceEvent indicates that the current price has changed.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2005.05.02
 */
public class DailyDataLoadedEvent extends EventObject {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a DailyDataLoadedEvent to inform listeners that the daily prices
     *  have been loaded.
     *
     *  @param  source  the contract the event was issued for
     *  @param  date    the date of the price information
     */
    public DailyDataLoadedEvent(Object source) {
        super(source);
    }

}
