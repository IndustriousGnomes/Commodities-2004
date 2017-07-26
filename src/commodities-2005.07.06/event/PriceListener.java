/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

/**
 *  The PriceListener is an EventListener that listens for the individual price
 *  information to change.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public interface PriceListener extends EventListener {
    /**
     *  Invoked when the date changes.
     *  @param  e   the PriceEvent
     */
    public void changeDate(PriceEvent e);

    /**
     *  Invoked when an open price changes.
     *  @param  e   the PriceEvent
     */
    public void changeOpenPrice(PriceEvent e);

    /**
     *  Invoked when a high price changes.
     *  @param  e   the PriceEvent
     */
    public void changeHighPrice(PriceEvent e);

    /**
     *  Invoked when a low price changes.
     *  @param  e   the PriceEvent
     */
    public void changeLowPrice(PriceEvent e);

    /**
     *  Invoked when a close price changes.
     *  @param  e   the PriceEvent
     */
    public void changeClosePrice(PriceEvent e);

    /**
     *  Invoked when the open interest changes.
     *  @param  e   the PriceEvent
     */
    public void changeOpenInterest(PriceEvent e);

    /**
     *  Invoked when the volume changes.
     *  @param  e   the PriceEvent
     */
    public void changeVolume(PriceEvent e);

}
