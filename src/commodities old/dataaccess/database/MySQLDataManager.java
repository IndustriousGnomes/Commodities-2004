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
 *  @update 2004.12.06
 */
public class MySQLDataManager implements DataManagerInterface {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** Standard format for all dates used in SQL */
    private static final String SQL_DATE_FORMAT = "MM/dd/yyyy";

    /** Data manager instance */
    private static MySQLDataManager dbm;
    /** Database connection pool */
    private static DBConnectionPool dbcp;

    /** Prepared statement for inserting a commodity */
    private static PreparedStatement preparedInsertCommodity;
    /** Prepared statement for updating a commodity */
    private static PreparedStatement preparedUpdateCommodity;

    /** Prepared statement for selecting a contract */
    private static PreparedStatement preparedSelectContract;
    /** Prepared statement for selecting contracts */
    private static PreparedStatement preparedSelectContracts;
    /** Prepared statement for inserting a contract */
    private static PreparedStatement preparedInsertContract;
    /** Prepared statement for updating a contract */
    private static PreparedStatement preparedUpdateContract;
    /** Prepared statement for selecting a contract's last date */
    private static PreparedStatement preparedSelectContractLastDate;

    /** Prepared statement for inserting an account */
    private static PreparedStatement preparedInsertAccount;
    /** Prepared statement for updating an account */
    private static PreparedStatement preparedUpdateAccount;

    /** Prepared statement for selecting the current prices */
    private static PreparedStatement preparedSelectCurrentPrices;
    /** Prepared statement for selecting prices */
    private static PreparedStatement preparedSelectPrices;
    /** Prepared statement for selecting prices for a given date */
    private static PreparedStatement preparedSelectPrice;
    /** Prepared statement for inserting prices */
    private static PreparedStatement preparedInsertDailyPrice;
    /** Prepared statement for updating prices */
    private static PreparedStatement preparedUpdateDailyPrice;

    /** Prepared statement for inserting orders */
    private static PreparedStatement preparedInsertOrderEntry;
    /** Prepared statement for updating orders */
    private static PreparedStatement preparedUpdateOrderEntry;
    /** Prepared statement for deleting orders */
    private static PreparedStatement preparedDeleteOrderEntry;

    /** Prepared statement for selecting the best test cross-references */
    private static PreparedStatement preparedSelectBestTestXref;
    /** Prepared statement for inserting test cross-references */
    private static PreparedStatement preparedInsertTestXref;
    /** Prepared statement for deleting test cross-references. */
    private static PreparedStatement preparedDeleteTestXref;
    /** Prepared statement for deleting test cross-references for a commodity. */
    private static PreparedStatement preparedDeleteTestXrefCommodity;
    /** Prepared statement for deleting test cross-references for a contract. */
    private static PreparedStatement preparedDeleteTestXrefContract;

    /** Prepared statement for inserting a commodity price location */
    private static PreparedStatement preparedInsertCommodityPriceLocation;
    /** Prepared statement for deleting a commodity price location */
    private static PreparedStatement preparedDeleteCommodityPriceLocationPage;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /** Create an instance of the database data manager. */
    private MySQLDataManager() {
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
     *  @return     Singleton instance of MySQLDataManager
     */
    public static MySQLDataManager instance() {
        if (dbm == null) {
            synchronized (MySQLDataManager.class) {
                if (dbm == null) {
                    dbm = new MySQLDataManager();
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

        Collection collect = new LinkedHashSet();
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();
            sb.append("select  id, exchange, name, unit_type, unit_size, unit_price, ");
            sb.append("        tick_size, tick_price, tick_daily_limit, Contract_Standard_Price, display_decimals, open_outcry_symbol, ");
            sb.append("        acm_symbol, trade_month_mask, database_symbol ");
            sb.append("from Commodity ");
//            if (commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
//                sb.append("  where inactive = 0 ");
//            }
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
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
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
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
            if (preparedInsertCommodity == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("insert into Commodity(Id, Exchange, Name, Unit_Type, Unit_Size, Unit_Price, Tick_Size, ");
                sb.append("Tick_Price, Tick_Daily_Limit, Contract_Standard_Price, Display_Decimals, Open_Outcry_Symbol, ");
                sb.append("ACM_Symbol, Trade_Month_Mask, Database_Symbol) ");
                sb.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

                String sqlTxt = sb.toString();
                preparedInsertCommodity = connect.prepareStatement(sqlTxt);
            }

            preparedInsertCommodity.setString(1,  commodity.getSymbol());
            preparedInsertCommodity.setString(2,  commodity.getExchange());
            preparedInsertCommodity.setString(3,  commodity.getName());
            preparedInsertCommodity.setString(4,  commodity.getUnitType());
            preparedInsertCommodity.setInt   (5,  commodity.getUnitSize());
            preparedInsertCommodity.setString(6,  commodity.getUnitPrice());
            preparedInsertCommodity.setDouble(7,  commodity.getTickSize());
            preparedInsertCommodity.setDouble(8,  commodity.getTickPrice());
            preparedInsertCommodity.setInt   (9,  commodity.getTickDailyLimit());
            preparedInsertCommodity.setInt   (10, commodity.getMargin());
            preparedInsertCommodity.setInt   (11, commodity.getDisplayDecimals());
            preparedInsertCommodity.setString(12, commodity.getOpenOutcrySymbol());
            preparedInsertCommodity.setString(13, commodity.getAcmSymbol());
            preparedInsertCommodity.setInt   (14, commodity.getTradeMonthMask());
            preparedInsertCommodity.setString(15, commodity.getDatabaseSymbol());

            preparedInsertCommodity.executeUpdate();

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
            if (preparedUpdateCommodity == null) {
                Connection connect = dbcp.retrieveConnection();

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

                String sqlTxt = sb.toString();
                preparedUpdateCommodity = connect.prepareStatement(sqlTxt);
            }

            preparedUpdateCommodity.setString(1,  commodity.getExchange());
            preparedUpdateCommodity.setString(2,  commodity.getName());
            preparedUpdateCommodity.setString(3,  commodity.getUnitType());
            preparedUpdateCommodity.setInt   (4,  commodity.getUnitSize());
            preparedUpdateCommodity.setString(5,  commodity.getUnitPrice());
            preparedUpdateCommodity.setDouble(6,  commodity.getTickSize());
            preparedUpdateCommodity.setDouble(7,  commodity.getTickPrice());
            preparedUpdateCommodity.setInt   (8,  commodity.getTickDailyLimit());
            preparedUpdateCommodity.setInt   (9,  commodity.getMargin());
            preparedUpdateCommodity.setInt   (10, commodity.getDisplayDecimals());
            preparedUpdateCommodity.setString(11, commodity.getOpenOutcrySymbol());
            preparedUpdateCommodity.setString(12, commodity.getAcmSymbol());
            preparedUpdateCommodity.setInt   (13, commodity.getTradeMonthMask());
            preparedUpdateCommodity.setString(14, commodity.getDatabaseSymbol());

            preparedUpdateCommodity.setString(15,  commodity.getSymbol()); // where clause

            preparedUpdateCommodity.executeUpdate();

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
            if (preparedSelectContract == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("select id, commodity_id, contract_sort, price ");
                sb.append("from contract ");
                sb.append("where commodity_id = ? ");
                sb.append("  and contract_sort = ? ");
//                if (prototype.commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
//                    sb.append("  and inactive = 0 ");
//                }
                String sqlTxt = sb.toString();
                preparedSelectContract = connect.prepareStatement(sqlTxt);
            }

            preparedSelectContract.setString(1, symbol);
            preparedSelectContract.setString(2, contractMonth);

            ResultSet rs = preparedSelectContract.executeQuery();
            Contract contract = null;
            if (rs.next()) {
                contract = new Contract(rs.getInt("id"),
                                        rs.getString("commodity_id"),
                                        rs.getString("contract_sort"),
                                        rs.getInt("price"));
            }
            rs.close();

            return contract;
        } catch (SQLException e) {
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
            if (preparedSelectContracts == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("select id, commodity_id, contract_sort, price ");
                sb.append("from contract ");
                sb.append("where commodity_id = ? ");
                sb.append("  and contract_sort >= ? ");
//                if (prototype.commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
//                    sb.append("  and inactive = 0 ");
//                }

                String sqlTxt = sb.toString();
                preparedSelectContracts = connect.prepareStatement(sqlTxt);
            }

            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            String contractSort = calendar.get(CommodityCalendar.YEAR) + Month.byNumber(calendar.get(CommodityCalendar.MONTH)).getSymbol();
            preparedSelectContracts.setString(1, symbol);
            preparedSelectContracts.setString(2, contractSort);

            ResultSet rs = preparedSelectContracts.executeQuery();
            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new Contract(rs.getInt("id"),
                                        rs.getString("commodity_id"),
                                        rs.getString("contract_sort"),
                                        rs.getInt("price")));
            }
            rs.close();
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
            if (preparedInsertContract == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("insert into contract (commodity_id, contract_month, contract_sort) ");
                sb.append("values (?, ?, ?) ");

                String sqlTxt = sb.toString();
                preparedInsertContract = connect.prepareStatement(sqlTxt);
            }

            preparedInsertContract.setString(1, symbol);
            preparedInsertContract.setString(2, contractMonth.substring(4) + contractMonth.substring(0,4));
            preparedInsertContract.setString(3, contractMonth);

            preparedInsertContract.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Contract already exists: " + symbol + " " + contractMonth);
//            e.printStackTrace();
//            throw new IOException(e.toString());
        }
    }

    /**
     *  Add an existing contract.
     *
     *  @param  contract    the contract to update
     */
    public synchronized void updateContract(Contract contract) throws IOException {
        try {
            if (preparedUpdateContract == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("update Contract ");
                sb.append("   set Price=? ");
                sb.append(" where Commodity_Id = ? ");
                sb.append("   and Contract_Sort = ? ");

                String sqlTxt = sb.toString();
                preparedUpdateContract = connect.prepareStatement(sqlTxt);
            }

            preparedUpdateContract.setInt(1, contract.getMargin());
            preparedUpdateContract.setString(2, contract.getSymbol());
            preparedUpdateContract.setString(3, contract.getMonth());
            preparedUpdateContract.executeUpdate();

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
        Connection connect = null;

        try {
            connect = dbcp.retrieveConnection();

            long index = -1;

            StringBuffer sb = new StringBuffer();
            sb.append("select max(id) " +
                      "from Account ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            if (rs.next()) {
                index = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            return index;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Loads all of the accounts from the account table and provides
     *  an itertor of Account objects.
     *
     *  @return Iterator of Account objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public Iterator getAccounts() throws IOException {
        Connection connect = null;

        try {
            connect = dbcp.retrieveConnection();

            Collection collect = new LinkedHashSet();

            StringBuffer sb = new StringBuffer();
            sb.append("select id, first_name, last_name, capital ");
            sb.append("from Account ");
            sb.append("order by last_name, first_name, id ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
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
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Adds an account to the account table.
     *
     *  @param  account     The account to add to the table.
     *  @throws IOException There was a problem accessing the database table
     */
    public void addAccount(Account account) throws IOException {
        try {
            if (preparedInsertAccount == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("insert into Account(id, first_name, last_name, capital) ");
                sb.append("values (?, ?, ?, ?) ");

                String sqlTxt = sb.toString();
                preparedInsertAccount = connect.prepareStatement(sqlTxt);
            }

            preparedInsertAccount.setInt(1,  account.getAccountNumber());
            preparedInsertAccount.setString(2,  account.getFirstName());
            preparedInsertAccount.setString(3,  account.getLastName());
            preparedInsertAccount.setDouble(4,  account.getCapital());

            preparedInsertAccount.executeUpdate();
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
    public void updateAccount(Account account) throws IOException {
        try {
            if (preparedUpdateAccount == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("update Account ");
                sb.append("   set first_name=?, ");
                sb.append("       last_name=?, ");
                sb.append("       capital=? ");
                sb.append(" where Id = ? ");

                String sqlTxt = sb.toString();
                preparedUpdateAccount = connect.prepareStatement(sqlTxt);
            }

            preparedUpdateAccount.setString(1,  account.getFirstName());
            preparedUpdateAccount.setString(2,  account.getLastName());
            preparedUpdateAccount.setDouble(3,  account.getCapital());

            preparedUpdateAccount.setInt(4,  account.getAccountNumber());  // where clause

            preparedUpdateAccount.executeUpdate();
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
            if (preparedSelectCurrentPrices == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("select price_date, open_tick, high_tick, low_tick, close_tick, volume, open_interest ");
                sb.append("from Daily_Prices ");
                sb.append("where Contract_Id = ? ");
//                sb.append("  and price_date <= ? ");
                sb.append("order by price_date desc");
                String sqlTxt = sb.toString();
                preparedSelectCurrentPrices = connect.prepareStatement(sqlTxt);
            }

            java.util.Date today = ((CommodityCalendar)CommodityCalendar.getInstance()).getTime();
            preparedSelectCurrentPrices.setInt(1, id);
//            preparedSelectCurrentPrices.setString(2, FormatDate.formatDateProp(today, FormatDate.FORMAT_SQL_DATE));

            ResultSet rs = preparedSelectCurrentPrices.executeQuery();
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
    }

    /**
     *  Retrieves all prices for a given contract.
     *
     *  @param  id      the id of the contract
     *  @return         an iterator of the contract prices
     */
    public synchronized Iterator getPrices(int id) throws IOException {
        try {
            if (preparedSelectPrices == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("select price_date, open_tick, high_tick, low_tick, close_tick, volume, open_interest " +
                          "from Daily_Prices " +
                          "where Contract_Id = ? " +
                          "order by price_date desc ");
                String sqlTxt = sb.toString();
                preparedSelectPrices = connect.prepareStatement(sqlTxt);
            }

            java.util.Date today = ((CommodityCalendar)CommodityCalendar.getInstance()).getTime();
            preparedSelectPrices.setInt(1, id);

            ResultSet rs = preparedSelectPrices.executeQuery();
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
            return collect.iterator();
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
    public synchronized void addDailyPrice(Contract contract,
                                                Prices prices) throws IOException {
        try {
            if (preparedInsertDailyPrice == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("insert into Daily_Prices(Contract_Id, price_date, open_tick, high_tick, low_tick, close_tick, volume, open_interest) " +
                          "values (?, ?, ?, ?, ?, ?, ?, ?) ");

                String sqlTxt = sb.toString();
                preparedInsertDailyPrice = connect.prepareStatement(sqlTxt);
            }

        System.out.println("*" + contract.getSymbol() + "* *" + contract.getMonthFormatted() + "* *" + prices.getDate() + "*");

            preparedInsertDailyPrice.setInt   (1, contract.getId());
//System.out.println(prices.getDate() + " ==> " + FormatDate.formatDate(prices.getDate(), SQL_DATE_FORMAT));
//            preparedInsertDailyPrice.setString(2, FormatDate.formatDate(prices.getDate(), SQL_DATE_FORMAT));
            preparedInsertDailyPrice.setDate(2, new java.sql.Date(prices.getDate().getTime()));
            preparedInsertDailyPrice.setDouble(3, prices.getOpen());
            preparedInsertDailyPrice.setDouble(4, prices.getHigh());
            preparedInsertDailyPrice.setDouble(5, prices.getLow());
            preparedInsertDailyPrice.setDouble(6, prices.getClose());
            preparedInsertDailyPrice.setLong  (7, prices.getVolume());
            preparedInsertDailyPrice.setLong  (8, prices.getInterest());

            preparedInsertDailyPrice.executeUpdate();
        } catch (SQLException e) {
            try {
                if (preparedUpdateDailyPrice == null) {
                    Connection connect = dbcp.retrieveConnection();

                    StringBuffer sb = new StringBuffer();
                    sb.append("update Daily_Prices " +
                              "   set open_tick = ?, " +
                              "       high_tick = ?, " +
                              "       low_tick = ?, " +
                              "       close_tick = ?, " +
                              "       volume = ? " +
                              "       open_interest = ?, " +
                              " where Contract_Id = ? " +
                              "   and price_date = ? ");

                    String sqlTxt = sb.toString();
                    preparedUpdateDailyPrice = connect.prepareStatement(sqlTxt);
                }

                preparedUpdateDailyPrice.setDouble(1, prices.getOpen());
                preparedUpdateDailyPrice.setDouble(2, prices.getHigh());
                preparedUpdateDailyPrice.setDouble(3, prices.getLow());
                preparedUpdateDailyPrice.setDouble(4, prices.getClose());
                preparedUpdateDailyPrice.setLong  (5, prices.getVolume());
                preparedUpdateDailyPrice.setLong  (6, prices.getInterest());
                preparedUpdateDailyPrice.setInt   (7, contract.getId());
                preparedUpdateDailyPrice.setDate  (8, new java.sql.Date(prices.getDate().getTime()));

                preparedUpdateDailyPrice.executeUpdate();
            } catch (SQLException ex) {
                throw new IOException(e.toString());
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
            java.sql.Date date = null;

            if (preparedSelectContractLastDate == null) {
                Connection connect = dbcp.retrieveConnection();

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

                String sqlTxt = sb.toString();
                preparedSelectContractLastDate = connect.prepareStatement(sqlTxt);
            }
            preparedSelectContractLastDate.setInt(1,  contract.getId());

            ResultSet rs = preparedSelectContractLastDate.executeQuery();
            if (rs.next()) {
                date = rs.getDate(1);
            }
            rs.close();
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
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();
            long index = -1;

            StringBuffer sb = new StringBuffer();
            sb.append("select max(id) " +
                      "from Orders ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            if (rs.next()) {
                index = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            return index;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Retrieve all of the open orders for the given account number.
     *
     *  @param  acctNumber  The account number to retrieve orders for.
     *  @return     an iterator of open orders
     */
    public synchronized Iterator getOpenOrders(int acctNumber) throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

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
                            " Note_text, " +
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
            Statement stmt = connect.createStatement();
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
                                    rs.getString("Note_text").trim(),
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
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
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
                                              int           acctNumber,
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
            if (preparedUpdateOrderEntry == null) {
                Connection connect = dbcp.retrieveConnection();

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
                          "       Note_text=?, " +
                          "       Offsetting_Order=?, " +
                          "       Date_Considered=?, " +
                          "       Date_Placed=?, " +
                          "       Date_Filled=?, " +
                          "       Date_Cancel_Placed=?, " +
                          "       Date_Cancelled=?, " +
                          "       Date_Stop_Loss=? " +
                          " where Id = ? ");

                String sqlTxt = sb.toString();
                preparedUpdateOrderEntry = connect.prepareStatement(sqlTxt);
            }

            preparedUpdateOrderEntry.setInt   ( 1, (int)acctNumber);
            preparedUpdateOrderEntry.setString( 2, buyOrder?"B":"S");
            preparedUpdateOrderEntry.setDate  ( 3, new java.sql.Date(date.getTime()));
            preparedUpdateOrderEntry.setInt   ( 4, number.intValue());
            preparedUpdateOrderEntry.setString( 5, month);
            preparedUpdateOrderEntry.setString( 6, commodityName);
            preparedUpdateOrderEntry.setDouble( 7, desiredPrice.doubleValue());
            preparedUpdateOrderEntry.setDouble( 8, stopPrice.doubleValue());
            preparedUpdateOrderEntry.setString( 9, status);
            preparedUpdateOrderEntry.setDouble(10, actualPrice.doubleValue());
            preparedUpdateOrderEntry.setString(11, note);
            preparedUpdateOrderEntry.setLong  (12, offsettingOrder);

            if (dateConsidered == null) {
                preparedUpdateOrderEntry.setNull(13, java.sql.Types.DATE);
            } else {
                preparedUpdateOrderEntry.setDate(13, new java.sql.Date(dateConsidered.getTime()));
            }

            if (datePlaced == null) {
                preparedUpdateOrderEntry.setNull(14, java.sql.Types.DATE);
            } else {
                preparedUpdateOrderEntry.setDate(14, new java.sql.Date(datePlaced.getTime()));
            }

            if (dateFilled == null) {
                preparedUpdateOrderEntry.setNull(15, java.sql.Types.DATE);
            } else {
                preparedUpdateOrderEntry.setDate(15, new java.sql.Date(dateFilled.getTime()));
            }

            if (dateCancelPlaced == null) {
                preparedUpdateOrderEntry.setNull(16, java.sql.Types.DATE);
            } else {
                preparedUpdateOrderEntry.setDate(16, new java.sql.Date(dateCancelPlaced.getTime()));
            }

            if (dateCancelled == null) {
                preparedUpdateOrderEntry.setNull(17, java.sql.Types.DATE);
            } else {
                preparedUpdateOrderEntry.setDate(17, new java.sql.Date(dateCancelled.getTime()));
            }

            if (dateStopLoss == null) {
                preparedUpdateOrderEntry.setNull(18, java.sql.Types.DATE);
            } else {
                preparedUpdateOrderEntry.setDate(18, new java.sql.Date(dateStopLoss.getTime()));
            }



            preparedUpdateOrderEntry.setLong  (19, id);

            int count = preparedUpdateOrderEntry.executeUpdate();
            if (count == 0) {
                throw new SQLException();
            }

        } catch (SQLException e) {
             try {
                if (preparedInsertOrderEntry == null) {
                    Connection connect = dbcp.retrieveConnection();

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

                    String sqlTxt = sb.toString();
                    preparedInsertOrderEntry = connect.prepareStatement(sqlTxt);
                }
                preparedInsertOrderEntry.setLong  ( 1, id);
                preparedInsertOrderEntry.setInt   ( 2, (int)acctNumber);
                preparedInsertOrderEntry.setString( 3, buyOrder?"B":"S");
                preparedInsertOrderEntry.setDate  ( 4, new java.sql.Date(date.getTime()));
                preparedInsertOrderEntry.setInt   ( 5, number.intValue());
                preparedInsertOrderEntry.setString( 6, month);
                preparedInsertOrderEntry.setString( 7, commodityName);
                preparedInsertOrderEntry.setDouble( 8, desiredPrice.doubleValue());
                preparedInsertOrderEntry.setDouble( 9, stopPrice.doubleValue());
                preparedInsertOrderEntry.setString(10, status);
                preparedInsertOrderEntry.setDouble(11, actualPrice.doubleValue());
                preparedInsertOrderEntry.setString(12, note);
                preparedInsertOrderEntry.setLong  (13, offsettingOrder);

                if (dateConsidered == null) {
                    preparedInsertOrderEntry.setNull(14, java.sql.Types.DATE);
                } else {
                    preparedInsertOrderEntry.setDate(14, new java.sql.Date(dateConsidered.getTime()));
                }

                if (datePlaced == null) {
                    preparedInsertOrderEntry.setNull(15, java.sql.Types.DATE);
                } else {
                    preparedInsertOrderEntry.setDate(15, new java.sql.Date(datePlaced.getTime()));
                }

                if (dateFilled == null) {
                    preparedInsertOrderEntry.setNull(16, java.sql.Types.DATE);
                } else {
                    preparedInsertOrderEntry.setDate(16, new java.sql.Date(dateFilled.getTime()));
                }

                if (dateCancelPlaced == null) {
                    preparedInsertOrderEntry.setNull(17, java.sql.Types.DATE);
                } else {
                    preparedInsertOrderEntry.setDate(17, new java.sql.Date(dateCancelPlaced.getTime()));
                }

                if (dateCancelled == null) {
                    preparedInsertOrderEntry.setNull(18, java.sql.Types.DATE);
                } else {
                    preparedInsertOrderEntry.setDate(18, new java.sql.Date(dateCancelled.getTime()));
                }

                if (dateStopLoss == null) {
                    preparedInsertOrderEntry.setNull(19, java.sql.Types.DATE);
                } else {
                    preparedInsertOrderEntry.setDate(19, new java.sql.Date(dateStopLoss.getTime()));
                }

                preparedInsertOrderEntry.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("*** FAILED TO UPDATE/ADD ORDER ***");
                System.out.println("id                 = " + id);
                System.out.println("Account_Id         = " + (int)acctNumber);
                System.out.println("Buy_Sell_Ind       = " + ((buyOrder)?"B":"S"));
                System.out.println("Date_Entered       = " + date.getTime());
                System.out.println("Nbr_Contracts      = " + number.intValue());
                System.out.println("Contract_Month     = " + month);
                System.out.println("Commodity_Name     = " + commodityName);
                System.out.println("Desired_Price      = " + desiredPrice.doubleValue());
                System.out.println("Stop_Price         = " + stopPrice.doubleValue());
                System.out.println("Status             = " + status);
                System.out.println("Actual_Price       = " + actualPrice.doubleValue());
                System.out.println("Note_Text          = " + note);
                System.out.println("Offsetting_Order   = " + offsettingOrder);
                System.out.println("Date_Considered    = " + dateConsidered.getTime());
                System.out.println("Date_Placed        = " + datePlaced.getTime());
                System.out.println("Date_Filled        = " + dateFilled.getTime());
                System.out.println("Date_Cancel_Placed = " + dateCancelPlaced.getTime());
                System.out.println("Date_Cancelled     = " + dateCancelled.getTime());
                System.out.println("Date_Stop_Loss     = " + dateStopLoss.getTime());

                e.printStackTrace();
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
            if (preparedDeleteOrderEntry == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("delete from Orders " +
                          "where Id = ? ");

                String sqlTxt = sb.toString();
                preparedDeleteOrderEntry = connect.prepareStatement(sqlTxt);
            }

            preparedDeleteOrderEntry.setLong  (1, id);
            preparedDeleteOrderEntry.executeUpdate();

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
    public void clearTestXref() throws IOException {
        try {
            if (preparedDeleteTestXref == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("delete from Test_Xref ");

                String sqlTxt = sb.toString();
                preparedDeleteTestXref = connect.prepareStatement(sqlTxt);
            }

            preparedDeleteTestXref.executeUpdate();

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
    public void clearTestXref(Commodity commodity) throws IOException {
        try {
            if (preparedDeleteTestXrefCommodity == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("delete from Test_Xref " +
                          "where Commodity_Id = ? ");

                String sqlTxt = sb.toString();
                preparedDeleteTestXrefCommodity = connect.prepareStatement(sqlTxt);
            }

            preparedDeleteTestXrefCommodity.setString(1, commodity.getSymbol());

            preparedDeleteTestXrefCommodity.executeUpdate();

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
    public void clearTestXref(Contract contract) throws IOException {
        try {
            if (preparedDeleteTestXrefContract == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("delete from Test_Xref " +
                          "where Commodity_Id = ? " +
                          "  and Contract_Month = ? ");

                String sqlTxt = sb.toString();
                preparedDeleteTestXrefContract = connect.prepareStatement(sqlTxt);
            }

            preparedDeleteTestXrefContract.setString(1, contract.getCommodity().getSymbol());
            preparedDeleteTestXrefContract.setString(2, contract.getMonth());

            preparedDeleteTestXrefContract.executeUpdate();

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
            if (preparedInsertTestXref == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("insert into Test_Xref(commodity_id, contract_month, test_class, test_id, test_interval, success_ratio, profit_loss) " +
                          "values (?, ?, ?, ?, ?, ?, ?) ");

                String sqlTxt = sb.toString();
                preparedInsertTestXref = connect.prepareStatement(sqlTxt);
            }

            preparedInsertTestXref.setString(1, contract.getSymbol());
            preparedInsertTestXref.setString(2, contract.getMonth());
            preparedInsertTestXref.setString(3, testClass);
            preparedInsertTestXref.setInt   (4, testId);
            preparedInsertTestXref.setInt   (5, stats.getInterval());
            preparedInsertTestXref.setDouble(6, stats.getCountRatio());
            preparedInsertTestXref.setDouble(7, stats.getProfit());

            preparedInsertTestXref.executeUpdate();

        } catch (SQLException e) {
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
        Collection negProfits = new LinkedList();
        try {
            if (preparedSelectBestTestXref == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("select test_class, test_id, success_ratio, profit_loss " +
                          "from test_xref " +
                          "where Commodity_Id = ? " +
                          "  and Contract_Month = ? " +
                          "  and Test_Interval > ? " +
                          "  and Test_Interval <= ? " +
                          "order by Success_Ratio desc, Profit_Loss desc ");

                String sqlTxt = sb.toString();
                preparedSelectBestTestXref = connect.prepareStatement(sqlTxt);

            }

            preparedSelectBestTestXref.setString(1, contract.getSymbol());
            preparedSelectBestTestXref.setString(2, contract.getMonth());
            preparedSelectBestTestXref.setInt   (3, termBottom);
            preparedSelectBestTestXref.setInt   (4, termTop);

            ResultSet rs = preparedSelectBestTestXref.executeQuery();

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
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT name, abbrev, number, symbol " +
                      "FROM month_lookup ");

            String sqlTxt = sb.toString();
            Statement stmt = connect.createStatement();
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
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
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
            if (preparedInsertCommodityPriceLocation == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("insert into Commodity_Price_Pages(page, loc, symbol, title) " +
                          "values (?, ?, ?, ?) ");

                String sqlTxt = sb.toString();
                preparedInsertCommodityPriceLocation = connect.prepareStatement(sqlTxt);
            }

            preparedInsertCommodityPriceLocation.setInt(1, cpl.getPage());
            preparedInsertCommodityPriceLocation.setInt(2, cpl.getLocation());
            preparedInsertCommodityPriceLocation.setString(3, cpl.getSymbol());
            preparedInsertCommodityPriceLocation.setString(4, cpl.getTitle());

            preparedInsertCommodityPriceLocation.executeUpdate();

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
            if (preparedDeleteCommodityPriceLocationPage == null) {
                Connection connect = dbcp.retrieveConnection();

                StringBuffer sb = new StringBuffer();
                sb.append("delete from Commodity_Price_Pages " +
                          "where page = ? ");

                String sqlTxt = sb.toString();
                preparedDeleteCommodityPriceLocationPage = connect.prepareStatement(sqlTxt);
            }

            preparedDeleteCommodityPriceLocationPage.setInt(1, page);
            preparedDeleteCommodityPriceLocationPage.executeUpdate();

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
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();

            sb.append("select  page, loc, symbol, title " +
                      "from Commodity_Price_Pages " +
                      "order by page, loc ");

            String sqlTxt = sb.toString();
            Statement stmt = connect.createStatement();
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
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

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
    public synchronized void saveTestParameters(String sqlTable, String columns, String columnValues, Contract contract, StatsAbstract stats, String className) throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            int testId = 0;
            Statement stmt = null;
            StringBuffer sb = null;
            String sqlTxt = null;
            ResultSet rs = null;
            try {
                sb = new StringBuffer();
                sb.append("select max(id) " +
                          "from " + sqlTable + " ");
                sqlTxt = sb.toString();

                stmt = connect.createStatement();
                rs = stmt.executeQuery(sqlTxt);

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

            try {
                sb = new StringBuffer();
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
                sqlTxt = sb.toString();
                stmt = connect.createStatement();
                stmt.executeUpdate(sqlTxt);

                insertTestXref(contract, stats, className, testId);

            } catch (SQLException e) {
                throw new IOException(e.toString());
            } finally {
                try { stmt.close(); } catch (Exception ex) {}
            }
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }
}