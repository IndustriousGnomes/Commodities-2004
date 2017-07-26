/*  x Properly Documented
 */
package commodities.graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import commodities.contract.*;
import commodities.event.*;
import commodities.price.*;
import commodities.util.*;
import commodities.window.*;

/**
 *  GraphDailyPrices is an implementation of GraphRedrawListener.
 *
 *  @see    commodities.graph.Graph
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

//public class GraphDailyPrices implements GraphRedrawListener {
public class GraphDailyPrices implements GraphRedrawListener, ContractSelectionListener {
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
    private int graphNbr;
    /** The contract to be graphed. */
    private Contract contract;
    /** The Graph this set of statistics is being drawn in. */
    private Graph graph;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  The graph a contract to use in generating the daily price graph.
     *  By default, the graph number is reset.
     *
     *  @param  graph       the graph area to draw the prices
     */
    public GraphDailyPrices(Graph graph) {
        this(graph, true);
    }

    /**
     *  The graph a contract to use in generating the daily price graph to the
     *  supplied graph area.
     *  If the resetCount parameter is set to false, then this graph is
     *  a subsequent graph and is overlaid on the prior graphs.
     *
     *  @param  graph       the graph area to draw the prices
     *  @param  resetCount  false indicates a subsequent graph to the first
     */
    public GraphDailyPrices(Graph graph, boolean resetCount) {
        this.contract = null;

        this.graph   = graph;
        if (resetCount) {
            graphCount = 0;
        }
        graphNbr = graphCount++;
        graph.addListener(this);
        Contract.addSelectionListener(this);
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
        return 20;
    }

    /**
     *  Gets the maximum value represented in the graph.
     *
     *  @return         The data's maximum value.
     */
    public double getMaxValue() {
        if (contract == null) {
            return 0.0;
        }
        return contract.getContractHighPrice();
    }

    /**
     *  Gets the minimum value represented in the graph.
     *
     *  @return         The data's minimum value.
     */
    public double getMinValue() {
        if (contract == null) {
            return 0.0;
        }
            return contract.getContractLowPrice() ;
    }
    /**
     *  Gets the size of a tick for the commodity being drawn.
     *  This keeps the scale of the graph in multiples of this
     *  tick size.
     *
     *  @return         The data's tick size.
     */
    public double getTickSize() {
        if (contract == null) {
            return 0.0;
        }
        return contract.getCommodity().getTickSize();
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
        if (contract == null) {
            return false;
        }
        Rectangle graphRect = graph.getGraphRect();
        int xAxisLeft       = (int)graphRect.getX();

        g.setColor(colors[graphNbr]);
        g.drawString(contract.getCommodity().getNameExchange() + " - " + contract.getMonthFormatted(), xAxisLeft, yOffset+g.getFontMetrics().getAscent());
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
        if (contract == null) {
            return;
        }

        Graphics g = e.getGraphics();

        Rectangle graphRect    = graph.getGraphRect();
        int xAxisLeft    = (int)graphRect.getX();
        int xAxisRight   = (int)graphRect.getX() + (int)graphRect.getWidth();
        int yAxisBottom  = (int)graphRect.getY() + (int)graphRect.getHeight();

        double  minValue    = e.getMinValue();
        double  priceScale  = e.getYScale() * e.getMinTickSize();
        int     xScale      = e.getXScale();
        int     drawOffset  = (xScale - Graph.DEFAULT_X_SCALE) / 2;

        // Determine the date of the right side of the graph.
        CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
        calendar.clearTime();
        calendar.setNextDayOfWeek(CommodityCalendar.MONDAY);

        // Draw the price ranges
        for (int x = xAxisRight; x > xAxisLeft; x -= xScale) {
            g.setColor(colors[graphNbr]);

            Prices prices = contract.getPrices(new Date(calendar.getTimeInMillis()));
            if (prices != null) {
               for (int w = 0 - drawOffset; w <= 0 + drawOffset; w++) {
                    g.drawLine(x - 1 - drawOffset + graphNbr, (int)(yAxisBottom + w - (prices.getOpen() - minValue) / priceScale),
                               x - drawOffset + graphNbr, (int)(yAxisBottom + w - (prices.getOpen() - minValue) / priceScale));   // open

                    g.drawLine(x + w + graphNbr, (int)(yAxisBottom - (prices.getHigh() - minValue) / priceScale),
                               x + w + graphNbr, (int)(yAxisBottom - (prices.getLow() - minValue) / priceScale));      // range

                    g.drawLine(x + drawOffset + graphNbr, (int)(yAxisBottom + w - (prices.getClose() - minValue) / priceScale),
                               x + drawOffset + 1 + graphNbr, (int)(yAxisBottom + w - (prices.getClose() - minValue) / priceScale)); // close
                }
            }

            calendar.addWeekDays(-1);
        }
    }

/* *************************************** */
/* ***ContractSelectionListener METHODS*** */
/* *************************************** */
    /**
     *  Invoked when the contract selection changes.  This is a 2 pass
     *  execution so that some classes that need to execute first can
     *  process when init is true and the remaining listeners can
     *  process when init is false.
     *
     *  @param  e       the ContractSelectionEvent
     *  @param  init    true if first pass for initialization, otherwise false
     */
    public void selectContract(ContractSelectionEvent e, boolean init) {
        if (!init) {
            this.contract = (Contract)e.getSource();
            graphCount = 0;
            graph.addListener(this);
            graph.repaint();
        }
    }
}
