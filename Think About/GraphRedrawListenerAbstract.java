/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import commodities.*; // Debug only
import commodities.util.*;
import commodities.dataaccess.*;

import commodities.tests.*;

//import java.io.*;
//import java.sql.*;
//import commodities.dataaccess.database.*;

/**
 *  The GraphRedrawListenerAbstract implements the common methods of the
 *  GraphRedrawListener.
 *
 *  @author J.R. Titko
 */
public abstract class GraphRedrawListenerAbstract implements GraphRedrawListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A counter of how many GraphRedrawListenerAbstract are present. */
    protected static int          graphCount = 0;
    /** The colors that each graph should use based on the graph's number. */
    protected static Color        colors[][] = {{Color.BLACK, Color.RED, Color.MAGENTA, Color.PINK},
                                                {Color.BLUE, Color.CYAN, Color.GRAY}};

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The name of the test. */
    protected String testName = "";

    /** The contract to perform the test functions on. */
    protected Contract contract;
    /** The statistical data for this test. */
    protected Collection statCollection;
    /** The minimum value results in this test */
    protected double statDataMin = 9999999;
    /** The maximum value results in this test */
    protected double statDataMax = 0;

    /** The graph to draw in */
    protected Graph   graph;
    /** The number of this price graph. */
    protected int     graphNbr;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  The graph and contract to use in generating the daily price graph.
     *  If the resetCount parameter is set to false, then this graph is
     *  a subsequent graph and is overlaid on the prior graphs.
     *
     *  @param  contract    the contract to be graphed
     *  @param  resetCount  false indicates a subsequent graph to the first
     */
    public GraphRedrawListenerAbstract(Contract     contract,
                                       String       testName,
                                       Collection   statCollection,
                                       double       statDataMax,
                                       double       statDataMin) {
Debug.println(this, "GraphRedrawListenerAbstract start");

        this.contract       = contract;
        this.testName       = testName;
        this.statCollection = statCollection;
        this.statDataMax    = statDataMax;
        this.statDataMin    = statDataMin;

Debug.println(this, "GraphRedrawListenerAbstract end");
    }

/* *************************************** */
/* *** GraphRedrawListener METHODS     *** */
/* *************************************** */

    /**
     *  Gets the number of pixels that should be added as white
     *  space to the top and bottom of the graph when calculating
     *  the scale of the graph.  This value is inside the graph's
     *  frame and therefore reduces the amount of drawing space
     *  by twice its amount.
     *
     *  @return         The number of pixels to use at the
     *                  top and bottom of the graph.
     */
    public double getYBuffer() {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.getYBuffer() only");
        return 0;
    }

    /**
     *  Gets the maximum value represented in the graph.
     *
     *  @return         The data's maximum value.
     */
    public double getMaxValue() {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.getMaxValue() only");
        return statDataMax;
    }

    /**
     *  Gets the minimum value represented in the graph.
     *
     *  @return         The data's minimum value.
     */
    public double getMinValue() {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.getMinValue() only");
        return statDataMin;
    }

    /**
     *  Gets the size of a tick for the commodity being drawn.
     *  This keeps the scale of the graph in multiples of this
     *  tick size.
     *
     *  @return         The data's tick size.
     */
    public double getTickSize() {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.getTickSize() only");
        return Commodity.bySymbol(contract.getSymbol()).getTickSize();
    }

    /**
     *  Draws the title of the graph at the top of the graph offset by
     *  the other graph titles that are alread there.
     *
     *  @return         always returns true
     */
    public boolean redrawTitle(Graphics g, int yOffset) {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.redrawTitle() start");
        Rectangle graphRect = graph.getGraphRect();
        int xAxisLeft       = (int)graphRect.getX();

        boolean firstTest = true;
        int graphSeq = 0;

        g.setColor(Color.BLACK);
        String testNameString = testName + ": ";
        g.drawString(testNameString, xAxisLeft, yOffset+g.getFontMetrics().getAscent());
        int testNameOffset = g.getFontMetrics().stringWidth(testNameString);

        Iterator it = statCollection.iterator();
        while (it.hasNext()) {
            if (!firstTest) {
                g.setColor(colors[graphNbr][0]);
                g.drawString(", ", xAxisLeft + testNameOffset, yOffset+g.getFontMetrics().getAscent());
                testNameOffset += g.getFontMetrics().stringWidth(", ");
            }
            g.setColor(colors[graphNbr][graphSeq]);
            firstTest = false;
            String title = ((StatsAbstract)it.next()).getName();
            g.drawString(title, xAxisLeft + testNameOffset, yOffset+g.getFontMetrics().getAscent());
            testNameOffset += g.getFontMetrics().stringWidth(title);

            graphSeq++;
            if (graphSeq >= colors[graphNbr].length) {
                graphSeq--;
            }
        }
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.redrawTitle() end");
        return true;
    }

    /**
     *  Draws the graph.
     *
     *  @param  e   the event containing the graph to draw into
     */
    public void redrawComponent(GraphRedrawEvent e) {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.redrawComponent() start");
        Graphics g = e.getGraphics();

        Rectangle graphRect    = graph.getGraphRect();
        int xAxisLeft   = (int)graphRect.getX();
        int xAxisRight  = (int)graphRect.getX() + (int)graphRect.getWidth();
        int yAxisBottom = (int)graphRect.getY() + (int)graphRect.getHeight();

        double minValue = e.getMinValue();
        double scale    = e.getYScale() * e.getMinTickSize();

        int graphSeq = 0;
        // Draw the price ranges
        Iterator it = statCollection.iterator();
        while (it.hasNext()) {
            g.setColor(colors[graphNbr][graphSeq]);
            // Determine the date of the right side of the graph.
            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            calendar.clearTime();
            calendar.setNextDayOfWeek(CommodityCalendar.MONDAY);

            double oldValue = 0.0;
            double newValue = 0.0;
            int    xOld = xAxisRight;
            StatsAbstract stats = (StatsAbstract)it.next();
            for (int x = xAxisRight; x > xAxisLeft; x -= Graph.PIXEL_SCALE) {
                Double value = (Double)stats.getData(new java.util.Date(calendar.getTimeInMillis()));
                if (value != null) {
                    newValue = value.doubleValue();
                    if (oldValue!= 0.0) {
                        g.drawLine(xOld, (int)(yAxisBottom - (oldValue - minValue) / scale), x, (int)(yAxisBottom - (newValue - minValue) / scale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldValue - minValue) / scale), x, (int)(yAxisBottom + 1 - (newValue - minValue) / scale));
                    }
                    oldValue = newValue;
                    xOld = x;
                }
                calendar.addWeekDays(-1);
            }
            graphSeq++;
            if (graphSeq >= colors[graphNbr].length) {
                graphSeq--;
            }
        }
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.redrawComponent() end");
    }

    /**
     *  Remove all listeners.
     *
     *  @param  resetCount  false indicates a subsequent graph to the first
     */
    public void removeListeners() {
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.removeListeners() start");
        graph.removeListener(this);
Debug.println(DEBUG, this, "GraphRedrawListenerAbstract.removeListeners() end");
    }
}
