/*  x Properly Documented
 */
package commodities.dataaccess.database;

import java.io.*;
import java.sql.*;
import java.util.*;

import commodities.account.*;
import commodities.commodity.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.order.*;
import commodities.price.*;
import commodities.tests.*;
import commodities.tests.technical.*;
import commodities.util.*;

import com.util.FormatDate;

/**
 *  The interface for all of the data accesses in the commodity system.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class AccessDataManager implements DataManagerInterface {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Standard format for all dates used in SQL */
    private static final String SQL_DATE_FORMAT = "MM/dd/yyyy";
    /** Connection for SQLs */
    private static Connection connection = null;
    /** Connection type for insert/update/delete or select SQLs */
    private static boolean connectUpdate = false;

    /** Data manager instance */
    private static AccessDataManager dbm;
    /** Database connection pool */
    private static DBConnectionPool dbcp;

    /** Prepared statement for inserting a commodity */
    private static String preparedInsertCommodity;
    /** Prepared statement for updating a commodity */
    private static String preparedUpdateCommodity;

    /** Prepared statement for selecting a contract */
    private static String preparedSelectContract;
    /** Prepared statement for selecting contracts */
    private static String preparedSelectContracts;
    /** Prepared statement for inserting a contract */
    private static String preparedInsertContract;
    /** Prepared statement for updating a contract */
    private static String preparedUpdateContract;
    /** Prepared statement for selecting a contract's last date */
    private static String preparedSelectContractLastDate;

    /** Prepared statement for inserting an account */
    private static String preparedInsertAccount;
    /** Prepared statement for updating an account */
    private static String preparedUpdateAccount;

    /** Prepared statement for selecting the current prices */
    private static String preparedSelectCurrentPrices;
    /** Prepared statement for selecting prices */
    private static String preparedSelectPrices;
    /** Prepared statement for selecting prices for a given date */
    private static String preparedSelectPrice;
    /** Prepared statement for inserting prices */
    private static String preparedInsertDailyPrice;
    /** Prepared statement for updating prices */
    private static String preparedUpdateDailyPrice;

    /** Prepared statement for inserting orders */
    private static String preparedInsertOrderEntry;
    /** Prepared statement for updating orders */
    private static String preparedUpdateOrderEntry;
    /** Prepared statement for deleting orders */
    private static String preparedDeleteOrderEntry;

    /** Prepared statement for selecting the best test cross-references */
    private static String preparedSelectBestTestXref;
    /** Prepared statement for inserting test cross-references */
    private static String preparedInsertTestXref;
    /** Prepared statement for deleting test cross-references. */
    private static String preparedDeleteTestXref;
    /** Prepared statement for deleting test cross-references for a commodity. */
    private static String preparedDeleteTestXrefCommodity;
    /** Prepared statement for deleting test cross-references for a contract. */
    private static String preparedDeleteTestXrefContract;

    /** Prepared statement for inserting a commodity price location */
    private static String preparedInsertCommodityPriceLocation;
    /** Prepared statement for deleting a commodity price location */
    private static String preparedDeleteCommodityPriceLocationPage;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /** Create an instance of the database data manager. */
    private AccessDataManager() {
//System.out.println("**********************************");
//System.out.println("** Construct AccessDataManager  **");
//System.out.println("**********************************");
/*
try {
    throw new Exception();
} catch (Exception e) {
    e.printStackTrace();
}
*/
        try {
            dbcp = DBConnectionPool.instance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    /**
     *  Get the singleton instance of the database data manager.
     *
     *  @return     Singleton instance of AccessDataManager
     */
    public static AccessDataManager instance() {
        if (dbm == null) {
            synchronized (AccessDataManager.class) {
                if (dbm == null) {
                    dbm = new AccessDataManager();
                }
            }
        }
        return dbm;
    }

/* *************************************** */
/* *** DataManagerInterface METHODS    *** */
/* *************************************** */
/* ***---------------------------------*** */
/* *** COMMODITY                       *** */
/* ***---------------------------------*** */
    /**
     *  Loads all of the commodities from the commodity table and returns
     *  an itertor of Commodity objects.
     *
     *  @return Iterator of Commodity objects
     *  @throws SQLException    There was a problem accessing the database table
     */
    public synchronized Iterator getCommodities() throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        Collection collect = new LinkedHashSet();
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select  id, exchange, name, unit_type, unit_size, unit_price, ");
            sb.append("        tick_size, tick_price, tick_daily_limit, Contract_Standard_Price, display_decimals, open_outcry_symbol, ");
            sb.append("        acm_symbol, trade_month_mask, database_symbol ");
            sb.append("from Commodity ");
//            if (commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
//                sb.append("  where inactive = 0 ");
//            }
            String sqlTxt = sb.toString();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            while (rs.next()) {
                collect.add(new Commodity(rs.getString("id"),
                                          rs.getString("exchange"),
                                          rs.getString("name"),
                                          rs.getString("unit_type"),
                                          rs.getInt("unit_size"),
                                          rs.getString("unit_price"),
                                          rs.getDouble("tick_size"),
                                          rs.getDouble("tick_price"),
                                          rs.getInt("tick_daily_limit"),
                                          rs.getInt("Contract_Standard_Price"),
                                          rs.getInt("display_decimals"),
                                          rs.getString("open_outcry_symbol"),
                                          rs.getString("acm_symbol"),
                                          rs.getInt("trade_month_mask"),
                                          rs.getString("database_symbol")));
            }
            rs.close();
            stmt.close();

            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Adds a commodity to the commodities table.
     *
     *  @param  commodity   The commodity to add to the table.
     *  @throws SQLException    There was a problem accessing the database table
     */
    public synchronized void addCommodity(Commodity commodity) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedInsertCommodity == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("insert into Commodity(Id, Exchange, Name, Unit_Type, Unit_Size, Unit_Price, Tick_Size, ");
                sb.append("Tick_Price, Tick_Daily_Limit, Contract_Standard_Price, Display_Decimals, Open_Outcry_Symbol, ");
                sb.append("ACM_Symbol, Trade_Month_Mask, Database_Symbol) ");
                sb.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
                preparedInsertCommodity = sb.toString();
            }

            PreparedStatement stmt = connection.prepareStatement(preparedInsertCommodity);

            stmt.setString(1,  commodity.getSymbol());
            stmt.setString(2,  commodity.getExchange());
            stmt.setString(3,  commodity.getName());
            stmt.setString(4,  commodity.getUnitType());
            stmt.setInt   (5,  commodity.getUnitSize());
            stmt.setString(6,  commodity.getUnitPrice());
            stmt.setDouble(7,  commodity.getTickSize());
            stmt.setDouble(8,  commodity.getTickPrice());
            stmt.setInt   (9,  commodity.getTickDailyLimit());
            stmt.setInt   (10, commodity.getMargin());
            stmt.setInt   (11, commodity.getDisplayDecimals());
            stmt.setString(12, commodity.getOpenOutcrySymbol());
            stmt.setString(13, commodity.getAcmSymbol());
            stmt.setInt   (14, commodity.getTradeMonthMask());
            stmt.setString(15, commodity.getDatabaseSymbol());

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Updates a commodity in the commodities table.
     *
     *  @param  commodity   The commodity to add to the table.
     *  @throws SQLException    There was a problem accessing the database table
     */
    public synchronized void updateCommodity(Commodity commodity) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedUpdateCommodity == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("update Commodity ");
                sb.append("   set Exchange=?, ");
                sb.append("       Name=?, ");
                sb.append("       Unit_Type=?, ");
                sb.append("       Unit_Size=?, ");
                sb.append("       Unit_Price=?, ");
                sb.append("       Tick_Size=?, ");
                sb.append("       Tick_Price=?, ");
                sb.append("       Tick_Daily_Limit=?, ");
                sb.append("       Contract_Standard_Price=?, ");
                sb.append("       Display_Decimals=?, ");
                sb.append("       Open_Outcry_Symbol=?, ");
                sb.append("       ACM_Symbol=?, ");
                sb.append("       Trade_Month_Mask=?, ");
                sb.append("       Database_Symbol=? ");
                sb.append(" where Id = ? ");

                preparedUpdateCommodity = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedUpdateCommodity);

            stmt.setString(1,  commodity.getExchange());
            stmt.setString(2,  commodity.getName());
            stmt.setString(3,  commodity.getUnitType());
            stmt.setInt   (4,  commodity.getUnitSize());
            stmt.setString(5,  commodity.getUnitPrice());
            stmt.setDouble(6,  commodity.getTickSize());
            stmt.setDouble(7,  commodity.getTickPrice());
            stmt.setInt   (8,  commodity.getTickDailyLimit());
            stmt.setInt   (9,  commodity.getMargin());
            stmt.setInt   (10, commodity.getDisplayDecimals());
            stmt.setString(11, commodity.getOpenOutcrySymbol());
            stmt.setString(12, commodity.getAcmSymbol());
            stmt.setInt   (13, commodity.getTradeMonthMask());
            stmt.setString(14, commodity.getDatabaseSymbol());

            stmt.setString(15,  commodity.getSymbol()); // where clause

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }


/* ***---------------------------------*** */
/* *** CONTRACT                        *** */
/* ***---------------------------------*** */
    /**
     *  Retrieves a contract by symbol and contract month.
     *
     *  @param  symbol          the symbol of the commodity to retrieve.
     *  @param  contractMonth   the contract month of the commodity to retrieve
     *  @return         The contract for the requested commodity and contract month.
     */
    public synchronized Contract getContractByMonth(String symbol, String contractMonth) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            if (preparedSelectContract == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select id, commodity_id, contract_sort, price ");
                sb.append("from contract ");
                sb.append("where commodity_id = ? ");
                sb.append("  and contract_sort = ? ");
//                if (commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
//                    sb.append("  and inactive = 0 ");
//                }
                preparedSelectContract = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectContract);

            stmt.setString(1, symbol);
            stmt.setString(2, contractMonth);

            ResultSet rs = stmt.executeQuery();
            Contract contract = null;
            if (rs.next()) {
                contract = new Contract(rs.getInt("id"),
                                        rs.getString("commodity_id"),
                                        rs.getString("contract_sort"),
                                        rs.getInt("price"));
            }
            rs.close();
            stmt.close();
            return contract;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }
    }

    /**
     *  Retrieves all contracts by symbol.
     *
     *  @param  symbol  the symbol of the commodity to retrieve.
     *  @return         an iterator of the contracts for the commodity
     */
    public synchronized Iterator getContracts(String symbol) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            if (preparedSelectContracts == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select id, commodity_id, contract_sort, price ");
                sb.append("from contract ");
                sb.append("where commodity_id = ? ");
                sb.append("  and contract_sort >= ? ");
//                if (commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
//                    sb.append("  and inactive = 0 ");
//                }

                preparedSelectContracts = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectContracts);

            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            String contractSort = calendar.get(CommodityCalendar.YEAR) + Month.byNumber(calendar.get(CommodityCalendar.MONTH)).getSymbol();

            stmt.setString(1, symbol);
            stmt.setString(2, contractSort);

            ResultSet rs = stmt.executeQuery();
            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new Contract(rs.getInt("id"),
                                        rs.getString("commodity_id"),
                                        rs.getString("contract_sort"),
                                        rs.getInt("price")));
            }
            rs.close();
            stmt.close();
            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Add a new contract by symbol and contract month.
     *
     *  @param  symbol          the symbol of the commodity to retrieve.
     *  @param  contractMonth   the contract month of the commodity to retrieve
     */
    public synchronized void addContract(String symbol, String contractMonth) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedInsertContract == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("insert into contract (commodity_id, contract_month, contract_sort) ");
                sb.append("values (?, ?, ?) ");
                preparedInsertContract = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedInsertContract);

            stmt.setString(1, symbol);
            stmt.setString(2, contractMonth.substring(4) + contractMonth.substring(0,4));
            stmt.setString(3, contractMonth);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Contract already exists: " + symbol + " " + contractMonth);
//            e.printStackTrace();
        }
    }

    /**
     *  Add an existing contract.
     *
     *  @param  contract    the contract to update
     */
    public synchronized void updateContract(Contract contract) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedUpdateContract == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("update Contract ");
                sb.append("   set Price=? ");
                sb.append(" where Commodity_Id = ? ");
                sb.append("   and Contract_Sort = ? ");

                preparedUpdateContract = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedUpdateContract);

            stmt.setInt(1, contract.getMargin());
            stmt.setString(2, contract.getSymbol());
            stmt.setString(3, contract.getMonth());

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

/* ***---------------------------------*** */
/* *** ACCOUNT                         *** */
/* ***---------------------------------*** */
    /**
     *  Retrieves the last index number used in the account table
     *
     *  @return The most current index number in the account table
     */
    public synchronized long getLastAccountIndex() throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            long index = -1;

            StringBuffer sb = new StringBuffer();
            sb.append("select max(id) " +
                      "from Account ");
            String sqlTxt = sb.toString();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            if (rs.next()) {
                index = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            return index;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Loads all of the accounts from the account table and provides
     *  an itertor of Account objects.
     *
     *  @return Iterator of Account objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized Iterator getAccounts() throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        Collection collect = new LinkedHashSet();
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select id, first_name, last_name, capital ");
            sb.append("from Account ");
            sb.append("order by last_name, first_name, id ");
            String sqlTxt = sb.toString();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            while (rs.next()) {
                collect.add(new Account(rs.getInt("id"),
                                        rs.getString("first_name"),
                                        rs.getString("last_name"),
                                        rs.getDouble("capital")));
            }
            rs.close();
            stmt.close();

            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Adds an account to the account table.
     *
     *  @param  account     The account to add to the table.
     *  @throws IOException There was a problem accessing the database table
     */
    public synchronized void addAccount(Account account) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedInsertAccount == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("insert into Account(id, first_name, last_name, capital) ");
                sb.append("values (?, ?, ?, ?) ");
                preparedInsertAccount = sb.toString();
            }

            PreparedStatement stmt = connection.prepareStatement(preparedInsertAccount);

            stmt.setInt(1,  account.getAccountNumber());
            stmt.setString(2,  account.getFirstName());
            stmt.setString(3,  account.getLastName());
            stmt.setDouble(4,  account.getCapital());

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Updates an account in the account table.
     *
     *  @param  account     The account to update in the table.
     *  @throws IOException There was a problem accessing the database table
     */
    public synchronized void updateAccount(Account account) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedUpdateAccount == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("update Account ");
                sb.append("   set first_name=?, ");
                sb.append("       last_name=?, ");
                sb.append("       capital=? ");
                sb.append(" where Id = ? ");

                preparedUpdateAccount = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedUpdateAccount);

            stmt.setString(1,  account.getFirstName());
            stmt.setString(2,  account.getLastName());
            stmt.setDouble(3,  account.getCapital());

            stmt.setInt(4,  account.getAccountNumber());  // where clause

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

/* ***---------------------------------*** */
/* *** DAILY PRICES                    *** */
/* ***---------------------------------*** */
    /**
     *  Retrieves the current prices for a given contract.
     *
     *  @param  id      the id of the contract
     *  @return         the current prices for the contract
     */
    public synchronized Prices getCurrentPrices(int id) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            if (preparedSelectCurrentPrices == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select price_date, open_tick, high_tick, low_tick, close_tick, volume, open_interest ");
                sb.append("from Daily_Prices ");
                sb.append("where Contract_Id = ? ");
                sb.append("order by price_date desc");
                preparedSelectCurrentPrices = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectPrices);

            java.util.Date today = ((CommodityCalendar)CommodityCalendar.getInstance()).getTime();
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            Prices prices = null;
            if (rs.next()) {
                prices = new Prices(rs.getDate("price_date"),
                                    rs.getDouble("open_tick"),
                                    rs.getDouble("high_tick"),
                                    rs.getDouble("low_tick"),
                                    rs.getDouble("close_tick"),
                                    rs.getLong("volume"),
                                    rs.getLong("open_interest"));
            } else {
                prices = new Prices(today,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0,
                                    0);
            }
            rs.close();
            return prices;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
//        return null;
    }

    /**
     *  Retrieves all prices for a given contract.
     *
     *  @param  id      the id of the contract
     *  @return         an iterator of the contract prices
     */
    public synchronized Iterator getPrices(int id) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            if (preparedSelectPrices == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select price_date, open_tick, high_tick, low_tick, close_tick, volume, open_interest " +
                          "from Daily_Prices " +
                          "where Contract_Id = ? " +
                          "order by price_date desc ");
                preparedSelectPrices = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectPrices);

            java.util.Date today = ((CommodityCalendar)CommodityCalendar.getInstance()).getTime();
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            Collection collect = new LinkedHashSet();

            while (rs.next()) {
                collect.add(new Prices(rs.getDate("price_date"),
                                       rs.getDouble("open_tick"),
                                       rs.getDouble("high_tick"),
                                       rs.getDouble("low_tick"),
                                       rs.getDouble("close_tick"),
                                       rs.getLong("volume"),
                                       rs.getLong("open_interest")));
            }

            rs.close();
            stmt.close();
            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Retrieves the prices for a given contract on a given date.
     *
     *  @param  contract    the contract to get the prices for
     *  @param  priceDate   the date of the prices to retrieve
     *  @return             the prices
     */
    public synchronized Prices getPrice(Contract contract, java.util.Date priceDate) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            if (preparedSelectPrice == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select price_date, open_tick, high_tick, low_tick, close_tick, volume, open_interest " +
                          "from Daily_Prices " +
                          "where Contract_Id = ? " +
                          "  and price_date = ? ");
                preparedSelectPrice = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectPrice);

            stmt.setInt(1, contract.getId());
            stmt.setString(2, FormatDate.formatDate(priceDate, SQL_DATE_FORMAT));

            ResultSet rs = stmt.executeQuery();
            Prices prices = null;

            if (rs.next()) {
                prices = new Prices(rs.getDate("price_date"),
                                    rs.getDouble("open_tick"),
                                    rs.getDouble("high_tick"),
                                    rs.getDouble("low_tick"),
                                    rs.getDouble("close_tick"),
                                    rs.getLong("volume"),
                                    rs.getLong("open_interest"));
            }

            rs.close();
            stmt.close();
            return prices;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Adds a price interval.
     *
     *  @param  contract    the contract month
     *  @param  prices      the prices to add
     *
     *  @return     A threeway switch to indicate an add, update, or neither
     */
    /*
     *  This is coded with using a select of the current prices to speed up the process of
     *  inserting or updating the record.  In this case, we use exception processing of the
     *  select to determine that an insert is required.
     *  WARNING: this is not necessarially good programming techinque.
     */
    public synchronized void addDailyPrice(Contract contract,
                                                Prices prices) throws IOException {
        Prices existingPrices = null;

        try {
            existingPrices = getPrice(contract, prices.getDate());

            if ((existingPrices.getOpen()           != prices.getOpen()) ||
                (existingPrices.getHigh()           != prices.getHigh()) ||
                (existingPrices.getLow()            != prices.getLow()) ||
                (existingPrices.getClose()          != prices.getClose()) ||
                ((int)existingPrices.getVolume()    != (int)prices.getVolume()) ||
                ((int)existingPrices.getInterest()  != (int)prices.getInterest())) {

                try {
                    if (connection == null || connection.isClosed()) {
                        connection = (Connection)dbcp.createObject();
                    }
                    connectUpdate = true;

                    if (preparedUpdateDailyPrice == null) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("update Daily_Prices " +
                                  "   set open_tick = ?, " +
                                  "       high_tick = ?, " +
                                  "       low_tick = ?, " +
                                  "       close_tick = ?, " +
                                  "       volume = ?, " +
                                  "       open_interest = ? " +
                                  " where Contract_Id = ? " +
                                  "   and Price_Date = ? ");

                        preparedUpdateDailyPrice = sb.toString();
                    }
                    PreparedStatement stmt = connection.prepareStatement(preparedUpdateDailyPrice);

                    stmt.setDouble(1, prices.getOpen());
                    stmt.setDouble(2, prices.getHigh());
                    stmt.setDouble(3, prices.getLow());
                    stmt.setDouble(4, prices.getClose());
                    stmt.setInt   (5, (int)prices.getVolume());
                    stmt.setInt   (6, (int)prices.getInterest());
                    stmt.setInt   (7, contract.getId());
                    stmt.setString(8, FormatDate.formatDate(prices.getDate(), SQL_DATE_FORMAT));

                    stmt.executeUpdate();
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new IOException(ex.toString());
                }
            }

        } catch (Exception e) {
            if (existingPrices == null) {
                try {

                    if (connection == null || connection.isClosed()) {
                        connection = (Connection)dbcp.createObject();
                    }
                    connectUpdate = true;

                    if (preparedInsertDailyPrice == null) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("insert into Daily_Prices(Contract_Id, Price_Date, open_tick, high_tick, low_tick, close_tick, volume, open_interest) " +
                                  "values (?, ?, ?, ?, ?, ?, ?, ?) ");

                        preparedInsertDailyPrice = sb.toString();
                    }
                    PreparedStatement stmt = connection.prepareStatement(preparedInsertDailyPrice);

                System.out.println("*" + contract.getSymbol() + "* *" + contract.getMonthFormatted() + "* *" + prices.getDate() + "*");

                    stmt.setInt   (1, contract.getId());
                    stmt.setString(2, FormatDate.formatDate(prices.getDate(), SQL_DATE_FORMAT));
                    stmt.setDouble(3, prices.getOpen());
                    stmt.setDouble(4, prices.getHigh());
                    stmt.setDouble(5, prices.getLow());
                    stmt.setDouble(6, prices.getClose());
                    stmt.setInt  (7, (int)prices.getVolume());
                    stmt.setInt  (8, (int)prices.getInterest());

                    stmt.executeUpdate();
                    stmt.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new IOException(ex.toString());
                }
            } else {
                throw new IOException(e.toString());
            }
        }
    }

    /**
     *  Retrieves the last date prices were loaded to the database.
     *
     *  @return     the most current date in the pricing table.
     */
/*
    public synchronized java.util.Date getLastPriceDateLoaded() throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();
            java.sql.Date date = null;

            StringBuffer sb = new StringBuffer();
            sb.append("select max(price_date) " +
                      "from Daily_Prices ");
            String sqlTxt = sb.toString();
            CallableStatement sql = connect.prepareCall(sqlTxt);

            ResultSet rs = sql.executeQuery();
            if (rs.next()) {
                date = rs.getDate(1);
            }
            rs.close();
            return date;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Retrieves the last date prices were loaded to the database for a given contract.
     *
     *  @param  contract    The contract to get the last date for
     *  @return     the most current date in the pricing table.
     */
    public synchronized java.util.Date getLastPriceDateLoaded(Contract contract) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            java.sql.Date date = null;

            if (preparedSelectContractLastDate == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select max(price_date) " +
                          "from Daily_Prices " +
                          "where Contract_Id = ? " +
                          "  and open_tick > 0 " +
                          "  and high_tick > 0 " +
                          "  and low_tick > 0 " +
                          "  and close_tick > 0 " +
                          "  and volume <> 0 " +
                          "  and open_interest <> 0 ");

                preparedSelectContractLastDate = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectContractLastDate);

            stmt.setInt(1,  contract.getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                date = rs.getDate(1);
            }
            rs.close();
            stmt.close();
            return date;

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }


/* ***---------------------------------*** */
/* *** ORDERS                          *** */
/* ***---------------------------------*** */
    /**
     *  Retrieves the last index number used in the order table
     *
     *  @return The most current index number in the order table
     */
    public synchronized long getLastOrderIndex() throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            long index = -1;

            StringBuffer sb = new StringBuffer();
            sb.append("select max(id) " +
                      "from Orders ");
            String sqlTxt = sb.toString();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            if (rs.next()) {
                index = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            return index;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Retrieve all of the open orders for the given account number.
     *
     *  @param  acctNumber  The account number to retrieve orders for.
     *  @return     an iterator of open orders
     */
    public synchronized Iterator getOpenOrders(int acctNumber) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select Id, " +
                            " Buy_Sell_Ind, " +
                            " Date_Entered, " +
                            " Nbr_Contracts, " +
                            " Contract_Month, " +
                            " Commodity_Name, " +
                            " Desired_Price, " +
                            " Stop_Price, " +
                            " Status, " +
                            " Actual_Price, " +
                            " Note_Text, " +
                            " Offsetting_Order, " +
                            " Date_Considered, " +
                            " Date_Placed, " +
                            " Date_Filled, " +
                            " Date_Cancel_Placed, " +
                            " Date_Cancelled, " +
                            " Date_Stop_Loss " +
                        "from Orders " +
                       "where Account_Id = " + acctNumber + " " +
                       "  and Offsetting_Order < 0 " +
                       "order by Commodity_Name, Contract_Month, Date_Entered ");

            String sqlTxt = sb.toString();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new ContractOrder(
                                    rs.getLong  ("Id"),
                                    acctNumber,
                                    ("B".equals(rs.getString("Buy_Sell_Ind")))?true:false,
                                    rs.getDate  ("Date_Entered"),
                                    new Integer(rs.getInt   ("Nbr_Contracts")),
                                    rs.getString("Contract_Month").trim(),
                                    rs.getString("Commodity_Name").trim(),
                                    new Double(rs.getDouble("Desired_Price")),
                                    new Double (rs.getDouble("Stop_Price")),
                                    rs.getString("Status").trim(),
                                    new Double (rs.getDouble("Actual_Price")),
                                    rs.getString("Note_Text").trim(),
                                    rs.getLong  ("Offsetting_Order"),
                                    rs.getDate  ("Date_Considered"),
                                    rs.getDate  ("Date_Placed"),
                                    rs.getDate  ("Date_Filled"),
                                    rs.getDate  ("Date_Cancel_Placed"),
                                    rs.getDate  ("Date_Cancelled"),
                                    rs.getDate  ("Date_Stop_Loss"),
                                    true));
            }
            rs.close();
            stmt.close();

            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

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
    public synchronized void updateOrderEntry(long           id,
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
                                              java.util.Date dateStopLoss) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedUpdateOrderEntry == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("update Orders " +
                          "   set Account_Id=?, " +
                          "       Buy_Sell_Ind=?, " +
                          "       Date_Entered=?, " +
                          "       Nbr_Contracts=?, " +
                          "       Contract_Month=?, " +
                          "       Commodity_Name=?, " +
                          "       Desired_Price=?, " +
                          "       Stop_Price=?, " +
                          "       Status=?, " +
                          "       Actual_Price=?, " +
                          "       Note_Text=?, " +
                          "       Offsetting_Order=?, " +
                          "       Date_Considered=?, " +
                          "       Date_Placed=?, " +
                          "       Date_Filled=?, " +
                          "       Date_Cancel_Placed=?, " +
                          "       Date_Cancelled=?, " +
                          "       Date_Stop_Loss=? " +
                          " where Id = ? ");

                preparedUpdateOrderEntry = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedUpdateOrderEntry);

            stmt.setInt   ( 1, acctNumber);
            stmt.setString( 2, buyOrder?"B":"S");
            stmt.setString( 3, FormatDate.formatDate(date, SQL_DATE_FORMAT));
            stmt.setInt   ( 4, number.intValue());
            stmt.setString( 5, month);
            stmt.setString( 6, commodityName);
            stmt.setDouble( 7, desiredPrice.doubleValue());
            stmt.setDouble( 8, stopPrice.doubleValue());
            stmt.setString( 9, status);
            stmt.setDouble(10, actualPrice.doubleValue());
            stmt.setString(11, note);
            stmt.setInt   (12, (int)offsettingOrder);

            if (dateConsidered == null) {
                stmt.setNull(13, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(13, FormatDate.formatDate(dateConsidered, SQL_DATE_FORMAT));
            }

            if (datePlaced == null) {
                stmt.setNull(14, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(14, FormatDate.formatDate(datePlaced, SQL_DATE_FORMAT));
            }

            if (dateFilled == null) {
                stmt.setNull(15, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(15, FormatDate.formatDate(dateFilled, SQL_DATE_FORMAT));
            }

            if (dateCancelPlaced == null) {
                stmt.setNull(16, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(16, FormatDate.formatDate(dateCancelPlaced, SQL_DATE_FORMAT));
            }

            if (dateCancelled == null) {
                stmt.setNull(17, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(17, FormatDate.formatDate(dateCancelled, SQL_DATE_FORMAT));
            }

            if (dateStopLoss == null) {
                stmt.setNull(18, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(18, FormatDate.formatDate(dateStopLoss, SQL_DATE_FORMAT));
            }

            stmt.setInt  (19, (int)id);

            int count = stmt.executeUpdate();
            stmt.close();
            if (count == 0) {
                throw new SQLException();
            }

        } catch (SQLException e) {
            try {
                if (connection == null || connection.isClosed()) {
                    connection = (Connection)dbcp.createObject();
                }
                connectUpdate = true;

                if (preparedInsertOrderEntry == null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("insert into Orders( " +
                              "       Id, " +
                             "       Account_Id, " +
                              "       Buy_Sell_Ind, " +
                              "       Date_Entered, " +
                              "       Nbr_Contracts, " +
                              "       Contract_Month, " +
                              "       Commodity_Name, " +
                              "       Desired_Price, " +
                              "       Stop_Price, " +
                              "       Status, " +
                              "       Actual_Price, " +
                              "       Note_Text, " +
                              "       Offsetting_Order, " +
                              "       Date_Considered, " +
                              "       Date_Placed, " +
                              "       Date_Filled, " +
                              "       Date_Cancel_Placed, " +
                              "       Date_Cancelled, " +
                              "       Date_Stop_Loss) " +
                              " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
                    preparedInsertOrderEntry = sb.toString();
                }
                PreparedStatement stmt = connection.prepareStatement(preparedInsertOrderEntry);

                stmt.setInt   ( 1, (int)id);
                stmt.setInt   ( 2, acctNumber);
                stmt.setString( 3, buyOrder?"B":"S");
                stmt.setString( 4, FormatDate.formatDate(date, SQL_DATE_FORMAT));
                stmt.setInt   ( 5, number.intValue());
                stmt.setString( 6, month);
                stmt.setString( 7, commodityName);
                stmt.setDouble( 8, desiredPrice.doubleValue());
                stmt.setDouble( 9, stopPrice.doubleValue());
                stmt.setString(10, status);
                stmt.setDouble(11, actualPrice.doubleValue());
                stmt.setString(12, note);
                stmt.setInt   (13, (int)offsettingOrder);

                if (dateConsidered == null) {
                    stmt.setNull(14, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(14, FormatDate.formatDate(dateConsidered, SQL_DATE_FORMAT));
                }

                if (datePlaced == null) {
                    stmt.setNull(15, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(15, FormatDate.formatDate(datePlaced, SQL_DATE_FORMAT));
                }

                if (dateFilled == null) {
                    stmt.setNull(16, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(16, FormatDate.formatDate(dateFilled, SQL_DATE_FORMAT));
                }

                if (dateCancelPlaced == null) {
                    stmt.setNull(17, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(17, FormatDate.formatDate(dateCancelPlaced, SQL_DATE_FORMAT));
                }

                if (dateCancelled == null) {
                    stmt.setNull(18, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(18, FormatDate.formatDate(dateCancelled, SQL_DATE_FORMAT));
                }

                if (dateStopLoss == null) {
                    stmt.setNull(19, java.sql.Types.VARCHAR);
                } else {
                    stmt.setString(19, FormatDate.formatDate(dateStopLoss, SQL_DATE_FORMAT));
                }

                stmt.executeUpdate();

            } catch (SQLException ex) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                throw new IOException(ex.toString());
            }
        }
    }

    /**
     *  Deletes an an order entry with the given id.
     *
     *  @param  id              the id of this order
     *
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized void deleteOrderEntry(long id) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedDeleteOrderEntry == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("delete from Orders " +
                          "where Id = ? ");

                preparedDeleteOrderEntry = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedDeleteOrderEntry);

            stmt.setInt  (1, (int)id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

/* ***---------------------------------*** */
/* *** TEST XREF                       *** */
/* ***---------------------------------*** */
    /**
     *  Clears the technical test xref table of all entries.
     *
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized void clearTestXref() throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedDeleteTestXref == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("delete from Test_Xref ");

                preparedDeleteTestXref = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedDeleteTestXref);

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Clears the technical test xref table of the commodity.
     *
     *  @param  commodity   The commodity to clear the tests for.
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized void clearTestXref(Commodity commodity) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedDeleteTestXrefCommodity == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("delete from Test_Xref " +
                          "where Commodity_Id = ? ");

                preparedDeleteTestXrefCommodity = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedDeleteTestXrefCommodity);

            stmt.setString(1, commodity.getSymbol());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Clears the technical test xref table of the contract.
     *
     *  @param  contract    The contract to clear the tests for.
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized void clearTestXref(Contract contract) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedDeleteTestXrefContract == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("delete from Test_Xref " +
                          "where Commodity_Id = ? " +
                          "  and Contract_Month = ? ");

                preparedDeleteTestXrefContract = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedDeleteTestXrefContract);

            stmt.setString(1, contract.getCommodity().getSymbol());
            stmt.setString(2, contract.getMonth());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }


    /**
     *  Insert into the technical test xref table.
     *
     *  @param  contract    the contract the test is for
     *  @param  stats       the statistics for the test
     *  @param  testClass   the name of the test class
     *  @param  testId      the id of the test
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized void insertTestXref(Contract contract, StatsAbstract stats, String testClass, int testId) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedInsertTestXref == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("insert into Test_Xref(commodity_id, contract_month, test_class, test_id, test_interval, success_ratio, profit_loss) " +
                          "values (?, ?, ?, ?, ?, ?, ?) ");

                preparedInsertTestXref = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedInsertTestXref);
/*
System.out.println("AccessDataManager.insertTestXref() - contract.getSymbol() = " + contract.getSymbol());
System.out.println("AccessDataManager.insertTestXref() - contract.getMonth() = " + contract.getMonth());
System.out.println("AccessDataManager.insertTestXref() - testClass = " + testClass);
System.out.println("AccessDataManager.insertTestXref() - testId = " + testId);
System.out.println("AccessDataManager.insertTestXref() - stats.getInterval() = " + stats.getInterval());
System.out.println("AccessDataManager.insertTestXref() - stats.getCountRatio() = " + stats.getCountRatio());
System.out.println("AccessDataManager.insertTestXref() - stats.getProfit() = " + stats.getProfit());
System.out.println("AccessDataManager.insertTestXref() - *** variable list complete ***");
*/
            stmt.setString(1, contract.getSymbol());
            stmt.setString(2, contract.getMonth());
            stmt.setString(3, testClass);
            stmt.setInt   (4, testId);
            stmt.setInt   (5, stats.getInterval());
            stmt.setDouble(6, stats.getCountRatio());
            stmt.setDouble(7, stats.getProfit());

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }
    }

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
    public synchronized Collection getBestTestXref(Contract contract, int termTop, int termBottom, int numberToReturn) throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        Collection negProfits = new LinkedList();
        try {
            if (preparedSelectBestTestXref == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("select test_class, test_id, success_ratio, profit_loss " +
                          "from test_xref " +
                          "where Commodity_Id = ? " +
                          "  and Contract_Month = ? " +
                          "  and Test_Interval > ? " +
                          "  and Test_Interval <= ? " +
                          "order by Success_Ratio desc, Profit_Loss desc ");

                preparedSelectBestTestXref = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedSelectBestTestXref);

            stmt.setString(1, contract.getSymbol());
            stmt.setString(2, contract.getMonth());
            stmt.setInt   (3, termBottom);
            stmt.setInt   (4, termTop);

            ResultSet rs = stmt.executeQuery();

            Collection collect = new LinkedList();
            int count = 0;
            while (rs.next() && count < numberToReturn) {
                String testClass    = rs.getString(1).trim();
                int    testId       = rs.getInt(2);
                double successRatio = rs.getDouble(3);
                double profitLoss   = rs.getDouble(4);

                if (profitLoss > 0.0) {
                    collect.add(new TestIdentifier(testClass, testId));
                    count++;
                } else {
                    negProfits.add(new TestIdentifier(testClass, testId));
                }
            }
            rs.close();
            stmt.close();

            Iterator it = negProfits.iterator();
            while (it.hasNext() && (count < numberToReturn)) {
                collect.add(it.next());
                count++;
            }
            return collect;

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }


/* ***---------------------------------*** */
/* *** MONTH LOOKUP                    *** */
/* ***---------------------------------*** */
    /**
     *  Loads all of the months from the month_lookup table and returns
     *  an itertor of Month objects.
     *
     *  @return Iterator of Month objects
     *  @throws SQLException    There was a problem accessing the database table
     */
//    public synchronized Iterator getMonths() throws IOException {
    public Iterator getMonths() throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT name, abbrev, number, symbol " +
                      "FROM month_lookup ");

            String sqlTxt = sb.toString();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new Month(rs.getString("Name"),
                                      rs.getString("Abbrev"),
                                      rs.getInt("Number"),
                                      rs.getString("Symbol")));
            }
            rs.close();
            stmt.close();

            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }


/* ***---------------------------------*** */
/* *** COMMODITY PRICE PAGES           *** */
/* ***---------------------------------*** */
    /**
     *  Saves a commodity price location.
     *
     *  @param cpl      The CommodityPriceLocation to save.
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized void putCommodityPageLocation(CommodityPriceLocation cpl) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedInsertCommodityPriceLocation == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("insert into Commodity_Price_Pages(page, loc, symbol, title) " +
                          "values (?, ?, ?, ?) ");

                preparedInsertCommodityPriceLocation = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedInsertCommodityPriceLocation);

            stmt.setInt(1, cpl.getPage());
            stmt.setInt(2, cpl.getLocation());
            stmt.setString(3, cpl.getSymbol());
            stmt.setString(4, cpl.getTitle());

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Saves the reordered CommodityPriceLocation after some have been removed.
     *
     *  @param page     The page number to be reordered.
     *  @param locs      The updated list of CommodityPriceLocations for a given page
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized void removeCommodityPriceLocations(int page, Collection locs) throws IOException {
        deleteCommodityPriceLocationPage(page);
        Iterator it = locs.iterator();
        int i = 0;
        while (it.hasNext()) {
            CommodityPriceLocation cpl = (CommodityPriceLocation)it.next();
            cpl.setLocation(i++);
            putCommodityPageLocation(cpl);
        }
    }

    /**
     *  Deletes the entire page of CommodityPriceLocations.
     *
     *  @param page     The page number to be reordered.
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized void deleteCommodityPriceLocationPage(int page) throws IOException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
            }
            connectUpdate = true;

            if (preparedDeleteCommodityPriceLocationPage == null) {
                StringBuffer sb = new StringBuffer();
                sb.append("delete from Commodity_Price_Pages " +
                          "where page = ? ");

                preparedDeleteCommodityPriceLocationPage = sb.toString();
            }
            PreparedStatement stmt = connection.prepareStatement(preparedDeleteCommodityPriceLocationPage);

            stmt.setInt(1, page);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Loads all of the commodity price locations and returns
     *  an itertor of CommodityPriceLocation objects.
     *
     *  @return Iterator of CommodityPriceLocation objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized Iterator getCommodityPageLoc() throws IOException {
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
        }

        try {
            StringBuffer sb = new StringBuffer();

            sb.append("select  page, loc, symbol, title " +
                      "from Commodity_Price_Pages " +
                      "order by page, loc ");

            String sqlTxt = sb.toString();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new CommodityPriceLocation
                                        (rs.getInt("page"),
                                         rs.getInt("loc"),
                                         rs.getString("symbol"),
                                         rs.getString("title")));
            }
            rs.close();
            stmt.close();

            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
   }

/* ***---------------------------------*** */
/* *** TEST PARAMETERS                 *** */
/* ***---------------------------------*** */
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
    /* do not synchronize this method */
    public void saveTestParameters(String sqlTable, String columns, String columnValues, Contract contract, StatsAbstract stats, String className) throws IOException {
//jrt
    int tries = 0;
    while (tries < 5) {
    synchronized (AccessDataManager.class) {
        int testId = 0;
        try {
            if (connectUpdate && connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
            if (connection == null || connection.isClosed()) {
                connection = (Connection)dbcp.createObject();
                connectUpdate = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Statement stmt = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select max(id) " +
                      "from " + sqlTable + " ");
            String sqlTxt = sb.toString();

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            if (rs.next()) {
                testId = rs.getInt(1);
                testId++;
            }
            rs.close();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            try { stmt.close(); } catch (Exception ex) {}
        }

        boolean tryAgain = true;
        while (tryAgain) {
            try {
                if (connection == null || connection.isClosed()) {
System.out.println("creating new object");
                    connection = (Connection)dbcp.createObject();
               }
                connectUpdate = true;

                StringBuffer sb = new StringBuffer();
                sb.append("insert into " + sqlTable + " " +
                          "       (id, commodity_id, contract_month, " +
                          "       " + columns + ", " +
                          "        test_interval, success_ratio, profit_loss, profit_count, profit_total, loss_count, loss_total, largest_profit, largest_loss) " +
                          "values (" + testId + ", " +
                          "        '" + contract.getSymbol() + "', " +
                          "        '" + contract.getMonth() + "', " +
                          "        " + columnValues + ", " +
                          "        " + stats.getInterval() + ", " +
                          "        " + stats.getCountRatio() + ", " +
                          "        " + stats.getProfit() + ", " +
                          "        " + stats.getProfitCount() + ", " +
                          "        " + stats.getProfitTotal() + ", " +
                          "        " + stats.getLossCount() + ", " +
                          "        " + stats.getLossTotal() + ", " +
                          "        " + stats.getLargestProfit() + ", " +
                          "        " + stats.getLargestLoss() +
                          "       )");
                String sqlTxt = sb.toString();

                stmt = connection.createStatement();
                stmt.execute(sqlTxt);
System.out.println("successful update - " + contract.getName() + " - " + sqlTable + " - interval = " + stats.getInterval());
//                break;
        insertTestXref(contract, stats, className, testId);
        return;
            } catch (SQLException e) {
System.out.println("locking error - " + contract.getName() + " - " + sqlTable + " - interval = " + stats.getInterval() + " - tries = " + tries);
//                if ((tries > 5) || (e.toString().indexOf("currently locked") < 0))
                if ((e.toString().indexOf("currently locked") < 0)) {
                    throw new IOException(e.toString());
                } else {
                    tryAgain = false;
                    try { stmt.close(); } catch (Exception ex) {}
                    try { connection.commit(); } catch (Exception ex) {}
                    try { connection.close(); } catch (Exception ex) {}
                    try {
                        Thread.sleep(20);
//                        Thread.sleep(tries * 500 + 10);
                    } catch (InterruptedException ie) {
                    }
                }
            } finally {
                try { stmt.close(); } catch (Exception ex) {}
            }
        }
//        insertTestXref(contract, stats, className, testId);
//        return;

    }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }
        tries++;
System.out.println("trying again - " + contract.getName() + " - " + sqlTable + " - interval = " + stats.getInterval());
    }
System.out.println("done trying - " + contract.getName() + " - " + sqlTable + " - interval = " + stats.getInterval());
    }


    /**
     *  Execute this method from the garbage collector before deleting the object.
     *
     *  @throws Throwable   the exception raised by this method
     */
/*
    protected void finalize() throws Throwable {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.close();
        }
    }
*/
}
