/*  x Properly Documented
 */
package commodities.tests.technical;

import commodities.tests.*;

/**
 *  The TechnicalTestInterface is a standard interface for all of the
 *  technical tests.
 *
 *  All classes that extend this interface must contain the following 2 constructors:
 *      Test() - Used for retrieving the name of the test ONLY!
 *      Test(commodities.dataaccess.Contract) - For all other accesses.
 *  This is required since the getName() method must be included in every class but
 *  is a static name that must also be used to populate the Test menu through reflection.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public interface TechnicalTestInterface {
    /** Short Term days */
    public static final int SHORT_TERM  = 15;
    /** Medium Term days */
    public static final int MEDIUM_TERM = 45;
    /** Long Term days */
    public static final int LONG_TERM   = 120;
    /** Minumum success ratio. 40% allows good tests that might have had some minor mishaps but still profitable. */
    public static final double MIN_SUCCESS_RATIO   = 0.40;
    /** The minimum number of contracts that must be traded to consider the test useful. */
    public static final int MIN_NUMBER_OF_TRADES = 3;


    /**
     *  Retrieve the name of the test.  This name will be the english style name
     *  that will appear in the menu and as a header for the graph.
     */
    public String getName();

    /**
     *  This method will depict the results of the test.  Although this will
     *  typically be in the form of a line graph, the graph can appear in
     *  any appropriate format.
     *
     *  The location to draw the graph in will depend on each test.  If the
     *  graph should be overlayed on the price graph, the test will have to
     *  register with the price graph.  If it should be put in its own space,
     *  then the statistical graph manager will have to be used.
     */
    public void graphResults();

    /**
     *  Stop graphing this test.
     */
    public void stopGraphResults();

    /**
     *  This method loads the data used for each test that was
     *  determined to be an optimal test for a contract.
     */
    public void loadData();

    /**
     *  Perform an optimization of the test to determine the most profitable
     *  combination(s) of parameters to use with the test.
     */
    public void optimizeTest();

    /**
     *  Retrieve the recommendation resulting from the test for the
     *  specified date.
     *
     *  @param  testId  the id of the test in the test table
     *  @param  date    the date to get the recommendation for
     *  @return         the recommendation
     */
    public Recommendation getRecommendation(int testId, java.util.Date date);

    /**
     *  Make a recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a recommendation based on this test
     */
    public Recommendation makeRecommendation(java.util.Date date, StatsAbstract stats);

    /**
     *  Make a buy/sell recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a buy/sell recommendation based on this test
     */
    public int makeBuySellRecommendation(java.util.Date date, StatsAbstract stats);

    /**
     *  Make a stop loss recommendation based on the stats data.
     *
     *  @param  date        the date the recommendation is for
     *  @param  statsObject the statistics as an object
     *  @return             a stop loss recommendation based on this test
     */
    public double makeStopLossRecommendation(java.util.Date date, StatsAbstract stats);
}