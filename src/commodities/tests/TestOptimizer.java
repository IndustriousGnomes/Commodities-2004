/* _ Review Javadocs */
package commodities.tests;

import java.io.*;
import java.util.*;
import javax.swing.*;

import commodities.dataaccess.*;
import commodities.commodity.*;
import commodities.contract.*;
import commodities.tests.technical.*;
import commodities.window.*;

import com.lang.Trilean;

/**
 *  TestOptimizer controls the optimization of the technical tests.
 *  Either a single contract, all the contracts in a commodity, or
 *  all of the commodities can have their tests optimized.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @updated    2004.11.15
 */

public class TestOptimizer extends Thread {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** A reference to the TestManager. */
    private static TestManager testManager = TestManager.instance();

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /**
     *  Indicates what processing mode (contract, commodity, or all) to run in.
     */
    private Trilean runMode = new Trilean();

    /** The contract. */
    private Contract contract;
    /** The commodity. */
    private Commodity commodity;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a TestOptimizer for a single contract.
     *
     *  @param  contract    The contract to perform the test optimization on.
     */
    public TestOptimizer(Contract contract) {
        runMode.pos1();
        this.contract = contract;

        start();
    }

    /**
     *  Create a TestOptimizer for all the contracts in a single commodity.
     *
     *  @param  commodity   The commodity to perform the test optimization on.
     */
    public TestOptimizer(Commodity commodity) {
        runMode.pos2();
        this.commodity = commodity;

        start();
    }

    /**
     *  Create a TestOptimizer for all commodities.
     */
    public TestOptimizer() {
        runMode.pos3();

        start();
    }


/* *************************************** */
/* *** Thread METHODS                *** */
/* *************************************** */
    /**
     *  The main processing loop of this thread.
     */
    public void run() {
        try {
            if (runMode.isPos1()) {
                dataManager.clearTestXref(contract);
                optimize(contract);
            } else if (runMode.isPos2()) {
                dataManager.clearTestXref(commodity);
                optimize(commodity);
            } else if (runMode.isPos3()) {
                dataManager.clearTestXref();
                optimize();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(CommodityAnalyzerJFrame.instance(), "Your optimization request could not be performed.  See error log.");
            return;
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Optimize the tests for all commodities.
     */
    public void optimize() {
        Iterator it = Commodity.getNameMap().values().iterator();
        while (it.hasNext()) {
            optimize((Commodity)it.next());
        }
    }

    /**
     *  Optimize the tests for all contracts in a single commodity.
     *
     *  @param  commodity   The commodity to perform the test optimization on.
     */
    public void optimize(Commodity commodity) {
        Iterator it = commodity.getContracts();
        while (it.hasNext()) {
            optimize((Contract)it.next());
        }
    }

    /**
     *  Optimize the tests for a single contract.
     *
     *  @param  contract    The contract to perform the test optimization on.
     */
    public void optimize(Contract contract) {
        Iterator it = testManager.getTestNameIterator();
        while (it.hasNext()) {
            String testName = (String)it.next();
System.out.println("Optimizing " + contract.getName() + " - " + testName);
            try {
//                TechnicalTestInterface test = testManager.getTestInstance(testName, contract);
//                test.optimizeTest();
                testManager.optimizeTest(testName, contract);
            } catch (Exception e) {
                System.err.println("Error optimizing test " + testName + " for " + contract.getName());
                e.printStackTrace();
            }
        }
System.out.println("Optimization complete for " + contract.getName());
//        reprocessRecommendations();
    }

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
    public static void main(String args[]) {
        new TestOptimizer();
    }
}