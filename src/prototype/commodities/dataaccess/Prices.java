/*  x Properly Documented
 */
package prototype.commodities.dataaccess;

import java.util.*;
import prototype.commodities.*; // debug only

/**
 *  The Prices class contains the prices for a single commoditiy
 *  contract for a processing time period.  The prices that are
 *  containd in the association are the open, high, low, and
 *  closing prices for the period.  If available, the open interst
 *  and volume are also included.
 *
 *  @author J.R. Titko
 */

public class Prices {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The date timestamp for these prices */
    private Date    date;
    /** The opening price for the price range */
    private double  open;
    /** The high price for the price range */
    private double  high;
    /** The low price for the price range */
    private double  low;
    /** The closing price for the price range */
    private double  close;
    /** The number of open contracts */
    private long    interest;
    /** The number of trades occuring during the range */
    private long    volume;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a price group with an open, high, low, and close price.  Other values
     *  are set to 0.
     *
     *  @param  date    The date timestamp that this grouping is for.
     *  @param  open    The opening price for this grouping.
     *  @param  high    The high price for this grouping.
     *  @param  low     The low price for this grouping.
     *  @param  close   The closing price for this grouping.
     */
    public Prices(Date date, double open, double high, double low, double close) {
        this(date, open, high, low, close, 0, 0);
    }

    /**
     *  Creates a price group with an open, high, low, and close price in addition to the
     *  open interst and volume for the grouping.
     *
     *  @param  date    The date timestamp that this grouping is for.
     *  @param  open    The opening price for this grouping.
     *  @param  high    The high price for this grouping.
     *  @param  low     The low price for this grouping.
     *  @param  close   The closing price for this grouping.
     *  @param  interst The open interst for this grouping.
     *  @param  volume  The volumne of trades for this grouping.
     */
    public Prices(Date date, double open, double high, double low, double close, long volume, long interest) {
        this.date   = date;
        this.open   = open;
        this.high   = high;
        this.low    = low;
        this.close  = close;
        this.interest = interest;
        this.volume = volume;
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the date timestamp of the price grouping.
     *  @return     the date of the price grouping.
     */
    public Date   getDate() { return date; }
    /**
     *  Get the opening price of the price grouping.
     *  @return     the open price
     */
    public double getOpen() { return open; }
    /**
     *  Get the high price of the price grouping.
     *  @return     the high price
     */
    public double getHigh() { return high; }
    /**
     *  Get the low price of the price grouping.
     *  @return     the low price
     */
    public double getLow()  { return low; }
    /**
     *  Get the closing price of the price grouping.
     *  @return     the close price
     */
    public double getClose(){ return close; }
    /**
     *  Get the open interest of the price grouping.
     *  @return     the open interest
     */
    public long   getInterest() { return interest; }
    /**
     *  Get the volume of trades of the price grouping.
     *  @return     the volume
     */
    public long   getVolume() { return volume; }

    /**
     *  Get the opening price of the price grouping as an object.
     *  @return     the open price as a Double
     */
    public Double getOpenObject()     { return new Double(open); }
    /**
     *  Get the high price of the price grouping as an object.
     *  @return     the high price as a Double
     */
    public Double getHighObject()     { return new Double(high); }
    /**
     *  Get the low price of the price grouping as an object.
     *  @return     the low price as a Double
     */
    public Double getLowObject()      { return new Double(low); }
    /**
     *  Get the closing price of the price grouping as an object.
     *  @return     the closing price as a Double
     */
    public Double getCloseObject()    { return new Double(close); }
    /**
     *  Get the open interest of the price grouping as an object.
     *  @return     the open interest as a Long
     */
    public Long   getInterestObject() { return new Long(interest); }
    /**
     *  Get the volume of trades of the price grouping as an object.
     *  @return     the volume as a Long
     */
    public Long   getVolumeObject()   { return new Long(volume); }
}
