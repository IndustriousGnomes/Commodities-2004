/*  x Properly Documented
 */
package commodities.tests;

/**
 *  The TestIdentifier class contains the Test Id and the class used for
 *  the test so that the TestManager can track which tests are best for
 *  a given commodity.  This information comes from the test_xref table.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class TestIdentifier {
/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The class of the test. */
    private String  testClass;
    /** The id of the test. */
    private int     testId;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a TestIdentifier.
     *
     *  @param  testClass   the class of the test
     *  @param  testId      the id of the test
     */
    public TestIdentifier(String testClass, int testId) {
        this.testClass = testClass;
        this.testId = testId;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the test class.
     *  @return     the test class
     */
    public String getTestClass() {
        return testClass;
    }

    /**
     *  Get the test id.
     *  @return     the test id
     */
    public int getTestId() {
        return testId;
    }
}