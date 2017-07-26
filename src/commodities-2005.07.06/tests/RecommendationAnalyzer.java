/* _ Review Javadocs */
package commodities.tests;

import java.util.*;

import commodities.contract.*;
import commodities.tests.technical.*;

import com.util.ThreadPool;

/**
 *  RecommendationAnalyzer analyzes recommendations for contracts.
 *
 *  @author     J.R. Titko
 *  @version    1.00
 *  @update     2004.11.28
 */

public class RecommendationAnalyzer extends Thread {
//    private boolean check = false;
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Singleton instance of TestManager */
    private static TestManager testManager = TestManager.instance();

    /** The ThreadPool to use with processing recomendations. */
    private static ThreadPool threadPool = new ThreadPool(9);

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The contract to get the recommendation for */
    private Contract contract;
    /** The date to recommend for */
    private java.util.Date recommendDate = null;
    /** The tests for the recommendation as a collection of TestIdentifiers. */
    private Collection tests;

    /** The tests by class available from the TestManager. */
    private static Map testsByClass;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */


/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a recommendation analyzer for a contract.
     *
     *  @param  contract    the contract to make recommendations for
     */
    public RecommendationAnalyzer(Contract contract) {
        this(contract, contract.getCurrentDate());
    }

    /**
     *  Create a RecommendationAnalyzer for the given contract and date.
     *
     *  @param  contract    the contract to make recommendations for
     *  @param  date        the date to make the recommendations for
     */
    public RecommendationAnalyzer(Contract contract, java.util.Date date) {
        this.contract   = contract;
        recommendDate   = date;
        testsByClass    = testManager.getTestClasses();

        setPriority(7);
        threadPool.runTask(this);
    }

    /**
     *  Create a recommendation analyzer for a contract for the given date and using
     *  the supplied set of tests.  This constructor can only be called from static
     *  methods within this class as it will not start the thread and will require
     *  the calling method to call the getRecommendation(tests) method directly.
     *
     *  @param  contract    the contract to make recommendations for
     *  @param  date        the date to make the recommendations for
     *  @param  tests       the collection of TestIdentifiers to use in getting a recommendation
     */
    private RecommendationAnalyzer(Contract contract,
                                   java.util.Date date,
                                   Collection tests) {
        this.contract   = contract;
        recommendDate   = date;
        testsByClass    = testManager.getTestClasses();
        this.tests      = tests;
    }

/* *************************************** */
/* *** Thread METHODS                  *** */
/* *************************************** */
    /**
     *  Main processing loop for the thread.
     */
    public void run() {
        if (!contract.isActualPriceDate(recommendDate)) {
            Recommendation recommendation = new Recommendation(recommendDate, "Consolidated", Recommendation.NO_ACTION);
            contract.setShortTermRecommendation(recommendation);
            contract.setMediumTermRecommendation(recommendation);
            contract.setLongTermRecommendation(recommendation);
            return;
        }

//check = true;
        tests = contract.getBestShortTermTests();
        if (tests != null) {
            contract.setShortTermRecommendation(getRecommendation());
        } else {
            contract.setShortTermRecommendation(null);
        }
//check = false;

        tests = contract.getBestMediumTermTests();
        if (tests != null) {
            contract.setMediumTermRecommendation(getRecommendation());
        } else {
            contract.setMediumTermRecommendation(null);
        }

        tests = contract.getBestLongTermTests();
        if (tests != null) {
            contract.setLongTermRecommendation(getRecommendation());
        } else {
            contract.setLongTermRecommendation(null);
        }
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Determine the buy/sell recommendation based on the
     *  tests.
     *
     *  @param  tests   the tests to make a recommendation on.
     *  @return         the recommendation
     */
    private Recommendation getRecommendation() {
        boolean firstIteration = true;
        int origOrientation = 0; // neg is buy, pos is sell

        // Set all tests to no action
        Map recommendations = new HashMap();
        TestIdentifier testId = null;
        Iterator it = tests.iterator();
        while (it.hasNext()) {
            testId = (TestIdentifier)it.next();
            recommendations.put(testId, new Recommendation(recommendDate, (String)testsByClass.get(testId.getTestClass()), Recommendation.NO_ACTION));
        }

        // possition prices to the point where a recommendation is wanted
        // based on the date the recommendation is for.
        ListIterator dateIterator = contract.getPriceDates(Contract.DESCENDING);
        while (dateIterator.hasPrevious()) {
            java.util.Date date = (java.util.Date)dateIterator.previous();
            if (date.equals(recommendDate)) {
                dateIterator.next();
                break;
            }
        }

loop:   while (dateIterator.hasPrevious()) {
            java.util.Date date = (java.util.Date)dateIterator.previous();
            Map dailyRecommendations = getDailyRecommendations(date);

            it = dailyRecommendations.keySet().iterator();
            while(it.hasNext()) {
                testId = (TestIdentifier)it.next();
                if (recommendations.containsKey(testId)) {
                    Recommendation recommend = (Recommendation)dailyRecommendations.get(testId);
                    int oldBuySell = ((Recommendation)recommendations.get(testId)).getBuySellRecommendation();
                    int todayBuySell = recommend.getBuySellRecommendation();

                    if (oldBuySell == todayBuySell) {
                        // do nothing
                    } else if (oldBuySell == Recommendation.NO_ACTION) {
                        recommendations.put(testId, recommend);
                    } else if (todayBuySell == Recommendation.NO_ACTION) {
                        // do nothing
                    } else if ((oldBuySell == Recommendation.SELL) &&
                               (todayBuySell == Recommendation.SETTLE_SELL)) {
                        // do nothing
                    } else if ((oldBuySell == Recommendation.SETTLE_SELL) &&
                               (todayBuySell == Recommendation.SELL)) {
                        recommendations.put(testId, recommend);
                    } else if ((oldBuySell == Recommendation.BUY) &&
                               (todayBuySell == Recommendation.SETTLE_BUY)) {
                        // do nothing
                    } else if ((oldBuySell == Recommendation.SETTLE_BUY) &&
                               (todayBuySell == Recommendation.BUY)) {
                        recommendations.put(testId, recommend);
                    } else {
                        // If test is no longer valid due to contradictory signals,
                        // remove the test from the list
                        recommendations.remove(testId);
//                        recommendations.put(testId, new Recommendation(recommend.getTestName(), Recommendation.NO_ACTION));
//                        break loop;
                    }
                }
            }

            int buyRec = 0;
            int settleBuyRec = 0;
            int settleSellRec = 0;
            int sellRec = 0;
            it = recommendations.values().iterator();
            while (it.hasNext()) {
                int rec = ((Recommendation)it.next()).getBuySellRecommendation();
                switch (rec) {
                    case Recommendation.BUY:
                        buyRec++;
                        break;
                    case Recommendation.SETTLE_BUY:
                        settleBuyRec++;
                        break;
                    case Recommendation.SETTLE_SELL:
                        settleSellRec++;
                        break;
                    case Recommendation.SELL:
                        sellRec++;
                        break;
                }
            }

            if (firstIteration) {
                if ((buyRec + settleBuyRec > 0) && (sellRec + settleSellRec > 0)) {
                    return new Recommendation(recommendDate, "Consolidated", Recommendation.NO_ACTION);
                } else {
                    origOrientation = (sellRec + settleSellRec) - (buyRec + settleBuyRec);
                    firstIteration = false;
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " Original Orientation = " + origOrientation);
                }
            } else if ((((sellRec - buyRec) < 0) && (origOrientation > 0)) ||
                       (((sellRec - buyRec) > 0) && (origOrientation < 0))) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " reversal 1 - No Action");
                return new Recommendation(recommendDate, "Consolidated", Recommendation.NO_ACTION);
            }

            if ((buyRec + settleBuyRec + settleSellRec + sellRec) == 0) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " reversal 2 - No Action");
                return new Recommendation(recommendDate, "Consolidated", Recommendation.NO_ACTION);
            }

            if (recommendations.size() < (tests.size() / 2)) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " reversal 3 - No Action");
                return new Recommendation(recommendDate, "Consolidated", Recommendation.NO_ACTION);
            }

            if (buyRec > (tests.size() / 2)) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " CONFIRM BUY - buyRec = " + buyRec + "  Test size = " + tests.size() + " / 2 = " + (tests.size() / 2));
                return new Recommendation(recommendDate, "Consolidated", Recommendation.BUY);
            }

            if ((buyRec + settleBuyRec) > (tests.size() / 2)) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " CONFIRM SETTLE BUY - buyRec = " + buyRec + " + settleBuyRec = " + settleBuyRec + "   Test size = " + tests.size() + " / 2 = " + (tests.size() / 2));
                return new Recommendation(recommendDate, "Consolidated", Recommendation.SETTLE_BUY);
            }

            if (sellRec > (tests.size() / 2)) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " CONFIRM SELL - buyRec = " + sellRec + "  Test size = " + tests.size() + " / 2 = " + (tests.size() / 2));
                return new Recommendation(recommendDate, "Consolidated", Recommendation.SELL);
            }

            if ((sellRec + settleSellRec) > (tests.size() / 2)) {
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " CONFIRM SETTLE SELL - buyRec = " + sellRec + " + settleSellRec = " + settleSellRec + "  Test size = " + tests.size() + " / 2 = " + (tests.size() / 2));
                return new Recommendation(recommendDate, "Consolidated", Recommendation.SETTLE_SELL);
            }
        }
//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getRecommendation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " NO CONSENSUS");
        return new Recommendation(recommendDate, "Consolidated", Recommendation.NO_ACTION);
    }

    /**
     *  Get the recommendations from all of the tests based for the requested date.
     *
     *  @return             the consolidated recommendation
     */
    private Map getDailyRecommendations(java.util.Date date) {
        Map recommendations = new HashMap();
        Iterator it = tests.iterator();
        while (it.hasNext()) {
            TestIdentifier testId = (TestIdentifier)it.next();
            String testName = (String)testsByClass.get(testId.getTestClass());

            try {
//                TechnicalTestInterface test = testManager.getTestInstance(testName, contract);
//                Recommendation recommendation = test.getRecommendation(testId.getTestId(), date);
                Recommendation recommendation = testManager.getTestRecommendation(testName, contract, testId.getTestId(), date);


//if (check && "BO".equals(contract.getSymbol()) && "Jul 05".equals(contract.getMonthFormatted()))
//System.out.println("RecommendationAnalyzer.getDailyRecommenation - contract = " + contract.getSymbol() + " " + contract.getMonthFormatted() + " recommendation = " + recommendation.toString());
                if (recommendation != null) {
                    recommendations.put(testId, recommendation);
                }
            } catch (Exception e) {
e.printStackTrace();
                System.err.println("Error recommending test " + testName + " for " + contract.getSymbol() + " " + contract.getMonthFormatted());
System.exit(1);
            }
        }
        return recommendations;
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Request a Recommendation using the supplied set of tests.
     *
     *  @param  contract    the contract to make recommendations for
     *  @param  date        the date to make the recommendations for
     *  @param  tests       the collection of TestIdentifiers to use in getting a recommendation
     */
    public static Recommendation requestRecommendation(Contract contract,
                                                       java.util.Date date,
                                                       Collection tests) {
        return (new RecommendationAnalyzer(contract, date, tests)).getRecommendation();
    }

/* *************************************** */
/* *** MAIN METHOD                     *** */
/* *************************************** */
}