/*  _ Properly Documented
 */
package commodities.tests.technical;

import java.util.*;

/**
 *  MovingAverageExponential calculates the moving average on a set of data.
 *  The sourceMap that is passed to MovingAverageExponential must be of
 *  date/Double values in ascending order by date.  MovingAverageExponential
 *  will then traverse the sourceMap to determine the moving average
 *  for a given date or for the entire range.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.12
 */

public class MovingAverageExponential extends MovingAverage {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The value of the current moving average. */
    private double movingAverage = 0;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public MovingAverageExponential(int expPercent, TreeMap sourceMap) {
        this.interval = expPercent;
        this.sourceMap = sourceMap;
        calculateMovingAverages();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Calculate the moving averages.
     */
    protected void calculateMovingAverages() {
        movingAverage = 0;
        super.calculateMovingAverages();
    }

    /**
     *  Calculate the moving averages.
     */
    protected void calculateMovingAverage(java.util.Date date, Double value) {
        if (movingAverage > 0) {
            movingAverage = value.doubleValue() * (interval / 100.0) + movingAverage * ((100 - interval) / 100.0);
        } else {
            movingAverage = value.doubleValue();
        }
        dataMap.put(date, new Double(movingAverage));
    }
}
