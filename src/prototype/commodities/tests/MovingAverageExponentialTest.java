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
 *  The MovingAverageExponentialTest performs the moving average test using exponential
 *  factoring for a commodity.
 *
 *  @author J.R. Titko
 */
public class MovingAverageExponentialTest extends TechnicalTestAbstract {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The name of the test. */
    private static final String TEST_NAME = "Moving Average - Exponential";
    /** The name of the sql table associated with this test. */
    private static final String SQL_TABLE = "test_moving_averages_exponential";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The GraphPanel for the graph */
    protected GraphPanel graphPanel = null;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  Stats holds the individual tests interval and the related
     *  statistical data.
     *
     *  @author J.R. Titko
     */
    protected class Stats extends StatsAbstract {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** Exponential percentage of current day data. */
        private int expPercent;
        /** Source data for determining the moving average. */
        private TreeMap sourceMap = new TreeMap();
        /** Instance of MovingAverage to use. */
        private MovingAverageExponential movingAverage;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        public Stats(int expPercent) {
            this.expPercent = expPercent;
        }

    /* *************************************** */
    /* *** GET AND SET METHODS             *** */
    /* *************************************** */
        /**
         *  Get the exponential percent
         *  @return     the exponential percent
         */
        public int getExpPercent() { return expPercent; }

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

            sourceMap.put(date, prices.getCloseObject());
            if (movingAverage != null) {
                movingAverage.add(date, prices.getCloseObject());
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
            return "" + expPercent + "%";
        }

        /**
         *  Get the interval used to determine short/medium/long term.
         *  @return     the interval time
         */
        public int getInterval() {
            return (int)((2.0 / (expPercent / 100.0)) - 1);
        }

        /**
         *  Get the statistics data
         *  @return     the statistics data
         */
        protected LinkedList getData() {
            if (movingAverage == null) {
                movingAverage = new MovingAverageExponential(expPercent, sourceMap);
                dataMap = movingAverage.getMap();
            }
            return super.getData();
        }
        /**
         *  Get the statistics for a key
         *  @return     the data value for the keyed entry
         */
        protected Object getData(java.util.Date key) {
            if (movingAverage == null) {
                movingAverage = new MovingAverageExponential(expPercent, sourceMap);
                dataMap = movingAverage.getMap();
            }
            return super.getData(key);
        }

        /**
         *  Get the keys for the statistics
         *  @return     the data value for the keyed entry
         */
        protected LinkedList getKeys() {
            if (movingAverage == null) {
                movingAverage = new MovingAverageExponential(expPercent, sourceMap);
                dataMap = movingAverage.getMap();
            }
            return super.getKeys();
        }

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
            return (((Stats)o2).getExpPercent() - expPercent);
        }
    }

    /**
     *  GraphPanel controls the graphing of the statistics for this test.
     *
     *  @author J.R. Titko
     */
    protected class GraphPanel extends TechnicalTestAbstract.LineGraphPanelAbstract {

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
         *  The graph of this test's statistics.
         *  If the resetCount parameter is set to false, then this graph is
         *  a subsequent graph and is overlaid on the prior graphs.
         *
         *  @param  resetCount  false indicates a subsequent graph to the first
         */
        public GraphPanel(boolean resetCount) {
            graph   = CommodityJFrame.graph;
            if (resetCount) {
                graphCount = 0;
            }
            graphNbr = graphCount++;
            graph.addListener(this);
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
    public MovingAverageExponentialTest() {
        testName = TEST_NAME;
        sqlTable = SQL_TABLE;
    }

    /**
     *  Constructor used to instantiate a test for the contract.
     *
     *  @param  contract    the contract to perform the test on
     */
    public MovingAverageExponentialTest(Contract contract) {
        this();
        this.contract = contract;
        loadParameters();
        loadData();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  This method loads the data used for each moving average test that was
     *  determined to be an optimal moving average test for a contract.
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
            sb.append("select id, param_percent " +
                      "from " + sqlTable + " " +
                      "where commodity_id = '" + contract.getSymbol() + "' " +
                      "  and contract_month = '" + contract.getMonthAsKey() + "' ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            while (rs.next()) {
                int id = rs.getInt(1);
                Stats stats = new Stats(rs.getInt(2));
                statCollection.add(stats);
                tableByTestId.put(new Integer(id), stats);
            }
            rs.close();
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
     *  @param  statsArray      array of success ratios
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
     *  Saves the parameter data for a moving averages test
     *
     *  @param  statsAbs    the stats to be saved
     */
    protected void saveParameters(StatsAbstract statsAbs) {
Debug.println(DEBUG, this, "saveParameters(statsAbs) start");
        Stats stats = (Stats)statsAbs;
        String columns = "param_percent";
        String columnValues = "" + stats.getExpPercent();
        saveParameters(columns, columnValues, stats);
Debug.println(DEBUG, this, "saveParameters(statsAbs) end");
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
        graphPanel = new GraphPanel();
Debug.println(DEBUG, this, "graphResults() end");
    }

    /**
     *  This method will turn off the display of the graph.
     */
    public void stopGraphResults() {
Debug.println(DEBUG, this, "graphResults() start");
        graphPanel.removeListeners();
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
        int endRange = 40;
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
            Stats stats = new Stats(i);
            ListIterator lit = contract.getPriceDateListIterator(false);
            while (lit.hasNext()) {
                java.util.Date date = (java.util.Date)lit.next();
                stats.addStatData(date, contract.getPrices(date));
            }

            CommodityTestSimulator simulator = new CommodityTestSimulator(contract, this, stats);

            if ((stats.getProfitCount() + stats.getLossCount()) > 1) {
                if (stats.getCountRatio() > MIN_SUCCESS_RATIO) {
                    Double ratioDouble = new Double(stats.getCountRatio() - (i / 100000000.0)); // insures no duplicates
                    tableByPercent.put(ratioDouble, new Integer(i));
                    statsArray[i] = stats;
                }
            }
        }

        removeExtraOptimizedStats(tableByPercent, statsArray);
        saveOptimizedStats(statsArray);

        statCollection = new TreeSet();
        recommendCollection = new TreeSet();
        tableByTestId = new TreeMap();
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
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics
     *  @return             a recommendation based on this test
     */
    public int makeRecommendation(java.util.Date date, StatsAbstract statsObject) {
        return makeRecommendation(date, statsObject, true);
    }

    /**
     *  Make a recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics
     *  @return             a recommendation based on this test
     */
    protected int makeRecommendation(java.util.Date date, StatsAbstract statsObject, boolean firstDay) {
        int recommendation = Recommendation.NO_ACTION;
        double priceClose = contract.getPrices(date).getClose();
        Stats stats = (Stats)statsObject;

        LinkedList statsKeys = stats.getKeys();
        if (firstDay && (statsKeys.size() < 3)) {
            return Recommendation.NO_ACTION;
        }

        int keyIndex = statsKeys.indexOf(date);
        if (firstDay && (keyIndex < 2)) {
            return Recommendation.NO_ACTION;
        }

        java.util.Date yesterDate = (java.util.Date)statsKeys.get(keyIndex - 1);

        double y2 = ((Double)stats.getData(date)).doubleValue();
        double y1 = ((Double)stats.getData(yesterDate)).doubleValue();
        double slope = y2 - y1;

        if ((slope > 0.0) && (priceClose >= y2)) {                       // Positive slope and price above moving average
            recommendation = Recommendation.BUY;
        } else if ((slope < 0.0) && (priceClose <= y2)) {              // Negative slope and price below moving average
            recommendation = Recommendation.SELL;
/*
        } else if (priceClose >= y2) {                                  // Price above moving average but not positive slope
            recommendation = Recommendation.SETTLE_BUY;
        } else if (priceClose <= y2) {                                  // Price below moving average but not negative slope
            recommendation = Recommendation.SETTLE_SELL;
*/
        }

        if (firstDay && (recommendation == makeRecommendation(yesterDate, statsObject, false))) {
            return Recommendation.NO_ACTION;
        } else {
            return recommendation;
        }
    }
}