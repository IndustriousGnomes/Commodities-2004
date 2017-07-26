/* x Review Javadocs */
package commodities.tests.technical;

import java.sql.*;
import java.util.*;

import commodities.contract.*;
import commodities.dataaccess.database.*;
import commodities.price.*;
import commodities.simulator.*;
import commodities.tests.*;
import commodities.window.*;

/**
 *  The CycleLowTest determines cycles based on price lows.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @update     2004.11.21
 */

public class CycleLowTest extends TechnicalTestAbstract {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The name of the test. */
    private static final String TEST_NAME = "Cycle Lows";
    /** The name of the sql table associated with this test. */
    private static final String SQL_TABLE = "test_cycle_lows";

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
     *  @author     J.R. Titko
     *  @version    1.0
     *  @update     2004.11.21
     */
    protected class Stats extends StatsAbstract {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** Cycle interval */
        private int interval;
        /** Source data for determining the cycle. */
        private TreeSet sourceData = new TreeSet();
        /** Instance of Cycle to use. */
        private Cycle cycle;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        public Stats(int interval) {
            this.interval = interval;
            sourceData.add(contract.getCurrentDate());
        }

    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Add to the statistical data.
         *  @param  date    the date the prices are for
         */
        public void addStatData(java.util.Date date) {
            dataList = null;
            keyList = null;

            sourceData.add(date);
            if (cycle != null) {
                cycle.add(date);
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
            return "" + interval;
        }

        /**
         *  Get the moving average interval
         *  @return     the moving average interval
         */
        public int getInterval() {
            return interval;
        }

        /**
         *  Get the statistics data
         *  @return     the statistics data
         */
        protected LinkedList getData() {
            if (cycle == null) {
                cycle = new Cycle(interval, sourceData);
            }
            dataList = new LinkedList(cycle.getMap().keySet());
            return dataList;
        }

        /**
         *  Get the statistics for a key
         *  @param  key the date to retrieve a value for
         *  @return     the data value for the keyed entry
         */
        protected Object getData(java.util.Date key) {
            if (cycle == null) {
                cycle = new Cycle(interval, sourceData);
                dataMap = cycle.getMap();
            }
            return super.getData(key);
        }

        /**
         *  Get the statistics for a key for use in a graph.
         *  @param  key the date to retrieve a value for
         *  @return     the data value for the keyed entry
         */
        protected Object getGraphData(java.util.Date key) {
            if (getData(key) == null) {
                return null;
            } else {
                return new Double(contract.getContractLowPrice() + 3);
            }
        }

        /**
         *  Get the keys for the statistics
         *  @return     the data value for the keyed entry
         */
        protected LinkedList getKeys() {
            if (cycle == null) {
                cycle = new Cycle(interval, sourceData);
                dataMap = cycle.getMap();
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
            return (interval - ((Stats)o2).getInterval());
        }
    }

    /**
     *  GraphPanel controls the graphing of the statistics for this test.
     *
     *  @author J.R. Titko
     */
    protected class GraphPanel extends TechnicalTestAbstract.SegmentLineGraphPanelAbstract {

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
            graph   = CommodityAnalyzerJFrame.instance().getGraphPanel();
            if (resetCount) {
                graphCount = 0;
            }
            graphNbr = graphCount++;
System.out.println("CycleLowTest - adding graph listener");
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
    public CycleLowTest() {
        testName = TEST_NAME;
        sqlTable = SQL_TABLE;
    }

    /**
     *  Constructor used to instantiate a test for the contract.
     *
     *  @param  contract    the contract to perform the test on
     */
    public CycleLowTest(Contract contract) {
        this();
        this.contract = contract;
        loadParameters();
        loadData();
    }

/* *************************************** */
/* *** TestnicalTestAbstract METHODS   *** */
/* *************************************** */
    /**
     *  This method loads the data used for each cycle test that was
     *  determined to be an optimal moving average test for a contract.
     */
    public void loadData() {
        Collection  pointList = contract.getLowPricePoints();
        Prices  prices = null;
        java.util.Date  statsDate = null;

// For each day's prices...
        Iterator it = pointList.iterator();
        while (it.hasNext()) {
            CriticalPricePoint point = (CriticalPricePoint)it.next();

            statsDate = point.getDate();

// recalculate the statististics data by adding it to the queue for that cycle.
            Iterator it2 = statCollection.iterator();
            while (it2.hasNext()) {
                ((Stats)it2.next()).addStatData(statsDate);
            }
        }

/* always the size of the price screen
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
            sb.append("select id, cycle " +
                      "from " + sqlTable + " " +
                      "where commodity_id = '" + contract.getSymbol() + "' " +
                      "  and contract_month = '" + contract.getMonth() + "' ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            while (rs.next()) {
                int id = rs.getInt(1);
                int cycle = rs.getInt(2);
                Stats stats = new Stats(cycle);
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
        Stats stats = (Stats)statsAbs;
        String columns = "cycle";
        String columnValues = "" + stats.getInterval();
        saveParameters(columns, columnValues, stats);
    }

    /**
     *  This method will depict the results of the test.  This test is
     *  displayed in the price graph panel.
     */
    public void graphResults() {
        graphPanel = new GraphPanel();
    }

    /**
     *  Stop graphing this test.
     */
    public void stopGraphResults() {
        graphPanel.removeListeners();
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
        int endRange = Math.min(LONG_TERM + 1, contract.getPriceList().size() / MIN_NUMBER_OF_TRADES);
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
            Iterator lit = contract.getLowPricePoints().iterator();
            while (lit.hasNext()) {
                java.util.Date date = ((CriticalPricePoint)lit.next()).getDate();
                stats.addStatData(date);
            }

// simulator goes here


// success criteria goes here
            if (stats.getCountRatio() > MIN_SUCCESS_RATIO) {
                int interval = stats.getInterval();
                Double ratioDouble = new Double(stats.getCountRatio() - (interval / 100000000.0)); // insures no duplicates
                tableByPercent.put(ratioDouble, new Integer(interval));
                statsArray[interval] = stats;
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
     *  @param  date        the date the recommendation is for
     *  @param  statsAbs    the statistics as an object
     *  @return             a recommendation based on this test
     */
    public Recommendation makeRecommendation(java.util.Date date, StatsAbstract statsAbs) {
        String testName = this.testName + " " + statsAbs.getName();
        return new Recommendation(date, testName, makeBuySellRecommendation(date, statsAbs), makeStopLossRecommendation(date, statsAbs));
    }

    /**
     *  Make a buy/sell recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a buy/sell recommendation based on this test
     */
    public int makeBuySellRecommendation(java.util.Date date, StatsAbstract stats) {
        if (stats.getData(date) != null) {
            return Recommendation.BUY;
        }
        return Recommendation.NO_ACTION;
    }

    /**
     *  Make a stop loss recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a stop loss recommendation based on this test
     */
    public double makeStopLossRecommendation(java.util.Date date, StatsAbstract stats) {
        return 0.0;
    }
}