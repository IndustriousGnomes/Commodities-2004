/*  x Properly Documented
 */
package commodities.commodity;

import java.io.*;
import java.text.*;
import java.util.*;

import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.event.*;

//import java.awt.*;
//import javax.swing.*;
//import com.awt.*;

/**
 *  Commodity is an object that contains the basic information about
 *  a commodity regardless of delivery (contract).
 *
 *  This window is divided into four main parts...
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class Commodity {

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** A reference to the data manager. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();
    /** The commodity table sorted by commodity symbol. */
    private static Map tableBySymbol;
    /** The commodity table sorted by commodity name. */
    private static Map tableByName;

    /**
     *  The commodity table organized by exchange and then organized by commodity name.
     *  There is a sub-table for each exchange which holds the names of the commodities
     *  traded on that exchange.
     */
    private static Map tableByExchange;

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

    /** The commodities are loaded. */
    private static boolean loaded = false;

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
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
    /** The normal margin of a contract for this commodity. */
    private int     margin;
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

    /** Listener list for contracts. */
    private LinkedList contractListeners = new LinkedList();

    /** The contracts for this commodity are loaded. */
    private boolean contractsLoaded = false;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
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
     *  @param  margin   The standard price of a contract for this commodity
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
                        int     margin,
                        int     displayDecimals) {

        this(symbol, exchange, name, unitType, unitSize, unitPrice, tickSize, tickPrice, tickDailyLimit, margin, displayDecimals, 0);
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
     *  @param  margin          The standard margin of a contract for this commodity
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
                        int     margin,
                        int     displayDecimals,
                        int     tradeMonthMask) {

        this(symbol, exchange, name, unitType, unitSize, unitPrice, tickSize, tickPrice, tickDailyLimit, margin, displayDecimals,
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
     *  @param  margin          The standard margin of a contract for this commodity
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
                        int     margin,
                        int     displayDecimals,
                        String  openOutcrySymbol,
                        String  acmSymbol,
                        int     tradeMonthMask) {

        this(symbol, exchange, name, unitType, unitSize, unitPrice, tickSize, tickPrice, tickDailyLimit, margin, displayDecimals,
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
     *  @param  margin          The standard margin of a contract for this commodity
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
                        int     margin,
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
        this.tickPrice          = (int)(tickPrice * 100.0 + .01) / 100.0;
        this.tickDailyLimit     = tickDailyLimit;
        this.margin             = margin;
        this.displayDecimals    = displayDecimals;
        this.openOutcrySymbol   = openOutcrySymbol.trim();
        this.acmSymbol          = acmSymbol.trim();
        this.tradeMonthMask     = tradeMonthMask;
        this.databaseSymbol     = databaseSymbol.trim();

        loadContracts();
    }

/* *************************************** */
/* *** GET & SET METHODS               *** */
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
    public int     getMargin()          { return margin; }
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
     *  Set the type of unit of the commodity.
     *  @param  unitType    the type of unit of the commodity
     */
    public void  setUnitType(String unitType) {
        this.unitType = unitType;
    }
    /**
     *  Set the size of a unit of the commodity.
     *  @param  unitSize    the size of a unit of the commodity
     */
    public void  setUnitSize(int unitSize) {
        this.unitSize = unitSize;
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
        this.tickPrice = (int)(tickPrice * 100 + .01) / 100.0;
    }
    /**
     *  Set the daily limit in ticks of the commodity.
     *  @param  tickDailyLimit  the daily limit in ticks of the commodity
     */
    public void  setTickDailyLimit(int tickDailyLimit) {
        this.tickDailyLimit = tickDailyLimit;
    }
    /**
     *  Set the standard margin of the commodity.
     *  @param  margin   the standard price of the commodity
     */
    public void  setMargin(int margin) {
        this.margin = margin;
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

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Get a specific contract within this commodity.
     *
     *  @param  contractMonth   The month of the contract to be retrieved.
     *  @return         The contract if it exists or null.
     */
    public Contract getContract(String contractMonth) {
        loadContracts();
        return (Contract)contracts.get(contractMonth);
    }

    /**
     *  Get all contracts within this commodity.
     *
     *  @return The iterator to the contracts.
     */
    public Iterator getContracts() {
        loadContracts();
        return contracts.values().iterator();
    }

    /**
     *  Adds a new contract to the commodity.  This contract will be stored.
     *
     *  @param  contractMonth    the month of the contract in 'YYYYM'
     *  @return     the contract added or the original contract
     */
    public Contract addContract(String contractMonth) {
        Contract contract = null;
        if ((contract = getContract(contractMonth)) == null) {
            try {
                dataManager.addContract(symbol, contractMonth);
                contract = dataManager.getContractByMonth(symbol, contractMonth);
                addContract(contract);
            } catch (IOException e) {
                System.out.println("Failed to add contract " + symbol + " " + contractMonth);
                return null;
            }
        }
        return contract;
    }

    /**
     *  Adds a new contract to the commodity.
     *
     *  @param  contract    contract to add
     */
    public void addContract(Contract contract) {

        contracts.put(contract.getMonth(), contract);
    }

    /**
     *  Load the contracts that go with this commodity into memory.
     */
    private void loadContracts() {
        if (!contractsLoaded) {
            synchronized(this) {
                if (!contractsLoaded) {
                    contracts   = new TreeMap();

                    try {
                        Iterator it = dataManager.getContracts(symbol);
                        while (it.hasNext()) {
                            Contract contract = (Contract)it.next();
                            addContract(contract);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                contractsLoaded = true;
            }
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

/* ***---------------------------------*** */
/* *** EVENT LISTENER METHODS          *** */
/* ***---------------------------------*** */
    /**
     *  Add a listener for adding contracts.
     *
     *  @param  listener    a ContractListener
     */
    public void addContractListener(ContractListener listener) {
        contractListeners.add(listener);
    }

    /**
     *  Remove a listener from adding contracts.
     *
     *  @param  listener    a ContractListener
     */
    public void removeContractListener(ContractListener listener) {
        contractListeners.remove(listener);
    }

    /**
     *  Clear all listeners for adding contracts.
     */
    public void clearContractListeners() {
        contractListeners.clear();
    }

    /**
     *  Fire a ContractEvent to all of the ContractListeners.
     *
     *  @param  prices  the prices to display
     */
    public void fireContractUpdated(Contract contract) {
        ContractEvent event = new ContractEvent(this, contract);

        Iterator it = contractListeners.iterator();
        while (it.hasNext()) {
            ContractListener listener = (ContractListener)it.next();
            listener.addContract(event);
        }
    }

/* *************************************** */
/* *** STATIC METHODS                  *** */
/* *************************************** */
    /**
     *  Retrieve a Commodity object by using the commodity symbol.
     *
     *  @param  symbol  the commodity symbol
     *  @return         a Commodity object of the requested commodity
     */
    public static Commodity bySymbol(String symbol) {
        loadTable();
        return (Commodity)tableBySymbol.get(symbol);
    }

    /**
     *  Retrieve a Commodity object by using the commodity name and exchange.
     *
     *  @param  nameExchange    the commodity name and exchange
     *  @return                 a Commodity object of the requested commodity
     */
    public static Commodity byNameExchange(String nameExchange) {
        loadTable();
        return (Commodity)tableByName.get(nameExchange);
    }

    /**
     *  Retrieve a map keyed on the names of the available commodities with Commodity
     *  objects as associated values.
     *
     *  @return     a map containing all Commodities keyed by name
     */
    public static Map getNameMap() {
        loadTable();
        return tableByName;
    }

    /**
     *  Retrieve a map keyed on the names of the available commodities within an exchange
     *  with Commodity objects as associated values.
     *
     *  @param  exchange    The exchange to retrieve the commodity list for
     *  @return     a map containing all Commodities for an exchange
     */
    public static Map getExchangeMap(String exchange) {
        loadTable();
        return (Map)tableByExchange.get(exchange);
    }

    /**
     *  Load the Commodity table and store it to be keyed by symbol.
     */
    private static void loadTable() {
        if (!loaded) {
            synchronized(Commodity.class) {
                if (!loaded) {
                    tableBySymbol   = new TreeMap();
                    tableByName     = new TreeMap();
                    tableByExchange = new TreeMap();

                    try {
                        Iterator it = dataManager.getCommodities();
                        while (it.hasNext()) {
                            Commodity commodity = (Commodity)it.next();
                            tableBySymbol.put(commodity.getSymbol(), commodity);
                            tableByName.put(commodity.getNameExchange(), commodity);

                            if (!tableByExchange.containsKey(commodity.getExchange())) {
                                tableByExchange.put(commodity.getExchange(), new TreeMap());
                            }
                            ((Map)tableByExchange.get(commodity.getExchange())).put(commodity.getNameExchange(), commodity);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                loaded = true;
            }
        }
    }
}
