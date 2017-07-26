/*  x Properly Documented
 */
package commodities.tests.technical;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import commodities.commodity.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.dataaccess.database.*;
import commodities.event.*;
import commodities.graph.*;
import commodities.tests.*;
import commodities.util.*;

/**
 *  The TechnicalTestAbstract implements the common methods of the
 *  TechnicalTestInterface.  It also supplies methods for storing
 *  data to a database.
 *
 *  REQUIREMENT FROM TechnicalTestInterface:
 *  All classes that extend this abstract must contain the following 2 constructors:
 *      Test() - Used for retrieving the name of the test ONLY!
 *      Test(commodities.dataaccess.Contract) - For all other accesses.
 *  This is required since the getName() method must be included in every class but
 *  is a static name that must also be used to populate the Test menu.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public abstract class TechnicalTestAbstract implements TechnicalTestInterface {
//remove
   private static int testId = 0;


/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    protected static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** A reference to the test manager. */
    protected static TestManager testManager = TestManager.instance();

    /** A counter of how many occurances of this test are present. */
    protected static int          graphCount = 0;
    /** The colors that each graph should use based on the graph's number. */
    protected static Color        colors[][] = {{Color.BLACK, Color.RED, Color.MAGENTA, Color.PINK, Color.GREEN, Color.GRAY, Color.CYAN},
                                                {Color.BLUE, Color.CYAN, Color.GRAY}};

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The name of the test. */
    protected String testName = "";
    /** The name of the DB table. */
    protected String sqlTable = "";

    /** The contract to perform the test functions on. */
    protected Contract contract;
    /** The statistical data for this test. */
    protected Set statCollection = new TreeSet();
    /** The test ids of the stats used for recommendations. */
    protected Set recommendCollection = new TreeSet();
    /** The minimum value results in this test */
    protected double statDataMin = 9999999;
    /** The maximum value results in this test */
    protected double statDataMax = 0;

    /** The data for this test has been loaded. */
    private boolean dataLoaded = false;

    /** Map of stats loaded by test id */
    protected Map tableByTestId = new TreeMap();

    /** The collection of recommendations for each set of parameters
     *  that are used for this test.
     */
//    protected Collection recommendations = null;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  GraphPanelAbstract provides an abstract class for
     *  drawing graphs and supplying default methods for
     *  the GraphRedrawListener.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    protected abstract class GraphPanelAbstract implements GraphRedrawListener {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The graph to draw in */
        protected Graph   graph;
        /** The number of this price graph. */
        protected int     graphNbr;

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
            return 5;
       }

        /**
         *  Gets the maximum value represented in the graph.
         *
         *  @return         The data's maximum value.
         */
        public double getMaxValue() {
            return statDataMax;
        }

        /**
         *  Gets the minimum value represented in the graph.
         *
         *  @return         The data's minimum value.
         */
        public double getMinValue() {
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
            return Commodity.bySymbol(contract.getSymbol()).getTickSize();
        }

        /**
         *  Draws the title of the graph at the top of the graph offset by
         *  the other graph titles that are alread there.
         *
         *  @return         always returns true
         */
        public boolean redrawTitle(Graphics g, int yOffset) {
            Rectangle graphRect = graph.getGraphRect();
            int xAxisLeft       = (int)graphRect.getX();

            boolean firstTest = true;
            int graphSeq = 0;

            g.setColor(Color.BLACK);
            String testNameString = testName + ": ";
            g.drawString(testNameString, xAxisLeft, yOffset+g.getFontMetrics().getAscent());
            int testNameOffset = g.getFontMetrics().stringWidth(testNameString);

            Iterator it = statCollection.iterator();
//            Iterator it = recommendCollection.iterator();
            while (it.hasNext()) {
                StatsAbstract stats = (StatsAbstract)it.next();
                int term = stats.getInterval();
                if (((term <= SHORT_TERM) && testManager.doGraphShortTermTests()) ||
                    ((term > SHORT_TERM)  && (term <= MEDIUM_TERM) && testManager.doGraphMediumTermTests()) ||
                    ((term > MEDIUM_TERM) && (term <= LONG_TERM)   && testManager.doGraphLongTermTests())) {

                    if (!firstTest) {
                        g.setColor(colors[graphNbr][0]);
                        g.drawString(", ", xAxisLeft + testNameOffset, yOffset+g.getFontMetrics().getAscent());
                        testNameOffset += g.getFontMetrics().stringWidth(", ");
                    }
                    g.setColor(colors[graphNbr][graphSeq]);
                    firstTest = false;
                    String title = stats.getName();
                    g.drawString(title, xAxisLeft + testNameOffset, yOffset+g.getFontMetrics().getAscent());
                    testNameOffset += g.getFontMetrics().stringWidth(title);

                    graphSeq++;
                    if (graphSeq >= colors[graphNbr].length) {
                        graphSeq--;
                    }
                }
            }
            return true;
        }

        /**
         *  Sets up the drawing space, scale, and then draws the graph.
         *
         *  @param  e   the event containing the graph to draw into
         */
        public void redrawComponent(GraphRedrawEvent e) {
            Graphics g = e.getGraphics();

            Rectangle graphRect    = graph.getGraphRect();
            int xAxisLeft   = (int)graphRect.getX();
            int xAxisRight  = (int)graphRect.getX() + (int)graphRect.getWidth();
            int yAxisTop    = (int)graphRect.getY();
            int yAxisBottom = (int)graphRect.getY() + (int)graphRect.getHeight();

            double minValue = e.getMinValue();
            double maxValue = e.getMaxValue();
            int    xScale   = e.getXScale();
            double yScale   = e.getYScale() * e.getMinTickSize();

            int graphSeq = 0;
            // Draw the price ranges
            Iterator it = statCollection.iterator();
//            Iterator it = recommendCollection.iterator();

            while (it.hasNext()) {
                StatsAbstract stats = (StatsAbstract)it.next();
                int term = stats.getInterval();
                if (((term <= SHORT_TERM) && testManager.doGraphShortTermTests()) ||
                    ((term > SHORT_TERM)  && (term <= MEDIUM_TERM) && testManager.doGraphMediumTermTests()) ||
                    ((term > MEDIUM_TERM) && (term <= LONG_TERM)   && testManager.doGraphLongTermTests())) {

                    g.setColor(colors[graphNbr][graphSeq]);

                    redrawGraph(g, stats, xAxisLeft, xAxisRight, yAxisTop, yAxisBottom, minValue, maxValue, xScale, yScale);

                    graphSeq++;
                    if (graphSeq >= colors[graphNbr].length) {
                        graphSeq--;
                    }
                }
            }
        }

        /**
         *  Aligns the right hand side of the graph to the next monday and then draws the graph.
         *
         *  @param  g           the graphics area to draw in
         *  @param  stats       the statistics to be draw
         *  @param  xAxisLeft   the possision of the left side of the graph
         *  @param  xAxisRight  the possision of the right side of the graph
         *  @param  yAxisTop    the possision of the top side of the graph
         *  @param  yAxisBottom the possision of the bottom side of the graph
         *  @param  minValue    the minimum value to be graphed
         *  @param  maxValue    the maximum value to be graphed
         *  @param  xScale      the horizontal scale the graph is to be drawn in
         *  @param  yScale      the vertical scale the graph is to be drawn in
         */
        public void redrawGraph(Graphics      g,
                                StatsAbstract stats,
                                int           xAxisLeft,
                                int           xAxisRight,
                                int           yAxisTop,
                                int           yAxisBottom,
                                double        minValue,
                                double        maxValue,
                                int           xScale,
                                double        yScale) {

            // Determine the date of the right side of the graph.
            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            calendar.clearTime();
            calendar.setNextDayOfWeek(CommodityCalendar.MONDAY);
            drawGraph(g, stats, calendar, xAxisLeft, xAxisRight, yAxisTop, yAxisBottom, minValue, maxValue, xScale, yScale);
        }

        /**
         *  Draw the graph itself.
         *
         *  @param  g           the graphics area to draw in
         *  @param  stats       the statistics to be drawn
         *  @param  calendar    the calendar to be used to process the graph starting on a monday
         *  @param  xAxisLeft   the position of the left side of the graph
         *  @param  xAxisRight  the position of the right side of the graph
         *  @param  yAxisTop    the position of the top side of the graph
         *  @param  yAxisBottom the position of the bottom side of the graph
         *  @param  minValue    the minimum value to be graphed
         *  @param  maxValue    the maximum value to be graphed
         *  @param  xScale      the horizontal scale the graph is to be drawn in
         *  @param  yScale      the vertical scale the graph is to be drawn in
         */
        public abstract void drawGraph(Graphics      g,
                                       StatsAbstract stats,
                                       CommodityCalendar calendar,
                                       int           xAxisLeft,
                                       int           xAxisRight,
                                       int           yAxisTop,
                                       int           yAxisBottom,
                                       double        minValue,
                                       double        maxValue,
                                       int           xScale,
                                       double        yScale);

        /**
         *  Draw the recommendation symbols.
         *
         *  @param  g           the graphics area to draw in
         *  @param  graphColor  the primary color of the graph
         *  @param  recommend   the recommendation to graph
         *  @param  x           the x position of the symbol
         *  @param  yBuy        the y position of a buy symbol
         *  @param  ySell       the y position of a sell symbol
         */
        public void drawRecommendation(Graphics g, Color graphColor, int recommend, int x, int yBuy, int ySell) {
            Polygon poly = null;

            switch (recommend) {
                case Recommendation.SELL:
                    g.setColor(Color.WHITE);
                    g.fillOval(x - 5, ySell - 5, 11, 11);
                    g.setColor(Color.BLACK);
                    g.drawOval(x - 5, ySell - 5, 10, 10);

                    g.setColor(graphColor);
                    poly = new SellArrow();
                    poly.translate(x, ySell);
                    g.fillPolygon(poly);
                    break;

                case Recommendation.SETTLE_SELL:
                    g.setColor(graphColor);
                    g.fillOval(x - 5, ySell - 5, 11, 11);
                    g.setColor(Color.BLACK);
                    g.drawOval(x - 5, ySell - 5, 10, 10);

                    g.setColor(Color.WHITE);
                    poly = new SellArrow();
                    poly.translate(x, ySell);
                    g.fillPolygon(poly);
                    break;

                case Recommendation.SETTLE_BUY:
                    g.setColor(graphColor);
                    g.fillOval(x - 5, yBuy - 5, 11, 11);
                    g.setColor(Color.BLACK);
                    g.drawOval(x - 5, yBuy - 5, 10, 10);

                    g.setColor(Color.WHITE);
                    poly = new BuyArrow();
                    poly.translate(x, yBuy);
                    g.fillPolygon(poly);
                    break;

                case Recommendation.BUY:
                    g.setColor(Color.WHITE);
                    g.fillOval(x - 5, yBuy - 5, 11, 11);
                    g.setColor(Color.BLACK);
                    g.drawOval(x - 5, yBuy - 5, 10, 10);

                    g.setColor(graphColor);
                    poly = new BuyArrow();
                    poly.translate(x, yBuy);
                    g.fillPolygon(poly);
                    break;
            }
        }

        /**
         *  Remove all listeners.
         */
        public void removeListeners() {
            graph.removeListener(this);
        }

    }


    /**
     *  ContinuousLineGraphPanelAbstract provides an abstract class for
     *  drawing continuous line graphs and supplying default methods for
     *  the GraphRedrawListener.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    protected abstract class ContinuousLineGraphPanelAbstract extends GraphPanelAbstract {

    /* *************************************** */
    /* *** GraphPanelAbstract METHODS      *** */
    /* *************************************** */
        /**
         *  Draw the a line graph.
         *
         *  @param  g           the graphics area to draw in
         *  @param  stats       the statistics to be drawn
         *  @param  calendar    the calendar to be used to process the graph starting on a monday
         *  @param  xAxisLeft   the position of the left side of the graph
         *  @param  xAxisRight  the position of the right side of the graph
         *  @param  yAxisTop    the position of the top side of the graph
         *  @param  yAxisBottom the position of the bottom side of the graph
         *  @param  minValue    the minimum value to be graphed
         *  @param  maxValue    the maximum value to be graphed
         *  @param  xScale      the horizontal scale the graph is to be drawn in
         *  @param  yScale      the vertical scale the graph is to be drawn in
         */
        public void drawGraph(Graphics      g,
                              StatsAbstract stats,
                              CommodityCalendar calendar,
                              int           xAxisLeft,
                              int           xAxisRight,
                              int           yAxisTop,
                              int           yAxisBottom,
                              double        minValue,
                              double        maxValue,
                              int           xScale,
                              double        yScale) {
            Color  graphColor = g.getColor();
            double oldValue = -1.0;
            double newValue = -1.0;
            int    xOld = xAxisRight;
            for (int x = xAxisRight; x > xAxisLeft; x -= xScale) {
                Double value = (Double)stats.getGraphData(calendar.getTime());
                if (value != null) {
                    newValue = value.doubleValue();
                    if (oldValue != -1.0) {
                        g.setColor(graphColor);
                        g.drawLine(xOld, (int)(yAxisBottom - (oldValue - minValue) / yScale), x, (int)(yAxisBottom - (newValue - minValue) / yScale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldValue - minValue) / yScale), x, (int)(yAxisBottom + 1 - (newValue - minValue) / yScale));
                    }

                    int recommend = makeBuySellRecommendation(calendar.getTime(), stats);
                    int yBuy = Math.min((int)(yAxisBottom - (newValue - minValue) / yScale + 30), yAxisBottom);
                    int ySell = Math.max((int)(yAxisBottom - (newValue - minValue) / yScale - 30), yAxisTop);

                    drawRecommendation(g, graphColor, recommend, x, yBuy, ySell);

                    oldValue = newValue;
                    xOld = x;
                }
                calendar.addWeekDays(-1);
            }
        }
    }

    /**
     *  TrendlineGraphPanelAbstract provides an abstract class for
     *  drawing trendline graphs and supplying default methods for
     *  the GraphRedrawListener.
     *
     *  @author     Jennifer Middleton
     *  @version    1.0
     *  @update     2004.11.20
     */
    protected abstract class TrendlineGraphPanelAbstract extends GraphPanelAbstract {

    /* *************************************** */
    /* *** GraphPanelAbstract METHODS      *** */
    /* *************************************** */
        /**
         *  Draw the a line graph.
         *
         *  @param  g           the graphics area to draw in
         *  @param  stats       the statistics to be drawn
         *  @param  calendar    the calendar to be used to process the graph starting on a monday
         *  @param  xAxisLeft   the position of the left side of the graph
         *  @param  xAxisRight  the position of the right side of the graph
         *  @param  yAxisTop    the position of the top side of the graph
         *  @param  yAxisBottom the position of the bottom side of the graph
         *  @param  minValue    the minimum value to be graphed
         *  @param  maxValue    the maximum value to be graphed
         *  @param  xScale      the horizontal scale the graph is to be drawn in
         *  @param  yScale      the vertical scale the graph is to be drawn in
         */
        public void drawGraph(Graphics      g,
                              StatsAbstract stats,
                              CommodityCalendar calendar,
                              int           xAxisLeft,
                              int           xAxisRight,
                              int           yAxisTop,
                              int           yAxisBottom,
                              double        minValue,
                              double        maxValue,
                              int           xScale,
                              double        yScale) {
            Color  graphColor = g.getColor();
            double oldValue = -1.0;
            double newValue = -1.0;
            int    xOld = xAxisRight;
            for (int x = xAxisRight; x > xAxisLeft; x -= xScale) {
                Double value = (Double)stats.getGraphData(calendar.getTime());
                if (value != null) {
                    newValue = value.doubleValue();
                    if (oldValue != -1.0) {
/*
                        //The slope of the line between the two known points
                        double slope = (newValue - oldValue)/(x - xOld);

                        //Calculate the start (x,y) value
                        double yStart = y - (slope * (xAxisLeft - x));
                        int xStart;
                        if (yStart < 0) {
                            xStart = x + (newValue / slope);
                            yStart = 0;
                        } else {
                            xStart = xAxisLeft;
                        }

                        //Calculate the end (x,y) value
                        double yEnd = y - (slope * (xAxisRight - xStart));
                        int xEnd;
                        if (yEnd > yAxisBottom) {
                            yEnd = yAxisBottom;
                            xEnd = xStart + ((yStart + yEnd) / slope);
                        } else {
                            xEnd = xAxisRight;
                        }

                        g.setColor(graphColor);
                        g.drawLine(xStart, yStart, xEnd, yEnd);
                        g.drawLine(xStart, yStart + 1, xEnd, yEnd + 1);
*/
                        g.setColor(graphColor);
                        g.drawLine(xOld, (int)(yAxisBottom - (oldValue - minValue) / yScale), x, (int)(yAxisBottom - (newValue - minValue) / yScale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldValue - minValue) / yScale), x, (int)(yAxisBottom + 1 - (newValue - minValue) / yScale));
                    }

                    int recommend = makeBuySellRecommendation(calendar.getTime(), stats);
                    int yBuy = Math.min((int)(yAxisBottom - (newValue - minValue) / yScale + 30), yAxisBottom);
                    int ySell = Math.max((int)(yAxisBottom - (newValue - minValue) / yScale - 30), yAxisTop);

                    drawRecommendation(g, graphColor, recommend, x, yBuy, ySell);

                    oldValue = newValue;
                    xOld = x;
                }
                calendar.addWeekDays(-1);
            }
        }
    }

    /**
     *  SegmentLineGraphPanelAbstract provides an abstract class for
     *  drawing line segment graphs and supplying default methods for
     *  the GraphRedrawListener.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.21
     */
    protected abstract class SegmentLineGraphPanelAbstract extends GraphPanelAbstract {

    /* *************************************** */
    /* *** GraphPanelAbstract METHODS      *** */
    /* *************************************** */
        /**
         *  Draw the a line graph.
         *
         *  @param  g           the graphics area to draw in
         *  @param  stats       the statistics to be drawn
         *  @param  calendar    the calendar to be used to process the graph starting on a monday
         *  @param  xAxisLeft   the position of the left side of the graph
         *  @param  xAxisRight  the position of the right side of the graph
         *  @param  yAxisTop    the position of the top side of the graph
         *  @param  yAxisBottom the position of the bottom side of the graph
         *  @param  minValue    the minimum value to be graphed
         *  @param  maxValue    the maximum value to be graphed
         *  @param  xScale      the horizontal scale the graph is to be drawn in
         *  @param  yScale      the vertical scale the graph is to be drawn in
         */
        public void drawGraph(Graphics      g,
                              StatsAbstract stats,
                              CommodityCalendar calendar,
                              int           xAxisLeft,
                              int           xAxisRight,
                              int           yAxisTop,
                              int           yAxisBottom,
                              double        minValue,
                              double        maxValue,
                              int           xScale,
                              double        yScale) {
           Color  graphColor = g.getColor();
            double oldValue = -1.0;
            double newValue = -1.0;
            int    xOld = xAxisRight;
            for (int x = xAxisRight; x > xAxisLeft; x -= xScale) {
                Double value = (Double)stats.getGraphData(calendar.getTime());
                if (value != null) {
                    newValue = value.doubleValue();
                    if (oldValue != -1.0) {
                        g.setColor(graphColor);
//Extra for validation
                        g.drawLine(xOld, (int)(yAxisBottom + 2 - (oldValue - minValue) / yScale), xOld, 10);

                        g.drawLine(xOld, (int)(yAxisBottom + 2 - (oldValue - minValue) / yScale), xOld, (int)(yAxisBottom - 1 - (oldValue - minValue) / yScale));
                        g.drawLine(xOld + 1, (int)(yAxisBottom + 2 - (oldValue - minValue) / yScale), xOld + 1, (int)(yAxisBottom - 1 - (oldValue - minValue) / yScale));

                        g.drawLine(xOld, (int)(yAxisBottom - (oldValue - minValue) / yScale), x, (int)(yAxisBottom - (newValue - minValue) / yScale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldValue - minValue) / yScale), x, (int)(yAxisBottom + 1 - (newValue - minValue) / yScale));

                        g.drawLine(x, (int)(yAxisBottom + 2 - (newValue - minValue) / yScale), x, (int)(yAxisBottom -1 - (newValue - minValue) / yScale));
                        g.drawLine(x - 1, (int)(yAxisBottom + 2 - (newValue - minValue) / yScale), x - 1, (int)(yAxisBottom -1 - (newValue - minValue) / yScale));
                    }

                    int recommend = makeBuySellRecommendation(calendar.getTime(), stats);
                    int yBuy = Math.min((int)(yAxisBottom - (newValue - minValue) / yScale + 30), yAxisBottom);
                    int ySell = Math.max((int)(yAxisBottom - (newValue - minValue) / yScale - 30), yAxisTop);

                    drawRecommendation(g, graphColor, recommend, x, yBuy, ySell);

                    oldValue = newValue;
                    xOld = x;
                }
                calendar.addWeekDays(-1);
            }
        }
    }

    /**
     *  BarGraphPanelAbstract provides an abstract class for
     *  drawing bar graphs and supplying default methods for
     *  the GraphRedrawListener.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */

    protected abstract class BarGraphPanelAbstract extends GraphPanelAbstract {
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
            return 0;
        }

        /**
         *  Gets the size of a tick for the commodity being drawn.
         *  This keeps the scale of the graph in multiples of this
         *  tick size.
         *
         *  @return         The data's tick size.
         */
        public double getTickSize() {
            return 1;
        }

        /**
         *  Draw a bar graph.
         *
         *  @param  g           the graphics area to draw in
         *  @param  stats       the statistics to be drawn
         *  @param  calendar    the calendar to be used to process the graph starting on a monday
         *  @param  xAxisLeft   the position of the left side of the graph
         *  @param  xAxisRight  the position of the right side of the graph
         *  @param  yAxisTop    the position of the top side of the graph
         *  @param  yAxisBottom the position of the bottom side of the graph
         *  @param  minValue    the minimum value to be graphed
         *  @param  maxValue    the maximum value to be graphed
         *  @param  xScale      the horizontal scale the graph is to be drawn in
         *  @param  yScale      the vertical scale the graph is to be drawn in
         */
        public void drawGraph(Graphics      g,
                              StatsAbstract stats,
                              CommodityCalendar calendar,
                              int           xAxisLeft,
                              int           xAxisRight,
                              int           yAxisTop,
                              int           yAxisBottom,
                              double        minValue,
                              double        maxValue,
                              int           xScale,
                              double        yScale) {
            for (int x = xAxisRight; x > xAxisLeft; x -= xScale) {
                Long value = (Long)stats.getGraphData(new java.util.Date(calendar.getTimeInMillis()));
                if (value != null) {
                    g.drawLine(x, (int)yAxisBottom, x, (int)(yAxisBottom - (value.longValue() - minValue) / yScale));
                }
                calendar.addWeekDays(-1);
            }
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Retrieves the parameter data for a test
     */
    protected abstract void loadParameters();

    /**
     *  Deletes all of the parameter data for a test
     *  for this contract.
     */
    protected void clearParameters() {
        DBConnectionPool dbcp = null;

        try {
            dbcp = DBConnectionPool.instance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }

        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("delete from " + sqlTable + " " +
                      " where Commodity_Id = '" + contract.getSymbol() + "' " +
                      "   and Contract_Month = '" + contract.getMonth() + "' ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            stmt.execute(sqlTxt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Saves the parameter data
     *
     *  @param  colums          comma separated list of parameter columns for the table
     *  @param  columnValues    comma separated list of values for the table in the same order as the columns
     *  @param  stats           the statistical data to be stored in every parameter table
     */
    protected synchronized void saveParameters(String columns,
                                               String columnValues,
                                               StatsAbstract stats) {
        try {
            dataManager.saveTestParameters(sqlTable, columns, columnValues, contract, stats, this.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/* *************************************** */
/* *** TechnicalTestInterface METHODS  *** */
/* *************************************** */
    /**
     *  Retrieve the name of the test.  This name will be the english style name
     *  that will appear in the menu and as a header for the graph.
     *
     *  @return     the name of the test
     */
    public String getName() {
        return testName;
    }

    /**
     *  This method will depict the results of the test.  Although this will
     *  typically be in the form of a line graph, the graph can appear in
     *  any appropriate format.
     */
    public abstract void graphResults();

    /**
     *  This method will turn off the display of the graph.
     */
    public abstract void stopGraphResults();

    /**
     *  This method loads the data used for each test that was
     *  determined to be an optimal test for a contract.
     */
    public abstract void loadData();

    /**
     *  Perform an optimization of the test to determine the most profitable
     *  combination(s) of parameters to use with the test.
     */
    public abstract void optimizeTest();

    /**
     *  Remove extra statistics that are too close to another that is more optimial.
     *  This prevents multiple statistics that are clumped together from all being
     *  reported.
     *
     *  @param  tableByPercent  table of success percentages
     *  @param  statsArray      array of success statistics
     */
    protected void removeExtraOptimizedStats(TreeMap tableByPercent,
                                             StatsAbstract statsArray[]) {
        Iterator it = tableByPercent.keySet().iterator();
        while (it.hasNext()) {
            Double percent = (Double)it.next();
            int term = ((Integer)tableByPercent.get(percent)).intValue();
            int validTestSpan = 0;
            if (term <= SHORT_TERM) {
                validTestSpan = 3;
            } else if (term <= MEDIUM_TERM) {
                validTestSpan = 4;
            } else {            // LONG_TERM
                validTestSpan = 6;
            }
            for (int i = term - validTestSpan; i <= (term + validTestSpan); i++) {
                if ((i > 0) && (i != term) && (i < statsArray.length) && (statsArray[i] != null)) {
                    statsArray[term] = null;
                    break;
                }
            }
        }
    }

    /**
     *  Save the optimization tests.
     *
     *  @param  statsArray      array of successful stats
     */
    public abstract void saveOptimizedStats(StatsAbstract statsArray[]);

    /**
     *  Resets the test from the saved parameters.  This should be performed after
     *  optimizing the test.
     */
    public void resetTest() {
        statCollection = new TreeSet();
        recommendCollection = new TreeSet();
        tableByTestId = new TreeMap();
        loadParameters();
        loadData();
    }

    /**
     *  Retrieve the recommendation resulting from the test for the
     *  specified date.
     *
     *  @param  testId  the id of the test in the test table
     *  @param  date    the date to get the recommendation for
     *  @return         the recommendation
     */
    public abstract Recommendation getRecommendation(int testId,
                                                     java.util.Date date);

    /**
     *  Make a recommendation based on the stats data.
     *
     *  @param  date    the date the recommendation is for
     *  @param  stats   the statistics
     *  @return         a recommendation based on this test
     */
    public abstract Recommendation makeRecommendation(java.util.Date date,
                                             StatsAbstract statsObject);
}
