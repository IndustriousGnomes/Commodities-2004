/*  x Properly Documented
 */
package commodities.dataaccess;

import java.io.*;
import java.util.*;

import commodities.account.*;
import commodities.commodity.*;
import commodities.contract.*;
import commodities.price.*;
import commodities.tests.technical.*;
import commodities.util.*;

import com.util.*;

/**
 *  The interface for all of the data accesses in the commodity system.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public interface DataManagerInterface {

/* *************************************** */
/* *** COMMODITY                       *** */
/* *************************************** */
    /**
     *  Loads all of the commodities from the commodity table and returns
     *  an itertor of Commodity objects.
     *
     *  @return Iterator of Commodity objects
     *  @throws SQLException    There was a problem accessing the database table
     */
    public Iterator getCommodities() throws IOException;

    /**
     *  Adds a commodity to the commodities table.
     *
     *  @param  commodity   The commodity to add to the table.
     *  @throws SQLException    There was a problem accessing the database table
     */
    public void addCommodity(Commodity commodity) throws IOException;

    /**
     *  Updates a commodity in the commodities table.
     *
     *  @param  commodity   The commodity to add to the table.
     *  @throws SQLException    There was a problem accessing the database table
     */
    public void updateCommodity(Commodity commodity) throws IOException;


/* *************************************** */
/* *** CONTRACT                        *** */
/* *************************************** */
    /**
     *  Retrieves a contract by symbol and contract month.
     *
     *  @param  symbol          the symbol of the commodity to retrieve.
     *  @param  contractMonth   the contract month of the commodity to retrieve
     *  @return         The contract for the requested commodity and contract month.
     */
    public Contract getContractByMonth(String symbol, String contractMonth) throws IOException;

    /**
     *  Retrieves all contracts by symbol.
     *
     *  @param  symbol  the symbol of the commodity to retrieve.
     *  @return         an iterator of the contracts for the commodity
     */
    public Iterator getContracts(String symbol) throws IOException;

    /**
     *  Add a new contract by symbol and contract month.
     *
     *  @param  symbol          the symbol of the commodity to retrieve.
     *  @param  contractMonth   the contract month of the commodity to retrieve
     */
    public void addContract(String symbol, String contractMonth) throws IOException;

    /**
     *  Add an existing contract.
     *
     *  @param  contract    the contract to update
     */
    public void updateContract(Contract contract) throws IOException;


/* *************************************** */
/* *** ACCOUNT                         *** */
/* *************************************** */
    /**
     *  Retrieves the last index number used in the account table
     *
     *  @return The most current index number in the account table
     */
    public long getLastAccountIndex() throws IOException;

    /**
     *  Loads all of the accounts from the account table and provides
     *  an itertor of Account objects.
     *
     *  @return Iterator of Account objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public Iterator getAccounts() throws IOException;

    /**
     *  Adds an account to the account table.
     *
     *  @param  account     The account to add to the table.
     *  @throws IOException There was a problem accessing the database table
     */
    public void addAccount(Account account) throws IOException;

    /**
     *  Updates an account in the account table.
     *
     *  @param  account     The account to update in the table.
     *  @throws IOException There was a problem accessing the database table
     */
    public void updateAccount(Account account) throws IOException;


/* *************************************** */
/* *** DAILY PRICES                    *** */
/* *************************************** */
    /**
     *  Retrieves the current prices for a given contract.
     *
     *  @param  id      the id of the contract
     *  @return         the current prices for the contract
     */
    public Prices getCurrentPrices(int id) throws IOException;

    /**
     *  Retrieves all prices for a given contract.
     *
     *  @param  id      the id of the contract
     *  @return         an iterator of the contract prices
     */
    public Iterator getPrices(int id) throws IOException;

    /**
     *  Adds a price interval.
     *
     *  @param  symbol      the commodity symbol
     *  @param  contract    the contract month
     *  @param  date        the date the prices are for
     *  @param  open        the open price
     *  @param  high        the high price
     *  @param  low         the low price
     *  @param  close       the close price
     *  @param  volume      the volume
     *  @param  openint     the open interest
     *
     *  @return     A threeway switch to indicate an add, update, or neither
     */
    public void addDailyPrice(Contract contract,
                              Prices prices) throws IOException;

    /**
     *  Retrieves the last date prices were loaded to the database for a given contract.
     *
     *  @param  contract    The contract to get the last date for
     *  @return     the most current date in the pricing table.
     */
    public java.util.Date getLastPriceDateLoaded(Contract contract) throws IOException;


/* *************************************** */
/* *** ORDERS                          *** */
/* *************************************** */
    /**
     *  Retrieves the last index number used in the order table
     *
     *  @return The most current index number in the order table
     */
    public long getLastOrderIndex() throws IOException;

    /**
     *  Retrieve all of the open orders for the given account number.
     *
     *  @param  acctNumber  The account number to retrieve orders for.
     *  @return     an iterator of open orders
     */
    public Iterator getOpenOrders(int acctNumber) throws IOException;

    /**
     *  Updates an entry for the orders.  If the order does not exist, it is added.
     *
     *  @param  id                  the id of this order
     *  @param  acctNumber          the account number of the owner of this order
     *  @param  buyOrder            true if buy order; false if sell order
     *  @param  date                date the transaction was recorded for
     *  @param  number              number of contracts in order
     *  @param  month               contract month in mmm yy format
     *  @param  commodityName       commodity name and exchange
     *  @param  desiredPrice        the desired price of the contract
     *  @param  stopPrice           the stop loss price of the contract
     *  @param  status              the status of the contract
     *  @param  note                the note of the order
     *  @param  actualPrice         the actual price of the contract
     *  @param  offsettingOrder     the offsetting order for this contract
     *  @param  dateConsidered      the date the order was considered
     *  @param  datePlaced          the date the order was placed
     *  @param  dateFilled          the date the order was filled
     *  @param  dateCancelPlaced    the date the cancel order was placed
     *  @param  dateCancelled       the date the order was cancelled
     *  @param  dateStopLoss        the date the stoploss was filled
     *
     *  @throws IOException         there was a problem accessing the database table
     */
    public void updateOrderEntry(long           id,
                                 int            acctNumber,
                                 boolean        buyOrder,
                                 java.util.Date date,
                                 Integer        number,
                                 String         month,
                                 String         commodityName,
                                 Double         desiredPrice,
                                 Double         stopPrice,
                                 String         status,
                                 Double         actualPrice,
                                 String         note,
                                 long           offsettingOrder,
                                 java.util.Date dateConsidered,
                                 java.util.Date datePlaced,
                                 java.util.Date dateFilled,
                                 java.util.Date dateCancelPlaced,
                                 java.util.Date dateCancelled,
                                 java.util.Date dateStopLoss) throws IOException;

    /**
     *  Deletes an an order entry with the given id.
     *
     *  @param  id              the id of this order
     *
     *  @throws IOException     There was a problem accessing the database table
     */
    public void deleteOrderEntry(long id) throws IOException;

/* *************************************** */
/* *** TEST XREF                       *** */
/* *************************************** */
    /**
     *  Clears the technical test xref table of all entries.
     *
     *  @throws IOException     There was a problem accessing the database table
     */
    public void clearTestXref() throws IOException;

    /**
     *  Clears the technical test xref table of the commodity.
     *
     *  @param  commodity   The commodity to clear the tests for.
     *  @throws IOException     There was a problem accessing the database table
     */
    public void clearTestXref(Commodity commodity) throws IOException;

    /**
     *  Clears the technical test xref table of the contract.
     *
     *  @param  contract    The contract to clear the tests for.
     *  @throws IOException     There was a problem accessing the database table
     */
    public void clearTestXref(Contract contract) throws IOException;

    /**
     *  Insert into the technical test xref table.
     *
     *  @param  contract    the contract the test is for
     *  @param  stats       the statistics for the test
     *  @param  testClass   the name of the test class
     *  @param  testId      the id of the test
     *  @throws IOException     There was a problem accessing the database table
     */
    public void insertTestXref(Contract contract, StatsAbstract stats, String testClass, int testId) throws IOException;

    /**
     *  Get the best technical test xref table entries for the contract.
     *
     *  @param  contract        the contract the test is for
     *  @param  termTop         the top of the time interval range
     *  @param  termBottom      the bottom of the time interval range
     *  @param  numberToReturn  the maximum number of rows to return
     *  @return                 returns a collection of the rows returned
     *  @throws IOException     There was a problem accessing the database table
     */
    public Collection getBestTestXref(Contract contract, int termTop, int termBottom, int numberToReturn) throws IOException;


/* *************************************** */
/* *** MONTH LOOKUP                    *** */
/* *************************************** */
    /**
     *  Loads all of the months from the month_lookup table and returns
     *  an itertor of Month objects.
     *
     *  @return Iterator of Month objects
     *  @throws SQLException    There was a problem accessing the database table
     */
    public Iterator getMonths() throws IOException;


/* *************************************** */
/* *** COMMODITY PRICE PAGES           *** */
/* *************************************** */
    /**
     *  Saves a commodity price location.
     *
     *  @param cpl      The CommodityPriceLocation to save.
     *  @throws IOException    There was a problem accessing the database table
     */
    public void putCommodityPageLocation(CommodityPriceLocation cpl) throws IOException;

    /**
     *  Saves the reordered CommodityPriceLocation after some have been removed.
     *
     *  @param page     The page number to be reordered.
     *  @param locs     The updated list of CommodityPriceLocations for a given page
     *  @throws IOException    There was a problem accessing the database table
     */
    public void removeCommodityPriceLocations(int page, Collection cpl) throws IOException;

    /**
     *  Deletes the entire page of CommodityPriceLocations.
     *
     *  @param page     The page number to be reordered.
     *  @throws IOException    There was a problem accessing the database table
     */
    public void deleteCommodityPriceLocationPage(int page) throws IOException;

    /**
     *  Loads all of the commodity price locations and returns
     *  an itertor of CommodityPriceLocation objects.
     *
     *  @return Iterator of CommodityPriceLocation objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public Iterator getCommodityPageLoc() throws IOException;

/* *************************************** */
/* *** TEST PARAMETERS                 *** */
/* *************************************** */
    /**
     *  Save test parameters.
     *
     *  @param sqlTable     The SQL to execute.
     *  @param columns      The table columns to use.
     *  @param columnValues The column values to set.
     *  @param contract     The contract that the stats are for.
     *  @param stats        The stats to retrieve information from.
     *  @param className    The name of the class for the test.
     *  @throws IOException    There was a problem accessing the database table
     */
    public void saveTestParameters(String sqlTable, String columns, String columnValues, Contract contract, StatsAbstract stats, String className) throws IOException;

}