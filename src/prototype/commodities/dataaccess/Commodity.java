/*  x Properly Documented
 */
package prototype.commodities.dataaccess;

import java.io.*;
import java.util.*;
import java.text.*;
import prototype.commodities.*; // debug only

/**
 *  The Commodity class is a table lookup of the variety of representations of the information
 *  that defines a commodity regardless of the particular monthly contract that is being traded.
 *
 *  @author J.R. Titko
 */
public class Commodity {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The singleton instance of the DataManagerFactory. */
    private static DataManagerInterface dataManager = DataManagerFactory.getInstance();
    /** The commodity table sorted by commodity symbol. */
    private static Map tableBySymbol;
    /** The commodity table sorted by commodity name. */
    private static Map tableByName;

    /** Bit map for the month of January.  This is used as a mask to the valid months of a commoidity. */
    public static final int JAN = 1 << 11;
    /** Bit map for the month of February.  This is used as a mask to the valid months of a commoidity. */
    public static final int FEB = 1 << 10;
    /** Bit map for the month of March.  This is used as a mask to the valid months of a commoidity. */
    public static final int MAR = 1 << 9;
    /** Bit map for the month of April.  This is used as a mask to the valid months of a commoidity. */
    public static final int APR = 1 << 8;
    /** Bit map for the month of May.  This is used as a mask to the valid months of a commoidity. */
    public static final int MAY = 1 << 7;
    /** Bit map for the month of June.  This is used as a mask to the valid months of a commoidity. */
    public static final int JUN = 1 << 6;
    /** Bit map for the month of July.  This is used as a mask to the valid months of a commoidity. */
    public static final int JUL = 1 << 5;
    /** Bit map for the month of August.  This is used as a mask to the valid months of a commoidity. */
    public static final int AUG = 1 << 4;
    /** Bit map for the month of September.  This is used as a mask to the valid months of a commoidity. */
    public static final int SEP = 1 << 3;
    /** Bit map for the month of October.  This is used as a mask to the valid months of a commoidity. */
    public static final int OCT = 1 << 2;
    /** Bit map for the month of November.  This is used as a mask to the valid months of a commoidity. */
    public static final int NOV = 1 << 1;
    /** Bit map for the month of December.  This is used as a mask to the valid months of a commoidity. */
    public static final int DEC = 1;


    /** The symbol for the commodity. */
    private String  symbol;
    /** The exchange that the commodity is traded on. */
    private String  exchange;
    /** The name of the commodity. */
    private String  name;
    /** The type of units the contract is measured in. */
    private String  unitType;
    /** The number of units in a contract. */
    private int     unitSize;
    /** The type of units the prices is recorded in (cents, dollars, etc). */
    private String  unitPrice;
    /** The size of a single tick. */
    private double  tickSize;
    /** The monetary value of a tick. */
    private double  tickPrice;
    /** The maximum number of ticks a commodity may move in a day. */
    private int     tickDailyLimit;
    /** The normal price of a contract for this commodity. */
    private int     standardPrice;
    /** The number of decimal places to display. */
    private int     displayDecimals;
    /** The symbol used for open outcry. */
    private String  openOutcrySymbol;
    /** The symbol used for the ACM. */
    private String  acmSymbol;
    /** The months that this commodity is traded in. */
    private int     tradeMonthMask;
    /** The special symbol used for database access. */
    private String  databaseSymbol;

    /** A table of contracts for this commodity. */
    private Map     contracts;

    /**
     *  Creates a new commodity with the minimum amount of information required when setting up
     *  a new commodity.
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  exchange        The exchange that the commodity is traded on
     *  @param  name            The name of the commodity
     *  @param  unitType        The type of units the contract is measured in
     *  @param  unitSize        The number of units in a contract
     *  @param  unitPrice       The type of units the prices is recorded in (cents, dollars, etc)
     *  @param  tickSize        The size of a single tick
     *  @param  tickPrice       The monetary value of a tick
     *  @param  tickDailyLimit  The maximum number of ticks a commodity may move in a day
     *  @param  standardPrice   The standard price of a contract for this commodity
     *  @param  displayDecimals The number of decimal places to display
     */
    public Commodity (  String  symbol,
                        String  exchange,
                        String  name,
                        String  unitType,
                        int     unitSize,
                        String  unitPrice,
                        double  tickSize,
                        double  tickPrice,
                        int     tickDailyLimit,
                        int     standardPrice,
                        int     displayDecimals) {

        this(symbol, exchange, name, unitType, unitSize, unitPrice, tickSize, tickPrice, tickDailyLimit, standardPrice, displayDecimals, 0);
    }


    /**
     *  Creates a new commodity with a mask of commodity trading months.  The mask is a representation
     *  of what months can be traded by using the constant variables for the months in a binary or (|).
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  exchange        The exchange that the commodity is traded on
     *  @param  name            The name of the commodity
     *  @param  unitType        The type of units the contract is measured in
     *  @param  unitSize        The number of units in a contract
     *  @param  unitPrice       The type of units the prices is recorded in (cents, dollars, etc)
     *  @param  tickSize        The size of a single tick
     *  @param  tickPrice       The monetary value of a tick
     *  @param  tickDailyLimit  The maximum number of ticks a commodity may move in a day
     *  @param  standardPrice   The standard price of a contract for this commodity
     *  @param  displayDecimals The number of decimal places to display
     *  @param  tradeMonthMask  The months that this commodity is traded in
     */
    public Commodity (  String  symbol,
                        String  exchange,
                        String  name,
                        String  unitType,
                        int     unitSize,
                        String  unitPrice,
                        double  tickSize,
                        double  tickPrice,
                        int     tickDailyLimit,
                        int     standardPrice,
                        int     displayDecimals,
                        int     tradeMonthMask) {

        this(symbol, exchange, name, unitType, unitSize, unitPrice, tickSize, tickPrice, tickDailyLimit, standardPrice, displayDecimals,
            "", "", tradeMonthMask);
    }

    /**
     *  Creates a new commodity with the open outcry symbol, acm symbol, and a mask of commodity trading months.
     *  The mask is a representation of what months can be traded by using the constant variables for the months
     *  in a binary or (|).
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  exchange        The exchange that the commodity is traded on
     *  @param  name            The name of the commodity
     *  @param  unitType        The type of units the contract is measured in
     *  @param  unitSize        The number of units in a contract
     *  @param  unitPrice       The type of units the prices is recorded in (cents, dollars, etc)
     *  @param  tickSize        The size of a single tick
     *  @param  tickPrice       The monetary value of a tick
     *  @param  tickDailyLimit  The maximum number of ticks a commodity may move in a day
     *  @param  standardPrice   The standard price of a contract for this commodity
     *  @param  displayDecimals The number of decimal places to display
     *  @param  openOutcrySymbol The symbol used for open outcry
     *  @param  acmSymbol       The symbol used for the ACM
     *  @param  tradeMonthMask  The months that this commodity is traded in
     */
    public Commodity (  String  symbol,
                        String  exchange,
                        String  name,
                        String  unitType,
                        int     unitSize,
                        String  unitPrice,
                        double  tickSize,
                        double  tickPrice,
                        int     tickDailyLimit,
                        int     standardPrice,
                        int     displayDecimals,
                        String  openOutcrySymbol,
                        String  acmSymbol,
                        int     tradeMonthMask) {

        this(symbol, exchange, name, unitType, unitSize, unitPrice, tickSize, tickPrice, tickDailyLimit, standardPrice, displayDecimals,
            "", "", tradeMonthMask, "");
    }

    /**
     *  Creates a new commodity with the open outcry symbol, acm symbol, and a mask of commodity trading months.
     *  The mask is a representation of what months can be traded by using the constant variables for the months
     *  in a binary or (|).
     *
     *  @param  symbol          The symbol for the commodity
     *  @param  exchange        The exchange that the commodity is traded on
     *  @param  name            The name of the commodity
     *  @param  unitType        The type of units the contract is measured in
     *  @param  unitSize        The number of units in a contract
     *  @param  unitPrice       The type of units the prices is recorded in (cents, dollars, etc)
     *  @param  tickSize        The size of a single tick
     *  @param  tickPrice       The monetary value of a tick
     *  @param  tickDailyLimit  The maximum number of ticks a commodity may move in a day
     *  @param  standardPrice   The standard price of a contract for this commodity
     *  @param  displayDecimals The number of decimal places to display
     *  @param  openOutcrySymbol The symbol used for open outcry
     *  @param  acmSymbol       The symbol used for the ACM
     *  @param  tradeMonthMask  The months that this commodity is traded in
     *  @param  databaseSymbol  The special symbol used for DB access
     */
    public Commodity (  String  symbol,
                        String  exchange,
                        String  name,
                        String  unitType,
                        int     unitSize,
                        String  unitPrice,
                        double  tickSize,
                        double  tickPrice,
                        int     tickDailyLimit,
                        int     standardPrice,
                        int     displayDecimals,
                        String  openOutcrySymbol,
                        String  acmSymbol,
                        int     tradeMonthMask,
                        String  databaseSymbol) {

        this.symbol             = symbol.trim();
        this.exchange           = exchange.trim();
        this.name               = name.trim();
        this.unitType           = unitType.trim();
        this.unitSize           = unitSize;
        this.unitPrice          = unitPrice.trim();
        this.tickSize           = tickSize;
        this.tickPrice          = tickPrice;
        this.tickDailyLimit     = tickDailyLimit;
        this.standardPrice      = standardPrice;
        this.displayDecimals    = displayDecimals;
        this.openOutcrySymbol   = openOutcrySymbol.trim();
        this.acmSymbol          = acmSymbol.trim();
        this.tradeMonthMask     = tradeMonthMask;
        this.databaseSymbol     = databaseSymbol.trim();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Get the symbol of the commodity.
     *  @return     the symbol of the commodity
     */
    public String  getSymbol()          { return symbol; }
    /**
     *  Get the name of the exchange the commodity is traded on.
     *  @return     the exchange of the commodity
     */
    public String  getExchange()        { return exchange; }
    /**
     *  Get the name of the commodity.
     *  @return     the name of the commodity
     */
    public String  getName()            { return name; }
    /**
     *  Get the name and exchange of the commodity.
     *  @return     the name and exchange of the commodity
     */
    public String  getNameExchange()    { return name + " (" + exchange + ")"; }
    /**
     *  Get the type of unit the commodity is recorded in.
     *  @return     the unit type of the commodity
     */
    public String  getUnitType()        { return unitType; }
    /**
     *  Get the size of the unit the commodity is recorded in.
     *  @return     the unit size of the commodity
     */
    public int     getUnitSize()        { return unitSize; }
    /**
     *  Get the unit price of the commodity.
     *  @return     the unit price of the commodity
     */
    public String  getUnitPrice()       { return unitPrice; }
    /**
     *  Get the tick size of the commodity.
     *  @return     the tick size of the commodity
     */
    public double  getTickSize()        { return tickSize; }
    /**
     *  Get the tick price of the commodity.
     *  @return     the tick price of the commodity
     */
    public double  getTickPrice()       { return tickPrice; }
    /**
     *  Get the daily limit in ticks of the commodity.
     *  @return     the daily limit of the commodity in ticks
     */
    public int     getTickDailyLimit()  { return tickDailyLimit; }
    /**
     *  Get the standard price of the commodity.
     *  @return     the standard price of the commodity
     */
    public int     getStandardPrice()   { return standardPrice; }
    /**
     *  Get the number of display decimals of the commodity.
     *  @return     the number of decimals of the commodity
     */
    public int     getDisplayDecimals() { return displayDecimals; }
    /**
     *  Get the open outcry symbol of the commodity.
     *  @return     the open outcry symbol of the commodity
     */
    public String  getOpenOutcrySymbol(){ return openOutcrySymbol; }
    /**
     *  Get the ACM symbol of the commodity.
     *  @return     the acm symbol of the commodity
     */
    public String  getAcmSymbol()       { return acmSymbol; }
    /**
     *  Get the months the commodity is traded as a series of bits.  Use the month constants (JAN, FEB, MAR, APR,
     *  MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC) to check if a month is traded for this commodity.
     *
     *  @return     the bit representation of the trading months
     */
    public int     getTradeMonthMask()  { return tradeMonthMask; }
    /**
     *  Get the special DB symbol for database access.
     *  @return     the special DB symbol for the commodity
     */
    public String  getDatabaseSymbol()       {
        return databaseSymbol;
    }
    /**
     *  Get the special DB symbol for database access or the commodity
     *  symbol if there is no special DB symbol.
     *  @return     the special DB symbol or commodity symbol for the commodity
     */
    public String  getDatabaseCommoditySymbol()       {
        if ((databaseSymbol == null) || "".equals(databaseSymbol.trim())) {
            return getSymbol();
        }
        return databaseSymbol;
    }

    /**
     *  Set the name of the commodity.
     *  @param  name        the name of the commodity
     */
    public void  setName(String name) {
        this.name = name;
    }
    /**
     *  Set the unit price of the commodity.
     *  @param  unitPrice   the unit price of the commodity
     */
    public void  setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
    /**
     *  Set the tick size of the commodity.
     *  @param  tickSize    the tick size of the commodity
     */
    public void  setTickSize(double tickSize)  {
        this.tickSize = tickSize;
    }
    /**
     *  Set the tick price of the commodity.
     *  @param  tickPrice   the tick price of the commodity
     */
    public void  setTickPrice(double tickPrice) {
        this.tickPrice = tickPrice;
    }
    /**
     *  Set the daily limit in ticks of the commodity.
     *  @param  tickDailyLimit  the daily limit in ticks of the commodity
     */
    public void  setTickDailyLimit(int tickDailyLimit) {
        this.tickDailyLimit = tickDailyLimit;
    }
    /**
     *  Set the standard price of the commodity.
     *  @param  standardPrice   the standard price of the commodity
     */
    public void  setStandardPrice(int standardPrice) {
        this.standardPrice = standardPrice;
    }
    /**
     *  Set the number of decimals to display for the commodity.
     *  @param  displayDecimals the number of decimals to display
     */
    public void  setDisplayDecimals(int displayDecimals) {
        this.displayDecimals = displayDecimals;
    }
    /**
     *  Set the open outcry symbol of the commodity.
     *  @param  openOutcrySymbol    the open outcry symbol of the commodity
     */
    public void  setOpenOutcrySymbol(String openOutcrySymbol) {
        this.openOutcrySymbol = openOutcrySymbol;
    }
    /**
     *  Set the ACM symbol of the commodity.
     *  @param  acmSymbol   the ACM symbol of the commodity
     */
    public void  setAcmSymbol(String acmSymbol) {
        this.acmSymbol = acmSymbol;
    }
    /**
     *  Set the trading months of the commodity.
     *  @param  tradeMonthMask  the month mask of the commodity
     */
    public void  setTradeMonthMask(int tradeMonthMask) {
        this.tradeMonthMask = tradeMonthMask;
    }
    /**
     *  Set the special DB symbol for the commodity.
     *  @param  databaseSymbol   the special DB symbol for the commodity
     */
    public void  setDatabaseSymbol(String databaseSymbol) {
        this.databaseSymbol = databaseSymbol;
    }

    /**
     *  Get a specific contract within this commodity.  The contractMonth variable
     *  must be of the format returned by Contract.getMonthAsKey.
     *
     *  @param  contractMonth   The month of the contract to be retrieved.
     *  @return The contract if it exists or null.
     *  @see Contract.getMonthAsKey
     */
    public Contract getContract(String contractMonth) {
        if (contracts == null) {
            loadContracts();
        }
        return (Contract)contracts.get(contractMonth);
    }

    /**
     *  Get all contracts within this commodity.
     *
     *  @return The iterator to the contracts.
     */
    public Iterator getContracts() {
        if (contracts == null) {
            loadContracts();
        }
        return contracts.values().iterator();
    }

    /**
     *  Removes all contracts associated with this commodity from
     *  the internal list.  The next access for contracts will then
     *  be a fresh retrieval.
     */
    public void clearContracts() {
        contracts = null;
    }

    /**
     *  Load the contracts that go with this commodity into memory.
     */
    private void loadContracts() {
        contracts   = new TreeMap();

        try {
            Iterator it = dataManager.getContracts(symbol);
            while (it.hasNext()) {
                Contract contract = (Contract)it.next();
                contracts.put(contract.getMonthAsKey(), contract);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Format a price to the appropriate number of decimal places so that
     *  the number of decimal places are fixed even if they end in zeros.
     *
     *  @param  price   the price to be formatted
     *  @return         the formatted price
     */
    public String formatPrice(double price) {
        DecimalFormat format = null;
        double tickSize = getTickSize();

        if (tickSize >= 1) {
            format = new DecimalFormat("0");
        } else {
            StringBuffer sb = new StringBuffer();
            double value = tickSize;
            do {
                sb.append("0");
                value *= 10;
            } while (((value - (int)value) != 0.0) && (sb.length() < 3));
            format = new DecimalFormat("0." + sb);
        }

        return format.format(price);
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Retrieve a Commodity object by using the commodity symbol.
     *
     *  @param  symbol  the commodity symbol
     *  @return         a Commodity object of the requested commodity
     */
    public static Commodity bySymbol(String symbol) {
        if (tableBySymbol == null) {
            loadTable();
        }
        return (Commodity)tableBySymbol.get(symbol);
    }

    /**
     *  Retrieve a Commodity object by using the commodity name and exchange.
     *
     *  @param  nameExchange    the commodity name and exchange
     *  @return                 a Commodity object of the requested commodity
     */
    public static Commodity byNameExchange(String nameExchange) {
        if (tableByName == null) {
            loadTable();
        }
        return (Commodity)tableByName.get(nameExchange);
    }

    /**
     *  Retrieve a map keyed on the names of the available commodities with Commodity
     *  objects as associated values.
     *
     *  @return     a map containing all Commodities keyed by name
     */
    public static Map getNameMap() {
        if (tableByName == null) {
            loadTable();
        }
        return tableByName;
    }

    /**
     *  Load the Commodity table and store it to be keyed by symbol.
     */
    private static void loadTable() {
        tableBySymbol   = new TreeMap();
        tableByName     = new TreeMap();

        try {
            Iterator it = dataManager.getCommodities();
            while (it.hasNext()) {
                Commodity commodity = (Commodity)it.next();
                tableBySymbol.put(commodity.getSymbol(), commodity);
                tableByName.put(commodity.getNameExchange(), commodity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Add or update a commodity in the lookup table.
     *
     *  @param  commodity   the commodity that changed or should be added
     */
    public static void editCommodity(Commodity commodity) {
        if (tableBySymbol == null) {
            loadTable();
        }

        try {
            Object orig = tableBySymbol.put(commodity.getSymbol(), commodity);
            tableByName.put(commodity.getNameExchange(), commodity);

            if (orig == null) {
                dataManager.addCommodity(commodity);
            } else {
                dataManager.updateCommodity(commodity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
