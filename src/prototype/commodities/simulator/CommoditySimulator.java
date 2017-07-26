/*  _ Properly Documented
 */
package prototype.commodities.simulator;

import java.io.*;
import java.sql.*;
import java.util.*;

import prototype.commodities.*; // debug only
import prototype.commodities.util.*;
import prototype.commodities.tests.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.dataaccess.database.*;

/**
 *  The CommoditySimulator simulates the daily actions of the commodity
 *  program user.  It makes decisions on buying and selling contracts
 *  based on the analysis done within the commodities program.
 *
 *  An exception to a normal daily run, is that it keeps a log of
 *  its activities in the Simulator_Orders DB2 table instead of putting
 *  entries in the Orders table.
 *
 *  Another exception is that all orders are automatically filled on the
 *  next day's opening value.  At the end of the simulator run, all
 *  open orders are resolved on the closing price of the last day.
 *
 *  The current date will be set to the date six months ago from today's
 *  date.  The optimization of all analyitical tests for all commodities
 *  will be performed.  Then the simulator will advance the date one day
 *  at a time (skipping weekends) over the next six month period to see
 *  the results of the trading. During that six month period, additional
 *  optimizations of the technical tests will occur monthly.  This simulates
 *  the need to reoptimize the tests periodically.  The tests will
 *  be reoptimized at the end of six months as the to reset the optimizations
 *  that the simulator has modified.
 *
 *  @author J.R. Titko
 */
public class CommoditySimulator {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Designates of a simulatored run is being performed */
    public static boolean SIMULATED_RUN = false;

    private static final int OPTIMIZATION_PERIOD_MONTHS = 1;

    /** The name of the report file. */
//    private static final String REPORT_FILE = "D:\\JavaProjects\\Commodities\\SimulatorReports\\SimulatorReport";

    /** The number of months for trading. */
    private static final int TRADING_MONTHS = 5;
    /** The amount, in dollars, of the starting account.  This must be a positive amount. */
//    private static final double STARTING_ACCOUNT = 50000.0;
    /** Whether stoplosses should be used in this run */

//    private static final boolean STOPLOSSES = true;
    /** The amount, in dollars, of the stoploss.  This must be a negative amount. */
//    private static final double STOPLOSS_AMOUNT = -1000.0;



/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** Linked list of contracts that are part of the simulator run */
    private LinkedList contracts = new LinkedList();

    /** Map of short term contract orders that have not been fulfilled. */
//    private Map shortOpenOrders = new HashMap();
    /** Map of medium term contract orders that have not been fulfilled. */
//    private Map mediumOpenOrders = new HashMap();
    /** Map of long term contract orders that have not been fulfilled. */
//    private Map longOpenOrders = new HashMap();
    /** Map of all term contract orders that have not been fulfilled. */
//    private Map totalOpenOrders = new HashMap();

    /** Map of short term contract orders that have been fulfilled. */
//    private Map shortFilledOrders = new HashMap();
    /** Map of medium term contract orders that have been fulfilled. */
//    private Map mediumFilledOrders = new HashMap();
    /** Map of long term contract orders that have been fulfilled. */
//    private Map longFilledOrders = new HashMap();
    /** Map of all term contract orders that have been fulfilled. */
//    private Map totalFilledOrders = new HashMap();

    /** Instance of TestManager */
    TestManager testManager = TestManager.instance();

    /** Daily Calendar */
    public CommodityCalendar dailyCalendar = null;
    /** Calendar of a period for reoptimization. */
    private CommodityCalendar periodCalendar = null;
    /** Calendar with the final date of the simulation. */
    private CommodityCalendar endCalendar = null;

    /** Output file for the report */
//    PrintWriter reportOut = null;

    /** Current account balance */
//    private double accountBalance = STARTING_ACCOUNT;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  Order holds an order.
     *
     *  @author J.R. Titko
     */
//    private class Order {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** Is it a buy order */
//        private boolean isBuy;
        /** Date the order was placed */
//        private java.util.Date date;
        /** Actual price */
//        private double price = 0;
        /** Stoploss price */
//        private double stoploss = 0;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
/*
        public Order(boolean isBuy, java.util.Date date, double price) {
            this.isBuy = isBuy;
            this.date  = date;
            this.price = price;
        }

        public Order(boolean isBuy, java.util.Date date, double price, double stoploss) {
            this(isBuy, date, price);
            this.stoploss = stoploss;
        }

    /* *************************************** */
    /* *** GET AND SET METHODS             *** */
    /* *************************************** */
        /**
         *  Returns if this is a buy order, otherwise it is a sell order.
         *  @return     true if the order is a buy order
         */
//        public boolean isBuy() { return isBuy; }
        /**
         *  Gets the date.
         *  @return     the date
         */
//        public java.util.Date getDate() { return date; }
        /**
         *  Gets the price.
         *  @return     the price
         */
//        public double getPrice() { return price; }
        /**
         *  Gets the stoploss price.
         *  @return     the stoploss price
         */
//        public double getStoploss() { return stoploss; }

        /**
         *  Sets the stoploss price.
         *  @param     the stoploss price
         */
/*
        public void setStoploss() {
            this.stoploss = stoploss;
        }
    }
*/
    /**
     *  OrderLine holds an buy and sell order
     *
     *  @author J.R. Titko
     */
//    private class OrderLine {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The buy order */
//        private Order buyOrder;
        /** The sell order */
//        private Order sellOrder;

    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
/*        public OrderLine(Order buyOrder, Order sellOrder) {
            this.buyOrder   = buyOrder;
            this.sellOrder  = sellOrder;
        }

    /* *************************************** */
    /* *** GET AND SET METHODS             *** */
    /* *************************************** */
        /**
         *  Returns the buy order.
         *  @return     the buy order
         */
//        public Order getBuyOrder() { return buyOrder; }

        /**
         *  Returns the sell order.
         *  @return     the sell order
         */
//        public Order getSellOrder() { return sellOrder; }

        /**
         *  Updates the buy order.
         *  @return     the buy order
         */
/*        public void setBuyOrder(Order buyOrder) {
            this.buyOrder   = buyOrder;
        }

        /**
         *  Updates the sell order.
         *  @return     the sell order
         */
/*        public void setSellOrder(Order sellOrder) {
            this.sellOrder  = sellOrder;
        }
    }
*/
/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  This constructor should be called when the simulator is to
     *  be run in full functionallity mode.  It will perform a
     *  simmulated run using all technical tests.  The length
     *  of the simulated run will be over the most current 6 months.
     */
    public CommoditySimulator() {
        SIMULATED_RUN = true;
/*
        try {
            reportOut = new PrintWriter(new BufferedWriter(new FileWriter(REPORT_FILE + " " + FormatDate.formatDateProp((Calendar.getInstance()).getTime(), FormatDate.FORMAT_SIMULATOR_DATE) + ".txt")));
        } catch (IOException e) {
            e.printStackTrace();
            try { reportOut.close(); } catch (Exception ex) {}
            reportOut = null;
        }
*/
//        printlnFile("Processing:");
        System.out.println("Processing:");
        // create an entry in the contractMaps for each available contract
        Iterator it = Commodity.getNameMap().values().iterator();
        while (it.hasNext()) {
            Commodity commodity = (Commodity)it.next();
            Iterator it2 = (commodity).getContracts();
            while (it2.hasNext()) {
                Contract contract = (Contract)it2.next();
//                printlnFile(contract.getName());
                System.out.println(contract.getName());
                contracts.add(contract);
/*
                LinkedList list = new LinkedList();
                shortOpenOrders.put(contract, list);
                list = new LinkedList();
                mediumOpenOrders.put(contract, list);
                list = new LinkedList();
                longOpenOrders.put(contract, list);
                list = new LinkedList();
                totalOpenOrders.put(contract, list);

                list = new LinkedList();
                shortFilledOrders.put(contract, list);
                list = new LinkedList();
                mediumFilledOrders.put(contract, list);
                list = new LinkedList();
                longFilledOrders.put(contract, list);
                list = new LinkedList();
                totalFilledOrders.put(contract, list);
*/
            }
        }
//        printlnFile("");

//        printlnFile("Simulator start time: " + (Calendar.getInstance()).getTime());
        System.out.println("Simulator start time: " + (Calendar.getInstance()).getTime());

        dailyCalendar = (CommodityCalendar)CommodityCalendar.getInstance(); // create a calendar
        dailyCalendar.clearSimulatorDate();                                 // clear the simulator date

        endCalendar = (CommodityCalendar)CommodityCalendar.getInstance();   // get the end date of the simulation before the simulator date is set
        endCalendar.clearTime();

        dailyCalendar.add(Calendar.MONTH, -TRADING_MONTHS);                 // TRADING_MONTHS months ago as starting date for application
        dailyCalendar.setNextDayOfWeek(Calendar.FRIDAY);                    // start the following friday
        dailyCalendar.clearTime();
        dailyCalendar.setSimulatorDate();                                   // To limit test level simulators

        TradingStrategy1 strategy1 = new TradingStrategy1(contracts);
        TradingStrategy2 strategy2 = new TradingStrategy2(contracts);
        TradingStrategy3 strategy3 = new TradingStrategy3(contracts);
//        TradingStrategy4 strategy4 = new TradingStrategy4(contracts);

        do {
//            printlnFile("");
//            printlnFile("Simulator Optimizing tests for " + dailyCalendar.getTime());
            System.out.println("Simulator Optimizing tests for " + dailyCalendar.getTime());
            TestManager.instance().optimizeTests();

            periodCalendar = (CommodityCalendar)CommodityCalendar.getInstance();
            periodCalendar.add(Calendar.MONTH, OPTIMIZATION_PERIOD_MONTHS);                          // add 1 month
            periodCalendar.addWeekDays(-1);                                 // get previous day
            periodCalendar.setNextDayOfWeek(Calendar.THURSDAY);             // get the following friday
            periodCalendar.clearTime();

            while (!dailyCalendar.after(periodCalendar) && !dailyCalendar.after(endCalendar)) {
System.out.print("~");

                strategy1.dailyProcessing(dailyCalendar);
                strategy2.dailyProcessing(dailyCalendar);
                strategy3.dailyProcessing(dailyCalendar);
//                strategy4.dailyProcessing(dailyCalendar);

                if (dailyCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
//                    printlnFile("");
//                    printlnFile("Reporting for period ending " + dailyCalendar.getTime());
                    strategy1.reportResults(dailyCalendar);
                    strategy2.reportResults(dailyCalendar);
                    strategy3.reportResults(dailyCalendar);
//                    strategy4.reportResults(dailyCalendar);
                }

                dailyCalendar.addWeekDays(1);
                dailyCalendar.clearTime();
                dailyCalendar.setSimulatorDate();                           // To limit test level simulators
            }
System.out.println();
        } while (!dailyCalendar.after(endCalendar));

        strategy1.wrapUpProcessing();
        strategy2.wrapUpProcessing();
        strategy3.wrapUpProcessing();
//        strategy4.wrapUpProcessing();
/*
        printlnFile("");
        printlnFile("============================================================");
        printlnFile("============================================================");
        printlnFile("Final Reporting for period ending " + dailyCalendar.getTime());
*/
        strategy1.reportResults(dailyCalendar);
        strategy2.reportResults(dailyCalendar);
        strategy3.reportResults(dailyCalendar);
//        strategy4.reportResults(dailyCalendar);
        strategy1.recordResults();
        strategy2.recordResults();
        strategy3.recordResults();
//        strategy4.recordResults();

        strategy1.closeReport();
        strategy2.closeReport();
        strategy3.closeReport();
//        strategy4.closeReport();

//        printlnFile("Simulator finish time: " + (Calendar.getInstance()).getTime());
        System.out.println("Simulator finish time: " + (Calendar.getInstance()).getTime());
/*
        if (reportOut != null) {
            try { reportOut.close(); } catch (Exception e) {}
        }
*/
        dailyCalendar.clearSimulatorDate();

//        printlnFile("Final Simulator Optimizing tests for " + dailyCalendar.getTime());
        System.out.println("Final Simulator Optimizing tests for " + dailyCalendar.getTime());
        TestManager.instance().optimizeTests();


    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

    /**
     *  This method simulates the users processes that goes on in a daily
     *  trading situation.
     */
/*    private void dailyProcessing() {
        java.util.Date today    = dailyCalendar.getTime();
        java.util.Date purchaseDate = null;

        Iterator it = contracts.iterator();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();
            ListIterator dateIterator = contract.getPriceDateListIterator(false);
            while (dateIterator.hasNext()) {
                java.util.Date d = (java.util.Date)dateIterator.next();
                if (d.equals(today)) {
                    break;
                } else if (d.after(today)) {
                    continue;                   // no prices for today - no action
                }
            }
            if (dateIterator.hasNext()) {
                purchaseDate = (java.util.Date)dateIterator.next();
            } else {
                continue;                       // no prices for tomorrow and after - no action - all will be filled in wrapup
            }

            double purchasePrice = contract.getPrices(purchaseDate).getOpen();
            if (purchasePrice == 0.0) {
                purchasePrice = ((contract.getPrices(purchaseDate).getHigh() + contract.getPrices(purchaseDate).getLow()) / 2.0);
            }

            processRecommendation(contract,
                                  (testManager.getRecommendation(contract, TechnicalTestInterface.SHORT_TERM, today)).getRecommendation(),
                                  purchaseDate,
                                  purchasePrice,
                                  shortOpenOrders,
                                  shortFilledOrders,
                                  false);
            processRecommendation(contract,
                                  (testManager.getRecommendation(contract, TechnicalTestInterface.MEDIUM_TERM, today)).getRecommendation(),
                                  purchaseDate,
                                  purchasePrice,
                                  mediumOpenOrders,
                                  mediumFilledOrders,
                                  false);
            processRecommendation(contract,
                                  (testManager.getRecommendation(contract, TechnicalTestInterface.LONG_TERM, today)).getRecommendation(),
                                  purchaseDate,
                                  purchasePrice,
                                  longOpenOrders,
                                  longFilledOrders,
                                  false);

            if (STOPLOSSES) {
                processStopLosses(contract,
                                  today,
                                  shortOpenOrders,
                                  shortFilledOrders);

                processStopLosses(contract,
                                  today,
                                  mediumOpenOrders,
                                  mediumFilledOrders);
                processStopLosses(contract,
                                  today,
                                  longOpenOrders,
                                  longFilledOrders);
            }
    //      processStopLosses();
    //      setNewStopLosses();
        }
    }

    /**
     *  This method wraps up all open orders at the end of the simulation.
     *  This is for a single test.
     */
/*    private void wrapUpProcessing() {
        Iterator it = contracts.iterator();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();

            java.util.Date purchaseDate = contract.getLastDate();
            double purchasePrice = contract.getPrices().getClose();
            if (purchasePrice == 0.0) {
                purchasePrice = ((contract.getPrices().getHigh() + contract.getPrices().getLow()) / 2.0);
            }

            LinkedList orders = (LinkedList)shortOpenOrders.get(contract);
            if (orders.size() > 0) {
                processRecommendation(contract,
                                      (((Order)orders.getFirst()).isBuy())?Recommendation.SELL:Recommendation.BUY,
                                      purchaseDate,
                                      purchasePrice,
                                      shortOpenOrders,
                                      shortFilledOrders,
                                      true);
            }

            orders = (LinkedList)mediumOpenOrders.get(contract);
            if (orders.size() > 0) {
                processRecommendation(contract,
                                      (((Order)orders.getFirst()).isBuy())?Recommendation.SELL:Recommendation.BUY,
                                      purchaseDate,
                                      purchasePrice,
                                      mediumOpenOrders,
                                      mediumFilledOrders,
                                      true);
            }

            orders = (LinkedList)longOpenOrders.get(contract);
            if (orders.size() > 0) {
                processRecommendation(contract,
                                      (((Order)orders.getFirst()).isBuy())?Recommendation.SELL:Recommendation.BUY,
                                      purchaseDate,
                                      purchasePrice,
                                      longOpenOrders,
                                      longFilledOrders,
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
/*    private void processRecommendation(Contract         contract,
                                       int              recommendation,
                                       java.util.Date   purchaseDate,
                                       double           purchasePrice,
                                       Map              openOrders,
                                       Map              filledOrders,
                                       boolean          wrapup) {

        if (recommendation == Recommendation.NO_ACTION) {
            return;
        }

        LinkedList orders = (LinkedList)openOrders.get(contract);
        Iterator it = orders.iterator();
        while (it.hasNext()) {
            Order order = (Order)it.next();
            if (order.isBuy() && ((recommendation == Recommendation.SELL) || (recommendation == Recommendation.SETTLE_SELL))) {
                Order fillOrder = new Order(false, purchaseDate, purchasePrice);
                LinkedList fOrders = (LinkedList)filledOrders.get(contract);
                fOrders.add(new OrderLine(order, fillOrder));
                processOverallOrders(contract, fillOrder, wrapup);
                it.remove();
            } else if (!order.isBuy() && ((recommendation == Recommendation.BUY) || (recommendation == Recommendation.SETTLE_BUY))) {
                Order fillOrder = new Order(true, purchaseDate, purchasePrice);
                LinkedList fOrders = (LinkedList)filledOrders.get(contract);
                fOrders.add(new OrderLine(fillOrder, order));
                processOverallOrders(contract, fillOrder, wrapup);
                it.remove();
            }
        }

        if (!wrapup) {
            if ((orders.size() == 0) ||
                ((orders.size() > 0) && (calculateProfit(contract, orders, purchasePrice) > Math.min((orders.size() * (STOPLOSS_AMOUNT / 4)), STOPLOSS_AMOUNT)))) {
                if (recommendation == Recommendation.BUY) {
                    Order newOrder = new Order(true, purchaseDate, purchasePrice, (purchasePrice - calculateStoplossAmount(contract)));
                    orders.add(newOrder);
                    processOverallOrders(contract, newOrder, wrapup);
                } else if (recommendation == Recommendation.SELL) {
                    Order newOrder = new Order(false, purchaseDate, purchasePrice, (purchasePrice + calculateStoplossAmount(contract)));
                    orders.add(newOrder);
                    processOverallOrders(contract, newOrder, wrapup);
                }
            }
        }
    }

    private void processOverallOrders(Contract contract, Order newOrder, boolean wrapup) {
        LinkedList orders = (LinkedList)totalOpenOrders.get(contract);
        Iterator it = null;

        if (!wrapup) {
            it = orders.iterator();
            while (it.hasNext()) {
                Order order = (Order)it.next();
                if ((order.isBuy() && !newOrder.isBuy()) || (!order.isBuy() && newOrder.isBuy())) {
                    if (order.getDate().equals(newOrder.getDate())) {
                        it.remove();
                        return;
                    }
                }
            }
            LinkedList fOrders = (LinkedList)totalFilledOrders.get(contract);
            it = fOrders.iterator();
            while (it.hasNext()) {
                OrderLine orderLine = (OrderLine)it.next();
                if (!newOrder.isBuy()) {
                    if (orderLine.getBuyOrder().getDate().equals(newOrder.getDate())) {
                        newOrder = orderLine.getSellOrder();
                        it.remove();
                    }
                } else if (newOrder.isBuy()) {
                    if (orderLine.getSellOrder().getDate().equals(newOrder.getDate())) {
                        newOrder = orderLine.getBuyOrder();
                        it.remove();
                    }
                }
            }
        }

        it = orders.iterator();
        if (it.hasNext()) {
            Order order = (Order)it.next();
            if (order.isBuy() && !newOrder.isBuy()) {
                LinkedList fOrders = (LinkedList)totalFilledOrders.get(contract);
                fOrders.add(new OrderLine(order, newOrder));
                it.remove();
            } else if (!order.isBuy() && newOrder.isBuy()) {
                LinkedList fOrders = (LinkedList)totalFilledOrders.get(contract);
                fOrders.add(new OrderLine(newOrder, order));
                it.remove();
            } else {
                orders.add(newOrder);
            }
        } else {
            orders.add(newOrder);
        }
    }

    /**
     *  Determine if a stoploss order needs to be fulfilled.
     */
/*    private void processStopLosses(Contract         contract,
                                   java.util.Date   today,
                                   Map              openOrders,
                                   Map              filledOrders) {

        LinkedList orders = (LinkedList)openOrders.get(contract);
        Iterator it = orders.iterator();
        if (!it.hasNext()) {
            return;             // no orders to process
        }

        double lowPrice = contract.getPrices(today).getLow();
        double highPrice = contract.getPrices(today).getHigh();

        double profit = 0;
        double stoplossSum = 0;
        while (it.hasNext()) {
            Order order = (Order)it.next();
            if (order.isBuy()) {
                profit += calculateProfit(contract, order.getPrice(), lowPrice);
                stoplossSum += order.getStoploss();
            } else if (!order.isBuy()) {
                profit += calculateProfit(contract, highPrice, order.getPrice());
                stoplossSum += order.getStoploss();
            }
        }

        if ((orders.size() * STOPLOSS_AMOUNT) > profit) {
            double stoplossCalc = stoplossSum / orders.size();
            double purchasePrice = 0;
            if ((lowPrice < stoplossCalc) && (stoplossCalc < highPrice)) {
                purchasePrice = stoplossCalc;
            } else {
                if ((((Order)orders.getFirst()).isBuy()) && (stoplossCalc > highPrice)) {
                    purchasePrice = highPrice;
                } else if (!((((Order)orders.getFirst()).isBuy()) && (stoplossCalc < lowPrice))) {
                    purchasePrice = lowPrice;
                }
            }

            processRecommendation(contract,
                                  (((Order)orders.getFirst()).isBuy())?Recommendation.SELL:Recommendation.BUY,
                                  today,
                                  purchasePrice,
                                  openOrders,
                                  filledOrders,
                                  true);
        }
    }

    /**
     *  Calculate the profit/loss of a trade and record the results
     *  appropriately.
     */
/*    private double calculateProfit(Contract contract, double buyValue, double sellValue) {
        double tickPrice = Commodity.bySymbol(contract.getSymbol()).getTickPrice();
        double tickSize  = Commodity.bySymbol(contract.getSymbol()).getTickSize();
        return (sellValue - buyValue) / tickSize * tickPrice;
    }

    /**
     *  Calculate the profit/loss of all orders.
     */
/*    private double calculateProfit(Contract contract, LinkedList orders, double purchasePrice) {
        double  profit = 0;

        Iterator it = orders.iterator();
        while (it.hasNext()) {
            Order order = (Order)it.next();
            if (purchasePrice != 0.0) {
                if (order.isBuy()) {
                    profit += calculateProfit(contract, order.getPrice(), purchasePrice);
                } else {
                    profit += calculateProfit(contract, purchasePrice, order.getPrice());
                }
            }
        }
        return profit;
    }

    /**
     *  Calculate the amount a contract has to move in price to produce the stoploss.
     */
/*    private double calculateStoplossAmount(Contract contract) {
        double tickPrice = Commodity.bySymbol(contract.getSymbol()).getTickPrice();
        double tickSize  = Commodity.bySymbol(contract.getSymbol()).getTickSize();
        return STOPLOSS_AMOUNT / tickPrice * tickSize;
    }


    /**
     *  Report on each commodity's results.
     */
/*    private void reportResults() {
        double  overallProfit = 0;
        double  shortProfit = 0;
        double  mediumProfit = 0;
        double  longProfit = 0;
        double  totalProfit = 0;

        double  overallOpenProfit = 0;
        double  shortOpenProfit = 0;
        double  mediumOpenProfit = 0;
        double  longOpenProfit = 0;
        double  totalOpenProfit = 0;

        double  filledProfit = 0;
        int     filledOrderCount = 0;
        LinkedList orderLines = null;
        double  openProfit = 0;
        int     openOrderCount = 0;
        LinkedList orders = null;
        Iterator it2 = null;

        java.util.Date purchaseDate  = dailyCalendar.getTime();
        dailyCalendar.addWeekDays(-1);
        java.util.Date prevPurchDate = dailyCalendar.getTime();
        dailyCalendar.addWeekDays(1);

        Iterator it = contracts.iterator();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();

            double purchasePrice = 0.0;
            Prices prices = contract.getPrices(purchaseDate);
            if (prices != null) {
                purchasePrice = prices.getClose();
                if (purchasePrice == 0.0) {
System.out.println("Alternate purchasePrice #1 - use date: " + purchaseDate);
                    purchasePrice = ((contract.getPrices(purchaseDate).getHigh() + contract.getPrices(purchaseDate).getLow()) / 2.0);
                }
            } else {
                prices = contract.getPrices(prevPurchDate);
                purchasePrice = prices.getClose();
                if (purchasePrice == 0.0) {
System.out.println("Alternate purchasePrice #1 - use date: " + purchaseDate);
                    purchasePrice = ((contract.getPrices(purchaseDate).getHigh() + contract.getPrices(purchaseDate).getLow()) / 2.0);
                }
            }


    // Short
            filledProfit = 0;
            orderLines = (LinkedList)shortFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                filledProfit += calculateProfit(contract, orderLine.getBuyOrder().getPrice(), orderLine.getSellOrder().getPrice());
            }

            orders = (LinkedList)shortOpenOrders.get(contract);
            openProfit = calculateProfit(contract, orders, purchasePrice);

            overallProfit += filledProfit;
            shortProfit += filledProfit;
            overallOpenProfit += openProfit;
            shortOpenProfit += openProfit;
            printlnFile(contract.getName() + " short  - filled profit= " + ((int)(filledProfit * 100.0) / 100.0) + "  trades=" + orderLines.size()
                                           + "      open profit= " + ((int)(openProfit * 100.0) / 100.0) + "  trades=" + orders.size()
                                           + "      total profit=" + ((int)((filledProfit + openProfit) * 100.0) / 100.0) + " trades=" + (filledOrderCount + openOrderCount));

    // Medium
            filledProfit = 0;
            orderLines = (LinkedList)mediumFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                filledProfit += calculateProfit(contract, orderLine.getBuyOrder().getPrice(), orderLine.getSellOrder().getPrice());
            }

            orders = (LinkedList)mediumOpenOrders.get(contract);
            openProfit = calculateProfit(contract, orders, purchasePrice);

            overallProfit += filledProfit;
            mediumProfit += filledProfit;
            overallOpenProfit += openProfit;
            mediumOpenProfit += openProfit;
            printlnFile(contract.getName() + " middle - filled profit= " + ((int)(filledProfit * 100.0) / 100.0) + "  trades=" + orderLines.size()
                                           + "      open profit= " + ((int)(openProfit * 100.0) / 100.0) + "  trades=" + orders.size()
                                           + "      total profit=" + ((int)((filledProfit + openProfit) * 100.0) / 100.0) + " trades=" + (filledOrderCount + openOrderCount));



    // Long
            filledProfit = 0;
            orderLines = (LinkedList)longFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                filledProfit += calculateProfit(contract, orderLine.getBuyOrder().getPrice(), orderLine.getSellOrder().getPrice());
            }

            orders = (LinkedList)longOpenOrders.get(contract);
            openProfit = calculateProfit(contract, orders, purchasePrice);

            overallProfit += filledProfit;
            longProfit += filledProfit;
            overallOpenProfit += openProfit;
            longOpenProfit += openProfit;
            printlnFile(contract.getName() + " long   - filled profit= " + ((int)(filledProfit * 100.0) / 100.0) + "  trades=" + orderLines.size()
                                           + "      open profit= " + ((int)(openProfit * 100.0) / 100.0) + "  trades=" + orders.size()
                                           + "      total profit=" + ((int)((filledProfit + openProfit) * 100.0) / 100.0) + " trades=" + (filledOrderCount + openOrderCount));


    // Total
            filledProfit = 0;
            orderLines = (LinkedList)totalFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                filledProfit += calculateProfit(contract, orderLine.getBuyOrder().getPrice(), orderLine.getSellOrder().getPrice());
            }

            orders = (LinkedList)totalOpenOrders.get(contract);
            openProfit = calculateProfit(contract, orders, purchasePrice);

            totalProfit += filledProfit;
            totalOpenProfit += openProfit;
            printlnFile(contract.getName() + " total  - filled profit= " + ((int)(filledProfit * 100.0) / 100.0) + "  trades=" + orderLines.size()
                                           + "      open profit= " + ((int)(openProfit * 100.0) / 100.0) + "  trades=" + orders.size()
                                           + "      total profit=" + ((int)((filledProfit + openProfit) * 100.0) / 100.0) + " trades=" + (filledOrderCount + openOrderCount));

            printlnFile("");
        }


        printlnFile("--------------------------------------------------------");
        printlnFile("FILLED - Overall = " + ((int)(overallProfit * 100.0) / 100.0) + "  Short = " + ((int)(shortProfit * 100.0) / 100.0) + "  Medium = " + ((int)(mediumProfit * 100.0) / 100.0)  + "  Long = " + ((int)(longProfit * 100.0) / 100.0) + "  [Total = " + ((int)(totalProfit * 100.0) / 100.0) + "]");
        printlnFile("OPEN   - Overall = " + ((int)(overallOpenProfit * 100.0) / 100.0) + "  Short = " + ((int)(shortOpenProfit * 100.0) / 100.0) + "  Medium = " + ((int)(mediumOpenProfit * 100.0) / 100.0)  + "  Long = " + ((int)(longOpenProfit * 100.0) / 100.0) + "  [Total = " + ((int)(totalOpenProfit * 100.0) / 100.0) + "]");
        printlnFile("TOTAL  - Overall = " + ((int)((overallProfit + overallOpenProfit) * 100.0) / 100.0) + "  Short = " + ((int)((shortProfit + shortOpenProfit) * 100.0) / 100.0) + "  Medium = " + ((int)((mediumProfit + mediumOpenProfit) * 100.0) / 100.0)  + "  Long = " + ((int)((longProfit + longOpenProfit) * 100.0) / 100.0) + "  [Total = " + ((int)((totalProfit + totalOpenProfit) * 100.0) / 100.0) + "]");

    }

    public void printlnFile(String output) {
        System.out.println(output);
        if (reportOut != null) {
            reportOut.println(output);
        }
    }

    /**
     *  Record the transactions that were traded in this run.
     */
/*    private void recordResults() {
        clearSimulatorOrders();

        Iterator it = contracts.iterator();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();

            LinkedList orderLines = (LinkedList)shortFilledOrders.get(contract);
            Iterator it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                updateSimulatedOrders(contract, TechnicalTestInterface.SHORT_TERM, orderLine);
            }

            orderLines = (LinkedList)mediumFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                updateSimulatedOrders(contract, TechnicalTestInterface.MEDIUM_TERM, orderLine);
            }

            orderLines = (LinkedList)longFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                updateSimulatedOrders(contract, TechnicalTestInterface.LONG_TERM, orderLine);
            }

            orderLines = (LinkedList)totalFilledOrders.get(contract);
            it2 = orderLines.iterator();
            while (it2.hasNext()) {
                OrderLine orderLine = (OrderLine)it2.next();
                updateSimulatedOrders(contract, 0, orderLine);
            }
        }
    }

    /**
     *  Deletes all of the simulator orders.
     */
/*    protected void clearSimulatorOrders() {
Debug.println(DEBUG, this, "clearSimulatorOrders() start");
        DBConnectionPool dbcp = null;

        try {
            dbcp = DBConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }

        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("delete from Simulator_Orders ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            stmt.execute(sqlTxt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
Debug.println(DEBUG, this, "clearSimulatorOrders() end");
    }

    /**
     *  Write the transactions to the order table.
     */
/*    private void updateSimulatedOrders(Contract contract, int term, OrderLine orderLine) {
Debug.println(DEBUG, this, "updateSimulatedOrders() start");
        double profit = calculateProfit(contract, orderLine.getBuyOrder().getPrice(), orderLine.getSellOrder().getPrice());

        DBConnectionPool dbcp = null;
        try {
            dbcp = DBConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }
        Connection connect = null;

        try {
            connect = dbcp.retrieveConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("insert into Simulator_Orders " +
                      "       (Commodity_Name, Commodity_Symbol, Contract_Month, Term, Buy_Date, Buy_Price, Sell_Date, Sell_Price, Profit, Original_Order_Date, Original_Fill_Date) " +
                      "values ('" + (Commodity.bySymbol(contract.getSymbol())).getName() + "', " +
                      "        '" + contract.getSymbol() + "', " +
                      "        '" + contract.getMonthFormatted() + "', " +
                      "        " + term + ", " +
                      "        '" + FormatDate.formatDateProp(orderLine.getBuyOrder().getDate(), FormatDate.FORMAT_SQL_DATE) + "', " +
                      "        " + orderLine.getBuyOrder().getPrice() + ", " +
                      "        '" + FormatDate.formatDateProp(orderLine.getSellOrder().getDate(), FormatDate.FORMAT_SQL_DATE) + "', " +
                      "        " + orderLine.getSellOrder().getPrice() + ", " +
                      "        " + profit + ", " +
                      "        '" + FormatDate.formatDateProp(new java.util.Date(Math.min(orderLine.getBuyOrder().getDate().getTime(), orderLine.getSellOrder().getDate().getTime())), FormatDate.FORMAT_SQL_DATE) + "', " +
                      "        '" + FormatDate.formatDateProp(new java.util.Date(Math.max(orderLine.getBuyOrder().getDate().getTime(), orderLine.getSellOrder().getDate().getTime())), FormatDate.FORMAT_SQL_DATE) + "' " +
                      "       )");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            stmt.execute(sqlTxt);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }

Debug.println(DEBUG, this, "updateSimulatedOrders() end");
    }



    protected void finalize() throws Throwable {
        if (reportOut != null) {
            try { reportOut.close(); } catch (Exception e) {}
        }
    }
*/


    public static void main(String args[]) {
        new CommoditySimulator();
    }
}