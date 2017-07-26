/* _ Review Javadocs */
package commodities.simulator;

import commodities.commodity.*;
import commodities.contract.*;
import commodities.tests.*;
import commodities.tests.technical.*;
import commodities.util.*;

import java.util.*;

/**
 *  The TestSimulator simulates the daily actions of the commodity
 *  program user for a specific test.  It makes decisions on buying and
 *  selling contracts based on the analysis done within the commodities program.
 *
 *  An exception to the daily actions is that all orders are automatically
 *  filled on the next day's opening value unless a stop loss was enacted.
 *  A stop loss will be sold at the stoploss price if that day's prices span the
 *  stop loss amount.  Otherwise, the open price of the next day will be the stop loss
 *  out price.  Stop loss prices are set by the test.  Tests that do not supply stop
 *  loss prices will not have contracts sold based on stop loss amounts.
 *  At the end of the simulator run, all open orders are resolved on the
 *  closing price of the last day.
 *
 *  TEST OPTIMIZATION
 *  -----------------
 *  A technical test will start the simulator for its test only.  The simulator
 *  will start from the first day of data available for the commodity that the
 *  test is being optimized for.  The simulator will advance the date one day
 *  at a time (skipping weekends) until the current date.
 *
 *  @author     J.R. Titko
 *  @since      1.00
 *  @version    1.00
 */

public class TestSimulator {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Map of open orders */
    private Map openOrders = new HashMap();
    /** The last processing day of the simulation. */
    java.util.Date lastDate;
    /** The last date of processing for this test. */
    private java.util.Date wrapupDate;

    /** The contract to run the test over. */
    private Contract contract;
    /** The test to be processed. */
    private TechnicalTestInterface test;
    /** The statistical data to process for the test. */
    private StatsAbstract stats;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  The Order class holds a single order.
     */
    private class Order {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** A buy order(true) or a sell order(false) */
        private boolean isBuy;
        /** Date the order was placed */
        private java.util.Date date;
        /** Actual price the order was entered. */
        private double price;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  Create an order.  Orders can be buy or sell, have the date
         *  the order was filled, and the price it was filled at.
         */
        public Order(boolean isBuy, java.util.Date date, double price) {
            this.isBuy = isBuy;
            this.date  = date;
            this.price = price;
        }

    /* *************************************** */
    /* *** GET & SET METHODS               *** */
    /* *************************************** */
        /**
         *  Returns if this is a buy order, otherwise it is a sell order.
         *  @return     true if the order is a buy order
         */
        public boolean isBuy() { return isBuy; }
        /**
         *  Gets the date.
         *  @return     the date
         */
        public java.util.Date getDate() { return date; }
        /**
         *  Gets the price.
         *  @return     the price
         */
        public double getPrice() { return price; }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  This constructor will perform a simmulated run using only
     *  the one technical test requested.  The length of the simulated
     *  run will be over the range of all available data.
     *
     *  @param  contract    The contract to run the test over.
     *  @param  test        The test to be processed.
     *  @param  stats       The statistical data to process for the test.
     */
    public TestSimulator(Contract contract,
                         TechnicalTestInterface test,
                         StatsAbstract stats) {
        this(contract, test, stats, ((CommodityCalendar)CommodityCalendar.getInstance()).getTime());
    }

    /**
     *  This constructor will perform a simmulated run using only
     *  the one technical test requested.  The length of the simulated
     *  run will be over the range from the start of the data to the given
     *  last date.
     *
     *  @param  contract    The contract to run the test over.
     *  @param  test        The test to be processed.
     *  @param  stats       The statistical data to process for the test.
     */
    public TestSimulator(Contract contract,
                        TechnicalTestInterface test,
                        StatsAbstract stats,
                        java.util.Date lastDate) {
        this.contract   = contract;
        this.test       = test;
        this.stats      = stats;
        this.lastDate   = lastDate;

        dailyProcessing();
        wrapUpProcessing();     // last trading day
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */


/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  This method simulates the users processes that goes on in a daily
     *  trading situation using only a single technical test.
     *
     *  @param  <param>             <param description>
     *  @throws <exception type>    <exception description>
     *  @return                     <description of return value>
     */
    private void dailyProcessing() {
        ListIterator dateIterator = contract.getPriceDates(Contract.ASCENDING);
        while (dateIterator.hasNext()) {
            java.util.Date date = (java.util.Date)dateIterator.next();
            if (date.after(lastDate)) {
                return;
            } else {
                wrapupDate = date;
            }

            int buySellRec = test.makeBuySellRecommendation(date, stats);
            if (buySellRec != Recommendation.NO_ACTION) {
                java.util.Date purchaseDate = null;
                double purchasePrice = 0;
                if (dateIterator.hasNext()) {
                    purchaseDate = (java.util.Date)dateIterator.next();
                    purchasePrice = contract.getPrices(purchaseDate).getOpen();
                    dateIterator.previous();
                } else {
                    purchaseDate = date;
                    purchasePrice = contract.getPrices(purchaseDate).getClose();
                }

                if (purchasePrice == 0.0) {
                    purchasePrice = ((contract.getPrices(purchaseDate).getHigh() + contract.getPrices(purchaseDate).getLow()) / 2.0);
                }

                processRecommendation(contract, buySellRec, purchaseDate, purchasePrice);
            }
    //      processStopLosses();
    //      setNewStopLosses();
        }
    }

    /**
     *  This method wraps up all open orders at the end of the simulation.
     */
    private void wrapUpProcessing() {
        Iterator it = openOrders.keySet().iterator();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();
            LinkedList orders = (LinkedList)openOrders.get(contract);
            if (orders.size() > 0) {
                processRecommendation(contract,
                                      (((Order)orders.getFirst()).isBuy())?Recommendation.SETTLE_SELL:Recommendation.SETTLE_BUY,
                                      wrapupDate,
                                      contract.getPrices(wrapupDate).getClose());
            }
        }
    }

    /**
     *  Process a recommended buy or sell order by checking existing orders for
     *  an offsetting order and if none is found, adding it to the list.  If an
     *  offsetting order is found, the profit/loss is calculated and the offsetting
     *  order is removed from the list.
     *
     *  @param  contract        The contract to be bought/sold.
     *  @param  buySellRec      The buy/sell recommendation.
     *  @param  purchaseDate    The date the contract was purchased.
     *  @param  purchasePrice   The price of the contract was purchased for.
     */
    private void processRecommendation(Contract contract,
                                       int buySellRec,
                                       java.util.Date purchaseDate,
                                       double purchasePrice) {
        if (!openOrders.containsKey(contract)) {
            openOrders.put(contract, new LinkedList());
        }
        LinkedList orders = (LinkedList)openOrders.get(contract);

        Iterator it = orders.iterator();
        while (it.hasNext()) {
            Order order = (Order)it.next();
            if (order.isBuy() && ((buySellRec == Recommendation.SELL) || (buySellRec == Recommendation.SETTLE_SELL))) {
                calculateProfit(contract, order.getPrice(), purchasePrice);
                it.remove();
            } else if (!order.isBuy() && ((buySellRec == Recommendation.BUY) || (buySellRec == Recommendation.SETTLE_BUY))) {
                calculateProfit(contract, purchasePrice, order.getPrice());
                it.remove();
            }
        }

        if (buySellRec == Recommendation.BUY) {
            orders.add(new Order(true, purchaseDate, purchasePrice));
        } else if (buySellRec == Recommendation.SELL) {
            orders.add(new Order(false, purchaseDate, purchasePrice));
        }
    }

    /**
     *  Calculate the profit/loss of a trade and record the results
     *  appropriately.
     *
     *  @param  contract    The contract that has been bought and sold.
     *  @param  buyValue    The buy price of the contract.
     *  @param  sellValue   The sell price of the contract.
     */
    private void calculateProfit(Contract contract, double buyValue, double sellValue) {
        double tickPrice = Commodity.bySymbol(contract.getSymbol()).getTickPrice();
        double tickSize  = Commodity.bySymbol(contract.getSymbol()).getTickSize();
        double p = (sellValue - buyValue) / tickSize * tickPrice;
        stats.addProfit(p);
    }
}