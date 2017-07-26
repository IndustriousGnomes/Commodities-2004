/*  _ Properly Documented
 */
package commodities.tests.technical;

import java.util.*;

import commodities.*; // Debug only

/**
 *  MovingAverageTriangle calculates the moving average on a set of data.
 *  The sourceMap that is passed to MovingAverageTriangle must be of
 *  date/Double values in ascending order by date.  MovingAverageTriangle
 *  will then traverse the sourceMap to determine the moving average
 *  for a given date or for the entire range.
 *
 *  @author J.R. Titko
 */

public class MovingAverageTriangle extends MovingAverage {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The smoothing Moving Average interval */
    private int smoothedInterval = 0;
    /** The window of smoothed data being analyzed for this interval. */
    private LinkedList smoothedWindow = new LinkedList();
    /** The value of the smoothed interval within the window. */
    private double smoothedWindowTotal = 0;


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    public MovingAverageTriangle(int interval, TreeMap sourceMap) {
        this.interval = interval;
        this.sourceMap = sourceMap;
        smoothedInterval = (int)(interval / 2.0 + 0.5);

        calculateMovingAverages();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Calculate the moving averages.
     */
    protected void calculateMovingAverages() {
        smoothedWindow = new LinkedList();
        smoothedWindowTotal = 0;
        super.calculateMovingAverages();
    }

    /**
     *  Calculate the moving averages.
     */
    protected void calculateMovingAverage(java.util.Date date, Double value) {
        window.addLast(value);
        windowTotal += (value).doubleValue();

        if (window.size() > interval) {
            windowTotal -= ((Double)window.removeFirst()).doubleValue();

            double primaryValue = ((windowTotal / interval) * 1000) / 1000;

            smoothedWindow.addLast(new Double(primaryValue));
            smoothedWindowTotal += primaryValue;
            if (smoothedWindow.size() > smoothedInterval) {
                smoothedWindowTotal -= ((Double)smoothedWindow.removeFirst()).doubleValue();

                double smoothedValue = ((smoothedWindowTotal / smoothedInterval) * 1000) / 1000;
                dataMap.put(date, new Double(smoothedValue));
            }
        }
    }
}
