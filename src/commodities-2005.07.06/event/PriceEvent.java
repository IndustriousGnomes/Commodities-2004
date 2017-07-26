/*  x Properly Documented
 */
package commodities.event;

import java.util.*;

import commodities.contract.*;

/**
 *  PriceEvent indicates that the current price has changed.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class PriceEvent extends EventObject {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The date the price is good for. */
    private Date date;
    /** The current price item.  This can be a price, open interest, volume, or date. */
    private Object priceObject;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a PriceEvent to inform listeners that the current price has
     *  changed.
     *
     *  @param  source  the contract the event was issued for
     *  @param  date    the date of the price information
     *  @param  priceObject   the new price information
     */
    public PriceEvent(Object source, Date date, Object priceObject) {
        super(source);
        this.date = date;
        this.priceObject = priceObject;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the contract affected.
     *  @return     the contract
     */
    public Contract getContract() {
        return (Contract)source;
    }

    /**
     *  Get the date to display.
     *  @param      the price
     */
    public Date getDate() {
        return date;
    }

    /**
     *  Get the prices to display.
     *  @param      the price
     */
    public Double getPrice() {
        return (Double)priceObject;
    }

    /**
     *  Get the volume of transactions to display.
     *  @param      the volume
     */
    public Long getVolume() {
        return (Long)priceObject;
    }

    /**
     *  Get the open interest to display.
     *  @param      the open interest
     */
    public Long getOpenInterest() {
        return (Long)priceObject;
    }
}
