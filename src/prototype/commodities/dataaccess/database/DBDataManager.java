/*  _ Properly Documented
 */
/** DBDataManager
 *  This class controlls the storage of data to databases.
 */
package prototype.commodities.dataaccess.database;

import java.io.*;
import java.sql.*;
import java.util.*;

import prototype.commodities.*;
import prototype.commodities.util.*;
import prototype.commodities.contract.*;
import prototype.commodities.dataaccess.*;
import prototype.commodities.tests.*;

public class DBDataManager implements DataManagerInterface {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    private static final String SQL_DATE_FORMAT = "MM/dd/yyyy";

    private static DBDataManager dbm;
    private static DBConnectionPool dbcp;

    private static PreparedStatement preparedSelectContract;
    private static PreparedStatement preparedSelectContracts;
    private static PreparedStatement preparedUpdateContract;
    private static PreparedStatement preparedSelectPrices;
    private static PreparedStatement preparedSelectCurrentPrices;
    private static PreparedStatement preparedInsertDailyPrice;
    private static PreparedStatement preparedUpdateDailyPrice;
    private static PreparedStatement preparedInsertCommodityPriceLocation;
    private static PreparedStatement preparedDeleteCommodityPriceLocationPage;
    private static PreparedStatement preparedInsertCommodity;
    private static PreparedStatement preparedUpdateCommodity;
    private static PreparedStatement preparedSelectContractLastDate;
    private static PreparedStatement preparedUpdateOrderEntry;
    private static PreparedStatement preparedInsertOrderEntry;
    private static PreparedStatement preparedDeleteOrderEntry;
    private static PreparedStatement preparedInsertTestXref;
    private static PreparedStatement preparedSelectBestTestXref;

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    private DBDataManager() {
        try {
            dbcp = DBConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Database failure");
        }
    }

/* *************************************** */
/* *** CLASS METHODS                   *** */
/* *************************************** */
    public static DBDataManager getInstance() {
        if (dbm == null) {
            synchronized (DBDataManager.class) {
                if (dbm == null) {
                    dbm = new DBDataManager();
                }
            }
        }
        return dbm;
//        return new DBDataManager();
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Retrieves a contract by symbol and contract month.
     *
     *  @param  symbol          The symbol of the commodity to retrieve.
     *  @param  contractMonth   The contract month of the commodity to retrieve
     *  @return The contract for the requested commodity and contract month.
     */
    public synchronized Contract getContractByMonth(String symbol, String contractMonth) throws IOException {
        try {
            if (preparedSelectContract == null) {
                preparedSelectContractSQL();
            }

            preparedSelectContract.setString(1, symbol);
            preparedSelectContract.setString(2, contractMonth);

            ResultSet rs = preparedSelectContract.executeQuery();
            Contract contract = null;
            if (rs.next()) {
                contract = new Contract(rs.getInt("Id"),
                                        rs.getString("Commodity_Id"),
                                        rs.getString("Contract_Month"),
                                        rs.getInt("Price"));
            }
            rs.close();

            return contract;
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Retrieves all contracts by symbol.
     */
    public synchronized Iterator getContracts(String symbol) throws IOException {
        try {
            if (preparedSelectContracts == null) {
                preparedSelectContractsSQL();
            }

            CommodityCalendar calendar = (CommodityCalendar)CommodityCalendar.getInstance();
            String contractSort = calendar.get(CommodityCalendar.YEAR) + Month.byNumber(calendar.get(CommodityCalendar.MONTH)).getSymbol();
            preparedSelectContracts.setString(1, symbol);
            preparedSelectContracts.setString(2, contractSort);

            ResultSet rs = preparedSelectContracts.executeQuery();
            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new Contract(rs.getInt("Id"),
                                        rs.getString("Commodity_Id"),
                                        rs.getString("Contract_Month"),
                                        rs.getInt("Price")));
            }
            rs.close();
            return collect.iterator();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

/**
 *  Executes stored procedure 'dbo.InsertContractNoPrice()'
 */
    public synchronized void addContract(String symbol, String contractMonth) throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            CallableStatement call = connect.prepareCall("{call dbo.InsertContractNoPrice(?,?,?)}");
            call.setString(1, symbol);
            call.setString(2, contractMonth);
            call.setString(3, contractMonth.substring(1) + contractMonth.substring(0,1));
            call.executeUpdate();
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
        }
    }

    /**
     *  Updates a contract in the contract table.
     *
     *  @param  contract        The contract to add to the table.
     *  @throws SQLException    There was a problem accessing the database table
     */
    public void updateContract(Contract contract) throws IOException {
        try {
            if (preparedUpdateContract == null) {
                preparedUpdateContractSQL();
            }

            preparedUpdateContract.setInt(1, contract.getPrice());
            preparedUpdateContract.setString(2, contract.getSymbol());
            preparedUpdateContract.setString(3, contract.getMonth().substring(1) + contract.getMonth().substring(0,1));
            preparedUpdateContract.executeUpdate();

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     *  Retrieves the current prices for a given contract.
     */
    public synchronized Prices getCurrentPrices(int id) throws IOException {
        try {
            if (preparedSelectCurrentPrices == null) {
                preparedSelectCurrentPricesSQL();
            }

            java.util.Date today = ((CommodityCalendar)CommodityCalendar.getInstance()).getTime();
            preparedSelectCurrentPrices.setInt(1, id);
//            preparedSelectCurrentPrices.setString(2, FormatDate.formatDateProp(today, FormatDate.FORMAT_SQL_DATE));

            ResultSet rs = preparedSelectCurrentPrices.executeQuery();
            Prices prices = null;
            if (rs.next()) {
                prices = new Prices(rs.getDate("date"),
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
     */
    public synchronized Iterator getPrices(int id) throws IOException {
        try {
            if (preparedSelectPrices == null) {
                preparedSelectPricesSQL();
            }

            java.util.Date today = ((CommodityCalendar)CommodityCalendar.getInstance()).getTime();
            preparedSelectPrices.setInt(1, id);
//            preparedSelectPrices.setString(2, FormatDate.formatDateProp(today, FormatDate.FORMAT_SQL_DATE));

            ResultSet rs = preparedSelectPrices.executeQuery();
            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new Prices(rs.getDate("date"),
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
     *  Executes stored procedure 'dbo.InsertDailyPrice()'
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
    public synchronized DBTrilean addDailyPrice(String symbol,
                                                String contract,
                                                String date,
                                                double open,
                                                double high,
                                                double low,
                                                double close,
                                                int    volume,
                                                int    openint) throws IOException {
        DBTrilean updateSwitch = new DBTrilean();
        int contractId = 0;

        try {
            if (preparedInsertDailyPrice == null) {
                preparedInsertDailyPriceSQL();
            }

            Contract cont = getContractByMonth(symbol, contract);
            if (cont == null) {
                try {
                    addContract(symbol, contract);
                    updateSwitch.add();
                } catch (IOException e) {
                    return updateSwitch;
                }
                cont = getContractByMonth(symbol, contract);
            }
            contractId = cont.getId();

        System.out.println("*" + symbol + "* *" + contract + "* *" + date + "*");

            preparedInsertDailyPrice.setInt(1, contractId);
            preparedInsertDailyPrice.setString(2, date);
            preparedInsertDailyPrice.setDouble(3, open);
            preparedInsertDailyPrice.setDouble(4, high);
            preparedInsertDailyPrice.setDouble(5, low);
            preparedInsertDailyPrice.setDouble(6, close);
            preparedInsertDailyPrice.setLong(7, volume);
            preparedInsertDailyPrice.setLong(8, openint);

            preparedInsertDailyPrice.executeUpdate();
            updateSwitch.update();
        } catch (SQLException e) {
            try {
                if (preparedUpdateDailyPrice == null) {
                    preparedUpdateDailyPriceSQL();
                }

                preparedUpdateDailyPrice.setDouble(1, open);
                preparedUpdateDailyPrice.setDouble(2, high);
                preparedUpdateDailyPrice.setDouble(3, low);
                preparedUpdateDailyPrice.setDouble(4, close);
                preparedUpdateDailyPrice.setLong  (5, volume);
                preparedUpdateDailyPrice.setLong  (6, openint);
                preparedUpdateDailyPrice.setInt   (7, contractId);
                preparedUpdateDailyPrice.setString(8, date);

                preparedUpdateDailyPrice.executeUpdate();
                updateSwitch.update();
            } catch (SQLException ex) {
                throw new IOException(e.toString());
            }
        }
        return updateSwitch;
    }

    /**
     *  Retrieves the last date prices were loaded to the database.
     *
     *  @return The most current date in the pricing table.
     */
    public synchronized java.util.Date getLastPriceDateLoaded() throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();
            java.sql.Date date = null;

            StringBuffer sb = new StringBuffer();
            sb.append("select max(date) " +
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
     *  @return             The most current date in the pricing table
     */
    public synchronized java.util.Date getLastPriceDateLoaded(Contract contract) throws IOException {
        try {
            java.sql.Date date = null;

            if (preparedSelectContractLastDate == null) {
                preparedSelectContractLastDateSQL();
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

    /**
     *  Saves a commodity price location.
     *
     *  @param cpl   The CommodityPriceLocation to save.
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized void putCommodityPageLocation(CommodityPriceLocation cpl) throws IOException {
        try {
            if (preparedInsertCommodityPriceLocation == null) {
                preparedInsertCommodityPriceLocationSQL();
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
     *  @param locs     The updated list of CommodityPriceLocations for a given page
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
                preparedDeleteCommodityPriceLocationPageSQL();
            }

            preparedDeleteCommodityPriceLocationPage.setInt(1, page);
            preparedDeleteCommodityPriceLocationPage.executeUpdate();

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
    public void addCommodity(Commodity commodity) throws IOException {
        try {
            if (preparedInsertCommodity == null) {
                preparedInsertCommoditySQL();
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
            preparedInsertCommodity.setInt   (10, commodity.getStandardPrice());
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
    public void updateCommodity(Commodity commodity) throws IOException {
        try {
            if (preparedUpdateCommodity == null) {
                preparedUpdateCommoditySQL();
            }

            preparedUpdateCommodity.setString(1,  commodity.getExchange());
            preparedUpdateCommodity.setString(2,  commodity.getName());
            preparedUpdateCommodity.setString(3,  commodity.getUnitType());
            preparedUpdateCommodity.setInt   (4,  commodity.getUnitSize());
            preparedUpdateCommodity.setString(5,  commodity.getUnitPrice());
            preparedUpdateCommodity.setDouble(6,  commodity.getTickSize());
            preparedUpdateCommodity.setDouble(7,  commodity.getTickPrice());
            preparedUpdateCommodity.setInt   (8,  commodity.getTickDailyLimit());
            preparedUpdateCommodity.setInt   (9,  commodity.getStandardPrice());
            preparedUpdateCommodity.setInt   (10,  commodity.getDisplayDecimals());
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

/* *************************************** */
/* *** ORDER DATA METHODS              *** */
/* *************************************** */
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
     *  Retrieve all of the open orders.
     *
     *  @return     an iterator of open orders
     */
    public synchronized Iterator getOpenOrders() throws IOException {
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
                            " Note, " +
                            " Offsetting_Order, " +
                            " Date_Considered, " +
                            " Date_Placed, " +
                            " Date_Filled, " +
                            " Date_Cancel_Placed, " +
                            " Date_Cancelled, " +
                            " Date_Stop_Loss " +
                        "from Orders " +
                       "where Offsetting_Order < 0 " +
                       "order by Commodity_Name, Contract_Month, Date_Entered ");

            String sqlTxt = sb.toString();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);

            Collection collect = new LinkedHashSet();
            while (rs.next()) {
                collect.add(new ContractOrder(
                                    rs.getLong  ("Id"),
                                    ("B".equals(rs.getString("Buy_Sell_Ind")))?true:false,
                                    rs.getDate  ("Date_Entered"),
                                    new Integer(rs.getInt   ("Nbr_Contracts")),
                                    rs.getString("Contract_Month").trim(),
                                    rs.getString("Commodity_Name").trim(),
                                    new Double(rs.getDouble("Desired_Price")),
                                    new Double (rs.getDouble("Stop_Price")),
                                    rs.getString("Status").trim(),
                                    new Double (rs.getDouble("Actual_Price")),
                                    rs.getString("Note").trim(),
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

    public synchronized void updateOrderEntry(
                                long           id,
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
                preparedUpdateOrderEntrySQL();
            }

            preparedUpdateOrderEntry.setString( 1, buyOrder?"B":"S");
            preparedUpdateOrderEntry.setString( 2, FormatDate.formatDate(date, SQL_DATE_FORMAT));
            preparedUpdateOrderEntry.setInt   ( 3, number.intValue());
            preparedUpdateOrderEntry.setString( 4, month);
            preparedUpdateOrderEntry.setString( 5, commodityName);
            preparedUpdateOrderEntry.setDouble( 6, desiredPrice.doubleValue());
            preparedUpdateOrderEntry.setDouble( 7, stopPrice.doubleValue());
            preparedUpdateOrderEntry.setString( 8, status);
            preparedUpdateOrderEntry.setDouble( 9, actualPrice.doubleValue());
            preparedUpdateOrderEntry.setString(10, note);
            preparedUpdateOrderEntry.setLong  (11, offsettingOrder);

            if (dateConsidered == null) {
                preparedUpdateOrderEntry.setNull(12, java.sql.Types.VARCHAR);
            } else {
                preparedUpdateOrderEntry.setString(12, FormatDate.formatDate(dateConsidered, SQL_DATE_FORMAT));
            }

            if (datePlaced == null) {
                preparedUpdateOrderEntry.setNull(13, java.sql.Types.VARCHAR);
            } else {
                preparedUpdateOrderEntry.setString(13, FormatDate.formatDate(datePlaced, SQL_DATE_FORMAT));
            }

            if (dateFilled == null) {
                preparedUpdateOrderEntry.setNull(14, java.sql.Types.VARCHAR);
            } else {
                preparedUpdateOrderEntry.setString(14, FormatDate.formatDate(dateFilled, SQL_DATE_FORMAT));
            }

            if (dateCancelPlaced == null) {
                preparedUpdateOrderEntry.setNull(15, java.sql.Types.VARCHAR);
            } else {
                preparedUpdateOrderEntry.setString(15, FormatDate.formatDate(dateCancelPlaced, SQL_DATE_FORMAT));
            }

            if (dateCancelled == null) {
                preparedUpdateOrderEntry.setNull(16, java.sql.Types.VARCHAR);
            } else {
                preparedUpdateOrderEntry.setString(16, FormatDate.formatDate(dateCancelled, SQL_DATE_FORMAT));
            }

            if (dateStopLoss == null) {
                preparedUpdateOrderEntry.setNull(17, java.sql.Types.VARCHAR);
            } else {
                preparedUpdateOrderEntry.setString(17, FormatDate.formatDate(dateStopLoss, SQL_DATE_FORMAT));
            }


Debug.println(DEBUG,this,"processing id=" + id);

            preparedUpdateOrderEntry.setLong  (18, id);
Debug.println(DEBUG,this,"executing update");

            int count = preparedUpdateOrderEntry.executeUpdate();
Debug.println(DEBUG,this,"rows updated = " + count);
            if (count == 0) {
                throw new SQLException();
            }

        } catch (SQLException e) {
Debug.println(DEBUG,this,"Failed update, trying insert");
             try {
                if (preparedInsertOrderEntry == null) {
                    preparedInsertOrderEntrySQL();
                }
Debug.println(DEBUG,this,"updateOrderEntry - date = " + date + " translates to *" + FormatDate.formatDate(date, SQL_DATE_FORMAT) + "*");
                preparedInsertOrderEntry.setLong  ( 1, id);
                preparedInsertOrderEntry.setString( 2, buyOrder?"B":"S");
                preparedInsertOrderEntry.setString( 3, FormatDate.formatDate(date, SQL_DATE_FORMAT));
                preparedInsertOrderEntry.setInt   ( 4, number.intValue());
                preparedInsertOrderEntry.setString( 5, month);
                preparedInsertOrderEntry.setString( 6, commodityName);
                preparedInsertOrderEntry.setDouble( 7, desiredPrice.doubleValue());
                preparedInsertOrderEntry.setDouble( 8, stopPrice.doubleValue());
                preparedInsertOrderEntry.setString( 9, status);
                preparedInsertOrderEntry.setDouble(10, actualPrice.doubleValue());
                preparedInsertOrderEntry.setString(11, note);
                preparedInsertOrderEntry.setLong  (12, offsettingOrder);

                if (dateConsidered == null) {
                    preparedInsertOrderEntry.setNull(13, java.sql.Types.VARCHAR);
                } else {
                    preparedInsertOrderEntry.setString(13, FormatDate.formatDate(dateConsidered, SQL_DATE_FORMAT));
                }

                if (datePlaced == null) {
                    preparedInsertOrderEntry.setNull(14, java.sql.Types.VARCHAR);
                } else {
                    preparedInsertOrderEntry.setString(14, FormatDate.formatDate(datePlaced, SQL_DATE_FORMAT));
                }

                if (dateFilled == null) {
                    preparedInsertOrderEntry.setNull(15, java.sql.Types.VARCHAR);
                } else {
                    preparedInsertOrderEntry.setString(15, FormatDate.formatDate(dateFilled, SQL_DATE_FORMAT));
                }

                if (dateCancelPlaced == null) {
                    preparedInsertOrderEntry.setNull(16, java.sql.Types.VARCHAR);
                } else {
                    preparedInsertOrderEntry.setString(16, FormatDate.formatDate(dateCancelPlaced, SQL_DATE_FORMAT));
                }

                if (dateCancelled == null) {
                    preparedInsertOrderEntry.setNull(17, java.sql.Types.VARCHAR);
                } else {
                    preparedInsertOrderEntry.setString(17, FormatDate.formatDate(dateCancelled, SQL_DATE_FORMAT));
                }

                if (dateStopLoss == null) {
                    preparedInsertOrderEntry.setNull(18, java.sql.Types.VARCHAR);
                } else {
                    preparedInsertOrderEntry.setString(18, FormatDate.formatDate(dateStopLoss, SQL_DATE_FORMAT));
                }

                preparedInsertOrderEntry.executeUpdate();

            } catch (SQLException ex) {
                e.printStackTrace();
Debug.println(DEBUG,this,"Failed insert");
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
                preparedDeleteOrderEntrySQL();
            }

            preparedDeleteOrderEntry.setLong  (1, id);
            preparedDeleteOrderEntry.executeUpdate();

        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

/* *************************************** */
/* *** TECHNICAL TEST METHODS          *** */
/* *************************************** */

    /**
     *  Clears the technical test xref table of all entries.
     *
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized void clearTestXref() throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();
            sb.append("delete from test_xref ");
            String sqlTxt = sb.toString();

            Statement stmt = connect.createStatement();
            stmt.execute(sqlTxt);
        } catch (SQLException e) {
            throw new IOException(e.toString());
        } finally {
            if (connect != null) {
                dbcp.returnConnection(connect);
            }
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
                preparedInsertTestXrefSQL();
            }

            preparedInsertTestXref.setString(1, contract.getSymbol());
            preparedInsertTestXref.setString(2, contract.getMonthAsKey());
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
     *  @return                 returns collection of the rows returned
     *  @throws IOException     There was a problem accessing the database table
     */
    public synchronized Collection getBestTestXref(Contract contract, int termTop, int termBottom, int numberToReturn) throws IOException {
        Collection negProfits = new LinkedList();
        try {
            if (preparedSelectBestTestXref == null) {
                preparedSelectBestTestXrefSQL();
            }

            preparedSelectBestTestXref.setString(1, contract.getSymbol());
            preparedSelectBestTestXref.setString(2, contract.getMonthAsKey());
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


/* *************************************** */
/* *** PREPARE SQL                     *** */
/* *************************************** */


    private static void preparedSelectContractsSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("select id, commodity_id, contract_month, price " +
                  "from contract " +
                  "where commodity_id = ? " +
                  "  and contract_sort >= ? ");
        if (prototype.commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
            sb.append("  and inactive = 0 ");
        }

        String sqlTxt = sb.toString();
        preparedSelectContracts = connect.prepareStatement(sqlTxt);
    }

    private static void preparedSelectContractSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("select id, commodity_id, contract_month, price " +
                  "from contract " +
                  "where commodity_id = ? " +
                  "  and contract_month = ? ");
        if (prototype.commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
            sb.append("  and inactive = 0 ");
        }
        String sqlTxt = sb.toString();
        preparedSelectContract = connect.prepareStatement(sqlTxt);
    }

    private static void preparedUpdateContractSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("update Contract " +
                  "   set Price=? " +
                  " where Commodity_Id = ? " +
                  "   and Contract_Sort = ? ");

        String sqlTxt = sb.toString();
        preparedUpdateContract = connect.prepareStatement(sqlTxt);
    }

    private static void preparedSelectPricesSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("select date, open_tick, high_tick, low_tick, close_tick, volume, open_interest " +
                  "from Daily_Prices " +
                  "where Contract_Id = ? " +
//                  "  and date <= ? " +
                  "order by date ");
        String sqlTxt = sb.toString();
        preparedSelectPrices = connect.prepareStatement(sqlTxt);
    }

    private static void preparedSelectCurrentPricesSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("select date, open_tick, high_tick, low_tick, close_tick, volume, open_interest " +
                  "from Daily_Prices " +
                  "where Contract_Id = ? " +
//                  "  and date <= ? " +
                  "order by date desc");
        String sqlTxt = sb.toString();
        preparedSelectCurrentPrices = connect.prepareStatement(sqlTxt);
    }

    private static void preparedInsertDailyPriceSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("insert into Daily_Prices(Contract_Id, Date, open_tick, high_tick, low_tick, close_tick, volume, open_interest) " +
                  "values (?, ?, ?, ?, ?, ?, ?, ?) ");

        String sqlTxt = sb.toString();
        preparedInsertDailyPrice = connect.prepareStatement(sqlTxt);
    }

    private static void preparedUpdateDailyPriceSQL () throws SQLException {
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
                  "   and Date = ? ");

        String sqlTxt = sb.toString();
        preparedUpdateDailyPrice = connect.prepareStatement(sqlTxt);
    }

    private static void preparedInsertCommodityPriceLocationSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("insert into Commodity_Price_Pages(page, loc, symbol, title) " +
                  "values (?, ?, ?, ?) ");

        String sqlTxt = sb.toString();
        preparedInsertCommodityPriceLocation = connect.prepareStatement(sqlTxt);
    }

    private static void preparedDeleteCommodityPriceLocationPageSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("delete from Commodity_Price_Pages " +
                  "where page = ? ");

        String sqlTxt = sb.toString();
        preparedDeleteCommodityPriceLocationPage = connect.prepareStatement(sqlTxt);
    }

    private static void preparedInsertCommoditySQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("insert into Commodity(Id, Exchange, Name, Unit_Type, Unit_Size, Unit_Price, Tick_Size, " +
                  "Tick_Price, Tick_Daily_Limit, Contract_Standard_Price, Display_Decimals, Open_Outcry_Symbol, " +
                  "ACM_Symbol, Trade_Month_Mask, Database_Symbol) " +
                  "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

        String sqlTxt = sb.toString();
        preparedInsertCommodity = connect.prepareStatement(sqlTxt);
    }

    private static void preparedUpdateCommoditySQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("update Commodity " +
                  "   set Exchange=?, " +
                  "       Name=?, " +
                  "       Unit_Type=?, " +
                  "       Unit_Size=?, " +
                  "       Unit_Price=?, " +
                  "       Tick_Size=?, " +
                  "       Tick_Price=?, " +
                  "       Tick_Daily_Limit=?, " +
                  "       Contract_Standard_Price=?, " +
                  "       Display_Decimals=?, " +
                  "       Open_Outcry_Symbol=?, " +
                  "       ACM_Symbol=?, " +
                  "       Trade_Month_Mask=?, " +
                  "       Database_Symbol=? " +
                  " where Id = ? ");

        String sqlTxt = sb.toString();
        preparedUpdateCommodity = connect.prepareStatement(sqlTxt);
    }

    private static void preparedSelectContractLastDateSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("select max(date) " +
                  "from Daily_Prices " +
                  "where Contract_Id = ? " +
                  "  and open_tick > 0 " +
                  "  and high_tick > 0 " +
                  "  and low_tick > 0 " +
                  "  and close_tick > 0 " +
                  "  and volume > 0 " +
                  "  and open_interest > 0 ");

        String sqlTxt = sb.toString();
        preparedSelectContractLastDate = connect.prepareStatement(sqlTxt);
    }

    private static void preparedUpdateOrderEntrySQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("update Orders " +
                  "   set Buy_Sell_Ind=?, " +
                  "       Date_Entered=?, " +
                  "       Nbr_Contracts=?, " +
                  "       Contract_Month=?, " +
                  "       Commodity_Name=?, " +
                  "       Desired_Price=?, " +
                  "       Stop_Price=?, " +
                  "       Status=?, " +
                  "       Actual_Price=?, " +
                  "       Note=?, " +
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

    private static void preparedInsertOrderEntrySQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("insert into Orders( " +
                  "       Id, " +
                  "       Buy_Sell_Ind, " +
                  "       Date_Entered, " +
                  "       Nbr_Contracts, " +
                  "       Contract_Month, " +
                  "       Commodity_Name, " +
                  "       Desired_Price, " +
                  "       Stop_Price, " +
                  "       Status, " +
                  "       Actual_Price, " +
                  "       Note, " +
                  "       Offsetting_Order, " +
                  "       Date_Considered, " +
                  "       Date_Placed, " +
                  "       Date_Filled, " +
                  "       Date_Cancel_Placed, " +
                  "       Date_Cancelled, " +
                  "       Date_Stop_Loss) " +
                  " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

        String sqlTxt = sb.toString();
        preparedInsertOrderEntry = connect.prepareStatement(sqlTxt);
    }

    private static void preparedDeleteOrderEntrySQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("delete from Orders " +
                  "where Id = ? ");

        String sqlTxt = sb.toString();
        preparedDeleteOrderEntry = connect.prepareStatement(sqlTxt);
    }

    private static void preparedInsertTestXrefSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("insert into Test_Xref(commodity_id, contract_month, test_class, test_id, test_interval, success_ratio, profit_loss) " +
                  "values (?, ?, ?, ?, ?, ?, ?) ");

        String sqlTxt = sb.toString();
        preparedInsertTestXref = connect.prepareStatement(sqlTxt);
    }

    private static void preparedSelectBestTestXrefSQL () throws SQLException {
        Connection connect = dbcp.retrieveConnection();

        StringBuffer sb = new StringBuffer();
        sb.append("select test_class, test_id, success_ratio, profit_loss " +
                  "from test_xref " +
                  "where Commodity_Id = ? " +
                  "  and Contract_Month = ? " +
                  "  and Test_Interval > ? " +
                  "  and Test_Interval <= ? " +
//                  "  and Profit_Loss > 0 " +
                  "order by Success_Ratio desc, Profit_Loss desc ");

        String sqlTxt = sb.toString();
        preparedSelectBestTestXref = connect.prepareStatement(sqlTxt);
    }


/* *************************************** */
/* *** LOAD LOOKUP TABLES              *** */
/* *************************************** */

    /**
     *  Loads all of the months from the month_lookup table and returns
     *  an itertor of Month objects.
     *
     *  @return Iterator of Month objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized Iterator getMonths() throws IOException {
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

    /**
     *  Loads all of the commodities from the commodity table and returns
     *  an itertor of Commodity objects.
     *
     *  @return Iterator of Commodity objects
     *  @throws IOException    There was a problem accessing the database table
     */
    public synchronized Iterator getCommodities() throws IOException {
        Connection connect = null;
        try {
            connect = dbcp.retrieveConnection();

            StringBuffer sb = new StringBuffer();

            sb.append("select  id, exchange, name, unit_type, unit_size, unit_price, " +
                      "        tick_size, tick_price, tick_daily_limit, Contract_Standard_Price, display_decimals, open_outcry_symbol, " +
                      "        acm_symbol, trade_month_mask, database_symbol " +
                      "from Commodity ");
            if (prototype.commodities.simulator.CommoditySimulator.SIMULATED_RUN) {
                sb.append("  where inactive = 0 ");
            }

            String sqlTxt = sb.toString();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTxt);
            Collection collect = new LinkedHashSet();
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
                      "from Commodity_Price_Pages ");

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
}