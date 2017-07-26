/*  x Properly Documented
 */
package commodities.tests.technical;

import java.io.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import commodities.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.dataaccess.database.*;
import commodities.event.*;
import commodities.graph.*;
import commodities.price.*;
import commodities.simulator.*;
import commodities.tests.*;
import commodities.util.*;
import commodities.window.*;

/**
 *  The StochasticTest performs the stochastic oscillator test where
 *  the oscillator is compared to crossing a percentage level on both
 *  the high end (eg 80%) and the low end (eg 20%).  Both ends must be
 *  equal distance from 100% and 0% respectively.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.16
 */
public class StochasticTest extends TechnicalTestAbstract {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The name of the test. */
    private static final String TEST_NAME = "Stochastic Oscillator";
    /** The name of the sql table associated with this test. */
    private static final String SQL_TABLE = "test_stochastic_oscillator";

    /** The maximum number of slowing periods for a stochastic test. */
    protected static final int MAX_SLOWING_PERIODS = 3;
    /** The minimum percentage for a stochastic test. */
    protected static final int MIN_PERCENTAGE = 10;
    /** The maximum percentage for a stochastic test. */
    protected static final int MAX_PERCENTAGE = 35;
    /** The percentage interval for a stochastic test optimization. */
    protected static final int PERCENTAGE_INTERVAL = 5;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /**
     *  A list of GraphPanels for the graph.
     *  Each set of parms gets its own graph.
     */
    protected LinkedList graphPanelList = null;

    /** The statistics graph manager to put this graph in */
    private TestGraphManager testGraphManager = null;


/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  Stats holds the individual tests parameters and the related
     *  statistical data.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.16
     */
    protected class Stats extends StatsAbstract {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The number of time periods for %K. */
        protected int kPeriods;
        /** The slowing period for %K. */
        protected int kSlowing;
        /** The percent line for %K to cross for an indicator. */
        protected int percent;

        /** The window of data being analyzed for the %K periods. */
        protected LinkedList window = new LinkedList();

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        public Stats(int kPeriods, int kSlowing, int percent) {
            this.kPeriods = kPeriods;
            this.kSlowing = kSlowing;
            this.percent = percent;
        }

    /* *************************************** */
    /* *** GET AND SET METHODS             *** */
    /* *************************************** */
        /**
         *  Get the number of time periods for %K.
         *  @return     the %K time periods
         */
        public int getKPeriods() { return kPeriods; }

        /**
         *  Get the slowing period for %K.
         *  @return     the %K slowing period
         */
        public int getKSlowing() { return kSlowing; }

        /**
         *  Get the percent line.
         *  @return     the percent line
         */
        public int getPercent() { return percent; }

    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Add to the statistical data.
         *  @param  key     the date the prices are for
         *  @param  prices  the contract prices for the date
         */
        public void addStatData(java.util.Date date, Prices prices) {
            dataList = null;
            keyList = null;

            window.addLast(prices);

            if (window.size() > kPeriods) {
                dataList = null;
                keyList = null;
                window.removeFirst();

                double high = 0.0;
                double low = 9999.0;
                Iterator it = window.iterator();
                while (it.hasNext()) {
                    Prices p = (Prices)it.next();
                    if (p.getHigh() > high) {
                        high = p.getHigh();
                    }
                    if (p.getLow() < low) {
                        low = p.getLow();
                    }
                }

                if (high != low) {
                    double k = ((prices.getClose() - low) / (high - low)) * 100;
                    dataMap.put(date, new Double(k));
                }
            }
        }

    /* *************************************** */
    /* *** StatsAbstract METHODS           *** */
    /* *************************************** */
        /**
         *  Get the statistics map
         *  @return     the statistics data map
         */
        protected String getName() {
            String speed = "N";
            switch (kSlowing) {
                case 1: speed = "F";
                        break;
                case 2: speed = "N";
                        break;
                case 3: speed = "S";
                        break;
            }

            return "" + kPeriods + "(" + speed + "w/" + percent + "%)";
        }

        /**
         *  Get the interval used to determine short/medium/long term.
         *  @return     the interval time
         */
        public int getInterval() { return kPeriods; }

    /* *************************************** */
    /* *** Comparable METHODS              *** */
    /* *************************************** */
        /**
         *  Compare the objects for sorting.
         *  @param  o2      the object to compare to
         *  @return         negative, zero, possitive number depending on
         *                  this object to the passed object
         */
        public int compareTo(Object o2) {
            int c = kPeriods - ((Stats)o2).getKPeriods();
            if (c == 0) {
                c = kSlowing - ((Stats)o2).getKSlowing();
            }
            if (c == 0) {
                c = percent - ((Stats)o2).getPercent();
            }

            return c;
        }
    }

    /**
     *  GraphPanel controls the graphing of the statistics for this test.
     *
     *  @author J.R. Titko
     */
    protected class GraphPanel extends TechnicalTestAbstract.ContinuousLineGraphPanelAbstract {

    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The stats for this graph. */
        private Stats  stats;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  The graph of this test's statistics.
         *  By default, the graph number is reset.
         */
        public GraphPanel() {
            this(true);
        }

        /**
         *  The graph for a specific set of statistics.
         *  By default, the graph number is reset.
         */
        public GraphPanel(Stats stats) {
            this(true);
            this.stats = stats;
        }

        /**
         *  The graph of this test's statistics.
         *  If the resetCount parameter is set to false, then this graph is
         *  a subsequent graph and is overlaid on the prior graphs.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public GraphPanel(boolean resetCount) {
            graph = new TestGraphPanel();

            if (resetCount) {
                graphCount = 0;
            }
            graphNbr = graphCount++;
            graph.addListener(this);
        }

        /**
         *  Get the graph panel to draw the graph into.
         */
        public JPanel getGraphPanel() { return graph; }

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
         *  Gets the maximum value represented in the graph.
         *
         *  @return         The data's maximum value.
         */
        public double getMaxValue() {
            return 100.0;
        }

        /**
         *  Gets the minimum value represented in the graph.
         *
         *  @return         The data's minimum value.
         */
        public double getMinValue() {
            return 0.0;
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
         *  Draws the title of the graph at the top of the graph offset by
         *  the other graph titles that are alread there.
         *
         *  @return         always returns true
         */
        public boolean redrawTitle(Graphics g, int yOffset) {
            Rectangle graphRect = graph.getGraphRect();
            int xAxisLeft       = (int)graphRect.getX();

            g.setColor(Color.BLACK);
            String testNameString = testName + ": ";
            g.drawString(testNameString, xAxisLeft, yOffset+g.getFontMetrics().getAscent());
            int testNameOffset = g.getFontMetrics().stringWidth(testNameString);

            g.setColor(colors[graphNbr][0]);
            String title = stats.getName();
            g.drawString(title, xAxisLeft + testNameOffset, yOffset+g.getFontMetrics().getAscent());

            return true;
        }

        /**
         *  Draws the graph.
         *
         *  @param  e   the event containing the graph to draw into
         */
        public void redrawComponent(GraphRedrawEvent e) {
            Graphics g = e.getGraphics();

            Rectangle graphRect = graph.getGraphRect();
            int xAxisLeft       = (int)graphRect.getX();
            int xAxisRight      = (int)graphRect.getX() + (int)graphRect.getWidth();
            int yAxisTop        = (int)graphRect.getY();
            int yAxisBottom     = (int)graphRect.getY() + (int)graphRect.getHeight();

            int xScale = e.getXScale();
            double yScale = e.getYScale() * e.getMinTickSize();
            double minValue = e.getMinValue();
            double maxValue = e.getMaxValue();

            g.setColor(colors[graphNbr][0]);
            redrawGraph(g, stats, xAxisLeft, xAxisRight, yAxisTop, yAxisBottom, minValue, maxValue, xScale, yScale);

            int percent = (int)(stats.getPercent() / yScale);
            g.drawLine(xAxisLeft, yAxisBottom - percent, xAxisRight, yAxisBottom - percent);
            g.drawLine(xAxisLeft, yAxisTop + percent, xAxisRight, yAxisTop + percent);
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Constructor only used to retrieve the name of the test (getName()).
     *  This constructor cannot be used to successfully access the remaining
     *  methods.
     */
    public StochasticTest() {
        testName = TEST_NAME;
        sqlTable = SQL_TABLE;
    }

    /**
     *  Constructor used to instantiate a test for the contract.
     *
     *  @param  contract    the contract to perform the test on
     */
    public StochasticTest(Contract contract) {
        this();
        this.contract = contract;
        this.testGraphManager = CommodityAnalyzerJFrame.instance().getTestGraphPanel();
        loadParameters();
        loadData();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

/* *************************************** */
/* *** TestnicalTestAbstract METHODS   *** */
/* *************************************** */
    /**
     *  This method loads the data used for each stochastic test that was
     *  determined to be an optimal stochastic test for a contract.
     */
    protected void loadData() {
        Map     priceList = contract.getPriceList();
        Prices  prices = null;
        java.util.Date  statsDate = null;

// For each day's prices...
        Iterator it = priceList.keySet().iterator();
        while (it.hasNext()) {
            statsDate = (java.util.Date)it.next();
            prices = (Prices)priceList.get(statsDate);

// recalculate the statististics data by adding it to the queue for that moving average.
            Iterator it2 = statCollection.iterator();
            while (it2.hasNext()) {
                ((Stats)it2.next()).addStatData(statsDate, prices);
            }
        }

/* always 0-100%
        it = statCollection.iterator();
        while (it.hasNext()) {
            Stats s = (Stats)it.next();
            Iterator it2 = s.getData().iterator();
            while (it2.hasNext()) {
                double value = ((Double)it2.next()).doubleValue();
                if (value < statDataMin) {
                    statDataMin = value;
                }
                if (value > statDataMax) {
                    statDataMax = value;
                }
            }
        }
*/
    }

    /**
     *  Retrieves the parameter data for moving averages
     */
    protected void loadParameters() {
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
            sb.append("select id, k_periods, k_slowing_periods, percent_line " +
                      "from " + sqlTable + " " +
                      "where commodity_id = '" + contract.getSymbol() + "' " +
                      "  and contract_month = '" + contract.getMonth() + "' ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            while (rs.next()) {
                int id = rs.getInt(1);
                Stats stats = new Stats(rs.getInt(2), rs.getInt(3), rs.getInt(4));
                statCollection.add(stats);
                tableByTestId.put(new Integer(id), stats);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Save the optimization tests.
     *
     *  @param  statsArray      array of successful stats
     */
    public void saveOptimizedStats(StatsAbstract statsArray[]) {
        clearParameters();
        for (int i = 1; i < statsArray.length; i++) {
            if (statsArray[i] != null) {
                saveParameters(statsArray[i]);
            }
        }
    }

    /**
    *  Saves the parameter data for a stochastic test
     *
     *  @param  statsAbs    the statistics to save parameters from
     */
    protected void saveParameters(StatsAbstract statsAbs) {
        Stats stats = (Stats)statsAbs;
        String columns = "k_periods, k_slowing_periods, percent_line";
        String columnValues = "" + stats.getKPeriods() + ", " +
                                 + stats.getKSlowing() + ", " +
                                 + stats.getPercent();
        saveParameters(columns, columnValues, stats);
    }

    /**
     *  This method will depict the results of the test.  This test is
     *  displayed in the test graph panel.
     */
    public void graphResults() {
        GraphPanel graphPanel = null;
        graphPanelList = new LinkedList();
        Iterator it = statCollection.iterator();
//        Iterator it = recommendCollection.iterator();
        while (it.hasNext()) {
            Stats stats = (Stats)it.next();
            int term = stats.getInterval();
            if (((term <= SHORT_TERM) && testManager.doGraphShortTermTests()) ||
                ((term > SHORT_TERM)  && (term <= MEDIUM_TERM) && testManager.doGraphMediumTermTests()) ||
                ((term > MEDIUM_TERM) && (term <= LONG_TERM)   && testManager.doGraphLongTermTests())) {

                graphPanel = new GraphPanel(stats);
                testGraphManager.addTestGraph((TestGraphPanel)graphPanel.getGraphPanel());
                graphPanelList.add(graphPanel);
            }
        }
    }

    /**
     *  This method will turn off the display of the graph.
     */
    public void stopGraphResults() {
        GraphPanel graphPanel = null;
        Iterator it = graphPanelList.iterator();
        while (it.hasNext()) {
            graphPanel = (GraphPanel)it.next();
            testGraphManager.removeTestGraph((TestGraphPanel)graphPanel.getGraphPanel());
        }
        graphPanelList = null;
    }

    /**
     *  Optimize this test to determine the most profitable combination
     *  of parameters that should be used.  This does not mean the test
     *  will produce a positive profitability, but simply the most
     *  profitable (least loss).
     */
    public void optimizeTest() {
        System.out.print(".");
        int startRange = 4;
        int endRange = Math.min(LONG_TERM + 1, contract.getPriceList().size() / 3);
        optimizeTest(startRange, endRange);
    }

    /**
     *  Optimize this test to determine the most profitable combination
     *  of parameters that should be used.  This does not mean the test
     *  will produce a positive profitability, but simply the most
     *  profitable (least loss).
     *
     *  @param  startRange  starting range of test optimization
     *  @param  endRange    ending range of test optimization
     */
    public void optimizeTest(int startRange, int endRange) {
        TreeMap tableByPercent  = new TreeMap();
        StatsAbstract statsArray[] = new StatsAbstract[endRange + 1];

        for (int i = startRange; i < endRange + 1; i++) {
            Stats  bestStats = null;
            for (int j = 1; j <= MAX_SLOWING_PERIODS; j++) {
                for (int k = MIN_PERCENTAGE; k <= MAX_PERCENTAGE; k += PERCENTAGE_INTERVAL) {
                    Stats stats = new Stats(i, j, k);
                    ListIterator lit = contract.getPriceDates(Contract.ASCENDING);
                    while (lit.hasNext()) {
                        java.util.Date date = (java.util.Date)lit.next();
                        stats.addStatData(date, contract.getPrices(date));
                    }

                    new TestSimulator(contract, this, stats);

                    if ((stats.getProfitCount() + stats.getLossCount()) >= MIN_NUMBER_OF_TRADES) {
                        if ((stats.getCountRatio() > MIN_SUCCESS_RATIO) && ((bestStats == null) || (bestStats.getCountRatio() < stats.getCountRatio()))) {
                            bestStats = stats;
                        }
                    }
                }
            }
            if (bestStats != null) {
//System.out.println("Stochastic found");
                int interval = bestStats.getInterval();
                Double ratioDouble = new Double(bestStats.getCountRatio() - (interval / 100000000.0)); // insures no duplicates
                tableByPercent.put(ratioDouble, new Integer(interval)); // insures no duplicates
                statsArray[interval] = bestStats;
            }
        }

        removeExtraOptimizedStats(tableByPercent, statsArray);
        saveOptimizedStats(statsArray);

        resetTest();
    }

    /**
     *  Retrieve the recommendation resulting from the test for the
     *  specified date.
     *
     *  @param  testId  the id of the test in the test table
     *  @param  date    the date to get the recommendation for
     *  @return         the recommendation
     */
    public Recommendation getRecommendation(int testId, java.util.Date date) {
        Stats stats = (Stats)tableByTestId.get(new Integer(testId));

        // The following lines insures that any test that was requested to make a recommendation
        // appears on the graphs.
        if (!recommendCollection.contains(stats)) {
            recommendCollection.add(stats);
        }
        return makeRecommendation(date, stats);
    }

    /**
     *  Make a recommendation based on the stats data.
     *
     *  @param  date    the date the recommendation is for
     *  @param  stats   the statistics
     *  @return         a recommendation based on this test
     */
    public Recommendation makeRecommendation(java.util.Date date, StatsAbstract statsAbs) {
        int recommendation = Recommendation.NO_ACTION;
        Stats stats = (Stats)statsAbs;

        LinkedList statsKeys = stats.getKeys();
        if (statsKeys.size() < 2) {
//System.out.println("Stochastic recommend NO ACTION (keys)");
            return new Recommendation(date, testName, Recommendation.NO_ACTION);
        }

        int keyIndex = statsKeys.indexOf(date);
        if (keyIndex < 2) {
//System.out.println("Stochastic recommend NO ACTION (index)");
            return new Recommendation(date, testName, Recommendation.NO_ACTION);
        }

        java.util.Date yesterDate = (java.util.Date)statsKeys.get(keyIndex - 1);

        double k = ((Double)stats.getData(date)).doubleValue();
        double kYesterday = ((Double)stats.getData(yesterDate)).doubleValue();
        int percent = stats.getPercent();

        if ((kYesterday <= percent) && (k > percent)) {
//System.out.println("Stochastic recommend BUY - " + date);
            recommendation = Recommendation.BUY;
        } else if ((kYesterday >= (100 - percent)) && (k < (100 - percent))) {
//System.out.println("Stochastic recommend SELL - " + date);
            recommendation = Recommendation.SELL;
        }
//else {
//System.out.println("Stochastic recommend NO ACTION - " + date);
//}
        return new Recommendation(date, testName, recommendation);
    }

    /**
     *  Make a buy/sell recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a buy/sell recommendation based on this test
     */
    public int makeBuySellRecommendation(java.util.Date date, StatsAbstract stats) {
        return makeRecommendation(date, stats).getBuySellRecommendation();
    }

    /**
     *  Make a stop loss recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a stop loss recommendation based on this test
     *  @ToDo               add Stop-Loss Information
     */
    public double makeStopLossRecommendation(java.util.Date date, StatsAbstract stats) {
        //return ((Double)((Stats)stats).getData(date)).doubleValue();
        return 0;
    }
}
