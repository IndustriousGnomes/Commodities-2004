/*  x Properly Documented
 */
package commodities;

import java.awt.*;
import javax.swing.*;

import commodities.contract.*;
import commodities.dataaccess.dataloader.*;
import commodities.graph.*;
import commodities.menu.*;
import commodities.order.*;
import commodities.price.*;
import commodities.util.*;
import commodities.window.*;

/**
 *  The CommodityAnalyzer is the driving class for the
 *  Commodity Analyzer application.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class CommodityAnalyzer {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Properties file identifier for the properties manager. */
    private static final String DATA_ACCESS_PROPERTIES_FILE = "dataaccess";
    /** Property for deciding if data should be retrieved from the internet. */
    private static final String RETRIEVE_DATA = "retrieve_data";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Application user interface frame. */
    private JFrame applicationJFrame;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a CommodityAnalyzer.
     */
    private CommodityAnalyzer() {
        setup();
    }

    /**
     *  Setup the user interface to the Commodity Analyzer application.
    */
    private void setup() {
//*     Load commodities as thread - store instance here but make listeners for everyone else
//*     Load contracts as threads (or part of commodities)

        if ("no".equalsIgnoreCase(PropertyManager.instance().getProperties(DATA_ACCESS_PROPERTIES_FILE).getProperty(RETRIEVE_DATA, "yes"))) {
            System.out.println("Data retrieval aborted for test purposes");
        } else {
            DailyDataLoaderFactory.instance().retrievePrices();
        }

        applicationJFrame = CommodityAnalyzerJFrame.instance();
        PricingTabbedPane.instance().loadPages();

        ((CommodityAnalyzerJFrame)applicationJFrame).setOrderPanel(new ContractPanel(AccountMenu.selectAccount())); // can be last setup action

System.out.println("Return from loading pages");
    }

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
    public static void main(String args[]) {
        Thread myMainThread = Thread.currentThread();   // Grab the current (main) thread
        myMainThread.setPriority(8);                    // Set main thread priority to 8
        new CommodityAnalyzer();
    }
}