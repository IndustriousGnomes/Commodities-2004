/*  x Properly Documented
 */
package prototype.commodities.tests;

import java.io.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.graph.*;
import prototype.commodities.simulator.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.dataaccess.database.*;

/**
 *  The StochasticKDTest performs the stochastic oscillator test where
 *  the oscillator (%K) is compared to a moving average (%D) of the
 *  oscillator.  When %K crosses %D, a buy/sell signal is produced.
 *
 *  @author J.R. Titko
 */
public class StochasticKDTest extends TechnicalTestAbstract {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The name of the test. */
    private static final String TEST_NAME = "Stochastic %K/%D";
    /** The name of the sql table associated with this test. */
    private static final String SQL_TABLE = "test_stochastic_kd";

    /** The maximum number of slowing periods for a stochastic test. */
    protected static final int MAX_SLOWING_PERIODS = 3;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The GraphPanel for the graph */
    protected GraphPanel graphPanel = null;
    /**
     *  A list of GraphPanels for the graph.
     *  Each set of parms gets its own graph.
     */
    protected LinkedList graphPanelList = null;

    /** The statistics graph manager to put this graph in */
    private StatsGraphManager statsGraph = null;


/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  Stats holds one day's worth of data.  This is used because there
     *  is more than one value to return for a given date.
     *
     *  @author J.R. Titko
     */
    protected class StatData {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The value of %K. */
        protected double k;
        /** The value of %D. */
        protected double d;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        public StatData(double k, double d) {
            this.k = k;
            this.d = d;
        }
    /* *************************************** */
    /* *** GET AND SET METHODS             *** */
    /* *************************************** */
        public double getK() { return k; }
        public double getD() { return d; }
    }

    /**
     *  Stats holds the individual tests parameters and the related
     *  statistical data.
     *
     *  @author J.R. Titko
     */
    protected class Stats extends StatsAbstract {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The number of time periods for %K. */
        protected int kPeriods;
        /** The slowing period for %K. */
        protected int kSlowing;
        /** The number of time periods for %D moving average. */
        protected int dPeriods;

        /** Source data for determining the moving average. */
        private TreeMap sourceMap = new TreeMap();
        /** Instance of MovingAverage to use. */
        private MovingAverage dMovingAverage;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        public Stats(int kPeriods, int kSlowing, int dPeriods) {
            this.kPeriods = kPeriods;
            this.kSlowing = kSlowing;
            this.dPeriods = dPeriods;
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
         *  Get the number of time periods for %D.
         *  @return     the %D time periods
         */
        public int getDPeriods() { return dPeriods; }

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

            sourceMap.put(date, prices);
            dataMap = new TreeMap();
        }

        /**
         *  Calculate the statistical data.
         */
        public void calculateStatData() {
            TreeMap kMap = new TreeMap();
            LinkedList window = new LinkedList();
            double windowHigh = 0.0;
            double windowLow = 9999.0;

            dataList = null;
            keyList = null;

            Iterator it = sourceMap.keySet().iterator();
            while (it.hasNext()) {
                java.util.Date date = (java.util.Date)it.next();
                Prices prices = (Prices)sourceMap.get(date);

                window.addLast(prices);

                if (window.size() > kPeriods) {
                    Prices oldPrices = (Prices)window.removeFirst();

                    if (prices.getHigh() > windowHigh) {
                        windowHigh = prices.getHigh();
                    }
                    if (prices.getLow() < windowLow) {
                        windowLow = prices.getLow();
                    }

                    if  (((oldPrices.getHigh() == windowHigh) && (prices.getHigh() != windowHigh)) ||
                         ((oldPrices.getLow() == windowLow) && (prices.getLow() != windowLow))) {

                        windowHigh = 0.0;
                        windowLow = 9999.0;
                        Iterator it2 = window.iterator();
                        while (it2.hasNext()) {
                            Prices p = (Prices)it2.next();
                            if (p.getHigh() > windowHigh) {
                                windowHigh = p.getHigh();
                            }
                            if (p.getLow() < windowLow) {
                                windowLow = p.getLow();
                            }
                        }
                    }

                    if (windowHigh != windowLow) {
                        double k = ((prices.getClose() - windowLow) / (windowHigh - windowLow)) * 100;
                        kMap.put(date, new Double(k));
                    }
                }
            }

            dMovingAverage = new MovingAverage(dPeriods, kMap);
            Map dMap = dMovingAverage.getMap();

            it = dMap.keySet().iterator();
            while (it.hasNext()) {
                java.util.Date dDate = (java.util.Date)it.next();
                dataMap.put(dDate, new StatData(((Double)kMap.get(dDate)).doubleValue(), ((Double)dMap.get(dDate)).doubleValue()));
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

            return "" + kPeriods + "(" + speed + "w/ %D=" + dPeriods + ")";
        }

        /**
         *  Get the interval used to determine short/medium/long term.
         *  @return     the interval time
         */
        public int getInterval() { return kPeriods; }

        /**
         *  Get the statistics data
         *  @return     the statistics data
         */
        protected LinkedList getData() {
            if (dataMap.size() == 0) {
                calculateStatData();
            }
            return super.getData();
        }
        /**
         *  Get the statistics for a key
         *  @return     the data value for the keyed entry
         */
        protected Object getData(java.util.Date key) {
            if (dataMap.size() == 0) {
                calculateStatData();
            }
            return super.getData(key);
        }

        /**
         *  Get the keys for the statistics
         *  @return     the data value for the keyed entry
         */
        protected LinkedList getKeys() {
            if (dataMap.size() == 0) {
                calculateStatData();
            }
            return super.getKeys();
        }

    /* *************************************** */
    /* *** Comparable METHODS              *** */
    /* *************************************** */
        /**
         *  Compare the objects for sorting.
         *  @param  o2      the object to compare to
         *  @return         negative, zero, possitive number dep

         ending on
         *                  this object to the passed object
         */
        public int compareTo(Object o2) {
            int c = kPeriods - ((Stats)o2).getKPeriods();
            if (c == 0) {
                c = kSlowing - ((Stats)o2).getKSlowing();
            }
            if (c == 0) {
                c = dPeriods - ((Stats)o2).getDPeriods();
            }

            return c;
        }
    }

    /**
     *  GraphPanel controls the graphing of the statistics for this test.
     *
     *  @author J.R. Titko
     */
    protected class GraphPanel extends TechnicalTestAbstract.LineGraphPanelAbstract {

    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The panel to draw the graph into. */
        private JPanel  graphPanel;
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
            graphPanel = new StatsGraphPanel();

            if (resetCount) {
                graph   = new Graph();
                graphCount = 0;
                graphPanel.add(graph);
            }
            graphNbr = graphCount++;
            graph.addListener(this);
        }

        /**
         *  Get the graph panel to draw the graph into.
         */
        public JPanel getGraphPanel() { return graphPanel; }

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
Debug.println(DEBUG, this, "StochasticKDTest.GraphPanel.getYBuffer() only");
            return 0;
        }

        /**
         *  Gets the maximum value represented in the graph.
         *
         *  @return         The data's maximum value.
         */
        public double getMaxValue() {
Debug.println(DEBUG, this, "StochasticKDTest.GraphPanel.getMaxValue() only");
            return 100.0;
        }

        /**
         *  Gets the minimum value represented in the graph.
         *
         *  @return         The data's minimum value.
         */
        public double getMinValue() {
Debug.println(DEBUG, this, "StochasticKDTest.GraphPanel.getMinValue() only");
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
Debug.println(DEBUG, this, "StochasticKDTest.GraphPanel.getTickSize() only");
            return 1;
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

            g.setColor(Color.BLACK);
            String testNameString = testName + ": ";
            g.drawString(testNameString, xAxisLeft, yOffset+g.getFontMetrics().getAscent());
            int testNameOffset = g.getFontMetrics().stringWidth(testNameString);

            g.setColor(colors[graphNbr][0]);
            String title = stats.getName();
            g.drawString(title, xAxisLeft + testNameOffset, yOffset+g.getFontMetrics().getAscent());

Debug.println(DEBUG, this, "GraphPanel.redrawTitle() end");
            return true;
        }

        /**
         *  Draws the graph.
         *
         *  @param  e   the event containing the graph to draw into
         */
        public void redrawComponent(GraphRedrawEvent e) {
Debug.println(DEBUG, this, "StochasticTest.GraphPanel.redrawComponent() start");
            Graphics g = e.getGraphics();

            Rectangle graphRect = graph.getGraphRect();
            int xAxisLeft       = (int)graphRect.getX();
            int xAxisRight      = (int)graphRect.getX() + (int)graphRect.getWidth();
            int yAxisTop        = (int)graphRect.getY();
            int yAxisBottom     = (int)graphRect.getY() + (int)graphRect.getHeight();

            double scale    = e.getYScale() * e.getMinTickSize();
            double minValue = e.getMinValue();

            g.setColor(colors[graphNbr][0]);
            redrawGraph(g, stats, xAxisLeft, xAxisRight, yAxisBottom, minValue, scale);

Debug.println(DEBUG, this, "StochasticTest.GraphPanel.redrawComponent() end");
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
            double oldKValue = -1.0;
            double newKValue = -1.0;
            double oldDValue = -1.0;
            double newDValue = -1.0;

            boolean colorD = false;

            int    xOld = xAxisRight;
            for (int x = xAxisRight; x > xAxisLeft; x -= Graph.PIXEL_SCALE) {
                StatData value = (StatData)stats.getData(calendar.getTime());
                if (value != null) {
                    newKValue = value.getK();
                    newDValue = value.getD();
                    if (oldKValue!= -1.0) {
                        g.setColor(colors[graphNbr][0]);
                        g.drawLine(xOld, (int)(yAxisBottom - (oldKValue - minValue) / scale), x, (int)(yAxisBottom - (newKValue - minValue) / scale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldKValue - minValue) / scale), x, (int)(yAxisBottom + 1 - (newKValue - minValue) / scale));

                        g.setColor((colorD)?(colors[graphNbr][1]):Color.WHITE);
                        g.drawLine(xOld, (int)(yAxisBottom - (oldDValue - minValue) / scale), x, (int)(yAxisBottom - (newDValue - minValue) / scale));
                        g.drawLine(xOld, (int)(yAxisBottom + 1 - (oldDValue - minValue) / scale), x, (int)(yAxisBottom + 1 - (newDValue - minValue) / scale));
                    }

                    int recommend = makeRecommendation(calendar.getTime(), stats);
                    if (recommend > 0) {
                        g.setColor(Color.RED);
                        Polygon poly = new SellArrow();
                        poly.translate(x, (int)(yAxisBottom - (newKValue - minValue) / scale - 30));
                        g.fillPolygon(poly);
                    } else if (recommend < 0) {
                        g.setColor(Color.BLUE);
                        Polygon poly = new BuyArrow();
                        poly.translate(x, (int)(yAxisBottom - (newKValue - minValue) / scale + 30));
                        g.fillPolygon(poly);
                    }

                    oldKValue = newKValue;
                    oldDValue = newDValue;
                    xOld = x;

                    colorD = !colorD;
                }
                calendar.addWeekDays(-1);
            }
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
    public StochasticKDTest() {
        testName = TEST_NAME;
        sqlTable = SQL_TABLE;
    }

    /**
     *  Constructor used to instantiate a test for the contract.
     *
     *  @param  contract    the contract to perform the test on
     */
    public StochasticKDTest(Contract contract) {
        this();
        this.contract = contract;
        this.statsGraph = CommodityJFrame.statsGraph;
        loadParameters();
        loadData();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  This method loads the data used for each stochastic test that was
     *  determined to be an optimal stochastic test for a contract.
     */
    protected void loadData() {
Debug.println(DEBUG, this, "loadData() start");
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
                double value = ((StatData)it2.next()).getK();
                if (value < statDataMin) {
                    statDataMin = value;
                }
                if (value > statDataMax) {
                    statDataMax = value;
                }
            }
        }
/*
*/
Debug.println(DEBUG, this, "loadData() end");
    }

    /**
     *  Retrieves the parameter data for moving averages
     */
    protected void loadParameters() {
Debug.println(DEBUG, this, "loadParameters() start");
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
            sb.append("select id, k_periods, k_slowing_periods, d_periods " +
                      "from " + sqlTable + " " +
                      "where commodity_id = '" + contract.getSymbol() + "' " +
                      "  and contract_month = '" + contract.getMonthAsKey() + "' ");
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
Debug.println(DEBUG, this, "loadParameters() end");
    }

    /**
     *  Save the optimization tests.
     *
     *  @param  statsArray      array of successful stats
     */
    public void saveOptimizedStats(StatsAbstract statsArray[]) {
Debug.println(DEBUG, this, "saveOptimizedStats(statsArray[]) start");
        clearParameters();
        for (int i = 1; i < statsArray.length; i++) {
            if (statsArray[i] != null) {
                saveParameters(statsArray[i]);
            }
        }
Debug.println(DEBUG, this, "saveOptimizedStats(statsArray[]) start");
    }

    /**
     *  Saves the parameter data for a stochastic test
     *
     *  @param  statsAbs    the statistics to save parameters from
     */
    protected void saveParameters(StatsAbstract statsAbs) {
Debug.println(DEBUG, this, "saveParameters(paramDays) start");
        Stats stats = (Stats)statsAbs;
        String columns = "k_periods, k_slowing_periods, d_periods";
        String columnValues = "" + stats.getKPeriods() + ", " +
                                 + stats.getKSlowing() + ", " +
                                 + stats.getDPeriods();
        saveParameters(columns, columnValues, stats);
Debug.println(DEBUG, this, "saveParameters(paramDays) end");
    }

/* *************************************** */
/* *** TechnicalTestInterface METHODS  *** */
/* *************************************** */
    /**
     *  This method will depict the results of the test.  This test is
     *  displayed in the price graph panel.
     */
    public void graphResults() {
Debug.println(DEBUG, this, "graphResults() start");
        graphPanelList = new LinkedList();
        Iterator it = recommendCollection.iterator();
        while (it.hasNext()) {
            graphPanel = new GraphPanel((Stats)it.next());
            statsGraph.addStatsGraph(graphPanel.getGraphPanel());
            graphPanelList.add(graphPanel);
        }
Debug.println(DEBUG, this, "graphResults() end");
    }

    /**
     *  This method will turn off the display of the graph.
     */
    public void stopGraphResults() {
Debug.println(DEBUG, this, "graphResults() start");
        Iterator it = graphPanelList.iterator();
        while (it.hasNext()) {
            GraphPanel graphPanel = (GraphPanel)it.next();
            statsGraph.removeStatsGraph(graphPanel.getGraphPanel());
        }
        graphPanelList = null;
Debug.println(DEBUG, this, "graphResults() end");
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
Debug.println(DEBUG, this, "optimizeTest start - " + contract.getSymbol() + " " + contract.getMonthFormatted());
        TreeMap tableByPercent  = new TreeMap();
        Stats statsArray[] = new Stats[endRange + 1];

        for (int i = startRange; i < endRange + 1; i++) {
            Stats  bestStats = null;
            for (int j = 1; j <= MAX_SLOWING_PERIODS; j++) {
                for (int k = 4; k <= SHORT_TERM; k++) {
                    Stats stats = new Stats(i, j, k);
                    ListIterator lit = contract.getPriceDateListIterator(false);
                    while (lit.hasNext()) {
                        java.util.Date date = (java.util.Date)lit.next();
                        stats.addStatData(date, contract.getPrices(date));
                    }

                    CommodityTestSimulator simulator = new CommodityTestSimulator(contract, this, stats);

                    if ((stats.getProfitCount() + stats.getLossCount()) > 1) {
                        if ((stats.getCountRatio() > MIN_SUCCESS_RATIO) && ((bestStats == null) || (bestStats.getCountRatio() < stats.getCountRatio()))) {
                            bestStats = stats;
                        }
                    }
                }
            }
            if (bestStats != null) {
//System.out.println("Stochastic found");
                tableByPercent.put(new Double(bestStats.getCountRatio() - (i / 100000000.0)), new Integer(i)); // insures no duplicates
                statsArray[i] = bestStats;
            }
        }

        removeExtraOptimizedStats(tableByPercent, statsArray);
        saveOptimizedStats(statsArray);

        statCollection = new TreeSet();
        tableByTestId = new TreeMap();
Debug.println(DEBUG, this, "optimizeTest end   - " + contract.getSymbol() + " " + contract.getMonthFormatted());
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
Debug.println(DEBUG, this, "getRecommendation(int testId, java.util.Date date)");
        Stats stats = (Stats)tableByTestId.get(new Integer(testId));
        if (!recommendCollection.contains(stats)) {
            recommendCollection.add(stats);
        }
Debug.println(DEBUG, this, "getRecommendation(int testId, java.util.Date date) - recommendCollection.size=" + recommendCollection.size());
        String testName = this.testName + " " + stats.getName();
        return new Recommendation(testName, makeRecommendation(date, stats));
    }

    /**
     *  Make a recommendation based on the stats data.
     *
     *  @param  date    the date the recommendation is for
     *  @param  stats   the statistics
     *  @return         a recommendation based on this test
     */
    public int makeRecommendation(java.util.Date date, StatsAbstract statsObject) {
        int recommendation = Recommendation.NO_ACTION;
        Stats stats = (Stats)statsObject;

        LinkedList statsKeys = stats.getKeys();
        if (statsKeys.size() < 2) {
//System.out.println("Stochastic recommend NO ACTION (keys)");
            return Recommendation.NO_ACTION;
        }

        int keyIndex = statsKeys.indexOf(date);
        if (keyIndex < 2) {
//System.out.println("Stochastic recommend NO ACTION (index)");
            return Recommendation.NO_ACTION;
        }

        java.util.Date yesterDate = (java.util.Date)statsKeys.get(keyIndex - 1);

        double k = ((StatData)stats.getData(date)).getK();
        double kYesterday = ((StatData)stats.getData(yesterDate)).getK();
        double d = ((StatData)stats.getData(date)).getD();
        double dYesterday = ((StatData)stats.getData(yesterDate)).getD();

        if ((kYesterday < dYesterday) && (k > d)) {
            recommendation = Recommendation.BUY;
        } else if ((kYesterday > dYesterday) && (k < d)) {
            recommendation = Recommendation.SELL;
        }
        return recommendation;
    }
}