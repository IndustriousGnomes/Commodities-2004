/*  x Properly Documented
 */
package prototype.commodities.tests;

import prototype.commodities.*; // Debug only

/**
 *  Recommendation contains a test's recommendation on what action
 *  to take with a commodity (buy/sell).
 *
 *  @author J.R. Titko
 */
public class Recommendation implements Comparable {
    private static boolean DEBUG = false;

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
    /** Name of the test that this recommendation is for */
    private String testName;
    /** The recommendation of this test */
    private int recommendation;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a recommendation.
     *
     *  @param  testName        the name of the test to use in displays
     *  @param  recommendation  the recommendation for this test
     */
    public Recommendation(String testName, int recommendation) {
        this.testName = testName;
        this.recommendation = recommendation;
Debug.println(DEBUG, this, getTestName() + "  " + getRecommendationLabel());
    }

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the test name.
     *  @return     name of the test that this recommendation is for
     */
    public String getTestName() { return testName; }

    /**
     *  Get the recommendation.
     *  @return     the recommendation
     */
    public int getRecommendation() { return recommendation; }

    /**
     *  Get the label of the recommendation.
     *
     *  @return         the label used for the recommendation
     */
    public String getRecommendationLabel() {
        return labels[recommendation + 2];
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
        return testName.compareTo(((Recommendation)o2).getTestName());
    }

}