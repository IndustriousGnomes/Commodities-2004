/*  x Properly Documented
 */
package commodities;

import java.awt.*;

/**
 *  The StrategySimulator is the driving class for the
 *  Commodity Analyzer Strategy Simulator application.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class StrategySimulator {
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a StrategySimulator.
     */
    private StrategySimulator() {
        setupGUI();
    }

    /**
     *  Setup the user interface to the Commodity Analyzer application.
     */
    private void setupGUI() {

    }


/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
    /**
     *  The main class for starting the Commodity Analyzer application.
     */
    public static void main(String args[]) {
        new StrategySimulator();
    }
}