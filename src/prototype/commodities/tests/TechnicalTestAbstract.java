/*  x Properly Documented
 */
package commodities.tests;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import commodities.*; // Debug only
import commodities.util.*;
import commodities.dataaccess.*;

import java.io.*;
import java.sql.*;
import commodities.dataaccess.database.*;

import commodities.graph.*;

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
 */
public abstract class TechnicalTestAbstract implements TechnicalTestInterface {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Data manager for db2 calls */
    protected static DataManagerInterface dataManager = DataManagerFactory.getInstance();

    /** A counter of how many GraphDailyPrices are present. */
    protected static int          graphCount = 0;
    /** The colors that each graph should use based on the graph's number. */
    protected static Color        colors[][] = {{Color.BLACK, Color.RED, Color.MAGENTA, Color.PINK},
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
Debug.println(DEBUG, this, "GraphPanel.getYBuffer() only");
            return 5;
        }

        /**
         *  Gets the maximum value represented in the graph.
         *
         *  @return         The data's maximum value.
         */
        public double getMaxValue() {
Debug.println(DEBUG, this, "GraphPanel.getMaxValue() only");
            return statDataMax;
        }

        /**
         *  Gets the minimum value represented in the graph.
         *
         *  @return         The data's minimum value.
         */
        public double getMinValue() {
Debug.println(DEBUG, this, "GraphPanel.getMinValue() only");
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
Debug.println(DEBUG, this, "GraphPanel.getTickSize() only");
            return Commodity.bySymbol(contract.getSymbol()).getTickSize();
        }

        /**
         *  Draws the title of the graph at the top of the graph offset by
         *  the other graph titles that are alread there.
         *
         *  @return         always returns true
         */
        public boolean redrawTitle(Graphics g, int yOffset) {
Debug.println(DEBUG, this, "GraphPanel.redrawTitle() start");
            Rectangle graphRect = graph.getGraphRect();
            int xAxisLeft       = (int)graphRect.getX();

            boolean firstTest = true;
            int graphSeq = 0;

            g.setColor(Color.BLACK);
            String testNameString = testName + ": ";
            g.drawString(testNameString, xAxisLeft, yOffset+g.getFontMetrics().getAscent());
            int testNameOffset = g.getFontMetrics().stringWidth(testNameString);

//            Iterator it = statCollection.iterator();
            Iterator it = recommendCollection.iterator();
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
Debug.println(DEBUG, this, "GraphPanel.redrawTitle() end");
            return true;
        }

        /**
         *  Draws the graph.
         *
         *  @param  e   the event containing the graph to draw into
         */
        public void redrawComponent(GraphRedrawEvent e) {
Debug.println(DEBUG, this, "GraphPanel.redrawComponent() start");
            Graphics g = e.getGraphics();

            Rectangle graphRect    = graph.getGraphRect();
            int xAxisLeft   = (int)graphRect.getX();
            int xAxisRight  = (int)graphRect.getX() + (int)graphRect.getWidth();
            int yAxisBottom = (int)graphRect.getY() + (int)graphRect.getHeight();

            double minValue = e.getMinValue();
            double scale    = e.getYScale() * e.getMinTickSize();

            int graphSeq = 0;
            // Draw the price ranges
//            Iterator it = statCollection.iterator();
            Iterator it = recommendCollection.iterator();
            while (it.hasNext()) {
                StatsAbstract stats = (StatsAbstract)it.next();
                g.setColor(colors[graphNbr][graphSeq]);

                redrawGraph(g, stats, xAxisLeft, xAxisRight, yAxisBottom, minValue, scale);

                graphSeq++;
                if (graphSeq >= colors[graphNbr].length) {
                    graphSeq--;
                }
            }
Debug.println(DEBUG, this, "GraphPanel.redrawComponent() end");
        }

        /**
         *  Draw the graph itself.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public void redrawGraph(Graphics      g,
                                StatsAbstract stats,
                                int           xAxisLeft,
                                int           xAxisRight,
                                int           yAxisBottom,
                                double        minValue,
                                double        scale) {

            // Determine the date of the right side of the graph.
            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            calendar.clearTime();
            calendar.setNextDayOfWeek(CommodityCalendar.MONDAY);
            drawGraph(g, stats, calendar, xAxisLeft, xAxisRight, yAxisBottom, minValue, scale);
        }

        /**
         *  Draw the graph itself.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public abstract void drawGraph(Graphics      g,
                                       StatsAbstract stats,
                                       CommodityCalendar calendar,
                                       int           xAxisLeft,
                                       int           xAxisRight,
                                       int           yAxisBottom,
                                       double        minValue,
                                       double        scale);

        /**
         *  Remove all listeners.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public void removeListeners() {
Debug.println(DEBUG, this, "GraphPanel.removeListeners() start");
            graph.removeListener(this);
Debug.println(DEBUG, this, "GraphPanel.removeListeners() end");
        }
    }




    /**
     *  LineGraphPanelAbstract provides an abstract class for
     *  drawing line graphs and supplying default methods for
     *  the GraphRedrawListener.
     *
     *  @author J.R. Titko
     */
    protected abstract class LineGraphPanelAbstract extends GraphPanelAbstract {

    /* *************************************** */
    /* *** GraphPanelAbstract METHODS      *** */
    /* *************************************** */
        /**
         *  Draw the graph itself.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public void drawGraph(Graphics      g,
                              StatsAbstract stats,
                              CommodityCalendar calendar,
                              int           xAxisLeft,
                              int           xAxisRight,
                              int           yAxisBottom,
                              double        minValue,
                              double        scale) {
            Color  graphColor = g.getColor();
            double oldValue = -1.0;
            double newValue = -1.0;
            int    xOld = xAxisRight;
            for (int x = xAxisRight; x > xAxisLeft; x -= Graph.PIXEL_SCALE) {
//                Double value = (Double)stats.getData(new java.util.Date(calendar.getTimeInMillis()));
                Double value = (Double)stats.getData(calendar.getTime());
                if (value != null) {
                    newValue = value.doubleValue();
                    if (oldValue!= -1.0) {
                        g.setColor(graphColor);
                        g.drawLine(xOld, (int)(yAxisBottom - (oldValue - minValue) / scale), x, (int)(yAxisBottom - (newValue - minValue) / scale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldValue - minValue) / scale), x, (int)(yAxisBottom + 1 - (newValue - minValue) / scale));

                        int recommend = makeRecommendation(calendar.getTime(), stats);
                        if (recommend > 0) {
                            g.setColor(Color.RED);
                            Polygon poly = new SellArrow();
                            poly.translate(x, (int)(yAxisBottom - (newValue - minValue) / scale - 30));
                            g.fillPolygon(poly);
                        } else if (recommend < 0) {
                            g.setColor(Color.BLUE);
                            Polygon poly = new BuyArrow();
                            poly.translate(x, (int)(yAxisBottom - (newValue - minValue) / scale + 30));
                            g.fillPolygon(poly);
                        }
                    }
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
Debug.println(DEBUG, this, "GraphPanel.getYBuffer() only");
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
Debug.println(DEBUG, this, "GraphPanel.getTickSize() only");
            return 1;
        }

        /**
         *  Draw the graph itself.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public void drawGraph(Graphics      g,
                              StatsAbstract stats,
                              CommodityCalendar calendar,
                              int           xAxisLeft,
                              int           xAxisRight,
                              int           yAxisBottom,
                              double        minValue,
                              double        scale) {
            for (int x = xAxisRight; x > xAxisLeft; x -= Graph.PIXEL_SCALE) {
                Long value = (Long)stats.getData(new java.util.Date(calendar.getTimeInMillis()));
                if (value != null) {
                    g.drawLine(x, (int)yAxisBottom, x, (int)(yAxisBottom - (value.longValue() - minValue) / scale));
                }
                calendar.addWeekDays(-1);
            }
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  This method loads the data used for each moving average test that was
     *  determined to be an optimal moving average test for a contract.
     */
    protected abstract void loadData();

    /**
     *  Retrieves the parameter data for moving averages
     */
    protected abstract void loadParameters();

    /**
     *  Deletes all of the parameter data for moving averages
     *  for this contract.
     */
    protected void clearParameters() {
Debug.println(DEBUG, this, "clearParameters(sqlTxt) start");
        DBConnectionPool dbcp = null;

        try {
            dbcp = DBConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }

        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("delete from " + sqlTable + " " +
                      " where Commodity_Id = '" + contract.getSymbol() + "' " +
                      "   and Contract_Month = '" + contract.getMonthAsKey() + "' ");
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
Debug.println(DEBUG, this, "clearParameters(sqlTxt) end");
    }

    /**
     *  Saves the parameter data
     *
     *  @param  paramDays   the number of days for the moving average
     */
    protected synchronized void saveParameters(String columns, String columnValues, StatsAbstract stats) {
Debug.println(DEBUG, this, "saveParameters(sqlTxt) start");
/*
        if (commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
            System.out.println("Parameters: contract=" + contract.getName() + "  test=" + testName + "  parms=" + columns + "  values=" + columnValues);
        }
*/
        DBConnectionPool dbcp = null;
        int testId = 0;

        try {
            dbcp = DBConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }

        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();
            sb.append("select max(id) " +
                      "from " + sqlTable + " ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            if (rs.next()) {
                testId = rs.getInt(1);
                testId++;
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }

        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();
            sb.append("insert into " + sqlTable + " " +
                      "       (id, commodity_id, contract_month, " +
                      "       " + columns + ", " +
                      "        success_ratio, profit_loss, profit_count, profit_total, loss_count, loss_total, largest_profit, largest_loss) " +
                      "values (" + testId + ", " +
                      "        '" + contract.getSymbol() + "', " +
                      "        '" + contract.getMonthAsKey() + "', " +
                      "        " + columnValues + ", " +
                      "        " + stats.getCountRatio() + ", " +
                      "        " + stats.getProfit() + ", " +
                      "        " + stats.getProfitCount() + ", " +
                      "        " + stats.getProfitTotal() + ", " +
                      "        " + stats.getLossCount() + ", " +
                      "        " + stats.getLossTotal() + ", " +
                      "        " + stats.getLargestProfit() + ", " +
                      "        " + stats.getLargestLoss() +
                      "       )");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            stmt.execute(sqlTxt);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }

        try {
            dataManager.insertTestXref(contract, stats, this.getClass().getName(), testId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

Debug.println(DEBUG, this, "saveParameters(sqlTxt) end");
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
    protected void removeExtraOptimizedStats(TreeMap tableByPercent, StatsAbstract statsArray[]) {
        Iterator it = tableByPercent.keySet().iterator();
        while (it.hasNext()) {
            int term = ((Integer)tableByPercent.get(it.next())).intValue();
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
     *  Retrieve the recommendation resulting from the test for the
     *  specified date.
     *
     *  @param  testId  the id of the test in the test table
     *  @param  date    the date to get the recommendation for
     *  @return         the recommendation
     */
    public abstract Recommendation getRecommendation(int testId, java.util.Date date);

    /**
     *  Make a recommendation based on the stats data.
     *
     *  @param  date    the date the recommendation is for
     *  @param  stats   the statistics
     *  @return         a recommendation based on this test
     */
    public abstract int makeRecommendation(java.util.Date date, StatsAbstract statsObject);

}