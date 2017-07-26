/*  x Properly Documented
 */
package prototype.commodities.tests;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.graph.*;
import prototype.commodities.dataaccess.*;

/**
 *  The OpenInterestTest performs the Open Interest test for a commodity.
 *
 *  @author J.R. Titko
 */
public class OpenInterestTest extends TechnicalTestAbstract {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The name of the test. */
    private static final String TEST_NAME = "Open Interest";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The GraphPanel for the graph */
    protected GraphPanel graphPanel = null;

    /** The statistics graph manager to put this graph in */
    private StatsGraphManager statsGraph = null;
    /** The lone stats instance */
    private Stats stats = null;

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
    /* *** GET AND SET METHODS             *** */
    /* *************************************** */
        /**
         *  Get the statistics map
         *  @return     the statistics data map
         */
        protected String getName() {
            return "";
        }

        /**
         *  Get the interval used to determine short/medium/long term.
         *  @return     the interval time
         */
        public int getInterval() { return 0; }

    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Add to the statistical data.
         *  @param  key     the date the prices are for
         *  @param  prices  the contract prices for the date
         *  @return         the calculated statistical value
         */
        public void addStatData(java.util.Date date, long openInterest) {
            dataList = null;
            keyList = null;

            dataMap.put(date, new Long(openInterest));
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
            return 0;
        }
    }

    /**
     *  GraphPanel controls the graphing of the statistics for this test.
     *
     *  @author J.R. Titko
     */
    private class GraphPanel extends TechnicalTestAbstract.BarGraphPanelAbstract {

    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The panel to draw the graph into. */
        private JPanel  graphPanel;

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

    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Constructor only used to retrieve the name of the test (getName()).
     *  This constructor cannot be used to successfully access the remaining
     *  methods.
     */
    public OpenInterestTest() {
        testName = TEST_NAME;
        statDataMin = 0;        // min is always 0
    }

    /**
     *  Constructor used to instantiate a test for the contract.
     *
     *  @param  contract    the contract to perform the test on
     */
    public OpenInterestTest(Contract contract) {
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
     *  Loads the data to for the moving averages test.
     */
    protected void loadData() {
Debug.println(DEBUG, this, "loadData() start");
        Map     priceList = contract.getPriceList();
        Prices  prices = null;
        Date    statsDate = null;

        Iterator it = priceList.keySet().iterator();
        while (it.hasNext()) {
            statsDate = (Date)it.next();
            prices = (Prices)priceList.get(statsDate);

            long value = prices.getInterest();
            stats.addStatData(statsDate, value);

/*  Minimum is always 0 for Open Interest
            if (value < statDataMin) {
                statDataMin = value;
            }
*/
            if (value > statDataMax) {
                statDataMax = value;
            }
        }
Debug.println(DEBUG, this, "loadData() end");
    }


    /**
     *  Retrieves the parameter data for moving averages
     */
    protected void loadParameters() {
        stats = new Stats();
        statCollection.add(stats);
        recommendCollection.add(stats);
    }

    /**
     *  Save the optimization tests.
     *
     *  @param  statsArray      array of successful stats
     */
    public void saveOptimizedStats(StatsAbstract statsArray[]) {}
    /**
     *  Saves the parameter data for a moving averages test
     *
     *  @param  paramDays   the number of days for the moving average
     */
    protected void saveParameters(int paramDays) {}

/* *************************************** */
/* *** TechnicalTestInterface METHODS  *** */
/* *************************************** */
    /**
     *  This method will depict the results of the test.  This test is
     *  displayed within its own statistics panel.
     */
    public void graphResults() {
Debug.println(DEBUG, this, "graphResults() start");
        graphPanel = new GraphPanel();
        statsGraph.addStatsGraph(graphPanel.getGraphPanel());
Debug.println(DEBUG, this, "graphResults() end");
    }

    /**
     *  This method will turn off the display of the graph.
     */
    public void stopGraphResults() {
Debug.println(DEBUG, this, "stopGraphResults() start");
        statsGraph.removeStatsGraph(graphPanel.getGraphPanel());
Debug.println(DEBUG, this, "stopGraphResults() end");
    }

    /**
     *  THIS METHOD NEEDS DEFINING
     *  THIS METHOD NEEDS DEFINING
     *  THIS METHOD NEEDS DEFINING
     */
    public void optimizeTest() { }

    /**
     *  Optimize this test to determine the most profitable combination
     *  of parameters that should be used.  This does not mean the test
     *  will produce a positive profitability, but simply the most
     *  profitable (least loss).
     *
     *  @param  startRange  starting range of test optimization
     *  @param  endRange    ending range of test optimization
     */
    public void optimizeTest(int startRange, int endRange) {}

    /**
     *  Process to occur each day for the optimization test.
     *
     *  @param  date    the next date to process
     *  @param  s   the stats object
     */
    public void optimizeDailyProcess(java.util.Date date, StatsAbstract s) {
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
     *  @param  statsObject the statistics as an object
     *  @return             a recommendation based on this test
     */
    public int makeRecommendation(java.util.Date date,  StatsAbstract statsObject) {
        return Recommendation.NO_ACTION;
    }
}