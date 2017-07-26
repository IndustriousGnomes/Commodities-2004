/*  x Properly Documented
 */
package commodities.price;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

import commodities.commodity.*;
import commodities.contract.*;
import commodities.event.*;
import commodities.tests.*;
import commodities.util.*;

import com.util.FormatDate;

/**
 *  The PricePanel displays all of the pricing information for a given commodity and its
 *  contracts in a table format.
 *
 *  @author J.R. Titko
 *  @version 1.0
 *  @update 2004.11.11
 */

public class PricePanel extends JPanel implements ContractSelectionListener {
/* *************************************** */
/* *** CLASS VARIABLES                 *** */
/* *************************************** */
    /** The preferred width of the price panel. */
    private static final int PREFERRED_PANEL_WIDTH   = 500;
    /** The preferred height of the price panel. */
    private static final int PREFERRED_PANEL_HEIGHT  = 148;
    /** Width of the price panel with the border included */
    public static final int PANEL_WIDTH   = PREFERRED_PANEL_WIDTH + 2;
    /** Height of the price panel with the border and title included */
    public static final int PANEL_HEIGHT  = PREFERRED_PANEL_HEIGHT + 19;

    /* TABLE CELLS */
    /** Contract column */
    private static final int CONTRACT_COLUMN    = 0;
    /** Date of prices column */
    private static final int DATE_COLUMN        = 1;
    /** Open price column */
    private static final int OPEN_COLUMN        = 2;
    /** High price column */
    private static final int HIGH_COLUMN        = 3;
    /** Low price column */
    private static final int LOW_COLUMN         = 4;
    /** Closing price column */
    private static final int CLOSE_COLUMN       = 5;
    /** Volume column */
    private static final int VOLUME_COLUMN      = 6;
    /** Open interest column */
    private static final int INTEREST_COLUMN    = 7;
    /** Short term recommendation column */
    private static final int SHORT_TERM_COLUMN  = 8;
    /** Medium term recommendation column */
    private static final int MEDIUM_TERM_COLUMN = 9;
    /** Long term recommendation column */
    private static final int LONG_TERM_COLUMN   = 10;

    /** Format of dates in the panel. */
    private static final String DATE_FORMAT = "MM/dd";

/* *************************************** */
/* *** INSTANCE VARIABLES              *** */
/* *************************************** */
    /** The JTable that will be used to display the data. */
    private JTable table;
    /** The table model for formatting the JTable. */
    private PriceTableModel tableModel;

    /** The JScrollPane to allow the JTable to be scrolled. */
    private JScrollPane jsp;
    /** The commodity to be presented in the JTable. */
    private Commodity commodity;
    /** The contracts in the rows of data stored in the JTable. */
    private ArrayList contractData = new ArrayList();
    /** A reference to this PricePanel. */
    private PricePanel myPricePanel;

/* *************************************** */
/* *** INNER CLASSES                   *** */
/* *************************************** */
    /**
     *  The table model for laying out the JTable's appearance
     *  for displaying a commodity's information.
     *
     *  @author J.R. Titko
     */
    public class PriceTableModel extends AbstractTableModel implements PriceListener, RecommendationListener {
    /* *************************************** */
    /* *** INSTANCE VARIABLES              *** */
    /* *************************************** */
        /** The names of the columns of the JTable. */
        private String columnNames[] = {"Contract", "Date", "Open", "High", "Low", "Close", "Volume", "Interest", "RS", "RM", "RL"};
        /** The rows of data stored in the JTable. */
        private ArrayList rowData = new ArrayList();

    /* *************************************** */
    /* *** INSTANCE METHODS                *** */
    /* *************************************** */
        /**
         *  Adds a new row to the bottom of the JTable containing the appropriate
         *  information retrieved from the passed in fields.
         *
         *  @param  contract        the specific contract to add
         */
        public void addRow(Contract contract) {
            Object columnData[] = {contract.getMonthFormatted(),
                                   "",
                                   "",
                                   "",
                                   "",
                                   "",
                                   new Long(0),
                                   new Long(0),
                                   "",
                                   "",
                                   ""};
            rowData.add(columnData);
            contractData.add(contract);
            contract.addPriceListener(this);
            contract.addRecommendationListener(this);
            fireTableRowsInserted(rowData.size(),rowData.size());
            contract.requestRecommendations();
        }

    /* *************************************** */
    /* *** AbstractTableModel METHODS      *** */
    /* *************************************** */
        /**
         *  Gets the number of columns in the JTable.
         *  @return         number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         *  Gets the number of rows in the JTable.
         *  @return         number of rows
         */
        public int getRowCount() {
            return rowData.size();
        }

        /**
         *  Gets the name of the indexed column in the JTable.
         *
         *  @param  col     the column to get the name for
         *  @return         title of column
         */
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /**
         *  Gets the value of the cell in the JTable by row and column.
         *
         *  @param  row     the row of the cell
         *  @param  col     the column of the cell
         *  @return         contents of the cell
         */
        public Object getValueAt(int row, int col) {
            Object columnData[] = (Object[])rowData.get(row);
            return columnData[col];
        }

        /**
         *  Sets the value of the cell at row and column with the object in value.
         *
         *  @param  value   the object to put in the cell
         *  @param  row     the row of the cell
         *  @param  col     the column of the cell
         */
        public void setValueAt(Object value, int row, int col) {
            Object columnData[] = (Object[])rowData.get(row);
            columnData[col] = value;
            fireTableCellUpdated(row, col);
        }

    /* *************************************** */
    /* *** PriceListener METHODS           *** */
    /* *************************************** */
        /**
         *  Invoked when the date changes.
         *  @param  e   the PriceEvent
         */
        public void changeDate(PriceEvent e) {
            int row = contractData.indexOf(e.getContract());
            String priceDate = FormatDate.formatDate(e.getDate(), DATE_FORMAT);
            if ((getValueAt(row, DATE_COLUMN) == null) || (((String)getValueAt(row, DATE_COLUMN)).compareTo(priceDate) <= 0)) {
                setValueAt(priceDate, row, DATE_COLUMN);
                fireTableRowsUpdated(row, row);
            }
        }

        /**
         *  Invoked when an open price changes.
         *  @param  e   the PriceEvent
         */
        public void changeOpenPrice(PriceEvent e) {
            setValueAt(commodity.formatPrice(e.getPrice().doubleValue()), contractData.indexOf(e.getContract()), OPEN_COLUMN);
        }

        /**
         *  Invoked when a high price changes.
         *  @param  e   the PriceEvent
         */
        public void changeHighPrice(PriceEvent e) {
            setValueAt(commodity.formatPrice(e.getPrice().doubleValue()), contractData.indexOf(e.getContract()), HIGH_COLUMN);
        }

        /**
         *  Invoked when a low price changes.
         *  @param  e   the PriceEvent
         */
        public void changeLowPrice(PriceEvent e) {
            setValueAt(commodity.formatPrice(e.getPrice().doubleValue()), contractData.indexOf(e.getContract()), LOW_COLUMN);
        }

        /**
         *  Invoked when a close price changes.
         *  @param  e   the PriceEvent
         */
        public void changeClosePrice(PriceEvent e) {
            setValueAt(commodity.formatPrice(e.getPrice().doubleValue()), contractData.indexOf(e.getContract()), CLOSE_COLUMN);
        }

        /**
         *  Invoked when the volume changes.
         *  @param  e   the PriceEvent
         */
        public void changeVolume(PriceEvent e) {
            setValueAt(e.getVolume(), contractData.indexOf(e.getContract()), VOLUME_COLUMN);
        }

        /**
         *  Invoked when the open interest changes.
         *  @param  e   the PriceEvent
         */
        public void changeOpenInterest(PriceEvent e) {
            setValueAt(e.getOpenInterest(), contractData.indexOf(e.getContract()), INTEREST_COLUMN);
        }

    /* *************************************** */
    /* *** RecommendationListener METHODS  *** */
    /* *************************************** */
        /**
         *  Invoked when a recommendation has been requested.
         *  @param  e   the RecommendationEvent
         */
        public void recommendationRequested(RecommendationEvent e) {
            int row = contractData.indexOf(e.getContract());
            String priceDate = FormatDate.formatDate(e.getDate(), DATE_FORMAT);
            if ((getValueAt(row, DATE_COLUMN) == null) || (((String)getValueAt(row, DATE_COLUMN)).compareTo(priceDate) == 0)) {
                setValueAt(" ", contractData.indexOf(e.getContract()), SHORT_TERM_COLUMN);
                setValueAt(" ", contractData.indexOf(e.getContract()), MEDIUM_TERM_COLUMN);
                setValueAt(" ", contractData.indexOf(e.getContract()), LONG_TERM_COLUMN);
            }
        }

        /**
         *  Invoked when a short term recommendation is changed.
         *  @param  e   the RecommendationEvent
         */
        public void changeShortTermRecommondation(RecommendationEvent e) {
            int row = contractData.indexOf(e.getContract());
            String priceDate = FormatDate.formatDate(e.getDate(), DATE_FORMAT);
            if ((getValueAt(row, DATE_COLUMN) != null) && (((String)getValueAt(row, DATE_COLUMN)).compareTo(priceDate) == 0)) {
                setValueAt(formatRecommendation(e.getRecommendation()), contractData.indexOf(e.getContract()), SHORT_TERM_COLUMN);
            }
        }

        /**
         *  Invoked when a medium term recommendation is changed.
         *  @param  e   the RecommendationEvent
         */
        public void changeMediumTermRecommondation(RecommendationEvent e) {
            int row = contractData.indexOf(e.getContract());
            String priceDate = FormatDate.formatDate(e.getDate(), DATE_FORMAT);
            if ((getValueAt(row, DATE_COLUMN) != null) && (((String)getValueAt(row, DATE_COLUMN)).compareTo(priceDate) == 0)) {
                setValueAt(formatRecommendation(e.getRecommendation()), contractData.indexOf(e.getContract()), MEDIUM_TERM_COLUMN);
            }
        }

        /**
         *  Invoked when a long term recommendation is changed.
         *  @param  e   the RecommendationEvent
         */
        public void changeLongTermRecommondation(RecommendationEvent e) {
            int row = contractData.indexOf(e.getContract());
            String priceDate = FormatDate.formatDate(e.getDate(), DATE_FORMAT);
            if ((getValueAt(row, DATE_COLUMN) != null) && (((String)getValueAt(row, DATE_COLUMN)).compareTo(priceDate) == 0)) {
                setValueAt(formatRecommendation(e.getRecommendation()), contractData.indexOf(e.getContract()), LONG_TERM_COLUMN);
            }
        }

        /**
         *  Format the recommendation to be displayed.
         *  @param  recommendation  the recommendation to format
         *  @return     the formatted recommendation
         */
        public String formatRecommendation(Recommendation recommendation) {
            return recommendation.getBuySellLabel();
        }
    }

    /**
     *  The cell renderer for laying out the appearence of individual
     *  JTable cells.
     *
     *  @author J.R. Titko
     */
    public class PriceTableCellRenderer extends DefaultTableCellRenderer {
    /* *************************************** */
    /* *** CONSTRUCTORS                    *** */
    /* *************************************** */
        /** Create a PriceTableCellRenderer. */
        public PriceTableCellRenderer() {
            setOpaque(true);
        }

    /* **************************************** */
    /* *** DefaultTableCellRenderer METHODS *** */
    /* **************************************** */
        /**
         *  Gets the rendering of a cell based on information contained
         *  in those cells.
         *
         *  @param  table       the JTable
         *  @param  value       the value to assign to the cell at [row, column]
         *  @param  isSelected  true if cell is selected
         *  @param  hasFocus    if true, render cell appropriately.
         *  @param  row         the row of the cell to render
         *  @param  column      the column of the cell to render
         *  @return             this component as a properly rendered cell
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Alignment
            switch (column) {
                case CONTRACT_COLUMN:
                        setHorizontalAlignment(LEFT);
                        break;
                case DATE_COLUMN:
                        setHorizontalAlignment(CENTER);
                        break;
                case SHORT_TERM_COLUMN:
                case MEDIUM_TERM_COLUMN:
                case LONG_TERM_COLUMN:
                        setHorizontalAlignment(CENTER);
                        break;
                default:
                        setHorizontalAlignment(RIGHT);
            }

            // Font
            switch (column) {
                case CONTRACT_COLUMN:
                case SHORT_TERM_COLUMN:
                case MEDIUM_TERM_COLUMN:
                case LONG_TERM_COLUMN:
                        setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                        break;
                default:
                        setHorizontalAlignment(RIGHT);
            }

            // Colors
            switch (column) {
                case SHORT_TERM_COLUMN:
                case MEDIUM_TERM_COLUMN:
                case LONG_TERM_COLUMN:
                        if (value != null) {
                            String s = value.toString();
                            if (Recommendation.BUY_LABEL.equals(s) || Recommendation.SETTLE_BUY_LABEL.equals(s)) {
                                setBackground(Color.GREEN);
                                setForeground(Color.BLACK);
                            } else if (Recommendation.SELL_LABEL.equals(s) || Recommendation.SETTLE_SELL_LABEL.equals(s)) {
                                setBackground(Color.CYAN);
                                setForeground(Color.BLACK);
                            } else if (Recommendation.NO_ACTION_LABEL.equals(s)) {
                                setBackground(Color.WHITE);
                                setForeground(Color.BLACK);
                            } else {
                                setBackground(Color.YELLOW);
                                setForeground(Color.DARK_GRAY);
                            }
                        }
                        break;
                default:
                    if (isSelected || hasFocus) {
                        setBackground(table.getSelectionBackground());
                        setForeground(table.getSelectionForeground());
                    } else {
                        if (row == 0) {
                            int month = Month.byAbbrev(((String)tableModel.getValueAt(0, CONTRACT_COLUMN)).substring(0,3)).getNumber();
                            month -= 1;
                            if (month <= (CommodityCalendar.getInstance().get(Calendar.MONTH) + 1)) {
                                setBackground(Color.RED);
                                setForeground(Color.BLACK);
                            }
                        }
                        if ((column != CONTRACT_COLUMN) && ((String)tableModel.getValueAt(row, DATE_COLUMN)).length() > 0) {
                            Prices prices = ((Contract)contractData.get(row)).getPrices();
                            CommodityCalendar calendar = new CommodityCalendar(prices.getDate());

                            int month = Integer.parseInt(((String)tableModel.getValueAt(row, DATE_COLUMN)).substring(0,2));
                            int day   = Integer.parseInt(((String)tableModel.getValueAt(row, DATE_COLUMN)).substring(3,5));
                            if ((month != (CommodityCalendar.getInstance().get(Calendar.MONTH) + 1)) ||
                                (day   != (CommodityCalendar.getInstance().get(Calendar.DATE)))) {
                                setBackground(Color.YELLOW);
                                setForeground(Color.DARK_GRAY);
                            }
                        }
                    }
                }
            return this;
        }
    }

/* *************************************** */
/* *** CONSTRUCTORS                    *** */
/* *************************************** */
    /**
     *  Create a new PricePanel for the commodity with a reference
     *  back to the PricingPanel that this panel will reside in.
     *
     *  @param  commodity       the commodity to set up in this PricePanel
     */
    public PricePanel (Commodity commodity) {
        this.commodity = commodity;
        this.myPricePanel = this;

        setupGUI();

        Iterator it = commodity.getContracts();
        while (it.hasNext()) {
            Contract contract = (Contract)it.next();
            addRow(contract);
        }
        Contract.addSelectionListener(this);
    }
/* *************************************** */
/* *** GETS & SETS                     *** */
/* *************************************** */
    /**
     *  Get the commodity in this panel.
     *  @return     the commodity
     */
    public Commodity getCommodity() {
        return commodity;
    }

/* *************************************** */
/* *** INSTANCE METHODS                *** */
/* *************************************** */
    /**
     *  Set up the layout of the PricePanel with a header and JTable.
     */
    private void setupGUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));

        tableModel = new PriceTableModel();
        table = new JTable(tableModel) {
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new PriceTableCellRenderer();
            }
        };
        table.setSelectionBackground(Color.BLACK);
        table.setSelectionForeground(Color.WHITE);
        table.getColumn(tableModel.getColumnName(CONTRACT_COLUMN)).setPreferredWidth(100);
        table.getColumn(tableModel.getColumnName(SHORT_TERM_COLUMN)).setPreferredWidth(32);
        table.getColumn(tableModel.getColumnName(MEDIUM_TERM_COLUMN)).setPreferredWidth(32);
        table.getColumn(tableModel.getColumnName(LONG_TERM_COLUMN)).setPreferredWidth(32);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.BLUE);
        tableHeader.setForeground(Color.WHITE);



        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.GREEN);

        JLabel contPrice = new JLabel("  Cost=$" + commodity.getMargin());
        contPrice.setFont(new Font(contPrice.getFont().getName(), Font.BOLD, contPrice.getFont().getSize()-1));
        contPrice.setForeground(Color.BLACK);

        JLabel title = new JLabel(commodity.getName() + " (" + commodity.getExchange() + ") " +
                                  commodity.getUnitSize() + " " + commodity.getUnitType() + "; " +
                                  commodity.getUnitPrice() + " per " + commodity.getUnitType());
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(Color.BLACK);

        JLabel price = new JLabel("TS=" + commodity.getTickSize() + " ($" + commodity.getTickPrice() + ")  ");
        price.setFont(new Font(price.getFont().getName(), Font.BOLD, price.getFont().getSize()-1));
        price.setForeground(Color.BLACK);

        titlePanel.add(contPrice, BorderLayout.WEST);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(price, BorderLayout.EAST);

        jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH, PREFERRED_PANEL_HEIGHT));
        jsp.setViewportView(table);

        add(BorderLayout.NORTH, titlePanel);
        add(BorderLayout.CENTER, jsp);

        ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    //no rows are selected
                } else {
                    ((Contract)contractData.get(lsm.getMinSelectionIndex())).select();
                }
            }
        });
    }

    /**
     *  Adding a row to this panel actually adds a row to the
     *  JTable within the panel.
     *
     *  @param  contract        the specific contract to add
     */
    public void addRow(Contract contract) {
        tableModel.addRow(contract);
    }

/* *************************************** */
/* ***ContractSelectionListener METHODS*** */
/* *************************************** */
    /**
     *  Invoked when the contract selection changes.  This is a 2 pass
     *  execution so that some classes that need to execute first can
     *  process when init is true and the remaining listeners can
     *  process when init is false.
     *
     *  @param  e       the ContractSelectionEvent
     *  @param  init    true if first pass for initialization, otherwise false
     */
    public void selectContract(ContractSelectionEvent e, boolean init) {
        if (init) {
            int newRow = contractData.indexOf((Contract)e.getSource());
            if (newRow == -1) {
                table.clearSelection();
            } else if (!table.isRowSelected(newRow)) {
                table.setRowSelectionInterval(newRow, newRow);
            }
        }
    }
}
