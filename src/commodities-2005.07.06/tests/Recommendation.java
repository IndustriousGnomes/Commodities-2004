/*  x Properly Documented
 */
package commodities.tests;

/**
 *  Recommendation contains a test's recommendation on what action
 *  to take with a commodity (buy/sell).
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class Recommendation implements Comparable {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Value for recommending a sell action */
    public static final int SELL       = 2;
    /** Value for recommending a sell action if a contract is currently bought */
    public static final int SETTLE_SELL   = 1;
    /** Value for recommending no action to be taken */
    public static final int NO_ACTION  = 0;
    /** Value for recommending a buy action if a contract is currently sold */
    public static final int SETTLE_BUY    = -1;
    /** Value for recommending a buy action */
    public static final int BUY        = -2;

    /** Label for recommending a sell action */
    public static final String  SELL_LABEL          = "S";
    /** Label for recommending a sell action if a contract is currently bought */
    public static final String  SETTLE_SELL_LABEL   = "SS";
    /** Label for recommending no action to be taken */
    public static final String  NO_ACTION_LABEL     = "-";
    /** Label for recommending a buy action if a contract is currently sold */
    public static final String  SETTLE_BUY_LABEL    = "SB";
    /** Label for recommending a buy action */
    public static final String  BUY_LABEL           = "B";

    /** Recommendation label names */
    private static final String labels[] = {BUY_LABEL, SETTLE_BUY_LABEL, NO_ACTION_LABEL, SETTLE_SELL_LABEL, SELL_LABEL};


/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Date the recommendation is for. */
    private java.util.Date date;
    /** Name of the test that this recommendation is for */
    private String testName;
    /** The buy/sell recommendation of this test */
    private int buySell;
    /** The recommended stoploss. */
    private double stoploss;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a recommendation for the given test.
     *
     *  @param  date        the date the recommendation is for
     *  @param  testName    the name of the test to use in displays
     *  @param  buySell     the buy/sell recommendation for this test
     */
    public Recommendation(java.util.Date date, String testName, int buySell) {
        this(date, testName, buySell, 0.0);
    }

    /**
     *  Create a recommendation for the given test with a stoploss amount.
     *  The stoploss amount assumes a preferred loss amount of $0.  The
     *  actual stoploss amount should be adjusted accordingly.  Even if the
     *  recommendation is no action, the stoploss should still indicate an
     *  appropriate exit price.
     *
     *  @param  date        the date the recommendation is for
     *  @param  testName        the name of the test to use in displays
     *  @param  buySell         the buy/sell recommendation for this test
     *  @param  stoploss        the stoploss recommendation
     */
    public Recommendation(java.util.Date date, String testName, int buySell, double stoploss) {
        this.date = date;
        this.testName = testName;
        this.buySell = buySell;
        this.stoploss = stoploss;
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the date of the recommendation.
     *  @return     the date
     */
    public java.util.Date getDate() {
        return date;
    }

    /**
     *  Get the test name.
     *  @return     name of the test that this recommendation is for
     */
    public String getTestName() {
        return testName;
    }

    /**
     *  Get the recommendation.
     *  @return     the recommendation
     */
    public int getBuySellRecommendation() {
        return buySell;
    }

    /**
     *  Get the label of the buy/sell recommendation.
     *
     *  @return         the label used for the recommendation
     */
    public String getBuySellLabel() {
        return labels[buySell + 2];
    }

    /**
     *  Get the recommended stoploss price.  This amount takes into consideration a
     *  preferred stoploss cost of $0.  The final stoploss amount should be adjusted
     *  accordingly.
     *
     *  @return     the stoploss amount
     */
    public double getStoploss() {
        return stoploss;
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
System.out.println("Recommendation.compareTo was executed.  This needs updated");
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
System.exit(1);
}
        return testName.compareTo(((Recommendation)o2).getTestName());
    }

/* *************************************** */
/* *** Object METHODS              *** */
/* *************************************** */
    /**
     *  Return a recommendation as a string.
     *  @return The recommendation information
     */
    public String toString() {
        return testName + "  " + date + " - " + getBuySellLabel();
    }
}
