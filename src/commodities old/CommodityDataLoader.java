/*  x Properly Documented
 */
package commodities;

import java.awt.*;
import javax.swing.*;

import commodities.contract.*;
import commodities.dataaccess.dataloader.*;


/**
 *  The CommodityDataLoader is the driving class loading commodity
 *  data without running the full application.  This driver is designed to
 *  be executed in a scheduled fashion.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class CommodityDataLoader {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a CommodityAnalyzer.
     */
    private CommodityDataLoader() {
        setup();
    }

    /**
     *  Setup the user interface to the Commodity Analyzer application.
     */
    private void setup() {
        DailyDataLoaderFactory.instance().retrievePrices(true);

System.out.println("Return from loading data");
        System.exit(0);     // Never do this normally but system must shut down all threads.
    }

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
    public static void main(String args[]) {
        new CommodityDataLoader();
    }
}