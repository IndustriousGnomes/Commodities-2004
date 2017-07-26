/*  x Properly Documented
 */
package commodities.tests.technical;

import java.util.*;

/**
 *  MovingAverage calculates the moving average on a set of data.
 *  The sourceMap that is passed to MovingAverage must be of
 *  date/Double values in ascending order by date.  MovingAverage
 *  will then traverse the sourceMap to determine the moving average
 *  for a given date or for the entire range.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class MovingAverage {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Moving Average Intervals */
    protected int interval;
    /** Data map of date/Double values */
    protected TreeMap sourceMap;
    /** Moving averages map of date/Double values */
    protected TreeMap dataMap = new TreeMap();

    /** The window of data being analyzed for this interval. */
    protected LinkedList window = new LinkedList();
    /** The value of the interval within the window. */
    protected double windowTotal = 0;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  A default constructor so other methods can extend this one.
     */
    public MovingAverage() {}

    /**
     *  Creates a moving average test based on the interval and data
     *  supplied in the sourceMap.
     *
     *  @param  interval    the interval to be used in calculating the moving average
     *  @param  sourceMap   the raw data to be used in calculating the moving average
     */
    public MovingAverage(int interval, TreeMap sourceMap) {
        this.interval = interval;
        this.sourceMap = sourceMap;
        calculateMovingAverages();
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the map of moving average values in the form
     *  of date/Double values.
     *
     *  @return     the map of the moving average data
     */
    public TreeMap getMap() {
        return dataMap;
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Add a date/value pair to the moving average.
     *
     *  @param  date    the date of the new moving average data
     *  @param  value   the value of the moving average on the date
     */
    public void add(java.util.Date date,
                    Double value) {
System.out.println("MovingAverage.add()");
        if ((dataMap.size() != 0) && ((java.util.Date)dataMap.lastKey()).before(date)) {
            sourceMap.put(date, value);
            calculateMovingAverage(date, value);
        } else {
            sourceMap.put(date, value);
            calculateMovingAverages();
        }
    }

    /**
     *  Calculate the moving averages.
     */
    protected void calculateMovingAverages() {
        window = new LinkedList();
        windowTotal = 0;

        Iterator it = sourceMap.keySet().iterator();
        while (it.hasNext()) {
            java.util.Date date = (java.util.Date)it.next();
            calculateMovingAverage(date, (Double)sourceMap.get(date));
        }
    }

    /**
     *  Calculate the moving averages.
     *
     *  @param  date    the date of the raw data
     *  @param  value   the raw value to add to the calculation
     */
    protected void calculateMovingAverage(java.util.Date date,
                                          Double value) {
        window.addLast(value);
        windowTotal += (value).doubleValue();
        if (window.size() > interval) {
            windowTotal -= ((Double)window.removeFirst()).doubleValue();
            double movingAverage = ((windowTotal / interval) * 1000) / 1000;
            dataMap.put(date, new Double(movingAverage));
        }
    }
}
