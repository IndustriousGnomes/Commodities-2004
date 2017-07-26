/*  x Properly Documented
 */
package commodities.tests.technical;

import java.util.*;

import commodities.util.*;

/**
 *  Cycle calculates the cycle on a set of data.
 *  The sourceMap that is passed to Cycle must be of
 *  date/Double values in ascending order by date of the critical points.
 *  Cycle will then traverse the sourceMap to determine the moving average
 *  for a given date or for the entire range.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class Cycle {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Cycle Intervals */
    protected int interval;
    /** Data map of date values */
    protected TreeSet sourceData;
    /** Cycle map of date/Double values */
    protected TreeMap dataMap = new TreeMap();

    /** The dates for calculating days difference between dates */
    protected ArrayList dateList = new ArrayList();
    /** The number of days difference between dates */
    protected ArrayList differenceList = new ArrayList();

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates a cycle test based on the interval and data
     *  supplied in the sourceMap.
     *
     *  @param  interval    the interval to be used in calculating the moving average
     *  @param  sourceMap   the raw data to be used in calculating the moving average
     */
    public Cycle(int interval, TreeSet sourceData) {
        this.interval = interval;
        this.sourceData = sourceData;
        calculateCycle();
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the map of cycle values in the form
     *  of from date/to date values.
     *
     *  @return     the map of the cycle data
     */
    public TreeMap getMap() {
        return dataMap;
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Add a date/value pair to the cycle.
     *
     *  @param  date    the date of the new cycle data
     *  @param  value   the value of the data on the date
     */
    public void add(java.util.Date date) {
        if ((dataMap.size() != 0) && ((java.util.Date)dataMap.lastKey()).before(date)) {
            sourceData.add(date);
            calculateCycle(date);
        } else {
            sourceData.add(date);
            calculateCycle();
        }
    }

    /**
     *  Calculate the cycle.
     */
    protected void calculateCycle() {
        if (sourceData.size() < 2) {
            return;
        }
        java.util.Date firstDate = null;
        java.util.Date lastDate = null;
        CommodityCalendar cal = null;

        dateList = new ArrayList(sourceData);
        differenceList = new ArrayList();

        Iterator it = dateList.iterator();
        firstDate = (java.util.Date)it.next();
        while (it.hasNext()) {
            java.util.Date secondDate = (java.util.Date)it.next();
            int difDays = CommodityCalendar.getDaysDifference(secondDate, firstDate);
System.out.println("second date = " + secondDate + "   -   first date = " + firstDate + "  = " + difDays);
            differenceList.add(new Integer(difDays));
            firstDate = secondDate;
        }

        int count = 0;
        int accum = 0;
        int tolerance = (int)(interval / 5) + 1;
        firstDate = (java.util.Date)dateList.get(0);
        lastDate = null;
        it = differenceList.iterator();
        while (it.hasNext()) {
            int span = ((Integer)it.next()).intValue();
            if ((accum == 0) || (accum + span) <= (interval + tolerance)) {
// needs fixed
                if ((accum + span) >= interval - tolerance) {
                    lastDate = (java.util.Date)dateList.get(count + 1);
//System.out.println("start date = " + firstDate + " - end date = " + lastDate + " - span = " + (accum + span));
                    dataMap.put(firstDate, lastDate);
                    firstDate = lastDate;
                    accum = 0;
                } else {
                    accum += span;
                }
            } else if (span > (interval + tolerance)) {
                do {
                    int d = interval - accum;
                    cal = (CommodityCalendar)CommodityCalendar.getInstance();
                    cal.setTime(firstDate);
                    cal.clearTime();
                    cal.addWeekDays(interval);
                    lastDate = cal.getTime();
//System.out.println("start date = " + firstDate + " - end date = " + lastDate + " - span = " + (interval) + " - left over span = " + (span - d));
                   dataMap.put(firstDate, lastDate);
                    firstDate = lastDate;
                    span -= d;
                    accum = 0;
                } while (span > (interval - tolerance));
                accum = span;
            } else {
                lastDate = (java.util.Date)dateList.get(count);
//System.out.println("start date = " + firstDate + " - end date = " + lastDate + " - span = " + (accum));
                dataMap.put(firstDate, lastDate);
                firstDate = lastDate;
                accum = span;
            }
            count++;
        }

        dataMap.put(firstDate, firstDate);
    }

    /**
     *  Calculate the cycle.
     *
     *  @param  date    the date of the raw data
     *  @param  value   the raw value to add to the calculation
     */
    protected void calculateCycle(java.util.Date date) {
        calculateCycle();
    }
}
