/*  x Properly Documented
 */
/**
 *  The TestIdentifier class represents the information for a given test id.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.tests;

import prototype.commodities.*; // debug only

public class TestIdentifier {
    private static boolean DEBUG = false;

    private String  testClass;
    private int     testId;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a TestIdentifier.
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  month   The trading month of this contract
     */
    public TestIdentifier(String testClass, int testId) {
        this.testClass = testClass;
        this.testId = testId;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    public String getTestClass()    { return testClass; }
    public int    getTestId()       { return testId; }
}