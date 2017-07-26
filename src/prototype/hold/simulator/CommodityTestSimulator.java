/*  _ Properly Documented
 */
package prototype.commodities.simulator;

import java.awt.*;
import java.util.*;
//import javax.swing.*;

//import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.tests.*;
import prototype.commodities.dataaccess.*;

/**
 *  The CommodityTestSimulator simulates the daily actions of the commodity
 *  program user for a specific test.  It makes decisions on buying and
 *  selling contracts based on the analysis done within the commodities program.
 *
 *  An exception to the daily actions is that all orders are automatically
 *  filled on the next day's opening value.  At the end of the simulator run, all
 *  open orders are resolved on the closing price of the last day.
 *
 *  TEST OPTIMIZATION
 *  -----------------
 *  A technical test will start the simulator for its test only.  The simulator
 *  will start from the first day of data available for the commodity that the
 *  test is being optimized for.  The simulator will advance the date one day
 *  at a time (skipping weekends) until the current date.
 *
 *  @author J.R. Titko
 */
public class CommodityTestSimulator {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Map of open orders */
    private Map openOrders = new HashMap();
    /** Calendar */
    CommodityCalendar calendar = null;
    /** The last date of processing for this test. */
    private java.util.Date wrapupDate;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  Order holds an order.
     *
     *  @author J.R. Titko
     */
    private class Order {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** Is it a buy order */
        private boolean isBuy;
        /** Date the order was placed */
        private java.util.Date date;
        /** Actual price */
        private double price;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        public Order(boolean isBuy, java.util.Date date, double price) {
            this.isBuy = isBuy;
            this.date  = date;
            this.price = price;
        }

    /* *************************************** */
    /* *** GET AND SET METHODS             *** */
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
     *  run will be over from the start of available data to the present.
     */
    public CommodityTestSimulator(Contract contract, TechnicalTestInterface test, StatsAbstract stats) {
        calendar = (CommodityCalendar)CommodityCalendar.getInstance();
        dailyProcessing(contract, test, stats);
        wrapUpProcessing(stats);     // last trading day
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

    /**
     *  This method simulates the users processes that goes on in a daily
     *  trading situation using only a single technical test.
     */
    private void dailyProcessing(Contract contract, TechnicalTestInterface test, StatsAbstract stats) {
        ListIterator dateIterator = contract.getPriceDateListIterator(false);
        while (dateIterator.hasNext()) {
            java.util.Date date = (java.util.Date)dateIterator.next();
            if (date.after(calendar.getTime())) {
                return;
            } else {
                wrapupDate = date;
            }

            int recommendation = test.makeRecommendation(date, stats);
            if (recommendation != Recommendation.NO_ACTION) {
                java.util.Date purchaseDate = null;
                double purchasePrice = 0;
                if (dateIterator.hasNext()) {
                    purchaseDate = (java.util.Date)dateIterator.next();
                    purchasePrice = contract.getPrices(purchaseDate).getOpen();
                    if (purchasePrice == 0.0) {
                        purchasePrice = ((contract.getPrices(purchaseDate).getHigh() + contract.getPrices(purchaseDate).getLow()) / 2.0);
                    }
                    dateIterator.previous();
                } else {
                    purchaseDate = date;
                    purchasePrice = contract.getPrices(purchaseDate).getClose();
                    if (purchasePrice == 0.0) {
                        purchasePrice = ((contract.getPrices(purchaseDate).getHigh() + contract.getPrices(purchaseDate).getLow()) / 2.0);
                    }
                }
                processRecommendation(contract, stats, recommendation, purchaseDate, purchasePrice, false);
            }
    //      processStopLosses();
    //      setNewStopLosses();
        }
    }

    /**
     *  This method wraps up all open orders at the end of the simulation.
     *  This is for a single test.
     */
    private void wrapUpProcessing(StatsAbstract stats) {
        Iterator it = openOrders.keySet().iterator();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();
            LinkedList orders = (LinkedList)openOrders.get(contract);
            if (orders.size() > 0) {
                processRecommendation(contract,
                                      stats,
                                      (((Order)orders.getFirst()).isBuy())?Recommendation.SELL:Recommendation.BUY,
                                      wrapupDate,
                                      contract.getPrices(wrapupDate).getClose(),
                                      true);
            }
        }
    }

    /**
     *  Process a recommended buy or sell order by checking existing orders for
     *  an offsetting order and if none is found, adding it to the list.  If an
     *  offsetting order is found, the profit/loss is calculated and the offsetting
     *  order is removed from the list.
     */
    private void processRecommendation(Contract contract, StatsAbstract stats, int recommendation, java.util.Date purchaseDate, double purchasePrice, boolean wrapup) {

        if (!openOrders.containsKey(contract)) {
            openOrders.put(contract, new LinkedList());
        }
        LinkedList orders = (LinkedList)openOrders.get(contract);

        Iterator it = orders.iterator();
        while (it.hasNext()) {
            Order order = (Order)it.next();
            if (order.isBuy() && ((recommendation == Recommendation.SELL) || (recommendation == Recommendation.SETTLE_SELL))) {
                calculateProfit(contract, stats, order.getPrice(), purchasePrice);
                it.remove();
            } else if (!order.isBuy() && ((recommendation == Recommendation.BUY) || (recommendation == Recommendation.SETTLE_BUY))) {
                calculateProfit(contract, stats, purchasePrice, order.getPrice());
                it.remove();
            }
        }
        if (!wrapup) {
            if (recommendation == Recommendation.BUY) {
                orders.add(new Order(true, purchaseDate, purchasePrice));
            } else if (recommendation == Recommendation.SELL) {
                orders.add(new Order(false, purchaseDate, purchasePrice));
            }
        }
    }

    /**
     *  Calculate the profit/loss of a trade and record the results
     *  appropriately.
     */
    private void calculateProfit(Contract contract, StatsAbstract stats, double buyValue, double sellValue) {
        double tickPrice = Commodity.bySymbol(contract.getSymbol()).getTickPrice();
        double tickSize  = Commodity.bySymbol(contract.getSymbol()).getTickSize();
        double p = (sellValue - buyValue) / tickSize * tickPrice;
        stats.addProfit(p);
    }
}