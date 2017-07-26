/*  x Properly Documented
 */
package commodities.tests;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import commodities.dataaccess.*;
import commodities.commodity.*;
import commodities.contract.*;
import commodities.event.*;
import commodities.menu.*;
import commodities.tests.technical.*;
import commodities.util.*;
import commodities.window.*;

/**
 *  The TestManager class controls what tests are available to the system
 *  and how they are accessed.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class TestManager implements PriceListener, ContractSelectionListener {
//    private static int counter = 0;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** Singleton instance of TestManager */
    private static TestManager testManager = TestManager.instance();

    /** Properties file identifier for the properties manager. */
    private static final String PROPERTIES_FILE = "commodities";
    /** The property name of the directory where the commodity test class files reside. */
    private static final String COMMODITY_TEST_DIR = "folder.commodity_tests";
    /** The property name of the package name of the commodity test classes. */
    private static final String COMMODITY_TEST_PACKAGE = "folder.commodity_tests_package";

    /** The table of tests by name. */
    private static Map testsByName;
    /** The table of tests by class. */
    private static Map testsByClass;

    /** Graph the short term graphs. */
    private static boolean shortTermGraphs = true;
    /** Graph the medium term graphs. */
    private static boolean mediumTermGraphs = true;
    /** Graph the long term graphs. */
    private static boolean longTermGraphs = true;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** List of tests to be graphed */
    private Map testGraphs = new TreeMap();

    /** Map of tests loaded for each contract */
    private Map testsByContract = new HashMap();
    /**
     *  List of classes loaded for each test.
     *  This is used as a sub-table of testsByContract.
     */
    private Map tableOfTests = new HashMap();

    /** Collection of recommendation locations */
//    private Set recommendSet = new HashSet();

    /** Graph the short term graphs. */
//    private boolean shortTermGraphs = true;
    /** Graph the medium term graphs. */
//    private boolean mediumTermGraphs = true;
    /** Graph the long term graphs. */
//    private boolean longTermGraphs = true;



/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Creates the Tests Manager.
     */
    private TestManager() {
//System.out.println("****************************");
//System.out.println("** Construct TestManager  **");
//System.out.println("****************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
        loadTests();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Get the names of all the tests.
     *
     *  @return     Array of all test names
     */
    public String[] getTestNames() {
        String names[] = new String[testsByName.size()];
        Iterator it = testsByName.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            names[i++] = (String)it.next();
        }
        return names;
    }

    /**
     *  Get an iterator of the names of all the tests.
     *
     *  @return     Iterator of test names.
     */
    public Iterator getTestNameIterator() {
        return testsByName.keySet().iterator();
    }

    /**
     *  Get the map of the class names of all the tests.
     *
     *  @return     Map of test names and classes
     */
    public Map getTestClasses() {
        return testsByClass;
    }

    /**
     *  Load the name of the tests and their class names.
     *
     *  This method uses the special constructor in the test classes
     *  that does not take a Contract and can only be used for retrieving
     *  the test's name.
     */
    private void loadTests() {
        testsByName     = new TreeMap();
        testsByClass    = new TreeMap();
        String pkgName = PropertyManager.instance().getProperties(PROPERTIES_FILE).getProperty(COMMODITY_TEST_PACKAGE);
        String dirName = PropertyManager.instance().getProperties(PROPERTIES_FILE).getProperty(COMMODITY_TEST_DIR);

        String dir[] = new File(dirName + "\\.").list(new FilenameFilter() {
                public boolean accept(File dir, String s) {
                    return s.endsWith("Test.class");
                }
            });

        for (int i = 0; i < dir.length; i++) {
            String className = dir[i].substring(0, dir[i].lastIndexOf("."));
            String testName = null;

            try {
                className = pkgName + "." + className;
                testName = ((TechnicalTestInterface)Class.forName(className).newInstance()).getName();
            } catch (Exception e) {
                System.err.println("Error instantiating class *" + className + "*");
                e.printStackTrace();
            }

            testsByName.put(testName, className);
            testsByClass.put(className, testName);
        }
    }

    /**
     *  Cycle through all of the contracts for all of the commodities and register
     *  as a contract price listener and contract selection listener for each contract.
     */
    private void registerContractListeners() {
        Iterator it = Commodity.getNameMap().keySet().iterator();
        while (it.hasNext()) {
            Iterator it2 = Commodity.byNameExchange((String)it.next()).getContracts();
            while (it2.hasNext()) {
                Contract contract = (Contract)it2.next();
                contract.addPriceListener(this);
                contract.addSelectionListener(this);
            }
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
        if (!testsByContract.containsKey(contract)) {
            tableOfTests = new HashMap();
            testsByContract.put(contract, tableOfTests);
        }
        tableOfTests = (HashMap)testsByContract.get(contract);
        if (!tableOfTests.containsKey(testName)) {
            Class paramTypes[] = {contract.getClass()};
            Constructor constructor = Class.forName((String)testsByName.get(testName)).getConstructor(paramTypes);
            Object obj[] = {contract};
            test = (TechnicalTestInterface)constructor.newInstance(obj);
            tableOfTests.put(testName, test);
        } else {
            test = (TechnicalTestInterface)tableOfTests.get(testName);
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
        CommodityAnalyzerJFrame.instance().getGraphPanel().repaint();
        CommodityAnalyzerJFrame.instance().getTestGraphPanel().repaint();
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
            CommodityAnalyzerJFrame.instance().getGraphPanel().repaint();
            CommodityAnalyzerJFrame.instance().getTestGraphPanel().setVisible(false);
            CommodityAnalyzerJFrame.instance().getTestGraphPanel().repaint();
            CommodityAnalyzerJFrame.instance().getTestGraphPanel().setVisible(true);
        }
    }

    /**
     *  Remove all tests from the list of tests to be graphed on the screen.
     */
    public void removeAllGraphs() {
        testGraphs.clear();
        CommodityAnalyzerJFrame.instance().getGraphPanel().repaint();
        CommodityAnalyzerJFrame.instance().getTestGraphPanel().clearTestGraph();
        CommodityAnalyzerJFrame.instance().getTestGraphPanel().repaint();
    }

    /**
     *  Graph short term graphs.
     *
     *  @param  graph   true to display graphs, otherwise false
     */
    public void graphShortTermTests(boolean graph) {
        if (shortTermGraphs != graph) {
            shortTermGraphs = graph;
        }
    }

    /**
     *  Graph medium term graphs.
     *
     *  @param  graph   true to display graphs, otherwise false
     */
    public void graphMediumTermTests(boolean graph) {
        if (mediumTermGraphs != graph) {
            mediumTermGraphs = graph;
        }
    }

    /**
     *  Graph long term graphs.
     *
     *  @param  graph   true to display graphs, otherwise false
     */
    public void graphLongTermTests(boolean graph) {
        if (longTermGraphs != graph) {
            longTermGraphs = graph;
        }
    }

    /**
     *  Short term graphs should be displayed or not.
     *
     *  @return     true to display graphs, otherwise false
     */
    public boolean doGraphShortTermTests() {
        return shortTermGraphs;
    }

    /**
     *  Medium term graphs should be displayed or not.
     *
     *  @return     true to display graphs, otherwise false
     */
    public boolean doGraphMediumTermTests() {
        return mediumTermGraphs;
    }

    /**
     *  Long term graphs should be displayed or not.
     *
     *  @return     true to display graphs, otherwise false
     */
    public boolean doGraphLongTermTests() {
        return longTermGraphs;
    }

/* *************************************** */
/* *** PriceListener METHODS           *** */
/* *************************************** */
    /**
     *  Invoked when the date changes.
     *  @param  e   the PriceEvent
     */
    public void changeDate(PriceEvent e) { }

    /**
     *  Invoked when an open price changes.
     *  @param  e   the PriceEvent
     */
    public void changeOpenPrice(PriceEvent e) { }

    /**
     *  Invoked when a high price changes.
     *  @param  e   the PriceEvent
     */
    public void changeHighPrice(PriceEvent e) { }

    /**
     *  Invoked when a low price changes.
     *  @param  e   the PriceEvent
     */
    public void changeLowPrice(PriceEvent e) { }

    /**
     *  Invoked when a close price changes.
     *  @param  e   the PriceEvent
     */
    public void changeClosePrice(PriceEvent e) {
//System.out.println("changeClosePrice() - Contract name = " + e.getContract().getName() + "   Date = " + e.getContract().getCurrentDate() + "   Counter = " + counter++);
//        new RecommendationAnalyzer(testsByClass, e.getContract(), TechnicalTestInterface.SHORT_TERM);
//        new RecommendationAnalyzer(testsByClass, e.getContract(), TechnicalTestInterface.MEDIUM_TERM);
//        new RecommendationAnalyzer(testsByClass, e.getContract(), TechnicalTestInterface.LONG_TERM);
    }

    /**
     *  Invoked when the volume changes.
     *  @param  e   the PriceEvent
     */
    public void changeVolume(PriceEvent e) { }

    /**
     *  Invoked when the open interest changes.
     *  @param  e   the PriceEvent
     */
    public void changeOpenInterest(PriceEvent e) { }

/* *************************************** */
/* *** ContractSelectionListener METHODS * */
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
        if (init) {
            removeAllGraphs();
            TestMenu.resetMenuCheckboxes();
        }
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
            synchronized (TestManager.class) {
                if (testManager == null) {
                    testManager = new TestManager();
                    // This cannot be in the constructor as it causes an infinite iteration loop
                    testManager.registerContractListeners();
                }
            }
        }
        return testManager;
    }
}
