/*  _ Properly Documented
 */
package prototype.commodities.tests;

import java.util.*;

import prototype.commodities.*; // Debug only

/**
 *  MovingAverage calculates the moving average on a set of data.
 *  The sourceMap that is passed to MovingAverage must be of
 *  date/Double values in ascending order by date.  MovingAverage
 *  will then traverse the sourceMap to determine the moving average
 *  for a given date or for the entire range.
 *
 *  @author J.R. Titko
 */
public class MovingAverage {
    public static boolean DEBUG = false;

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
    public MovingAverage() {}

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
     */
    public TreeMap getMap() {
        return dataMap;
    }

    /**
     *  Get the moving average for the given date.
     */
    public Double get(java.util.Date date) {
        if (dataMap.containsKey(date)) {
            return (Double)dataMap.get(date);
        } else {
            return null;
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Add a date/value pair to the moving average.
     */
    public void add(java.util.Date date, Double value) {
        if ((dataMap.size() != 0) && ((java.util.Date)dataMap.lastKey()).before(date)) {
            dataMap.put(date, value);
            calculateMovingAverage(date, value);
        } else {
            dataMap.put(date, value);
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
     */
    protected void calculateMovingAverage(java.util.Date date, Double value) {
        window.addLast(value);
        windowTotal += (value).doubleValue();
        if (window.size() > interval) {
            windowTotal -= ((Double)window.removeFirst()).doubleValue();
            double movingAverage = ((windowTotal / interval) * 1000) / 1000;
            dataMap.put(date, new Double(movingAverage));
        }
    }
}
