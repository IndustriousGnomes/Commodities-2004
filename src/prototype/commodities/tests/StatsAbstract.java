/*  x Properly Documented
 */
package prototype.commodities.tests;

//import java.awt.*;
import java.util.*;
//import javax.swing.*;

//import prototype.commodities.*;
//import prototype.commodities.util.*;
//import prototype.commodities.graph.*;
//import prototype.commodities.simulator.*;
//import prototype.commodities.dataaccess.*;

/**
 *  The StatsAbstract contains methods used by all statistics classes within
 *  technical tests.
 *
 *  @author J.R. Titko
 */
public abstract class StatsAbstract implements Comparable {

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The statistical data for this test in a Map format. */
    protected TreeMap dataMap = new TreeMap();
    /** The keys for the statistical data for this test in a LinkedList format. */
    protected LinkedList keyList = null;
    /** The statistical data for this test in a LinkedList format. */
    protected LinkedList dataList = null;
    /** The statistical data for this test in a Map format. */
    protected TreeMap recommendationMap = new TreeMap();

    /** Profitability of traded contracts */
    private double  profit = 0;
    /** Number of profitable trades */
    private int     profitCount = 0;
    /** Number of unprofitable trades */
    private int     lossCount = 0;
    /** Total value of profitable trades */
    private double  profitTotal = 0;
    /** Total value of unprofitable trades */
    private double  lossTotal = 0;
    /** Largest profitable trade */
    private double  largestProfit = 0;
    /** Largest unprofitable trade */
    private double  largestLoss = 0;

/* *************************************** */
/* *** GET AND SET METHODS             *** */
/* *************************************** */
    /**
     *  Get the statistics name
     *  @return     the statistics name
     */
    protected abstract String getName();

    /**
     *  Get the interval used to determine short/medium/long term.
     *  @return     the interval time
     */
    public abstract int getInterval();

    /**
     *  Get the statistics data
     *  @return     the statistics data
     */
    protected LinkedList getData() {
        if (dataList == null) {
            dataList = new LinkedList();
            Iterator it = dataMap.values().iterator();
            while (it.hasNext()) {
                dataList.add(it.next());
            }
        }
        return dataList;
    }
    /**
     *  Get the statistics for a key
     *  @return     the data value for the keyed entry
     */
    protected Object getData(java.util.Date key) {
        return dataMap.get(key);
    }

    /**
     *  Get the keys for the statistics
     *  @return     the data value for the keyed entry
     */
    protected LinkedList getKeys() {
        if (keyList == null) {
            keyList = new LinkedList();
            Iterator it = dataMap.keySet().iterator();
            while (it.hasNext()) {
                keyList.add(it.next());
            }
        }
        return keyList;
    }

    /**
     *  Get the profitability of traded contracts
     *  @return     the profit amount
     */
    public double getProfit() { return profit; }

    /**
     *  Get the number of profitable trades
     *  @return     the number of profitable trades
     */
    public int getProfitCount() { return profitCount; }

    /**
     *  Get the number of unprofitable trades
     *  @return     the number of unprofitable trades
     */
    public int getLossCount() { return lossCount; }

    /**
     *  Get the total value of profitable trades
     *  @return     the value of profitable trades
     */
    public double getProfitTotal() { return profitTotal; }

    /**
     *  Get the total value of unprofitable trades
     *  @return     the value of unprofitable trades
     */
    public double getLossTotal() { return lossTotal; }

    /**
     *  Get the largest profitable trade
     *  @return     the value of profitable trade
     */
    public double getLargestProfit() { return largestProfit; }

    /**
     *  Get the largest unprofitable trade
     *  @return     the value of unprofitable trade
     */
    public double getLargestLoss() { return largestLoss; }

    /**
     *  Get the total value of unprofitable trades
     *  @return     the value of unprofitable trades
     */
    public double getCountRatio() {
        return (double)profitCount / (double)(profitCount + lossCount);
    }

    /**
     *  Add profit/loss amount of a trade
     *  @param  p   profit/loss amount to add
     */
    public void addProfit(double p) {
        profit += p;
        if (p > 0) {
            if (p > largestProfit) {
                largestProfit = p;
            }
            profitCount++;
            profitTotal += p;
        } else {
            if (p < largestLoss) {
                largestLoss = p;
            }
            lossCount++;
            lossTotal += p;
        }
    }
}