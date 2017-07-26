/*  x Properly Documented
 */
package commodities.order;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;

import commodities.account.*;
import commodities.commodity.*;
import commodities.contract.*;
import commodities.dataaccess.*;
import commodities.event.*;
import commodities.util.*;

import com.util.FormatDate;

/**
 *  The ContractOrderTablePanel contains the table of contracts that are
 *  outstanding.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */
public class ContractOrderTablePanel extends JPanel {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The singleton instance of the DataManagerFactory. */
    private static DataManagerInterface dataManager = DataManagerFactory.instance();

    /** Properties file identifier for the properties manager. */
    private static final String PROPERTIES_FILE = "commodities";
    /** The property name for date formatting on the order screen. */
    private static final String DATE_FORMAT = "dateformat.orderscreen";
    /** Date format */
    private static String dateFormat = PropertyManager.instance().getProperties(PROPERTIES_FILE).getProperty(DATE_FORMAT);

    /** Column of the buy date. */
    private static  final   int     BUY_DATE        = 0;
    /** Column of the number of contracts to buy. */
    private static  final   int     BUY_NUMBER      = 1;
    /** Column of the month of the contract to buy. */
    private static  final   int     BUY_MONTH       = 2;
    /** Column of the commodity of the contract to buy. */
    private static  final   int     BUY_COMMODITY   = 3;
    /** Column of the price of the contract to buy. */
    private static  final   int     BUY_PRICE       = 4;
    /** Column of the stop loss of the contract to buy. */
    private static  final   int     BUY_STOP        = 5;
    /** Column of the status of the contract to buy. */
    private static  final   int     BUY_STATUS      = 6;
    /** Column of the actual price of the contract bought. */
    private static  final   int     BUY_ACTUAL      = 7;
    /** Column of the notes on the contract to buy. */
    private static  final   int     BUY_NOTE        = 8;
    /** Column of a break between the buy and sell sections. */
    private static  final   int     BUY_BREAK       = 9;
    /** Column of the sell date. */
    private static  final   int     SELL_DATE       = 10;
    /** Column of the number of contracts to sell. */
    private static  final   int     SELL_NUMBER     = 11;
    /** Column of the month of the contract to sell. */
    private static  final   int     SELL_MONTH      = 12;
    /** Column of the commodity of the contract to sell. */
    private static  final   int     SELL_COMMODITY  = 13;
    /** Column of the price of the contract to sell. */
    private static  final   int     SELL_PRICE      = 14;
    /** Column of the stop loss of the contract to sell. */
    private static  final   int     SELL_STOP       = 15;
    /** Column of the status of the contract to sell. */
    private static  final   int     SELL_STATUS     = 16;
    /** Column of the actual price of the contract sold. */
    private static  final   int     SELL_ACTUAL     = 17;
    /** Column of the notes on the contract to sell. */
    private static  final   int     SELL_NOTE       = 18;
    /** Column of a break between the sell and profit sections. */
    private static  final   int     SELL_BREAK      = 19;
    /** Column of the profit amount from a bought and sold contract. */
    private static  final   int     PROFIT          = 20;

    /** A contract order status of no status */
    public  static  final   String  NO_STATUS       = " ";
    /** A contract order status of considering the order */
    public  static  final   String  CONSIDERATION   = "Consideration";
    /** A contract order status of the order is placed */
    public  static  final   String  PLACED          = "Placed";
    /** A contract order status of the order is filled */
    public  static  final   String  FILLED          = "Filled";
    /** A contract order status of a cancel order has been placed */
    public  static  final   String  CANCEL_PLACED   = "Cancel Placed";
    /** A contract order status of an order has been cancelled */
    public  static  final   String  CANCELLED       = "Cancelled";
    /** A contract order status of a stoploss has been taken */
    public  static  final   String  STOP_LOSS       = "Stop Loss";
    /** The weighting of order statuses for sorting purposes */
    private static  final   int[] STATUS_SORT_WEIGHTS = {0, 1, 2, 3, 2, 0, 3}; // based on the order STATUSES is filled.

    /** A table of valid status values that can be taken based on the current status value */
    //                                                             00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
    private static  final   boolean[][] STATUS_CHANGES      = {{ true,  true, false, false, false, false, false},  // 00 - NO_STATUS
                                                               { true,  true,  true, false, false, false, false},  // 01 - CONSIDERATION
                                                               {false,  true,  true,  true,  true, false, false},  // 02 - PLACED
                                                               {false, false,  true,  true, false, false,  true},  // 03 - FILLED
                                                               {false, false,  true,  true,  true,  true, false},  // 04 - CANCEL_PLACED
                                                               {false, false, false, false,  true,  true, false},  // 05 - CANCELLED
                                                               { true, false, false, false, false, false,  true}}; // 06 - STOP_LOSS
    /** A table of valid status combinations between a buy and sell order. */
    //                                                             00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
    private static  final   boolean[][] STATUS_COMBINATIONS = {{ true,  true,  true,  true,  true,  true,  true},  // 00 - NO_STATUS
                                                               { true,  true,  true,  true,  true,  true,  true},  // 01 - CONSIDERATION
                                                               { true,  true,  true,  true,  true,  true,  true},  // 02 - PLACED
                                                               { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                               { true,  true,  true,  true,  true,  true,  true},  // 04 - CANCEL_PLACED
                                                               { true, false,  true,  true,  true,  true, false},  // 05 - CANCELLED
                                                               {false, false, false,  true, false, false, false}}; // 06 - STOP_LOSS


/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The JTable the orders are displayed in. */
    private JTable table;
    /** The table model used for displaying the table. */
    private ContractTableModel tableModel;
    /** The scrollable pane to hold the table. */
    private JScrollPane jsp;
    /** The preferred size of this JPanel */
    private Dimension preferredSize = new Dimension(0,0);

    /** A selection box with the possible contract months. */
    JComboBox monthComboBox = new JComboBox();
    /** A selection box with the commodities */
    JComboBox commodityComboBox = new JComboBox();
    /** A selection box of the order statuses. */
    JComboBox statusComboBox;
    /** A checkbox indicating if the order is a stop order. */
    JCheckBox stopCheckbox = new JCheckBox();

    /** A list of the possible order statuses */
    private ArrayList STATUSES      = new ArrayList(7);

    /** The account that owns the orders. */
    Account account;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  ContractTableModel is a table model for controlling the look and feel
     *  of the order table.
     *
     *  @author J.R. Titko
     */
    private class ContractTableModel extends AbstractTableModel implements PriceListener {
        /** The editable state of the date cells based on the status of the buy and sell order. */
        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_DATE_CELL      = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           {false, false, false, false, false, false, false},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           {false, false, false, false, false, false, false}}; // 06 - STOP_LOSS

        /** The editable state of the number of contract cells based on the status of the buy and sell order. */
        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_NUMBER_CELL    = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           { true,  true,  true,  true,  true,  true,  true}}; // 06 - STOP_LOSS

        /** The editable state of the commodity cells based on the status of the buy and sell order. */
        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_COMMODITY_CELL = {{ true, false, false, false, false, false, false},  // 00 - NO_STATUS
                                                           {false, false, false, false, false, false, false},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           {false, false, false, false, false, false, false},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           {false, false, false, false, false, false, false}}; // 06 - STOP_LOSS

        /** The editable state of the price cells based on the status of the buy and sell order. */
        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_PRICE_CELL     = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           {false, false, false, false, false, false, false},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           {false, false, false, false, false, false, false}}; // 06 - STOP_LOSS

        /** The editable state of the stoploss cells based on the status of the buy and sell order. */
        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_STOP_CELL      = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           { true,  true,  true,  true,  true,  true,  true},  // 02 - PLACED
                                                           { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           { true,  true,  true,  true,  true,  true,  true}}; // 06 - STOP_LOSS

        /** The editable state of the actual price cells based on the status of the buy and sell order. */
        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_ACTUAL_CELL    = {{false, false, false, false, false, false, false},  // 00 - NO_STATUS
                                                           {false, false, false, false, false, false, false},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           { true,  true,  true,  true,  true,  true,  true}}; // 06 - STOP_LOSS

        /** The array of column names */
        private String columnNames[] = {"B.Date", "B.#", "B.M", "B.Commodity", "B.Price", "S.Stop", "B.Status", "B.Actual", "B.Note", ".", "S.Date", "S.#", "S.M", "S.Commodity", "S.Price", "B.Stop", "S.Status", "S.Actual", "S.Note", ",", "Profit"};
        /** A list of the row data in the table. */
        private ArrayList rowData = new ArrayList();

    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Add an order to the last row of the table.  If the last row in the
         *  table has an order in it after the orders are consolidated,
         *  a new row is added to the table.
         *
         *  @param  order   the contract order to add
         */
        public void addOrder(ContractOrder order) {
            rowData.set(rowData.size() - 1, createSupportContractOrderLine(order));
            consolidateOrders(order, rowData.size() - 1);
            fireTableRowsUpdated(rowData.size() - 1, rowData.size() - 1);
            if (!(NO_STATUS.equals(((ContractOrderLine)rowData.get(rowData.size() - 1)).getLongOrder().getStatus())) ||
                !(NO_STATUS.equals(((ContractOrderLine)rowData.get(rowData.size() - 1)).getShortOrder().getStatus()))) {
                addRow();
            }
//            Commodity commodity = Commodity.byNameExchange(order.getCommodityName());
//            Contract contract = commodity.getContract(Contract.convertFormattedMonthToKey(order.getMonth()));
//            contract.addPriceListener(this);
            Commodity.byNameExchange(order.getCommodityName()).getContract(Contract.convertFormattedMonthToKey(order.getMonth())).addPriceListener(this);
        }

        /**
         *  Add a blank row to the table.
         */
        public void addRow() {
            ContractOrderLine line = null;
            if (account != null) {
                line = new ContractOrderLine(account.getAccountNumber());
            } else {
                line = new ContractOrderLine(-1);
            }
            addRow(line);
        }

        /**
         *  Add an order line to the table.
         *
         *  @param  line    the order line to add
         */
        public void addRow(ContractOrderLine line) {
            rowData.add(line);
            fireTableRowsInserted(rowData.size(),rowData.size());
        }

        /**
         *  Add buy and sell order to the table.
         *
         *  @param  buyOrder    the buy order to add
         *  @param  sellOrder   the sell order to add
         */
        public void addRow(ContractOrder buyOrder, ContractOrder sellOrder) {
            ContractOrderLine line = new ContractOrderLine(account.getAccountNumber(), buyOrder, sellOrder);
            addRow(line);
        }

        /**
         *  Remove the given row from the table.
         *
         *  @param  line    the order line to remove
         */
        public void removeRow(ContractOrderLine line) {
            int row = rowData.indexOf(line);
            rowData.remove(row);
            fireTableRowsDeleted(row,row);
        }

        /**
         *  The number of columns in the table.
         *
         *  @return     the number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         *  The number of rows in the table.
         *
         *  @return     the number of rows
         */
        public int getRowCount() {
            return rowData.size();
        }

        /**
         *  Gets the value at the given row and column in the table.  The value
         *  is returned as an object that can be type case into the expected type
         *  of that cell.
         *
         *  @param  row     the row of the item desired
         *  @param  col     the column of the item desired
         *  @return     the object of the cell
         */
        public Object getValueAt(int row, int col) {
            ContractOrderLine line = (ContractOrderLine)rowData.get(row);
            Integer i = null;

            switch(col) {
                case BUY_DATE:      return FormatDate.formatDate(line.getLongOrder().getDate(), dateFormat);
                case BUY_NUMBER:    i = line.getLongOrder().getNumberOfContracts();
                                    return (i.intValue() == 0)?"":i.toString();
                case BUY_MONTH:     return line.getLongOrder().getMonth();
                case BUY_COMMODITY: return line.getLongOrder().getCommodityName();
                case BUY_PRICE:     if ("".equals(line.getLongOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getLongOrder().getDesiredPrice().doubleValue());
                                        if (Double.parseDouble(d) == 0.0) {
                                            return "";
                                        } else if (ContractOrder.MARKET_ORDER.equals(Double.valueOf(d))) {
                                            return "Market";
                                        } else {
                                            return d;
                                        }

//                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case BUY_STOP:      if ("".equals(line.getLongOrder().getCommodityName().trim())) {
                                        return new Boolean(false);
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getLongOrder().getStopPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?new Boolean(false):new Boolean(true);
                                    }
                case BUY_STATUS:    return line.getLongOrder().getStatus();
                case BUY_ACTUAL:    if ("".equals(line.getLongOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getLongOrder().getActualPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case BUY_NOTE:      return line.getLongOrder().getNote();
                case SELL_DATE:     return FormatDate.formatDate(line.getShortOrder().getDate(), dateFormat);
                case SELL_NUMBER:   i = line.getShortOrder().getNumberOfContracts();
                                    return (i.intValue() == 0)?"":i.toString();
                case SELL_MONTH:    return line.getShortOrder().getMonth();
                case SELL_COMMODITY:return line.getShortOrder().getCommodityName();
                case SELL_PRICE:    if ("".equals(line.getShortOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getShortOrder().getDesiredPrice().doubleValue());
                                        if (Double.parseDouble(d) == 0.0) {
                                            return "";
                                        } else if (ContractOrder.MARKET_ORDER.equals(Double.valueOf(d))) {
                                            return "Market";
                                        } else {
                                            return d;
                                        }
//                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case SELL_STOP:     if ("".equals(line.getShortOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getShortOrder().getStopPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case SELL_STATUS:   return line.getShortOrder().getStatus();
                case SELL_ACTUAL:   if ("".equals(line.getShortOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getShortOrder().getActualPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case SELL_NOTE:     return line.getShortOrder().getNote();

                case PROFIT:        return "" + line.getProfit();
            }
            return "";
        }

        /**
         *  Consolidate the orders in the table based on the passed in order and the
         *  row that it currently resides in.  The order will be the order to be consolidated
         *  into a row above its current location.
         *
         *  @param  order   the order to consolidate on
         *  @param  row     the row the order is originally on
         */
        private void consolidateOrders(ContractOrder order, int row) {

            ContractOrderLine   orderLine = (ContractOrderLine)rowData.get(row);
            ContractOrderLine   line = null;
            ContractOrder       origOrder = order;
            ContractOrder       checkOrder = null;
            ContractOrder       offsettingOrder = null;
            int                 orderIndex = STATUSES.indexOf(origOrder.getStatus());
            if (orderIndex == -1) orderIndex = 0;
            int                 checkOrderIndex = 0;
            int                 checkTilRow = row;
            boolean             firstSwap = true;

            for (int i = 0; i < checkTilRow; i++) {
                line = (ContractOrderLine)rowData.get(i);
                if (origOrder.isLongOrder()) {
                    checkOrder = line.getLongOrder();
                    offsettingOrder = line.getShortOrder();
                } else {
                    checkOrder = line.getShortOrder();
                    offsettingOrder = line.getLongOrder();
                }

                checkOrderIndex = STATUSES.indexOf(checkOrder.getStatus());
                if (checkOrderIndex == -1) checkOrderIndex = 0;
                if ((STATUS_SORT_WEIGHTS[checkOrderIndex] < STATUS_SORT_WEIGHTS[orderIndex]) &&
                    ((origOrder.getCommodityName().equals(offsettingOrder.getCommodityName())) &&
                     (origOrder.getMonth().equals(offsettingOrder.getMonth())))) {
                    if (origOrder.isLongOrder()) {
                        line.setLongOrder(origOrder);
                        if ((FILLED.equals(line.getLongOrder().getStatus()) && FILLED.equals(line.getShortOrder().getStatus())) ||
                            (FILLED.equals(line.getLongOrder().getStatus()) && STOP_LOSS.equals(line.getShortOrder().getStatus())) ||
                            (STOP_LOSS.equals(line.getLongOrder().getStatus()) && FILLED.equals(line.getShortOrder().getStatus()))) {
                            line.getLongOrder().setOffsettingOrder(line.getShortOrder().getId());
                            line.getShortOrder().setOffsettingOrder(line.getLongOrder().getId());
                        }
                        orderLine.setLongOrder(checkOrder);
                        fireTableRowsUpdated(i, i);
                        fireTableRowsUpdated(row, row);
                    } else {
                        line.setShortOrder(origOrder);
                        if (FILLED.equals(line.getLongOrder().getStatus()) && FILLED.equals(line.getShortOrder().getStatus())) {
                            line.getLongOrder().setOffsettingOrder(line.getShortOrder().getId());
                            line.getShortOrder().setOffsettingOrder(line.getLongOrder().getId());
                        }
                        orderLine.setShortOrder(checkOrder);
                        fireTableRowsUpdated(i, i);
                        fireTableRowsUpdated(row, row);
                    }
                    balanceContractCount(i);
                    balanceContractCount(row);
                    origOrder = checkOrder;
                }
            }
        }

        /**
         *  Split and consolidate order lines as needed to maintain an equal number
         *  of contract orders on both the buy and sell side of an order line.
         *
         *  @param  row     the row of the order line to balance
         */
        private void balanceContractCount(int row) {
            ContractOrderLine   line = (ContractOrderLine)rowData.get(row);
            ContractOrder       longOrder = line.getLongOrder();
            ContractOrder       shortOrder = line.getShortOrder();
            ContractOrder       newOrder = null;
            int                 longCount = longOrder.getNumberOfContracts().intValue();
            int                 shortCount = shortOrder.getNumberOfContracts().intValue();
            int                 lastRowIndex = rowData.size() - 1;

            if ((longCount == shortCount) ||
                (longCount == 0) ||
                (shortCount == 0) ||
                (NO_STATUS.equals(longOrder.getStatus())) ||
                (NO_STATUS.equals(shortOrder.getStatus()))) {
                return;
            }

            if (longCount > shortCount) {
                if ((newOrder = longOrder.split(longCount - shortCount)) == null) {
                    return;
                }
                rowData.set(lastRowIndex, createSupportContractOrderLine(newOrder));
                fireTableRowsUpdated(row, row);
                fireTableRowsUpdated(lastRowIndex, lastRowIndex);
                consolidateOrders(newOrder, lastRowIndex);
                if (!(NO_STATUS.equals(((ContractOrderLine)rowData.get(lastRowIndex)).getLongOrder().getStatus())) ||
                    !(NO_STATUS.equals(((ContractOrderLine)rowData.get(lastRowIndex)).getShortOrder().getStatus()))) {
                    addRow();
                }
            } else if (longCount < shortCount) {
                if ((newOrder = shortOrder.split(shortCount - longCount)) == null) {
                    return;
                }
                rowData.set(lastRowIndex, createSupportContractOrderLine(newOrder));
                fireTableRowsUpdated(row, row);
                fireTableRowsUpdated(lastRowIndex, lastRowIndex);
                consolidateOrders(newOrder, lastRowIndex);
                if (!(NO_STATUS.equals(((ContractOrderLine)rowData.get(lastRowIndex)).getLongOrder().getStatus())) ||
                    !(NO_STATUS.equals(((ContractOrderLine)rowData.get(lastRowIndex)).getShortOrder().getStatus()))) {
                    addRow();
                }
            }
        }

        /**
         *  Combine like orders onto a single line.  This requires that all of the
         *  information for an order is identical including dates, contracts, prices
         *  and statuses.
         */
        private void combineOrders() {
            int startSearchRow = 0;

            for (int currentRow = (rowData.size() - 1); currentRow > 0; currentRow--) {
                ContractOrderLine   currentOrderLine    = (ContractOrderLine)rowData.get(currentRow);
                ContractOrder       currentLongOrder    = currentOrderLine.getLongOrder();
                ContractOrder       currentShortOrder   = currentOrderLine.getShortOrder();
                int currentLongValue                    = currentLongOrder.getNumberOfContracts().intValue();
                int currentShortValue                   = currentShortOrder.getNumberOfContracts().intValue();

                if ((currentLongValue == 0) &&
                    (currentShortValue == 0) &&
                    (currentRow != (rowData.size() - 1))) {

                    removeRow(currentOrderLine);

                } else if ((currentLongValue > 0) &&
                           (currentShortValue == 0)) {

                    for (int searchRow = startSearchRow; searchRow < currentRow; searchRow++) {
                        ContractOrderLine   searchOrderLine = (ContractOrderLine)rowData.get(searchRow);
                        ContractOrder       searchLongOrder = searchOrderLine.getLongOrder();
                        ContractOrder       searchShortOrder= searchOrderLine.getShortOrder();
                        int searchLongValue                 = searchLongOrder.getNumberOfContracts().intValue();
                        int searchShortValue                = searchShortOrder.getNumberOfContracts().intValue();

                        if (((searchLongValue == 0) ||
                             (searchShortValue == 0)) &&
                            (startSearchRow == 0)) {
                            startSearchRow = searchRow;
                        }

                        if ((searchShortValue == 0) &&
                            (currentLongOrder.equals(searchLongOrder))) {
                            searchLongOrder.combine(currentLongOrder);
                            fireTableRowsUpdated(searchRow, searchRow);
                            removeRow(currentOrderLine);
                            break;
                        }
                    }

                } else if ((currentLongValue == 0) &&
                           (currentShortValue > 0)) {

                    for (int searchRow = startSearchRow; searchRow < currentRow; searchRow++) {
                        ContractOrderLine   searchOrderLine = (ContractOrderLine)rowData.get(searchRow);
                        ContractOrder       searchLongOrder = searchOrderLine.getLongOrder();
                        ContractOrder       searchShortOrder= searchOrderLine.getShortOrder();
                        int searchLongValue                 = searchLongOrder.getNumberOfContracts().intValue();
                        int searchShortValue                = searchShortOrder.getNumberOfContracts().intValue();

                        if (((searchLongValue == 0) ||
                             (searchShortValue == 0)) &&
                            (startSearchRow == 0)) {
                            startSearchRow = searchRow;
                        }

                        if ((searchLongValue == 0) &&
                            (currentShortOrder.equals(searchShortOrder))) {
                            searchShortOrder.combine(currentShortOrder);
                            fireTableRowsUpdated(searchRow, searchRow);
                            removeRow(currentOrderLine);
                            break;
                        }
                    }
                } else if ((currentLongValue > 0) &&
                           (currentShortValue > 0)) {
                    for (int searchRow = 0; searchRow < currentRow; searchRow++) {
                        ContractOrderLine   searchOrderLine = (ContractOrderLine)rowData.get(searchRow);
                        ContractOrder       searchLongOrder = searchOrderLine.getLongOrder();
                        ContractOrder       searchShortOrder= searchOrderLine.getShortOrder();

                        if (currentLongOrder.equals(searchLongOrder) &&
                            currentShortOrder.equals(searchShortOrder)) {
                            searchLongOrder.combine(currentLongOrder);
                            searchShortOrder.combine(currentShortOrder);
                            fireTableRowsUpdated(searchRow, searchRow);
                            removeRow(currentOrderLine);
                            break;
                        }
                    }
                }
            }
        }

        /**
         *  Create a contract order line that contains the requested order and makes an
         *  empty offsetting order to complete the line.
         *
         *  @param  order   the order to include on the line
         *  @return     the order line
         */
        private ContractOrderLine createSupportContractOrderLine(ContractOrder order) {
            ContractOrder offsettingOrder = new ContractOrder(account.getAccountNumber(), !order.isLongOrder());
            offsettingOrder.setMonth(order.getMonth());
            offsettingOrder.setCommodityName(order.getCommodityName());

            if (ContractOrder.LONG == order.isLongOrder()) {
                return new ContractOrderLine(account.getAccountNumber(), order, offsettingOrder);
            } else {
                return new ContractOrderLine(account.getAccountNumber(), offsettingOrder, order);
            }
        }

    /* *************************************** */
    /* *** AbstractTableModel METHODS      *** */
    /* *************************************** */
        /**
         *  Get the name of the table column.
         *
         *  @param  col     the column number to get the name for
         *  @return     the name of the column
         */
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /**
         *  Gets the value at the given row and column in the table.  The value
         *  is returned as an object that can be type case into the expected type
         *  of that cell.
         *
         *  @param  row     the row of the item desired
         *  @param  col     the column of the item desired
         *  @return     the object of the cell
         */
        public void setValueAt(Object value, int row, int col) {
            ContractOrderLine line = (ContractOrderLine)rowData.get(row);
            ContractOrder origOrder = null;
            ContractOrder stopOrder = null;
            ContractOrder offsetOrder = null;
            ContractOrder newOrder  = null;
System.out.println("setValueAt value - " + value);
System.out.println("setValueAt value.class - " + value.getClass().getName());
            String val = null;
            Boolean boolVal = null;
            if (value instanceof String) {
                val = (String)value;
            } else if (value instanceof Boolean) {
                boolVal = (Boolean)value;
            }

            if ((col == BUY_STATUS) || (col == SELL_STATUS)) {
                int newValue = STATUSES.indexOf(val);
                if (newValue == -1) newValue = 0;

                String current = (String)getValueAt(row, col);
                int currentValue = STATUSES.indexOf(current);
                if (currentValue == -1) currentValue = 0;

                String opposite = (String)getValueAt(row, ((col==BUY_STATUS)?SELL_STATUS:BUY_STATUS));
                int oppositeValue = STATUSES.indexOf(opposite);
                if (oppositeValue == -1) oppositeValue = 0;

                if (!STATUS_CHANGES[currentValue][newValue] || !STATUS_COMBINATIONS[oppositeValue][newValue]) {
                    return; // reject change
                }
            }

            if (((col == BUY_NUMBER) && (FILLED.equals((String)table.getValueAt(row, BUY_STATUS)) ||
                                         STOP_LOSS.equals((String)table.getValueAt(row, BUY_STATUS)))) ||
                ((col == SELL_NUMBER) && (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)) ||
                                          STOP_LOSS.equals((String)table.getValueAt(row, SELL_STATUS))))) {

                int currentValue = Integer.parseInt((String)getValueAt(row, col));
                int newValue = 0;
                try {
                    newValue = Integer.parseInt(val.trim());
                    if (newValue < 1) {
                        return; // reject change
                    } else if (currentValue < newValue) {
                        return; // reject change
                    } else if (currentValue != newValue) {
                        //******************************************************
                        // *** STOP_LOSS does not follow FILLED in this case ***
                        //******************************************************
                        if (((col == BUY_NUMBER) && (FILLED.equals((String)table.getValueAt(row, BUY_STATUS)))) ||
                            ((col == SELL_NUMBER) && (FILLED.equals((String)table.getValueAt(row, SELL_STATUS))))) {

                            if ((col == BUY_NUMBER) && (FILLED.equals((String)table.getValueAt(row, BUY_STATUS)))) {
                                origOrder = ((ContractOrderLine)rowData.get(row)).getLongOrder();
                                newOrder = origOrder.split(currentValue - newValue);
                                newOrder.setStatus(PLACED);
                            } else if ((col == SELL_NUMBER) && (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)))) {
                                origOrder = ((ContractOrderLine)rowData.get(row)).getShortOrder();
                                newOrder = origOrder.split(currentValue - newValue);
                                newOrder.setStatus(PLACED);
                            }
                            rowData.set(rowData.size() - 1, createSupportContractOrderLine(newOrder));
                            consolidateOrders(newOrder, rowData.size() - 1);
                            fireTableRowsUpdated(rowData.size() - 1, rowData.size() - 1);
                            if (!(NO_STATUS.equals(((ContractOrderLine)rowData.get(rowData.size() - 1)).getLongOrder().getStatus())) ||
                                !(NO_STATUS.equals(((ContractOrderLine)rowData.get(rowData.size() - 1)).getShortOrder().getStatus()))) {
                                addRow();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    return;
                }
            }

            try {
                switch(col) {
                    case BUY_DATE:      line.getLongOrder().setDate(val.trim());
                                        break;
                    case BUY_NUMBER:    line.getLongOrder().setNumberOfContracts(("".equals(val.trim()))?new Integer(0):Integer.valueOf(val.trim()));
                                        balanceContractCount(row);
                                        combineOrders();
                                        break;
                    case BUY_PRICE:     val = val.trim();
                                        if ("M".equalsIgnoreCase(val.substring(0,1))) {
                                            line.getLongOrder().setDesiredPrice(ContractOrder.MARKET_ORDER);
                                        } else {
                                            line.getLongOrder().setDesiredPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
                                        }
                                        break;

                    case BUY_STOP:      line.getLongOrder().setStopPrice((boolVal.booleanValue())?new Double(1):new Double(0));
                                        break;
                    case BUY_STATUS:    if (STOP_LOSS.equals(val)) {
                                            offsetOrder = ((ContractOrderLine)rowData.get(row)).getShortOrder();

                                            origOrder = ((ContractOrderLine)rowData.get(row)).getLongOrder();
                                            rowData.set(row, createSupportContractOrderLine(origOrder));
                                            stopOrder = ((ContractOrderLine)rowData.get(row)).getShortOrder();
                                            stopOrder.setNumberOfContracts(origOrder.getNumberOfContracts());
                                            stopOrder.setDesiredPrice(origOrder.getStopPrice());
                                            stopOrder.setStatus(STOP_LOSS);

                                            origOrder.setOffsettingOrder(stopOrder.getId());
                                            stopOrder.setOffsettingOrder(origOrder.getId());

                                            fireTableRowsUpdated(row, row);

                                            if (!NO_STATUS.equals(offsetOrder.getStatus())) {
                                                rowData.set(rowData.size() - 1, createSupportContractOrderLine(offsetOrder));
                                                consolidateOrders(((ContractOrderLine)rowData.get(rowData.size() - 1)).getShortOrder(), rowData.size() - 1);
                                                fireTableRowsUpdated(rowData.size() - 1, rowData.size() - 1);
                                                addRow();
                                            }
                                        } else {
                                            line.getLongOrder().setStatus(val);
                                        }
                                        if (FILLED.equals(val) && FILLED.equals(line.getShortOrder().getStatus())) {
                                            line.getLongOrder().setOffsettingOrder(line.getShortOrder().getId());
                                            line.getShortOrder().setOffsettingOrder(line.getLongOrder().getId());
                                        }
                                        if (row == (rowData.size()-1)) {
                                            addRow();
                                        }
                                        balanceContractCount(row);
                                        consolidateOrders(line.getLongOrder(), row);
                                        combineOrders();
                                        break;
                    case BUY_ACTUAL:    line.getLongOrder().setActualPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
                                        fireTableCellUpdated(row, PROFIT);
                                        break;
                    case BUY_NOTE:      line.getLongOrder().setNote(val.trim());
                                        break;
                    case SELL_DATE:     line.getShortOrder().setDate(val.trim());
                                        break;
                    case SELL_NUMBER:   line.getShortOrder().setNumberOfContracts(("".equals(val.trim()))?new Integer(0):Integer.valueOf(val.trim()));
                                        balanceContractCount(row);
                                        combineOrders();
                                        break;
                    case SELL_PRICE:    val = val.trim();
                                        if ("M".equalsIgnoreCase(val.substring(0,1))) {
                                            line.getShortOrder().setDesiredPrice(ContractOrder.MARKET_ORDER);
                                        } else {
                                            line.getShortOrder().setDesiredPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
                                        }
                                        break;
                    case SELL_STOP:     line.getShortOrder().setStopPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
                                        break;
                    case SELL_STATUS:   if (STOP_LOSS.equals(val)) {
                                            offsetOrder = ((ContractOrderLine)rowData.get(row)).getLongOrder();

                                            origOrder = ((ContractOrderLine)rowData.get(row)).getShortOrder();
                                            rowData.set(row, createSupportContractOrderLine(origOrder));
                                            stopOrder = ((ContractOrderLine)rowData.get(row)).getLongOrder();
                                            stopOrder.setNumberOfContracts(origOrder.getNumberOfContracts());
                                            stopOrder.setDesiredPrice(origOrder.getStopPrice());
                                            stopOrder.setStatus(STOP_LOSS);

                                            origOrder.setOffsettingOrder(stopOrder.getId());
                                            stopOrder.setOffsettingOrder(origOrder.getId());

                                            fireTableRowsUpdated(row, row);

                                            if (!NO_STATUS.equals(offsetOrder.getStatus())) {
                                                rowData.set(rowData.size() - 1, createSupportContractOrderLine(offsetOrder));
                                                consolidateOrders(((ContractOrderLine)rowData.get(rowData.size() - 1)).getLongOrder(), rowData.size() - 1);
                                                fireTableRowsUpdated(rowData.size() - 1, rowData.size() - 1);
                                                addRow();
                                            }
                                        } else {
                                            line.getShortOrder().setStatus(val);
                                        }
                                        if (FILLED.equals(val) && FILLED.equals(line.getLongOrder().getStatus())) {
                                            line.getLongOrder().setOffsettingOrder(line.getShortOrder().getId());
                                            line.getShortOrder().setOffsettingOrder(line.getLongOrder().getId());
                                        }
                                        if (row == (rowData.size()-1)) {
                                            addRow();
                                        }
                                        balanceContractCount(row);
                                        consolidateOrders(line.getShortOrder(), row);
                                        combineOrders();
                                        break;
                    case SELL_ACTUAL:   line.getShortOrder().setActualPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
                                        fireTableCellUpdated(row, PROFIT);
                                        break;
                    case SELL_NOTE:     line.getShortOrder().setNote(val.trim());
                                        break;
                    case BUY_COMMODITY: // Drop through to months to fix months
                    case SELL_COMMODITY:line.getLongOrder().setCommodityName(val);
                                        line.getShortOrder().setCommodityName(val);
//                                        fireTableRowsUpdated(row, row);
                                        val = line.getLongOrder().getMonth();
                    case BUY_MONTH:
                    case SELL_MONTH:    if (!"".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim())) {
                                            Iterator it = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).getContracts();
                                            boolean enabled = false;
                                            while (it.hasNext()) {
                                                if (val.equals(((Contract)it.next()).getMonthFormatted())) {
                                                    enabled = true;
                                                    break;
                                                }
                                            }
                                            if (!enabled) { val = ""; }
                                        }
                                        line.getLongOrder().setMonth(val);
                                        line.getShortOrder().setMonth(val);
                                        fireTableRowsUpdated(row, row);
                                        return;
                    default:
                        return;
                }
                fireTableCellUpdated(row, col);
            } catch (NumberFormatException e) {
                return;
            }
        }

        /**
         *  Determines if the cell is editable within the table.
         *
         *  @param  row     the row of the cell
         *  @param  column  the column of the cell
         *  @return     true if the cell is editable
         */
        public boolean isCellEditable(int row, int column) {
            String buyStatus    = (String)table.getValueAt(row, BUY_STATUS);
            int    buyStatusValue = STATUSES.indexOf(buyStatus);
            if (buyStatusValue == -1) buyStatusValue = 0;

            String sellStatus   = (String)table.getValueAt(row, SELL_STATUS);
            int    sellStatusValue = STATUSES.indexOf(sellStatus);
            if (sellStatusValue == -1) sellStatusValue = 0;

            if ((column == BUY_DATE) || (column == SELL_DATE)) {                    // Date columns
                if (column == BUY_DATE) {
                    return EDIT_DATE_CELL[buyStatusValue][sellStatusValue];
                } else if (column == SELL_DATE) {
                    return EDIT_DATE_CELL[sellStatusValue][buyStatusValue];
                }
            } else if ((column == BUY_NUMBER) || (column == SELL_NUMBER)) {         // # columns
                if (column == BUY_NUMBER) {
                    return EDIT_NUMBER_CELL[buyStatusValue][sellStatusValue];
                } else if (column == SELL_NUMBER) {
                    return EDIT_NUMBER_CELL[sellStatusValue][buyStatusValue];
                }
            } else if (((column == BUY_COMMODITY) || (column == SELL_COMMODITY)) || // Commodity columns
                       ((column == BUY_MONTH) || (column == SELL_MONTH))) {         // Month columns
                if ((column == BUY_COMMODITY) || (column == BUY_MONTH)) {
                    return EDIT_COMMODITY_CELL[buyStatusValue][sellStatusValue];
                } else if ((column == SELL_COMMODITY) || (column == SELL_MONTH)) {
                    return EDIT_COMMODITY_CELL[sellStatusValue][buyStatusValue];
                }
            } else if ((column == BUY_PRICE) || (column == SELL_PRICE)) {           // Price columns
                if ("".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim()) ||
                    "".equals(((String)table.getValueAt(row, BUY_MONTH)).trim())) {
                    return false;
                }
                if (column == BUY_PRICE) {
                    return EDIT_PRICE_CELL[buyStatusValue][sellStatusValue];
                } else if (column == SELL_PRICE) {
                    return EDIT_PRICE_CELL[sellStatusValue][buyStatusValue];
                }
            } else if ((column == BUY_STOP) || (column == SELL_STOP)) {             // Stop columns
                if ("".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim()) ||
                    "".equals(((String)table.getValueAt(row, BUY_MONTH)).trim())) {
                    return false;
                }
                if ((FILLED.equals((String)table.getValueAt(row, BUY_STATUS)) ||
                     STOP_LOSS.equals((String)table.getValueAt(row, BUY_STATUS))) &&
                    (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)) ||
                     STOP_LOSS.equals((String)table.getValueAt(row, SELL_STATUS)))) {
                    return false;
                }
                if (column == BUY_STOP) {
                    return EDIT_STOP_CELL[buyStatusValue][sellStatusValue];
                } else if (column == SELL_STOP) {
                    return EDIT_STOP_CELL[sellStatusValue][buyStatusValue];
                }
            } else if (column == BUY_STATUS) {                                      // Buy Status columns
                if ("".equals(((String)table.getValueAt(row, BUY_NUMBER)).trim())       ||
                    "".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim())    ||
                    "".equals(((String)table.getValueAt(row, BUY_MONTH)).trim())        ||
                    "".equals(((String)table.getValueAt(row, BUY_PRICE)).trim())) {
                    return false;
                } else {
                    return true;
                }
            } else if (column == SELL_STATUS) {                                     // Sell Status columns
                if ("".equals(((String)table.getValueAt(row, SELL_NUMBER)).trim())      ||
                    "".equals(((String)table.getValueAt(row, SELL_COMMODITY)).trim())   ||
                    "".equals(((String)table.getValueAt(row, SELL_MONTH)).trim())       ||
                    "".equals(((String)table.getValueAt(row, SELL_PRICE)).trim())) {
                    return false;
                } else {
                    return true;
                }
            } else if ((column == BUY_ACTUAL) || (column == SELL_ACTUAL)) {         // Actual columns
                if ("".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim()) ||
                    "".equals(((String)table.getValueAt(row, BUY_MONTH)).trim())) {
                    return false;
                }
                if (column == BUY_ACTUAL) {
                    return EDIT_ACTUAL_CELL[buyStatusValue][sellStatusValue];
                } else if (column == SELL_ACTUAL) {
                    return EDIT_ACTUAL_CELL[sellStatusValue][buyStatusValue];
                }
            } else if ((column == BUY_NOTE) || (column == SELL_NOTE)) {             // note columns
                if ("".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim()) ||
                    "".equals(((String)table.getValueAt(row, BUY_MONTH)).trim())) {
                    return false;
                }
                return true;
            } else if ((column == BUY_BREAK) || (column == SELL_BREAK)) {                // Divider cells
                return false;
            } else if (column == PROFIT) {                                          // Profit columns
                return false;
            }
            return false;
        }

    /* *************************************** */
    /* *** PriceListener METHODS           *** */
    /* *************************************** */
        /**
         *  Invoked when the date changes.
         *  @param  e   the PriceEvent
         */
        public void changeDate(PriceEvent e) { }

        /**
         *  Invoked when an open price changes.
         *  @param  e   the PriceEvent
         */
        public void changeOpenPrice(PriceEvent e) { }

        /**
         *  Invoked when a high price changes.
         *  @param  e   the PriceEvent
         */
        public void changeHighPrice(PriceEvent e) { }

        /**
         *  Invoked when a low price changes.
         *  @param  e   the PriceEvent
         */
        public void changeLowPrice(PriceEvent e) { }

        /**
         *  Invoked when a close price changes.
         *  @param  e   the PriceEvent
         */
        public void changeClosePrice(PriceEvent e) {
            boolean found = false;
            for (int i = 0; i  < rowData.size() - 1; i++) {
                ContractOrder order = ((ContractOrderLine)rowData.get(i)).getLongOrder();
                Contract contract = Commodity.byNameExchange(order.getCommodityName()).getContract(Contract.convertFormattedMonthToKey(order.getMonth()));
                if (e.getContract().equals(contract)) {
                    fireTableRowsUpdated(i, i);
                    found = true;
                }
            }
            if (!found) {
                e.getContract().removePriceListener(this);
            }

//            fireTableDataChanged();
//            setValueAt(commodity.formatPrice(e.getPrice().doubleValue()), contractData.indexOf(e.getContract()), CLOSE_COLUMN);
        }

        /**
         *  Invoked when the volume changes.
         *  @param  e   the PriceEvent
         */
        public void changeVolume(PriceEvent e) { }

        /**
         *  Invoked when the open interest changes.
         *  @param  e   the PriceEvent
         */
        public void changeOpenInterest(PriceEvent e) { }
    }

    /**
     *  ContractTableCellRenderer is the cell renderer for the contract order table.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    private class ContractTableCellRenderer extends DefaultTableCellRenderer {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  Create a table cell renderer.
         */
        public ContractTableCellRenderer() {
            setOpaque(true);
        }

    /* *************************************** */
    /* *** DefaultTableCellRenderer METHODS*** */
    /* *************************************** */
        /**
         *  Renders the table cells for the contract order table.
         *
         *  @param table        the JTable
         *  @param value        the value to assign to the cell at [row, column]
         *  @param isSelected   true if cell is selected
         *  @param row          the row of the cell to render
         *  @param column       the column of the cell to render
         *  @param hasFocus     if true, render cell appropriately. For example, put a special border
         *                      on the cell, if the cell can be edited, render in the color used to indicate editing
         *  @return     the default table cell renderer
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
            if ((column == BUY_DATE) || (column == SELL_DATE)) {                    // Date columns
                setHorizontalAlignment(CENTER);
                if (table.isCellEditable(row, column)) {
                    setBackground(Color.CYAN);
                }
            } else if ((column == BUY_NUMBER) || (column == SELL_NUMBER)) {         // # columns
                setHorizontalAlignment(RIGHT);
                if (table.isCellEditable(row, column)) {
                    if (((column == BUY_NUMBER) && (FILLED.equals((String)table.getValueAt(row, BUY_STATUS)) ||
                                                    STOP_LOSS.equals((String)table.getValueAt(row, BUY_STATUS)))) ||
                        ((column == SELL_NUMBER) && (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)) ||
                                                     STOP_LOSS.equals((String)table.getValueAt(row, SELL_STATUS))))) {
                        setBackground(Color.PINK);
                    } else {
                        setBackground(Color.CYAN);
                    }
                }
            } else if ((column == BUY_MONTH) || (column == SELL_MONTH)) {           // Month columns
                setFont(new Font(getFont().getName(), Font.BOLD, (getFont().getSize() - 1)));
                setHorizontalAlignment(RIGHT);
                setBackground(Color.ORANGE);
                setForeground(Color.BLACK);
            } else if ((column == BUY_COMMODITY) || (column == SELL_COMMODITY)) {   // Commodity columns
                setFont(new Font(getFont().getName(), Font.BOLD, (getFont().getSize() - 1)));
                setHorizontalAlignment(LEFT);
                setBackground(Color.YELLOW);
                setForeground(Color.BLACK);
            } else if ((column == BUY_PRICE) || (column == SELL_PRICE)) {           // Price columns
                setHorizontalAlignment(RIGHT);
                if (table.isCellEditable(row, column)) {
                    setBackground(Color.CYAN);
                }
            } else if ((column == BUY_STOP) || (column == SELL_STOP)) {             // Stop columns
                setHorizontalAlignment(CENTER);
                if (table.isCellEditable(row, column)) {
                    setBackground(Color.CYAN);
                }
            } else if ((column == BUY_STATUS) || (column == SELL_STATUS)) {         // Status columns
                if ((FILLED.equals(table.getValueAt(row, column))) ||
                    (STOP_LOSS.equals(table.getValueAt(row, column)))) {
                    setBackground(Color.RED);
                    setForeground(Color.WHITE);
                } else if ((PLACED.equals(table.getValueAt(row, column))) ||
                           (CANCEL_PLACED.equals(table.getValueAt(row, column)))) {
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                setHorizontalAlignment(CENTER);
            } else if ((column == BUY_ACTUAL) || (column == SELL_ACTUAL)) {         // Actual columns
                setHorizontalAlignment(RIGHT);
                if (table.isCellEditable(row, column)) {
                    setBackground(Color.PINK);
                }
            } else if ((column == BUY_MONTH) || (column == SELL_MONTH)) {           // Month columns
                setHorizontalAlignment(RIGHT);
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            } else if ((column == BUY_BREAK) || (column == SELL_BREAK)) {                // Divider cells
                setHorizontalAlignment(CENTER);
                setBackground(Color.BLACK);
// Make this into a border if possible


            } else if (column == PROFIT) {                                          // Profit columns
                setHorizontalAlignment(RIGHT);
                setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                if (value != null) {
                    double net = Double.parseDouble((String)value);

                    if (((FILLED.equals(table.getValueAt(row, BUY_STATUS))) ||
                         (STOP_LOSS.equals(table.getValueAt(row, BUY_STATUS)))) &&
                        ((FILLED.equals(table.getValueAt(row, SELL_STATUS))) ||
                         (STOP_LOSS.equals(table.getValueAt(row, SELL_STATUS))))) {

                        if (net < 0.0) {
                            setBackground(Color.RED);
                            setForeground(Color.WHITE);
                        } else {
                            setBackground(Color.ORANGE);
                            setForeground(Color.BLACK);
                        }
                    } else {
                        if (net < 0.0) {
                            setBackground(Color.PINK);
                            setForeground(Color.GRAY);
                        } else {
                            setBackground(Color.YELLOW);
                            setForeground(Color.GRAY);
                        }
                    }
                }
            } else {
                setHorizontalAlignment(CENTER);
            }

            return this;
        }
    }

    /**
     *  StatusListCellRenderer is the renderer for the drop down selection box of status codes.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    private class StatusListCellRenderer extends JLabel implements ListCellRenderer {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  Create a drop down selection box renderer.
         */
        public StatusListCellRenderer() {
            setOpaque(true);
        }

    /* *************************************** */
    /* *** ListCellRenderer METHODS        *** */
    /* *************************************** */
        /**
         *  Renders the drop down list for the status codes.
         *
         *  @param list         The JList we're painting.
         *  @param value        The value returned by list.getModel().getElementAt(index).
         *  @param index        The cells index.
         *  @param isSelected   True if the specified cell was selected.
         *  @param cellHasFocus True if the specified cell has the focus.
         *  @return     A component whose paint() method will render the specified value
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            int col = table.getSelectedColumn();
            int row = table.getSelectedRow();

            String s = (value == null)?"":value.toString();
            setText(s);
            setFont(list.getFont());

            if (isSelected) {
                setBackground(Color.BLACK);
                setForeground(Color.YELLOW);
            } else {
                setBackground(Color.YELLOW);
                setForeground(Color.BLACK);
            }

            if ((row == -1) || (col == -1) || (index == -1) || ((col != BUY_STATUS) && (col != SELL_STATUS))) {
                setEnabled(list.isEnabled());
            } else {
                String current = (String)table.getValueAt(row, col);
                int currentValue = STATUSES.indexOf(current);
                if (currentValue == -1) currentValue = 0;

                String opposite = (String)table.getValueAt(row, ((col==BUY_STATUS)?SELL_STATUS:BUY_STATUS));
                int oppositeValue = STATUSES.indexOf(opposite);
                if (oppositeValue == -1) oppositeValue = 0;
                setEnabled(STATUS_CHANGES[currentValue][index] && STATUS_COMBINATIONS[oppositeValue][index]);
            }

            return this;
        }
    }

    /**
     *  StatusListCellRenderer is the renderer for the drop down selection box of months.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    private class MonthListCellRenderer extends JLabel implements ListCellRenderer {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  Create a drop down selection box renderer.
         */
        public MonthListCellRenderer() {
            setOpaque(true);
        }

        /**
         *  Renders the drop down list for the months.
         *
         *  @param list         The JList we're painting.
         *  @param value        The value returned by list.getModel().getElementAt(index).
         *  @param index        The cells index.
         *  @param isSelected   True if the specified cell was selected.
         *  @param cellHasFocus True if the specified cell has the focus.
         *  @return     A component whose paint() method will render the specified value
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            int col = table.getSelectedColumn();
            int row = table.getSelectedRow();

            String s = (value == null)?"":value.toString();
            setText(s);
//            setFont(list.getFont());
            setFont(new Font(list.getFont().getName(), Font.PLAIN, (list.getFont().getSize() - 1)));

            if (isSelected) {
                setBackground(Color.BLACK);
                setForeground(Color.YELLOW);
            }
            else {
                setBackground(Color.YELLOW);
                setForeground(Color.BLACK);
            }

            if ((row == -1) || (col == -1) || (index == -1) || ("".equals(((String)table.getValueAt(row, BUY_COMMODITY)).trim()))) {
                setEnabled(list.isEnabled());
            } else {
                String month = (String)value;
                String commodity = (String)table.getValueAt(row, BUY_COMMODITY);
                Iterator it = Commodity.byNameExchange(commodity).getContracts();
                boolean enable = false;
                while (it.hasNext()) {
                    if (month.equals(((Contract)it.next()).getMonthFormatted())) {
                        enable = true;
                        break;
                    }
                }
                setEnabled(enable);
            }
            return this;
        }
    }

    /**
     *  StatusListCellRenderer is the renderer for the drop down selection box of commodities.
     *
     *  @author J.R. Titko
     *  @version 1.0
     *  @update 2004.11.11
     */
    private class CommodityListCellRenderer extends JLabel implements ListCellRenderer {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /**
         *  Create a drop down selection box renderer.
         */
        public CommodityListCellRenderer() {
            setOpaque(true);
        }

        /**
         *  Renders the drop down list for the commodities.
         *
         *  @param list         The JList we're painting.
         *  @param value        The value returned by list.getModel().getElementAt(index).
         *  @param index        The cells index.
         *  @param isSelected   True if the specified cell was selected.
         *  @param cellHasFocus True if the specified cell has the focus.
         *  @return     A component whose paint() method will render the specified value
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            int col = table.getSelectedColumn();
            int row = table.getSelectedRow();

            String s = (value == null)?"":value.toString();
            setText(s);
//            setFont(list.getFont());
            setFont(new Font(list.getFont().getName(), Font.PLAIN, (list.getFont().getSize() - 1)));

            if (isSelected) {
                setBackground(Color.BLACK);
                setForeground(Color.YELLOW);
            }
            else {
                setBackground(Color.YELLOW);
                setForeground(Color.BLACK);
            }

            return this;
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create the panel and setup the table to hold the contracts.
     */
    public ContractOrderTablePanel() {
        this(null);
    }

    /**
     *  Create the panel and setup the table to hold the contracts.
     *
     *  @param  account The account currently being tracked.
     */
    public ContractOrderTablePanel(Account account) {
        this.account = account;
        setupGUI();

        if (account != null) {
            tableModel.addRow();
            loadTable();
        }
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the display of the commodity order table.
     */
    private void setupGUI() {
        setLayout(new BorderLayout());

        tableModel = new ContractTableModel();
        table = new JTable(tableModel) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new ContractTableCellRenderer();
            }
        };
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(getFont().getSize() * 2);

        int cm = CommodityCalendar.getInstance().get(Calendar.MONTH);
        String cy = ("" + CommodityCalendar.getInstance().get(Calendar.YEAR)).substring(2);
        for (int i = 0; i < 12; i++) {
            monthComboBox.addItem(Month.byNumber(cm++).getAbbrev() + " " + cy);
            if (cm > 11) {
                cm -= 12;
                cy = ("" + (CommodityCalendar.getInstance().get(Calendar.YEAR) + 1)).substring(2);
            }
        }
        monthComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        monthComboBox.setRenderer(new MonthListCellRenderer());

        Map commodityNameMap = Commodity.getNameMap();
        commodityComboBox.addItem(" ");
        Iterator it = commodityNameMap.keySet().iterator();
        while (it.hasNext()) {
            commodityComboBox.addItem((String)it.next());
        }
        commodityComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        commodityComboBox.setRenderer(new CommodityListCellRenderer());

        stopCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });

        STATUSES.add(NO_STATUS);
        STATUSES.add(CONSIDERATION);
        STATUSES.add(PLACED);
        STATUSES.add(FILLED);
        STATUSES.add(CANCEL_PLACED);
        STATUSES.add(CANCELLED);
        STATUSES.add(STOP_LOSS);
        statusComboBox = new JComboBox(STATUSES.toArray(new String[STATUSES.size()]));
        statusComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        statusComboBox.setRenderer(new StatusListCellRenderer());

        // Column size for Buy
        table.getColumn(tableModel.getColumnName(BUY_DATE)).setPreferredWidth(35);
        table.getColumn(tableModel.getColumnName(BUY_NUMBER)).setPreferredWidth(30);
        table.getColumn(tableModel.getColumnName(BUY_MONTH)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(BUY_MONTH)).setCellEditor(new DefaultCellEditor(monthComboBox));
        table.getColumn(tableModel.getColumnName(BUY_COMMODITY)).setPreferredWidth(125);
        table.getColumn(tableModel.getColumnName(BUY_COMMODITY)).setCellEditor(new DefaultCellEditor(commodityComboBox));
        table.getColumn(tableModel.getColumnName(BUY_PRICE)).setPreferredWidth(50);
        table.getColumn(tableModel.getColumnName(BUY_STOP)).setPreferredWidth(50);
        table.getColumn(tableModel.getColumnName(BUY_STOP)).setCellEditor(new DefaultCellEditor(stopCheckbox));
        table.getColumn(tableModel.getColumnName(BUY_STATUS)).setPreferredWidth(90);
        table.getColumn(tableModel.getColumnName(BUY_STATUS)).setCellEditor(new DefaultCellEditor(statusComboBox));
        table.getColumn(tableModel.getColumnName(BUY_ACTUAL)).setPreferredWidth(50);
        table.getColumn(tableModel.getColumnName(BUY_NOTE)).setPreferredWidth(150);

        // Divider column
        table.getColumn(tableModel.getColumnName(BUY_BREAK)).setPreferredWidth(1);

        // Column size for Sell
        table.getColumn(tableModel.getColumnName(SELL_DATE)).setPreferredWidth(35);
        table.getColumn(tableModel.getColumnName(SELL_NUMBER)).setPreferredWidth(30);
        table.getColumn(tableModel.getColumnName(SELL_MONTH)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(SELL_MONTH)).setCellEditor(new DefaultCellEditor(monthComboBox));
        table.getColumn(tableModel.getColumnName(SELL_COMMODITY)).setPreferredWidth(125);
        table.getColumn(tableModel.getColumnName(SELL_COMMODITY)).setCellEditor(new DefaultCellEditor(commodityComboBox));
        table.getColumn(tableModel.getColumnName(SELL_PRICE)).setPreferredWidth(50);
        table.getColumn(tableModel.getColumnName(SELL_STOP)).setPreferredWidth(50);
        table.getColumn(tableModel.getColumnName(BUY_STOP)).setCellEditor(new DefaultCellEditor(stopCheckbox));
        table.getColumn(tableModel.getColumnName(SELL_STATUS)).setPreferredWidth(90);
        table.getColumn(tableModel.getColumnName(SELL_STATUS)).setCellEditor(new DefaultCellEditor(statusComboBox));
        table.getColumn(tableModel.getColumnName(SELL_ACTUAL)).setPreferredWidth(50);
        table.getColumn(tableModel.getColumnName(SELL_NOTE)).setPreferredWidth(150);

        // Divider column
        table.getColumn(tableModel.getColumnName(SELL_BREAK)).setPreferredWidth(1);

        // Columns size for line items
        table.getColumn(tableModel.getColumnName(PROFIT)).setPreferredWidth(60);

        // Column header layout
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.BLUE);
        tableHeader.setForeground(Color.WHITE);

        jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(preferredSize);
        jsp.setViewportView(table);

        add(BorderLayout.CENTER, jsp);

        // When row is selected, select the appropriate contract in the price panel for graphing.
        ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

               ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //no rows are selected
                } else {
                    int row = lsm.getMinSelectionIndex();
                    Commodity commodity = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY));
                    String month = (String)table.getValueAt(row, BUY_MONTH);
                    if (!"".equals(month.trim())) {
                        Contract contract = commodity.getContract(Contract.convertFormattedMonthToKey(month));
                        contract.select();
                    }
                }
            }
        });

    }

    /**
     *  Gets the preferred size of the ContractPanel.
     *
     *  @return The preferred dimensions of the panel.
     */
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    /**
     *  Sets the preferred size of the ContractPanel.
     *
     *  @param  preferredSize   The preferred dimensions of the panel.
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    /**
     *  Load the outstanding contract orders for the current account into the table.
     */
    private void loadTable() {
        try {
            Iterator it = dataManager.getOpenOrders(account.getAccountNumber());
            while (it.hasNext()) {
                tableModel.addOrder((ContractOrder)it.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
