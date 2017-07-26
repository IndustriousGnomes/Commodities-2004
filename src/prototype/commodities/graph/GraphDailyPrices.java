/*  x Properly Documented
 */
package prototype.commodities.graph;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.dataaccess.*;

public class GraphDailyPrices implements GraphRedrawListener {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A counter of how many GraphDailyPrices are present. */
    private static int      graphCount = 0;
    /** The colors that each graph should use based on the graph's number. */
    private static Color    colors[] = {Color.BLUE, Color.GRAY};

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The number of this price graph. */
    private int         graphNbr;
    /** The contract to be graphed. */
    private Contract contract;
    /** The Graph this set of statistics is being drawn in. */
    private Graph graph;


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  The graph and contract to use in generating the daily price graph.
     *  By default, the graph number is reset.
     *
     *  @param  contract    the contract to be graphed
     */
    public GraphDailyPrices(Contract contract) {
        this(contract, true);
    }

    /**
     *  The graph and contract to use in generating the daily price graph.
     *  If the resetCount parameter is set to false, then this graph is
     *  a subsequent graph and is overlaid on the prior graphs.
     *
     *  @param  contract    the contract to be graphed
     *  @param  resetCount  false indicates a subsequent graph to the first
     */
    public GraphDailyPrices(Contract contract, boolean resetCount) {
Debug.println(this, "GraphDailyPrices start");
        this.contract = contract;

        this.graph   = CommodityJFrame.graph;
        if (resetCount) {
            graphCount = 0;
        }
        graphNbr = graphCount++;
        graph.addListener(this);
Debug.println(this, "GraphDailyPrices end");
    }

/* *************************************** */
/* *** AbstractGraphRedraw METHODS     *** */
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
        return 20;
    }

    /**
     *  Gets the maximum value represented in the graph.
     *
     *  @return         The data's maximum value.
     */
    public double getMaxValue() {
        return contract.getMaxPrice();
    }

    /**
     *  Gets the minimum value represented in the graph.
     *
     *  @return         The data's minimum value.
     */
    public double getMinValue() {
            return contract.getMinPrice();
        }
    /**
     *  Gets the size of a tick for the commodity being drawn.
     *  This keeps the scale of the graph in multiples of this
     *  tick size.
     *
     *  @return         The data's tick size.
     */
    public double getTickSize() {
        return Commodity.bySymbol(contract.getSymbol()).getTickSize();
    }

    /**
     *  Defines how the title for the graph is to be drawn.
     *  The yOffset is used if multiple titles appear in
     *  the same graph to offset them so they dont write over
     *  each other.
     *
     *  @param  g       The graphics area to draw in
     *  @param  yOffset The y-axis offset the title should have
     *  @return         True if a title was drawn.
     */
    public boolean redrawTitle(Graphics g, int yOffset) {
        Commodity commodity = Commodity.bySymbol(contract.getSymbol());
        Rectangle graphRect = graph.getGraphRect();
        int xAxisLeft       = (int)graphRect.getX();

        g.setColor(colors[graphNbr]);
        g.drawString(commodity.getNameExchange() + " - " + contract.getMonthFormatted(), xAxisLeft, yOffset+g.getFontMetrics().getAscent());
        return true;
   }

    /**
     *  Draws the graph using the GraphRedrawEvent.  This is limited
     *  to the statistical part of the graph and not the graph frame
     *  or scale indicators.
     *
     *  @param  e       The GraphRedrawEvent
     */
    public void redrawComponent(GraphRedrawEvent e) {
Debug.println(this, "redrawComponent start");
        Graphics g = e.getGraphics();
Debug.println(this, "source = " + e.getSource());

        Rectangle graphRect    = graph.getGraphRect();
        int xAxisLeft    = (int)graphRect.getX();
        int xAxisRight   = (int)graphRect.getX() + (int)graphRect.getWidth();
        int yAxisBottom  = (int)graphRect.getY() + (int)graphRect.getHeight();

        double minValue     = e.getMinValue();
        double priceScale   = e.getYScale() * e.getMinTickSize();;

        // Determine the date of the right side of the graph.
        CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
        calendar.clearTime();
        calendar.setNextDayOfWeek(CommodityCalendar.MONDAY);

        // Draw the price ranges
        for (int x = xAxisRight; x > xAxisLeft; x -= Graph.PIXEL_SCALE) {
            g.setColor(colors[graphNbr]);

            Prices prices = contract.getPrices(new Date(calendar.getTimeInMillis()));
            if (prices != null) {
                g.drawLine(x-1+graphNbr, (int)(yAxisBottom - (prices.getOpen() - minValue) / priceScale), x+graphNbr, (int)(yAxisBottom - (prices.getOpen() - minValue) / priceScale));   // open
                g.drawLine(x+graphNbr, (int)(yAxisBottom - (prices.getHigh() - minValue) / priceScale), x+graphNbr, (int)(yAxisBottom - (prices.getLow() - minValue) / priceScale));      // range
                g.drawLine(x+graphNbr, (int)(yAxisBottom - (prices.getClose() - minValue) / priceScale), x+1+graphNbr, (int)(yAxisBottom - (prices.getClose() - minValue) / priceScale)); // close
            }

            calendar.addWeekDays(-1);
        }
Debug.println(this, "redrawComponent end");
    }
}
