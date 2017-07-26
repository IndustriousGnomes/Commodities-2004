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

//import prototype.commodities.*;
//import prototype.commodities.util.*;
//import prototype.commodities.dataaccess.*;

/**
 *  The ContractPanel contains the table of contracts that are
 *  outstanding.
 *
 *  @author J.R. Titko
 */
public class ContractPanel extends JPanel {
    private static boolean DEBUG = false;

/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The singleton instance of the DataManagerFactory. */
    private static DataManagerInterface dataManager = DataManagerFactory.getInstance();

    /** Column of the buy date. */
    private static  final   int     BUY_DATE        = 0;
    private static  final   int     BUY_NUMBER      = 1;
    private static  final   int     BUY_MONTH       = 2;
    private static  final   int     BUY_COMMODITY   = 3;
    private static  final   int     BUY_PRICE       = 4;
    private static  final   int     BUY_STOP        = 5;
    private static  final   int     BUY_STATUS      = 6;
    private static  final   int     BUY_ACTUAL      = 7;
    private static  final   int     BUY_NOTE        = 8;
    private static  final   int     BUY_BREAK       = 9;
    private static  final   int     SELL_DATE       = 10;
    private static  final   int     SELL_NUMBER     = 11;
    private static  final   int     SELL_MONTH      = 12;
    private static  final   int     SELL_COMMODITY  = 13;
    private static  final   int     SELL_PRICE      = 14;
    private static  final   int     SELL_STOP       = 15;
    private static  final   int     SELL_STATUS     = 16;
    private static  final   int     SELL_ACTUAL     = 17;
    private static  final   int     SELL_NOTE       = 18;
    private static  final   int     SELL_BREAK      = 19;
    private static  final   int     PROFIT          = 20;

    public  static  final   String  NO_STATUS       = " ";
    public  static  final   String  CONSIDERATION   = "Consideration";
    public  static  final   String  PLACED          = "Placed";
    public  static  final   String  FILLED          = "Filled";
    public  static  final   String  CANCEL_PLACED   = "Cancel Placed";
    public  static  final   String  CANCELLED       = "Cancelled";
    public  static  final   String  STOP_LOSS       = "Stop Loss";
    private static  final   ArrayList STATUSES      = new ArrayList(7);
    private static  final   int[] STATUS_SORT_WEIGHTS = {0, 1, 2, 3, 2, 0, 3}; // based on the order STATUSES is filled.

    //                                                             00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
    private static  final   boolean[][] STATUS_CHANGES      = {{ true,  true, false, false, false, false, false},  // 00 - NO_STATUS
                                                               { true,  true,  true, false, false, false, false},  // 01 - CONSIDERATION
                                                               {false,  true,  true,  true,  true, false, false},  // 02 - PLACED
                                                               {false, false,  true,  true, false, false,  true},  // 03 - FILLED
                                                               {false, false,  true,  true,  true,  true, false},  // 04 - CANCEL_PLACED
                                                               {false, false, false, false,  true,  true, false},  // 05 - CANCELLED
                                                               { true, false, false, false, false, false,  true}}; // 06 - STOP_LOSS
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
    private JTable table;
    private ContractTableModel tableModel;
    private JScrollPane jsp;
    private Dimension preferredSize = new Dimension(0,0);

    JComboBox monthComboBox = new JComboBox();
    JComboBox commodityComboBox = new JComboBox();
    JComboBox statusComboBox;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    private class ContractTableModel extends AbstractTableModel {

        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_DATE_CELL      = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           {false, false, false, false, false, false, false},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           {false, false, false, false, false, false, false}}; // 06 - STOP_LOSS

        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_NUMBER_CELL    = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           { true,  true,  true,  true,  true,  true,  true}}; // 06 - STOP_LOSS

        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_COMMODITY_CELL = {{ true, false, false, false, false, false, false},  // 00 - NO_STATUS
                                                           {false, false, false, false, false, false, false},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           {false, false, false, false, false, false, false},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           {false, false, false, false, false, false, false}}; // 06 - STOP_LOSS

        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_PRICE_CELL     = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           {false, false, false, false, false, false, false},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           {false, false, false, false, false, false, false}}; // 06 - STOP_LOSS

        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_STOP_CELL      = {{ true,  true,  true,  true, false, false,  true},  // 00 - NO_STATUS
                                                           { true,  true,  true,  true, false, false,  true},  // 01 - CONSIDERATION
                                                           { true,  true,  true,  true,  true,  true,  true},  // 02 - PLACED
                                                           { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           { true,  true,  true,  true,  true,  true,  true}}; // 06 - STOP_LOSS

        //                                                     00,    01,    02,    03,    04,    05,    06    <2nd   v 1st
        private final   boolean[][] EDIT_ACTUAL_CELL    = {{false, false, false, false, false, false, false},  // 00 - NO_STATUS
                                                           {false, false, false, false, false, false, false},  // 01 - CONSIDERATION
                                                           {false, false, false, false, false, false, false},  // 02 - PLACED
                                                           { true,  true,  true,  true,  true,  true,  true},  // 03 - FILLED
                                                           {false, false, false, false, false, false, false},  // 04 - CANCEL_PLACED
                                                           {false, false, false, false, false, false, false},  // 05 - CANCELLED
                                                           { true,  true,  true,  true,  true,  true,  true}}; // 06 - STOP_LOSS

        private String columnNames[] = {"B.Date", "B.#", "B.M", "B.Commodity", "B.Price", "B.Stop", "B.Status", "B.Actual", "B.Note", ".", "S.Date", "S.#", "S.M", "S.Commodity", "S.Price", "S.Stop", "S.Status", "S.Actual", "S.Note", ",", "Profit"};
        private ArrayList rowData = new ArrayList();

        public void addOrder(ContractOrder order) {
            rowData.set(rowData.size() - 1, createSupportContractOrderLine(order));
            consolidateOrders(order, rowData.size() - 1);
            fireTableRowsUpdated(rowData.size() - 1, rowData.size() - 1);
            if (!(NO_STATUS.equals(((ContractOrderLine)rowData.get(rowData.size() - 1)).getLongOrder().getStatus())) ||
                !(NO_STATUS.equals(((ContractOrderLine)rowData.get(rowData.size() - 1)).getShortOrder().getStatus()))) {
                addRow();
            }
        }

        public void addRow() {
            ContractOrderLine line = new ContractOrderLine();
            addRow(line);
        }

        public void addRow(ContractOrderLine line) {
            rowData.add(line);
            fireTableRowsInserted(rowData.size(),rowData.size());
        }

        public void addRow(ContractOrder buyOrder, ContractOrder sellOrder) {
            ContractOrderLine line = new ContractOrderLine(buyOrder, sellOrder);
            addRow(line);
        }

        public void removeRow(ContractOrderLine line) {
            int row = rowData.indexOf(line);
            rowData.remove(row);
            fireTableRowsDeleted(row,row);
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return rowData.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            ContractOrderLine line = (ContractOrderLine)rowData.get(row);
            Integer i = null;

Debug.println(DEBUG, this, "getValueAt row=" + row + " col=" + col);
            switch(col) {
                case BUY_DATE:      return FormatDate.formatDateProp(line.getLongOrder().getDate(),FormatDate.FORMAT_SCREEN_DATE);
                case BUY_NUMBER:    i = line.getLongOrder().getNumberOfContracts();
                                    return (i.intValue() == 0)?"":i.toString();
                case BUY_MONTH:     return line.getLongOrder().getMonth();
                case BUY_COMMODITY: return line.getLongOrder().getCommodityName();
                case BUY_PRICE:     if ("".equals(line.getLongOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getLongOrder().getDesiredPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case BUY_STOP:      if ("".equals(line.getLongOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getLongOrder().getStopPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case BUY_STATUS:    return line.getLongOrder().getStatus();
                case BUY_ACTUAL:    if ("".equals(line.getLongOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getLongOrder().getActualPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
                                    }
                case BUY_NOTE:      return line.getLongOrder().getNote();
                case SELL_DATE:     return FormatDate.formatDateProp(line.getShortOrder().getDate(),FormatDate.FORMAT_SCREEN_DATE);
                case SELL_NUMBER:   i = line.getShortOrder().getNumberOfContracts();
                                    return (i.intValue() == 0)?"":i.toString();
                case SELL_MONTH:    return line.getShortOrder().getMonth();
                case SELL_COMMODITY:return line.getShortOrder().getCommodityName();
                case SELL_PRICE:    if ("".equals(line.getShortOrder().getCommodityName().trim())) {
                                        return "";
                                    } else {
                                        String d = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY)).formatPrice(line.getShortOrder().getDesiredPrice().doubleValue());
                                        return (Double.parseDouble(d) == 0.0)?"":d;
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

                case PROFIT:
// why is this not using the variable line instead of table lookups?
                    if ((FILLED.equals((String)table.getValueAt(row, BUY_STATUS)) ||
                         STOP_LOSS.equals((String)table.getValueAt(row, BUY_STATUS))) &&
                        (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)) ||
                         STOP_LOSS.equals((String)table.getValueAt(row, SELL_STATUS))) &&
                        (!"".equals(((String)table.getValueAt(row, BUY_ACTUAL)).trim()) &&
                         !"".equals(((String)table.getValueAt(row, SELL_ACTUAL)).trim()))) {
                            Commodity commodity = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY));
                            double profit = (int)((Double.parseDouble((String)table.getValueAt(row, SELL_ACTUAL)) -
                                                   Double.parseDouble((String)table.getValueAt(row, BUY_ACTUAL))) *
                                                   Integer.parseInt((String)table.getValueAt(row, BUY_NUMBER)) /
                                                   commodity.getTickSize() * commodity.getTickPrice() * 100.0) / 100.0;
                            return "" + profit;
                    } else if (FILLED.equals((String)table.getValueAt(row, BUY_STATUS)) &&
                               !"".equals(((String)table.getValueAt(row, BUY_ACTUAL)).trim())) {
                        Commodity commodity = Commodity.byNameExchange((String)table.getValueAt(row, BUY_COMMODITY));
                        Contract  contract = commodity.getContract(Contract.convertFormattedMonthToKey((String)table.getValueAt(row, BUY_MONTH)));
                        double profit = (int)((contract.getPrices().getClose() -
                                               Double.parseDouble((String)table.getValueAt(row, BUY_ACTUAL))) *
                                               Integer.parseInt((String)table.getValueAt(row, BUY_NUMBER)) /
                                               commodity.getTickSize() * commodity.getTickPrice() * 100.0) / 100.0;
                        return "" + profit;
                    } else if (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)) &&
                               !"".equals(((String)table.getValueAt(row, SELL_ACTUAL)).trim())) {
                        Commodity commodity = Commodity.byNameExchange((String)table.getValueAt(row, SELL_COMMODITY));
                        Contract  contract = commodity.getContract(Contract.convertFormattedMonthToKey((String)table.getValueAt(row, SELL_MONTH)));
                        double profit = (int)((Double.parseDouble((String)table.getValueAt(row, SELL_ACTUAL)) -
                                               contract.getPrices().getClose()) *
                                               Integer.parseInt((String)table.getValueAt(row, SELL_NUMBER)) /
                                               commodity.getTickSize() * commodity.getTickPrice() * 100.0) / 100.0;
                        return "" + profit;
                    } else {
                        return null;
                    }
            }
            return "";
        }

        public void setValueAt(Object value, int row, int col) {
            ContractOrderLine line = (ContractOrderLine)rowData.get(row);
            ContractOrder origOrder = null;
            ContractOrder stopOrder = null;
            ContractOrder offsetOrder = null;
            ContractOrder newOrder  = null;
            String val = (String)value;

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
                    case BUY_PRICE:     line.getLongOrder().setDesiredPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
                                        break;
                    case BUY_STOP:      line.getLongOrder().setStopPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
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
                    case SELL_PRICE:    line.getShortOrder().setDesiredPrice(("".equals(val.trim()))?new Double(0):Double.valueOf(val.trim()));
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
Debug.println(DEBUG, this, "setValueAt - NumberFormatException");
                return;
            }
        }

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
                if ((FILLED.equals((String)table.getValueAt(row, BUY_STATUS)) ||
                     STOP_LOSS.equals((String)table.getValueAt(row, BUY_STATUS))) &&
                    (FILLED.equals((String)table.getValueAt(row, SELL_STATUS)) ||
                     STOP_LOSS.equals((String)table.getValueAt(row, SELL_STATUS)))) {
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

        private void consolidateOrders(ContractOrder order, int row) {
Debug.println(DEBUG, this, "consolidateOrders start");

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
Debug.print(DEBUG, this, "consolidateOrders - Swapping orders rows " + row + " and " + i);
                    if (origOrder.isLongOrder()) {
Debug.println(DEBUG, " long orders");
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
Debug.println(DEBUG, " short orders");
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
Debug.println(DEBUG, this, "consolidateOrders end");
        }

        private void balanceContractCount(int row) {
Debug.println(DEBUG, this, "balanceContractCount start");
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
Debug.println(DEBUG, this, "balanceContractCount end");
        }

        private void combineOrders() {
Debug.println(DEBUG, this, "combineOrders start");
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

Debug.println(DEBUG, this, "combineOrders combine Long orders = " + currentRow);
                    for (int searchRow = startSearchRow; searchRow < currentRow; searchRow++) {
Debug.println(DEBUG, this, "combineOrders searchRow = " + searchRow);
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
Debug.println(DEBUG, this, "combineOrders combine");
                            searchLongOrder.combine(currentLongOrder);
                            fireTableRowsUpdated(searchRow, searchRow);
                            removeRow(currentOrderLine);
                            break;
                        }
                    }

                } else if ((currentLongValue == 0) &&
                           (currentShortValue > 0)) {

Debug.println(DEBUG, this, "combineOrders combine Short orders = " + currentRow);
                    for (int searchRow = startSearchRow; searchRow < currentRow; searchRow++) {
Debug.println(DEBUG, this, "combineOrders searchRow = " + searchRow);
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
Debug.println(DEBUG, this, "combineOrders combine");
                            searchShortOrder.combine(currentShortOrder);
                            fireTableRowsUpdated(searchRow, searchRow);
                            removeRow(currentOrderLine);
                            break;
                        }
                    }
                } else if ((currentLongValue > 0) &&
                           (currentShortValue > 0)) {
Debug.println(DEBUG, this, "combineOrders combine full orders = " + currentRow);
                    for (int searchRow = 0; searchRow < currentRow; searchRow++) {
Debug.println(DEBUG, this, "combineOrders searchRow = " + searchRow);
                        ContractOrderLine   searchOrderLine = (ContractOrderLine)rowData.get(searchRow);
                        ContractOrder       searchLongOrder = searchOrderLine.getLongOrder();
                        ContractOrder       searchShortOrder= searchOrderLine.getShortOrder();

                        if (currentLongOrder.equals(searchLongOrder) &&
                            currentShortOrder.equals(searchShortOrder)) {
Debug.println(DEBUG, this, "combineOrders combine");
                            searchLongOrder.combine(currentLongOrder);
                            searchShortOrder.combine(currentShortOrder);
                            fireTableRowsUpdated(searchRow, searchRow);
                            removeRow(currentOrderLine);
                            break;
                        }
                    }
                }
            }
Debug.println(DEBUG, this, "combineOrders end");
        }


        private ContractOrderLine createSupportContractOrderLine(ContractOrder order) {
            ContractOrder offsettingOrder = new ContractOrder(!order.isLongOrder());
            offsettingOrder.setMonth(order.getMonth());
            offsettingOrder.setCommodityName(order.getCommodityName());

            if (ContractOrder.LONG == order.isLongOrder()) {
                return new ContractOrderLine(order, offsettingOrder);
            } else {
                return new ContractOrderLine(offsettingOrder, order);
            }
        }
    }

    private class ContractTableCellRenderer extends DefaultTableCellRenderer {
        public ContractTableCellRenderer() {
            setOpaque(true);
        }

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
                setHorizontalAlignment(RIGHT);
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

    private class StatusListCellRenderer extends JLabel implements ListCellRenderer {
        public StatusListCellRenderer() {
            setOpaque(true);
        }

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

    private class MonthListCellRenderer extends JLabel implements ListCellRenderer {
        public MonthListCellRenderer() {
            setOpaque(true);
        }

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

    private class CommodityListCellRenderer extends JLabel implements ListCellRenderer {
        public CommodityListCellRenderer() {
            setOpaque(true);
        }

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
    public ContractPanel() {
Debug.println(this, "ContractPanel start");
        STATUSES.add(NO_STATUS);
        STATUSES.add(CONSIDERATION);
        STATUSES.add(PLACED);
        STATUSES.add(FILLED);
        STATUSES.add(CANCEL_PLACED);
        STATUSES.add(CANCELLED);
        STATUSES.add(STOP_LOSS);
        statusComboBox = new JComboBox(STATUSES.toArray(new String[STATUSES.size()]));

        setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        setupGUI();

        tableModel.addRow();
        loadTable();
Debug.println(this, "ContractPanel end");
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */

    private void setupGUI() {
Debug.println(this, "setupGUI start");
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));

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

        statusComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        statusComboBox.setRenderer(new StatusListCellRenderer());

        // Column size for Buy
        table.getColumn(tableModel.getColumnName(BUY_DATE)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(BUY_NUMBER)).setPreferredWidth(30);
        table.getColumn(tableModel.getColumnName(BUY_MONTH)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(BUY_MONTH)).setCellEditor(new DefaultCellEditor(monthComboBox));
        table.getColumn(tableModel.getColumnName(BUY_COMMODITY)).setPreferredWidth(125);
        table.getColumn(tableModel.getColumnName(BUY_COMMODITY)).setCellEditor(new DefaultCellEditor(commodityComboBox));
        table.getColumn(tableModel.getColumnName(BUY_PRICE)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(BUY_STOP)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(BUY_STATUS)).setPreferredWidth(90);
        table.getColumn(tableModel.getColumnName(BUY_STATUS)).setCellEditor(new DefaultCellEditor(statusComboBox));
        table.getColumn(tableModel.getColumnName(BUY_ACTUAL)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(BUY_NOTE)).setPreferredWidth(100);

        // Divider column
        table.getColumn(tableModel.getColumnName(BUY_BREAK)).setPreferredWidth(1);

        // Column size for Sell
        table.getColumn(tableModel.getColumnName(SELL_DATE)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(SELL_NUMBER)).setPreferredWidth(30);
        table.getColumn(tableModel.getColumnName(SELL_MONTH)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(SELL_MONTH)).setCellEditor(new DefaultCellEditor(monthComboBox));
        table.getColumn(tableModel.getColumnName(SELL_COMMODITY)).setPreferredWidth(125);
        table.getColumn(tableModel.getColumnName(SELL_COMMODITY)).setCellEditor(new DefaultCellEditor(commodityComboBox));
        table.getColumn(tableModel.getColumnName(SELL_PRICE)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(SELL_STOP)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(SELL_STATUS)).setPreferredWidth(90);
        table.getColumn(tableModel.getColumnName(SELL_STATUS)).setCellEditor(new DefaultCellEditor(statusComboBox));
        table.getColumn(tableModel.getColumnName(SELL_ACTUAL)).setPreferredWidth(55);
        table.getColumn(tableModel.getColumnName(SELL_NOTE)).setPreferredWidth(100);

        // Divider column
        table.getColumn(tableModel.getColumnName(SELL_BREAK)).setPreferredWidth(1);

        // Columns size for line items
        table.getColumn(tableModel.getColumnName(PROFIT)).setPreferredWidth(60);

        // Column header layout
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.BLUE);
        tableHeader.setForeground(Color.WHITE);


        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.YELLOW);

        JLabel title = new JLabel("Contract Management");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(Color.BLACK);

        titlePanel.add(title, BorderLayout.CENTER);

        jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(preferredSize);
        jsp.setViewportView(table);

        add(BorderLayout.NORTH, titlePanel);
        add(BorderLayout.CENTER, jsp);

Debug.println(this, "setupGUI end");
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
Debug.println(this, "setPreferredSize start");
        this.preferredSize = preferredSize;
Debug.println(this, "setPreferredSize end");
    }

    /**
     *  Load the outstanding contract orders into the table.
     */
    private void loadTable() {
        try {
            Iterator it = dataManager.getOpenOrders();
            while (it.hasNext()) {
                tableModel.addOrder((ContractOrder)it.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}