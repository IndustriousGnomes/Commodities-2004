/*  x Properly Documented
 */
package commodities.contract;

import java.io.*;
import java.util.*;

import commodities.commodity.*;
import commodities.dataaccess.*;
import commodities.event.*;
import commodities.price.*;
import commodities.tests.*;
import commodities.tests.technical.*;
import commodities.util.*;

import com.lang.Trilean;
import com.util.LinkedTreeMap;
import com.util.ThreadPool;

/**
 *  The Contract class represents the information for a given delivery month.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class Contract {

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();

    /** The ThreadPool to use with loading prices. */
    private static ThreadPool priceThreadPool = new ThreadPool(5);

    /** The number of tests to use as the best tests */
    private static final int NUMBER_OF_BEST_TESTS = 5;

    /** Listeners for changing the selected contract. */
    private static Collection selectionListeners = new HashSet();
    /** The contract that is currently selected. */
    private static Contract selectedContract;

    /** Ascending indicator for price retrieval. */
    public static final boolean ASCENDING = false;
    /** Descending indicator for price retrieval. */
    public static final boolean DESCENDING = true;


/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The unique identifier for this contract. */
    private int     id;
    /** The symbol for the commodity. */
    private String  symbol;
    /** The trading month of this contract. */
    private String  month;
    /** The margin of the contract in dollars. */
    private int     margin;
    /** A reference to the commodity for this contract. */
    private Commodity commodity;

    /** A list of daily prices for this commodity */
    private LinkedTreeMap priceList = new LinkedTreeMap();
    /** Load status of the price list.  Pos1 = not loaded, Pos2 = loading, Pos3 = loaded */
    private Trilean priceListStatus = new Trilean();
    /** Current price */
    private Prices currentPrices = null;

    /** The maximum price for the life of the contract. */
    private double  maxPrice = 0.0;
    /** The minimum price for the life of the contract. */
    private double  minPrice = 99999.0;

    /** Collection of the best short term tests. */
    private Collection shortTermBestTests;
    /** Collection of the best medium term tests. */
    private Collection mediumTermBestTests;
    /** Collection of the best long term tests. */
    private Collection longTermBestTests;

    /** Set of the recommendation request dates. */
    private Set recommendationRequestDates = new TreeSet();
    /** Map of the short term recommendations by date. */
    private Map shortTermRecommendations = new TreeMap();
    /** Map of the medium term recommendations by date. */
    private Map mediumTermRecommendations = new TreeMap();
    /** Map of the long term recommendations by date. */
    private Map longTermRecommendations = new TreeMap();

    /** TreeSet of only critical high price points. */
    private Collection highPricePoints;
    /** TreeSet of only critical low price points. */
    private Collection lowPricePoints;
    /** TreeSet of critical high and low price points. */
    private Collection highLowPricePoints;

    /** Listeners for changing prices. */
    private Collection priceListeners = new HashSet();
    /** The last date the price listeners were fired for. */
    private java.util.Date lastPriceListenerFiredDate = null;
    /** Listeners for changing recommendations. */
    private Collection recommendationListeners = new HashSet();

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  LoadThread loads the contract prices
     *  as a thread so the main processing can continue.
     *
     *  @author J.R. Titko
     */
    public class LoadThread implements Runnable {
    /* *************************************** */
    /* *** Thread METHODS                  *** */
    /* *************************************** */
        public void run() {
            Prices prices = null;
            try {
                priceListStatus.pos2();     // mark price list as loading
                Iterator it = dataManager.getPrices(id);
                while (it.hasNext()) {
                    prices = (Prices)it.next();

                    if ((currentPrices == null) || (prices.getDate().compareTo(currentPrices.getDate()) > 0)) {
                        currentPrices = prices;
                    }

                    if (prices.getHigh() > maxPrice) {
                        maxPrice = prices.getHigh();
                    }
                    if (prices.getLow() < minPrice) {
                        minPrice = prices.getLow();
                    }
                    priceList.put(prices.getDate(), prices);
                }
            } catch (NullPointerException npe) {
System.out.println("contract = " + getName());
System.out.println("priceList = " + priceList);
System.out.println("priceList count = " + priceList.size());
System.out.println("prices = " + prices);
System.out.println("prices date = " + prices.getDate());
                throw npe;
            } catch (IOException e) {
                e.printStackTrace();
            }
//System.out.println("firePricesUpdated #1");
            priceListStatus.pos3();     // mark price list as loaded
            firePricesUpdated(getCurrentDate());
            requestRecommendations(getCurrentDate());
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new Contract with a given commodity symbol and contract month.
     *  The contract price will default to the standard margin of the commodity.
     *
     *  @param  symbol  the symbol for the commodity
     *  @param  month   the trading month of this contract
     */
    public Contract(String symbol, String month) {
        this(symbol, month, Commodity.bySymbol(symbol).getMargin());
    }

    /**
     *  Create a new Contract with a given commodity symbol, contract month, and margin.
     *
     *  @param  symbol  the symbol for the commodity
     *  @param  month   the trading month of this contract
     *  @param  margin  the margin in dollars
     */
    public Contract(String symbol, String month, int margin) {
        this(0, symbol, month, margin);
    }

    /**
     *  Create a new contract with a given commodity symbol and contract month and the price the
     *  contract costs.
     *
     *  @param  symbol  the symbol for the commodity
     *  @param  month   the trading month of this contract
     *  @param  margin  the margin in dollars
     */
    public Contract(int id, String symbol, String month, int margin) {
        this.id     = id;
        this.symbol = symbol.trim();
        this.month  = month.trim();
        this.margin  = margin;
        loadPrices();
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    /**
     *  Get the contract's id
     *  @return     the id
     */
    public int getId() { return id; }

    /**
     *  Get the commodity symbol for this contract.
     *  For example 'C' is the symbol for Corn.
     *  @return     the symbol
     */
    public String getSymbol() { return symbol; }

    /**
     *  Get the month of the contract.
     *  Format is in YYYYM.
     *  @return     the month
     */
    public String getMonth() { return month; }

    /**
     *  Get the month of the contract formatted for output.
     *  Format is in 'MMM YY'
     *  @return     the formatted month
     */
    public String getMonthFormatted() {
        return Month.bySymbol(month.substring(4,5)).getAbbrev() + " " + month.substring(2,4);
    }

    /**
     *  Get the month of the contract formatted for comparisons.
     *  Format is in YYYYM
     *  @return      the formatted month as key
     */
//    public String getMonthAsKey() {
//        return month.substring(1).trim() + month.substring(0,1);
//    }

    /**
     *  Get the margin of the contract.
     *  @return     the margin
     */
    public int getMargin() { return margin; }

    /**
     *  Get the maximum price for the life of the contract.
     *  @return     the maximum price
     */
    public synchronized double getContractHighPrice() {
        return maxPrice;
    }

    /**
     *  Get the minimum price for the life of the contract.
     *  @return     the minimum price
     */
    public synchronized double getContractLowPrice() {
        return minPrice;
    }

    /**
     *  Get the name of the contract in the format of
     *  S MMM YY
     *  @return     name of contract
     */
    public String getName() {
        return (getSymbol() + " " + getMonthFormatted());
    }

    /**
     *  Get the most current date of the prices available.
     *  @return     the date
     */
    public synchronized Date getCurrentDate() {
        if ((priceList == null) || (priceList.size() == 0)) {
            return null;
        } else {
            return (Date)priceList.lastKey();
        }
    }

    /**
     *  Get the oldest date of the prices available.
     *  @return     the date
     */
    public synchronized Date getOldestDate() {
if (!priceListStatus.isPos3()) {
    System.out.println("Contract.getOldestDate() - delayed");
}
        while (!priceListStatus.isPos3()) {
            synchronized(this) {
                try {
                    wait(10);
                } catch (InterruptedException e) {
                }
            }
        }
        if ((priceList == null) || (priceList.size() == 0)) {
            return null;
        } else {
            return (Date)priceList.firstKey();
        }
    }

    /**
     *  Get a reference to the commodity that this contract is for.
     *  @return     the commodity
     */
    public Commodity getCommodity() {
        if (commodity == null) {
            commodity = Commodity.bySymbol(symbol);
        }
        return commodity;
    }

    /**
     *  Set the contract id.
     *  @param  id      contract id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *  Set the contract's margin.
     *  @param  margin  the margin in dollars
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Add new prices to this contract.  These prices will be stored.
     *
     *  @param  prices  the new prices
     */
    public synchronized void addPrices(Prices prices) {
        try {
            dataManager.addDailyPrice(this, prices);

            if (prices.getHigh() > maxPrice) {
                maxPrice = prices.getHigh();
            }
            if (prices.getLow() < minPrice) {
                minPrice = prices.getLow();
            }

            priceList.put(prices.getDate(), prices);
//System.out.println("firePricesUpdated #2");
            firePricesUpdated(prices);
            requestRecommendations(prices.getDate());
        } catch (IOException e) {
            // Data was already inserted for this day
        }
    }

    /**
     *  Get the current Prices of this contract.
     *
     *  @return The current Prices of this contract.
     */
    public synchronized Prices getPrices() {
if (!priceListStatus.isPos3()) {
    System.out.println("Contract.getPrices() - delayed");
}
        if (currentPrices == null) {
            try {
                currentPrices = dataManager.getCurrentPrices(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentPrices;
    }

    /**
     *  Get the Prices for the given date of this contract.
     *
     *  @param  date    The date to retrieve the prices for.
     *  @return     the Prices for the given date.
     */
    public synchronized Prices getPrices(java.util.Date date) {
if (!priceListStatus.isPos3()) {
    System.out.println("Contract.getPrices() - delayed");
}
        if (currentPrices == null) {
            getPrices();
        }
        while (!priceListStatus.isPos3()) {
            synchronized(this) {
                try {
                    wait(10);
                } catch (InterruptedException e) {
                }
            }
        }
        if ((priceList == null) || (priceList.size() == 0)) {
            return null;
        } else {
            return (Prices)priceList.get(date);
        }
    }

    /**
     *  Get the closest Prices for the given date of this contract.  If date
     *  has a price, then that date's prices will be returned.  If date
     *  does not have prices, then the previous price closes to that date
     *  is returned.
     *
     *  @param  date    the date to retrieve the prices for
     *  @return     the nearest Prices to the date
     */
    public synchronized Prices getNearestPrices(java.util.Date date) {
        Prices prices = getPrices(date);

        if (prices == null && date != null) {
            int countDown = 7;
            CommodityCalendar calendar = new CommodityCalendar(date);
            do {
                calendar.addWeekDays(-1);
                prices = getPrices(calendar.getTime());
            } while ((prices == null) || (countDown-- > 0));
        }
        return prices;
    }

    /**
     *  Returns if the date is an actual date or a nearest date.
     *
     *  @param  date    the date to check
     *  @return     true if actual date of prices or false if date was nearest date.
     */
    public boolean isActualPriceDate(java.util.Date date) {
if (!priceListStatus.isPos3()) {
    System.out.println("Contract.isActualPriceDate() - delayed");
}
        while (!priceListStatus.isPos3()) {
            synchronized(this) {
                try {
                    wait(10);
                } catch (InterruptedException e) {
                }
            }
        }
        if ((priceList == null) || (priceList.size() == 0)) {
            return false;
        } else {
            return !(priceList.get(date) == null);
        }
    }

    /**
     *  Get all of the Prices for this contract.
     *
     *  @return the Prices of this contract.
     */
    public synchronized Map getPriceList() {
if (!priceListStatus.isPos3()) {
    System.out.println("Contract.getPriceList() - delayed");
}
        while (!priceListStatus.isPos3()) {
            synchronized(this) {
                try {
                    wait(10);
                } catch (InterruptedException e) {
                }
            }
        }
        if (priceList == null) {
            loadPrices();
        }
        return priceList;
    }

    /**
     *  Get all of the Price date keys in a ListIterator format
     *  for forwards and backwards traversal of the list.
     *
     *  A descending (Contract.DESCENDING) list will begin at the most current date and
     *  should be traversed backwards (previous).
     *  An ascending list (Contract.ASCENDING) will begin at the earliest
     *  available date and should be traversed forwards (next).
     *
     *  This ListIterator cannot have objects added or removed from it.
     *
     *  @param  order   The order (ascending or decending) to return the list in.
     *  @return the ListIterator of keys.
     */
    public synchronized ListIterator getPriceDates(boolean order) {
        if (order) {
            return priceList.listIterator(priceList.size());
        } else {
            return priceList.listIterator(0);
        }
    }

    /**
     *  Load the prices from the database and determin the minimum
     *  and maximum prices for the contract.
     */
    private synchronized void loadPrices() {
        priceThreadPool.runTask(new LoadThread());
    }

    /**
     *  Make this contract the selected contract for all listeners.
     */
    public void select() {
        if (selectedContract != this) {
            selectedContract = this;
            fireSelectionUpdated();
        }
    }

    /**
     *  Get the best tests for short term analysis of this contract.
     *
     *  @return     tests for short term analysis
     */
    public Collection getBestShortTermTests() {
        if (shortTermBestTests == null) {
            try {
                shortTermBestTests = dataManager.getBestTestXref(this, TechnicalTestInterface.SHORT_TERM, 0, NUMBER_OF_BEST_TESTS);
            } catch (IOException e) {
                shortTermBestTests = new HashSet();
            }
        }
        return shortTermBestTests;
    }

    /**
     *  Get the best tests for medium term analysis of this contract.
     *
     *  @return     tests for medium term analysis
     */
    public Collection getBestMediumTermTests() {
        if (mediumTermBestTests == null) {
            try {
                mediumTermBestTests = dataManager.getBestTestXref(this, TechnicalTestInterface.MEDIUM_TERM, TechnicalTestInterface.SHORT_TERM, NUMBER_OF_BEST_TESTS);
            } catch (IOException e) {
                mediumTermBestTests = new HashSet();
            }
        }
        return mediumTermBestTests;
    }

    /**
     *  Get the best tests for long term analysis of this contract.
     *
     *  @return     tests for long term analysis
     */
    public Collection getBestLongTermTests() {
        if (longTermBestTests == null) {
            try {
                longTermBestTests = dataManager.getBestTestXref(this, TechnicalTestInterface.LONG_TERM, TechnicalTestInterface.MEDIUM_TERM, NUMBER_OF_BEST_TESTS);
            } catch (IOException e) {
                longTermBestTests = new HashSet();
            }
        }
        return longTermBestTests;
    }

    /**
     *  Clear the best tests analysis of this contract.
     */
    public void clearBestTests() {
        shortTermBestTests  = null;
        mediumTermBestTests = null;
        longTermBestTests   = null;
        recommendationRequestDates.clear();
        shortTermRecommendations.clear();
        mediumTermRecommendations.clear();
        longTermRecommendations.clear();
    }

    /**
     *  Request all recommendations for the current date.
     *
     *  @param  date    the date to get the recommendations for
     */
    public void requestRecommendations() {
        requestRecommendations(getCurrentDate());
    }

    /**
     *  Request all recommendations for the given date.
     *
     *  @param  date    the date to get the recommendations for
     */
    public void requestRecommendations(java.util.Date date) {
        if (date != null) {
            java.util.Date nearestDate = getNearestPrices(date).getDate();

            if (!recommendationRequestDates.contains(nearestDate)) {
                recommendationRequestDates.add(nearestDate);
                fireRecommendationRequested(nearestDate);
                new RecommendationAnalyzer(this, nearestDate);
            } else {
                if (shortTermRecommendations.containsKey(nearestDate)) {
                    fireShortTermRecommendationUpdated(nearestDate);
                }
                if (mediumTermRecommendations.containsKey(nearestDate)) {
                    fireMediumTermRecommendationUpdated(nearestDate);
                }
                if (longTermRecommendations.containsKey(nearestDate)) {
                    fireLongTermRecommendationUpdated(nearestDate);
                }
            }
        }
    }

    /**
     *  Set a short term recommendation.
     *
     *  @param  recommendation  the recommendation
     */
    public void setShortTermRecommendation(Recommendation recommendation) {
        java.util.Date date = recommendation.getDate();
        shortTermRecommendations.put(date, recommendation);
        fireShortTermRecommendationUpdated(date);
    }

    /**
     *  Set a medium term recommendation.
     *
     *  @param  recommendation  the recommendation
     */
    public void setMediumTermRecommendation(Recommendation recommendation) {
        java.util.Date date = recommendation.getDate();
        mediumTermRecommendations.put(recommendation.getDate(), recommendation);
        fireMediumTermRecommendationUpdated(date);
    }

    /**
     *  Set a long term recommendation.
     *
     *  @param  recommendation  the recommendation
     */
    public void setLongTermRecommendation(Recommendation recommendation) {
        java.util.Date date = recommendation.getDate();
        longTermRecommendations.put(recommendation.getDate(), recommendation);
        fireLongTermRecommendationUpdated(date);
    }

    /**
     *  Get the critical high price points for this contract.
     *
     *  @return     collection of the critical high price points
     */
     public Collection getHighPricePoints() {
        if (highPricePoints == null) {
            highPricePoints = CriticalPricePointAnalyzer.instance().getHighPricePoints(this);
        }
        return highPricePoints;
    }

    /**
     *  Get the critical low price points for this contract.
     *
     *  @return     collection of the critical low price points
     */
     public Collection getLowPricePoints() {
        if (lowPricePoints == null) {
            lowPricePoints = CriticalPricePointAnalyzer.instance().getLowPricePoints(this);
        }
        return lowPricePoints;
    }

    /**
     *  Get the critical high and low price points for this contract.
     *
     *  @return     collection of the critical high and low price points
     */
     public Collection getHighLowPricePoints() {
        if (highLowPricePoints == null) {
            highLowPricePoints = CriticalPricePointAnalyzer.instance().getHighLowPricePoints(this);
        }
        return highLowPricePoints;
    }

    /**
     *  Clear the critical price point collections
     */
    public void clearCriticalPricePoints() {
        highPricePoints  = null;
        lowPricePoints = null;
        highLowPricePoints = null;
    }

/* ***---------------------------------*** */
/* *** PriceListener CONTROL METHODS   *** */
/* ***---------------------------------*** */
    /**
     *  Add a listener for price changes.
     *
     *  @param  listener    a PriceListener
     */
    public void addPriceListener(PriceListener listener) {
        if (!priceListeners.contains(listener)) {
            priceListeners.add(listener);
            if (getCurrentDate() != null) {
                firePricesUpdated(listener, getCurrentDate());
            }
        }
    }

    /**
     *  Remove a listener from price changes.
     *
     *  @param  listener    a PriceListener
     */
    public void removePriceListener(PriceListener listener) {
        priceListeners.remove(listener);
    }

    /**
     *  Clear all listeners for price changes.
     */
    public void clearPriceListeners() {
        priceListeners.clear();
    }

    /**
     *  Fire all price information for a date to all PriceListeners.
     *
     *  @param  date  the date of prices to display
     */
    private void firePricesUpdated(java.util.Date date) {
        if (date != null) {
            Prices prices = getNearestPrices(date);
            java.util.Date nearestDate = prices.getDate();
            if (!nearestDate.equals(lastPriceListenerFiredDate)) {
                firePricesUpdated(prices);
                lastPriceListenerFiredDate = nearestDate;
            }
        }
    }

    /**
     *  Fire all price information to all PriceListeners.
     *
     *  @param  date  the date of prices to display
     */
    private void firePricesUpdated(Prices prices) {
        PriceEvent dateEvent    = new PriceEvent(this, prices.getDate(), prices.getDate());
        PriceEvent openEvent    = new PriceEvent(this, prices.getDate(), prices.getOpenObject());
        PriceEvent highEvent    = new PriceEvent(this, prices.getDate(), prices.getHighObject());
        PriceEvent lowEvent     = new PriceEvent(this, prices.getDate(), prices.getLowObject());
        PriceEvent closeEvent   = new PriceEvent(this, prices.getDate(), prices.getCloseObject());
        PriceEvent interestEvent = new PriceEvent(this, prices.getDate(), prices.getInterestObject());
        PriceEvent volumeEvent  = new PriceEvent(this, prices.getDate(), prices.getVolumeObject());

        Iterator it = priceListeners.iterator();
        while (it.hasNext()) {
            PriceListener listener = (PriceListener)it.next();
            listener.changeDate(dateEvent);
            listener.changeOpenPrice(openEvent);
            listener.changeHighPrice(highEvent);
            listener.changeLowPrice(lowEvent);
            listener.changeClosePrice(closeEvent);
            listener.changeOpenInterest(interestEvent);
            listener.changeVolume(volumeEvent);
        }
        lastPriceListenerFiredDate = null;
    }

    /**
     *  Fire all price information for a date to the given PriceListener.
     *
     *  @param  listener    the listener to fire the event to.
     *  @param  date        the date of prices to display
     */
    private void firePricesUpdated(PriceListener listener, java.util.Date date) {
        if (date != null) {
            firePricesUpdated(listener, getNearestPrices(date));
        }
    }

    /**
     *  Fire all price information to the given PriceListener.
     *
     *  @param  listener    the listener to fire the event to.
     *  @param  date        the date of prices to display
     */
    private void firePricesUpdated(PriceListener listener, Prices prices) {
        PriceEvent dateEvent    = new PriceEvent(this, prices.getDate(), prices.getDate());
        PriceEvent openEvent    = new PriceEvent(this, prices.getDate(), prices.getOpenObject());
        PriceEvent highEvent    = new PriceEvent(this, prices.getDate(), prices.getHighObject());
        PriceEvent lowEvent     = new PriceEvent(this, prices.getDate(), prices.getLowObject());
        PriceEvent closeEvent   = new PriceEvent(this, prices.getDate(), prices.getCloseObject());
        PriceEvent interestEvent = new PriceEvent(this, prices.getDate(), prices.getInterestObject());
        PriceEvent volumeEvent  = new PriceEvent(this, prices.getDate(), prices.getVolumeObject());

        listener.changeDate(dateEvent);
        listener.changeOpenPrice(openEvent);
        listener.changeHighPrice(highEvent);
        listener.changeLowPrice(lowEvent);
        listener.changeClosePrice(closeEvent);
        listener.changeOpenInterest(interestEvent);
        listener.changeVolume(volumeEvent);
    }

/* ***----------------------------------------*** */
/* *** RecommendationListener CONTROL METHODS *** */
/* ***----------------------------------------*** */
    /**
     *  Add a listener for recommendation changes.
     *
     *  @param  listener    a RecommendationListener
     */
    public void addRecommendationListener(RecommendationListener listener) {
        if (!recommendationListeners.contains(listener)) {
            recommendationListeners.add(listener);
            if (getCurrentDate() != null) {
                fireRecommendationUpdated(listener, getCurrentDate());
            }
        }
    }

    /**
     *  Remove a listener from recommendation changes.
     *
     *  @param  listener    a RecommendationListener
     */
    public void removeRecommendationListener(RecommendationListener listener) {
        recommendationListeners.remove(listener);
    }

    /**
     *  Clear all listeners for recommendation changes.
     */
    public void clearRecommendationListeners() {
        recommendationListeners.clear();
    }

    /**
     *  Fire that a recommendation was reqeusted for the given date.
     *
     *  @param  date  the date of recommendation request to fire
     */
    private void fireRecommendationRequested(java.util.Date date) {
        RecommendationEvent event  = new RecommendationEvent(this, date);

        Iterator it = recommendationListeners.iterator();
        while (it.hasNext()) {
            RecommendationListener listener = (RecommendationListener)it.next();
            listener.recommendationRequested(event);
        }
    }

    /**
     *  Fire short term recommendation information for a date to all RecommendationListeners.
     *
     *  @param  date  the date of recommendation to fire
     */
    private void fireShortTermRecommendationUpdated(java.util.Date date) {
        RecommendationEvent event  = new RecommendationEvent(this, (Recommendation)shortTermRecommendations.get(date));

        Iterator it = recommendationListeners.iterator();
        while (it.hasNext()) {
            RecommendationListener listener = (RecommendationListener)it.next();
            listener.changeShortTermRecommondation(event);
        }
    }

    /**
     *  Fire medium term recommendation information for a date to all RecommendationListeners.
     *
     *  @param  date  the date of recommendation to fire
     */
    private void fireMediumTermRecommendationUpdated(java.util.Date date) {
        RecommendationEvent event  = new RecommendationEvent(this, (Recommendation)mediumTermRecommendations.get(date));

        Iterator it = recommendationListeners.iterator();
        while (it.hasNext()) {
            RecommendationListener listener = (RecommendationListener)it.next();
            listener.changeMediumTermRecommondation(event);
        }
    }

    /**
     *  Fire long term recommendation information for a date to all RecommendationListeners.
     *
     *  @param  date  the date of recommendation to fire
     */
    private void fireLongTermRecommendationUpdated(java.util.Date date) {
        RecommendationEvent event  = new RecommendationEvent(this, (Recommendation)longTermRecommendations.get(date));

        Iterator it = recommendationListeners.iterator();
        while (it.hasNext()) {
            RecommendationListener listener = (RecommendationListener)it.next();
            listener.changeLongTermRecommondation(event);
        }
    }

    /**
     *  Fire all recommendation information for a date to the given RecommendationListener.
     *
     *  @param  listener    the listener to fire the event to.
     *  @param  date        the date of recommendation to display
     */
    private void fireRecommendationUpdated(RecommendationListener listener, java.util.Date date) {
        if (date != null) {
            RecommendationEvent event = null;
            java.util.Date nearestDate = getNearestPrices(date).getDate();

            if (!recommendationRequestDates.contains(nearestDate)) {
                return;
            } else {
                if (shortTermRecommendations.containsKey(nearestDate)) {
                    event  = new RecommendationEvent(this, (Recommendation)shortTermRecommendations.get(date));
                    listener.changeShortTermRecommondation(event);
                }
                if (mediumTermRecommendations.containsKey(nearestDate)) {
                    event  = new RecommendationEvent(this, (Recommendation)mediumTermRecommendations.get(date));
                    listener.changeMediumTermRecommondation(event);
                }
                if (longTermRecommendations.containsKey(nearestDate)) {
                    event  = new RecommendationEvent(this, (Recommendation)longTermRecommendations.get(date));
                    listener.changeLongTermRecommondation(event);
                }
            }
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Converts a formatted month in the format of 'MMM YY' to a month formatted
     *  for a key in the format 'YYYYM'.
     *
     *  @param  formattedMonth  month in 'MMM YY'
     *  @return                 month in 'YYYYM'
     */
    public static String convertFormattedMonthToKey(String formattedMonth) {
        StringBuffer formatted = new StringBuffer(formattedMonth);
        StringBuffer key = new StringBuffer();

        key.append("20");
        key.append(formatted.substring(formatted.length() - 2));
        key.append(Month.byAbbrev(formatted.substring(0, 3)).getSymbol());
        return key.toString();
    }

    /**
     *  Get the currently selected contract.  This is for those cases where
     *  a listener is not adequate.
     *
     *  @return     the currently selected contract
     */
    public static Contract getSelectedContract() throws ContractNotSelectedException {
        if (selectedContract == null) {
            throw new ContractNotSelectedException("No contract is currently selected");
        }
        return selectedContract;
    }

/* ***-------------------------------------------*** */
/* *** ContractSelectionListener CONTROL METHODS *** */
/* ***-------------------------------------------*** */
    /**
     *  Add a listener for selecting a contract.
     *
     *  @param  listener    a ContractSelectionListener
     */
    public static void addSelectionListener(ContractSelectionListener listener) {
        selectionListeners.add(listener);
    }

    /**
     *  Remove a listener from selecting a contract.
     *
     *  @param  listener    a ContractSelectionListener
     */
    public static void removeSelectionListener(ContractSelectionListener listener) {
        selectionListeners.remove(listener);
    }

    /**
     *  Clear all listeners for selecting a contract.
     */
    public static void clearSelectionListeners() {
        selectionListeners.clear();
    }

    /**
     *  Fire a contract selection change.
     */
    private static void fireSelectionUpdated() {
        ContractSelectionEvent event = new ContractSelectionEvent(selectedContract);

        Iterator it = selectionListeners.iterator();
        while (it.hasNext()) {
            ContractSelectionListener listener = (ContractSelectionListener)it.next();
            listener.selectContract(event, true);
        }

        it = selectionListeners.iterator();
        while (it.hasNext()) {
            ContractSelectionListener listener = (ContractSelectionListener)it.next();
            listener.selectContract(event, false);
        }
    }
}
