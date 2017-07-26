/*  _ Properly Documented
 */
/**
 *  The Contract class represents the information for a given delivery month.
 *
 *  @author J.R. Titko
 */
package prototype.commodities.dataaccess;

import java.io.*;
import java.util.*;
import prototype.commodities.util.*;
import prototype.commodities.*; // debug only

public class Contract {
    private static boolean DEBUG = false;

    private static DataManagerInterface dataManager = DataManagerFactory.getInstance();

    private int     id;                 // The unique identifier for this contract
    private String  symbol;             // The symbol for the commodity
    private String  month;              // The trading month of this contract
    private int     price;              // The price of the contract in dollars

    private LinkedTreeMap   priceList;  // A list of daily prices for this commodity.
    private Prices  currentPrices;      // The current price of this commodity.
    private double  minPrice = 99999.0; // The minimum price for the life of the contract.
    private double  maxPrice = 0.0;     // The maximum price for the life of the contract.
    private Date    firstDate;          // The first date that data is present.
    private Date    lastDate;           // The last date that data is present.
    private Date    nextToLastDate;           // The next to last date that data is present.

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new Contract with a given commodity symbol and contract month.
     *  The contract price will default to the standard price given for the commodity.
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  month   The trading month of this contract
     */
    public Contract(String symbol, String month) {
        this(symbol, month, Commodity.bySymbol(symbol).getStandardPrice());
    }

    /**
     *  Create a new Contract with a given commodity symbol and contract month and the price the
     *  contract costs.
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  month   The trading month of this contract
     *  @param  price           The price of the contract in dollars
     */
    public Contract(String symbol, String month, int price) {
        this(0, symbol, month, price);
    }

    /**
     *  Create a new contract with a given commodity symbol and contract month and the price the
     *  contract costs.
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  month   The trading month of this contract
     *  @param  price           The price of the contract in dollars
     */
    public Contract(int id, String symbol, String month, int price) {
        this.id     = id;
        this.symbol = symbol.trim();
        this.month = month.trim();
        this.price  = price;
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
/* *************************************** */
    public int    getId()           { return id; }

    public String getSymbol()       { return symbol; }

    /** Format is in MYYYY */
    public String getMonth()        { return month; }

    /** Format is in 'MMM YY' */
    public String getMonthFormatted() {
        return Month.bySymbol(month.substring(0,1)).getAbbrev() + " " + month.substring(3).trim();
    }

    /** Format is in YYYYM */
    public String getMonthAsKey(){
        return month.substring(1).trim() + month.substring(0,1);
    }
    public int    getPrice()        { return price; }
    public synchronized double getMinPrice()     {
        if (priceList == null) {
            loadPrices();
        }
        return minPrice;
    }

    public String getName() {
        return (getSymbol() + " " + getMonthFormatted());
    }

    public synchronized double getMaxPrice()     {
        if (priceList == null) {
            loadPrices();
        }
        return maxPrice;
    }

    public synchronized Date getFirstDate()     {
        if (priceList == null) {
            loadPrices();
        }
        return firstDate;
    }

    public synchronized Date getLastDate()     {
        if (priceList == null) {
            loadPrices();
        }
        return lastDate;
    }
/*
    public synchronized Date getNextToLastDate()     {
        if (priceList == null) {
            loadPrices();
        }
        return nextToLastDate;
    }
*/
    public void setId(int id) {
        this.id = id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Get the current Prices of this contract.
     *
     *  @return The current Prices of this contract.
     */
    public synchronized Prices getPrices() {
        if (currentPrices == null) {
            try {
                currentPrices = dataManager.getCurrentPrices(id);
Debug.println(DEBUG, this, "getPrices - currentPrices.getOpen = " + currentPrices.getOpen());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
//        calendar.clearTime();
//        if (calendar.getTime().compareTo(currentPrices.getDate()) < 0) {
//            return getPrices(calendar.getTime());
//        } else {
            return currentPrices;
//        }
    }

    /**
     *  Get the Prices for the given date of this contract.
     *
     *  @param  date    The date to retrieve the prices for.
     *  @return The current Prices of this month.
     */
    public synchronized Prices getPrices(java.util.Date date) {
        if (priceList == null) {
            loadPrices();
        }
        return (Prices)priceList.get(date);
    }

    /**
     *  Get the all of the Prices for this contract.
     *
     *  @return the Prices of this contract.
     */
    public synchronized Map getPriceList() {
        if (priceList == null) {
            loadPrices();
        }
        return priceList;
    }

    /**
     *  Get the all of the Price date keys in a ListIterator format
     *  for forwards and backwards traversal of the list.
     *
     *  A descending list will begin at the most current date and
     *  should be traversed backwards (previous).
     *  An ascending list (desc = false) will begin at the earliest
     *  available date and should be traversed forwards (next).
     *
     *  This ListIterator cannot have objects added or removed from it.
     *
     *  @return the ListIterator of keys.
     */
    public synchronized ListIterator getPriceDateListIterator(boolean desc) {
        if (priceList == null) {
            loadPrices();
        }
        if (desc) {
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
        if (priceList == null) {
            priceList   = new LinkedTreeMap();

            try {
                if (currentPrices == null) {
                    currentPrices = dataManager.getCurrentPrices(id);
                }
                Iterator it = dataManager.getPrices(id);
                while (it.hasNext()) {
                    Prices prices = (Prices)it.next();
                    if (prices.getHigh() > maxPrice) {
                        maxPrice = prices.getHigh();
                    }
                    if (prices.getLow() < minPrice) {
                        minPrice = prices.getLow();
                    }
                    if (firstDate == null) {
                        firstDate = prices.getDate();
                    }
//                    nextToLastDate = lastDate;
                    lastDate = prices.getDate();
                    priceList.put(lastDate, prices);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    public static void editContract(Contract contract) {
        try {
            dataManager.updateContract(contract);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertFormattedMonthToKey(String formattedMonth) {
        StringBuffer formatted = new StringBuffer(formattedMonth);
        StringBuffer key = new StringBuffer();

        key.append("20");
        key.append(formatted.substring(formatted.length() - 2));
        key.append(Month.byAbbrev(formatted.substring(0, 3)).getSymbol());
        return key.toString();
    }
}
