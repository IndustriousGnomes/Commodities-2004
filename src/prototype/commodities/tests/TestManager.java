/*  x Properly Documented
 */
package prototype.commodities.tests;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import prototype.commodities.dataaccess.*;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.price.*;

/**
 *  The TestManager class controls what tests are available to the system
 *  and how they are accessed.
 *
 *  @author J.R. Titko
 */
public class TestManager {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Data manager for db2 calls */
    private static DataManagerInterface dataManager = DataManagerFactory.getInstance();
    /** Singleton instance of TestManager */
    private static TestManager testManager = null;

    /** The property name of the directory where the commodity test class files reside. */
    private static final String COMMODITY_TEST_DIR = "folder.commodity_prototype_tests";

    /** The number of tests to use as the best tests */
    private static final int NUMBER_OF_BEST_TESTS = 5;

    /** The table of tests by name. */
    private static Map tableByName;
    /** The table of tests by class. */
    private static Map tableByClass;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** List of tests to be graphed */
    private Map testGraphs = new TreeMap();

    /** Map of tests loaded for each contract */
    private Map tableByContract = new HashMap();
    /**
     *  List of classes loaded for each test.
     *  This is used as a sub-table of tableByContract.
     */
    private Map tableByTest = new HashMap();

    /** Collection of recommendation locations */
    private Set recommendSet = new HashSet();

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  PricePanelRecommendationAnalyzer is a threaded check of recommendations
     *  for contracts and posts them to the Price Panel.
     *
     *  @author J.R. Titko
     */
    private class PricePanelRecommendationAnalyzer extends Thread {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The contract to get the recommendation for */
        private Contract contract;
        /** The term to recommend for */
        private int term;
        /** The price table model to callback to with the recommendation */
        private PricePanel.PriceTableModel callback;
        /** The row to recommend for */
        private int row;


    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */

        /**
         *  Create an analyzer for a contract and term.
         *
         *  @param  contract    the contract to get the recommendation for
         *  @param  term        short, medium, or long term recommendation
         *  @param  callback    the callback location to populate with the recommendation
         *  @param  row         the row in the table to update
         */
        public PricePanelRecommendationAnalyzer(Contract contract, int term, PricePanel.PriceTableModel callback, int row) {
            this.contract   = contract;
            this.term       = term;
            this.callback   = callback;
            this.row        = row;

            setPriority(6);
            start();
        }

    /* *************************************** */
    /* *** Thread METHODS                  *** */
    /* *************************************** */
        public void run() {
            RecommendationAnalyzer analyzer = new RecommendationAnalyzer(contract, term);
            Recommendation recommendation = analyzer.getRecommendation();
            switch (term) {
                case TechnicalTestInterface.SHORT_TERM :
                        callback.setShortTermRecommendation(row, recommendation);
                        break;
                case TechnicalTestInterface.MEDIUM_TERM:
                        callback.setMediumTermRecommendation(row, recommendation);
                        break;
                case TechnicalTestInterface.LONG_TERM  :
                        callback.setLongTermRecommendation(row, recommendation);
                        break;
            }
        }
    }

    /**
     *  PricePanelRecommendationAnalyzer is a threaded check of recommendations
     *  for contracts.
     *
     *  @author J.R. Titko
     */
    private class RecommendationAnalyzer {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The contract to get the recommendation for */
        private Contract contract = null;
        /** The term to recommend for */
        private int term = 0;
        /** The date to recommend for */
        private java.util.Date recommendDate = null;


    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */

        /**
         *  Create an analyzer for a contract and term.
         *
         *  @param  contract    the contract to get the recommendation for
         *  @param  term        short, medium, or long term recommendation
         */
        public RecommendationAnalyzer(Contract contract, int term) {
            this.contract   = contract;
            this.term       = term;
        }

        /**
         *  Create an analyzer for a contract and term.
         *
         *  @param  contract    the contract to get the recommendation for
         *  @param  row         the row in the table to update
         *  @param  term        short, medium, or long term recommendation
         *  @param  callback    the callback location to populate with the recommendation
         */
        public RecommendationAnalyzer(Contract contract, int term, java.util.Date recommendDate) {
            this(contract, term);
            this.recommendDate = recommendDate;
        }
    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Determine the buy/sell recommendation based on the
         *  optimal tests for a contract.
         *
         *  @return             the consolidated long term recommendation
         */
        private Recommendation getRecommendation() {

            try {
                int termBottom = 0;
                switch (term) {
                    case TechnicalTestInterface.SHORT_TERM : termBottom = 0;
                                                             break;
                    case TechnicalTestInterface.MEDIUM_TERM: termBottom = TechnicalTestInterface.SHORT_TERM;
                                                             break;
                    case TechnicalTestInterface.LONG_TERM  : termBottom = TechnicalTestInterface.MEDIUM_TERM;
                                                             break;
                }

                Collection tests = dataManager.getBestTestXref(contract, term, termBottom, NUMBER_OF_BEST_TESTS);

                boolean firstIteration = true;
                int origOrientation = 0; // neg is buy, pos is sell
                Map recommendations = new HashMap();

                ListIterator dateIterator = contract.getPriceDateListIterator(true);
                if (recommendDate != null) {
                    while (dateIterator.hasPrevious()) {
                        java.util.Date date = (java.util.Date)dateIterator.previous();
                        if (date.equals(recommendDate)) {
                            dateIterator.next();
                            break;
                        } else if (date.before(recommendDate)) {
                            return new Recommendation("Consolidated", Recommendation.NO_ACTION);
                        }
                    }
                }

loop:           while (dateIterator.hasPrevious()) {
                    java.util.Date date = (java.util.Date)dateIterator.previous();
//System.out.println("Contract = " + contract.getName() + " Looking at " + date);
                    Map dailyRecommendations = getTestRecommendations(tests, date);
                    Iterator it = dailyRecommendations.keySet().iterator();
                    while (it.hasNext()) {
                        TestIdentifier testId = (TestIdentifier)it.next();
                        Recommendation recommend = (Recommendation)dailyRecommendations.get(testId);
                        if (recommendations.containsKey(testId)) {
                            Recommendation oldRecommend = (Recommendation)recommendations.get(testId);

                            if (oldRecommend.getRecommendation() == Recommendation.NO_ACTION) {
                                recommendations.put(testId, recommend);
                            } else if (recommend.getRecommendation() == Recommendation.NO_ACTION) {
                                // do nothing

                            } else if (((oldRecommend.getRecommendation() == Recommendation.SELL) || (oldRecommend.getRecommendation() == Recommendation.SETTLE_SELL)) &&
                                       ((recommend.getRecommendation() == Recommendation.SELL) || (recommend.getRecommendation() == Recommendation.SETTLE_SELL))) {
                                recommendations.put(testId, new Recommendation(oldRecommend.getTestName(), Recommendation.SELL));

                            } else if (((oldRecommend.getRecommendation() == Recommendation.BUY) || (oldRecommend.getRecommendation() == Recommendation.SETTLE_BUY)) &&
                                       ((recommend.getRecommendation() == Recommendation.BUY) || (recommend.getRecommendation() == Recommendation.SETTLE_BUY))) {
                                recommendations.put(testId, new Recommendation(oldRecommend.getTestName(), Recommendation.BUY));
                            } else {
                                recommendations.put(testId, new Recommendation(oldRecommend.getTestName(), Recommendation.NO_ACTION));
                                break loop;
                            }
                        } else {
                            recommendations.put(testId, recommend);
                        }
                    }

                    int buyRec = 0;
                    int sellRec = 0;
                    it = recommendations.values().iterator();
                    while (it.hasNext()) {
                        int rec = ((Recommendation)it.next()).getRecommendation();
                        if ((rec == Recommendation.BUY) || (rec == Recommendation.SETTLE_BUY)) {
                            buyRec++;
                        } else if ((rec == Recommendation.SELL) || (rec == Recommendation.SETTLE_SELL)) {
                            sellRec++;
                        }
                    }
//System.out.println("Contract = " + contract.getName() + " Looking at " + date + " majority=" + (tests.size() / 2) + " buy=" + buyRec + " sell=" + sellRec);
                    if (firstIteration) {
                        origOrientation = sellRec - buyRec;
                    } else if ((((sellRec - buyRec) < 0) && (origOrientation > 0)) ||
                               (((sellRec - buyRec) > 0) && (origOrientation < 0))) {
                        return new Recommendation("Consolidated", Recommendation.NO_ACTION);
                    }
                    if ((buyRec + sellRec) == 0) {
                        return new Recommendation("Consolidated", Recommendation.NO_ACTION);
                    }
                    if (buyRec > (tests.size() / 2)) {
                        return new Recommendation("Consolidated", Recommendation.BUY);
                    }
                    if (sellRec > (tests.size() / 2)) {
                        return new Recommendation("Consolidated", Recommendation.SELL);
                    }
                    if (firstIteration && ((buyRec > 0) && (sellRec > 0))) {
                        return new Recommendation("Consolidated", Recommendation.NO_ACTION);
                    }
                    firstIteration = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new Recommendation("Consolidated", Recommendation.NO_ACTION);
        }

        /**
         *  Get the recommendations based on a date.
         *
         *  @return             the consolidated long term recommendation
         */
        private Map getTestRecommendations(Collection tests, java.util.Date date) {
            Map recommendations = new HashMap();

            Iterator it = tests.iterator();
            while (it.hasNext()) {
                TestIdentifier testIdentifier = (TestIdentifier)it.next();
                String testName = (String)tableByClass.get(testIdentifier.getTestClass());
//System.out.println("Test " + testName + " for " + contract.getSymbol() + " " + contract.getMonthFormatted() + " term=" + term);

                try {
                    TechnicalTestInterface test = getTestInstance(testName, contract);
                    Recommendation recommendation = test.getRecommendation(testIdentifier.getTestId(), date);
//System.out.println("Test " + testName + " for " + contract.getSymbol() + " " + contract.getMonthFormatted() + " term=" + term + " recommend=" + recommendation.getRecommendationLabel());

                    if (recommendation != null) {
                        recommendations.put(testIdentifier, recommendation);
                    }
                } catch (Exception e) {
                    System.err.println("Error recommending test " + testName + " for " + contract.getSymbol() + " " + contract.getMonthFormatted() + " term=" + term);
                }
            }
            return recommendations;
        }
    }


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates the Tests menu.
     */
    public TestManager() {
        loadTests();
    }

/* *************************************** */
/* *** METHODS                         *** */
/* *************************************** */

    /**
     *  Get the names of all the tests.
     *
     *  @return     Array of all test names
     */
    public String[] getTestNames() {
        if (testManager == null) {
            return null;
        }

        String names[] = new String[tableByName.size()];
        Iterator it = tableByName.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            names[i++] = (String)it.next();
        }
        return names;
    }

    /**
     *  Load the name of the tests and their class names.
     *
     *  This method uses the special constructor in the test classes
     *  that does not take a Contract and can only be used for retrieving
     *  the test's name.
     */
    private void loadTests() {
        tableByName     = new TreeMap();
        tableByClass    = new TreeMap();
        String dirName = CommodityProperties.instance().getProperty(COMMODITY_TEST_DIR);
        String dir[] = new File(dirName + "\\.").list(new FilenameFilter() {
                public boolean accept(File dir, String s) {
                    return s.endsWith("Test.class");
                }
            });

        for (int i = 0; i < dir.length; i++) {
            String className = dir[i].substring(0, dir[i].lastIndexOf("."));
            String testName = null;

            try {
                className = "prototype.commodities.tests." + className;
                testName = ((TechnicalTestInterface)Class.forName(className).newInstance()).getName();
            } catch (Exception e) {
                System.err.println("Error instantiating class *" + className + "*");
                e.printStackTrace();
            }

            tableByName.put(testName, className);
            tableByClass.put(className, testName);
        }
    }

    /**
     *  Get the instance of a test for a contract.
     *
     *  @param  testName    name of the test (from menu)
     *  @param  contract    contract to be graphed
     */
    public synchronized TechnicalTestInterface getTestInstance(String testName, Contract contract) throws Exception {
        TechnicalTestInterface test = null;
        if (!tableByContract.containsKey(contract)) {
            tableByTest = new HashMap();
            tableByContract.put(contract, tableByTest);
        }
        tableByTest = (HashMap)tableByContract.get(contract);
        if (!tableByTest.containsKey(testName)) {
            Class paramTypes[] = {contract.getClass()};
            Constructor constructor = Class.forName((String)tableByName.get(testName)).getConstructor(paramTypes);
            Object obj[] = {contract};
            test = (TechnicalTestInterface)constructor.newInstance(obj);
            tableByTest.put(testName, test);
        } else {
            test = (TechnicalTestInterface)tableByTest.get(testName);
        }
        return test;
    }


    /**
     *  Add test to the list of tests to be graphed on the screen.
     *
     *  @param  testName    name of the test (from menu)
     *  @param  contract    contract to be graphed
     */
    public void addGraph(String testName, Contract contract) {
        if (!testGraphs.containsKey(testName)) {
            try {
                TechnicalTestInterface test = getTestInstance(testName, contract);

                testGraphs.put(testName, test);
                test.graphResults();
            } catch (Exception e) {
                System.err.println("Error graphing test " + testName);
                e.printStackTrace();
            }
        }
        CommodityJFrame.graph.repaint();
        CommodityJFrame.statsGraph.repaint();
    }

    /**
     *  Remove test from the list of tests to be graphed on the screen.
     *
     *  @param  testName    name of the test (from menu)
     */
    public void removeGraph(String testName) {
        if (testGraphs.containsKey(testName)) {
            TechnicalTestInterface test = (TechnicalTestInterface)testGraphs.remove(testName);
            test.stopGraphResults();
            CommodityJFrame.graph.repaint();
            CommodityJFrame.statsGraph.setVisible(false);
            CommodityJFrame.statsGraph.repaint();
            CommodityJFrame.statsGraph.setVisible(true);
        }
    }

    /**
     *  Remove all tests from the list of tests to be graphed on the screen.
     */
    public void removeAllGraphs() {
        testGraphs.clear();
        CommodityJFrame.graph.repaint();
        CommodityJFrame.statsGraph.clearStatsGraph();
        CommodityJFrame.statsGraph.repaint();
    }

    /**
     *  Determine the buy/sell recommendation based on the
     *  optimal tests for a contract for a given date.
     *
     *  @param  contract    the contract to get the recommendation for
     *  @param  row         the row in the table to update
     *  @param  date        the date to get the recommendation for
     *  @return             the recommendation
     */
    public Recommendation getRecommendation(Contract contract, int term, java.util.Date date) {
        return (new RecommendationAnalyzer(contract, term, date)).getRecommendation();
    }

    /**
     *  Determine the buy/sell recommendation based on the
     *  optimal tests for a contract and puts it in the PricePanel.
     *
     *  @param  contract    the contract to get the recommendation for
     *  @param  term        short, medium, or long term recommendation
     *  @param  row         the row in the table to update
     *  @param  callback    the callback location to populate with the recommendation
     */
    public void getRecommendation(Contract contract, int term, PricePanel.PriceTableModel callback, int row) {
        recommendSet.add(new PricePanelRecommendationAnalyzer(contract, term, callback, row));
    }

    /**
     *  Reprocesses the recommendations that have been requested before.
     */
    public void reprocessRecommendations() {
        tableByContract = new HashMap();
        Iterator it = recommendSet.iterator();
System.out.println("Reprocess Recommendations");
        while (it.hasNext()) {
System.out.print("=");
            PricePanelRecommendationAnalyzer analyzer = (PricePanelRecommendationAnalyzer)it.next();
            analyzer.run();
        }
System.out.println();
    }

    /**
     *  Optimize all tests for all commodities.
     */
    public void optimizeTests() {
        try {
            dataManager.clearTestXref();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Map commodities = Commodity.getNameMap();
        Iterator it = commodities.values().iterator();
        while (it.hasNext()) {
            Commodity commodity = (Commodity)it.next();
            Iterator it2 = commodity.getContracts();
            while (it2.hasNext()) {
                Contract contract = (Contract)it2.next();

                Iterator it3 = tableByName.keySet().iterator();
                while (it3.hasNext()) {
                    String testName = (String)it3.next();
                    try {
                        TechnicalTestInterface test = getTestInstance(testName, contract);
                        test.optimizeTest();
                    } catch (Exception e) {
                        System.err.println("Error optimizing test " + testName);
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println();
        System.out.println("Optimization complete");
        reprocessRecommendations();
    }



/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Return the single instance of TestManager.
     *
     *  @return     singleton TestManager
     */
    public static TestManager instance() {
        if (testManager == null) {
            testManager = new TestManager();
        }
        return testManager;
    }

}